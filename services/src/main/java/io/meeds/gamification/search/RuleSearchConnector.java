/**
 * This file is part of the Meeds project (https://meeds.io/).
 * Copyright (C) 2020 - 2022 Meeds Association contact@meeds.io
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package io.meeds.gamification.search;

import static io.meeds.gamification.utils.Utils.removeSpecialCharacters;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import org.exoplatform.commons.search.es.ElasticSearchException;
import org.exoplatform.commons.search.es.client.ElasticSearchingClient;
import org.exoplatform.commons.utils.IOUtil;
import org.exoplatform.commons.utils.PropertyManager;
import org.exoplatform.container.configuration.ConfigurationManager;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.PropertiesParam;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import io.meeds.gamification.model.filter.RuleFilter;

public class RuleSearchConnector {

  private static final Log             LOG                          = ExoLogger.getLogger(RuleSearchConnector.class);

  private static final String          SEARCH_QUERY_FILE_PATH_PARAM = "query.file.path";

  private static final String          LANG                         = "@lang@";

  private static final String          KEYWORD_FILTERING_QUERY      =
                                                               """
                                                                         {
                                                                           "query_string": {
                                                                             "query": "title:(*@term@*) OR title_@lang@:(*@term@*) OR description:(@term_query@) OR description_@lang@:(@term_query@)",
                                                                             "default_operator": "AND",
                                                                             "fuzziness": 1,
                                                                             "phrase_slop": 1
                                                                           }
                                                                         }
                                                                   """;

  private static final String          TERMS_FILTERING_QUERY        =
                                                             """
                                                                       {
                                                                         "terms": {
                                                                           @favorite_query@
                                                                           @audience_filtering@
                                                                         }
                                                                       }
                                                                 """;

  private static final String          DOMAIN_FILTERING_QUERY       =
                                                              """
                                                                        {
                                                                          "term": {
                                                                            "domainId": {
                                                                              "value": "@domainId@"
                                                                            }
                                                                          }
                                                                        }
                                                                  """;

  private static final String          AUDIENCE_FILTERING_QUERY     =
                                                                """
                                                                        "audience": [@spaceList@]
                                                                    """;

  private static final String          FAVORITE_FILTERING_QUERY     =
                                                                """
                                                                        "metadatas.favorites.metadataName.keyword": ["@identity_id@"]
                                                                    """;

  private static final String          ILLEGAL_SEARCH_CHARACTERS    = "\\!?^()+-=<>{}[]:\"*~&|#%@";

  private final ConfigurationManager   configurationManager;

  private final ElasticSearchingClient client;

  private String                       index;

  private String                       searchQueryFilePath;

  private String                       searchQuery;

  public RuleSearchConnector(ConfigurationManager configurationManager,
                             ElasticSearchingClient client,
                             InitParams initParams) {
    this.configurationManager = configurationManager;
    this.client = client;

    PropertiesParam param = initParams.getPropertiesParam("constructor.params");
    this.index = param.getProperty("index");
    if (initParams.containsKey(SEARCH_QUERY_FILE_PATH_PARAM)) {
      searchQueryFilePath = initParams.getValueParam(SEARCH_QUERY_FILE_PATH_PARAM).getValue();
      try {
        retrieveSearchQuery();
      } catch (Exception e) {
        LOG.error("Can't read elasticsearch search query from path {}", searchQueryFilePath, e);
      }
    }
  }

  public List<Long> search(RuleFilter filter, long offset, long limit) {
    if (offset < 0) {
      throw new IllegalArgumentException("Offset must be positive");
    }
    if (limit < 0) {
      throw new IllegalArgumentException("Limit must be positive");
    }
    if (filter.getLocale() == null) {
      throw new IllegalArgumentException("Filter locale is mandatory");
    }
    String esQuery = buildQueryStatement(filter, offset, limit);
    if (StringUtils.isBlank(esQuery)) {
      return Collections.emptyList();
    }
    String jsonResponse = this.client.sendRequest(esQuery, this.index);
    return buildSearchResult(jsonResponse);
  }

  private String buildQueryStatement(RuleFilter filter, long offset, long limit) {
    String query = retrieveSearchQuery();

    String term = StringUtils.lowerCase(removeSpecialCharacters(filter.getTerm()));
    term = escapeIllegalCharacterInQuery(term);
    String termQuery = "";
    int filtersCount = 0;
    int termsFilterCount = 0;
    if (StringUtils.isNotBlank(term)) {
      term = term.trim();
      List<String> termsQuery = Arrays.stream(term.split(" ")).filter(StringUtils::isNotBlank).map(word -> {
        word = word.trim();
        if (word.length() > 4) {
          word = word + "~1";
        }
        return word;
      }).toList();
      termQuery = StringUtils.join(termsQuery, " AND ");
      query = query.replace("@keyword_filtering@", KEYWORD_FILTERING_QUERY)
                   .replace("@term@", term)
                   .replace("@term_query@", termQuery);
      filtersCount++;
    }

    if (isFavoriteQuery(filter)) {
      query = query.replace("@terms_filtering@", (filtersCount > 0 ? "," : "") + TERMS_FILTERING_QUERY);
      query = query.replace("@favorite_query@", FAVORITE_FILTERING_QUERY)
                   .replace("@identity_id@", String.valueOf(filter.getIdentityId()));
      filtersCount++;
      termsFilterCount++;
    }

    if (!CollectionUtils.isEmpty(filter.getSpaceIds())) {
      Set<Long> spaceList = Optional.ofNullable(filter.getSpaceIds())
                                    .map(HashSet::new)
                                    .orElse(new HashSet<>());
      query = query.replace("@terms_filtering@", (filtersCount > 0 ? "," : "") + TERMS_FILTERING_QUERY);
      query = query.replace("@audience_filtering@", (termsFilterCount > 0 ? "," : "") + AUDIENCE_FILTERING_QUERY)
                   .replace("@spaceList@", StringUtils.join(spaceList, ","));
    }

    if (filter.getProgramId() > 0) {
      query = query.replace("@term_filtering@", DOMAIN_FILTERING_QUERY)
                   .replace("@domainId@", String.valueOf(filter.getProgramId()));
    }

    return query.replace(LANG, filter.getLocale().toLanguageTag())
                .replace("@audience_filtering@", "")
                .replace("@keyword_filtering@", "")
                .replace("@terms_filtering@", "")
                .replace("@term_filtering@", "")
                .replace("@offset@", String.valueOf(offset))
                .replace("@limit@", String.valueOf(limit));
  }

  @SuppressWarnings("rawtypes")
  private List<Long> buildSearchResult(String jsonResponse) {
    List<Long> results = new ArrayList<>();
    JSONParser parser = new JSONParser();

    Map json = null;
    try {
      json = (Map) parser.parse(jsonResponse);
    } catch (ParseException e) {
      throw new ElasticSearchException("Unable to parse JSON response", e);
    }

    JSONObject jsonResult = (JSONObject) json.get("hits");
    if (jsonResult == null) {
      return results;
    }

    JSONArray jsonHits = (JSONArray) jsonResult.get("hits");
    for (Object jsonHit : jsonHits) {
      try {
        JSONObject jsonHitObject = (JSONObject) jsonHit;
        JSONObject hitSource = (JSONObject) jsonHitObject.get("_source");
        long id = parseLong(hitSource, "id");
        results.add(id);
      } catch (Exception e) {
        LOG.warn("Error processing rules search result item, ignore it from results", e);
      }
    }
    return results;
  }

  private boolean isFavoriteQuery(RuleFilter filter) {
    return filter.isFavorites() && filter.getIdentityId() > 0;
  }

  private long parseLong(JSONObject hitSource, String key) {
    String value = (String) hitSource.get(key);
    return StringUtils.isBlank(value) ? 0 : Long.parseLong(value);
  }

  private static String escapeIllegalCharacterInQuery(String query) {
    if (StringUtils.isBlank(query)) {
      return null;
    }
    for (char c : ILLEGAL_SEARCH_CHARACTERS.toCharArray()) {
      query = query.replace(c + "", "");
    }
    return query;
  }

  private String retrieveSearchQuery() {
    if (StringUtils.isBlank(this.searchQuery) || PropertyManager.isDevelopping()) {
      try {
        InputStream queryFileIS = this.configurationManager.getInputStream(searchQueryFilePath);
        this.searchQuery = IOUtil.getStreamContentAsString(queryFileIS);
      } catch (Exception e) {
        throw new IllegalStateException("Error retrieving search query from file: " + searchQueryFilePath, e);
      }
    }
    return this.searchQuery;
  }
}

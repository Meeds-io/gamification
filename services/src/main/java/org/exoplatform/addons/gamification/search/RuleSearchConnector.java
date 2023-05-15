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
package org.exoplatform.addons.gamification.search;

import java.io.InputStream;
import java.text.Normalizer;
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

import org.exoplatform.addons.gamification.service.dto.configuration.RuleDTO;
import org.exoplatform.addons.gamification.service.dto.configuration.RuleFilter;
import org.exoplatform.addons.gamification.service.dto.configuration.constant.DateFilterType;
import org.exoplatform.addons.gamification.storage.RuleStorage;
import org.exoplatform.commons.search.es.ElasticSearchException;
import org.exoplatform.commons.search.es.client.ElasticSearchingClient;
import org.exoplatform.commons.utils.IOUtil;
import org.exoplatform.commons.utils.PropertyManager;
import org.exoplatform.container.configuration.ConfigurationManager;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.PropertiesParam;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

public class RuleSearchConnector {

  private static final Log             LOG                          = ExoLogger.getLogger(RuleSearchConnector.class);

  private static final String          SEARCH_QUERY_FILE_PATH_PARAM = "query.file.path";

  private static final String          START_DATE                   = "startDate";

  private static final String          END_DATE                     = "endDate";

  private static final String          DATE_FIELD                   = "@dateField@";

  private static final String          START_DATE_QUERY             = "@startDateQuery@";

  private static final String          END_DATE_QUERY               = "@endDateQuery@";

  private static final String          DATE_CONDITION               = "@condition@";

  private static final String          DATE                         = "@date@";

  private static final String          DATE_FILTERING               = "@date_filtering@";

  private static final String          DATE_FIELD_FILTERING_QUERY   = """
           {
             "range": {
               "@dateField@": {
                 "@condition@": "@date@"
               }
             }
           }
      """;

  private static final String          DOMAIN_FILTERING_QUERY       = """
            ,{
              "term": {
                "domainId": {
                  "value": "@domainId@"
                }
              }
            }
      """;

  private static final String          AUDIENCE_FILTERING_QUERY     = """
            ,{
              "terms": {
                "audience": [
                  @spaceList@
                ]
              }
            }
      """;

  private static final String          DATE_FILTERING_QUERY         = ", \n @startDateQuery@ @endDateQuery@ \n";

  private static final String          ILLEGAL_SEARCH_CHARACTERS    = "\\!?^()+-=<>{}[]:\"*~&|#%@";

  private final ConfigurationManager   configurationManager;

  private final ElasticSearchingClient client;

  private String                       index;

  private String                       searchQueryFilePath;

  private String                       searchQuery;

  private RuleStorage                  ruleStorage;

  public RuleSearchConnector(ConfigurationManager configurationManager,
                             RuleStorage ruleStorage,
                             ElasticSearchingClient client,
                             InitParams initParams) {
    this.ruleStorage = ruleStorage;
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

  public List<RuleDTO> search(RuleFilter filter, long offset, long limit) {
    if (offset < 0) {
      throw new IllegalArgumentException("Offset must be positive");
    }
    if (limit < 0) {
      throw new IllegalArgumentException("Limit must be positive");
    }
    if (StringUtils.isBlank(filter.getTerm())) {
      throw new IllegalArgumentException("Filter term is mandatory");
    }
    String esQuery = buildQueryStatement(filter, offset, limit);
    if (StringUtils.isBlank(esQuery)) {
      return Collections.emptyList();
    }
    String jsonResponse = this.client.sendRequest(esQuery, this.index);
    return buildSearchResult(jsonResponse);
  }

  public int count(RuleFilter filter) {
    if (StringUtils.isBlank(filter.getTerm())) {
      throw new IllegalArgumentException("Filter term is mandatory");
    }
    String esQuery = buildQueryStatement(filter, 0, 0);
    if (StringUtils.isBlank(esQuery)) {
      return 0;
    }
    String jsonResponse = this.client.sendRequest(esQuery, this.index);
    return buildCountResult(jsonResponse);
  }

  private String buildQueryStatement(RuleFilter filter, long offset, long limit) {
    String term = removeSpecialCharacters(filter.getTerm());
    Set<Long> spaceList = Optional.ofNullable(filter.getSpaceIds())
                                  .map(HashSet::new)
                                  .orElse(new HashSet<>());
    term = escapeIllegalCharacterInQuery(term);
    if (StringUtils.isBlank(term)) {
      return null;
    }
    term = term.trim();
    List<String> termsQuery = Arrays.stream(term.split(" ")).filter(StringUtils::isNotBlank).map(word -> {
      word = word.trim();
      if (word.length() > 4) {
        word = word + "~1";
      }
      return word;
    }).toList();
    String termQuery = StringUtils.join(termsQuery, " AND ");
    String query = retrieveSearchQuery();
    if (filter.getDomainId() > 0) {
      query = query.replace("@domain_filtering@", DOMAIN_FILTERING_QUERY);
    } else {
      query = query.replace("@domain_filtering@", "");
    }
    if (!CollectionUtils.isEmpty(filter.getSpaceIds())) {
      query = query.replace("@audience_filtering@", AUDIENCE_FILTERING_QUERY);
    } else {
      query = query.replace("@audience_filtering@", "");
    }
    DateFilterType dateFilterType = filter.getDateFilterType();
    if (dateFilterType != null) {
      String date = String.valueOf(System.currentTimeMillis());
      switch (dateFilterType) {
      case STARTED:
        query = query.replace(DATE_FILTERING, DATE_FILTERING_QUERY);
        query = query.replace(START_DATE_QUERY, DATE_FIELD_FILTERING_QUERY)
                     .replace(DATE_FIELD, START_DATE)
                     .replace(DATE_CONDITION, "lte")
                     .replace(DATE, date);
        query = query.replace(END_DATE_QUERY, "," + DATE_FIELD_FILTERING_QUERY)
                     .replace(DATE_FIELD, END_DATE)
                     .replace(DATE_CONDITION, "gte")
                     .replace(DATE, date);
        break;
      case NOT_STARTED:
        query = query.replace(DATE_FILTERING, DATE_FILTERING_QUERY);
        query = query.replace(START_DATE_QUERY, DATE_FIELD_FILTERING_QUERY)
                     .replace(END_DATE_QUERY, "")
                     .replace(DATE_FIELD, START_DATE)
                     .replace(DATE_CONDITION, "gt")
                     .replace(DATE, date);
        break;
      case ENDED:
        query = query.replace(DATE_FILTERING, DATE_FILTERING_QUERY);
        query = query.replace(END_DATE_QUERY, DATE_FIELD_FILTERING_QUERY)
                     .replace(START_DATE_QUERY, "")
                     .replace(DATE_FIELD, END_DATE)
                     .replace(DATE_CONDITION, "lt")
                     .replace(DATE, date);
        break;
      default:
        query = query.replace(DATE_FILTERING, "");
      }
    } else {
      query = query.replace(DATE_FILTERING, "");
    }

    return query.replace("@domainId@", String.valueOf(filter.getDomainId()))
                .replace("@term@", term)
                .replace("@term_query@", termQuery)
                .replace("@spaceList@", StringUtils.join(spaceList, ","))
                .replace("@offset@", String.valueOf(offset))
                .replace("@limit@", String.valueOf(limit));
  }

  @SuppressWarnings("rawtypes")
  private List<RuleDTO> buildSearchResult(String jsonResponse) {
    LOG.debug("Search Query response from ES : {} ", jsonResponse);

    List<RuleDTO> results = new ArrayList<>();
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

    //
    JSONArray jsonHits = (JSONArray) jsonResult.get("hits");
    for (Object jsonHit : jsonHits) {
      try {
        JSONObject jsonHitObject = (JSONObject) jsonHit;
        JSONObject hitSource = (JSONObject) jsonHitObject.get("_source");
        long id = parseLong(hitSource, "id");
        results.add(ruleStorage.findRuleById(id));
      } catch (Exception e) {
        LOG.warn("Error processing rules search result item, ignore it from results", e);
      }
    }
    return results;
  }

  @SuppressWarnings("rawtypes")
  private int buildCountResult(String jsonResponse) {
    Map json = null;
    JSONParser parser = new JSONParser();
    try {
      json = (Map) parser.parse(jsonResponse);
    } catch (ParseException e) {
      throw new ElasticSearchException("Unable to parse JSON response", e);
    }
    JSONObject jsonResult = (JSONObject) json.get("hits");
    if (jsonResult == null) {
      return 0;
    }
    JSONObject total = (JSONObject) jsonResult.get("total");
    return Integer.parseInt(total.get("value").toString());
  }

  private long parseLong(JSONObject hitSource, String key) {
    String value = (String) hitSource.get(key);
    return StringUtils.isBlank(value) ? 0 : Long.parseLong(value);
  }

  private String removeSpecialCharacters(String string) {
    string = Normalizer.normalize(string, Normalizer.Form.NFD);
    string = string.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
    return string;
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

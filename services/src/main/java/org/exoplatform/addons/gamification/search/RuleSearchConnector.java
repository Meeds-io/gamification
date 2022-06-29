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
import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.exoplatform.addons.gamification.entities.domain.configuration.RuleEntity;
import org.exoplatform.addons.gamification.service.dto.configuration.RuleDTO;
import org.exoplatform.addons.gamification.service.dto.configuration.RuleFilter;
import org.exoplatform.addons.gamification.service.dto.configuration.constant.TypeRule;
import org.exoplatform.addons.gamification.utils.Utils;
import org.exoplatform.commons.search.es.ElasticSearchException;
import org.exoplatform.commons.search.es.client.ElasticSearchingClient;
import org.exoplatform.commons.utils.IOUtil;
import org.exoplatform.commons.utils.PropertyManager;
import org.exoplatform.container.configuration.ConfigurationManager;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.PropertiesParam;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class RuleSearchConnector {

  private static final Log             LOG                          = ExoLogger.getLogger(RuleSearchConnector.class);

  private static final String          SEARCH_QUERY_FILE_PATH_PARAM = "query.file.path";

  private final ConfigurationManager   configurationManager;

  private final ElasticSearchingClient client;

  private String                       index;

  private String                       searchQueryFilePath;

  private String                       searchQuery;

  public RuleSearchConnector(ConfigurationManager configurationManager, ElasticSearchingClient client, InitParams initParams) {
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

  public List<RuleEntity> search(RuleFilter filter, long offset, long limit) {
    if (offset < 0) {
      throw new IllegalArgumentException("Offset must be positive");
    }
    if (limit < 0) {
      throw new IllegalArgumentException("Limit must be positive");
    }
    if (StringUtils.isBlank(filter.getTerm())) {
      throw new IllegalArgumentException("Filter term is mandatory");
    }
    if (filter.getSpaceIds().isEmpty()) {
      throw new IllegalArgumentException("Filter spaceIds is mandatory");
    }
    if (filter.getDomainId() == 0 ) {
      throw new IllegalArgumentException("filter domain id must be positive");
    }
    String esQuery = buildQueryStatement(filter, offset, limit);
    String jsonResponse = this.client.sendRequest(esQuery, this.index);
    return buildSearchResult(jsonResponse);
  }

  public int count(RuleFilter filter) {
    if (StringUtils.isBlank(filter.getTerm())) {
      throw new IllegalArgumentException("Filter term is mandatory");
    }
    if (filter.getSpaceIds().isEmpty()) {
      throw new IllegalArgumentException("Filter spaceIds is mandatory");
    }
    if (filter.getDomainId() == 0 ) {
      throw new IllegalArgumentException("filter domain id must be positive");
    }
    String esQuery = buildQueryStatement(filter, 0, 0);
    String jsonResponse = this.client.sendRequest(esQuery, this.index);
    return buildCountResult(jsonResponse);
  }

  private String buildQueryStatement(RuleFilter filter, long offset, long limit) {
    String term = removeSpecialCharacters(filter.getTerm());
    List<String> termsQuery = Arrays.stream(term.split(" ")).filter(StringUtils::isNotBlank).map(word -> {
      word = word.trim();
      if (word.length() > 5) {
        word = word + "~1";
      }
      return word;
    }).collect(Collectors.toList());
    String termQuery = StringUtils.join(termsQuery, " AND ");
    return retrieveSearchQuery().replace("@domainId@", String.valueOf(filter.getDomainId()))
                                .replace("@term_query@", termQuery)
                                .replace("@spaceList@", StringUtils.join(filter.getSpaceIds(), ","))
                                .replace("@offset@", String.valueOf(offset))
                                .replace("@limit@", String.valueOf(limit));
  }

  @SuppressWarnings("rawtypes")
  private List<RuleEntity> buildSearchResult(String jsonResponse) {
    LOG.debug("Search Query response from ES : {} ", jsonResponse);

    List<RuleEntity> results = new ArrayList<>();
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
        RuleEntity rule = new RuleEntity();

        JSONObject jsonHitObject = (JSONObject) jsonHit;
        JSONObject hitSource = (JSONObject) jsonHitObject.get("_source");
        long id = parseLong(hitSource, "id");
        String title = (String) hitSource.get("title");
        String description = (String) hitSource.get("description");
        int score = parseLong(hitSource, "score").intValue();
        String area = (String) hitSource.get("area");
        Long domainId = parseLong(hitSource, "domainId");
        boolean enabled = Boolean.valueOf(String.valueOf(hitSource.get("enabled")));
        boolean deleted = Boolean.valueOf(String.valueOf(hitSource.get("deleted")));
        String createdBy = (String) hitSource.get("createdBy");
        Date createdDate = parseDate(hitSource,"createdDate" );
        Date lastModifiedDate = parseDate(hitSource,"lastModifiedDate");
        String lastModifiedBy = (String) hitSource.get("lastModifiedBy");
        String event = (String) hitSource.get("event");
        long audience = parseLong(hitSource, "audience");
        Date startDate =  parseDate(hitSource,"startDate");
        Date endDate = parseDate(hitSource,"endDate");
        String type = (String) hitSource.get("type");

        rule.setId(id);
        rule.setTitle(title);
        rule.setDescription(description);
        rule.setEvent(event);
        rule.setDomainEntity(Utils.getDomainById(domainId));
        rule.setScore(score);
        rule.setArea(area);
        rule.setEnabled(enabled);
        rule.setDeleted(deleted);
        rule.setAudience(audience);
        rule.setEndDate(endDate);
        rule.setStartDate(startDate);
        rule.setType(TypeRule.valueOf(type));
        rule.setManagers(getManagersList(hitSource, "managers"));
        rule.setCreatedBy(createdBy);
        rule.setCreatedDate(createdDate);
        rule.setLastModifiedBy(lastModifiedBy);
        rule.setLastModifiedDate(lastModifiedDate);

        results.add(rule);
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

  private Long parseLong(JSONObject hitSource, String key) {
    String value = (String) hitSource.get(key);
    return StringUtils.isBlank(value) ? null : Long.parseLong(value);
  }

  private Date parseDate(JSONObject hitSource, String key) {
    Long value = parseLong(hitSource, key);
    return value != 0 ? new Date(value) : null;
  }

  private List<Long> getManagersList(JSONObject hitSource, String key) {
    JSONArray jsonHits = (JSONArray) hitSource.get("managers");
    if (jsonHits.isEmpty()) {
      return Collections.emptyList();
    } else {
      List<Long> listManagers = new ArrayList<>();
      for (Object obj : jsonHits) {
        listManagers.add((Long) obj);
      }
      return listManagers;
    }
  }

  private String removeSpecialCharacters(String string) {
    string = Normalizer.normalize(string, Normalizer.Form.NFD);
    string = string.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "").replace("'", " ");
    return string;
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

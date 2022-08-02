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
package org.exoplatform.addons.gamification.connector;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.exoplatform.addons.gamification.entities.domain.configuration.RuleEntity;
import org.exoplatform.addons.gamification.storage.dao.RuleDAO;
import org.exoplatform.commons.search.domain.Document;
import org.exoplatform.commons.search.index.impl.ElasticIndexingServiceConnector;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.manager.IdentityManager;

public class RuleIndexingServiceConnector extends ElasticIndexingServiceConnector {

  public static final String INDEX = "rules";

  private static final Log   LOG   = ExoLogger.getLogger(RuleIndexingServiceConnector.class);

  private RuleDAO            ruleDAO;

  private IdentityManager    identityManager;

  public RuleIndexingServiceConnector(RuleDAO ruleDAO,
                                      IdentityManager identityManager,
                                      InitParams initParams) {
    super(initParams);
    this.ruleDAO = ruleDAO;
    this.identityManager = identityManager;
  }

  @Override
  public String getConnectorName() {
    return INDEX;
  }

  @Override
  public Document create(String id) {
    return getDocument(id);
  }

  @Override
  public Document update(String id) {
    return getDocument(id);
  }

  @Override
  public List<String> getAllIds(int offset, int limit) {
    throw new UnsupportedOperationException();
  }

  private Document getDocument(String id) {
    if (StringUtils.isBlank(id)) {
      throw new IllegalArgumentException("id is mandatory");
    }
    LOG.debug("Index document for rule with id={}", id);

    RuleEntity rule = ruleDAO.find(Long.valueOf(id));
    if (rule == null) {
      throw new IllegalStateException("rule with id '" + id + "' not found");
    }
    Map<String, String> fields = new HashMap<>();
    fields.put("id", Long.toString(rule.getId()));
    fields.put("title", rule.getTitle());
    fields.put("description", StringEscapeUtils.unescapeHtml(rule.getDescription()));
    fields.put("score", String.valueOf(rule.getScore()));
    fields.put("area", !StringUtils.isBlank(rule.getArea()) ? rule.getArea() : rule.getDomainEntity().getTitle());
    fields.put("domainId", String.valueOf(rule.getDomainEntity().getId()));
    fields.put("event", rule.getEvent());
    fields.put("audience", String.valueOf(rule.getAudience() != null ? rule.getAudience() : 0));
    fields.put("startDate", toMilliSecondsString(rule.getStartDate()));
    fields.put("endDate", toMilliSecondsString(rule.getEndDate()));
    fields.put("createdBy", getUserIdentityId(rule.getCreatedBy()));
    fields.put("createdDate", toMilliSecondsString(rule.getCreatedDate()));
    fields.put("lastModifiedBy", getUserIdentityId(rule.getLastModifiedBy()));
    fields.put("lastModifiedDate", toMilliSecondsString(rule.getLastModifiedDate()));
    fields.put("type", rule.getType().name());
    Document document = new Document(id, null, new Date(System.currentTimeMillis()), Collections.emptySet(), fields);
    if (CollectionUtils.isNotEmpty(rule.getManagers())) {
      document.addListField("managers", rule.getManagers().stream().map(String::valueOf).collect(Collectors.toList()));
    }
    return document;
  }

  private String getUserIdentityId(String username) {
    String userIdentityId = "0";
    if (StringUtils.isNotBlank(username)) {
      Identity identity = identityManager.getOrCreateUserIdentity(username);
      if (identity != null) {
        userIdentityId = identity.getId();
      }
    }
    return userIdentityId;
  }

  private String toMilliSecondsString(Date date) {
    return date != null ? String.valueOf(date.getTime()) : "0";
  }
}

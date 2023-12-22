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

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.exoplatform.commons.search.domain.Document;
import org.exoplatform.commons.search.index.impl.ElasticIndexingServiceConnector;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.resources.LocaleConfig;
import org.exoplatform.services.resources.LocaleConfigService;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.search.DocumentWithMetadata;
import org.exoplatform.social.metadata.MetadataService;
import org.exoplatform.social.metadata.model.MetadataItem;
import org.exoplatform.social.metadata.model.MetadataObject;

import io.meeds.gamification.model.ProgramDTO;
import io.meeds.gamification.model.RuleDTO;
import io.meeds.gamification.plugin.RuleTranslationPlugin;
import io.meeds.gamification.storage.RuleStorage;
import io.meeds.gamification.utils.Utils;
import io.meeds.social.translation.model.TranslationField;
import io.meeds.social.translation.service.TranslationService;

public class RuleIndexingServiceConnector extends ElasticIndexingServiceConnector {

  public static final String RULE_OBJECT_TYPE            = "rule";

  public static final String  INDEX = "rules";

  private static final Log    LOG   = ExoLogger.getLogger(RuleIndexingServiceConnector.class);

  private TranslationService  translationService;

  private RuleStorage         ruleStorage;

  private MetadataService     metadataService;

  private LocaleConfigService localeConfigService;

  private IdentityManager     identityManager;

  public RuleIndexingServiceConnector(RuleStorage ruleStorage,
                                      TranslationService translationService,
                                      IdentityManager identityManager,
                                      LocaleConfigService localeConfigService,
                                      MetadataService metadataService,
                                      InitParams initParams) {
    super(initParams);
    this.ruleStorage = ruleStorage;
    this.translationService = translationService;
    this.identityManager = identityManager;
    this.localeConfigService = localeConfigService;
    this.metadataService = metadataService;
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

    Long ruleId = Long.valueOf(id);
    RuleDTO rule = ruleStorage.findRuleById(Long.valueOf(id));
    if (rule == null) {
      throw new IllegalStateException("rule with id '" + id + "' not found");
    }
    Map<String, String> fields = new HashMap<>();
    fields.put("id", Long.toString(ruleId));
    addTranslationLabels(ruleId, RuleTranslationPlugin.RULE_TITLE_FIELD_NAME, fields, rule.getTitle());
    addTranslationLabels(ruleId, RuleTranslationPlugin.RULE_DESCRIPTION_FIELD_NAME, fields, rule.getDescription());

    fields.put("score", String.valueOf(rule.getScore()));
    fields.put("event", rule.getEvent());
    fields.put("startDate", toMilliSecondsString(rule.getStartDate()));
    // To add end of the day
    fields.put("endDate", toMilliSecondsString(rule.getEndDate()));
    fields.put("createdBy", getUserIdentityId(rule.getCreatedBy()));
    fields.put("createdDate", toMilliSecondsString(rule.getCreatedDate()));
    fields.put("lastModifiedBy", getUserIdentityId(rule.getLastModifiedBy()));
    fields.put("lastModifiedDate", toMilliSecondsString(rule.getLastModifiedDate()));
    fields.put("type", rule.getType().name());

    DocumentWithMetadata document = new DocumentWithMetadata();
    document.setId(id);
    document.setLastUpdatedDate(new Date());
    document.setFields(fields);
    document.setPermissions(Collections.emptySet());

    ProgramDTO program = rule.getProgram();
    if (program == null) {
      document.addField("domainId", "");
      document.addField("audience", "");
      document.addListField("managers", Collections.emptyList());
    } else {
      document.addField("domainId", String.valueOf(program.getId()));
      document.addField("audience", String.valueOf(program.getSpaceId()));
      if (CollectionUtils.isNotEmpty(program.getOwnerIds())) {
        document.addListField("managers", program.getOwnerIds().stream().map(String::valueOf).toList());
      } else {
        document.addListField("managers", Collections.emptyList());
      }
    }
    addDocumentMetadata(document, new MetadataObject(RULE_OBJECT_TYPE, id, null, rule.getSpaceId()));
    return document;
  }

  private void addDocumentMetadata(DocumentWithMetadata document, MetadataObject metadataObject) {
    List<MetadataItem> metadataItems = metadataService.getMetadataItemsByObject(metadataObject);
    document.setMetadataItems(metadataItems);
  }

  private void addTranslationLabels(Long ruleId, String fieldName, Map<String, String> fields, String defaultLabel) {
    try {
      TranslationField translationField = translationService.getTranslationField(RuleTranslationPlugin.RULE_OBJECT_TYPE,
                                                                                 ruleId,
                                                                                 fieldName);
      translationField.getLabels()
                      .forEach((locale, label) -> addLocalizedLabel(fields, fieldName, locale, label));
      List<Locale> supportedLocales = localeConfigService.getLocalConfigs().stream().map(LocaleConfig::getLocale).toList();
      supportedLocales.stream()
                      .filter(locale -> !translationField.getLabels().containsKey(locale))
                      .forEach(locale -> addLocalizedLabel(fields, fieldName, locale, defaultLabel));
    } catch (ObjectNotFoundException e) {
      LOG.warn("Error retrieving Translation Labels of rule {}", ruleId, e);
    }
  }

  private String addLocalizedLabel(Map<String, String> fields, String fieldName, Locale locale, String label) {
    return fields.put(fieldName + "_" + locale.toLanguageTag(),
                      StringUtils.lowerCase(removeSpecialCharacters(label)));
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

  private String toMilliSecondsString(String date) {
    return date != null ? String.valueOf(Utils.parseSimpleDate(date).getTime()) : "0";
  }

}

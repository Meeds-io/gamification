/**
 * This file is part of the Meeds project (https://meeds.io/).
 *
 * Copyright (C) 2020 - 2025 Meeds Association contact@meeds.io
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package io.meeds.gamification.plugin;

import static io.meeds.gamification.plugin.RuleTranslationPlugin.RULE_OBJECT_TYPE;
import static io.meeds.gamification.plugin.RuleTranslationPlugin.RULE_TITLE_FIELD_NAME;

import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.exoplatform.container.PortalContainer;
import org.exoplatform.portal.config.UserACL;
import org.exoplatform.services.security.Identity;
import org.exoplatform.social.core.manager.IdentityManager;

import io.meeds.gamification.constant.DateFilterType;
import io.meeds.gamification.constant.EntityStatusType;
import io.meeds.gamification.model.RuleDTO;
import io.meeds.gamification.model.filter.RuleFilter;
import io.meeds.gamification.service.RuleService;
import io.meeds.social.cms.model.ContentLinkExtension;
import io.meeds.social.cms.model.ContentLinkSearchResult;
import io.meeds.social.cms.plugin.ContentLinkPlugin;
import io.meeds.social.cms.service.ContentLinkPluginService;
import io.meeds.social.translation.service.TranslationService;

import jakarta.annotation.PostConstruct;

@Component
public class RuleContentLinkPlugin implements ContentLinkPlugin {

  public static final String                OBJECT_TYPE = RulePermanentLinkPlugin.OBJECT_TYPE;

  private static final String               TITLE_KEY   = "contentLink.action";

  private static final String               ICON        = "fa fa-trophy";

  private static final String               COMMAND     = "action";

  private static final ContentLinkExtension EXTENSION   = new ContentLinkExtension(OBJECT_TYPE,
                                                                                   TITLE_KEY,
                                                                                   ICON,
                                                                                   COMMAND);

  @Autowired
  private RuleService                       ruleService;

  @Autowired
  private IdentityManager                   identityManager;

  @Autowired
  private TranslationService                translationService;

  @Autowired
  private UserACL                           userAcl;

  @Autowired
  private PortalContainer                   container;

  @PostConstruct
  public void init() {
    container.getComponentInstanceOfType(ContentLinkPluginService.class).addPlugin(this);
  }

  @Override
  public ContentLinkExtension getExtension() {
    return EXTENSION;
  }

  @Override
  public List<ContentLinkSearchResult> search(String keyword,
                                              Identity identity,
                                              Locale locale,
                                              int offset,
                                              int limit) {

    RuleFilter filter = new RuleFilter();
    filter.setTerm(keyword);
    filter.setLocale(locale);
    filter.setDateFilterType(DateFilterType.ALL);
    filter.setProgramStatus(EntityStatusType.ENABLED);
    filter.setStatus(EntityStatusType.ENABLED);
    if (!userAcl.isAnonymousUser(identity)) {
      org.exoplatform.social.core.identity.model.Identity userIdentity =
                                                                       identityManager.getOrCreateUserIdentity(identity.getUserId());
      filter.setIdentityId(Long.parseLong(userIdentity.getId()));
    }

    return ruleService.getRules(filter,
                                offset,
                                limit)
                      .stream()
                      .map(r -> toContentLink(r, locale))
                      .toList();
  }

  @Override
  public String getContentTitle(String objectId, Locale locale) {
    RuleDTO rule = ruleService.findRuleById(Long.parseLong(objectId));
    return rule == null ? null : getRuleTitle(rule, locale);
  }

  private ContentLinkSearchResult toContentLink(RuleDTO rule, Locale locale) {
    return new ContentLinkSearchResult(OBJECT_TYPE,
                                       String.valueOf(rule.getId()),
                                       getRuleTitle(rule, locale),
                                       EXTENSION.getIcon());
  }

  private String getRuleTitle(RuleDTO rule, Locale locale) {
    String translatedTitle = translationService.getTranslationLabelOrDefault(RULE_OBJECT_TYPE,
                                                                             rule.getId(),
                                                                             RULE_TITLE_FIELD_NAME,
                                                                             locale);
    if (StringUtils.isNotBlank(translatedTitle)) {
      return translatedTitle;
    } else {
      return rule.getTitle();
    }
  }

}

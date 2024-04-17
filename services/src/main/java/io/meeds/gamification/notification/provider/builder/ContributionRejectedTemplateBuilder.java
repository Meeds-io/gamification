/*
 * This file is part of the Meeds project (https://meeds.io/).
 *
 * Copyright (C) 2020 - 2024 Meeds Association contact@meeds.io
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package io.meeds.gamification.notification.provider.builder;

import static io.meeds.gamification.plugin.RuleTranslationPlugin.RULE_OBJECT_TYPE;
import static io.meeds.gamification.plugin.RuleTranslationPlugin.RULE_TITLE_FIELD_NAME;
import static io.meeds.gamification.utils.Utils.REALIZATION_ID_NOTIFICATION_PARAM;
import static io.meeds.gamification.utils.Utils.getIdentityById;

import java.io.Writer;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;

import org.exoplatform.commons.api.notification.NotificationContext;
import org.exoplatform.commons.api.notification.channel.template.AbstractTemplateBuilder;
import org.exoplatform.commons.api.notification.channel.template.TemplateProvider;
import org.exoplatform.commons.api.notification.model.MessageInfo;
import org.exoplatform.commons.api.notification.model.NotificationInfo;
import org.exoplatform.commons.api.notification.service.template.TemplateContext;
import org.exoplatform.commons.notification.template.TemplateUtils;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.notification.LinkProviderUtils;
import org.exoplatform.social.notification.plugin.SocialNotificationUtils;
import org.exoplatform.webui.utils.TimeConvertUtils;

import io.meeds.gamification.model.Announcement;
import io.meeds.gamification.model.RuleDTO;
import io.meeds.gamification.service.AnnouncementService;
import io.meeds.gamification.service.RuleService;
import io.meeds.social.translation.service.TranslationService;

public class ContributionRejectedTemplateBuilder extends AbstractTemplateBuilder {

  private final RuleService         ruleService;

  private final AnnouncementService announcementService;

  private final TranslationService  translationService;

  private final TemplateProvider    templateProvider;

  private final boolean             pushNotification;

  public ContributionRejectedTemplateBuilder(RuleService ruleService,
                                             AnnouncementService announcementService,
                                             TranslationService translationService,
                                             TemplateProvider templateProvider,
                                             boolean pushNotification) {
    this.ruleService = ruleService;
    this.announcementService = announcementService;
    this.translationService = translationService;
    this.templateProvider = templateProvider;
    this.pushNotification = pushNotification;
  }

  @Override
  protected MessageInfo makeMessage(NotificationContext ctx) { // NOSONAR
    NotificationInfo notification = ctx.getNotificationInfo();
    String announcementId = notification.getValueOwnerParameter(REALIZATION_ID_NOTIFICATION_PARAM);
    if (StringUtils.isBlank(announcementId)) {
      return null;
    }
    Announcement announcement = announcementService.getAnnouncementById(Long.parseLong(announcementId));
    if (announcement == null) {
      return null;
    }

    long ruleId = announcement.getChallengeId();
    RuleDTO rule = ruleService.findRuleById(ruleId);
    if (rule == null || rule.isDeleted() || rule.getProgram() == null || rule.getProgram().isDeleted()) {
      return null;
    }

    Identity identity = getIdentityById(announcement.getCreator().toString());
    if (identity == null || identity.isDeleted() || !identity.isEnable()) {
      return null;
    }

    String pluginId = notification.getKey().getId();
    String language = getLanguage(notification);
    TemplateContext templateContext = TemplateContext.newChannelInstance(templateProvider.getChannelKey(), pluginId, language);

    Locale userLocale = new Locale(language);
    String ruleTitle = translationService.getTranslationLabel(RULE_OBJECT_TYPE, ruleId, RULE_TITLE_FIELD_NAME, userLocale);
    if (StringUtils.isBlank(ruleTitle)) {
      ruleTitle = rule.getTitle();
    }

    // Rule
    templateContext.put("RULE_TITLE", ruleTitle);

    // Realization
    templateContext.put("ANNOUNCEMENT_MESSAGE", announcement.getComment());
    String announcementUrl = LinkProviderUtils.getRedirectUrl("view_full_activity", String.valueOf(announcement.getActivityId()));
    templateContext.put("ANNOUNCEMENT_URL", announcementUrl);

    // Mail Footer
    SocialNotificationUtils.addFooterAndFirstName(notification.getTo(), templateContext);

    // Web Notif status
    templateContext.put("NOTIFICATION_ID", notification.getId());
    templateContext.put("LAST_UPDATED_TIME",
                        TimeConvertUtils.convertXTimeAgoByTimeServer(new Date(notification.getLastModifiedDate()),
                                                                     "EE, dd yyyy",
                                                                     userLocale,
                                                                     TimeConvertUtils.YEAR));

    MessageInfo messageInfo = new MessageInfo();
    if (pushNotification) {
      // Push Notif Url
      messageInfo.subject(announcementUrl);
    } else {
      // Mail Subject
      messageInfo.subject(TemplateUtils.processSubject(templateContext));
    }
    messageInfo.body(TemplateUtils.processGroovy(templateContext));
    ctx.setException(templateContext.getException());
    return messageInfo.end();
  }

  @Override
  protected boolean makeDigest(NotificationContext ctx, Writer writer) {
    return false;
  }

}

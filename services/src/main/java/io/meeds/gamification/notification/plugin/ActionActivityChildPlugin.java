/**
 * This file is part of the Meeds project (https://meeds.io/).
 *
 * Copyright (C) 2020 - 2023 Meeds Association contact@meeds.io
 *
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
package io.meeds.gamification.notification.plugin;

import static io.meeds.gamification.plugin.RuleTranslationPlugin.RULE_OBJECT_TYPE;
import static io.meeds.gamification.plugin.RuleTranslationPlugin.RULE_TITLE_FIELD_NAME;
import static io.meeds.gamification.utils.Utils.RULE_ACTIVITY_PARAM_RULE_ID;
import static io.meeds.gamification.utils.Utils.RULE_ACTIVITY_TYPE;

import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import org.exoplatform.commons.api.notification.NotificationContext;
import org.exoplatform.commons.api.notification.model.NotificationInfo;
import org.exoplatform.commons.api.notification.plugin.AbstractNotificationChildPlugin;
import org.exoplatform.commons.api.notification.service.template.TemplateContext;
import org.exoplatform.commons.notification.template.TemplateUtils;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.social.core.activity.model.ExoSocialActivity;
import org.exoplatform.social.core.manager.ActivityManager;
import org.exoplatform.social.notification.LinkProviderUtils;
import org.exoplatform.social.notification.plugin.SocialNotificationUtils;

import io.meeds.gamification.model.ProgramDTO;
import io.meeds.gamification.model.RuleDTO;
import io.meeds.gamification.service.RuleService;
import io.meeds.social.translation.service.TranslationService;

public class ActionActivityChildPlugin extends AbstractNotificationChildPlugin {

  private ActivityManager    activityManager;

  private TranslationService translationService;

  private RuleService        ruleService;

  public ActionActivityChildPlugin(ActivityManager activityManager,
                                   RuleService ruleService,
                                   TranslationService translationService,
                                   InitParams initParams) {
    super(initParams);
    this.activityManager = activityManager;
    this.translationService = translationService;
    this.ruleService = ruleService;
  }

  @Override
  public String makeContent(NotificationContext ctx) {
    NotificationInfo notification = ctx.getNotificationInfo();
    if (notification == null) {
      return "";
    }

    String language = getLanguage(notification);
    TemplateContext templateContext = new TemplateContext(getId(), language);

    String activityId = notification.getValueOwnerParameter(SocialNotificationUtils.ACTIVITY_ID.getKey());
    ExoSocialActivity activity = activityManager.getActivity(activityId);
    if (activity == null || activity.isComment()) {
      return "";
    }
    String ruleId = getActivityParamValue(activity, RULE_ACTIVITY_PARAM_RULE_ID);
    if (StringUtils.isBlank(ruleId)) {
      return "";
    }
    RuleDTO rule = ruleService.findRuleById(Long.parseLong(ruleId));
    if (rule == null
        || rule.isDeleted()
        || rule.getProgram() == null
        || rule.getProgram().isDeleted()) {
      return null;
    }
    ProgramDTO program = rule.getProgram();

    Locale userLocale = new Locale(language);
    String ruleTitle = translationService.getTranslationLabel(RULE_OBJECT_TYPE,
                                                              Long.parseLong(ruleId),
                                                              RULE_TITLE_FIELD_NAME,
                                                              userLocale);
    if (StringUtils.isBlank(ruleTitle)) {
      ruleTitle = rule.getTitle();
    }

    // Program
    templateContext.put("PROGRAM_AVATAR", program.getAvatarUrl());

    // Rule
    templateContext.put("RULE_ID", String.valueOf(ruleId));
    templateContext.put("RULE_TITLE", ruleTitle);
    String ruleUrl = LinkProviderUtils.getRedirectUrl("view_full_activity", String.valueOf(rule.getActivityId()));
    templateContext.put("RULE_URL", ruleUrl);
    return TemplateUtils.processGroovy(templateContext);
  }

  @Override
  public String getId() {
    return RULE_ACTIVITY_TYPE;
  }

  @Override
  public boolean isValid(NotificationContext ctx) {
    return false;
  }

  public String getActivityParamValue(ExoSocialActivity activity, String key) {
    Map<String, String> params = activity.getTemplateParams();
    if (params != null) {
      return params.get(key) != null ? params.get(key) : "";
    }
    return null;
  }

}

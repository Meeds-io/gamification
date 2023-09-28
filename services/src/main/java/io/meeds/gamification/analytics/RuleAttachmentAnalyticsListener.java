/*
 * This file is part of the Meeds project (https://meeds.io/).
 * Copyright (C) 2020 - 2023 Meeds Association contact@meeds.io
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
package io.meeds.gamification.analytics;

import io.meeds.gamification.model.ProgramDTO;
import io.meeds.gamification.model.RuleDTO;
import io.meeds.gamification.service.RuleService;
import org.exoplatform.analytics.listener.social.BaseAttachmentAnalyticsListener;
import org.exoplatform.analytics.model.StatisticData;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.services.listener.Asynchronous;
import org.exoplatform.social.attachment.AttachmentService;
import org.exoplatform.social.attachment.model.ObjectAttachmentId;
import org.exoplatform.social.core.space.spi.SpaceService;

@Asynchronous
public class RuleAttachmentAnalyticsListener extends BaseAttachmentAnalyticsListener {

  private RuleService ruleService;

  public RuleAttachmentAnalyticsListener(AttachmentService attachmentService,
                                         SpaceService spaceService,
                                         InitParams initParams,
                                         RuleService ruleService) {
    super(attachmentService, spaceService, initParams);
    this.ruleService = ruleService;
  }

  @Override
  protected void extendStatisticData(StatisticData statisticData, ObjectAttachmentId objectAttachment) {
    RuleDTO rule = ruleService.findRuleById(Long.parseLong(objectAttachment.getObjectId()));
    if (rule != null) {
      ProgramDTO program = rule.getProgram();
      if (program != null) {
        statisticData.addParameter("programTitle", rule.getProgram().getTitle());
      }
      statisticData.addParameter("ruleTitle", rule.getTitle());
      statisticData.addParameter("objectType", "rule");
    }
  }

  @Override
  protected String getModule() {
    return "gamification";
  }

  @Override
  protected String getSubModule() {
    return "rule";
  }
}

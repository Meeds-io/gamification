/**
 * This file is part of the Meeds project (https://meeds.io/).
 * Copyright (C) 2022 Meeds Association
 * contact@meeds.io
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
package io.meeds.gamification.activity.processor;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import org.exoplatform.container.xml.InitParams;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.social.core.BaseActivityProcessorPlugin;
import org.exoplatform.social.core.activity.model.ExoSocialActivity;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.service.LinkProvider;

import io.meeds.gamification.model.Announcement;
import io.meeds.gamification.service.AnnouncementService;
import io.meeds.gamification.utils.Utils;

/**
 * @deprecated this was used when the announcement was of type 'Activity'
 */
@Deprecated(forRemoval = true, since = "1.5.0")
public class AnnouncementActivityProcessor extends BaseActivityProcessorPlugin {

  public static final String  ANNOUNCEMENT_COMMENT_PARAM = "announcementComment";

  private static final Log    LOG                        = ExoLogger.getLogger(AnnouncementActivityProcessor.class);

  private static final String APP_URL                    = "/contributions/actions/";

  private AnnouncementService announcementService;

  private IdentityManager     identityManager;

  public AnnouncementActivityProcessor(AnnouncementService announcementService,
                                                IdentityManager identityManager,
                                                InitParams params) {
    super(params);
    this.announcementService = announcementService;
    this.identityManager = identityManager;
  }

  @Override
  public void processActivity(ExoSocialActivity activity) {
    if (!Utils.ANNOUNCEMENT_ACTIVITY_TYPE.equals(activity.getType())) {
      return;
    }
    if (activity.isComment() || activity.getType() == null) {
      return;
    }
    String announcementId = activity.getTemplateParams().get("announcementId");
    if (StringUtils.isBlank(announcementId)) {
      LOG.error("announcement id must not null");
      return;
    }
    Announcement announcement = announcementService.getAnnouncementById(Long.parseLong(announcementId));
    if (announcement == null) {
      return;
    }
    Map<String, String> params = new HashMap<>();
    Identity identity = identityManager.getIdentity(String.valueOf(announcement.getAssignee()));
    if (identity != null) {
      params.put("announcementAssigneeUsername", identity.getRemoteId());
      params.put("announcementAssigneeFullName", identity.getProfile().getFullName());
      params.put("announcementChallenge",
                 getAnnouncementChallenge(String.valueOf(announcement.getChallengeId()),
                                          activity.getTemplateParams().get("announcementDescription")));
      if (activity.getTemplateParams().containsKey(ANNOUNCEMENT_COMMENT_PARAM)) {
        String title = activity.getTemplateParams().get(ANNOUNCEMENT_COMMENT_PARAM);
        if (StringUtils.isNotBlank(title)) {
          activity.setTitle(title);
          activity.getTemplateParams().put(ANNOUNCEMENT_COMMENT_PARAM, null);
        }
      }
      activity.getTemplateParams().putAll(params);
    }
  }

  private String getAnnouncementChallenge(String challengeId, String challengeDescription) {
    StringBuilder challenge = new StringBuilder();
    challenge.append("<a class='primary--text' href=\"");
    String portalName = LinkProvider.getPortalName("");
    String portalOwner = LinkProvider.getPortalOwner("");
    String url = "/" + portalName + "/" + portalOwner + APP_URL + challengeId;
    challenge.append(url);
    challenge.append("\" target=\"_self\"  rel=\"nofollow\"> ");
    challenge.append(challengeDescription);
    challenge.append(" </a>");
    return String.valueOf(challenge);
  }

}

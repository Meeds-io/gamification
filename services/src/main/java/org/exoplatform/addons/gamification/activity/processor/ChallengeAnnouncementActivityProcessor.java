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
package org.exoplatform.addons.gamification.activity.processor;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.exoplatform.addons.gamification.model.Announcement;
import org.exoplatform.addons.gamification.service.AnnouncementService;
import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.social.core.BaseActivityProcessorPlugin;
import org.exoplatform.social.core.activity.model.ExoSocialActivity;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.service.LinkProvider;

public class ChallengeAnnouncementActivityProcessor extends BaseActivityProcessorPlugin {

  public static final String        ANNOUNCEMENT_COMMENT_PARAM = "announcementComment";

  public static final String        ACTIVITY_TYPE              = "challenges-announcement";

  private static final Log          LOG                        =
                                        ExoLogger.getLogger(ChallengeAnnouncementActivityProcessor.class);

  private static final String       APP_URL                    = "/contributions/challenges/";

  private final AnnouncementService announcementService;

  private final IdentityManager     identityManager;

  public ChallengeAnnouncementActivityProcessor(AnnouncementService announcementService,
                                                IdentityManager identityManager,
                                                InitParams params) {
    super(params);
    this.announcementService = announcementService;
    this.identityManager = identityManager;
  }

  @Override
  public void processActivity(ExoSocialActivity activity) {
    if (!ACTIVITY_TYPE.equals(activity.getType())) {
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
    try {
      Announcement announcement = announcementService.getAnnouncementById(Long.parseLong(announcementId));
      if (announcement == null) {
        throw new ObjectNotFoundException("announcement does not exist");
      }
      Map<String, String> params = new HashMap<>();

      Identity identity = identityManager.getIdentity(String.valueOf(announcement.getAssignee()));
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
    } catch (ObjectNotFoundException e) {
      LOG.error("Unexpected error", e);
    }
  }

  private String getAnnouncementChallenge(String challengeId, String challengeDescription) {
    StringBuilder challenge = new StringBuilder();
    challenge.append("<a href=\"");
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

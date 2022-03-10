package org.exoplatform.addons.gamification.activity.processor;

import org.apache.commons.lang3.StringUtils;
import org.exoplatform.addons.gamification.service.AnnouncementService;
import org.exoplatform.addons.gamification.service.dto.configuration.Announcement;
import org.exoplatform.addons.gamification.service.dto.configuration.UserInfo;
import org.exoplatform.addons.gamification.utils.Utils;
import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.social.core.BaseActivityProcessorPlugin;
import org.exoplatform.social.core.activity.model.ExoSocialActivity;
import org.exoplatform.social.core.service.LinkProvider;

import java.util.*;

public class ChallengeAnnouncementActivityProcessor extends BaseActivityProcessorPlugin {
    private static final Log LOG = ExoLogger.getLogger(ChallengeAnnouncementActivityProcessor.class);

    private static final String ACTIVITY_TYPE = "challenges-announcement";

    private static final String APP_URL = "/challenges/";

    AnnouncementService announcementService;

    public ChallengeAnnouncementActivityProcessor(InitParams params, AnnouncementService announcementService) {
        super(params);
        this.announcementService = announcementService;
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
            UserInfo userInfo = Utils.getUserById(announcement.getAssignee(),null);
            params.put("announcementAssigneeUsername", userInfo.getRemoteId());
            params.put("announcementAssigneeFullName", userInfo.getFullName());
            params.put("announcementChallenge", getAnnouncementChallenge(String.valueOf(announcement.getChallengeId()), activity.getTemplateParams().get("announcementDescription")));
            activity.getTemplateParams().putAll(params);
        } catch (ObjectNotFoundException e) {
            LOG.error("Unexpected error", e);
        }
    }

    private String getAnnouncementChallenge(String challengeId, String challengeDescription){
        StringBuilder challenge = new StringBuilder();
        challenge.append( "<a href=\"");
        String portalName  = LinkProvider.getPortalName("");
        String portalOwner  = LinkProvider.getPortalOwner("");
        String url =  "/" + portalName + "/" + portalOwner + APP_URL + challengeId;
        challenge.append(url);
        challenge.append( "\" target=\"_self\"  rel=\"nofollow\"> ");
        challenge.append(challengeDescription);
        challenge.append(" </a>");
        return String.valueOf(challenge);
    }

}

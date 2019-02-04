package org.exoplatform.addons.gamification.listener.social.profile;

import org.exoplatform.addons.gamification.entities.domain.effective.GamificationActionsHistory;
import org.exoplatform.addons.gamification.listener.GamificationListener;
import org.exoplatform.addons.gamification.service.configuration.RuleService;
import org.exoplatform.addons.gamification.service.dto.configuration.RuleDTO;
import org.exoplatform.addons.gamification.service.effective.GamificationProcessor;
import org.exoplatform.addons.gamification.service.effective.GamificationService;
import org.exoplatform.commons.utils.CommonsUtils;
import org.exoplatform.services.listener.Asynchronous;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.profile.ProfileLifeCycleEvent;
import org.exoplatform.social.core.profile.ProfileListenerPlugin;
import org.exoplatform.social.core.space.spi.SpaceService;

import java.time.LocalDate;

@Asynchronous
public class GamificationProfileListener extends ProfileListenerPlugin implements GamificationListener {

    private static final Log LOG = ExoLogger.getLogger(GamificationProfileListener.class);

    protected RuleService ruleService;
    protected GamificationProcessor gamificationProcessor;
    protected IdentityManager identityManager;
    protected SpaceService spaceService;
    protected GamificationService gamificationService;

    public GamificationProfileListener() {
        this.ruleService = CommonsUtils.getService(RuleService.class);
        this.gamificationProcessor = CommonsUtils.getService(GamificationProcessor.class);
        this.identityManager = CommonsUtils.getService(IdentityManager.class);
        this.spaceService = CommonsUtils.getService(SpaceService.class);
        this.gamificationService = CommonsUtils.getService(GamificationService.class);
    }

    @Override
    public void avatarUpdated(ProfileLifeCycleEvent event) {

        GamificationActionsHistory aHisoty = null;

        Long lastUpdate = event.getProfile().getAvatarLastUpdated();

        // Do not reward a user when he update his avatar, reward user only when he add an avatar for the first time
        if (lastUpdate != null) return;

        // To hold GamificationRule
        RuleDTO ruleDto = null;

        // Get associated rule :
        ruleDto = ruleService.findEnableRuleByTitle(GAMIFICATION_SOCIAL_PROFILE_ADD_AVATAR);

        // Process only when an enable rule is found
        if (ruleDto != null) {
            try {
                aHisoty = build(ruleDto, event.getProfile().getId(),event.getProfile().getId(),"/portal/intranet/profile/");

                //Save actionHistoy entry
                gamificationProcessor.execute(aHisoty);
            } catch (Exception e) {
                LOG.error("Error to process gamification for Rule {}", ruleDto.getTitle(), e);
            }
        }

    }

    @Override
    public void bannerUpdated(ProfileLifeCycleEvent event) {

        GamificationActionsHistory aHistory = null;

        Long lastUpdate = event.getProfile().getBannerLastUpdated();

        // Do not reward a user when he update his avatar, reward user only when he add an avatar for the first time
        if (lastUpdate != null) return;

        // To hold GamificationRule
        RuleDTO ruleDto = null;

        // Get associated rule :
        ruleDto = ruleService.findEnableRuleByTitle(GAMIFICATION_SOCIAL_PROFILE_ADD_BANNER);

        // Process only when an enable rule is found
        if (ruleDto != null) {
            try {
                String receiver =event.getProfile().getId();
               aHistory = build(ruleDto, event.getProfile().getId(),receiver,"/portal/intranet/profile/");

                // Save actionHistory entry
                gamificationProcessor.execute(aHistory);
                // Gamification simple audit logger
                LOG.info("service=gamification operation=add-new-entry parameters=\"date:{},user_social_id:{},global_score:{},domain:{},action_title:{},action_score:{}\"", LocalDate.now(),aHistory.getUserSocialId(), aHistory.getGlobalScore(), ruleDto.getArea(), ruleDto.getTitle(), ruleDto.getScore());
            } catch (Exception e) {
                LOG.error("Error to process gamification for Rule {}", ruleDto.getTitle(), e);
            }
        }
    }

    @Override
    public void basicInfoUpdated(ProfileLifeCycleEvent event) {

    }

    @Override
    public void contactSectionUpdated(ProfileLifeCycleEvent event) {

    }

    @Override
    public void experienceSectionUpdated(ProfileLifeCycleEvent event) {

    }

    @Override
    public void headerSectionUpdated(ProfileLifeCycleEvent event) {

    }

    @Override
    public void createProfile(ProfileLifeCycleEvent event) {


    }


    @Override
    public void aboutMeUpdated(ProfileLifeCycleEvent event) {

        GamificationActionsHistory aHistory = null;

        // To hold GamificationRule
        RuleDTO ruleDto = null;

        // Get associated rule : Reward user each time he update «about me» section
        ruleDto = ruleService.findEnableRuleByTitle(GAMIFICATION_SOCIAL_PROFILE_ADD_ABOUTME);

        // Process only when an enable rule is found
        if (ruleDto != null) {
            try {
                aHistory = build(ruleDto,event.getProfile().getId(),event.getProfile().getIdentity().getId(),"/portal/intranet/profile/"+event.getProfile().getIdentity().getId());
                // Save actionHistory entry
                gamificationProcessor.execute(aHistory);
                // Gamification simple audit logger
                LOG.info("service=gamification operation=add-new-entry parameters=\"date:{},user_social_id:{},global_score:{},domain:{},action_title:{},action_score:{}\"", LocalDate.now(),aHistory.getUserSocialId(), aHistory.getGlobalScore(), ruleDto.getArea(), ruleDto.getTitle(), ruleDto.getScore());

            } catch (Exception e) {
                LOG.error("Error to process gamification for Rule {}", ruleDto.getTitle(), e);
            }
        }

    }


}

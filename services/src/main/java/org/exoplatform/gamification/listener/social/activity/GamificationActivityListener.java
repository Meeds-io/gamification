package org.exoplatform.gamification.listener.social.activity;

import org.exoplatform.commons.utils.CommonsUtils;
import org.exoplatform.gamification.entities.effective.GamificationContext;
import org.exoplatform.gamification.listener.GamificationListener;
import org.exoplatform.gamification.service.configuration.RuleService;
import org.exoplatform.gamification.service.dto.configuration.RuleDTO;
import org.exoplatform.gamification.service.effective.GamificationService;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.social.core.activity.ActivityLifeCycleEvent;
import org.exoplatform.social.core.activity.ActivityListenerPlugin;
import org.exoplatform.social.core.activity.model.ExoSocialActivity;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.identity.provider.SpaceIdentityProvider;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.space.model.Space;
import org.exoplatform.social.core.space.spi.SpaceService;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

public class GamificationActivityListener extends ActivityListenerPlugin implements GamificationListener {

    private static final Log LOG = ExoLogger.getLogger(GamificationActivityListener.class);

    protected RuleService ruleService;
    protected GamificationService gamificationService;
    protected IdentityManager identityManager;
    protected SpaceService spaceService;


    public GamificationActivityListener() {
        this.ruleService = CommonsUtils.getService(RuleService.class);
        this.gamificationService = CommonsUtils.getService(GamificationService.class);
        this.identityManager = CommonsUtils.getService(IdentityManager.class);
        this.spaceService = CommonsUtils.getService(SpaceService.class);

    }

    @Override
    public void saveActivity(ActivityLifeCycleEvent event) {

        //--- To hold GamificationRule
        RuleDTO ruleDto= null;

        //--- To hold GamificationContext
        GamificationContext gamificationContext = null;

        //--- Gamification Activity
        ExoSocialActivity activity = event.getSource();

       try {


           //--- Case1 : User add an activity on his own Stream
           //--- Case2 : User add an activity on Stream of one of his network
           //--- Case3: User add an activity on a Space Stream
           //--- CAse3: User add an activity on a Space Stream where you are manager

           //TODO I really look for a pattern to move this test to GamificationService side
           //--- Build GamificationContext
           gamificationContext = GamificationContext.instance().setActorUserName(getUserId(activity.getPosterId()));
           //---Case1
           if (isSpaceActivity(activity)) {

               //--- Get the space
               Space space = spaceService.getSpaceByPrettyName(activity.getStreamOwner());
               //--- Load the Rule (User add an activity within a space)
               ruleDto= ruleService.findRuleByTitle(GamificationListener.GAMIFICATION_SOCIAL_ACTIVITY_ADD_NEW_ON_SPACE);

               //--- Set actorScore
               gamificationContext.setActorScore(ruleDto.getScore());

               //--- Load the Rule (User add an activity within a space where I'm a manager)
               ruleDto= ruleService.findRuleByTitle(GamificationListener.GAMIFICATION_SOCIAL_ACTIVITY_ADD_NEW_ON_SPACE_STREAM_MANAGER);

               //--- Set actorScore
               gamificationContext.setTargetScore(ruleDto.getScore());

               //--- Get Space manager List
               //--- Set target actors
               gamificationContext.setTargetUserName(Arrays.asList(space.getManagers()));



           } else {

           }

           //--- Build GamificationContext
           gamificationContext = GamificationContext.instance()
                                                               .setSpaceActivity(isSpaceActivity(activity))

                                                               .setCreatedDate(Instant.now())
                                                               .setLastModifiedDate(Instant.now());


           //--- Run Gamification process
           gamificationService.process(gamificationContext);

       } catch (Exception E) {

       }



    }

    @Override
    public void updateActivity(ActivityLifeCycleEvent event) {


    }

    @Override
    public void saveComment(ActivityLifeCycleEvent event) {

    }

    @Override
    public void likeActivity(ActivityLifeCycleEvent event) {


    }

    @Override
    public void likeComment(ActivityLifeCycleEvent event) {


    }


    //TODO : use eXo stack
    public String getUserId(String identityId) {
        return identityManager.getIdentity(identityId, false).getRemoteId();
    }

    //TODO : use eXo stack
    public boolean isSpaceActivity(ExoSocialActivity activity) {
        Identity id = identityManager.getOrCreateIdentity(SpaceIdentityProvider.NAME, activity.getStreamOwner(), false);
        return (id != null);
    }


}

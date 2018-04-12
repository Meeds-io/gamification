package org.exoplatform.gamification.listener.social.activity;

import org.exoplatform.commons.utils.CommonsUtils;
import org.exoplatform.gamification.entities.effective.GamificationContext;
import org.exoplatform.gamification.entities.effective.SourceContextHolder;
import org.exoplatform.gamification.entities.effective.TargetContextholder;
import org.exoplatform.gamification.listener.GamificationListener;
import org.exoplatform.gamification.service.configuration.RuleService;
import org.exoplatform.gamification.service.dto.configuration.RuleDTO;
import org.exoplatform.gamification.service.effective.GamificationProcessor;
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

import java.util.Arrays;

public class GamificationActivityListener extends ActivityListenerPlugin implements GamificationListener {

    private static final Log LOG = ExoLogger.getLogger(GamificationActivityListener.class);

    protected RuleService ruleService;
    protected GamificationProcessor gamificationProcessor;
    protected IdentityManager identityManager;
    protected SpaceService spaceService;


    public GamificationActivityListener() {
        this.ruleService = CommonsUtils.getService(RuleService.class);
        this.gamificationProcessor = CommonsUtils.getService(GamificationProcessor.class);
        this.identityManager = CommonsUtils.getService(IdentityManager.class);
        this.spaceService = CommonsUtils.getService(SpaceService.class);

    }

    @Override
    public void saveActivity(ActivityLifeCycleEvent event) {

        //--- To hold GamificationRule
        RuleDTO ruleDto = null;

        //--- To hold GamificationContext
        GamificationContext gamificationContext = GamificationContext.instance();

        //--- To hold SourceContext
        SourceContextHolder sourceContextHolder = null;

        //--- To hold TargetContext
        TargetContextholder targetContextholder = null;

        //--- Gamification Activity
        ExoSocialActivity activity = event.getSource();

        try {


            //--- Case1 : User add an activity on his own Stream
            //--- Case2 : User add an activity on Stream of one of his network
            //--- Case3: User add an activity on a Space Stream
            //--- CAse3: User add an activity on a Space Stream where you are manager

            //TODO I really look for a pattern to move this test to GamificationProcessor side
            //---Case1
            if (isSpaceActivity(activity)) {

                /********************** Manage SourceContext *********************/
                //--- Get the space
                Space space = spaceService.getSpaceByPrettyName(activity.getStreamOwner());
                //--- Load the Rule (User add an activity within a space)
                ruleDto = ruleService.findRuleByTitle(GamificationListener.GAMIFICATION_SOCIAL_ACTIVITY_ADD_NEW_ON_SPACE);
                //--- Build SourceContextHolder
                sourceContextHolder = new SourceContextHolder();
                sourceContextHolder.setUsername(getUserId(activity.getPosterId()));
                sourceContextHolder.setScore(ruleDto.getScore());

                /********************** Manage TargetContext *********************/
                //--- Load the Rule (User add an activity within a space where I'm a manager)
                ruleDto = ruleService.findRuleByTitle(GamificationListener.GAMIFICATION_SOCIAL_ACTIVITY_ADD_NEW_ON_SPACE_STREAM_MANAGER);
                //--- Build TargetContextHolder
                targetContextholder = new TargetContextholder();
                //--- Get Space manager List
                targetContextholder.setUsernames(Arrays.asList(space.getManagers()));
                //--- Set score
                targetContextholder.setScore(ruleDto.getScore());


            } else {

                //--- User push an activity on his own Stream
                if (activity.getPosterId().equalsIgnoreCase(activity.getStreamId())) {

                    /********************** Manage SourceContext *********************/
                    //--- Build SourceContextHolder
                    sourceContextHolder = new SourceContextHolder();
                    //--- Load the specific rule
                    ruleDto = ruleService.findRuleByTitle(GamificationListener.GAMIFICATION_SOCIAL_ACTIVITY_ADD_NEW_ON_SPACE);
                    //--- Set score
                    sourceContextHolder.setScore(ruleDto.getScore());


                } else { //--- User push an activity on the stream of another user

                    //--- Build SourceContextHolder
                    sourceContextHolder = new SourceContextHolder();
                    //--- Load the Rule (User add an activity within a space)
                    ruleDto = ruleService.findRuleByTitle(GamificationListener.GAMIFICATION_SOCIAL_ACTIVITY_ADD_NEW_ON_SPACE);
                    //--- Set score
                    sourceContextHolder.setScore(ruleDto.getScore());

                    // Get the target username
                    targetContextholder = new TargetContextholder();
                    targetContextholder.setUsernames(Arrays.asList(getUserId(activity.getStreamId())));
                    //--- Load the Rule (User add an activity within a space)
                    ruleDto = ruleService.findRuleByTitle(GamificationListener.GAMIFICATION_SOCIAL_ACTIVITY_ADD_NEW_ON_SPACE);
                    targetContextholder.setScore(ruleDto.getScore());

                }

                sourceContextHolder.setUsername(getUserId(activity.getPosterId()));

            }

            //--- Attach Sub-contexts to the global gamification context
            gamificationContext.setTargetContextholder(targetContextholder).setSourceContextHolder(sourceContextHolder);

            //--- Processing Gamification
            gamificationProcessor.process(gamificationContext);

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

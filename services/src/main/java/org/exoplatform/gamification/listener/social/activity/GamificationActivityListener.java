package org.exoplatform.gamification.listener.social.activity;

import org.exoplatform.commons.utils.CommonsUtils;
import org.exoplatform.gamification.service.dto.configuration.RuleDTO;
import org.exoplatform.gamification.entities.effective.GamificationContext;
import org.exoplatform.gamification.listener.GamificationListener;
import org.exoplatform.gamification.service.configuration.RuleService;
import org.exoplatform.gamification.service.effective.GamificationService;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.social.core.activity.ActivityLifeCycleEvent;
import org.exoplatform.social.core.activity.ActivityListenerPlugin;
import org.exoplatform.social.core.activity.model.ExoSocialActivity;

import java.time.Instant;

public class GamificationActivityListener extends ActivityListenerPlugin implements GamificationListener {

    private static final Log LOG = ExoLogger.getLogger(GamificationActivityListener.class);

    protected RuleService ruleService;
    protected GamificationService gamificationService;

    public GamificationActivityListener() {
        this.ruleService = CommonsUtils.getService(RuleService.class);
        this.gamificationService = CommonsUtils.getService(GamificationService.class);
    }

    @Override
    public void saveActivity(ActivityLifeCycleEvent event) {

        ExoSocialActivity activity = event.getSource();

       try {
           //--- Load the Rule
           RuleDTO ruleDto= ruleService.findRuleByTitle(GamificationListener.GAMIFICATION_ACTIVITIES_OWNER_NEW_ACTIVITY);

           //--- Create GamificationContext instance
           GamificationContext gamificationContext = GamificationContext.instance().setUserName(activity.getPosterId())
                   .setScore(ruleDto.getScore())
                   .setCreatedDate(Instant.now())
                   .setLastModifiedDate(Instant.now());


           //--- Run Gamification process
           gamificationService.process(gamificationContext);

       } catch (Exception E) {

       }



    }

    @Override
    public void updateActivity(ActivityLifeCycleEvent event) {

        //--- Get Activity
        ExoSocialActivity activity = event.getSource();
        //--- Load the Rule
        RuleDTO ruleDto= ruleService.findRuleByTitle(GamificationListener.GAMIFICATION_ACTIVITIES_OWNER_NEW_ACTIVITY);
        //--- Create GamificationContext instance
        GamificationContext gamificationContext = GamificationContext.instance().setUserName(activity.getPosterId())
                .setScore(ruleDto.getScore())
                .setCreatedDate(Instant.now())
                .setLastModifiedDate(Instant.now());


        //--- Run Gamification process
        gamificationService.process(gamificationContext);
    }

    @Override
    public void saveComment(ActivityLifeCycleEvent event) {
        ExoSocialActivity activity = event.getSource();
        //--- Load the Rule
        RuleDTO ruleDto= ruleService.findRuleByTitle(GamificationListener.GAMIFICATION_ACTIVITIES_OWNER_NEW_ACTIVITY);
        //--- Create GamificationContext instance
        GamificationContext gamificationContext = GamificationContext.instance().setUserName(activity.getPosterId())
                .setScore(ruleDto.getScore())
                .setCreatedDate(Instant.now())
                .setLastModifiedDate(Instant.now());


        //--- Run Gamification process
        gamificationService.process(gamificationContext);

    }

    @Override
    public void likeActivity(ActivityLifeCycleEvent event) {
        ExoSocialActivity activity = event.getSource();
        //--- Load the Rule
        RuleDTO ruleDto= ruleService.findRuleByTitle(GamificationListener.GAMIFICATION_ACTIVITIES_OWNER_NEW_ACTIVITY);
        //--- Create GamificationContext instance
        GamificationContext gamificationContext = GamificationContext.instance().setUserName(activity.getPosterId())
                .setScore(ruleDto.getScore())
                .setCreatedDate(Instant.now())
                .setLastModifiedDate(Instant.now());


        //--- Run Gamification process
        gamificationService.process(gamificationContext);

    }

    @Override
    public void likeComment(ActivityLifeCycleEvent event) {
        ExoSocialActivity activity = event.getSource();
        //--- Load the Rule
        RuleDTO ruleDto= ruleService.findRuleByTitle(GamificationListener.GAMIFICATION_ACTIVITIES_OWNER_NEW_ACTIVITY);
        //--- Create GamificationContext instance
        GamificationContext gamificationContext = GamificationContext.instance().setUserName(activity.getPosterId())
                .setScore(ruleDto.getScore())
                .setCreatedDate(Instant.now())
                .setLastModifiedDate(Instant.now());


        //--- Run Gamification process
        gamificationService.process(gamificationContext);

    }


}

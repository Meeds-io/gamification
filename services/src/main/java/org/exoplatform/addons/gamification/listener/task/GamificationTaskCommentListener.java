package org.exoplatform.addons.gamification.listener.task;

import static org.exoplatform.addons.gamification.GamificationConstant.*;

import org.exoplatform.addons.gamification.entities.domain.effective.GamificationActionsHistory;

import org.exoplatform.addons.gamification.service.configuration.RuleService;
import org.exoplatform.addons.gamification.service.dto.configuration.RuleDTO;
import org.exoplatform.addons.gamification.service.effective.GamificationProcessor;
import org.exoplatform.addons.gamification.service.effective.GamificationService;
import org.exoplatform.services.listener.Event;
import org.exoplatform.services.listener.Listener;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.social.core.identity.provider.OrganizationIdentityProvider;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.task.domain.Comment;
import org.exoplatform.task.service.TaskService;
import org.exoplatform.task.util.TaskUtil;

import java.time.LocalDate;

public class GamificationTaskCommentListener extends Listener<TaskService, Comment> {
  private static final Log LOG = ExoLogger.getLogger(GamificationTaskCommentListener.class);

    protected RuleService ruleService;
    protected GamificationProcessor gamificationProcessor;
    protected IdentityManager identityManager;
    protected GamificationService gamificationService;

    public GamificationTaskCommentListener(RuleService ruleService,
                                           GamificationProcessor gamificationProcessor,
                                           IdentityManager identityManager,
                                           GamificationService gamificationService) {
        this.ruleService = ruleService;
        this.gamificationProcessor = gamificationProcessor;
        this.identityManager = identityManager;
        this.gamificationService = gamificationService;
    }

    @Override
    public void onEvent(Event<TaskService, Comment> event) throws Exception {
        String actorUsername = ConversationState.getCurrent().getIdentity().getUserId();

        GamificationActionsHistory aHistory = null;

        // Compute user id
        String actorId = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, actorUsername, false).getId();

        // Get associated rule
        RuleDTO ruleDto = ruleService.findEnableRuleByTitle(GAMIFICATION_TASK_ADDON_COMMENT_TASK);

        // Process only when an enable rule is found
        if (ruleDto != null) {
            try {
                aHistory = gamificationService.build(ruleDto, actorId,actorId, TaskUtil.buildTaskURL(event.getData().getTask()));

                // Save GamificationHistory
                if(aHistory!=null) {
                    gamificationProcessor.execute(aHistory);
                    // Gamification simple audit logger
                    LOG.info("service=gamification operation=add-new-entry parameters=\"date:{},user_social_id:{},global_score:{},domain:{},action_title:{},action_score:{}\"", LocalDate.now(), actorId, aHistory.getGlobalScore(), ruleDto.getArea(), ruleDto.getTitle(), ruleDto.getScore());
                }
            } catch (Exception e) {
                LOG.error("Error processing the following ActionHistory entry {}", aHistory, e);
            }
        }

    }
}
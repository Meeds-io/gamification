package org.exoplatform.addons.gamification.listener.task;

import org.exoplatform.addons.gamification.entities.domain.effective.GamificationActionsHistory;
import org.exoplatform.addons.gamification.listener.GamificationListener;
import org.exoplatform.addons.gamification.service.configuration.RuleService;
import org.exoplatform.addons.gamification.service.dto.configuration.RuleDTO;
import org.exoplatform.addons.gamification.service.effective.GamificationProcessor;
import org.exoplatform.addons.gamification.service.effective.GamificationService;
import org.exoplatform.services.listener.Event;
import org.exoplatform.services.listener.Listener;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.social.core.identity.provider.OrganizationIdentityProvider;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.task.domain.Task;
import org.exoplatform.task.service.TaskPayload;
import org.exoplatform.task.service.TaskService;

import java.time.LocalDate;
import java.util.Iterator;
import java.util.Set;

public class GamificationTaskUpdateListener extends Listener<TaskService, TaskPayload> implements GamificationListener {

    protected RuleService ruleService;
    protected GamificationProcessor gamificationProcessor;
    protected IdentityManager identityManager;
    protected GamificationService gamificationService;

    public GamificationTaskUpdateListener(RuleService ruleService, GamificationProcessor gamificationProcessor, IdentityManager identityManager, GamificationService gamificationService) {
        this.ruleService = ruleService;
        this.gamificationProcessor = gamificationProcessor;
        this.identityManager = identityManager;
        this.gamificationService = gamificationService;
    }


    @Override
    public void onEvent(Event<TaskService, TaskPayload> event) throws Exception {

        TaskPayload data = event.getData();

        Object oldTask = data.before();
        Object newTask = data.after();

        // New Task has been created
        if (oldTask == null && newTask != null) {
            createTask("created");
        }

        // Update Task
        if (oldTask != null && newTask != null) {
            updateTask((Task) oldTask, (Task) newTask);
        }

    }

    protected void createTask(String actionName) {

        String actorUsername = ConversationState.getCurrent().getIdentity().getUserId();
        GamificationActionsHistory aHistory = null;

        // Compute user id
        String actorId = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, actorUsername, false).getId();
        // Get associated rule
        RuleDTO ruleDto = ruleService.findEnableRuleByTitle(GAMIFICATION_TASK_ADDON_CREATE_TASK);

        // Process only when an enable rule is found
        if (ruleDto != null) {
            try {
                aHistory = build(ruleDto, actorId,actorId,"");

                // Save GamificationHistory
                gamificationProcessor.execute(aHistory);
                // Gamification simple audit logger
                LOG.info("service=gamification operation=add-new-entry parameters=\"date:{},user_social_id:{},global_score:{},domain:{},action_title:{},action_score:{}\"", LocalDate.now(), actorId, aHistory.getGlobalScore(), ruleDto.getArea(), ruleDto.getTitle(), ruleDto.getScore());
            } catch (Exception e) {
                LOG.error("Error processing the following ActionHistory entry {}", aHistory, e);
            }
        }

    }

    protected void updateTask(Task before, Task after) {
        RuleDTO ruleDto = null;
        String actorId = "";
        GamificationActionsHistory aHistory = null;
        // Task completed
        if (isDiff(before.isCompleted(), after.isCompleted())) {


            // Get task assigned property
            if (after.getAssignee() != null && after.getAssignee().length() != 0) {
                // Compute user id
                actorId = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, after.getAssignee(), false).getId();

                // Get associated rule
                ruleDto = ruleService.findEnableRuleByTitle(GAMIFICATION_TASK_ADDON_COMPLETED_TASK_ASSIGNED);

                // Process only when an enable rule is found
                if (ruleDto != null) {
                    try {
                        aHistory = build(ruleDto, actorId,actorId,"");

                        // Save GamificationHistory
                        gamificationProcessor.execute(aHistory);
                        // Gamification simple audit logger
                        LOG.info("service=gamification operation=add-new-entry parameters=\"date:{},user_social_id:{},global_score:{},domain:{},action_title:{},action_score:{}\"", LocalDate.now(), actorId, aHistory.getGlobalScore(), ruleDto.getArea(), ruleDto.getTitle(), ruleDto.getScore());
                    } catch (Exception e) {
                        LOG.error("Error processing the following ActionHistory entry {}", aHistory, e);
                    }
                }
            }

            // Manage coworker
            Set<String> cowrokers = after.getCoworker();
            // Get task assigned property
            if (cowrokers == null) return;

            // Get associated rule
            ruleDto = ruleService.findEnableRuleByTitle(GAMIFICATION_TASK_ADDON_COMPLETED_TASK_COWORKER);

            Iterator<String> coworker = cowrokers.iterator();
            while (coworker.hasNext()) {
                // Compute user id
                actorId = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, coworker.next(), false).getId();

                // Process only when an enable rule is found
                if (ruleDto != null) {
                    try {
                        aHistory = build(ruleDto, actorId,actorId,"");

                        // Save GamificationHistory
                        gamificationProcessor.execute(aHistory);
                        // Gamification simple audit logger
                        LOG.info("service=gamification operation=add-new-entry parameters=\"date:{},user_social_id:{},global_score:{},domain:{},action_title:{},action_score:{}\"", LocalDate.now(), actorId, aHistory.getGlobalScore(), ruleDto.getArea(), ruleDto.getTitle(), ruleDto.getScore());
                    } catch (Exception e) {
                        LOG.error("Error processing the following ActionHistory entry {}", aHistory, e);
                    }
                }
            }
        } else { // Update a task regardless le action made by a user

            // Get connected user
            String actorUsername = ConversationState.getCurrent().getIdentity().getUserId();

            // Compute user id
            actorId = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, actorUsername, false).getId();

            // Get associated rule
            ruleDto = ruleService.findEnableRuleByTitle(GAMIFICATION_TASK_ADDON_UPDATE_TASK);

            // Process only when an enable rule is found
            if (ruleDto != null) {
                try {
                    aHistory = build(ruleDto, actorId,actorId,"");
                    // Gamification simple audit logger
                    LOG.info("service=gamification operation=add-new-entry parameters=\"date:{},user_social_id:{},global_score:{},domain:{},action_title:{},action_score:{}\"",
                            LocalDate.now(),
                            actorId,
                            aHistory.getGlobalScore(),
                            ruleDto.getArea(),
                            ruleDto.getTitle(),
                            ruleDto.getScore());
                    // Save GamificationHistory
                    gamificationProcessor.execute(aHistory);
                } catch (Exception e) {
                    LOG.error("Error processing the following ActionHistory entry {}", aHistory, e);
                }
            }
        }
    }

    /**
     * Check whether a task has been updated
     *
     * @param before : Task data before modification
     * @param after  : Task data after modification
     * @return a boolean if task has been changed false else
     */
    protected boolean isDiff(Object before, Object after) {
        if (before == after) {
            return false;
        }
        if (before != null) {
            return !before.equals(after);
        } else {
            return !after.equals(before);
        }
    }
}

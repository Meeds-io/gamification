package org.exoplatform.addons.gamification.listener.wiki;

import org.exoplatform.addons.gamification.entities.domain.effective.GamificationContextEntity;
import org.exoplatform.addons.gamification.entities.domain.effective.GamificationContextItemEntity;
import org.exoplatform.addons.gamification.listener.GamificationListener;
import org.exoplatform.addons.gamification.service.configuration.RuleService;
import org.exoplatform.addons.gamification.service.dto.configuration.RuleDTO;
import org.exoplatform.addons.gamification.service.dto.effective.GamificationContextHolder;
import org.exoplatform.addons.gamification.service.effective.GamificationProcessor;
import org.exoplatform.addons.gamification.service.effective.GamificationService;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.social.core.identity.provider.OrganizationIdentityProvider;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.wiki.WikiException;
import org.exoplatform.wiki.mow.api.Page;
import org.exoplatform.wiki.service.PageUpdateType;
import org.exoplatform.wiki.service.listener.PageWikiListener;
import org.exoplatform.wiki.utils.WikiConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class GamificationWikiListener extends PageWikiListener implements GamificationListener {

    private static final Log LOG = ExoLogger.getLogger(GamificationWikiListener.class);


    protected RuleService ruleService;
    protected GamificationProcessor gamificationProcessor;
    protected IdentityManager identityManager;
    protected GamificationService gamificationService;

    public GamificationWikiListener(RuleService ruleService, GamificationProcessor gamificationProcessor, IdentityManager identityManager, GamificationService gamificationService) {

        this.ruleService = ruleService;
        this.gamificationProcessor = gamificationProcessor;
        this.identityManager = identityManager;
        this.gamificationService = gamificationService;
    }

    @Override
    public void postAddPage(String wikiType, String wikiOwner, String pageId, Page page) throws WikiException {
        if (WikiConstants.WIKI_HOME_NAME.equals(pageId)) {
            // catch the case of the Wiki Home added as it's created by the system, not by users.
            return;
        }

        List<GamificationContextHolder> gamificationContextEntityList = null;

        // Get the space's creator username
        String actorUsername = ConversationState.getCurrent().getIdentity().getUserId();

        // Compute user id
        String actorId = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, actorUsername, false).getId();

        // Get associated rule
        RuleDTO ruleDto = ruleService.findEnableRuleByTitle(GAMIFICATION_WIKI_ADD_PAGE);

        // Process only when an enable rule is found
        if (ruleDto != null) {
            try {
                gamificationContextEntityList = gamify(ruleDto, actorId);
            } catch (Exception e) {
                LOG.error("Error to process gamification for Rule {}", ruleDto.getTitle(), e);
            }
        }
        // Save Gamification Context
        gamificationProcessor.process(gamificationContextEntityList);


    }

    @Override
    public void postDeletePage(String wikiType, String wikiOwner, String pageId, Page page) throws WikiException {

    }

    @Override
    public void postUpdatePage(String wikiType, String wikiOwner, String pageId, Page page, PageUpdateType wikiUpdateType) throws WikiException {
        // Generate an activity only in the following cases
        if(page != null && wikiUpdateType != null
                && (wikiUpdateType.equals(PageUpdateType.ADD_PAGE)
                || wikiUpdateType.equals(PageUpdateType.EDIT_PAGE_CONTENT)
                || wikiUpdateType.equals(PageUpdateType.EDIT_PAGE_CONTENT_AND_TITLE))) {
            //saveActivity(wikiType, wikiOwner, pageId, page, wikiUpdateType);
            String username = ConversationState.getCurrent().getIdentity().getUserId();

            List<GamificationContextHolder> gamificationContextEntityList = null;

            // Get the space's creator username
            String actorUsername = ConversationState.getCurrent().getIdentity().getUserId();

            // Compute user id
            String actorId = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, actorUsername, false).getId();

            // Get associated rule
            RuleDTO ruleDto = ruleService.findEnableRuleByTitle(GAMIFICATION_WIKI_UPDATE_PAGE);

            // Process only when an enable rule is found
            if (ruleDto != null) {
                try {
                    gamificationContextEntityList = gamify(ruleDto, actorId);
                } catch (Exception e) {
                    LOG.error("Error to process gamification for Rule {}", ruleDto.getTitle(), e);
                }
            }

            // Save Gamification Context
            gamificationProcessor.process(gamificationContextEntityList);

        }
    }

    /**
     * Compute GamificationContext for each social action made by enduser
     *
     * @param ruleDto
     * @param actor
     * @return GamificationContextHolder : a list of GamificationContextHolder to be persist in DB
     * @throws Exception
     */
    @Override
    public List<GamificationContextHolder> gamify(RuleDTO ruleDto, String actor) throws Exception {

        List<GamificationContextHolder> gamificationContextEntityList = new ArrayList<GamificationContextHolder>();

        // Build GamificationContextHolder
        GamificationContextHolder contextHolder = null;

        // Build a gamificationContext entry
        GamificationContextEntity gamificationContextEntity = null;

        // Process only when an enable rule is found
        if (ruleDto != null) {
            //Find if a gamificationContext exists for the current user
            gamificationContextEntity = gamificationService.findGamificationContextByUsername(actor);

            // Start building GamificationContextHolder
            contextHolder = new GamificationContextHolder();

            if (gamificationContextEntity != null) {

                // Load gamification  items
                final String title = ruleDto.getTitle();
                Set<GamificationContextItemEntity> gamificationContextItemEntitySet = gamificationContextEntity.getGamificationItems()
                        .stream()
                        .filter(item -> item.getOpType().equalsIgnoreCase(title))
                        .collect(Collectors.toSet());

                if (gamificationContextItemEntitySet != null && !gamificationContextItemEntitySet.isEmpty()) {
                    gamificationContextItemEntitySet.forEach(item -> {
                        item.setOccurrence(item.getOccurrence() + 1);

                        // Compute current score
                        item.setScore(item.getScore()+ruleDto.getScore());

                    });

                    // Update user's global score
                    gamificationContextEntity.setScore(gamificationContextEntity.getScore() + ruleDto.getScore());

                } else { // Create a new item entry
                    GamificationContextItemEntity gamificationContextItemEntity = new GamificationContextItemEntity();
                    gamificationContextItemEntity.setOpType(title);
                    gamificationContextItemEntity.setZone(ruleDto.getArea());

                    gamificationContextItemEntity.setOccurrence(1);

                    // Compute current score
                    gamificationContextItemEntity.setScore(ruleDto.getScore());

                    // Compute Global Score
                    gamificationContextEntity.setScore(gamificationContextEntity.getScore()+ruleDto.getScore());
                    // Link GamificationItem : parent/child
                    gamificationContextEntity.addGamificationItem(gamificationContextItemEntity);


                }

            } else {

                // Create new Gamification for current user
                gamificationContextEntity = new GamificationContextEntity();
                //gamificationContextEntity.setId(null);
                gamificationContextEntity.setUsername(actor);
                gamificationContextEntity.setScore(ruleDto.getScore());

                // Create specific gamification item for ingoing action
                GamificationContextItemEntity gamificationContextItemEntity = new GamificationContextItemEntity();

                gamificationContextItemEntity.setOccurrence(1);

                gamificationContextItemEntity.setOpType(ruleDto.getTitle());

                gamificationContextItemEntity.setZone(ruleDto.getArea());

                // set score
                gamificationContextItemEntity.setScore(ruleDto.getScore());

                // Compute current score
                gamificationContextEntity.setScore(ruleDto.getScore());

                // Link GamificationItem to its parent
                gamificationContextItemEntity.setGamificationUserEntity(gamificationContextEntity);

                // Add GamificationItem as child to GamificationContext
                gamificationContextEntity.getGamificationItems().add(gamificationContextItemEntity);

                // Udapte action (create enw enity)
                contextHolder.setNew(true);

            }
            contextHolder.setGamificationContextEntity(gamificationContextEntity);

            // Add the GamificationContext entry to list
            gamificationContextEntityList.add(contextHolder);
        }

        return gamificationContextEntityList;

    }
}

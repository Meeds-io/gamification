package org.exoplatform.addons.gamification.listener.wiki;

import org.exoplatform.addons.gamification.entities.domain.effective.GamificationActionsHistory;
import org.exoplatform.addons.gamification.listener.GamificationListener;
import org.exoplatform.addons.gamification.service.configuration.RuleService;
import org.exoplatform.addons.gamification.service.dto.configuration.RuleDTO;
import org.exoplatform.addons.gamification.service.effective.GamificationProcessor;
import org.exoplatform.addons.gamification.service.effective.GamificationService;
import org.exoplatform.services.listener.Asynchronous;
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

@Asynchronous
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

        GamificationActionsHistory aHistory = null;

        // Get the space's creator username
        String actorUsername = ConversationState.getCurrent().getIdentity().getUserId();

        // Compute user id
        String actorId = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, actorUsername, false).getId();

        // Get associated rule
        RuleDTO ruleDto = ruleService.findEnableRuleByTitle(GAMIFICATION_WIKI_ADD_PAGE);

        // Process only when an enable rule is found
        if (ruleDto != null) {
            try {
                aHistory = build(ruleDto, actorId);

                // Save GamificationHistory
                gamificationProcessor.execute(aHistory);
            } catch (Exception e) {
                LOG.error("Error processing the following ActionHistory entry {}", aHistory, e);
            }
        }
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

            GamificationActionsHistory aHistory = null;

            // Get the space's creator username
            String actorUsername = ConversationState.getCurrent().getIdentity().getUserId();

            // Compute user id
            String actorId = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, actorUsername, false).getId();

            // Get associated rule
            RuleDTO ruleDto = ruleService.findEnableRuleByTitle(GAMIFICATION_WIKI_UPDATE_PAGE);

            // Process only when an enable rule is found
            if (ruleDto != null) {
                try {
                    aHistory = build(ruleDto, actorId);

                    // Save GamificationHistory
                    gamificationProcessor.execute(aHistory);
                } catch (Exception e) {
                    LOG.error("Error processing the following ActionHistory entry {}", aHistory, e);
                }
            }
        }
    }
}

package org.exoplatform.addons.gamification.listener;

import org.exoplatform.addons.gamification.GamificationConstant;
import org.exoplatform.addons.gamification.GamificationUtils;
import org.exoplatform.addons.gamification.entities.domain.effective.GamificationActionsHistory;
import org.exoplatform.addons.gamification.service.dto.configuration.RuleDTO;
import org.exoplatform.addons.gamification.service.effective.GamificationService;
import org.exoplatform.commons.utils.CommonsUtils;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.social.core.manager.IdentityManager;

import java.util.Date;

public interface GamificationListener extends GamificationConstant {

    Log LOG = ExoLogger.getLogger(GamificationListener.class);

    default public GamificationActionsHistory build(RuleDTO ruleDto, String actor) throws Exception {

        GamificationActionsHistory oldHistory = null;

        GamificationActionsHistory aHistory = null;

        // check if the current user is not a bot
      //  if (isBlackListed(CommonsUtils.getService(IdentityManager.class).getIdentity(actor, false).getId())) {
        if (GamificationUtils.isBlackListed(CommonsUtils.getService(IdentityManager.class).getIdentity(actor, false).getRemoteId())) {
            return null;
        }

        // Buil only an entry when a rule enable and exist
        if (ruleDto != null) {

            oldHistory = CommonsUtils.getService(GamificationService.class).findLatestActionHistoryBySocialId(actor);

            aHistory = new GamificationActionsHistory();

            aHistory.setGlobalScore(ruleDto.getScore());

            if (oldHistory != null) {
                aHistory.setGlobalScore(oldHistory.getGlobalScore() + ruleDto.getScore());
            }

            aHistory.setDate(new Date());
            aHistory.setUserSocialId(actor);
            aHistory.setActionTitle(ruleDto.getTitle());
            aHistory.setDomain(ruleDto.getArea());
            aHistory.setActionScore(ruleDto.getScore());

            // Set update metadata
            aHistory.setLastModifiedDate(new Date());
            aHistory.setLastModifiedBy("Gamification Inner Process");
            // Set create metadata
            aHistory.setCreatedBy("Gamification Inner Process");

        }

        return aHistory;
    }


}

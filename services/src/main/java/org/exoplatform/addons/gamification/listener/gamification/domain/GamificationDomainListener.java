package org.exoplatform.addons.gamification.listener.gamification.domain;

import java.util.List;

import org.exoplatform.addons.gamification.entities.domain.configuration.DomainEntity;
import org.exoplatform.addons.gamification.service.configuration.BadgeService;
import org.exoplatform.addons.gamification.service.configuration.RuleService;
import org.exoplatform.addons.gamification.service.dto.configuration.BadgeDTO;
import org.exoplatform.addons.gamification.service.dto.configuration.RuleDTO;
import org.exoplatform.services.listener.Event;
import org.exoplatform.services.listener.Listener;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;


public class GamificationDomainListener extends Listener<DomainEntity,String>   {

        private static final Log LOG = ExoLogger.getLogger(GamificationDomainListener.class);
        protected RuleService ruleService;
        protected BadgeService badgeService;

        public GamificationDomainListener(RuleService ruleService, BadgeService badgeService) {
            this.ruleService = ruleService;
            this.badgeService = badgeService;
        }


    @Override
    public void onEvent(Event<DomainEntity, String> event) throws Exception {
            LOG.info("Update Rules related to the edited domain");
        DomainEntity domain=(DomainEntity) event.getSource();
        String action = (String) event.getData();
        List<RuleDTO> rules =ruleService.getAllRulesByDomain(domain.getTitle());
        List<BadgeDTO> badges =badgeService.findBadgesByDomain(domain.getTitle());
        if(action.equals("delete")){
            for (RuleDTO rule : rules){
                rule.setDomainDTO(null);
                rule.setArea("");
                rule.setEnabled(false);
                ruleService.updateRule(rule);
            }
            for (BadgeDTO badge : badges){
                badge.setDomainDTO(null);
                badge.setDomain("");
                badge.setEnabled(false);
                badgeService.updateBadge(badge);
            }
        }
        if(action.equals("disable")){
            for (RuleDTO rule : rules){
                rule.setEnabled(false);
                ruleService.updateRule(rule);
            }
            for (BadgeDTO badge : badges){
                badge.setEnabled(false);
                badgeService.updateBadge(badge);
            }
        }
        if(action.equals("enable")){
            for (RuleDTO rule : rules){
                rule.setEnabled(true);
                ruleService.updateRule(rule);
            }
            for (BadgeDTO badge : badges){
                badge.setEnabled(true);
                badgeService.updateBadge(badge);
            }
        }

    }
}
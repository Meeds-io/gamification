package org.exoplatform.gamification.service.configuration;

import org.exoplatform.commons.utils.CommonsUtils;
import org.exoplatform.gamification.entities.domain.configuration.RuleEntity;
import org.exoplatform.gamification.service.dto.configuration.RuleDTO;
import org.exoplatform.gamification.service.mapper.RuleMapper;
import org.exoplatform.gamification.storage.dao.RuleDAO;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

public class RuleService {

    private static final Log LOG = ExoLogger.getLogger(RuleService.class);

    protected final RuleDAO ruleDAO;
    protected final RuleMapper ruleMapper;

    public RuleService() {
        this.ruleDAO = CommonsUtils.getService(RuleDAO.class);
        this.ruleMapper = CommonsUtils.getService(RuleMapper.class);
    }

    /**
     *
     * @param ruleTitle
     * @return
     * @throws Exception
     */
    public RuleDTO findRuleByTitle (String ruleTitle) {

        try {
            //--- Get Entity from DB
            RuleEntity entity = ruleDAO.findRuleByTitle(ruleTitle);
            //--- Convert Entity to DTO
            if (entity != null ) {
                return ruleMapper.ruleToRuleDTO(entity);
            }

        } catch (Exception e) {
            LOG.error("Error to find Rule entity with title : {}",ruleTitle,e.getMessage());
        }
        return null;

    }
}

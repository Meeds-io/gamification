package org.exoplatform.addons.gamification.service.configuration;

import org.exoplatform.addons.gamification.entities.domain.configuration.RuleEntity;
import org.exoplatform.commons.api.persistence.ExoTransactional;
import org.exoplatform.commons.utils.CommonsUtils;
import org.exoplatform.addons.gamification.service.dto.configuration.RuleDTO;
import org.exoplatform.addons.gamification.service.mapper.RuleMapper;
import org.exoplatform.addons.gamification.storage.dao.RuleDAO;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import java.util.List;

public class RuleService {

    private static final Log LOG = ExoLogger.getLogger(RuleService.class);

    protected final RuleDAO ruleDAO;
    protected final RuleMapper ruleMapper;

    public RuleService() {
        this.ruleDAO = CommonsUtils.getService(RuleDAO.class);
        this.ruleMapper = CommonsUtils.getService(RuleMapper.class);
    }

    /**
     * Find a RuleEntity by title
     * @param ruleTitle : rule's title param
     * @return an instance of RuleDTO
     */
    public RuleDTO findEnableRuleByTitle (String ruleTitle) {

        try {
            //--- Get Entity from DB
            RuleEntity entity = ruleDAO.findEnableRuleByTitle(ruleTitle);
            //--- Convert Entity to DTO
            if (entity != null ) {
                return ruleMapper.ruleToRuleDTO(entity);
            }

        } catch (Exception e) {
            LOG.error("Error to find Rule entity with title : {}",ruleTitle,e.getMessage());
        }
        return null;

    }

    public List<RuleDTO> getAllRules() {
        try {
            //--- load all Rules
            List<RuleEntity> rules =  ruleDAO.getAllRules();
            if (rules != null) {
                return ruleMapper.rulesToRoleDTOs(rules);
            }

        } catch (Exception e) {
            LOG.error("Error to find Rules",e.getMessage());
        }
        return null;
    }

    @ExoTransactional
    public void deleteRule (String ruleTitle) {

        try {

            ruleDAO.deleteRuleByTitle(ruleTitle);

        } catch (Exception e) {
            LOG.error("Error to delete rule with title {}", ruleTitle, e);
        }
    }

    /**
     * Add Rule to DB
     * @param ruleDTO : an object of type RuleDTO
     * @return RuleDTO object
     */
    @ExoTransactional
    public RuleDTO addRule (RuleDTO ruleDTO) {

        RuleEntity ruleEntity = null;

        try {

            ruleEntity = ruleDAO.create(ruleMapper.ruleDTOToRule(ruleDTO));

        } catch (Exception e) {
            LOG.error("Error to delete rule with title {}", ruleDTO.getTitle() , e);
        }

        return ruleMapper.ruleToRuleDTO(ruleEntity);
    }

    /**
     * Update Rule to DB
     * @param ruleDTO : an object of type RuleDTO
     * @return RuleDTO object
     */
    @ExoTransactional
    public RuleDTO updateRule (RuleDTO ruleDTO) {

        RuleEntity ruleEntity = null;

        try {

            ruleEntity = ruleDAO.update(ruleMapper.ruleDTOToRule(ruleDTO));

        } catch (Exception e) {
            LOG.error("Error to update rule with title {}", ruleDTO.getTitle() , e);
        }

        return ruleMapper.ruleToRuleDTO(ruleEntity);
    }
}

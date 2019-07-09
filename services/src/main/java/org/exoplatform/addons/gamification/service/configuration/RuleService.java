package org.exoplatform.addons.gamification.service.configuration;

import org.exoplatform.addons.gamification.entities.domain.configuration.RuleEntity;
import org.exoplatform.addons.gamification.service.dto.configuration.RuleDTO;
import org.exoplatform.addons.gamification.service.mapper.RuleMapper;
import org.exoplatform.addons.gamification.storage.dao.RuleDAO;
import org.exoplatform.commons.api.persistence.ExoTransactional;
import org.exoplatform.commons.utils.CommonsUtils;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
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
     * Find enable RuleEntity by title
     * @param ruleTitle : rule's title param
     * @return an instance of RuleDTO
     */
    @ExoTransactional
    public RuleDTO findEnableRuleByTitle (String ruleTitle) throws EntityNotFoundException {

        try {
            //--- Get Entity from DB
            RuleEntity entity = ruleDAO.findEnableRuleByTitle(ruleTitle);
            //--- Convert Entity to DTO
            if (entity != null ) {
                return ruleMapper.ruleToRuleDTO(entity);
            }else{
                throw (new EntityNotFoundException());
            }

        } catch (Exception e) {
            LOG.error("Error to find enable Rule entity with title : {}",ruleTitle,e);
            throw e;
        }
    }

    /**
     * Find a RuleEntity by title
     * @param ruleTitle : rule's title param
     * @return an instance of RuleDTO
     */
    @ExoTransactional
    public RuleDTO findRuleByTitle (String ruleTitle) {

        try {
            //--- Get Entity from DB
            RuleEntity entity = ruleDAO.findRuleByTitle(ruleTitle);
            //--- Convert Entity to DTO
            if (entity != null ) {
                return ruleMapper.ruleToRuleDTO(entity);
            }else{
                return null;
            }

        } catch (Exception e) {
            LOG.error("Error to find Rule entity with title : {}",ruleTitle,e);
            throw(e);
        }
    }



    /**
     * Get all Rules from DB
     * @return RuleDTO list
     */

    public List<RuleDTO> getAllRules() {
        try {
            //--- load all Rules
            List<RuleEntity> rules =  ruleDAO.getAllRules();
            if (rules != null) {
                return ruleMapper.rulesToRoleDTOs(rules);
            }else{
                return null;
            }

        } catch (Exception e) {
            LOG.error("Error to find Rules",e);
            throw(e);
        }
    }
    /**
     * Get all Rules by Domain from DB
     * * @param domain : rule's domain param
     * @return RuleDTO list
     */
    @ExoTransactional
    public List<RuleDTO> getAllRulesByDomain(String domain){
        try {
            //--- load all Rules by Domain
            List<RuleEntity> rules =  ruleDAO.getAllRulesByDomain(domain);
            if (rules != null) {
                return ruleMapper.rulesToRoleDTOs(rules);
            }else{
                return null;
            }
        } catch (Exception e) {
            LOG.error("Error to find Rules",e);
            throw(e);
        }
    }

    /**
     * Get all Rules by with null DomainDTO from DB
     * @return RuleDTO list
     */
    @ExoTransactional
    public List<RuleDTO> getAllRulesWithNullDomain(){
        try {
            List<RuleEntity> rules =  ruleDAO.getAllRulesWithNullDomain();
            if (rules != null) {
                return ruleMapper.rulesToRoleDTOs(rules);
            }else{
                return null;
            }

        } catch (Exception e) {
            LOG.error("Error to find Rules",e);
            throw(e);
        }
    }


    /**
     * Get all Domains from Rules from DB
     * @return String list
     */
    @ExoTransactional
    public List<String> getDomainListFromRules() {
        return ruleDAO.getDomainList();
    }

    @ExoTransactional
    public void deleteRule (String ruleTitle)  throws Exception{

        try {
            ruleDAO.deleteRuleByTitle(ruleTitle);
        } catch (Exception e) {
            LOG.error("Error to delete rule with title {}", ruleTitle, e);
            throw(e);
        }
    }

    /**
     * Add Rule to DB
     * @param ruleDTO : an object of type RuleDTO
     * @return RuleDTO object
     */
    @ExoTransactional
    public RuleDTO addRule (RuleDTO ruleDTO)  throws Exception{

        RuleEntity ruleEntity = null;

        try {

            ruleEntity = ruleDAO.create(ruleMapper.ruleDTOToRule(ruleDTO));

        } catch (Exception e) {
            LOG.error("Error to delete rule with title {}", ruleDTO.getTitle() , e);
            throw(e);
        }

        return ruleMapper.ruleToRuleDTO(ruleEntity);
    }

    /**
     * Update Rule to DB
     * @param ruleDTO : an object of type RuleDTO
     * @return RuleDTO object
     */
    @ExoTransactional
    public RuleDTO updateRule (RuleDTO ruleDTO) throws Exception{

        RuleEntity ruleEntity = null;

        try {

            ruleEntity = ruleDAO.update(ruleMapper.ruleDTOToRule(ruleDTO));

        } catch (Exception e) {
            LOG.error("Error to update rule with title {}", ruleDTO.getTitle() , e);
            throw(e);
        }

        return ruleMapper.ruleToRuleDTO(ruleEntity);
    }
}

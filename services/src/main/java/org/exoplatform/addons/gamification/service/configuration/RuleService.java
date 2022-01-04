/*
 * This file is part of the Meeds project (https://meeds.io/).
 * Copyright (C) 2020 Meeds Association
 * contact@meeds.io
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.exoplatform.addons.gamification.service.configuration;

import org.exoplatform.addons.gamification.entities.domain.configuration.RuleEntity;
import org.exoplatform.addons.gamification.service.dto.configuration.RuleDTO;
import org.exoplatform.addons.gamification.service.mapper.RuleMapper;
import org.exoplatform.addons.gamification.storage.dao.RuleDAO;
import org.exoplatform.commons.api.persistence.ExoTransactional;
import org.exoplatform.commons.utils.CommonsUtils;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import static org.exoplatform.addons.gamification.GamificationConstant.*;
import javax.persistence.EntityExistsException;
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
    public RuleDTO findEnableRuleByTitle (String ruleTitle) {

        try {
            //--- Get Entity from DB
            RuleEntity entity = ruleDAO.findEnableRuleByTitle(ruleTitle);
            //--- Convert Entity to DTO
            if (entity != null ) {
                return ruleMapper.ruleToRuleDTO(entity);
            }else{
                return null;
            }

        } catch (Exception e) {
            LOG.error("Error to find enable Rule entity with title : {}",ruleTitle,e);
            throw e;
        }
    }

    /**
     * Get RuleEntity by id
     * @param id : rule's id param
     * @return an instance of RuleDTO
     */
    @ExoTransactional
    public RuleDTO findRuleById (Long id) {

        try {
            //--- Get Entity from DB
            RuleEntity entity = ruleDAO.find(id);
            //--- Convert Entity to DTO
            if (entity != null ) {
                return ruleMapper.ruleToRuleDTO(entity);
            }else{
                return null;
            }

        } catch (Exception e) {
            LOG.error("Error to find Rule entity with id : {}",id,e);
            throw e;
        }
    }

    /**
     * Find enable RuleEntity by title
     * @param ruleTitle : rule's title param
     * @return an instance of RuleDTO
     */
    @ExoTransactional
    public List<RuleDTO> findEnabledRulesByEvent (String ruleTitle) {

        try {
            //--- Get Entity from DB
            List<RuleEntity> entities = ruleDAO.findEnabledRulesByEvent(ruleTitle);
            //--- Convert Entity to DTO
            if (entities != null ) {
                return ruleMapper.rulesToRuleDTOs(entities);
            }else{
                return null;
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
     * Find a RuleEntity by title
     * @param ruleTitle : rule's title param
     * @param domain : rule's domain param
     * @return an instance of RuleDTO
     */
    @ExoTransactional
    public RuleDTO findRuleByEventAndDomain (String ruleTitle, String domain) {

        try {
            //--- Get Entity from DB
            RuleEntity entity = ruleDAO.findRuleByEventAndDomain(ruleTitle,domain);
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
    @ExoTransactional
    public List<RuleDTO> getAllRules() {
        try {
            //--- load all Rules
            List<RuleEntity> rules =  ruleDAO.getAllRules();
            if (rules != null) {
                return ruleMapper.rulesToRuleDTOs(rules);
            }else{
                return null;
            }

        } catch (Exception e) {
            LOG.error("Error to find Rules",e);
            throw(e);
        }
    }

    /**
     * Get all Rules from DB
     * @return RuleDTO list
     */
    @ExoTransactional
    public List<RuleDTO> getActiveRules() {
        try {
            //--- load all Rules
            List<RuleEntity> rules =  ruleDAO.getActiveRules();
            if (rules != null) {
                return ruleMapper.rulesToRuleDTOs(rules);
            }else{
                return null;
            }

        } catch (Exception e) {
            LOG.error("Error to find active rules",e);
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
                return ruleMapper.rulesToRuleDTOs(rules);
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
                return ruleMapper.rulesToRuleDTOs(rules);
            }else{
                return null;
            }

        } catch (Exception e) {
            LOG.error("Error to find Rules",e);
            throw(e);
        }
    }
    /**
     * Get all Events from rules
     * @return RuleDTO list
     */
    @ExoTransactional
    public List<String> getAllEvents(){
        try {
            return ruleDAO.getAllEvents();
        } catch (Exception e) {
            LOG.error("Error to find Events",e);
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
    public void deleteRule (Long id)  throws Exception{

        try {
            RuleEntity ruleEntity = ruleDAO.find(id);
            ruleEntity.setDeleted(true);
            ruleDAO.update(ruleEntity);
        } catch (Exception e) {
            LOG.error("Error to delete rule with id {}", id, e);
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
            ruleEntity = ruleDAO.findRuleByEventAndDomain(ruleDTO.getEvent(), ruleDTO.getArea());
            if(ruleEntity==null){
                ruleEntity = ruleDAO.create(ruleMapper.ruleDTOToRule(ruleDTO));
            }else if(ruleEntity.isDeleted()){
                Long id = ruleEntity.getId();
                ruleEntity = ruleMapper.ruleDTOToRule(ruleDTO);
                ruleEntity.setId(id);
                ruleEntity = ruleDAO.update(ruleEntity);
            }else{
                throw(new EntityExistsException("Rule with same event and domain already exist"));
            }
        } catch (Exception e) {
            LOG.error("Error to add rule with title {}", ruleDTO.getTitle() , e);
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
            ruleEntity = ruleDAO.findRuleByEventAndDomain(ruleDTO.getEvent(), ruleDTO.getArea());
            if(  ruleEntity!=null && ruleEntity.getId()!=ruleDTO.getId()){
                throw(new EntityExistsException("Rule with same event and domain already exist"));
            }
            if(!ruleDTO.getTitle().startsWith(GAMIFICATION_DEFAULT_DATA_PREFIX)){
                ruleDTO.setTitle(ruleDTO.getEvent()+"_"+ruleDTO.getArea());
            }
            ruleEntity = ruleDAO.update(ruleMapper.ruleDTOToRule(ruleDTO)); 
        } catch (Exception e) {
            LOG.error("Error to update rule with title {}", ruleDTO.getTitle() , e);
            throw(e);
        }
        return ruleMapper.ruleToRuleDTO(ruleEntity);
    }
}

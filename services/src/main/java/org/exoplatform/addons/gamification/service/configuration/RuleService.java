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

import org.exoplatform.addons.gamification.service.dto.configuration.RuleDTO;
import java.util.List;

public interface RuleService {

    /**
     * Find enable RuleEntity by title
     * @param ruleTitle : rule's title param
     * @return an instance of RuleDTO
     */
     RuleDTO findEnableRuleByTitle (String ruleTitle) throws IllegalArgumentException;

    /**
     * Get RuleEntity by id
     * @param id : rule's id param
     * @return an instance of RuleDTO
     */
    RuleDTO findRuleById (Long id) throws IllegalArgumentException;

    /**
     * Find enable RuleEntity by title
     * @param ruleTitle : rule's title param
     * @return an instance of RuleDTO
     */
     List<RuleDTO> findEnabledRulesByEvent (String ruleTitle) throws IllegalArgumentException;

    /**
     * Find a RuleEntity by title
     * @param ruleTitle : rule's title param
     * @return an instance of RuleDTO
     */
     RuleDTO findRuleByTitle (String ruleTitle) throws IllegalArgumentException;


    /**
     * Find a RuleEntity by title
     * @param ruleTitle : rule's title param
     * @param domain : rule's domain param
     * @return an instance of RuleDTO
     */
    RuleDTO findRuleByEventAndDomain (String ruleTitle, String domain) throws IllegalArgumentException;

    /**
     * Get all Rules from DB
     * @return RuleDTO list
     * @deprecated use methods with pagination instead to avoid performance and memory issues
     * @since Meeds 1.4.0
     */
    @Deprecated(since = "Meeds 1.4.0", forRemoval = true)
    List<RuleDTO> findAllRules() ;

    /**
     * Get all Rules from DB
     * @return RuleDTO list
     */
    List<RuleDTO> findAllRules(int offset, int limit) ;

    /**
     * Get all active Rules from DB
     * @return RuleDTO list
     */
     List<RuleDTO> getActiveRules();
    /**
     * Get all Rules by Domain from DB
     * * @param domain : rule's domain param
     * @return RuleDTO list
     */
    List<RuleDTO> getAllRulesByDomain(String domain) throws IllegalArgumentException;

    /**
     * Get all Rules by with null DomainDTO from DB
     * @return RuleDTO list
     */
     List<RuleDTO> getAllRulesWithNullDomain();

    /**
     * Get all Events from rules
     * @return RuleDTO list
     */
     List<String> getAllEvents();


    /**
     * Get all Domains from Rules from DB
     * @return String list
     */
     List<String> getDomainListFromRules() ;

    /**
     * delete rule with specific id
     */
     void deleteRule (Long id)  throws Exception;

    /**
     * Add Rule to DB
     * @param ruleDTO : an object of type RuleDTO
     * @return RuleDTO object
     */
     RuleDTO addRule (RuleDTO ruleDTO)  throws Exception;

    /**
     * Update Rule to DB
     * @param ruleDTO : an object of type RuleDTO
     * @return RuleDTO object
     */
     RuleDTO updateRule (RuleDTO ruleDTO) throws Exception;
}

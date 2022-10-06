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

import java.util.List;

import org.exoplatform.addons.gamification.service.dto.configuration.RuleDTO;
import org.exoplatform.addons.gamification.service.dto.configuration.RuleFilter;

public interface RuleService {

    /**
     * Find enable RuleEntity by title
     * @param ruleTitle : rule's title param
     * @return an instance of RuleDTO
     */
     RuleDTO findEnableRuleByTitle (String ruleTitle);

    /**
     * Get RuleEntity by id
     * @param id : rule's id param
     * @return an instance of RuleDTO
     */
    RuleDTO findRuleById (long id);

    /**
     * Find enable RuleEntity by title
     * @param ruleTitle : rule's title param
     * @return an instance of RuleDTO
     */
     List<RuleDTO> findEnabledRulesByEvent (String ruleTitle);

    /**
     * Find a RuleEntity by title
     * @param ruleTitle : rule's title param
     * @return an instance of RuleDTO
     */
     RuleDTO findRuleByTitle (String ruleTitle);


    /**
     * Find a RuleEntity by title
     * @param ruleTitle : rule's title param
     * @param domain : rule's domain param
     * @return an instance of RuleDTO
     */
    RuleDTO findRuleByEventAndDomain (String ruleTitle, String domain);

    /**
     * Get all Rules from DB
     * @return RuleDTO list
     * @deprecated use methods with pagination instead to avoid performance and memory issues
     * @since Meeds 1.4.0
     */
    @Deprecated(since = "Meeds 1.4.0", forRemoval = true)
    List<RuleDTO> findAllRules(); // NOSONAR

    /**
     * Get all Rules using offset and limit.
     * 
     * @param offset Offset of result
     * @param limit Limit of result
     * @return {@link List} of {@link RuleDTO}
     */
    List<RuleDTO> findAllRules(int offset, int limit) ;

    /**
     * Get Rules by filter using offset and limit.
     *
     * @param ruleFilter {@link RuleFilter} used to filter rules
     * @param offset Offset of result
     * @param limit Limit of result
     * @return {@link List} of {@link RuleDTO}
     */
    List<RuleDTO> getRulesByFilter(RuleFilter ruleFilter, int offset, int limit) ;

    /**
     * @return count rules by filter
     */
    int countAllRules(RuleFilter ruleFilter);

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
    List<RuleDTO> getAllRulesByDomain(String domain);

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
     *
     * @param domainId domain id
     * @return rules total scores that can be earned
     */
     long getRulesTotalScoreByDomain(long domainId);

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

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
import org.exoplatform.commons.exception.ObjectNotFoundException;

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
      * Deletes an existing rule
      *
      * @param ruleId Rule technical identifier to delete
      * @param username User name of user attempting to delete a rule
      * @return deleted {@link RuleDTO}
      * @throws IllegalAccessException when user is not authorized to delete the
      *           rule
      * @throws ObjectNotFoundException when the rule identified by its technical
      *           identifier is not found
      */
     RuleDTO deleteRuleById(Long ruleId, String username) throws IllegalAccessException, ObjectNotFoundException;

    /**
     * Add Rule to DB
     * @param ruleDTO {@link RuleDTO} to create
     * @param username User name of user attempting to create a rule
     * @return created {@link RuleDTO}
     * @throws IllegalAccessException when user is not authorized to create a rule
     */
     RuleDTO createRule (RuleDTO ruleDTO, String username) throws IllegalAccessException;

     /**
     * Add Rule to DB
     * @param ruleDTO {@link RuleDTO} to create
     * @return created {@link RuleDTO}
     */
     RuleDTO createRule (RuleDTO ruleDTO);

    /**
     * Update Rule to DB
     * @param ruleDTO {@link RuleDTO} to update
     * @param username User name of user attempting to update a rule
     * @return updated {@link RuleDTO}
     */
     RuleDTO updateRule (RuleDTO ruleDTO, String username) throws ObjectNotFoundException, IllegalAccessException;

    /**
     * Check whether user can manage rules or not.
     *
     * @param username User name
     * @return true if the user is a member of platform/rewarding, else return false.
     */
    boolean canManageRule(String username);
}

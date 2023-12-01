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
package io.meeds.gamification.service;

import java.util.List;

import org.exoplatform.commons.exception.ObjectNotFoundException;

import io.meeds.gamification.model.RuleDTO;
import io.meeds.gamification.model.filter.RuleFilter;

public interface RuleService {

  /**
   * Get RuleEntity by id
   * 
   * @param  id : rule's id param
   * @return    an instance of RuleDTO
   */
  RuleDTO findRuleById(long id);

  /**
   * @param  id                      rule technical identifier
   * @param  username                user accessing rule
   * @return                         {@link RuleDTO}
   * @throws IllegalAccessException  when user doesn't have enough privileges to
   *                                   access rule
   * @throws ObjectNotFoundException when rule with id isn't enabled or isn't
   *                                   found
   */
  RuleDTO findRuleById(long id, String username) throws IllegalAccessException, ObjectNotFoundException;

  /**
   * Find a RuleEntity by title
   * 
   * @param  ruleTitle : rule's title param
   * @return           an instance of RuleDTO
   */
  RuleDTO findRuleByTitle(String ruleTitle);

  /**
   * Get Rules accessible for a given user by filter using offset and limit.
   *
   * @param  ruleFilter {@link RuleFilter} used to filter rules
   * @param  username   User name accessing Programs
   * @param  offset     Offset of result
   * @param  limit      Limit of result
   * @return            {@link List} of {@link RuleDTO}
   */
  List<RuleDTO> getRules(RuleFilter ruleFilter, String username, int offset, int limit);

  /**
   * Get Rules by filter using offset and limit.
   *
   * @param  ruleFilter {@link RuleFilter} used to filter rules
   * @param  offset     Offset of result
   * @param  limit      Limit of result
   * @return            {@link List} of {@link RuleDTO}
   */
  List<RuleDTO> getRules(RuleFilter ruleFilter, int offset, int limit);

  /**
   * @param  ruleFilter {@link RuleFilter} used to count associated rules
   * @param  username   User name accessing Programs
   * @return            count rules by filter accessible to a given user
   */
  int countRules(RuleFilter ruleFilter, String username);

  /**
   * @param  ruleFilter {@link RuleFilter} used to count associated rules
   * @return            count rules by filter
   */
  int countRules(RuleFilter ruleFilter);

  /**
   * Returns the count of active rules of a given program identified by its id.
   * The list of active rules corresponds to enabled rules which started or is upcoming.
   * 
   * @param  programId Program technical identifier
   * @return           {@link Integer} got active rules count
   */
  int countActiveRules(long programId);

  /**
   * Deletes an existing rule
   *
   * @param  ruleId                  Rule technical identifier to delete
   * @param  username                User name of user attempting to delete a
   *                                   rule
   * @return                         deleted {@link RuleDTO}
   * @throws IllegalAccessException  when user is not authorized to delete the
   *                                   rule
   * @throws ObjectNotFoundException when the rule identified by its technical
   *                                   identifier is not found
   */
  RuleDTO deleteRuleById(long ruleId, String username) throws IllegalAccessException, ObjectNotFoundException;

  /**
   * Deletes an existing rule
   *
   * @param  ruleId Rule technical identifier to delete
   * @return        deleted {@link RuleDTO}
   */
  RuleDTO deleteRuleById(long ruleId);

  /**
   * Add Rule to DB
   * 
   * @param  ruleDTO                      {@link RuleDTO} to create
   * @param  username                     User name of user attempting to create
   *                                        a rule
   * @return                              created {@link RuleDTO}
   * @throws IllegalAccessException       when user is not authorized to create
   *                                        a rule
   * @throws ObjectNotFoundException      when program doesn't exists
   */
  RuleDTO createRule(RuleDTO ruleDTO, String username) throws IllegalAccessException,
                                                       ObjectNotFoundException;

  /**
   * Add Rule to DB
   * 
   * @param  ruleDTO {@link RuleDTO} to create
   * @return         created {@link RuleDTO}
   */
  RuleDTO createRule(RuleDTO ruleDTO);

  /**
   * Update Rule to DB
   * 
   * @param  ruleDTO                 {@link RuleDTO} to update
   * @param  username                User name of user attempting to update a
   *                                   rule
   * @return                         updated {@link RuleDTO}
   * @throws ObjectNotFoundException when rule doesn't exists
   * @throws IllegalAccessException  when user sin't allowed to update chosen
   *                                   rule
   */
  RuleDTO updateRule(RuleDTO ruleDTO, String username) throws ObjectNotFoundException, IllegalAccessException;

  /**
   * Update Rule to DB
   * 
   * @param  ruleDTO                 {@link RuleDTO} to update
   * @return                         updated {@link RuleDTO}
   * @throws ObjectNotFoundException when rule doesn't exists
   */
  default RuleDTO updateRule(RuleDTO ruleDTO) throws ObjectNotFoundException {
    return null;
  }

  /**
   * Retrieve prerequisite rules to achieve in order to be gamified for a given
   * rule by an earner
   * 
   * @param  ruleId {@link RuleDTO} identifier
   * @return        {@link List} of {@link RuleDTO}
   */
  List<RuleDTO> getPrerequisiteRules(long ruleId);

}

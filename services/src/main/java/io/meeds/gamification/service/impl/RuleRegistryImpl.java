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
package io.meeds.gamification.service.impl;

import static io.meeds.gamification.constant.GamificationConstant.GAMIFICATION_DEFAULT_DATA_PREFIX;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.picocontainer.Startable;

import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import io.meeds.gamification.model.RuleDTO;
import io.meeds.gamification.plugin.RuleConfigPlugin;
import io.meeds.gamification.service.ProgramService;
import io.meeds.gamification.service.RuleRegistry;
import io.meeds.gamification.service.RuleService;

public class RuleRegistryImpl implements Startable, RuleRegistry {

  private static final Log                    LOG                                        =
                                                  ExoLogger.getLogger(RuleRegistryImpl.class);

  // Gamification Settings
  public static final String                  GAMIFICATION_SETTINGS_RULE_KEY             = "GAMIFICATION_SETTINGS_RULE_KEY";

  // Compute the status of gamification's rules processing (true if rules are
  // processed successfully)
  public static final String                  GAMIFICATION_SETTINGS_RULE_PROCESSING_DONE =
                                                                                         "GAMIFICATION_SETTINGS_RULE_PROCESSING_DONE";

  private final Map<String, RuleConfigPlugin> ruleMap;

  protected RuleService                       ruleService;

  protected ProgramService                    programService;

  public RuleRegistryImpl(ProgramService programService,
                          RuleService ruleService) {
    this.programService = programService;
    this.ruleService = ruleService;
    this.ruleMap = new HashMap<>();
  }

  @Override
  public void addPlugin(RuleConfigPlugin ruleConfigPlugin) {
    if (StringUtils.isNotBlank(ruleConfigPlugin.getTitle())) {
      ruleMap.put(ruleConfigPlugin.getTitle(), ruleConfigPlugin);
    }
    registerAutomaticEvent(ruleConfigPlugin);
  }

  @Override
  public boolean remove(RuleConfigPlugin rule) {
    ruleMap.remove(rule.getTitle());
    return true;
  }

  @Override
  public void start() {
    try {
      // Processing registered rules

      for (RuleConfigPlugin ruleConfigPlugin : ruleMap.values()) {
        RuleDTO ruleDTO = ruleService.findRuleByTitle(GAMIFICATION_DEFAULT_DATA_PREFIX + ruleConfigPlugin.getTitle());
        if (ruleConfigPlugin.isEnable()
            && (ruleDTO == null
                || !(ruleDTO.getEvent().equals(ruleConfigPlugin.getEvent()))
                || !(ruleDTO.getTitle().equals(GAMIFICATION_DEFAULT_DATA_PREFIX + ruleConfigPlugin.getTitle())))) {
          store(ruleConfigPlugin, ruleDTO);
        }
      }
    } catch (Exception e) {
      LOG.error("Error when processing Rules ", e);
    }
  }

  @Override
  public void stop() {
    // Nothing to change
  }

  /**
   * Persist new rule within DB
   *
   * @param  ruleConfig
   * @return
   */
  private RuleDTO store(RuleConfigPlugin ruleConfig, RuleDTO ruleDTO) {
    try {
      if (ruleDTO != null) {
        ruleDTO.setTitle(GAMIFICATION_DEFAULT_DATA_PREFIX + ruleConfig.getTitle());
        ruleDTO.setDescription(ruleConfig.getDescription());
        ruleDTO.setEvent(ruleConfig.getEvent());
        if (!ruleDTO.isDeleted()) {
          ruleDTO = ruleService.updateRule(ruleDTO);
        }
      } else {
        ruleDTO = new RuleDTO();
        ruleDTO.setTitle(GAMIFICATION_DEFAULT_DATA_PREFIX + ruleConfig.getTitle());
        ruleDTO.setScore(ruleConfig.getScore());
        ruleDTO.setEnabled(ruleConfig.isEnable());
        ruleDTO.setEvent(ruleConfig.getEvent());
        ruleDTO.setDeleted(false);
        ruleDTO.setDescription(ruleConfig.getDescription());
        ruleDTO = ruleService.createRule(ruleDTO);
      }
    } catch (Exception e) {
      LOG.error("Error when saving Rule ", e);
    }
    return ruleDTO;
  }

  private void registerAutomaticEvent(RuleConfigPlugin ruleConfigPlugin) {
    if (StringUtils.isNotBlank(ruleConfigPlugin.getEvent())) {
      ruleService.addAutomaticEvent(ruleConfigPlugin.getEvent());
    } else if (StringUtils.isNotBlank(ruleConfigPlugin.getTitle())) {
      ruleService.addAutomaticEvent(ruleConfigPlugin.getTitle());
    }
  }

}

/**
 * This file is part of the Meeds project (https://meeds.io/).
 *
 * Copyright (C) 2020 - 2024 Meeds Association contact@meeds.io
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package io.meeds.gamification.plugin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.exoplatform.container.PortalContainer;
import org.exoplatform.portal.config.UserACL;
import org.exoplatform.services.security.Identity;

import io.meeds.gamification.model.RuleDTO;
import io.meeds.gamification.service.RuleService;
import io.meeds.portal.plugin.AclPlugin;

import jakarta.annotation.PostConstruct;

@Component
public class RuleAclPlugin implements AclPlugin {

  public static final String OBJECT_TYPE = RulePermanentLinkPlugin.OBJECT_TYPE;

  @Autowired
  private RuleService        ruleService;

  @Autowired
  private PortalContainer    container;

  @PostConstruct
  public void init() {
    container.getComponentInstanceOfType(UserACL.class).addAclPlugin(this);
  }

  @Override
  public String getObjectType() {
    return OBJECT_TYPE;
  }

  @Override
  public boolean hasPermission(String objectId, String permissionType, Identity identity) {
    String username = identity == null ? null : identity.getUserId();
    RuleDTO rule = ruleService.findRuleById(Long.parseLong(objectId));
    return switch (permissionType) {
    case VIEW_PERMISSION_TYPE: {
      yield ruleService.canViewRule(rule, username);
    }
    case EDIT_PERMISSION_TYPE, DELETE_PERMISSION_TYPE: {
      yield ruleService.canEditRule(rule, username);
    }
    default:
      yield false;
    };
  }

}

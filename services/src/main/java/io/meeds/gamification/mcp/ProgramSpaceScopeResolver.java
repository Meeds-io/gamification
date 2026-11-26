/*
 * This file is part of the Meeds project (https://meeds.io/).
 *
 * Copyright (C) 2020 - 2026 Meeds Association contact@meeds.io
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package io.meeds.gamification.mcp;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import io.meeds.gamification.service.ProgramService;
import io.meeds.mcp.server.plugin.SpaceScopeResolver;

import lombok.extern.slf4j.Slf4j;

/**
 * Tells which space a campaign (a program) belongs to: its own space, when it
 * is a space program. An open campaign belongs to no space. Read without any
 * access check, for the space scope decision only.
 */
@Service("gamificationProgramSpaceScopeResolver")
@Profile("mcp-server")
@Slf4j
public class ProgramSpaceScopeResolver implements SpaceScopeResolver {

  /** The name the campaign tools' {@code space_scope.resolver} gives. */
  public static final String NAME = "campaign";

  /** The service the program is read from, without access check. */
  private final ProgramService programService;

  /**
   * @param programService the program service
   */
  public ProgramSpaceScopeResolver(ProgramService programService) {
    this.programService = programService;
  }

  /**
   * @return {@value #NAME}
   */
  @Override
  public String getName() {
    return NAME;
  }

  /**
   * @param objectId the campaign id
   * @return the id of the campaign's space, or null for an open campaign, a
   *         campaign of no space, a deleted or missing one, an id that isn't
   *         a number, or a lookup that fails
   */
  @Override
  public Long spaceOf(String objectId) {
    if (!StringUtils.isNumeric(objectId)) {
      return null;
    }
    try {
      return ProgramSpace.of(programService.getProgramById(Long.parseLong(objectId)));
    } catch (RuntimeException e) {
      log.debug("The space of campaign '{}' couldn't be read", objectId, e);
      return null;
    }
  }

}

/*
 * 
 * This file is part of the Meeds project (https://meeds.io/).
 * 
 * Copyright (C) 2023 Meeds Association contact@meeds.io
 * 
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
 * 
 */
package io.meeds.gamification.plugin;

import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.space.model.Space;
import org.exoplatform.social.core.space.spi.SpaceService;

import io.meeds.gamification.model.ProgramDTO;
import io.meeds.gamification.service.ProgramService;
import io.meeds.social.translation.plugin.TranslationPlugin;

public class ProgramTranslationPlugin extends TranslationPlugin {

  public static final String PROGRAM_OBJECT_TYPE            = "program";

  public static final String PROGRAM_DESCRIPTION_FIELD_NAME = "description";

  public static final String PROGRAM_TITLE_FIELD_NAME       = "title";

  private ProgramService     programService;

  private SpaceService       spaceService;

  private IdentityManager    identityManager;

  public ProgramTranslationPlugin(ProgramService programService,
                                  SpaceService spaceService,
                                  IdentityManager identityManager) {
    this.programService = programService;
    this.spaceService = spaceService;
    this.identityManager = identityManager;
  }

  @Override
  public String getObjectType() {
    return PROGRAM_OBJECT_TYPE;
  }

  @Override
  public boolean hasAccessPermission(long programId, String username) throws ObjectNotFoundException {
    return programService.canViewProgram(programId, username);
  }

  @Override
  public boolean hasEditPermission(long programId, String username) throws ObjectNotFoundException {
    ProgramDTO program = this.programService.getProgramById(programId);
    if (program == null) {
      throw new ObjectNotFoundException(String.format("Program with id %s wasn't found",
                                                      programId));
    }
    return programService.isProgramOwner(programId, username);
  }

  @Override
  public long getAudienceId(long programId) throws ObjectNotFoundException {
    long spaceId = getSpaceId(programId);
    if (spaceId == 0) {
      return 0;
    }
    Space space = spaceService.getSpaceById(String.valueOf(spaceId));
    if (space == null) {
      throw new ObjectNotFoundException(String.format("Space with id %s wasn't found",
                                                      spaceId));
    }
    Identity spaceIdentity = identityManager.getOrCreateSpaceIdentity(space.getPrettyName());
    return Long.parseLong(spaceIdentity.getId());
  }

  @Override
  public long getSpaceId(long programId) throws ObjectNotFoundException {
    ProgramDTO program = this.programService.getProgramById(programId);
    if (program == null) {
      throw new ObjectNotFoundException(String.format("Program with id %s wasn't found",
                                                      programId));
    }
    return program.getSpaceId();
  }

}

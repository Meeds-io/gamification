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

import static io.meeds.portal.plugin.AclPlugin.EDIT_PERMISSION_TYPE;
import static io.meeds.portal.plugin.AclPlugin.VIEW_PERMISSION_TYPE;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.portal.config.UserACL;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.space.model.Space;
import org.exoplatform.social.core.space.spi.SpaceService;

import io.meeds.gamification.model.ProgramDTO;
import io.meeds.gamification.service.ProgramService;
import io.meeds.social.translation.plugin.TranslationPlugin;
import io.meeds.social.translation.service.TranslationService;

import jakarta.annotation.PostConstruct;

@Component
public class ProgramTranslationPlugin extends TranslationPlugin {

  public static final String PROGRAM_OBJECT_TYPE            = "program";

  public static final String PROGRAM_DESCRIPTION_FIELD_NAME = "description";

  public static final String PROGRAM_TITLE_FIELD_NAME       = "title";

  @Autowired
  private ProgramService     programService;

  @Autowired
  private SpaceService       spaceService;

  @Autowired
  private IdentityManager    identityManager;

  @Autowired
  private UserACL            userACL;

  @Autowired
  private PortalContainer    container;

  @PostConstruct
  public void init() {
    container.getComponentInstanceOfType(TranslationService.class).addPlugin(this);
  }

  @Override
  public String getObjectType() {
    return PROGRAM_OBJECT_TYPE;
  }

  @Override
  public boolean hasAccessPermission(long programId, String username) throws ObjectNotFoundException {
    return userACL.hasPermission(PROGRAM_OBJECT_TYPE, String.valueOf(programId), VIEW_PERMISSION_TYPE, username);
  }

  @Override
  public boolean hasEditPermission(long programId, String username) throws ObjectNotFoundException {
    return userACL.hasPermission(PROGRAM_OBJECT_TYPE, String.valueOf(programId), EDIT_PERMISSION_TYPE, username);
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

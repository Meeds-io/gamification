/**
 * This file is part of the Meeds project (https://meeds.io/).
 *
 * Copyright (C) 2026 Meeds Association contact@meeds.io
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

import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.portal.config.UserPortalConfigService;
import org.exoplatform.services.security.Identity;

import io.meeds.gamification.service.ProgramService;
import io.meeds.portal.permlink.model.PermanentLinkObject;
import io.meeds.portal.permlink.plugin.PermanentLinkPlugin;
import io.meeds.portal.permlink.service.PermanentLinkService;

import jakarta.annotation.PostConstruct;

/**
 * Permanent link plugin of a gamification program: it resolves a program id
 * into the program page of the engagement site, the page the platform's own
 * program chips open ({@code eXo.env.portal.engagementSiteName} is the meta
 * portal, as {@link UserPortalConfigService#getMetaPortal()} returns it).
 */
@Component
public class ProgramPermanentLinkPlugin implements PermanentLinkPlugin {

  public static final String      OBJECT_TYPE = ProgramTranslationPlugin.PROGRAM_OBJECT_TYPE;

  public static final String      URL_FORMAT  = "/portal/%s/contributions/programs/%s";

  @Autowired
  private UserPortalConfigService portalConfigService;

  @Autowired
  private ProgramService          programService;

  @Autowired
  private PortalContainer         container;

  /**
   * Registers this plugin in the portal's {@link PermanentLinkService}.
   */
  @PostConstruct
  public void init() {
    container.getComponentInstanceOfType(PermanentLinkService.class).addPlugin(this);
  }

  /**
   * @return {@link #OBJECT_TYPE}
   */
  @Override
  public String getObjectType() {
    return OBJECT_TYPE;
  }

  /**
   * Grants the redirection only to an identity that may view the program, as
   * {@link ProgramService#canViewProgram(long, String)} decides it.
   *
   * @param object the program reference, its object id being the program id
   * @param identity the identity following the link, null when unknown
   * @return true when the identity may view the program
   * @throws ObjectNotFoundException when the object id is not a program id
   */
  @Override
  public boolean canAccess(PermanentLinkObject object, Identity identity) throws ObjectNotFoundException {
    return identity != null && programService.canViewProgram(parseProgramId(object), identity.getUserId());
  }

  /**
   * Builds the program page path on the engagement site.
   *
   * @param object the program reference, its object id being the program id
   * @return the program page path, without the domain
   * @throws ObjectNotFoundException when the object id is not a program id
   */
  @Override
  public String getDirectAccessUrl(PermanentLinkObject object) throws ObjectNotFoundException {
    return String.format(URL_FORMAT, portalConfigService.getMetaPortal(), parseProgramId(object));
  }

  /**
   * Reads the program id of a permanent link object.
   *
   * @param object the program reference
   * @return the program id
   * @throws ObjectNotFoundException when the object id is not a positive number
   */
  private long parseProgramId(PermanentLinkObject object) throws ObjectNotFoundException {
    try {
      long programId = Long.parseLong(object.getObjectId());
      if (programId > 0) {
        return programId;
      }
    } catch (NumberFormatException e) {
      // Answered below, as any id that designates no program
    }
    throw new ObjectNotFoundException("Not a program id: " + object.getObjectId());
  }

}

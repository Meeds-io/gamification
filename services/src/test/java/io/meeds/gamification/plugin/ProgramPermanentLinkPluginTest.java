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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.portal.config.UserPortalConfigService;
import org.exoplatform.services.security.Identity;

import io.meeds.gamification.service.ProgramService;
import io.meeds.portal.permlink.model.PermanentLinkObject;
import io.meeds.portal.permlink.service.PermanentLinkService;

import jakarta.annotation.PostConstruct;

@RunWith(MockitoJUnitRunner.class)
public class ProgramPermanentLinkPluginTest {

  private static final String        PROGRAM_ID = "5";

  private static final String        USERNAME   = "testuser";

  @Mock
  private UserPortalConfigService    portalConfigService;

  @Mock
  private ProgramService             programService;

  @Mock
  private PortalContainer            container;

  @InjectMocks
  private ProgramPermanentLinkPlugin plugin;

  /**
   * The program link opens the program page of the engagement site, the page
   * the platform's program chips open.
   */
  @Test
  public void testGetDirectAccessUrlIsTheProgramPageOfTheEngagementSite() throws ObjectNotFoundException {
    when(portalConfigService.getMetaPortal()).thenReturn("dw");

    assertEquals("/portal/dw/contributions/programs/5",
                 plugin.getDirectAccessUrl(new PermanentLinkObject(ProgramPermanentLinkPlugin.OBJECT_TYPE, PROGRAM_ID)));
  }

  /**
   * An object id that designates no program is answered as not found, never
   * formatted into an address.
   */
  @Test
  public void testGetDirectAccessUrlRefusesAnIdThatIsNoProgramId() {
    assertThrows(ObjectNotFoundException.class,
                 () -> plugin.getDirectAccessUrl(new PermanentLinkObject(ProgramPermanentLinkPlugin.OBJECT_TYPE, "abc")));
    assertThrows(ObjectNotFoundException.class,
                 () -> plugin.getDirectAccessUrl(new PermanentLinkObject(ProgramPermanentLinkPlugin.OBJECT_TYPE, "0")));
    assertThrows(ObjectNotFoundException.class,
                 () -> plugin.getDirectAccessUrl(new PermanentLinkObject(ProgramPermanentLinkPlugin.OBJECT_TYPE, null)));
  }

  /**
   * An identity that may view the program is granted the redirection.
   */
  @Test
  public void testCanAccessGrantsAViewer() throws ObjectNotFoundException {
    when(programService.canViewProgram(5L, USERNAME)).thenReturn(true);

    assertTrue(plugin.canAccess(new PermanentLinkObject(ProgramPermanentLinkPlugin.OBJECT_TYPE, PROGRAM_ID),
                                new Identity(USERNAME)));
  }

  /**
   * An identity that may not view the program is refused the redirection.
   */
  @Test
  public void testCanAccessRefusesANonViewer() throws ObjectNotFoundException {
    when(programService.canViewProgram(5L, USERNAME)).thenReturn(false);

    assertFalse(plugin.canAccess(new PermanentLinkObject(ProgramPermanentLinkPlugin.OBJECT_TYPE, PROGRAM_ID),
                                 new Identity(USERNAME)));
  }

  /**
   * An unknown identity is refused without asking the program service.
   */
  @Test
  public void testCanAccessRefusesAnUnknownIdentity() throws ObjectNotFoundException {
    assertFalse(plugin.canAccess(new PermanentLinkObject(ProgramPermanentLinkPlugin.OBJECT_TYPE, PROGRAM_ID), null));
    verify(programService, never()).canViewProgram(anyLong(), anyString());
  }

  /**
   * The plugin answers the program object type.
   */
  @Test
  public void testGetObjectType() {
    assertEquals("program", plugin.getObjectType());
  }

  /**
   * The plugin registers itself in the portal's permanent link service, and
   * the framework calls that registration: a mutant dropping
   * {@code @PostConstruct} would leave every program address unresolved while
   * a test calling {@code init()} by hand stays green.
   */
  @Test
  public void testInitRegistersThePluginAtStartup() throws NoSuchMethodException {
    PermanentLinkService permanentLinkService = mock(PermanentLinkService.class);
    when(container.getComponentInstanceOfType(PermanentLinkService.class)).thenReturn(permanentLinkService);

    plugin.init();

    verify(permanentLinkService).addPlugin(plugin);
    assertTrue(ProgramPermanentLinkPlugin.class.getMethod("init").isAnnotationPresent(PostConstruct.class));
  }

}

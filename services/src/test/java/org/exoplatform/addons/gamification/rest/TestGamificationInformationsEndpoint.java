/**
 * This file is part of the Meeds project (https://meeds.io/).
 * Copyright (C) 2022 Meeds Association
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
package org.exoplatform.addons.gamification.rest;

import java.util.Collections;
import java.util.List;

import org.exoplatform.addons.gamification.entities.domain.configuration.DomainEntity;
import org.exoplatform.addons.gamification.rest.model.GamificationInformationRestEntity;
import org.exoplatform.services.security.MembershipEntry;
import org.junit.Before;
import org.junit.Test;

import org.exoplatform.addons.gamification.test.AbstractServiceTest;
import org.exoplatform.addons.gamification.utils.Utils;
import org.exoplatform.services.rest.impl.ContainerResponse;

public class TestGamificationInformationsEndpoint extends AbstractServiceTest { // NOSONAR

  private static final String REST_PATH    = "/gamification/gameficationinformationsboard";

  protected Class<?> getComponentClass() {
    return GamificationInformationsEndpoint.class;
  }

  @Override
  @Before
  public void setUp() throws Exception {
    super.setUp();
    org.exoplatform.services.security.Identity adminAclIdentity =
                                                                new org.exoplatform.services.security.Identity("root",
                                                                                                               Collections.singleton(new MembershipEntry(Utils.ADMINS_GROUP)));
    identityRegistry.register(adminAclIdentity);
    registry(getComponentClass());
  }

  @Test
  public void testGetAllLeadersByRank() throws Exception {
    DomainEntity domainEntity = newDomain();
    newGamificationActionsHistory("rule", domainEntity.getId());
    startSessionAs("root");
    ContainerResponse response = getResponse("GET", REST_PATH + "/history/all", null);
    assertNotNull(response);
    assertEquals(400, response.getStatus());

    response = getResponse("GET", REST_PATH + "/history/all?providerId=user", null);
    assertNotNull(response);
    assertEquals(400, response.getStatus());

    response = getResponse("GET", REST_PATH + "/history/all?capacity=20&providerId=user&remoteId=root1", null);
    assertNotNull(response);
    assertEquals(200, response.getStatus());

    List<GamificationInformationRestEntity> gamificationActionsHistoryRestEntities = (List<GamificationInformationRestEntity>) response.getEntity();
    assertEquals(1, gamificationActionsHistoryRestEntities.size());
  }

}

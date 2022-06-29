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

import java.util.Date;

import org.exoplatform.addons.gamification.service.mapper.DomainMapper;
import org.exoplatform.addons.gamification.utils.Utils;
import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.exoplatform.addons.gamification.entities.domain.configuration.DomainEntity;
import org.exoplatform.addons.gamification.service.dto.configuration.DomainDTO;
import org.exoplatform.addons.gamification.test.AbstractServiceTest;

import javax.persistence.EntityNotFoundException;
import static org.junit.Assert.assertThrows;

public class DomainServiceTest extends AbstractServiceTest {

  @Test
  public void testGetAllDomains() {
    assertEquals(domainService.getAllDomains().size(), 0);
    Date createDate = new Date(System.currentTimeMillis());
    Date lastModifiedDate = new Date(System.currentTimeMillis() + 10);
    DomainDTO domain1 = new DomainDTO();
    domain1.setTitle("domain1");
    domain1.setDescription("Description");
    domain1.setCreatedBy(TEST_USER_SENDER);
    domain1.setCreatedDate(Utils.toRFC3339Date(createDate));
    domain1.setLastModifiedBy(TEST_USER_SENDER);
    domain1.setDeleted(false);
    domain1.setEnabled(true);
    domain1.setLastModifiedDate(Utils.toRFC3339Date(lastModifiedDate));
    domainService.addDomain(domain1);
    DomainDTO domain2 = new DomainDTO();
    domain2.setTitle("domain2");
    domain2.setDescription("Description");
    domain2.setCreatedBy(TEST_USER_SENDER);
    domain2.setCreatedDate(Utils.toRFC3339Date(createDate));
    domain2.setLastModifiedBy(TEST_USER_SENDER);
    domain2.setDeleted(false);
    domain2.setEnabled(true);
    domain2.setLastModifiedDate(Utils.toRFC3339Date(lastModifiedDate));
    domainService.addDomain(domain2);
    DomainDTO domain3 = new DomainDTO();
    domain3.setTitle("domain3");
    domain3.setDescription("Description");
    domain3.setCreatedBy(TEST_USER_SENDER);
    domain3.setCreatedDate(Utils.toRFC3339Date(createDate));
    domain3.setLastModifiedBy(TEST_USER_SENDER);
    domain3.setDeleted(false);
    domain3.setEnabled(true);
    domain3.setLastModifiedDate(Utils.toRFC3339Date(lastModifiedDate));
    domainService.addDomain(domain3);
    assertEquals(domainService.getAllDomains().size(), 3);
  }

  @Test
  public void testFindEnabledDomainByTitle() {
    assertNull(domainService.findEnabledDomainByTitle(GAMIFICATION_DOMAIN));
    DomainDTO domainDTO = newDomainDTO();
    assertNotNull(domainService.findEnabledDomainByTitle(GAMIFICATION_DOMAIN));
    domainDTO.setEnabled(false);
    try {
      domainService.updateDomain(domainDTO);
    } catch (ObjectNotFoundException e) {
    //
    }
    assertNull(domainService.findEnabledDomainByTitle(GAMIFICATION_DOMAIN));
  }

  @Test
  public void testAddDomain() {
    assertEquals(domainDAO.findAll().size(), 0);
    Date createDate = new Date(System.currentTimeMillis());
    Date lastModifiedDate = new Date(System.currentTimeMillis() + 10);
    DomainDTO domain = new DomainDTO();
    domain.setTitle(GAMIFICATION_DOMAIN);
    domain.setDescription("Description");
    domain.setCreatedBy(TEST_USER_SENDER);
    domain.setCreatedDate(Utils.toRFC3339Date(createDate));
    domain.setLastModifiedBy(TEST_USER_SENDER);
    domain.setDeleted(false);
    domain.setEnabled(true);
    domain.setLastModifiedDate(Utils.toRFC3339Date(lastModifiedDate));
    domain = domainService.addDomain(domain);
    assertNotNull(domainDAO.find(domain.getId()));
  }

  @Test
  public void testUpdateDomain() {
    try {
      DomainEntity domainEntity = newDomain();
      domainEntity.setDescription("desc_2");
      domainService.updateDomain(DomainMapper.domainToDomainDTO(domainEntity));
      domainEntity = domainDAO.find(domainEntity.getId());
      assertEquals(domainEntity.getDescription(), "desc_2");
    } catch (Exception e) {
      fail("Error when updating domain", e);
    }

  }

  @Test
  public void testDeleteDomain() {
    try {
      DomainEntity domainEntity = newDomain();
      assertEquals(domainEntity.isDeleted(), false);
      domainService.deleteDomain(domainEntity.getId());
      assertEquals(domainEntity.isDeleted(), true);
      assertThrows(EntityNotFoundException.class, () ->  domainService.deleteDomain(58620l));
    } catch (Exception e) {
      fail("Error when deleteing domain", e);
    }
  }

  @Test
  public void testGetDomainByTitle() {
    assertNull(domainService.getDomainByTitle(GAMIFICATION_DOMAIN));
    DomainDTO domainDTO = newDomainDTO();
    assertNotNull(domainService.getDomainByTitle(GAMIFICATION_DOMAIN));
    domainDTO.setDeleted(true);
    domainService.addDomain(domainDTO);
    domainDTO = domainService.getDomainByTitle(GAMIFICATION_DOMAIN);
    assertNotNull(domainDTO);
    assertEquals(domainDTO.getTitle(), GAMIFICATION_DOMAIN);
  }
}

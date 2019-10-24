package org.exoplatform.addons.gamification.service.configuration;

import java.util.Date;

import org.junit.Test;

import org.exoplatform.addons.gamification.entities.domain.configuration.DomainEntity;
import org.exoplatform.addons.gamification.service.dto.configuration.DomainDTO;
import org.exoplatform.addons.gamification.test.AbstractServiceTest;

public class DomainServiceTest extends AbstractServiceTest {

  @Test
  public void testGetAllDomains() {
    assertEquals(domainService.getAllDomains().size(), 0);
    newDomain("domain1");
    newDomain("domain2");
    newDomain("domain3");
    newDomain("domain4");
    newDomain("domain5");
    assertEquals(domainService.getAllDomains().size(), 5);
  }

  @Test
  public void testFindDomainByTitle() {
    assertNull(domainService.findDomainByTitle(GAMIFICATION_DOMAIN));
    newDomain();
    assertNotNull(domainService.findDomainByTitle(GAMIFICATION_DOMAIN));
  }

  @Test
  public void testAddDomain() {
    assertEquals(domainStorage.findAll().size(), 0);
    DomainDTO domain = new DomainDTO();
    domain.setTitle(GAMIFICATION_DOMAIN);
    domain.setDescription("Description");
    domain.setCreatedBy(TEST_USER_SENDER);
    domain.setLastModifiedBy(TEST_USER_SENDER);
    domain.setDeleted(false);
    domain.setEnabled(true);
    domain.setLastModifiedDate(new Date());
    domain = domainService.addDomain(domain);
    assertNotNull(domainStorage.find(domain.getId()));
  }

  @Test
  public void testUpdateDomain() {
    try {
      DomainEntity domainEntity = newDomain();
      domainEntity.setDescription("desc_2");
      domainService.updateDomain(domainMapper.domainToDomainDTO(domainEntity));
      domainEntity = domainStorage.find(domainEntity.getId());
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
    } catch (Exception e) {
      fail("Error when deleteing domain", e);
    }
  }
}

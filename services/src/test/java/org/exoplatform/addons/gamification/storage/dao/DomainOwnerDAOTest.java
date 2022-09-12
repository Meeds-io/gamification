package org.exoplatform.addons.gamification.storage.dao;

import java.util.Collections;
import java.util.List;

import org.junit.Test;

import org.exoplatform.addons.gamification.entities.domain.configuration.DomainEntity;
import org.exoplatform.addons.gamification.entities.domain.configuration.DomainOwnerEntity;
import org.exoplatform.addons.gamification.service.dto.configuration.constant.EntityType;
import org.exoplatform.addons.gamification.test.AbstractServiceTest;

public class DomainOwnerDAOTest extends AbstractServiceTest {

  @Test
  public void testDeleteDomainOwners() {
    assertEquals(0, domainOwnerDAO.findAll().size());
    DomainEntity domainEntity = newDomain(EntityType.MANUAL, "domain", true, Collections.singleton(1l));
    assertEquals(1l, domainOwnerDAO.count().longValue());
    DomainEntity entity = domainDAO.find(domainEntity.getId());
    List<DomainOwnerEntity> owners = entity.getOwners();
    assertNotNull(owners);
    assertEquals(1, owners.size());
    DomainOwnerEntity savedOwnerEntity = owners.iterator().next();
    Long domainOwnerId = savedOwnerEntity.getId();
    savedOwnerEntity.setId(null);
    assertEquals(new DomainOwnerEntity(domainEntity, 1l), savedOwnerEntity);
    assertEquals(new DomainOwnerEntity(domainEntity, 1l).hashCode(), savedOwnerEntity.hashCode());

    savedOwnerEntity.setId(domainOwnerId);
    domainDAO.delete(domainEntity);
    assertEquals(0l, domainOwnerDAO.count().longValue());
  }

}

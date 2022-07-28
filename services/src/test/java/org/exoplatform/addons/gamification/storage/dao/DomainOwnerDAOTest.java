package org.exoplatform.addons.gamification.storage.dao;

import org.exoplatform.addons.gamification.entities.domain.configuration.DomainEntity;
import org.exoplatform.addons.gamification.service.dto.configuration.constant.EntityType;
import org.exoplatform.addons.gamification.test.AbstractServiceTest;
import org.junit.Test;

import java.util.Collections;

public class DomainOwnerDAOTest extends AbstractServiceTest {

	@Test
	public void testDeleteDomainOwners(){
		assertEquals(0, domainOwnerDAO.findAll().size());
		DomainEntity domainEntity = newDomain(EntityType.MANUAL, "domain", true, Collections.singleton(1l));
		assertEquals(1, domainOwnerDAO.findAll().size());
		domainOwnerDAO.deleteDomainOwners(domainEntity.getId());
		assertEquals(0, domainOwnerDAO.findAll().size());
	}
}

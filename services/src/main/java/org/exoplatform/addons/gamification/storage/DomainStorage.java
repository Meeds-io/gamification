package org.exoplatform.addons.gamification.storage;

import org.exoplatform.addons.gamification.entities.domain.configuration.DomainEntity;
import org.exoplatform.addons.gamification.service.dto.configuration.DomainDTO;
import org.exoplatform.addons.gamification.service.mapper.DomainMapper;
import org.exoplatform.addons.gamification.storage.dao.DomainDAO;

import java.util.Date;
import java.util.List;

public class DomainStorage {

  private DomainDAO domainDAO;

  public DomainStorage(DomainDAO domainDAO) {
    this.domainDAO = domainDAO;
  }

  public DomainDTO saveDomain(DomainDTO domainDTO) {

    DomainEntity domainEntity = DomainMapper.domainDTOToDomain(domainDTO);
    if (domainEntity.getId() == null || domainEntity.getId() == 0) {
      domainEntity.setId(null);
      domainEntity.setCreatedDate(new Date());
      domainEntity.setLastModifiedDate(new Date());
      domainEntity = domainDAO.create(domainEntity);
    } else {
      domainEntity.setLastModifiedDate(new Date());
      domainEntity = domainDAO.update(domainEntity);

    }
    return DomainMapper.domainToDomainDTO(domainEntity);

  }

  public DomainDTO findEnabledDomainByTitle(String domainTitle) {
    return DomainMapper.domainEntityToDomainDTO(domainDAO.findEnabledDomainByTitle(domainTitle));
  }

  public DomainDTO getDomainByTitle(String domainTitle) {
    return DomainMapper.domainEntityToDomainDTO(domainDAO.getDomainByTitle(domainTitle));
  }

  public List<DomainDTO> getAllDomains() {

    return DomainMapper.domainssToDomainDTOs(domainDAO.getAllDomains());
  }

  public List<DomainDTO> getEnabledDomains() {

    return DomainMapper.domainssToDomainDTOs(domainDAO.getEnabledDomains());
  }

  public DomainDTO getDomainByID(Long id) {
    return DomainMapper.domainEntityToDomainDTO(domainDAO.find(id));
  }

  public int deleteDomainByTitle(String domainTitle) {
    return domainDAO.deleteDomainByTitle(domainTitle);

  }
  public void clearCache() {// NOSONAR
    // implemented in cached storage
  }

}

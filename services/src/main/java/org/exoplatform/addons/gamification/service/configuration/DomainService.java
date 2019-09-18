package org.exoplatform.addons.gamification.service.configuration;

import java.util.List;

import javax.persistence.PersistenceException;

import org.exoplatform.addons.gamification.entities.domain.configuration.DomainEntity;
import org.exoplatform.addons.gamification.service.dto.configuration.DomainDTO;
import org.exoplatform.addons.gamification.service.mapper.DomainMapper;
import org.exoplatform.addons.gamification.storage.dao.DomainDAO;
import org.exoplatform.commons.api.persistence.ExoTransactional;
import org.exoplatform.commons.utils.CommonsUtils;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

public class DomainService {

  private static final Log     LOG = ExoLogger.getLogger(DomainService.class);

  protected final DomainDAO    domainStorage;

  protected final DomainMapper domainMapper;

  public DomainService(DomainDAO domainDAO, DomainMapper domainMapper) {
    this.domainStorage = domainDAO;
    this.domainMapper = domainMapper;

  }

  public DomainService() {
    this.domainStorage = CommonsUtils.getService(DomainDAO.class);
    this.domainMapper = CommonsUtils.getService(DomainMapper.class);

  }

  /**
   * Return all domains within the DB
   * 
   * @return a list of DomainDTO
   */
  public List<DomainDTO> getAllDomains() {
    try {
      // --- load all Domains
      List<DomainEntity> domains = domainStorage.findAll();
      if (domains != null) {
        return domainMapper.domainssToDomainDTOs(domains);
      } else {
        return null;
      }

    } catch (Exception e) {
      LOG.error("Error to find Domains", e);
      throw (e);
    }
  }

  /**
   * Find a DomainEntity by title
   * 
   * @param domainTitle : domain title
   * @return an instance DomainDTO
   */
  @ExoTransactional
  public DomainDTO findDomainByTitle(String domainTitle) {

    try {
      // --- Get Entity from DB
      DomainEntity entity = domainStorage.findDomainByTitle(domainTitle);
      // --- Convert Entity to DTO
      if (entity != null) {
        return domainMapper.domainToDomainDTO(entity);
      } else {
        return null;
      }

    } catch (Exception e) {
      LOG.error("Error to find Domain entity with title : {}", domainTitle, e);
      throw (e);
    }

  }

  /**
   * Add Domain to DB
   * 
   * @param domainDTO : an object of type DomainDTO
   * @return BadgeDTO object
   */
  @ExoTransactional
  public DomainDTO addDomain(DomainDTO domainDTO) throws PersistenceException {

    DomainEntity domainEntity = null;

    try {
      domainEntity = domainStorage.create(domainMapper.domainDTOToDomain(domainDTO));
      return domainMapper.domainToDomainDTO(domainEntity);
    } catch (Exception e) {
      LOG.error("Error to create badge with title {}", domainDTO.getTitle(), e);
      throw (e);
    }
  }

  /**
   * Update Domain
   * 
   * @param domainDTO : an instance of type DomainDTO
   * @return DomainDTO object
   */
  @ExoTransactional
  public DomainDTO updateDomain(DomainDTO domainDTO) throws PersistenceException {

    DomainEntity domainEntity = null;

    try {
      domainEntity = domainStorage.update(domainMapper.domainDTOToDomain(domainDTO));
      return domainMapper.domainToDomainDTO(domainEntity);

    } catch (Exception e) {
      LOG.error("Error to update domain {} with title {}", domainDTO.getId(), domainDTO.getTitle(), e);
      throw (e);
    }

  }

  /**
   * Delete Domain
   * 
   * @param id
   */
  @ExoTransactional
  public void deleteDomain(Long id) throws Exception {

    try {
      DomainEntity domain = domainStorage.find(id);
      domainStorage.delete(domain);
    } catch (Exception e) {
      LOG.error("Error to delete domain with title {}", id, e);
      throw (e);
    }
  }

}

package org.exoplatform.addons.gamification.service.configuration;

import org.exoplatform.addons.gamification.entities.domain.configuration.DomainEntity;
import org.exoplatform.addons.gamification.service.dto.configuration.DomainDTO;
import org.exoplatform.addons.gamification.service.mapper.DomainMapper;
import org.exoplatform.addons.gamification.storage.DomainStorage;
import org.exoplatform.addons.gamification.utils.Utils;
import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.exoplatform.commons.utils.CommonsUtils;
import org.exoplatform.services.listener.ListenerService;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import javax.persistence.EntityNotFoundException;
import java.util.Date;
import java.util.List;

public class DomainServiceImpl implements DomainService {

  private static final Log        LOG                          = ExoLogger.getLogger(DomainServiceImpl.class);

  protected final DomainStorage   domainStorage;

  protected final ListenerService listenerService;

  public static final String      GAMIFICATION_DOMAIN_LISTENER = "exo.gamification.domain.action";

  public DomainServiceImpl() {
    this.domainStorage = CommonsUtils.getService(DomainStorage.class);
    this.listenerService = CommonsUtils.getService(ListenerService.class);

  }

  public List<DomainDTO> getAllDomains() {
    try {
      // --- load all Domains
      return domainStorage.getAllDomains();
    } catch (Exception e) {
      LOG.error("Error to find Domains", e);
      throw (e);
    }
  }

  public List<DomainDTO> getEnabledDomains() {
    try {
      // --- load all Domains
      return domainStorage.getEnabledDomains();

    } catch (Exception e) {
      LOG.error("Error to find Domains", e);
      throw (e);
    }
  }

  public DomainDTO findEnabledDomainByTitle(String domainTitle) {
    try {
      return domainStorage.findEnabledDomainByTitle(domainTitle);

    } catch (Exception e) {
      LOG.error("Error to find Domain entity with title : {}", domainTitle, e);
      throw (e);
    }

  }

  public DomainDTO getDomainByTitle(String domainTitle) {
    try {
      return domainStorage.getDomainByTitle(domainTitle);

    } catch (Exception e) {
      LOG.error("Error to find Domain entity with title : {}", domainTitle, e);
      throw (e);
    }

  }

  public DomainDTO addDomain(DomainDTO domainDTO) {
    DomainDTO oldDomainDTO = null;
    String date = Utils.toRFC3339Date(new Date(System.currentTimeMillis()));
    try {
      oldDomainDTO = domainStorage.findEnabledDomainByTitle(domainDTO.getTitle());
      if (oldDomainDTO == null) {
        domainDTO.setCreatedDate(date);
        domainDTO.setLastModifiedDate(date);
        domainDTO = domainStorage.saveDomain(domainDTO);
      } else if (oldDomainDTO.isDeleted()) {
        domainDTO.setId(oldDomainDTO.getId());
        domainDTO.setLastModifiedDate(date);
        domainDTO = domainStorage.saveDomain(domainDTO);
      }
    } catch (Exception e) {
      LOG.error("Error to create domain with title {}", domainDTO.getTitle(), e);
      throw (e);
    }
    return domainDTO;
  }

  public DomainDTO updateDomain(DomainDTO domainDTO) throws ObjectNotFoundException {
    String date = Utils.toRFC3339Date(new Date(System.currentTimeMillis()));
    DomainDTO oldDomainDTO = domainStorage.getDomainByID(domainDTO.getId());
    if (oldDomainDTO != null) {
      domainDTO.setLastModifiedDate(date);
      domainDTO = domainStorage.saveDomain(domainDTO);
      DomainEntity domainEntity = DomainMapper.domainDTOToDomain(domainDTO);
      try {
        if (oldDomainDTO.isEnabled() && !domainDTO.isEnabled()) {
          listenerService.broadcast(GAMIFICATION_DOMAIN_LISTENER, domainEntity, "disable");
        }
        if (!oldDomainDTO.isEnabled() && domainDTO.isEnabled()) {
          listenerService.broadcast(GAMIFICATION_DOMAIN_LISTENER, domainEntity, "enable");
        }
      } catch (Exception e) {
        LOG.error("Error to update domain {} with title {}", domainDTO.getId(), domainDTO.getTitle(), e);
      }
      return domainDTO;
    } else {
      throw new ObjectNotFoundException("domain is not exist");
    }
  }

  public void deleteDomain(Long id) throws EntityNotFoundException {
    String date = Utils.toRFC3339Date(new Date(System.currentTimeMillis()));
    DomainDTO domainDTO = domainStorage.getDomainByID(id);
    if (domainDTO != null) {
      domainDTO.setDeleted(true);
      domainDTO.setLastModifiedDate(date);
      domainDTO = domainStorage.saveDomain(domainDTO);
      try {
        DomainEntity domainEntity = DomainMapper.domainDTOToDomain(domainDTO);
        listenerService.broadcast(GAMIFICATION_DOMAIN_LISTENER, domainEntity, "delete");
      } catch (Exception e) {
        LOG.error("Error to delete domain {}", id, e);
      }
    } else {
      LOG.warn("Domain {} not Found", id);
      throw (new EntityNotFoundException());
    }
  }

  @Override
  public DomainDTO getDomainByID(Long domainId) {
    if (domainId <= 0) {
      throw new IllegalArgumentException("domain id has to be positive integer");
    }
    return domainStorage.getDomainByID(domainId);

  }
}

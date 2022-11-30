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
package org.exoplatform.addons.gamification.service.configuration;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import org.exoplatform.addons.gamification.service.dto.configuration.DomainDTO;
import org.exoplatform.addons.gamification.service.dto.configuration.DomainFilter;
import org.exoplatform.addons.gamification.service.dto.configuration.constant.EntityType;
import org.exoplatform.addons.gamification.storage.DomainStorage;
import org.exoplatform.addons.gamification.utils.Utils;
import org.exoplatform.commons.api.persistence.ExoTransactional;
import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.exoplatform.commons.file.model.FileItem;
import org.exoplatform.commons.file.services.FileService;
import org.exoplatform.services.listener.ListenerService;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.security.Identity;
import org.exoplatform.services.security.IdentityConstants;
import org.exoplatform.social.core.manager.IdentityManager;

public class DomainServiceImpl implements DomainService {

  public static final String      GAMIFICATION_DOMAIN_LISTENER = "exo.gamification.domain.action";

  private static final Log        LOG                          = ExoLogger.getLogger(DomainServiceImpl.class);

  protected final DomainStorage   domainStorage;

  protected final ListenerService listenerService;

  protected final IdentityManager identityManager;

  protected final FileService     fileService;

  public DomainServiceImpl(DomainStorage domainStorage,
                           ListenerService listenerService,
                           IdentityManager identityManager,
                           FileService fileService) {
    this.domainStorage = domainStorage;
    this.listenerService = listenerService;
    this.identityManager = identityManager;
    this.fileService = fileService;
  }

  @Override
  public List<DomainDTO> getDomainsByFilter(DomainFilter domainFilter, int offset, int limit) {
    List<DomainDTO> domains = new ArrayList<>();
    List<Long> domainIds = domainStorage.getDomainsByFilter(domainFilter, offset, limit);
    for (Long domainId : domainIds) {
      DomainDTO domainDTO = getDomainById(domainId);
      domains.add(domainDTO);
    }
    return domains;
  }

  @Override
  public List<DomainDTO> getEnabledDomains() {
    return domainStorage.getEnabledDomains();
  }

  @Override
  public DomainDTO findEnabledDomainByTitle(String domainTitle) {
    if (StringUtils.isBlank(domainTitle)) {
      throw new IllegalArgumentException("domainTitle has to be not null");
    }
    return domainStorage.findEnabledDomainByTitle(domainTitle);
  }

  @Override
  public DomainDTO getDomainByTitle(String domainTitle) {
    if (StringUtils.isBlank(domainTitle)) {
      throw new IllegalArgumentException("domainTitle has to be not null");
    }
    return domainStorage.getDomainByTitle(domainTitle);
  }

  @Override
  public int countDomains(DomainFilter domainFilter) {
    return domainStorage.countDomains(domainFilter);
  }

  @Override
  @ExoTransactional
  public DomainDTO createDomain(DomainDTO domain, Identity aclIdentity) throws IllegalAccessException {
    if (domain == null) {
      throw new IllegalArgumentException("domain is mandatory");
    }
    if (domain.getId() != 0) {
      throw new IllegalArgumentException("domain id must be equal to 0");
    }
    if (!canAddDomain(aclIdentity)) {
      throw new IllegalAccessException("The user is not authorized to create a domain");
    }
    return createDomain(domain, aclIdentity.getUserId());
  }

  @Override
  @ExoTransactional
  public DomainDTO createDomain(DomainDTO domain) {
    return createDomain(domain, IdentityConstants.SYSTEM);
  }

  @Override
  @ExoTransactional
  public DomainDTO updateDomain(DomainDTO domain, Identity aclIdentity) throws IllegalAccessException, ObjectNotFoundException {
    if (!canUpdateDomain(domain.getId(), aclIdentity)) {
      throw new IllegalAccessException("The user is not authorized to update domain " + domain);
    }
    DomainDTO storedDomain = domainStorage.getDomainById(domain.getId());
    if (storedDomain == null) {
      throw new ObjectNotFoundException("domain doesn't exist");
    } else if (domain.equals(storedDomain)) {
      // No changes so no modifications needed
      return storedDomain;
    } else if (storedDomain.isDeleted()) {
      throw new IllegalAccessException("Domain is already marked as deleted");
    }
    domain.setLastModifiedBy(aclIdentity.getUserId());
    domain.setLastModifiedDate(Utils.toRFC3339Date(new Date(System.currentTimeMillis())));

    // Preserve non modifiable attributes
    domain.setType(storedDomain.getType());
    domain.setCreatedBy(storedDomain.getCreatedBy());
    domain.setCreatedDate(storedDomain.getCreatedDate());
    domain.setDeleted(storedDomain.isDeleted());
    domain.setCoverFileId(storedDomain.getCoverFileId());

    domain = domainStorage.saveDomain(domain);
    if (storedDomain.isEnabled() && !domain.isEnabled()) {
      broadcast(domain, "disable");
    }
    if (!storedDomain.isEnabled() && domain.isEnabled()) {
      broadcast(domain, "enable");
    }
    return domain;
  }

  @Override
  public DomainDTO deleteDomainById(long domainId, Identity aclIdentity) throws IllegalAccessException, ObjectNotFoundException {
    if (!canUpdateDomain(domainId, aclIdentity)) {
      throw new IllegalAccessException("The user is not authorized to create a domain");
    }
    String date = Utils.toRFC3339Date(new Date(System.currentTimeMillis()));
    DomainDTO domain = domainStorage.getDomainById(domainId);
    if (domain == null) {
      throw new ObjectNotFoundException("domain doesn't exist");
    }
    domain.setDeleted(true);
    domain.setLastModifiedDate(date);
    domain = domainStorage.saveDomain(domain);
    broadcast(domain, "delete");
    return domain;
  }

  @Override
  @ExoTransactional
  public DomainDTO getDomainById(long domainId) {
    if (domainId <= 0) {
      throw new IllegalArgumentException("domain id has to be positive integer");
    }
    return domainStorage.getDomainById(domainId);
  }

  @Override
  public InputStream getFileDetailAsStream(long domainId) throws ObjectNotFoundException {
    DomainDTO domain = domainStorage.getDomainById(domainId);
    if (domain == null) {
      throw new ObjectNotFoundException("Domain with id " + domainId + " doesn't exist");
    }
    if (domain.getCoverFileId() == 0) {
      throw new ObjectNotFoundException("Domain with id " + domainId + " doesn't have a coverdId");
    }
    FileItem fileItem;
    try {
      fileItem = fileService.getFile(domain.getCoverFileId());
      return fileItem == null || fileItem.getFileInfo() == null ? null : fileItem.getAsStream();
    } catch (Exception e) {
      LOG.warn("Error retrieving image with id {}", domainId, e);
      return null;
    }
  }

  @Override
  public boolean canAddDomain(Identity aclIdentity) {
    return isAdministrator(aclIdentity);
  }

  @Override
  public boolean canUpdateDomain(long domainId, Identity aclIdentity) {
    if (isAdministrator(aclIdentity)) {
      return true;
    } else if (aclIdentity == null) {
      return false;
    }
    DomainDTO domain = domainStorage.getDomainById(domainId);
    org.exoplatform.social.core.identity.model.Identity userIdentity =
                                                                     identityManager.getOrCreateUserIdentity(aclIdentity.getUserId());
    return domain != null && userIdentity != null && domain.getOwners() != null
        && domain.getOwners().contains(Long.parseLong(userIdentity.getId()));
  }

  private boolean isAdministrator(org.exoplatform.services.security.Identity identity) {
    return identity != null && identity.isMemberOf("/platform/administrators");
  }

  private void broadcast(DomainDTO domain, String operation) {
    try {
      listenerService.broadcast(GAMIFICATION_DOMAIN_LISTENER, domain, operation);
    } catch (Exception e) {
      LOG.warn("Error while broadcasting operation '{}' on domain {}. The operation '{}' isn't interrupted.",
               operation,
               domain,
               e);
    }
  }

  private DomainDTO createDomain(DomainDTO domain, String username) {
    if (StringUtils.isBlank(domain.getType())) {
      domain.setType(EntityType.AUTOMATIC.name());
    }
    domain.setCreatedBy(username);
    domain.setCreatedDate(Utils.toRFC3339Date(new Date(System.currentTimeMillis())));
    domain.setLastModifiedBy(username);
    domain.setLastModifiedDate(Utils.toRFC3339Date(new Date(System.currentTimeMillis())));
    return domainStorage.saveDomain(domain);
  }

}

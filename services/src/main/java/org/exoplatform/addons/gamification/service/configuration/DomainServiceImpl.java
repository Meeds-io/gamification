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
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import org.exoplatform.addons.gamification.service.dto.configuration.DomainDTO;
import org.exoplatform.addons.gamification.service.dto.configuration.DomainFilter;
import org.exoplatform.addons.gamification.service.dto.configuration.constant.EntityType;
import org.exoplatform.addons.gamification.storage.DomainStorage;
import org.exoplatform.addons.gamification.utils.Utils;
import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.exoplatform.commons.file.model.FileItem;
import org.exoplatform.commons.file.services.FileService;
import org.exoplatform.services.listener.ListenerService;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.security.Identity;
import org.exoplatform.services.security.IdentityConstants;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.space.spi.SpaceService;

public class DomainServiceImpl implements DomainService {

  private static final Log        LOG                                  = ExoLogger.getLogger(DomainServiceImpl.class);

  protected final DomainStorage   domainStorage;

  protected final ListenerService listenerService;

  protected final IdentityManager identityManager;

  protected final SpaceService    spaceService;

  protected final FileService     fileService;

  public DomainServiceImpl(DomainStorage domainStorage,
                           ListenerService listenerService,
                           IdentityManager identityManager,
                           SpaceService spaceService, FileService fileService) {
    this.domainStorage = domainStorage;
    this.listenerService = listenerService;
    this.identityManager = identityManager;
    this.spaceService = spaceService;
    this.fileService = fileService;
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<DomainDTO> getDomainsByFilter(DomainFilter domainFilter, String username, int offset, int limit) {
    List<DomainDTO> domains = new ArrayList<>();
    List<Long> domainIds;
    if (!Utils.isSuperManager(username)) {
      List<String> spaceIds = spaceService.getMemberSpacesIds(username, 0, -1);
      if (spaceIds.isEmpty()) {
        return Collections.emptyList();
      }
      List<Long> userSpaceIds = spaceIds.stream().map(Long::parseLong).toList();
      if (CollectionUtils.isNotEmpty(domainFilter.getSpacesIds())) {
        userSpaceIds = (List<Long>) CollectionUtils.intersection(userSpaceIds, domainFilter.getSpacesIds());
      }
      domainFilter.setSpacesIds(userSpaceIds);
    }
    if (domainFilter.isSortByBudget()) {
      domainIds = domainStorage.findHighestBudgetDomainIdsBySpacesIds(domainFilter.getSpacesIds(), offset, limit);
    } else {
      domainIds = domainStorage.getDomainsByFilter(domainFilter, offset, limit);
    }
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
  public DomainDTO getDomainByTitle(String domainTitle) {
    if (StringUtils.isBlank(domainTitle)) {
      throw new IllegalArgumentException("domainTitle has to be not null");
    }
    return domainStorage.getDomainByTitle(domainTitle);
  }

  @Override
  @SuppressWarnings("unchecked")
  public int countDomains(DomainFilter domainFilter, String username) {
    if (!Utils.isSuperManager(username)) {
      List<String> spaceIds = spaceService.getMemberSpacesIds(username, 0, -1);
      if (spaceIds.isEmpty()) {
        return 0;
      }
      List<Long> userSpaceIds = spaceIds.stream().map(Long::parseLong).toList();
      if (CollectionUtils.isNotEmpty(domainFilter.getSpacesIds())) {
        userSpaceIds = (List<Long>) CollectionUtils.intersection(userSpaceIds, domainFilter.getSpacesIds());
      }
      domainFilter.setSpacesIds(userSpaceIds);
    }
    return domainStorage.countDomains(domainFilter);
  }

  @Override
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
    DomainDTO createdDomain = createDomain(domain, aclIdentity.getUserId());
    broadcast(GAMIFICATION_DOMAIN_CREATE_LISTENER, createdDomain, aclIdentity.getUserId());
    return createdDomain;
  }

  @Override
  public DomainDTO createDomain(DomainDTO domain) {
    DomainDTO createdDomain = createDomain(domain, IdentityConstants.SYSTEM);
    broadcast(GAMIFICATION_DOMAIN_CREATE_LISTENER, createdDomain, null);
    return createdDomain;
  }

  @Override
  public DomainDTO updateDomain(DomainDTO domain, Identity aclIdentity) throws IllegalAccessException, ObjectNotFoundException {
    DomainDTO storedDomain = domainStorage.getDomainById(domain.getId());
    if (storedDomain == null) {
      throw new ObjectNotFoundException("domain doesn't exist");
    }
    if (!isDomainOwner(domain.getId(), aclIdentity)) {
      throw new IllegalAccessException("The user is not authorized to update domain " + domain);
    }
    if (domain.equals(storedDomain)) {
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
      broadcast(GAMIFICATION_DOMAIN_DISABLE_LISTENER, domain, aclIdentity.getUserId());
    } else if (!storedDomain.isEnabled() && domain.isEnabled()) {
      broadcast(GAMIFICATION_DOMAIN_ENABLE_LISTENER, domain, aclIdentity.getUserId());
    } else {
      broadcast(GAMIFICATION_DOMAIN_UPDATE_LISTENER, domain, aclIdentity.getUserId());
    }
    return domain;
  }

  @Override
  public DomainDTO deleteDomainById(long domainId, Identity aclIdentity) throws IllegalAccessException, ObjectNotFoundException {
    String date = Utils.toRFC3339Date(new Date(System.currentTimeMillis()));
    DomainDTO domain = domainStorage.getDomainById(domainId);
    if (domain == null) {
      throw new ObjectNotFoundException("domain doesn't exist");
    }
    if (!isDomainOwner(domainId, aclIdentity)) {
      throw new IllegalAccessException("The user is not authorized to delete the domain");
    }
    domain.setDeleted(true);
    domain.setLastModifiedDate(date);
    domain = domainStorage.saveDomain(domain);
    broadcast(GAMIFICATION_DOMAIN_DELETE_LISTENER, domain, aclIdentity.getUserId());
    return domain;
  }

  @Override
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
    return aclIdentity != null && Utils.isSuperManager(aclIdentity.getUserId());
  }

  @Override
  public boolean isDomainOwner(long domainId, Identity aclIdentity) {
    org.exoplatform.social.core.identity.model.Identity userIdentity = identityManager.getOrCreateUserIdentity(aclIdentity.getUserId());
    DomainDTO domain = domainStorage.getDomainById(domainId);
    return domain != null && Utils.isProgramOwner(domain.getAudienceId(), domain.getOwners(), userIdentity);
  }

  private void broadcast(String eventName, DomainDTO domain, String userName) {
    try {
      listenerService.broadcast(eventName, domain, userName);
    } catch (Exception e) {
      LOG.warn("Error while broadcasting operation '{}' on domain {}. The operation '{}' isn't interrupted.",
               eventName,
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

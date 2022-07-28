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

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

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
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.manager.IdentityManager;

public class DomainServiceImpl implements DomainService {

  private static final Log        LOG                          = ExoLogger.getLogger(DomainServiceImpl.class);

  protected final DomainStorage   domainStorage;

  protected final ListenerService listenerService;

  protected final IdentityManager identityManager;

  protected final FileService     fileService;

  public static final String      GAMIFICATION_DOMAIN_LISTENER = "exo.gamification.domain.action";

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
  public List<DomainDTO> getAllDomains(int offset, int limit, DomainFilter filter) {
    return domainStorage.getAllDomains(offset, limit, filter);
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
  public DomainDTO addDomain(DomainDTO domain, String username, boolean isAdministrator) throws Exception {

    if (domain == null) {
      throw new IllegalArgumentException("domain is mandatory");
    }
    if (domain.getId() != 0) {
      throw new IllegalArgumentException("domain id must be equal to 0");
    }
    if (StringUtils.isBlank(domain.getType())) {
      domain.setType(EntityType.AUTOMATIC.name());
    } else if (domain.getOwners().isEmpty() && EntityType.MANUAL.name().equals(domain.getType())) {
      throw new IllegalArgumentException("domain owners must not be null or empty");
    }
    if (EntityType.MANUAL.name().equals(domain.getType())) {
      checkDomainPermissions(domain, username, isAdministrator);
    }
    domain.setCreatedBy(username);
    domain.setCreatedDate(Utils.toRFC3339Date(new Date(System.currentTimeMillis())));
    domain.setLastModifiedBy(username);
    domain.setLastModifiedDate(Utils.toRFC3339Date(new Date(System.currentTimeMillis())));
    return domainStorage.saveDomain(domain);
  }

  @Override
  public DomainDTO updateDomain(DomainDTO domain, String username, boolean isAdministrator) throws Exception {
    String date = Utils.toRFC3339Date(new Date(System.currentTimeMillis()));
    DomainDTO oldDomain = domainStorage.getDomainById(domain.getId());
    if (oldDomain == null) {
      throw new ObjectNotFoundException("domain is not exist");
    }
    if (EntityType.MANUAL.name().equals(domain.getType())) {
      checkDomainPermissions(oldDomain, username, isAdministrator);
    }
    domain.setLastModifiedDate(date);
    domain.setLastModifiedBy(username);
    domain.setLastModifiedDate(Utils.toRFC3339Date(new Date(System.currentTimeMillis())));
    domain = domainStorage.saveDomain(domain);
    if (oldDomain.isEnabled() && !domain.isEnabled()) {
      listenerService.broadcast(GAMIFICATION_DOMAIN_LISTENER, domain, "disable");
    }
    if (!oldDomain.isEnabled() && domain.isEnabled()) {
      listenerService.broadcast(GAMIFICATION_DOMAIN_LISTENER, domain, "enable");
    }
    return domain;
  }

  @Override
  public void deleteDomain(Long id, String username, boolean isAdministrator) throws Exception {
    String date = Utils.toRFC3339Date(new Date(System.currentTimeMillis()));
    DomainDTO domain = domainStorage.getDomainById(id);
    if (domain == null) {
      throw new ObjectNotFoundException("domain doesn't exist");
    }
    if (EntityType.MANUAL.name().equals(domain.getType())) {
      checkDomainPermissions(domain, username, isAdministrator);
    }
    domain.setDeleted(true);
    domain.setLastModifiedDate(date);
    domain = domainStorage.saveDomain(domain);
    try {
      listenerService.broadcast(GAMIFICATION_DOMAIN_LISTENER, domain, "delete");
    } catch (Exception e) {
      LOG.error("Error to delete domain {}", id, e);
    }
  }

  @Override
  public DomainDTO getDomainById(Long domainId) {
    if (domainId <= 0) {
      throw new IllegalArgumentException("domain id has to be positive integer");
    }
    return domainStorage.getDomainById(domainId);
  }

  @Override
  public int countDomains(DomainFilter domainFilter) {
    return domainStorage.countDomains(domainFilter);
  }

  @Override
  public InputStream getFileDetailAsStream(long coverId) throws IOException {
    if (coverId == 0) {
      return null;
    }
    FileItem fileItem;
    try {
      fileItem = fileService.getFile(coverId);
    } catch (Exception e) {
      LOG.warn("Error retrieving image with id {}", coverId, e);
      return null;
    }
    return fileItem == null || fileItem.getFileInfo() == null ? null : fileItem.getAsStream();
  }

  private void checkDomainPermissions(DomainDTO domain, String username, boolean isAdministrator) throws IllegalAccessException {
    Identity identity = identityManager.getOrCreateUserIdentity(username);
    if (!isAdministrator && !Utils.isProgramOwner(domain.getOwners(), Long.parseLong(identity.getId()))) {
      throw new IllegalAccessException("User " + username + " is not allowed to save/update domain");
    }
  }
}

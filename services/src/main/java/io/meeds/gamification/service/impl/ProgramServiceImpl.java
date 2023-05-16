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
package io.meeds.gamification.service.impl;

import java.io.InputStream;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

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

import io.meeds.gamification.constant.EntityType;
import io.meeds.gamification.model.ProgramDTO;
import io.meeds.gamification.model.filter.ProgramFilter;
import io.meeds.gamification.service.ProgramService;
import io.meeds.gamification.storage.ProgramStorage;
import io.meeds.gamification.utils.Utils;

public class ProgramServiceImpl implements ProgramService {

  private static final Log        LOG = ExoLogger.getLogger(ProgramServiceImpl.class);

  protected final ProgramStorage  programStorage;

  protected final ListenerService listenerService;

  protected final IdentityManager identityManager;

  protected final SpaceService    spaceService;

  protected final FileService     fileService;

  public ProgramServiceImpl(ProgramStorage programStorage,
                            ListenerService listenerService,
                            IdentityManager identityManager,
                            SpaceService spaceService,
                            FileService fileService) {
    this.programStorage = programStorage;
    this.listenerService = listenerService;
    this.identityManager = identityManager;
    this.spaceService = spaceService;
    this.fileService = fileService;
  }

  @Override
  public List<ProgramDTO> getProgramsByFilter(ProgramFilter domainFilter, String username, int offset,
                                              int limit) throws IllegalAccessException {// NOSONAR
    List<Long> domainIds = getProgramIdsByFilter(domainFilter, username, offset, limit);
    return domainIds.stream().map(this::getProgramById).toList();
  }

  @Override
  public List<Long> getProgramIdsByFilter(ProgramFilter domainFilter,
                                          String username,
                                          int offset,
                                          int limit) throws IllegalAccessException {
    if (computeUserSpaces(domainFilter, username)) {
      if (domainFilter.isSortByBudget()) {
        return programStorage.findHighestBudgetDomainIdsBySpacesIds(domainFilter.getSpacesIds(), offset, limit);
      } else {
        return programStorage.getDomainsByFilter(domainFilter, offset, limit);
      }
    } else {
      return Collections.emptyList();
    }
  }

  @Override
  public List<ProgramDTO> getEnabledPrograms() {
    return programStorage.getEnabledDomains();
  }

  @Override
  public ProgramDTO getProgramByTitle(String domainTitle) {
    if (StringUtils.isBlank(domainTitle)) {
      throw new IllegalArgumentException("domainTitle has to be not null");
    }
    return programStorage.getProgramByTitle(domainTitle);
  }

  @Override
  public int countPrograms(ProgramFilter domainFilter, String username) throws IllegalAccessException {
    if (computeUserSpaces(domainFilter, username)) {
      return programStorage.countDomains(domainFilter);
    } else {
      return 0;
    }
  }

  @Override
  public ProgramDTO createProgram(ProgramDTO program, Identity aclIdentity) throws IllegalAccessException {
    if (program == null) {
      throw new IllegalArgumentException("domain is mandatory");
    }
    if (program.getId() != 0) {
      throw new IllegalArgumentException("domain id must be equal to 0");
    }
    if (program.isDeleted()) {
      throw new IllegalArgumentException("domain to create can't be marked as deleted");
    }
    if (!canAddProgram(aclIdentity)) {
      throw new IllegalAccessException("The user is not authorized to create a domain");
    }
    ProgramDTO createdDomain = createDomain(program, aclIdentity.getUserId());
    broadcast(GAMIFICATION_DOMAIN_CREATE_LISTENER, createdDomain, aclIdentity.getUserId());
    return createdDomain;
  }

  @Override
  public ProgramDTO createProgram(ProgramDTO domain) {
    ProgramDTO createdDomain = createDomain(domain, IdentityConstants.SYSTEM);
    broadcast(GAMIFICATION_DOMAIN_CREATE_LISTENER, createdDomain, null);
    return createdDomain;
  }

  @Override
  public ProgramDTO updateProgram(ProgramDTO program, Identity aclIdentity) throws IllegalAccessException,
                                                                            ObjectNotFoundException {
    ProgramDTO storedProgram = programStorage.getDomainById(program.getId());
    if (storedProgram == null) {
      throw new ObjectNotFoundException("Program doesn't exist");
    }
    if (storedProgram.isDeleted()) {
      throw new ObjectNotFoundException("Program is marked as deleted");
    }
    if (aclIdentity == null || !isProgramOwner(program.getId(), aclIdentity.getUserId())) {
      throw new IllegalAccessException("The user is not authorized to update domain " + program);
    }
    program.setLastModifiedBy(aclIdentity.getUserId());
    program.setLastModifiedDate(Utils.toRFC3339Date(new Date(System.currentTimeMillis())));

    // Preserve non modifiable attributes
    program.setType(storedProgram.getType());
    program.setCreatedBy(storedProgram.getCreatedBy());
    program.setCreatedDate(storedProgram.getCreatedDate());
    program.setDeleted(storedProgram.isDeleted());
    program.setCoverFileId(storedProgram.getCoverFileId());

    program = programStorage.saveDomain(program);
    if (storedProgram.isEnabled() && !program.isEnabled()) {
      broadcast(GAMIFICATION_DOMAIN_DISABLE_LISTENER, program, aclIdentity.getUserId());
    } else if (!storedProgram.isEnabled() && program.isEnabled()) {
      broadcast(GAMIFICATION_DOMAIN_ENABLE_LISTENER, program, aclIdentity.getUserId());
    } else {
      broadcast(GAMIFICATION_DOMAIN_UPDATE_LISTENER, program, aclIdentity.getUserId());
    }
    return getProgramById(program.getId());
  }

  @Override
  public ProgramDTO deleteProgramById(long domainId, Identity aclIdentity) throws IllegalAccessException,
                                                                           ObjectNotFoundException {
    String date = Utils.toRFC3339Date(new Date(System.currentTimeMillis()));
    ProgramDTO domain = programStorage.getDomainById(domainId);
    if (domain == null) {
      throw new ObjectNotFoundException("domain doesn't exist");
    }
    if (aclIdentity == null || !isProgramOwner(domainId, aclIdentity.getUserId())) {
      throw new IllegalAccessException("The user is not authorized to delete the domain");
    }
    domain.setDeleted(true);
    domain.setLastModifiedDate(date);
    domain = programStorage.saveDomain(domain);
    broadcast(GAMIFICATION_DOMAIN_DELETE_LISTENER, domain, aclIdentity.getUserId());
    return domain;
  }

  @Override
  public ProgramDTO getProgramById(long programId, String username) throws IllegalAccessException, ObjectNotFoundException {
    if (StringUtils.isBlank(username)) {
      throw new IllegalAccessException("Username is mandatory");
    }
    ProgramDTO program = getProgramById(programId);
    if (program == null) {
      throw new ObjectNotFoundException("Program doesn't exist");
    }
    if (program.isDeleted()) {
      throw new ObjectNotFoundException("Program has been deleted");
    }
    if (!isProgramMember(programId, username)
        || (!program.isEnabled() && !isProgramOwner(program.getId(), username))) {
      throw new IllegalAccessException("Program isn't accessible");
    }
    return program;
  }

  @Override
  public ProgramDTO getProgramById(long programId) {
    if (programId <= 0) {
      throw new IllegalArgumentException("Program id is mandatory");
    }
    return programStorage.getDomainById(programId);
  }

  @Override
  public InputStream getFileDetailAsStream(long domainId) throws ObjectNotFoundException {
    ProgramDTO domain = programStorage.getDomainById(domainId);
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
  public boolean canAddProgram(Identity aclIdentity) {
    return aclIdentity != null && Utils.isRewardingManager(aclIdentity.getUserId());
  }

  @Override
  public boolean isProgramOwner(long domainId, String username) {
    org.exoplatform.social.core.identity.model.Identity userIdentity = identityManager.getOrCreateUserIdentity(username);
    if (userIdentity == null || userIdentity.isDeleted() || !userIdentity.isEnable()) {
      return false;
    }
    ProgramDTO domain = programStorage.getDomainById(domainId);
    if (domain == null || domain.isDeleted()) {
      return false;
    }
    return Utils.isProgramOwner(domain.getAudienceId(), domain.getOwners(), userIdentity);
  }

  @Override
  public boolean isProgramMember(long domainId, String username) {
    org.exoplatform.social.core.identity.model.Identity userIdentity = identityManager.getOrCreateUserIdentity(username);
    if (userIdentity == null || userIdentity.isDeleted() || !userIdentity.isEnable()) {
      return false;
    }
    ProgramDTO program = programStorage.getDomainById(domainId);
    if (program == null) {
      return false;
    }

    return Utils.isRewardingManager(username)
        || Utils.isSpaceMember(program.getAudienceId(), username);
  }

  @SuppressWarnings("unchecked")
  private boolean computeUserSpaces(ProgramFilter domainFilter, String username) throws IllegalAccessException {
    if (Utils.isRewardingManager(username)) {
      domainFilter.setOwnerId(0);
      return true;
    }
    if (domainFilter.getOwnerId() > 0) {
      org.exoplatform.social.core.identity.model.Identity userIdentity = identityManager.getOrCreateUserIdentity(username);
      if (Long.parseLong(userIdentity.getId()) != domainFilter.getOwnerId()) {
        throw new IllegalAccessException();
      }

      List<Long> managedSpaceIds = spaceService.getManagerSpacesIds(username, 0, -1).stream().map(Long::parseLong).toList();
      if (CollectionUtils.isEmpty(managedSpaceIds)) {
        domainFilter.setSpacesIds(Collections.emptyList());
      } else {
        if (CollectionUtils.isNotEmpty(domainFilter.getSpacesIds())) {
          managedSpaceIds = (List<Long>) CollectionUtils.intersection(managedSpaceIds, domainFilter.getSpacesIds());
        }
        domainFilter.setSpacesIds(managedSpaceIds);
      }
    } else {
      List<Long> memberSpacesIds = spaceService.getMemberSpacesIds(username, 0, -1).stream().map(Long::parseLong).toList();
      if (CollectionUtils.isEmpty(memberSpacesIds)) {
        return false;
      }
      if (CollectionUtils.isNotEmpty(domainFilter.getSpacesIds())) {
        memberSpacesIds = (List<Long>) CollectionUtils.intersection(memberSpacesIds, domainFilter.getSpacesIds());
      }
      domainFilter.setSpacesIds(memberSpacesIds);
    }
    return true;
  }

  private void broadcast(String eventName, ProgramDTO domain, String userName) {
    try {
      listenerService.broadcast(eventName, domain, userName);
    } catch (Exception e) {
      LOG.warn("Error while broadcasting operation '{}' on domain {}. The operation '{}' isn't interrupted.",
               eventName,
               domain,
               e);
    }
  }

  private ProgramDTO createDomain(ProgramDTO domain, String username) {
    if (StringUtils.isBlank(domain.getType())) {
      domain.setType(EntityType.AUTOMATIC.name());
    }
    domain.setCreatedBy(username);
    domain.setCreatedDate(Utils.toRFC3339Date(new Date(System.currentTimeMillis())));
    domain.setLastModifiedBy(username);
    domain.setLastModifiedDate(Utils.toRFC3339Date(new Date(System.currentTimeMillis())));
    return programStorage.saveDomain(domain);
  }

}

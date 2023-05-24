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
import java.util.Set;

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
import org.exoplatform.social.core.space.model.Space;
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
  public List<ProgramDTO> getPrograms(ProgramFilter programFilter,
                                      String username,
                                      int offset,
                                      int limit) throws IllegalAccessException {
    List<Long> programIds = getProgramIds(programFilter, username, offset, limit);
    return programIds.stream().map(this::getProgramById).toList();
  }

  @Override
  public List<Long> getProgramIds(ProgramFilter programFilter,
                                  String username,
                                  int offset,
                                  int limit) throws IllegalAccessException {
    programFilter = computeUserSpaces(programFilter, username);
    if (programFilter == null) {
      return Collections.emptyList();
    } else {
      return getProgramIds(programFilter, offset, limit);
    }
  }

  @Override
  public List<Long> getProgramIds(ProgramFilter programFilter,
                                  int offset,
                                  int limit) {
    if (programFilter.isSortByBudget()) {
      return programStorage.findHighestBudgetProgramIdsBySpacesIds(programFilter.getSpacesIds(), offset, limit);
    } else {
      return programStorage.getProgramIdsByFilter(programFilter, offset, limit);
    }
  }

  @Override
  public ProgramDTO getProgramByTitle(String programTitle) {
    if (StringUtils.isBlank(programTitle)) {
      throw new IllegalArgumentException("programTitle has to be not null");
    }
    return programStorage.getProgramByTitle(programTitle);
  }

  @Override
  public int countPrograms(ProgramFilter programFilter, String username) throws IllegalAccessException {
    programFilter = computeUserSpaces(programFilter, username);
    if (programFilter == null) {
      return 0;
    } else {
      return countPrograms(programFilter);
    }
  }

  @Override
  public int countPrograms(ProgramFilter programFilter) {
    return programStorage.countPrograms(programFilter);
  }

  @Override
  public ProgramDTO createProgram(ProgramDTO program, Identity aclIdentity) throws IllegalAccessException {
    if (program == null) {
      throw new IllegalArgumentException("program is mandatory");
    }
    if (program.getId() != 0) {
      throw new IllegalArgumentException("program id must be equal to 0");
    }
    if (program.isDeleted()) {
      throw new IllegalArgumentException("program to create can't be marked as deleted");
    }
    if (!canAddProgram(aclIdentity)) {
      throw new IllegalAccessException("The user is not authorized to create a program");
    }
    ProgramDTO createdProgram = createProgram(program, aclIdentity.getUserId());
    broadcast(GAMIFICATION_DOMAIN_CREATE_LISTENER, createdProgram, aclIdentity.getUserId());
    return createdProgram;
  }

  @Override
  public ProgramDTO createProgram(ProgramDTO program) {
    ProgramDTO createdProgram = createProgram(program, IdentityConstants.SYSTEM);
    broadcast(GAMIFICATION_DOMAIN_CREATE_LISTENER, createdProgram, null);
    return createdProgram;
  }

  @Override
  public ProgramDTO updateProgram(ProgramDTO program, Identity aclIdentity) throws IllegalAccessException,
                                                                            ObjectNotFoundException {
    ProgramDTO storedProgram = programStorage.getProgramById(program.getId());
    if (storedProgram == null) {
      throw new ObjectNotFoundException("Program doesn't exist");
    }
    if (storedProgram.isDeleted()) {
      throw new ObjectNotFoundException("Program is marked as deleted");
    }
    if (aclIdentity == null || !isProgramOwner(program.getId(), aclIdentity.getUserId())) {
      throw new IllegalAccessException("The user is not authorized to update program " + program);
    }
    program.setLastModifiedBy(aclIdentity.getUserId());
    program.setLastModifiedDate(Utils.toRFC3339Date(new Date(System.currentTimeMillis())));

    // Preserve non modifiable attributes
    program.setType(storedProgram.getType());
    program.setCreatedBy(storedProgram.getCreatedBy());
    program.setCreatedDate(storedProgram.getCreatedDate());
    program.setDeleted(storedProgram.isDeleted());
    program.setCoverFileId(storedProgram.getCoverFileId());

    program = programStorage.saveProgram(program);
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
  public ProgramDTO deleteProgramById(long programId, Identity aclIdentity) throws IllegalAccessException,
                                                                           ObjectNotFoundException {
    String date = Utils.toRFC3339Date(new Date(System.currentTimeMillis()));
    ProgramDTO program = programStorage.getProgramById(programId);
    if (program == null) {
      throw new ObjectNotFoundException("program doesn't exist");
    }
    if (aclIdentity == null || !isProgramOwner(programId, aclIdentity.getUserId())) {
      throw new IllegalAccessException("The user is not authorized to delete the program");
    }
    program.setDeleted(true);
    program.setLastModifiedDate(date);
    program = programStorage.saveProgram(program);
    broadcast(GAMIFICATION_DOMAIN_DELETE_LISTENER, program, aclIdentity.getUserId());
    return program;
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
    return programStorage.getProgramById(programId);
  }

  @Override
  public InputStream getFileDetailAsStream(long programId) throws ObjectNotFoundException {
    ProgramDTO program = programStorage.getProgramById(programId);
    if (program == null) {
      throw new ObjectNotFoundException("program with id " + programId + " doesn't exist");
    }
    if (program.getCoverFileId() == 0) {
      throw new ObjectNotFoundException("program with id " + programId + " doesn't have a coverdId");
    }
    FileItem fileItem;
    try {
      fileItem = fileService.getFile(program.getCoverFileId());
      return fileItem == null || fileItem.getFileInfo() == null ? null : fileItem.getAsStream();
    } catch (Exception e) {
      LOG.warn("Error retrieving image with id {}", programId, e);
      return null;
    }
  }

  @Override
  public boolean canAddProgram(Identity aclIdentity) {
    return aclIdentity != null && Utils.isRewardingManager(aclIdentity.getUserId());
  }

  @Override
  public boolean isProgramOwner(long programId, String username) {
    org.exoplatform.social.core.identity.model.Identity userIdentity = identityManager.getOrCreateUserIdentity(username);
    if (userIdentity == null || userIdentity.isDeleted() || !userIdentity.isEnable()) {
      return false;
    }
    ProgramDTO program = programStorage.getProgramById(programId);
    if (program == null || program.isDeleted()) {
      return false;
    }
    return isProgramOwner(program.getAudienceId(), program.getOwnerIds(), userIdentity);
  }

  @Override
  public boolean isProgramMember(long programId, String username) {
    org.exoplatform.social.core.identity.model.Identity userIdentity = identityManager.getOrCreateUserIdentity(username);
    if (userIdentity == null || userIdentity.isDeleted() || !userIdentity.isEnable()) {
      return false;
    }
    ProgramDTO program = programStorage.getProgramById(programId);
    if (program == null) {
      return false;
    }

    return Utils.isRewardingManager(username)
        || isSpaceMember(program.getAudienceId(), username);
  }

  @SuppressWarnings("unchecked")
  private ProgramFilter computeUserSpaces(ProgramFilter programFilter, String username) throws IllegalAccessException { // NOSONAR
    programFilter = programFilter.clone();
    if (Utils.isRewardingManager(username)) {
      programFilter.setOwnerId(0);
      return programFilter;
    }
    if (programFilter.getOwnerId() > 0) {
      org.exoplatform.social.core.identity.model.Identity userIdentity = identityManager.getOrCreateUserIdentity(username);
      if (Long.parseLong(userIdentity.getId()) != programFilter.getOwnerId()) {
        throw new IllegalAccessException();
      }

      List<Long> managedSpaceIds = spaceService.getManagerSpacesIds(username, 0, -1).stream().map(Long::parseLong).toList();
      if (CollectionUtils.isEmpty(managedSpaceIds)) {
        programFilter.setSpacesIds(Collections.emptyList());
      } else {
        if (CollectionUtils.isNotEmpty(programFilter.getSpacesIds())) {
          managedSpaceIds = (List<Long>) CollectionUtils.intersection(managedSpaceIds, programFilter.getSpacesIds());
        }
        programFilter.setSpacesIds(managedSpaceIds);
      }
    } else {
      List<Long> memberSpacesIds = spaceService.getMemberSpacesIds(username, 0, -1).stream().map(Long::parseLong).toList();
      if (CollectionUtils.isEmpty(memberSpacesIds)) {
        return null;
      }
      if (CollectionUtils.isNotEmpty(programFilter.getSpacesIds())) {
        memberSpacesIds = (List<Long>) CollectionUtils.intersection(memberSpacesIds, programFilter.getSpacesIds());
      }
      programFilter.setSpacesIds(memberSpacesIds);
    }
    return programFilter;
  }

  private void broadcast(String eventName, ProgramDTO program, String userName) {
    try {
      listenerService.broadcast(eventName, program, userName);
    } catch (Exception e) {
      LOG.warn("Error while broadcasting operation '{}' on program {}. The operation '{}' isn't interrupted.",
               eventName,
               program,
               e);
    }
  }

  private ProgramDTO createProgram(ProgramDTO program, String username) {
    boolean isAutomatic = StringUtils.isBlank(username) || StringUtils.equals(IdentityConstants.SYSTEM, username);
    program.setType(isAutomatic ? EntityType.AUTOMATIC.name() : EntityType.MANUAL.name());
    program.setCreatedBy(username);
    program.setCreatedDate(Utils.toRFC3339Date(new Date(System.currentTimeMillis())));
    program.setLastModifiedBy(username);
    program.setLastModifiedDate(Utils.toRFC3339Date(new Date(System.currentTimeMillis())));
    return programStorage.saveProgram(program);
  }

  private boolean isProgramOwner(long spaceId, Set<Long> ownerIds,
                                 org.exoplatform.social.core.identity.model.Identity userIdentity) {
    if (userIdentity == null || userIdentity.isDeleted() || !userIdentity.isEnable()) {
      return false;
    }
    String username = userIdentity.getRemoteId();
    if (isSpaceManager(spaceId, username)) {
      return true;
    }
    if (isSpaceMember(spaceId, username)
        && ownerIds != null
        && ownerIds.contains(Long.parseLong(userIdentity.getId()))) {
      return true;
    }
    return Utils.isRewardingManager(username);
  }

  private boolean isSpaceManager(long spaceId, String username) {
    if (spaceService.isSuperManager(username)) {
      return true;
    }
    Space space = spaceService.getSpaceById(String.valueOf(spaceId));
    if (space == null) {
      return false;
    }
    return spaceService.isManager(space, username);
  }

  private boolean isSpaceMember(long spaceId, String username) {
    Space space = spaceService.getSpaceById(String.valueOf(spaceId));
    if (space == null) {
      return false;
    }
    return spaceService.isMember(space, username);
  }

}

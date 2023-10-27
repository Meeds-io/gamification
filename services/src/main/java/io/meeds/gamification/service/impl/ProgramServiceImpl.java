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
import org.exoplatform.services.listener.ListenerService;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.security.Identity;
import org.exoplatform.services.security.IdentityConstants;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.space.model.Space;
import org.exoplatform.social.core.space.spi.SpaceService;

import io.meeds.gamification.constant.EntityType;
import io.meeds.gamification.constant.EntityVisibility;
import io.meeds.gamification.model.ProgramColorAlreadyExists;
import io.meeds.gamification.model.ProgramDTO;
import io.meeds.gamification.model.filter.ProgramFilter;
import io.meeds.gamification.service.ProgramService;
import io.meeds.gamification.storage.ProgramStorage;
import io.meeds.gamification.utils.Utils;

@SuppressWarnings("deprecation")
public class ProgramServiceImpl implements ProgramService {

  private static final Log        LOG = ExoLogger.getLogger(ProgramServiceImpl.class);

  private static final String     PROGRAM_DOESN_T_EXIST = "Program doesn't exist";

  protected final ProgramStorage  programStorage;

  protected final ListenerService listenerService;

  protected final IdentityManager identityManager;

  protected final SpaceService    spaceService;

  public ProgramServiceImpl(ProgramStorage programStorage,
                            ListenerService listenerService,
                            IdentityManager identityManager,
                            SpaceService spaceService) {
    this.programStorage = programStorage;
    this.listenerService = listenerService;
    this.identityManager = identityManager;
    this.spaceService = spaceService;
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
    return getProgramIds(programFilter, offset, limit);
  }

  @Override
  public List<Long> getProgramIds(ProgramFilter programFilter,
                                  int offset,
                                  int limit) {
    if (programFilter.isSortByBudget()) {
      return programStorage.findHighestBudgetProgramIdsBySpacesIds(programFilter, offset, limit);
    } else {
      return programStorage.getProgramIdsByFilter(programFilter, offset, limit);
    }
  }

  @Override
  public List<Long> getOwnedProgramIds(String username, int offset, int limit) {
    if (StringUtils.isBlank(username)) {
      return Collections.emptyList();
    }
    org.exoplatform.social.core.identity.model.Identity userIdentity = identityManager.getOrCreateUserIdentity(username);
    long userIdentityId = Long.parseLong(userIdentity.getId());
    ProgramFilter programFilter = computeOwnedProgramsFilter(userIdentity.getRemoteId(), userIdentityId);
    return getProgramIds(programFilter, offset, limit);
  }

  @Override
  public List<Long> getMemberProgramIds(String username, int offset, int limit) {
    if (StringUtils.isBlank(username)) {
      return Collections.emptyList();
    }
    ProgramFilter programFilter = computeMemberProgramsFilter(username);
    return getProgramIds(programFilter, offset, limit);
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
    return countPrograms(programFilter);
  }

  @Override
  public int countPrograms(ProgramFilter programFilter) {
    return programStorage.countPrograms(programFilter);
  }

  @Override
  public int countOwnedPrograms(String username) {
    if (StringUtils.isBlank(username)) {
      return 0;
    }
    org.exoplatform.social.core.identity.model.Identity userIdentity = identityManager.getOrCreateUserIdentity(username);
    long userIdentityId = Long.parseLong(userIdentity.getId());
    ProgramFilter programFilter = computeOwnedProgramsFilter(userIdentity.getRemoteId(), userIdentityId);
    return countPrograms(programFilter);
  }

  @Override
  public int countMemberPrograms(String username) {
    if (StringUtils.isBlank(username)) {
      return 0;
    }
    ProgramFilter programFilter = computeMemberProgramsFilter(username);
    return countPrograms(programFilter);
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
      throw new ObjectNotFoundException(PROGRAM_DOESN_T_EXIST);
    }
    if (storedProgram.isDeleted()) {
      throw new ObjectNotFoundException("Program is marked as deleted");
    }

    if (aclIdentity == null
        || !isProgramOwner(storedProgram.getId(), aclIdentity.getUserId())) {
      throw new IllegalAccessException("The user is not authorized to update program " + program);
    }
    ProgramDTO programToSave = storedProgram.clone();
    programToSave.setLastModifiedBy(aclIdentity.getUserId());
    programToSave.setLastModifiedDate(Utils.toRFC3339Date(new Date(System.currentTimeMillis())));
    programToSave.setAvatarUploadId(program.getAvatarUploadId());
    programToSave.setCoverUploadId(program.getCoverUploadId());
    programToSave.setDescription(program.getDescription());
    programToSave.setTitle(program.getTitle());
    programToSave.setEnabled(program.isEnabled());
    programToSave.setOpen(program.isOpen());
    programToSave.setBudget(program.getBudget());
    programToSave.setOwnerIds(program.getOwnerIds());
    programToSave.setSpaceId(program.isOpen() ? 0 : program.getSpaceId());
    programToSave.setColor(program.getColor());

    return saveProgramAndBroadcast(programToSave, storedProgram, aclIdentity.getUserId());
  }

  @Override
  public ProgramDTO updateProgram(ProgramDTO program) throws ObjectNotFoundException {
    ProgramDTO storedProgram = programStorage.getProgramById(program.getId());
    if (storedProgram == null) {
      throw new ObjectNotFoundException(PROGRAM_DOESN_T_EXIST);
    }
    if (storedProgram.isDeleted()) {
      throw new ObjectNotFoundException("Program is marked as deleted");
    }
    if (program.isOpen()) {
      program.setSpaceId(0);
    }
    return saveProgramAndBroadcast(program, storedProgram, null);
  }

  @Override
  public void updateProgramDate(long programId) {
    programStorage.updateProgramDate(programId);
  }

  @Override
  public ProgramDTO deleteProgramById(long programId, Identity aclIdentity) throws IllegalAccessException,
                                                                            ObjectNotFoundException {
    ProgramDTO program = programStorage.getProgramById(programId);
    if (program == null) {
      throw new ObjectNotFoundException(PROGRAM_DOESN_T_EXIST);
    }
    if (aclIdentity == null || !isProgramOwner(programId, aclIdentity.getUserId())) {
      throw new IllegalAccessException("The user is not authorized to delete the program");
    }
    program.setDeleted(true);
    program.setVisibility(EntityVisibility.RESTRICTED);
    program = programStorage.saveProgram(program);
    broadcast(GAMIFICATION_DOMAIN_DELETE_LISTENER, program, aclIdentity.getUserId());
    return program;
  }

  @Override
  public void deleteProgramCoverById(long programId, Identity aclIdentity) throws ObjectNotFoundException,
                                                                           IllegalAccessException {
    ProgramDTO program = programStorage.getProgramById(programId);
    if (program == null) {
      throw new ObjectNotFoundException(PROGRAM_DOESN_T_EXIST);
    }
    if (aclIdentity == null || !isProgramOwner(programId, aclIdentity.getUserId())) {
      throw new IllegalAccessException("The user is not authorized to delete the program cover");
    }
    long coverFileId = program.getCoverFileId();
    if (coverFileId <= 0) {
      throw new ObjectNotFoundException("program cover doesn't exist");
    }
    programStorage.deleteImage(coverFileId);
    program.setCoverFileId(0);
    program = programStorage.saveProgram(program);
    broadcast(GAMIFICATION_DOMAIN_UPDATE_LISTENER, program, aclIdentity.getUserId());
  }

  @Override
  public void deleteProgramAvatarById(long programId, Identity aclIdentity) throws ObjectNotFoundException,
                                                                            IllegalAccessException {
    ProgramDTO program = programStorage.getProgramById(programId);
    if (program == null) {
      throw new ObjectNotFoundException(PROGRAM_DOESN_T_EXIST);
    }
    if (aclIdentity == null || !isProgramOwner(programId, aclIdentity.getUserId())) {
      throw new IllegalAccessException("The user is not authorized to delete the program avatar");
    }
    long avatarFileId = program.getAvatarFileId();
    if (avatarFileId <= 0) {
      throw new ObjectNotFoundException("program avatar doesn't exist");
    }
    programStorage.deleteImage(avatarFileId);
    program.setAvatarFileId(0);
    program = programStorage.saveProgram(program);
    broadcast(GAMIFICATION_DOMAIN_UPDATE_LISTENER, program, aclIdentity.getUserId());
  }

  @Override
  public ProgramDTO getProgramById(long programId, String username) throws IllegalAccessException, ObjectNotFoundException {
    ProgramDTO program = getProgramById(programId);
    if (program == null) {
      throw new ObjectNotFoundException(PROGRAM_DOESN_T_EXIST);
    }
    if (program.isDeleted()) {
      throw new ObjectNotFoundException("Program has been deleted");
    }
    if (!canViewProgram(program, username)) {
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
  public InputStream getProgramCoverStream(long programId) throws ObjectNotFoundException {
    ProgramDTO program = programStorage.getProgramById(programId);
    if (program == null) {
      throw new ObjectNotFoundException(String.format("program with id %s doesn't exist", programId));
    }
    if (program.getCoverFileId() == 0) {
      throw new ObjectNotFoundException(String.format("program with id %s doesn't have a coverId", programId));
    }
    return programStorage.getImageAsStream(program.getCoverFileId());
  }

  @Override
  public InputStream getProgramAvatarStream(long programId) throws ObjectNotFoundException {
    ProgramDTO program = programStorage.getProgramById(programId);
    if (program == null) {
      throw new ObjectNotFoundException("program with id " + programId + " doesn't exist");
    }
    if (program.getAvatarFileId() == 0) {
      throw new ObjectNotFoundException("program with id " + programId + " doesn't have an avatarId");
    }
    return programStorage.getImageAsStream(program.getAvatarFileId());
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
    return isProgramOwner(program.getSpaceId(),
                          program.getOwnerIds(),
                          program.isOpen(),
                          userIdentity);
  }

  @Override
  public boolean isProgramMember(long programId, String username) {
    org.exoplatform.social.core.identity.model.Identity userIdentity = identityManager.getOrCreateUserIdentity(username);
    if (userIdentity == null || userIdentity.isDeleted() || !userIdentity.isEnable()) {
      return false;
    }
    ProgramDTO program = programStorage.getProgramById(programId);
    if (program == null || program.isDeleted()) {
      return false;
    } else if (program.isOpen()) {
      return true;
    }

    return Utils.isRewardingManager(username)
        || isSpaceMember(program.getSpaceId(), username);
  }

  @Override
  public boolean canUseProgramColor(long programId, String color) {
    if (StringUtils.isBlank(color)) {
      return true;
    }
    ProgramDTO program = programId > 0 ? programStorage.getProgramById(programId) : null;
    return canUseProgramColor(color, program == null ? null : program.getColor());
  }

  @Override
  public boolean canViewProgram(long programId, String username) {
    ProgramDTO program = getProgramById(programId);
    return canViewProgram(program, username);
  }

  private boolean canViewProgram(ProgramDTO program, String username) {
    return program != null
           && (program.getVisibility() == EntityVisibility.OPEN || isProgramMember(program.getId(), username))
           && (program.isEnabled() || isProgramOwner(program.getId(), username));
  }

  @SuppressWarnings("unchecked")
  private ProgramFilter computeUserSpaces(ProgramFilter programFilter, String username) throws IllegalAccessException { // NOSONAR
    programFilter = programFilter.clone();
    if (Utils.isRewardingManager(username)) {
      programFilter.setOwnerId(0);
      programFilter.setAllSpaces(true);
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
    } else if (StringUtils.isNotBlank(username)) {
      List<Long> memberSpacesIds = spaceService.getMemberSpacesIds(username, 0, -1).stream().map(Long::parseLong).toList();
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
    if (program.isOpen()) {
      program.setSpaceId(0);
    }
    program.setVisibility(isSpaceOpen(program.getSpaceId()) ? EntityVisibility.OPEN : EntityVisibility.RESTRICTED);
    checkProgramColorUnicity(program.getColor(), null);
    return programStorage.saveProgram(program);
  }

  private void checkProgramColorUnicity(String newColor, String storedColor) {
    if (StringUtils.isNotBlank(newColor) && !canUseProgramColor(newColor, storedColor)) {
      throw new ProgramColorAlreadyExists();
    }
  }

  private boolean canUseProgramColor(String newColor, String storedColor) {
    return StringUtils.equals(newColor, storedColor) || !programStorage.isProgramColorExists(newColor);
  }

  private boolean isProgramOwner(long spaceId,
                                 Set<Long> ownerIds,
                                 boolean openProgram,
                                 org.exoplatform.social.core.identity.model.Identity userIdentity) {
    if (userIdentity == null || userIdentity.isDeleted() || !userIdentity.isEnable()) {
      return false;
    }
    String username = userIdentity.getRemoteId();
    if (Utils.isRewardingManager(username) || isSpaceManager(spaceId, username)) {
      return true;
    }
    return (openProgram || isSpaceMember(spaceId, username))
        && ownerIds != null
        && ownerIds.contains(Long.parseLong(userIdentity.getId()));
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
    if (StringUtils.isBlank(username)) {
      return false;
    }
    Space space = spaceService.getSpaceById(String.valueOf(spaceId));
    if (space == null) {
      return false;
    }
    return spaceService.isMember(space, username);
  }

  private boolean isSpaceOpen(long spaceId) {
    Space space = spaceId > 0 ? spaceService.getSpaceById(String.valueOf(spaceId)) : null;
    return space == null || Space.OPEN.equals(space.getRegistration());
  }

  private ProgramFilter computeOwnedProgramsFilter(String username, long userIdentityId) {
    ProgramFilter programFilter = new ProgramFilter();
    programFilter.setIncludeDeleted(true);
    if (Utils.isRewardingManager(username)) {
      programFilter.setAllSpaces(true);
    } else {
      programFilter.setOwnerId(userIdentityId);
      List<String> managedSpaceIds = spaceService.getManagerSpacesIds(username, 0, -1);
      if (CollectionUtils.isEmpty(managedSpaceIds)) {
        programFilter.setSpacesIds(Collections.emptyList());
      } else {
        programFilter.setSpacesIds(managedSpaceIds.stream().map(Long::parseLong).toList());
      }
    }
    return programFilter;
  }

  private ProgramFilter computeMemberProgramsFilter(String username) {
    ProgramFilter programFilter = new ProgramFilter();
    programFilter.setIncludeDeleted(true);
    if (Utils.isRewardingManager(username)) {
      programFilter.setAllSpaces(true);
    } else {
      List<String> memberSpaceIds = spaceService.getMemberSpacesIds(username, 0, -1);
      if (CollectionUtils.isEmpty(memberSpaceIds)) {
        programFilter.setSpacesIds(Collections.emptyList());
      } else {
        programFilter.setSpacesIds(memberSpaceIds.stream().map(Long::parseLong).toList());
      }
    }
    return programFilter;
  }

  private ProgramDTO saveProgramAndBroadcast(ProgramDTO program, ProgramDTO storedProgram, String username) {
    checkProgramColorUnicity(program.getColor(), storedProgram.getColor());
    program.setVisibility(isSpaceOpen(program.getSpaceId()) ? EntityVisibility.OPEN : EntityVisibility.RESTRICTED);
    program = programStorage.saveProgram(program);
    if (storedProgram.isEnabled() && !program.isEnabled()) {
      broadcast(GAMIFICATION_DOMAIN_DISABLE_LISTENER, program, username);
    } else if (!storedProgram.isEnabled() && program.isEnabled()) {
      broadcast(GAMIFICATION_DOMAIN_ENABLE_LISTENER, program, username);
    }
    broadcast(GAMIFICATION_DOMAIN_UPDATE_LISTENER, program, username);
    return getProgramById(program.getId());
  }

}

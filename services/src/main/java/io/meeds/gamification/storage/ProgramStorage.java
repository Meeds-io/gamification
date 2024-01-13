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

package io.meeds.gamification.storage;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import org.exoplatform.commons.file.model.FileItem;
import org.exoplatform.commons.file.services.FileService;
import org.exoplatform.commons.utils.ListAccess;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.organization.Group;
import org.exoplatform.services.organization.Membership;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.organization.User;
import org.exoplatform.services.organization.UserStatus;
import org.exoplatform.services.security.IdentityConstants;
import org.exoplatform.upload.UploadResource;
import org.exoplatform.upload.UploadService;

import io.meeds.gamification.dao.ProgramDAO;
import io.meeds.gamification.dao.RuleDAO;
import io.meeds.gamification.entity.ProgramEntity;
import io.meeds.gamification.model.ProgramDTO;
import io.meeds.gamification.model.filter.ProgramFilter;
import io.meeds.gamification.storage.mapper.ProgramMapper;
import io.meeds.gamification.utils.Utils;

import lombok.SneakyThrows;

public class ProgramStorage {

  private static final Log    LOG                 = ExoLogger.getLogger(ProgramStorage.class);

  private static final String FILE_API_NAME_SPACE = "gamification";

  protected final OrganizationService organizationService;

  private final ProgramDAO            programDAO;

  private final RuleDAO               ruleDAO;

  private final FileService           fileService;

  private final UploadService         uploadService;

  public ProgramStorage(FileService fileService,
                        UploadService uploadService,
                        ProgramDAO programDAO,
                        RuleDAO ruleDAO,
                        OrganizationService organizationService) {
    this.programDAO = programDAO;
    this.fileService = fileService;
    this.uploadService = uploadService;
    this.ruleDAO = ruleDAO;
    this.organizationService = organizationService;
  }

  public ProgramDTO saveProgram(ProgramDTO program) {
    ProgramEntity programEntity = ProgramMapper.toEntity(program);
    if (StringUtils.isNotBlank(program.getCoverUploadId())) {
      Long coverFileId = saveProgramImage(program.getCoverUploadId());
      programEntity.setCoverFileId(coverFileId);
    }
    if (StringUtils.isNotBlank(program.getAvatarUploadId())) {
      Long avatarFileId = saveProgramImage(program.getAvatarUploadId());
      programEntity.setAvatarFileId(avatarFileId);
    }
    if (CollectionUtils.isEmpty(programEntity.getOwners())) {
      programEntity.setOwners(new HashSet<>());
    } else {
      // Make Set modifiable to avoid Hibernate exception
      // which expects to have a modifiable collection
      programEntity.setOwners(new HashSet<>(programEntity.getOwners()));
    }
    if (programEntity.getId() == null || programEntity.getId() == 0) {
      programEntity.setId(null);
      programEntity.setCreatedDate(new Date());
      programEntity = programDAO.create(programEntity);
    } else {
      programEntity.setLastModifiedDate(new Date());
      programEntity = programDAO.update(programEntity);
    }
    return ProgramMapper.fromEntity(ruleDAO, programEntity);
  }

  public void updateProgramDate(long programId) {
    ProgramEntity programEntity = programDAO.find(programId);
    if (programEntity != null) {
      programEntity.setLastModifiedDate(new Date());
      programDAO.update(programEntity);
    }
  }

  public ProgramDTO getProgramByTitle(String programTitle) {
    return ProgramMapper.fromEntity(ruleDAO, programDAO.getProgramByTitle(programTitle));
  }

  public List<Long> findHighestBudgetProgramIdsBySpacesIds(ProgramFilter programFilter, int offset, int limit) {
    if (CollectionUtils.isNotEmpty(programFilter.getSpacesIds())) {
      return ruleDAO.findHighestBudgetProgramIdsBySpacesIds(programFilter.getSpacesIds(), offset, limit);
    } else if (programFilter.isAllSpaces()) {
      return ruleDAO.findHighestBudgetProgramIds(offset, limit);
    } else {
      return ruleDAO.findHighestBudgetOpenProgramIds(offset, limit);
    }
  }

  public List<Long> getProgramIdsByFilter(ProgramFilter filter, int offset, int limit) {
    return programDAO.getProgramIdsByFilter(offset, limit, filter);
  }

  public int countPrograms(ProgramFilter programFilter) {
    return programDAO.countPrograms(programFilter);
  }

  public ProgramDTO getProgramById(Long id) {
    ProgramEntity programEntity = programDAO.find(id);
    return ProgramMapper.fromEntity(ruleDAO, programEntity);
  }

  public boolean isProgramColorExists(String newColor) {
    return programDAO.countProgramColor(newColor) > 0;
  }

  public InputStream getImageAsStream(long fileId) {
    try {
      FileItem fileItem = fileService.getFile(fileId);
      return fileItem == null || fileItem.getFileInfo() == null ? null : fileItem.getAsStream();
    } catch (Exception e) {
      LOG.warn("Error retrieving image with id {}", fileId, e);
      return null;
    }
  }

  public void deleteImage(long fileId) {
    try {
      FileItem fileItem = fileService.getFile(fileId);
      if (fileItem != null && fileItem.getFileInfo() != null && !fileItem.getFileInfo().isDeleted()) {
        fileService.deleteFile(fileId);
      }
    } catch (Exception e) {
      LOG.warn("Error deleting file image with id {}", fileId, e);
    }
  }

  @SneakyThrows
  public List<String> getAdministrators() {
    Group rewardingGroup = organizationService.getGroupHandler().findGroupById(Utils.REWARDING_GROUP);
    if (rewardingGroup != null) {
      ListAccess<Membership> membershipsListAccess = organizationService.getMembershipHandler()
                                                                        .findAllMembershipsByGroup(rewardingGroup);
      int membershipSize = membershipsListAccess.getSize();
      if (membershipSize > 0) {
        Membership[] memberships = membershipsListAccess.load(0, membershipSize);
        return Arrays.stream(memberships)
                     .filter(Objects::nonNull)
                     .map(Membership::getUserName)
                     .filter(u -> findUserByName(u) != null)
                     .filter(Objects::nonNull)
                     .distinct()
                     .toList();
      }
    }
    return Collections.emptyList();
  }

  public void clearCache() {// NOSONAR
    // implemented in cached storage
  }

  private Long saveProgramImage(String uploadId) {
    if (uploadId == null || uploadId.isBlank()) {
      throw new IllegalArgumentException("uploadId is mandatory");
    }
    UploadResource uploadResource = uploadService.getUploadResource(uploadId);
    if (uploadResource == null) {
      throw new IllegalStateException("Can't find uploaded resource with id : " + uploadId);
    }
    try { // NOSONAR
      InputStream inputStream = new FileInputStream(uploadResource.getStoreLocation());
      FileItem fileItem = new FileItem(null,
                                       uploadResource.getFileName(),
                                       uploadResource.getMimeType(),
                                       FILE_API_NAME_SPACE,
                                       (long) uploadResource.getUploadedSize(),
                                       new Date(),
                                       IdentityConstants.SYSTEM,
                                       false,
                                       inputStream);
      fileItem = fileService.writeFile(fileItem);
      return fileItem != null && fileItem.getFileInfo() != null ? fileItem.getFileInfo().getId() : null;
    } catch (Exception e) {
      throw new IllegalStateException("Error while saving Program image file", e);
    } finally {
      uploadService.removeUploadResource(uploadResource.getUploadId());
    }
  }

  @SneakyThrows
  private User findUserByName(String u) {
    return organizationService.getUserHandler().findUserByName(u, UserStatus.ENABLED);
  }

}

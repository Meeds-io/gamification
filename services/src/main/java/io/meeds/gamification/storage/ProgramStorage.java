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
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import org.exoplatform.commons.file.model.FileItem;
import org.exoplatform.commons.file.services.FileService;
import org.exoplatform.services.security.IdentityConstants;
import org.exoplatform.upload.UploadResource;
import org.exoplatform.upload.UploadService;

import io.meeds.gamification.dao.ProgramDAO;
import io.meeds.gamification.dao.RuleDAO;
import io.meeds.gamification.entity.ProgramEntity;
import io.meeds.gamification.model.ProgramDTO;
import io.meeds.gamification.model.filter.ProgramFilter;
import io.meeds.gamification.utils.ProgramBuilder;

public class ProgramStorage {

  private static final String FILE_API_NAME_SPACE = "gamification";

  private final ProgramDAO    programDAO;

  private final RuleDAO       ruleDAO;

  private final FileService   fileService;

  private final UploadService uploadService;

  public ProgramStorage(FileService fileService,
                        UploadService uploadService,
                        ProgramDAO programDAO,
                        RuleDAO ruleDAO) {
    this.programDAO = programDAO;
    this.fileService = fileService;
    this.uploadService = uploadService;
    this.ruleDAO = ruleDAO;
  }

  public ProgramDTO saveProgram(ProgramDTO program) {
    ProgramEntity programEntity = ProgramBuilder.toEntity(program);
    if (StringUtils.isNotBlank(program.getCoverUploadId())) {
      Long coverFileId = saveProgramCover(program.getCoverUploadId());
      programEntity.setCoverFileId(coverFileId);
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
      programEntity = programDAO.create(programEntity);
    } else {
      programEntity = programDAO.update(programEntity);
    }
    return ProgramBuilder.fromEntity(ruleDAO, programEntity);
  }

  public ProgramDTO getProgramByTitle(String programTitle) {
    return ProgramBuilder.fromEntity(ruleDAO, programDAO.getProgramByTitle(programTitle));
  }

  public List<Long> findHighestBudgetProgramIdsBySpacesIds(List<Long> spacesIds, int offset, int limit) {
    if (CollectionUtils.isNotEmpty(spacesIds)) {
      return ruleDAO.findHighestBudgetProgramIdsBySpacesIds(spacesIds, offset, limit);
    } else {
      return ruleDAO.findHighestBudgetProgramIds(offset, limit);
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
    return ProgramBuilder.fromEntity(ruleDAO, programEntity);
  }

  public void clearCache() {// NOSONAR
    // implemented in cached storage
  }

  private Long saveProgramCover(String uploadId) {
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
      throw new IllegalStateException("Error while saving Program cover file", e);
    } finally {
      uploadService.removeUploadResource(uploadResource.getUploadId());
    }
  }
}

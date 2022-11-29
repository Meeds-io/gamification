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

package org.exoplatform.addons.gamification.storage;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import org.exoplatform.addons.gamification.entities.domain.configuration.DomainEntity;
import org.exoplatform.addons.gamification.entities.domain.configuration.DomainOwnerEntity;
import org.exoplatform.addons.gamification.service.dto.configuration.DomainDTO;
import org.exoplatform.addons.gamification.service.dto.configuration.DomainFilter;
import org.exoplatform.addons.gamification.service.mapper.DomainMapper;
import org.exoplatform.addons.gamification.storage.dao.DomainDAO;
import org.exoplatform.addons.gamification.storage.dao.DomainOwnerDAO;
import org.exoplatform.addons.gamification.storage.dao.RuleDAO;
import org.exoplatform.commons.file.model.FileItem;
import org.exoplatform.commons.file.services.FileService;
import org.exoplatform.services.security.IdentityConstants;
import org.exoplatform.upload.UploadResource;
import org.exoplatform.upload.UploadService;

public class DomainStorage {

  private static final String  FILE_API_NAME_SPACE = "gamification";

  private final DomainDAO      domainDAO;

  private final RuleDAO ruleDAO;

  private final DomainOwnerDAO domainOwnerDAO;

  private final FileService    fileService;

  private final UploadService  uploadService;

  public DomainStorage(DomainDAO domainDAO, DomainOwnerDAO domainOwnerDAO, FileService fileService, UploadService uploadService, RuleDAO ruleDAO) {
    this.domainDAO = domainDAO;
    this.domainOwnerDAO = domainOwnerDAO;
    this.fileService = fileService;
    this.uploadService = uploadService;
    this.ruleDAO = ruleDAO;
  }

  public DomainDTO saveDomain(DomainDTO domainDTO) {
    DomainEntity domainEntity = DomainMapper.domainDTOToDomainEntity(domainDTO);
    if (StringUtils.isNotBlank(domainDTO.getCoverUploadId())) {
      Long coverFileId = saveDomainCover(domainDTO.getCoverUploadId());
      domainEntity.setCoverFileId(coverFileId);
    }
    if (domainEntity.getId() == null || domainEntity.getId() == 0) {
      domainEntity.setId(null);
      domainEntity = domainDAO.create(domainEntity);
      saveDomainOwnerEntities(domainEntity, domainDTO.getOwners(), true);
    } else {
      domainEntity = domainDAO.update(domainEntity);
      saveDomainOwnerEntities(domainEntity, domainDTO.getOwners(), false);
    }
    return DomainMapper.domainEntityToDomainDTO(domainEntity, domainOwnerDAO);
  }

  public DomainDTO findEnabledDomainByTitle(String domainTitle) {
    return DomainMapper.domainEntityToDomainDTO(domainDAO.findEnabledDomainByTitle(domainTitle), domainOwnerDAO);
  }

  public DomainDTO getDomainByTitle(String domainTitle) {
    return DomainMapper.domainEntityToDomainDTO(domainDAO.getDomainByTitle(domainTitle), domainOwnerDAO);
  }

  public List<Long> getDomainsByFilter(DomainFilter filter, int offset, int limit) {
    if(filter.isSortByBudget()) {
      return ruleDAO.findHighestBudgetDomainIds(offset, limit);
    }
    return domainDAO.getDomainsByFilter(offset, limit, filter);
  }

  public int countDomains(DomainFilter domainFilter) {
    return domainDAO.countAllDomains(domainFilter);
  }

  public List<DomainDTO> getEnabledDomains() {
    return DomainMapper.domainsToDomainDTOs(domainDAO.getEnabledDomains(), domainOwnerDAO);
  }

  public DomainDTO getDomainById(Long id) {
    DomainEntity domainEntity = domainDAO.findByIdWithOwners(id);
    return DomainMapper.domainEntityToDomainDTO(domainEntity, domainOwnerDAO);
  }

  public void clearCache() {// NOSONAR
    // implemented in cached storage
  }

  private Long saveDomainCover(String uploadId) {
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
      throw new IllegalStateException("Error while saving domain cover file", e);
    } finally {
      uploadService.removeUploadResource(uploadResource.getUploadId());
    }
  }

  private void saveDomainOwnerEntities(DomainEntity domainEntity, Set<Long> owners, boolean isNew) {
    if (!isNew) {
      List<DomainOwnerEntity> storedOwners = domainOwnerDAO.getDomainOwners(domainEntity.getId());
      List<DomainOwnerEntity> domainOwnerEntities = new ArrayList<>(storedOwners);
      storedOwners.stream()
                  .filter(entity -> owners.stream().noneMatch(identityId -> identityId == entity.getIdentityId()))
                  .forEach(entity -> {
                    domainOwnerDAO.delete(entity);
                    domainOwnerEntities.remove(entity);
                  });
      if (owners != null) {
        owners.stream()
              .filter(identityId -> storedOwners.stream().noneMatch(otherEntity -> otherEntity.getIdentityId() == identityId))
              .forEach(identityId -> {
                DomainOwnerEntity entity = domainOwnerDAO.create(new DomainOwnerEntity(domainEntity, identityId));
                domainOwnerEntities.add(entity);
              });
      }
      domainEntity.setOwners(domainOwnerEntities);
    } else if (owners != null) {
      List<DomainOwnerEntity> domainOwnerEntities = new ArrayList<>();
      owners.stream().forEach(identityId -> {
        DomainOwnerEntity entity = domainOwnerDAO.create(new DomainOwnerEntity(domainEntity, identityId));
        domainOwnerEntities.add(entity);
      });
      domainEntity.setOwners(domainOwnerEntities);
    }
  }

}

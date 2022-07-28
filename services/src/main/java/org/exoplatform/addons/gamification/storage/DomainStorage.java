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
import java.util.*;

import org.exoplatform.addons.gamification.entities.domain.configuration.DomainOwnerEntity;
import org.exoplatform.addons.gamification.storage.dao.DomainOwnerDAO;
import org.exoplatform.commons.file.model.FileItem;

import org.exoplatform.addons.gamification.entities.domain.configuration.DomainEntity;
import org.exoplatform.addons.gamification.service.dto.configuration.DomainDTO;
import org.exoplatform.addons.gamification.service.dto.configuration.DomainFilter;
import org.exoplatform.addons.gamification.service.mapper.DomainMapper;
import org.exoplatform.addons.gamification.storage.dao.DomainDAO;
import org.exoplatform.commons.file.services.FileService;
import org.exoplatform.services.security.IdentityConstants;
import org.exoplatform.upload.UploadResource;
import org.exoplatform.upload.UploadService;

public class DomainStorage {

  private static final String FILE_API_NAME_SPACE = "Gamification";

  private DomainDAO           domainDAO;
  
  private DomainOwnerDAO      domainOwnerDAO;

  private FileService         fileService;

  private UploadService       uploadService;

  public DomainStorage(DomainDAO domainDAO, DomainOwnerDAO domainOwnerDAO, FileService fileService, UploadService uploadService) {
    this.domainDAO = domainDAO;
    this.domainOwnerDAO = domainOwnerDAO;
    this.fileService = fileService;
    this.uploadService = uploadService;
  }

  public DomainDTO saveDomain(DomainDTO domainDTO) throws Exception {
    DomainEntity domainEntity = DomainMapper.domainDTOToDomainEntity(domainDTO);
    Long coverFileId = saveDomainCover(domainDTO.getCoverUploadId());
    domainEntity.setCoverFileId(coverFileId);
    if (domainEntity.getId() == null || domainEntity.getId() == 0) {
      domainEntity.setId(null);
      domainEntity = domainDAO.create(domainEntity);
      List<DomainOwnerEntity> domainOwnerEntities = saveDomainOwnerEntities(domainEntity, domainDTO.getOwners());
      domainEntity.setOwners(domainOwnerEntities);
      domainDAO.update(domainEntity);
    } else {
      List<DomainOwnerEntity> domainOwnerEntities = updateDomainOwnerEntities(domainEntity, domainDTO.getOwners());
      domainEntity.setOwners(domainOwnerEntities);
      domainEntity = domainDAO.update(domainEntity);
    }
    return DomainMapper.domainEntityToDomainDTO(domainEntity);

  }

  public DomainDTO findEnabledDomainByTitle(String domainTitle) {
    return DomainMapper.domainEntityToDomainDTO(domainDAO.findEnabledDomainByTitle(domainTitle));
  }

  public DomainDTO getDomainByTitle(String domainTitle) {
    return DomainMapper.domainEntityToDomainDTO(domainDAO.getDomainByTitle(domainTitle));
  }

  public List<DomainDTO> getAllDomains(int offset, int limit, DomainFilter filter) {
    return DomainMapper.domainsToDomainDTOs(domainDAO.getAllDomains(offset, limit, filter));
  }

  public int countDomains(DomainFilter domainFilter) {
    return domainDAO.countAllDomains(domainFilter);
  }

  public List<DomainDTO> getEnabledDomains() {
    return DomainMapper.domainsToDomainDTOs(domainDAO.getEnabledDomains());
  }

  public DomainDTO getDomainById(Long id) {
    DomainEntity domainEntity = domainDAO.findByIdWithOwners(id);
    return DomainMapper.domainEntityToDomainDTO(domainEntity);
  }

  public void clearCache() {// NOSONAR
    // implemented in cached storage
  }

  private Long saveDomainCover(String uploadId) throws Exception {
    if (uploadId == null || uploadId.isBlank()) {
      return null;
    }
    UploadResource uploadResource = uploadService.getUploadResource(uploadId);
    if (uploadResource == null) {
      throw new IllegalStateException("Can't find uploaded resource with id : " + uploadId);
    }
    try { // NOSONAR
      InputStream inputStream = new FileInputStream(uploadResource.getStoreLocation());
      if (inputStream == null) {
        throw new IllegalStateException("inputStream is null");
      }
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
    } finally {
      uploadService.removeUploadResource(uploadResource.getUploadId());
    }
  }

  private List<DomainOwnerEntity> updateDomainOwnerEntities(DomainEntity domainEntity, Set<Long> owners){
    domainOwnerDAO.deleteDomainOwners(domainEntity.getId());
    return saveDomainOwnerEntities(domainEntity, owners);
  }
  private List<DomainOwnerEntity> saveDomainOwnerEntities(DomainEntity domainEntity, Set<Long> owners){
    if (owners== null || owners.isEmpty()) {
      return Collections.emptyList();
    }
    List<DomainOwnerEntity> ownersDomainOwnerEntities = new ArrayList<>();
    for (Long owner : owners) {
      DomainOwnerEntity domainOwnerEntity = domainOwnerDAO.create(toDomainOwnerEntity(domainEntity, owner));
      ownersDomainOwnerEntities.add(domainOwnerEntity);
    }
    return ownersDomainOwnerEntities;
  }
  private DomainOwnerEntity toDomainOwnerEntity(DomainEntity domainEntity, long identityId){
    return  new DomainOwnerEntity(domainEntity, identityId);
  }
}

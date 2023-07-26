/*
 * This file is part of the Meeds project (https://meeds.io/).
 *
 * Copyright (C) 2020 - 2023 Meeds Association contact@meeds.io
 *
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

import static io.meeds.gamification.storage.mapper.ConnectorHookMapper.*;
import static io.meeds.gamification.storage.mapper.ConnectorHookMapper.fromEntity;

import io.meeds.gamification.dao.ConnectorHookDAO;
import io.meeds.gamification.entity.ConnectorHookEntity;
import io.meeds.gamification.model.ConnectorHook;
import org.exoplatform.commons.ObjectAlreadyExistsException;
import org.exoplatform.commons.file.model.FileItem;
import org.exoplatform.commons.file.services.FileService;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import java.io.InputStream;
import java.util.Date;
import java.util.List;

public class ConnectorHookStorage {

  private static final Log       LOG = ExoLogger.getLogger(ConnectorHookStorage.class);

  private final ConnectorHookDAO connectorHookDAO;

  private final FileService      fileService;

  public ConnectorHookStorage(ConnectorHookDAO connectorHookDAO, FileService fileService) {
    this.connectorHookDAO = connectorHookDAO;
    this.fileService = fileService;
  }

  public ConnectorHook saveConnectorAccount(ConnectorHook connectorHook) throws ObjectAlreadyExistsException {
    ConnectorHook existsConnectorHook = getConnectorHook(connectorHook.getConnectorName(), connectorHook.getName());
    if (existsConnectorHook == null) {
      ConnectorHookEntity connectorHookEntity = toEntity(connectorHook);
      connectorHookEntity.setWatchDate(new Date());
      connectorHookEntity.setUpdatedDate(new Date());
      connectorHookEntity = connectorHookDAO.create(connectorHookEntity);
      return fromEntity(connectorHookEntity);
    } else {
      throw new ObjectAlreadyExistsException(existsConnectorHook);
    }
  }

  public ConnectorHook getConnectorHookById(Long id) {
    return fromEntity(connectorHookDAO.find(id));
  }

  public List<Long> getConnectorHookIds(String connectorName, int offset, int limit) {
    return connectorHookDAO.getConnectorHookIds(connectorName, offset, limit);
  }

  public ConnectorHook getConnectorHook(String connectorName, String name) {
    ConnectorHookEntity connectorHookEntity = connectorHookDAO.getConnectorHook(connectorName, name);
    return fromEntity(connectorHookEntity);
  }

  public ConnectorHook deleteConnectorHook(String connectorName, String name) {
    ConnectorHookEntity connectorHookEntity = connectorHookDAO.getConnectorHook(connectorName, name);
    if (connectorHookEntity != null) {
      connectorHookDAO.delete(connectorHookEntity);
    }
    return fromEntity(connectorHookEntity);
  }

  public String getConnectorHookSecret(String connectorName, long organizationRemoteId) {
    return connectorHookDAO.getConnectorHookSecret(connectorName, organizationRemoteId);
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
}

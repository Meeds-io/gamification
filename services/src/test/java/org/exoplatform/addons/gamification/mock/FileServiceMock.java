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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */

package org.exoplatform.addons.gamification.mock;

import org.exoplatform.commons.file.model.FileInfo;
import org.exoplatform.commons.file.model.FileItem;
import org.exoplatform.commons.file.services.FileService;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

public class FileServiceMock implements FileService {

  @Override
  public FileInfo getFileInfo(long id) {
    return new FileInfo(1l, "image", "image", "", 555, new Date(), "", "", false);
  }

  @Override
  public FileItem getFile(long id) {
    if (id != 1l) {
      throw new IllegalStateException("exception from test");
    }
    FileInfo fileInfo = new FileInfo(id, "image", "image", "", 555, new Date(System.currentTimeMillis()), "", "", false);
    FileItem fileItem;
    InputStream is = new ByteArrayInputStream("fileImageTest".getBytes());
    try {
      fileItem = new FileItem(fileInfo, is);
    } catch (Exception e) {
      return null;
    }
    return fileItem;
  }

  @Override
  public FileItem writeFile(FileItem file) {
    return null;
  }

  @Override
  public FileItem updateFile(FileItem file) {
    throw new UnsupportedOperationException();
  }

  @Override
  public FileInfo deleteFile(long id) {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<FileInfo> getFileInfoListByChecksum(String checksum) {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<FileItem> getFilesByChecksum(String checksum) {
    throw new UnsupportedOperationException();
  }
}

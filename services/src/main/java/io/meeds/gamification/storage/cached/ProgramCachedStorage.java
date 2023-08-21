/**
 * This file is part of the Meeds project (https://meeds.io/).
 *
 * Copyright (C) 2023 Meeds Association contact@meeds.io
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
package io.meeds.gamification.storage.cached;

import static io.meeds.gamification.utils.Utils.POST_CREATE_RULE_EVENT;
import static io.meeds.gamification.utils.Utils.POST_DELETE_RULE_EVENT;
import static io.meeds.gamification.utils.Utils.POST_UPDATE_RULE_EVENT;

import org.exoplatform.commons.cache.future.FutureExoCache;
import org.exoplatform.commons.cache.future.Loader;
import org.exoplatform.commons.file.services.FileService;
import org.exoplatform.services.cache.CacheService;
import org.exoplatform.services.cache.ExoCache;
import org.exoplatform.services.listener.Event;
import org.exoplatform.services.listener.Listener;
import org.exoplatform.services.listener.ListenerService;
import org.exoplatform.upload.UploadService;

import io.meeds.gamification.dao.ProgramDAO;
import io.meeds.gamification.dao.RuleDAO;
import io.meeds.gamification.model.ProgramDTO;
import io.meeds.gamification.storage.ProgramStorage;

public class ProgramCachedStorage extends ProgramStorage {

  private static final String                      PROGRAM_CACHE_NAME = "gamification.domain";

  private FutureExoCache<Long, ProgramDTO, Object> programFutureCache;

  public ProgramCachedStorage(FileService fileService,
                              UploadService uploadService,
                              ProgramDAO programDAO,
                              RuleDAO ruleDAO,
                              CacheService cacheService,
                              ListenerService listenerService) {
    super(fileService, uploadService, programDAO, ruleDAO);
    ExoCache<Long, ProgramDTO> programCache = cacheService.getCacheInstance(PROGRAM_CACHE_NAME);
    Loader<Long, ProgramDTO, Object> programLoader = new Loader<>() {
      @Override
      public ProgramDTO retrieve(Object context, Long id) throws Exception {
        return ProgramCachedStorage.super.getProgramById(id);
      }
    };
    this.programFutureCache = new FutureExoCache<>(programLoader, programCache);
    listenerService.addListener(POST_CREATE_RULE_EVENT, new RuleUpdatedListener());
    listenerService.addListener(POST_DELETE_RULE_EVENT, new RuleUpdatedListener());
    listenerService.addListener(POST_UPDATE_RULE_EVENT, new RuleUpdatedListener());
  }

  @Override
  public ProgramDTO saveProgram(ProgramDTO program) {
    try {
      program = super.saveProgram(program);
      return program;
    } finally {
      clearCache();
    }
  }

  @Override
  public void updateProgramDate(long programId) {
    try {
      super.updateProgramDate(programId);
    } finally {
      clearCache();
    }
  }

  @Override
  public ProgramDTO getProgramById(Long id) {
    ProgramDTO program = this.programFutureCache.get(null, id);
    return program == null ? null : program.clone();
  }

  @Override
  public void deleteImage(long fileId) {
    try {
      super.deleteImage(fileId);
    } finally {
      clearCache();
    }
  }

  @Override
  public void clearCache() {
    programFutureCache.clear();
  }

  public class RuleUpdatedListener extends Listener<Object, String> {
    @Override
    public void onEvent(Event<Object, String> event) throws Exception {
      clearCache();
    }
  }

}

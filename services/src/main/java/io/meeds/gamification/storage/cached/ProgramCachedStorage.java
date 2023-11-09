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

import java.util.List;

import org.exoplatform.commons.cache.future.FutureExoCache;
import org.exoplatform.commons.cache.future.Loader;
import org.exoplatform.commons.file.services.FileService;
import org.exoplatform.services.cache.CacheService;
import org.exoplatform.services.cache.ExoCache;
import org.exoplatform.services.listener.Event;
import org.exoplatform.services.listener.Listener;
import org.exoplatform.services.listener.ListenerService;
import org.exoplatform.services.organization.Membership;
import org.exoplatform.services.organization.MembershipEventListener;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.upload.UploadService;

import io.meeds.gamification.dao.ProgramDAO;
import io.meeds.gamification.dao.RuleDAO;
import io.meeds.gamification.model.ProgramDTO;
import io.meeds.gamification.storage.ProgramStorage;
import io.meeds.gamification.utils.Utils;

public class ProgramCachedStorage extends ProgramStorage {

  private static final String                      PROGRAM_CACHE_NAME = "gamification.domain";

  private FutureExoCache<Long, ProgramDTO, Object> programFutureCache;

  private List<String>                             administrators;

  public ProgramCachedStorage(FileService fileService,
                              UploadService uploadService,
                              ProgramDAO programDAO,
                              RuleDAO ruleDAO,
                              CacheService cacheService,
                              ListenerService listenerService,
                              OrganizationService organizationService) {
    super(fileService, uploadService, programDAO, ruleDAO, organizationService);
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
    organizationService.getMembershipHandler().addMembershipEventListener(new RewardingAdministratorMembershipListener());
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
  public List<String> getAdministrators() {
    if (this.administrators == null) {
      this.administrators = super.getAdministrators();
    }
    return administrators;
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

  public class RewardingAdministratorMembershipListener extends MembershipEventListener {
    @Override
    public void postSave(Membership m, boolean isNew) throws Exception {
      clearCachedAdministrators(m);
    }

    @Override
    public void postDelete(Membership m) throws Exception {
      clearCachedAdministrators(m);
    }

    private void clearCachedAdministrators(Membership m) {
      if (m != null && Utils.REWARDING_GROUP.equals(m.getGroupId())) {
        ProgramCachedStorage.this.administrators = null;
      }
    }
  }

}

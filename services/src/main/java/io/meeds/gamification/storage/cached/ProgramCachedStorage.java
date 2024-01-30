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
import java.util.concurrent.CompletableFuture;

import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.collections4.CollectionUtils;

import org.exoplatform.commons.cache.future.FutureCache;
import org.exoplatform.commons.cache.future.FutureExoCache;
import org.exoplatform.commons.cache.future.Loader;
import org.exoplatform.commons.file.services.FileService;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.container.RootContainer.PortalContainerPostCreateTask;
import org.exoplatform.services.cache.CacheService;
import org.exoplatform.services.cache.ExoCache;
import org.exoplatform.services.listener.Event;
import org.exoplatform.services.listener.Listener;
import org.exoplatform.services.listener.ListenerService;
import org.exoplatform.services.organization.Membership;
import org.exoplatform.services.organization.MembershipEventListener;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.organization.User;
import org.exoplatform.services.organization.UserEventListener;
import org.exoplatform.upload.UploadService;

import io.meeds.common.ContainerTransactional;
import io.meeds.gamification.dao.ProgramDAO;
import io.meeds.gamification.dao.RuleDAO;
import io.meeds.gamification.model.ProgramDTO;
import io.meeds.gamification.storage.ProgramStorage;
import io.meeds.gamification.utils.Utils;

import javax.servlet.ServletContext;
import lombok.SneakyThrows;

public class ProgramCachedStorage extends ProgramStorage {

  private static final String                       PROGRAM_CACHE_NAME = "gamification.domain";

  private FutureExoCache<Long, ProgramDTO, Object>  programFutureCache;

  private FutureCache<Object, List<String>, Object> administratorsRetrivalTask;

  private List<String>                              administrators;

  public ProgramCachedStorage(FileService fileService, // NOSONAR
                              UploadService uploadService,
                              ProgramDAO programDAO,
                              RuleDAO ruleDAO,
                              CacheService cacheService,
                              ListenerService listenerService,
                              OrganizationService organizationService,
                              PortalContainer container) {
    super(fileService, uploadService, programDAO, ruleDAO, organizationService);
    ExoCache<Long, ProgramDTO> programCache = cacheService.getCacheInstance(PROGRAM_CACHE_NAME);
    Loader<Long, ProgramDTO, Object> programLoader = new Loader<>() {
      @Override
      public ProgramDTO retrieve(Object context, Long id) throws Exception {
        return ProgramCachedStorage.super.getProgramById(id);
      }
    };
    this.programFutureCache = new FutureExoCache<>(programLoader, programCache);
    this.administratorsRetrivalTask = new FutureCache<Object, List<String>, Object>((c, k) -> getAdministratorsConcurrently()) {
      @Override
      protected List<String> get(Object key) {
        return administrators;
      }

      @Override
      protected void put(Object key, List<String> value) {
        administrators = value;
      }

      @Override
      protected void putOnly(Object key, List<String> value) {
        administrators = value;
      }
    };

    listenerService.addListener(POST_CREATE_RULE_EVENT, new RuleUpdatedListener());
    listenerService.addListener(POST_DELETE_RULE_EVENT, new RuleUpdatedListener());
    listenerService.addListener(POST_UPDATE_RULE_EVENT, new RuleUpdatedListener());
    organizationService.getMembershipHandler().addMembershipEventListener(new RewardingAdministratorMembershipListener());
    organizationService.getUserHandler().addUserEventListener(new RewardingAdministratorUserListener());
    PortalContainer.addInitTask(container.getPortalContext(), new PortalContainerPostCreateTask() {
      @Override
      public void execute(ServletContext context, PortalContainer portalContainer) {
        // Populate cache on startup
        getAdministrators();
      }
    });
  }

  @Override
  public void stop() {

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
  @SneakyThrows
  public List<String> getAdministrators() {
    if (this.administrators == null) {
      return administratorsRetrivalTask.get(null, "administrators");
    } else {
      return administrators;
    }
  }

  @Override
  public void clearCache() {
    programFutureCache.clear();
  }

  @ContainerTransactional
  public List<String> getAdministratorsConcurrently() {
    return super.getAdministrators();
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
      CompletableFuture.runAsync(() -> clearCachedAdministrators(m));
    }

    @Override
    public void postDelete(Membership m) throws Exception {
      CompletableFuture.runAsync(() -> clearCachedAdministrators(m));
    }

    private void clearCachedAdministrators(Membership m) {
      if (m != null && Utils.REWARDING_GROUP.equals(m.getGroupId())) {
        administrators = null;
      }
    }
  }

  public class RewardingAdministratorUserListener extends UserEventListener {

    @Override
    public void postSetEnabled(User user) throws Exception {
      CompletableFuture.runAsync(() -> clearCachedAdministrator(user));
    }

    @Override
    public void postDelete(User user) throws Exception {
      CompletableFuture.runAsync(() -> clearCachedAdministrator(user));
    }

    @SneakyThrows
    @ContainerTransactional
    public void clearCachedAdministrator(User user) {
      if (administrators != null
          && (administrators.stream().anyMatch(u -> StringUtils.equals(u, user.getUserName()))
              || CollectionUtils.isNotEmpty(organizationService.getMembershipHandler()
                                                               .findMembershipsByUserAndGroup(user.getUserName(),
                                                                                              Utils.REWARDING_GROUP)))) {
        administrators = null;
      }
    }

  }

}

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

package org.exoplatform.addons.gamification.storage.cached;

import java.io.Serializable;
import java.util.List;

import org.exoplatform.addons.gamification.service.dto.configuration.CacheKey;
import org.exoplatform.addons.gamification.service.dto.configuration.DomainDTO;
import org.exoplatform.addons.gamification.service.dto.configuration.DomainFilter;
import org.exoplatform.addons.gamification.service.dto.configuration.constant.EntityFilterType;
import org.exoplatform.addons.gamification.service.dto.configuration.constant.EntityStatusType;
import org.exoplatform.addons.gamification.storage.DomainStorage;
import org.exoplatform.addons.gamification.storage.dao.DomainDAO;
import org.exoplatform.addons.gamification.storage.dao.DomainOwnerDAO;
import org.exoplatform.commons.cache.future.FutureExoCache;
import org.exoplatform.commons.cache.future.Loader;
import org.exoplatform.commons.file.services.FileService;
import org.exoplatform.services.cache.CacheService;
import org.exoplatform.services.cache.ExoCache;
import org.exoplatform.upload.UploadService;

public class DomainCachedStorage extends DomainStorage {

  private static final int                               DOMAIN_ID_CONTEXT      = 0;

  private static final int                               DOMAIN_TITLE_CONTEXT   = 1;

  private static final int                               ALL_DOMAIN_CONTEXT     = 2;

  private static final int                               DOMAIN_ENABLED_CONTEXT = 3;

  private static final String                            DOMAIN_CACHE_NAME      = "gamification.domain";

  private FutureExoCache<Serializable, Object, CacheKey> domainFutureCache;

  public DomainCachedStorage(DomainDAO domainDAO,
                             DomainOwnerDAO domainOwnerDAO,
                             FileService fileService,
                             UploadService uploadService,
                             CacheService cacheService) {
    super(domainDAO, domainOwnerDAO, fileService, uploadService);
    ExoCache<Serializable, Object> domainCache = cacheService.getCacheInstance(DOMAIN_CACHE_NAME);
    Loader<Serializable, Object, CacheKey> domainLoader = new Loader<Serializable, Object, CacheKey>() {
      @Override
      public Object retrieve(CacheKey context, Serializable key) throws Exception {
        if (context.getContext() == DOMAIN_ID_CONTEXT) {
          return DomainCachedStorage.super.getDomainById(context.getId());
        } else if (context.getContext() == DOMAIN_TITLE_CONTEXT) {
          return DomainCachedStorage.super.findEnabledDomainByTitle(context.getTitle());
        } else if (context.getContext() == ALL_DOMAIN_CONTEXT) {
          return DomainCachedStorage.super.getAllDomains(context.getDomainFilter(), context.getOffset(), context.getLimit());
        } else if (context.getContext() == DOMAIN_ENABLED_CONTEXT) {
          return DomainCachedStorage.super.getEnabledDomains();
        } else {
          throw new IllegalStateException("Unknown context id " + context);
        }
      }
    };
    this.domainFutureCache = new FutureExoCache<>(domainLoader, domainCache);
  }

  @Override
  public DomainDTO saveDomain(DomainDTO domain) {
    try {
      domain = super.saveDomain(domain);
      return domain;
    } finally {
      clearCache();
    }
  }

  @Override
  public DomainDTO getDomainById(Long id) {
    CacheKey key = new CacheKey(DOMAIN_ID_CONTEXT, id);
    return (DomainDTO) this.domainFutureCache.get(key, key.hashCode());
  }

  @Override
  public DomainDTO findEnabledDomainByTitle(String title) {
    CacheKey key = new CacheKey(DOMAIN_TITLE_CONTEXT, title);
    return (DomainDTO) this.domainFutureCache.get(key, key.hashCode());
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<DomainDTO> getAllDomains(DomainFilter filter, int offset, int limit) {
    CacheKey key = new CacheKey(ALL_DOMAIN_CONTEXT, filter, offset, limit);
    return (List<DomainDTO>) this.domainFutureCache.get(key, key.hashCode());
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<DomainDTO> getEnabledDomains() {
    CacheKey key = new CacheKey(ALL_DOMAIN_CONTEXT, new DomainFilter(EntityFilterType.ALL, EntityStatusType.ENABLED, ""));

    return (List<DomainDTO>) this.domainFutureCache.get(key, key.hashCode());
  }

  @Override
  public void clearCache() {
    domainFutureCache.clear();
  }

}

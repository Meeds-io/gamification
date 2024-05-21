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

import java.io.Serializable;

import org.exoplatform.commons.cache.future.FutureExoCache;
import org.exoplatform.commons.cache.future.Loader;
import org.exoplatform.services.cache.CacheService;
import org.exoplatform.services.cache.ExoCache;

import io.meeds.gamification.dao.RealizationDAO;
import io.meeds.gamification.model.RealizationDTO;
import io.meeds.gamification.storage.ProgramStorage;
import io.meeds.gamification.storage.RealizationStorage;
import io.meeds.gamification.storage.RuleStorage;

public class RealizationCachedStorage extends RealizationStorage {

  public static final String                           REALIZATION_CACHE_NAME    = "gamification.realization";

  public static final int                              REALIZATION_ID_CONTEXT    = 0;

  public static final int                              REALIZATION_SCORE_CONTEXT = 1;

  private FutureExoCache<Serializable, Object, Object> realizationFutureCache;

  public RealizationCachedStorage(ProgramStorage programStorage,
                                  RuleStorage ruleStorage,
                                  RealizationDAO gamificationHistoryDAO,
                                  CacheService cacheService) {
    super(programStorage, ruleStorage, gamificationHistoryDAO);

    ExoCache<Serializable, Object> realizationCache = cacheService.getCacheInstance(REALIZATION_CACHE_NAME);
    Loader<Serializable, Object, Object> realizationLoader = new Loader<>() {
      @Override
      public Object retrieve(Object context, Serializable key) throws Exception {
        if (((int) context) == REALIZATION_SCORE_CONTEXT) {
          return RealizationCachedStorage.super.getScoreByIdentityId((String) key);
        } else if (((int) context) == REALIZATION_ID_CONTEXT) {
          return RealizationCachedStorage.super.getRealizationById((long) key);
        } else {
          throw new IllegalStateException("Unknown context id " + context);
        }
      }
    };
    this.realizationFutureCache = new FutureExoCache<>(realizationLoader, realizationCache);
  }

  @Override
  public long getScoreByIdentityId(String earnerIdentityId) {
    return (long) this.realizationFutureCache.get(REALIZATION_SCORE_CONTEXT, earnerIdentityId);
  }

  @Override
  public RealizationDTO getRealizationById(long id) {
    return (RealizationDTO) this.realizationFutureCache.get(REALIZATION_ID_CONTEXT, id);
  }

  @Override
  public RealizationDTO updateRealization(RealizationDTO realization) {
    realization = super.updateRealization(realization);
    if (realization != null) {
      this.realizationFutureCache.remove(realization.getEarnerId());
      this.realizationFutureCache.remove(realization.getId());
    }
    return realization;
  }

  @Override
  public RealizationDTO createRealization(RealizationDTO realization) {
    realization = super.createRealization(realization);
    if (realization != null) {
      this.realizationFutureCache.remove(realization.getEarnerId());
    }
    return realization;
  }

}

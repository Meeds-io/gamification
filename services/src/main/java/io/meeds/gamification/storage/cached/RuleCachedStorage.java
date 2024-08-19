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
import java.util.List;

import org.exoplatform.commons.cache.future.FutureExoCache;
import org.exoplatform.commons.cache.future.Loader;
import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.exoplatform.services.cache.CacheService;
import org.exoplatform.services.cache.CachedObjectSelector;
import org.exoplatform.services.cache.ExoCache;
import org.exoplatform.services.cache.ObjectCacheInfo;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import io.meeds.gamification.dao.ProgramDAO;
import io.meeds.gamification.dao.RuleDAO;
import io.meeds.gamification.model.RuleDTO;
import io.meeds.gamification.model.filter.RuleFilter;
import io.meeds.gamification.storage.EventStorage;
import io.meeds.gamification.storage.ProgramStorage;
import io.meeds.gamification.storage.RuleStorage;
import io.meeds.gamification.storage.cached.model.CacheKey;

public class RuleCachedStorage extends RuleStorage {

  public static final String                             RULE_CACHE_NAME               = "gamification.rule";

  public static final int                                RULE_ID_CONTEXT               = 0;

  public static final int                                RULE_TITLE_CONTEXT            = 1;

  public static final int                                RULES_BY_FILTER_CONTEXT       = 3;

  public static final int                                RULES_COUNT_BY_FILTER_CONTEXT = 4;

  private static final Log                               LOG                           = ExoLogger.getLogger(RuleCachedStorage.class);

  private ExoCache<Serializable, Object>                 ruleCache;

  private FutureExoCache<Serializable, Object, CacheKey> ruleFutureCache;

  public RuleCachedStorage(ProgramStorage programStorage,
                           EventStorage eventStorage,
                           ProgramDAO programDAO,
                           RuleDAO ruleDAO,
                           CacheService cacheService) {
    super(programStorage, eventStorage, programDAO, ruleDAO);
    this.ruleCache = cacheService.getCacheInstance(RULE_CACHE_NAME);
    this.ruleFutureCache = new FutureExoCache<>(new Loader<Serializable, Object, CacheKey>() {
      @Override
      public Object retrieve(CacheKey context, Serializable key) throws Exception {
        if (context.getContext() == RULE_ID_CONTEXT) {
          RuleDTO rule = RuleCachedStorage.super.findRuleById(context.getId());
          if (rule != null) {
            rule.setCacheTime(System.currentTimeMillis());
          }
          return rule;
        } else if (context.getContext() == RULE_TITLE_CONTEXT) {
          return RuleCachedStorage.super.findRuleByTitle(context.getTitle());
        } else if (context.getContext() == RULES_BY_FILTER_CONTEXT) {
          return RuleCachedStorage.super.findRuleIdsByFilter(context.getRuleFilter(), context.getOffset(), context.getLimit());
        } else if (context.getContext() == RULES_COUNT_BY_FILTER_CONTEXT) {
          return RuleCachedStorage.super.countRulesByFilter(context.getRuleFilter());
        } else {
          throw new IllegalStateException("Unknown context id " + context);
        }
      }
    }, this.ruleCache);
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<Long> findRuleIdsByFilter(RuleFilter ruleFilter, int offset, int limit) {
    CacheKey key = new CacheKey(RULES_BY_FILTER_CONTEXT, ruleFilter, offset, limit);
    return (List<Long>) this.ruleFutureCache.get(key, key);
  }

  @Override
  public int countRulesByFilter(RuleFilter ruleFilter) {
    CacheKey key = new CacheKey(RULES_COUNT_BY_FILTER_CONTEXT, ruleFilter);
    return (int) this.ruleFutureCache.get(key, key);
  }

  @Override
  public RuleDTO saveRule(RuleDTO ruleDTO) {
    RuleDTO oldRule = ruleDTO.getId() == null ? null : findRuleById(ruleDTO.getId());
    RuleDTO rule = super.saveRule(ruleDTO);
    if (oldRule != null) {
      clearCache(oldRule);
    }
    clearCache(rule);
    return rule;
  }

  @Override
  public RuleDTO findRuleById(Long id) {
    CacheKey key = new CacheKey(RULE_ID_CONTEXT, id);
    RuleDTO ruleDTO = (RuleDTO) this.ruleFutureCache.get(key, key.hashCode());
    return ruleDTO == null ? null : ruleDTO.clone();
  }

  @Override
  public RuleDTO findRuleByTitle(String title) {
    CacheKey key = new CacheKey(RULE_TITLE_CONTEXT, title);
    RuleDTO ruleDTO = (RuleDTO) this.ruleFutureCache.get(key, key.hashCode());
    return ruleDTO == null ? null : ruleDTO.clone();
  }

  @Override
  public RuleDTO deleteRuleById(long ruleId, String userId) throws ObjectNotFoundException {
    RuleDTO rule = super.deleteRuleById(ruleId, userId);
    clearCache(rule);
    return rule;
  }

  @Override
  public void clearCache() {
    this.ruleFutureCache.clear();
  }

  @Override
  public void clearCache(RuleDTO rule) {
    if (rule != null) {
      this.ruleFutureCache.remove(new CacheKey(RULE_ID_CONTEXT, rule.getId()).hashCode());
      this.ruleFutureCache.remove(new CacheKey(RULE_TITLE_CONTEXT, rule.getTitle()).hashCode());
    }
    clearListCache();
  }

  @Override
  public void clearListCache() {
    try {
      this.ruleCache.select(new CachedObjectSelector<>() {
        @Override
        public boolean select(Serializable key, ObjectCacheInfo<? extends Object> ocinfo) {
          return key instanceof CacheKey cacheKey
                 && (cacheKey.getContext() == RULES_BY_FILTER_CONTEXT
                     || cacheKey.getContext() == RULES_COUNT_BY_FILTER_CONTEXT);
        }

        @Override
        public void onSelect(ExoCache<? extends Serializable, ? extends Object> cache,
                             Serializable key,
                             ObjectCacheInfo<? extends Object> ocinfo) throws Exception {
          cache.remove(key);
        }
      });
    } catch (Exception e) {
      LOG.warn("Error while fine cache clearing, clear all gamification rule cache entries", e);
      this.ruleCache.clearCache();
    }
  }

}

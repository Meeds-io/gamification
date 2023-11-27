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

import io.meeds.gamification.storage.EventStorage;
import org.exoplatform.commons.cache.future.FutureExoCache;
import org.exoplatform.commons.cache.future.Loader;
import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.exoplatform.services.cache.CacheService;
import org.exoplatform.services.cache.ExoCache;

import io.meeds.gamification.constant.EntityType;
import io.meeds.gamification.dao.ProgramDAO;
import io.meeds.gamification.dao.RuleDAO;
import io.meeds.gamification.model.RuleDTO;
import io.meeds.gamification.storage.ProgramStorage;
import io.meeds.gamification.storage.RuleStorage;
import io.meeds.gamification.storage.cached.model.CacheKey;

public class RuleCachedStorage extends RuleStorage {

  private static final int                               RULE_ID_CONTEXT         = 0;

  private static final int                               RULE_TITLE_CONTEXT      = 1;

  private static final int                               RULES_BY_FILTER_CONTEXT = 3;

  private static final String                            RULE_CACHE_NAME         = "gamification.rule";

  private FutureExoCache<Serializable, Object, CacheKey> ruleFutureCache;

  public RuleCachedStorage(ProgramStorage programStorage,
                           EventStorage eventStorage,
                           ProgramDAO programDAO,
                           RuleDAO ruleDAO,
                           CacheService cacheService) {
    super(programStorage, eventStorage, programDAO, ruleDAO);
    ExoCache<Serializable, Object> ruleCache = cacheService.getCacheInstance(RULE_CACHE_NAME);
    Loader<Serializable, Object, CacheKey> ruleLoader = new Loader<Serializable, Object, CacheKey>() {
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
        } else {
          throw new IllegalStateException("Unknown context id " + context);
        }
      }
    };
    this.ruleFutureCache = new FutureExoCache<>(ruleLoader, ruleCache);
  }

  @Override
  public RuleDTO saveRule(RuleDTO ruleDTO) {
    try {
      return super.saveRule(ruleDTO);
    } finally {
      if (EntityType.MANUAL.equals(ruleDTO.getType())) {
        this.ruleFutureCache.clear();
      } else {
        clearCache(ruleDTO);
      }
    }
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
    this.ruleFutureCache.remove(new CacheKey(RULE_ID_CONTEXT, rule.getId()).hashCode());
    this.ruleFutureCache.remove(new CacheKey(RULE_TITLE_CONTEXT, rule.getTitle()).hashCode());
  }
}

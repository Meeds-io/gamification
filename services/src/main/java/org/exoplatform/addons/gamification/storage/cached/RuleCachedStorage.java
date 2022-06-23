package org.exoplatform.addons.gamification.storage.cached;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.exoplatform.addons.gamification.search.RuleSearchConnector;
import org.exoplatform.addons.gamification.service.dto.configuration.CacheKey;
import org.exoplatform.addons.gamification.service.dto.configuration.RuleDTO;
import org.exoplatform.addons.gamification.service.dto.configuration.RuleFilter;
import org.exoplatform.addons.gamification.storage.RuleStorage;
import org.exoplatform.addons.gamification.storage.dao.RuleDAO;
import org.exoplatform.commons.cache.future.FutureExoCache;
import org.exoplatform.commons.cache.future.Loader;
import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.exoplatform.services.cache.CacheService;
import org.exoplatform.services.cache.ExoCache;

@SuppressWarnings("unchecked")
public class RuleCachedStorage extends RuleStorage {

  private static final int                               RULE_ID_CONTEXT         = 0;

  private static final int                               RULE_TITLE_CONTEXT      = 1;

  private static final int                               ALL_RULE_CONTEXT        = 2;

  private static final int                               RULES_BY_FILTER_CONTEXT = 3;

  private static final String                            RULE_CACHE_NAME         = "gamification.rule";

  private FutureExoCache<Serializable, Object, CacheKey> ruleFutureCache;

  public RuleCachedStorage(RuleDAO ruleDAO, RuleSearchConnector ruleSearchConnector, CacheService cacheService) {
    super(ruleDAO, ruleSearchConnector);
    ExoCache<Serializable, Object> ruleCache = cacheService.getCacheInstance(RULE_CACHE_NAME);
    Loader<Serializable, Object, CacheKey> ruleLoader = new Loader<Serializable, Object, CacheKey>() {
      @Override
      public Object retrieve(CacheKey context, Serializable key) throws Exception {
        if (context.getContext() == RULE_ID_CONTEXT) {
          return RuleCachedStorage.super.findRuleById(context.getId());
        } else if (context.getContext() == RULE_TITLE_CONTEXT) {
          return RuleCachedStorage.super.findRuleByTitle(context.getTitle());
        } else if (context.getContext() == ALL_RULE_CONTEXT) {
          return RuleCachedStorage.super.findAllRules();
        } else if (context.getContext() == RULES_BY_FILTER_CONTEXT) {
          return RuleCachedStorage.super.findRulesByFilter(context.getFilter(), context.getOffset(), context.getLimit());
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
      ruleDTO = super.saveRule(ruleDTO);
      return ruleDTO;
    } finally {
      this.ruleFutureCache.remove(ruleDTO.getId());
      this.ruleFutureCache.remove(ruleDTO.getTitle());
      this.ruleFutureCache.remove(ALL_RULE_CONTEXT);
      this.ruleFutureCache.remove(RULES_BY_FILTER_CONTEXT);
    }
  }

  @Override
  public RuleDTO findRuleById(Long id) {
    CacheKey key = new CacheKey(RULE_ID_CONTEXT, id);
    return (RuleDTO) this.ruleFutureCache.get(key, key.hashCode());
  }

  @Override
  public RuleDTO findRuleByTitle(String title) {
    CacheKey key = new CacheKey(RULE_TITLE_CONTEXT, title);
    return (RuleDTO) this.ruleFutureCache.get(key, key.hashCode());
  }

  @Override
  public List<RuleDTO> findAllRules() {
    CacheKey key = new CacheKey(ALL_RULE_CONTEXT, 0L);
    return (List<RuleDTO>) this.ruleFutureCache.get(key, key.hashCode());
  }

  @Override
  public List<RuleDTO> findRulesByFilter(RuleFilter filter, int offset, int limit) {
    if (StringUtils.isBlank(filter.getTerm())) {
      CacheKey key = new CacheKey(RULES_BY_FILTER_CONTEXT, filter, offset, limit);
      return (List<RuleDTO>) this.ruleFutureCache.get(key, key.hashCode());
    } else {
      return super.findRulesByFilter(filter, offset, limit);
    }
  }

  @Override
  public RuleDTO deleteRule(long ruleId, boolean force) throws ObjectNotFoundException {
    RuleDTO rule = super.deleteRule(ruleId, force);
    this.ruleFutureCache.remove(rule.getId());
    this.ruleFutureCache.remove(rule.getTitle());
    this.ruleFutureCache.remove(ALL_RULE_CONTEXT);
    return rule;
  }

  @Override
  public void clearCache() {
    this.ruleFutureCache.clear();
  }
}

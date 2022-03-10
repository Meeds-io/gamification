package org.exoplatform.addons.gamification.storage.cached;

import org.exoplatform.addons.gamification.service.dto.configuration.RuleDTO;
import org.exoplatform.addons.gamification.storage.RuleStorage;
import org.exoplatform.addons.gamification.storage.dao.RuleDAO;
import org.exoplatform.commons.cache.future.FutureExoCache;
import org.exoplatform.commons.cache.future.Loader;
import org.exoplatform.services.cache.CacheService;
import org.exoplatform.services.cache.ExoCache;

import java.io.Serializable;
import java.util.List;

public class RuleCachedStorage extends RuleStorage {

  private static final int                              RULE_ID_CONTEXT    = 0;

  private static final int                              RULE_TITLE_CONTEXT = 1;

  private static final int                              ALL_RULE_CONTEXT   = 2;

  private static final String                           RULE_CACHE_NAME    = "gamification.rule";

  private FutureExoCache<Serializable, Object, Integer> ruleFutureCache;

  public RuleCachedStorage(RuleDAO ruleDAO, CacheService cacheService) {
    super(ruleDAO);
    ExoCache<Serializable, Object> ruleCache = cacheService.getCacheInstance(RULE_CACHE_NAME);
    Loader<Serializable, Object, Integer> ruleLoader = new Loader<Serializable, Object, Integer>() {
      @Override
      public Object retrieve(Integer context, Serializable key) throws Exception {
        if (context == RULE_ID_CONTEXT) {
          return RuleCachedStorage.super.findRuleById((Long) key);
        } else if (context == RULE_TITLE_CONTEXT) {
          return RuleCachedStorage.super.findRuleByTitle((String) key);
        } else if (context == ALL_RULE_CONTEXT) {
          return RuleCachedStorage.super.findAllRules();
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
      return ruleDTO ;
    } finally {
      this.ruleFutureCache.remove(ruleDTO.getId());
      this.ruleFutureCache.remove(ruleDTO.getTitle());
      this.ruleFutureCache.remove(0);
    }
  }

  @Override
  public RuleDTO findRuleById(Long id) {
    return (RuleDTO) this.ruleFutureCache.get(RULE_ID_CONTEXT, id);
  }

  @Override
  public RuleDTO findRuleByTitle(String title) {
    return (RuleDTO) this.ruleFutureCache.get(RULE_TITLE_CONTEXT, title);
  }

  @Override
  public List<RuleDTO> findAllRules() {
    return (List<RuleDTO>) this.ruleFutureCache.get(ALL_RULE_CONTEXT,0);
  }
  @Override
  public void deleteRule(RuleDTO rule) {
    try {
      super.deleteRule(rule);
    } finally {
      this.ruleFutureCache.remove(rule.getId());
      this.ruleFutureCache.remove(rule.getTitle());
      this.ruleFutureCache.remove(0);
    }  }
}

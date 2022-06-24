package org.exoplatform.addons.gamification.storage.cached;

import org.exoplatform.addons.gamification.entities.domain.configuration.RuleEntity;
import org.exoplatform.addons.gamification.service.dto.configuration.CacheKey;
import org.exoplatform.addons.gamification.service.dto.configuration.Challenge;
import org.exoplatform.addons.gamification.service.dto.configuration.RuleDTO;
import org.exoplatform.addons.gamification.storage.RuleStorage;
import org.exoplatform.addons.gamification.storage.dao.RuleDAO;
import org.exoplatform.commons.cache.future.FutureExoCache;
import org.exoplatform.commons.cache.future.Loader;
import org.exoplatform.services.cache.CacheService;
import org.exoplatform.services.cache.ExoCache;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("unchecked")
public class RuleCachedStorage extends RuleStorage {

  private static final int                               RULE_ID_CONTEXT        = 0;

  private static final int                               RULE_TITLE_CONTEXT     = 1;

  private static final int                               ALL_RULE_CONTEXT       = 2;

  private static final int                               CHALLENGE_USER_CONTEXT = 3;

  private static final int                               CHALLENGE_USER_DOMAIN_CONTEXT = 4;

  private static final String                            RULE_CACHE_NAME        = "gamification.rule";

  private FutureExoCache<Serializable, Object, CacheKey> ruleFutureCache;

  public RuleCachedStorage(RuleDAO ruleDAO, CacheService cacheService) {
    super(ruleDAO);
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
        } else if (context.getContext() == CHALLENGE_USER_CONTEXT) {
          return RuleCachedStorage.super.findAllChallengesByUser(context.getOffset(), context.getLimit(), context.getIds());
        } else if (context.getContext() == CHALLENGE_USER_DOMAIN_CONTEXT) {
          return RuleCachedStorage.super.findAllChallengesByUserByDomain(context.getDomainId(), context.getOffset(), context.getLimit(), context.getIds());
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
  public void deleteRule(RuleDTO rule) {
    try {
      super.deleteRule(rule);
    } finally {
      this.ruleFutureCache.remove(rule.getId());
      this.ruleFutureCache.remove(rule.getTitle());
      this.ruleFutureCache.remove(ALL_RULE_CONTEXT);
    }
  }

  @Override
  public List<RuleEntity> findAllChallengesByUser(int offset, int limit, List<Long> ids) {
    CacheKey key = new CacheKey(CHALLENGE_USER_CONTEXT, ids, offset, limit);
    return (List<RuleEntity>) this.ruleFutureCache.get(key, key.hashCode());
  }

  @Override
  public List<RuleEntity> findAllChallengesByUserByDomain(long domainId, int offset, int limit, List<Long> ids) {
    CacheKey key = new CacheKey(CHALLENGE_USER_DOMAIN_CONTEXT, ids, domainId, offset, limit);
    return (List<RuleEntity>) this.ruleFutureCache.get(key, key.hashCode());
  }

  @Override
  public Challenge saveChallenge(Challenge challenge, String username) {
    challenge = super.saveChallenge(challenge, username);
    this.ruleFutureCache.clear();
    return challenge;
  }
  @Override
  public void deleteChallenge(Challenge challenge) {
    super.deleteChallenge(challenge);
    this.ruleFutureCache.clear();
  }

  @Override
  public void clearCache() {
    this.ruleFutureCache.clear();
  }
}

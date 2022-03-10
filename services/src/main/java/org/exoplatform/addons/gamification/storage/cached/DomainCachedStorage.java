package org.exoplatform.addons.gamification.storage.cached;

import org.exoplatform.addons.gamification.service.dto.configuration.DomainDTO;
import org.exoplatform.addons.gamification.storage.DomainStorage;
import org.exoplatform.addons.gamification.storage.dao.DomainDAO;
import org.exoplatform.commons.cache.future.FutureExoCache;
import org.exoplatform.commons.cache.future.Loader;
import org.exoplatform.services.cache.CacheService;
import org.exoplatform.services.cache.ExoCache;

import java.io.Serializable;
import java.util.List;

public class DomainCachedStorage extends DomainStorage {

  private static final int                              DOMAIN_ID_CONTEXT      = 0;

  private static final int                              DOMAIN_TITLE_CONTEXT   = 1;

  private static final int                              ALL_DOMAIN_CONTEXT     = 2;

  private static final int                              DOMAIN_ENABLED_CONTEXT = 3;

  private static final String                           DOMAIN_CACHE_NAME      = "gamification.domain";

  private FutureExoCache<Serializable, Object, Integer> domainFutureCache;

  public DomainCachedStorage(DomainDAO domainDAO, CacheService cacheService) {
    super(domainDAO);
    ExoCache<Serializable, Object> domainCache = cacheService.getCacheInstance(DOMAIN_CACHE_NAME);
    Loader<Serializable, Object, Integer> domainLoader = new Loader<Serializable, Object, Integer>() {
      @Override
      public Object retrieve(Integer context, Serializable key) throws Exception {
        if (context == DOMAIN_ID_CONTEXT) {
          return DomainCachedStorage.super.getDomainByID((Long) key);
        } else if (context == DOMAIN_TITLE_CONTEXT) {
          return DomainCachedStorage.super.findDomainByTitle((String) key);
        } else if (context == ALL_DOMAIN_CONTEXT) {
          return DomainCachedStorage.super.getAllDomains();
        } else if (context == DOMAIN_ENABLED_CONTEXT) {
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
      this.domainFutureCache.remove(domain.getId());
      this.domainFutureCache.remove(domain.getTitle());
      this.domainFutureCache.remove(0);
      this.domainFutureCache.remove(1);
    }
  }

  @Override
  public DomainDTO getDomainByID(Long id) {
    return (DomainDTO) this.domainFutureCache.get(DOMAIN_ID_CONTEXT, id);
  }

  @Override
  public DomainDTO findDomainByTitle(String title) {
    return (DomainDTO) this.domainFutureCache.get(DOMAIN_TITLE_CONTEXT, title);
  }

  @Override
  public List<DomainDTO> getAllDomains() {
    return (List<DomainDTO>) this.domainFutureCache.get(ALL_DOMAIN_CONTEXT,0);
  }

  @Override
  public List<DomainDTO> getEnabledDomains() {
    return (List<DomainDTO>) this.domainFutureCache.get(DOMAIN_ENABLED_CONTEXT,1);
  }

  public void clearCache() {
    domainFutureCache.clear();
  }

}

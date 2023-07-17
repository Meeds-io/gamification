/*
 * This file is part of the Meeds project (https://meeds.io/).
 *
 * Copyright (C) 2020 - 2023 Meeds Association contact@meeds.io
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

import org.apache.commons.codec.binary.StringUtils;

import org.exoplatform.commons.ObjectAlreadyExistsException;
import org.exoplatform.commons.cache.future.FutureExoCache;
import org.exoplatform.commons.cache.future.Loader;
import org.exoplatform.services.cache.CacheService;
import org.exoplatform.services.cache.CachedObjectSelector;
import org.exoplatform.services.cache.ExoCache;
import org.exoplatform.services.cache.ObjectCacheInfo;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import io.meeds.gamification.dao.ConnectorAccountDAO;
import io.meeds.gamification.model.ConnectorAccount;
import io.meeds.gamification.storage.ConnectorAccountStorage;
import io.meeds.gamification.storage.cached.model.ConnectorAccountCachedKey;

public class ConnectorAccountCachedStorage extends ConnectorAccountStorage {

  private static final Log                                          LOG                            =
                                                                        ExoLogger.getLogger(ConnectorAccountCachedStorage.class);

  public static final String                                        CONNECTOR_CACHE_NAME           = "gamification.connectors";

  public static final String                                        CONNECTOR_ACCOUNT_BY_REMOTE_ID = "accountByRemoteId";

  public static final String                                        CONNECTOR_REMOTE_CONTEXT       = "remoteId";

  public static final String                                        CONNECTOR_LOCAL_CONTEXT        = "userId";

  private ExoCache<ConnectorAccountCachedKey, Object>               cacheInstance;

  private FutureExoCache<ConnectorAccountCachedKey, Object, String> futureCache;

  public ConnectorAccountCachedStorage(ConnectorAccountDAO connectorAccountDAO, CacheService cacheService) {
    super(connectorAccountDAO);

    this.cacheInstance = cacheService.getCacheInstance(CONNECTOR_CACHE_NAME);
    this.futureCache = new FutureExoCache<>(new Loader<>() {
      public Object retrieve(String context, ConnectorAccountCachedKey key) throws Exception {
        if (CONNECTOR_REMOTE_CONTEXT.equals(context)) {
          return ConnectorAccountCachedStorage.super.getConnectorRemoteId(key.getConnectorName(), key.getUserId());
        } else if (CONNECTOR_ACCOUNT_BY_REMOTE_ID.equals(context)) {
          return ConnectorAccountCachedStorage.super.getConnectorAccount(key.getConnectorName(), key.getUserId());
        } else if (CONNECTOR_LOCAL_CONTEXT.equals(context)) {
          return ConnectorAccountCachedStorage.super.getUserIdentityId(key.getConnectorName(), key.getRemoteId());
        } else {
          throw new UnsupportedOperationException();
        }
      }
    }, this.cacheInstance);
  }

  @Override
  public String getConnectorRemoteId(String connectorName, long userId) {
    ConnectorAccountCachedKey cacheKey = new ConnectorAccountCachedKey(connectorName, userId, false);
    try {
      String connectorRemoteId = (String) this.futureCache.get(CONNECTOR_REMOTE_CONTEXT, cacheKey);
      if (this.futureCache.get(new ConnectorAccountCachedKey(connectorName, connectorRemoteId)) == null && connectorRemoteId != null) {
        this.futureCache.put(new ConnectorAccountCachedKey(connectorName, connectorRemoteId), userId);
      }
      return connectorRemoteId;
    } catch (Exception e) {
      LOG.warn("Error when getting connector remote Id cache", e);
    }
    return null;
  }

  @Override
  public ConnectorAccount getConnectorAccount(String connectorName, long userId) {
    ConnectorAccountCachedKey cacheKey = new ConnectorAccountCachedKey(connectorName, userId, true);
    try {
      return (ConnectorAccount) this.futureCache.get(CONNECTOR_ACCOUNT_BY_REMOTE_ID, cacheKey);
    } catch (Exception e) {
      LOG.warn("Error when getting connector remote Id cache", e);
    }
    return null;
  }

  @Override
  public long getUserIdentityId(String connectorName, String connectorRemoteId) {
    ConnectorAccountCachedKey cacheKey = new ConnectorAccountCachedKey(connectorName, connectorRemoteId);
    try {
      return (long) this.futureCache.get(CONNECTOR_LOCAL_CONTEXT, cacheKey);
    } catch (Exception e) {
      LOG.warn("Error when getting connector user Id cache", e);
    }
    return 0;
  }

  @Override
  public void saveConnectorAccount(ConnectorAccount connectorAccount) throws ObjectAlreadyExistsException {
    try {
      super.saveConnectorAccount(connectorAccount);
    } finally {
      clearCacheEntries(connectorAccount.getConnectorName(), connectorAccount.getRemoteId());
      clearCacheEntries(connectorAccount.getConnectorName(), connectorAccount.getUserId());
    }
  }

  @Override
  public ConnectorAccount deleteConnectorAccount(String connectorName, String connectorRemoteId) {
    try {
      return super.deleteConnectorAccount(connectorName, connectorRemoteId);
    } finally {
      clearCacheEntries(connectorName, connectorRemoteId);
    }
  }

  public void clearCacheEntries(String connectorName, String connectorRemoteId) {
    try {
      this.cacheInstance.select(new CachedObjectSelector<ConnectorAccountCachedKey, Object>() {
        @Override
        public boolean select(ConnectorAccountCachedKey key, ObjectCacheInfo<? extends Object> ocinfo) {
          return StringUtils.equals(key.getConnectorName(), connectorName)
              && StringUtils.equals(key.getRemoteId(), connectorRemoteId);
        }

        @Override
        public void onSelect(ExoCache<? extends ConnectorAccountCachedKey, ? extends Object> cache,
                             ConnectorAccountCachedKey key,
                             ObjectCacheInfo<? extends Object> ocinfo) throws Exception {
          Long userIdentityId = (Long) cache.get(key);
          if (userIdentityId != null) {
            cache.remove(new ConnectorAccountCachedKey(connectorName, userIdentityId, false));
            cache.remove(new ConnectorAccountCachedKey(connectorName, userIdentityId, true));
          }
          cache.remove(key);
        }
      });
    } catch (Exception e) {
      LOG.warn("Unable to clean cache entry for connector {} and account name {}. Clean all cache entries to preserve coherence.",
               connectorName,
               connectorRemoteId,
               e);
      this.cacheInstance.clearCache();
    }
  }

  public void clearCacheEntries(String connectorName, long userIdentityId) {
    try {
      this.cacheInstance.select(new CachedObjectSelector<ConnectorAccountCachedKey, Object>() {
        @Override
        public boolean select(ConnectorAccountCachedKey key, ObjectCacheInfo<? extends Object> ocinfo) {
          return StringUtils.equals(key.getConnectorName(), connectorName) && key.getUserId() == userIdentityId;
        }

        @Override
        public void onSelect(ExoCache<? extends ConnectorAccountCachedKey, ? extends Object> cache,
                             ConnectorAccountCachedKey key,
                             ObjectCacheInfo<? extends Object> ocinfo) throws Exception {
          String remoteId = (String) cache.get(key);
          if (remoteId != null) {
            cache.remove(new ConnectorAccountCachedKey(connectorName, remoteId));
          }
          cache.remove(key);
        }
      });
    } catch (Exception e) {
      LOG.warn("Unable to clean cache entry for connector {} and user identity id {}. Clean all cache entries to preserve coherence.",
               connectorName,
               userIdentityId,
               e);
      this.cacheInstance.clearCache();
    }
  }
}

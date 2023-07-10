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

import io.meeds.gamification.dao.ConnectorAccountDAO;
import io.meeds.gamification.model.ConnectorAccount;
import io.meeds.gamification.storage.ConnectorAccountStorage;
import io.meeds.gamification.storage.cached.model.ConnectorAccountCachedKey;
import org.exoplatform.commons.cache.future.FutureExoCache;
import org.exoplatform.services.cache.CacheService;
import org.exoplatform.services.cache.ExoCache;

import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import java.io.Serializable;

public class ConnectorAccountCachedStorage extends ConnectorAccountStorage {

  private static final Log                                                          LOG                            =
                                                                                        ExoLogger.getLogger(ConnectorAccountCachedStorage.class);

  public static final String                                                        CONNECTOR_ACCOUNT_CACHE_NAME   =
                                                                                                                 "gamification.connectorAccount";

  public static final String                                                        CONNECTOR_REMOTE_ID_CACHE_NAME =
                                                                                                                   "gamification.connectorRemoteId";

  public static final String                                                        CONNECTOR_USER_ID_CACHE_NAME   =
                                                                                                                 "gamification.connectorUserId";

  private FutureExoCache<Serializable, ConnectorAccount, ConnectorAccountCachedKey> connectorAccountFutureCache;

  private FutureExoCache<Serializable, String, ConnectorAccountCachedKey>           connectorRemoteIdFutureCache;

  private FutureExoCache<Serializable, Long, ConnectorAccountCachedKey>             connectorUserIdFutureCache;

  public ConnectorAccountCachedStorage(ConnectorAccountDAO connectorAccountDAO, CacheService cacheService) {
    super(connectorAccountDAO);

    ExoCache<Serializable, ConnectorAccount> connectorAccountCache = cacheService.getCacheInstance(CONNECTOR_ACCOUNT_CACHE_NAME);
    ExoCache<Serializable, String> connectorRemoteIdCache = cacheService.getCacheInstance(CONNECTOR_REMOTE_ID_CACHE_NAME);
    ExoCache<Serializable, Long> connectorUserIdCache = cacheService.getCacheInstance(CONNECTOR_USER_ID_CACHE_NAME);
    this.connectorAccountFutureCache =
                                     new FutureExoCache<>((ConnectorAccountCachedKey context,
                                                           Serializable key) -> ConnectorAccountCachedStorage.super.getConnectorAccountById(context.getId()),
                                                          connectorAccountCache);

    this.connectorRemoteIdFutureCache =
                                      new FutureExoCache<>((ConnectorAccountCachedKey context,
                                                            Serializable key) -> ConnectorAccountCachedStorage.super.getConnectorRemoteId(context.getConnectorName(),
                                                                                                                                          context.getUserId()),
                                                           connectorRemoteIdCache);

    this.connectorUserIdFutureCache =
                                    new FutureExoCache<>((ConnectorAccountCachedKey context,
                                                          Serializable key) -> ConnectorAccountCachedStorage.super.getAssociatedUserId(context.getConnectorName(),
                                                                                                                                       context.getRemoteId()),
                                                         connectorUserIdCache);
  }

  @Override
  public ConnectorAccount getConnectorAccountById(long connectorAccountId) {
    ConnectorAccountCachedKey cacheKey = new ConnectorAccountCachedKey(connectorAccountId);
    ConnectorAccount connectorAccount = this.connectorAccountFutureCache.get(cacheKey, cacheKey.hashCode());
    return connectorAccount == null ? null : connectorAccount.clone();
  }

  @Override
  public String getConnectorRemoteId(String connectorName, long userId) {
    ConnectorAccountCachedKey cacheKey = new ConnectorAccountCachedKey(connectorName, userId);
    try {
      return this.connectorRemoteIdFutureCache.get(cacheKey, cacheKey.hashCode());
    } catch (Exception e) {
      LOG.warn("Error when getting connector remote Id cache", e);
    }
    return null;
  }

  @Override
  public long getAssociatedUserId(String connectorName, String connectorRemoteId) {
    ConnectorAccountCachedKey cacheKey = new ConnectorAccountCachedKey(connectorName, connectorRemoteId);
    try {
      return this.connectorUserIdFutureCache.get(cacheKey, cacheKey.hashCode());
    } catch (Exception e) {
      LOG.warn("Error when getting connector user Id cache", e);
    }
    return 0;
  }

  @Override
  public void deleteConnectorAccountById(long connectorAccountId) {
    super.deleteConnectorAccountById(connectorAccountId);
    ConnectorAccountCachedKey cacheKey = new ConnectorAccountCachedKey(connectorAccountId);
    ConnectorAccount connectorAccount = this.connectorAccountFutureCache.get(cacheKey, cacheKey.hashCode());
    this.connectorAccountFutureCache.remove(new ConnectorAccountCachedKey(connectorAccountId).hashCode());
    this.connectorRemoteIdFutureCache.remove(new ConnectorAccountCachedKey(connectorAccount.getConnectorName(),
                                                                           connectorAccount.getUserId()).hashCode());
    this.connectorUserIdFutureCache.remove(new ConnectorAccountCachedKey(connectorAccount.getConnectorName(),
                                                                         connectorAccount.getRemoteId()).hashCode());
  }
}

/*
 * This file is part of the Meeds project (https://meeds.io/).
 * Copyright (C) 2020 - 2023 Meeds Association
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
package io.meeds.gamification.dao;

import io.meeds.gamification.entity.ConnectorAccountEntity;

import org.exoplatform.commons.persistence.impl.GenericDAOJPAImpl;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

public class ConnectorAccountDAO extends GenericDAOJPAImpl<ConnectorAccountEntity, Long> {

  public static final String CONNECTOR_NAME   = "connectorName";

  public static final String USER_IDENTITY_ID = "userId";

  public static final String REMOTE_ID        = "remoteId";

  public ConnectorAccountEntity getConnectorAccountByNameAndUserId(String connectorName, long userIdentityId) {
    TypedQuery<ConnectorAccountEntity> query =
                                             getEntityManager().createNamedQuery("GamificationConnectorAccount.getConnectorAccountByNameAndUserId",
                                                                                 ConnectorAccountEntity.class);
    query.setParameter(CONNECTOR_NAME, connectorName);
    query.setParameter(USER_IDENTITY_ID, userIdentityId);
    query.setMaxResults(1);
    try {
      return query.getSingleResult();
    } catch (NoResultException e) {
      return null;
    }
  }

  public ConnectorAccountEntity getConnectorAccountByNameAndRemoteId(String connectorName, String connectorRemoteId) {
    TypedQuery<ConnectorAccountEntity> query =
                                             getEntityManager().createNamedQuery("GamificationConnectorAccount.getConnectorAccountByNameAndRemoteId",
                                                                                 ConnectorAccountEntity.class);
    query.setParameter(CONNECTOR_NAME, connectorName);
    query.setParameter(REMOTE_ID, connectorRemoteId);
    query.setMaxResults(1);
    try {
      return query.getSingleResult();
    } catch (NoResultException e) {
      return null;
    }
  }

  public String getConnectorRemoteId(String connectorName, long userIdentityId) {
    TypedQuery<String> query = getEntityManager().createNamedQuery("GamificationConnectorAccount.getConnectorRemoteId",
                                                                   String.class);
    query.setParameter(CONNECTOR_NAME, connectorName);
    query.setParameter(USER_IDENTITY_ID, userIdentityId);
    query.setMaxResults(1);
    try {
      return query.getSingleResult();
    } catch (NoResultException e) {
      return null;
    }
  }

  public long getAssociatedUserIdentityId(String connectorName, String connectorRemoteId) {
    TypedQuery<Long> query = getEntityManager().createNamedQuery("GamificationConnectorAccount.getAssociatedUserIdentityId",
                                                                 Long.class);
    query.setParameter(CONNECTOR_NAME, connectorName);
    query.setParameter(REMOTE_ID, connectorRemoteId);
    query.setMaxResults(1);
    try {
      Long result = query.getSingleResult();
      return result == null ? 0 : result;
    } catch (NoResultException e) {
      return 0;
    }
  }
}

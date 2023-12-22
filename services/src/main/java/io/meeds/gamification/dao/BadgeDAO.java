/*
 * This file is part of the Meeds project (https://meeds.io/).
 * Copyright (C) 2020 Meeds Association
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

import org.exoplatform.commons.persistence.impl.GenericDAOJPAImpl;

import io.meeds.gamification.entity.BadgeEntity;

import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;

import java.util.Collections;
import java.util.List;

public class BadgeDAO extends GenericDAOJPAImpl<BadgeEntity, Long> {

  private static final String DOMAIN_ID = "domainId";

  public BadgeEntity findBadgeByTitle(String badgeTitle) throws PersistenceException {

    TypedQuery<BadgeEntity> query = getEntityManager().createNamedQuery("GamificationBadge.findBadgeByTitle", BadgeEntity.class)
                                                      .setParameter("badgeTitle", badgeTitle);

    try {
      return query.getSingleResult();
    } catch (NoResultException e) {
      return null;
    }

  }

  public BadgeEntity findBadgeByTitleAndProgramId(String badgeTitle, long programId) throws PersistenceException {

    TypedQuery<BadgeEntity> query = getEntityManager()
                                                      .createNamedQuery("GamificationBadge.findBadgeByTitleAndDomain",
                                                                        BadgeEntity.class)
                                                      .setParameter("badgeTitle", badgeTitle)
                                                      .setParameter(DOMAIN_ID, programId);

    try {
      return query.getSingleResult();
    } catch (NoResultException e) {
      return null;
    }

  }

  public List<BadgeEntity> findBadgesByProgramId(long domainId) throws PersistenceException {

    TypedQuery<BadgeEntity> query = getEntityManager().createNamedQuery("GamificationBadge.findBadgeByDomain", BadgeEntity.class)
                                                      .setParameter(DOMAIN_ID, domainId);

    try {
      return query.getResultList();
    } catch (NoResultException e) {
      return Collections.emptyList();
    }

  }

  public List<BadgeEntity> findEnabledBadgesByProgramId(long domainId) throws PersistenceException {

    TypedQuery<BadgeEntity> query = getEntityManager().createNamedQuery("GamificationBadge.findEnabledBadgeByDomain",
                                                                        BadgeEntity.class)
                                                      .setParameter(DOMAIN_ID, domainId);

    try {
      return query.getResultList();
    } catch (NoResultException e) {
      return Collections.emptyList();
    }

  }

  public List<BadgeEntity> getAllBadges() throws PersistenceException {

    TypedQuery<BadgeEntity> query = getEntityManager().createNamedQuery("GamificationBadge.getAllBadges", BadgeEntity.class);

    try {
      return query.getResultList();
    } catch (NoResultException e) {
      return Collections.emptyList();
    }

  }

}

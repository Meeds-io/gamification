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
package org.exoplatform.addons.gamification.storage.dao;

import org.exoplatform.addons.gamification.entities.domain.configuration.BadgeEntity;
import org.exoplatform.commons.persistence.impl.GenericDAOJPAImpl;

import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import java.util.List;

public class BadgeDAO extends GenericDAOJPAImpl<BadgeEntity, Long> {

    public BadgeDAO() {

    }

    public BadgeEntity findBadgeByTitle(String badgeTitle) throws PersistenceException {

        TypedQuery<BadgeEntity> query = getEntityManager().createNamedQuery("GamificationBadge.findBadgeByTitle", BadgeEntity.class)
                .setParameter("badgeTitle", badgeTitle);

        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }

    }

    public BadgeEntity findBadgeByTitleAndDomain(String badgeTitle, String domain) throws PersistenceException {

        TypedQuery<BadgeEntity> query = getEntityManager().createNamedQuery("GamificationBadge.findBadgeByTitleAndDomain", BadgeEntity.class)
                .setParameter("badgeTitle", badgeTitle)
                .setParameter("domain", domain);

        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }

    }

    public List<BadgeEntity> findBadgesByDomain(String domain) throws PersistenceException {

        TypedQuery<BadgeEntity> query = getEntityManager().createNamedQuery("GamificationBadge.findBadgeByDomain", BadgeEntity.class)
                .setParameter("badgeDomain", domain);

        try {
            return query.getResultList();
        } catch (NoResultException e) {
            return null;
        }

    }
    public List<BadgeEntity> findEnabledBadgesByDomain(String domain) throws PersistenceException {

        TypedQuery<BadgeEntity> query = getEntityManager().createNamedQuery("GamificationBadge.findEnabledBadgeByDomain", BadgeEntity.class)
                .setParameter("badgeDomain", domain);

        try {
            return query.getResultList();
        } catch (NoResultException e) {
            return null;
        }

    }

    public List<BadgeEntity> getAllBadges() throws PersistenceException {

        TypedQuery<BadgeEntity> query = getEntityManager().createNamedQuery("GamificationBadge.getAllBadges", BadgeEntity.class);

        try {
            return query.getResultList();
        } catch (NoResultException e) {
            return null;
        }

    }


    public List<BadgeEntity> getAllBadgesWithNullDomain() throws PersistenceException {

        TypedQuery<BadgeEntity> query = getEntityManager().createNamedQuery("GamificationBadge.getAllBadgesWithNullDomain", BadgeEntity.class);
        try {
            return query.getResultList();
        } catch (NoResultException e) {
            return null;
        }

    }

    public List<String>  getDomainList() throws PersistenceException {
        TypedQuery<String> query = getEntityManager().createNamedQuery("GamificationBadge.getDomainList", String.class);

        try {
            return query.getResultList();
        } catch (NoResultException e) {
            return null;
        }

    }
}

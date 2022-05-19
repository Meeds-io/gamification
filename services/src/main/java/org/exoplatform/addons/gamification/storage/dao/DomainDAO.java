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

import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;

import org.exoplatform.addons.gamification.entities.domain.configuration.DomainEntity;
import org.exoplatform.commons.api.persistence.GenericDAO;
import org.exoplatform.commons.persistence.impl.GenericDAOJPAImpl;

import java.util.List;
import java.util.stream.Collectors;

public class DomainDAO extends GenericDAOJPAImpl<DomainEntity, Long> implements GenericDAO<DomainEntity, Long> {

    public DomainDAO() {
    }

    public DomainEntity findEnabledDomainByTitle(String domainTitle) throws PersistenceException {

        TypedQuery<DomainEntity> query = getEntityManager().createNamedQuery("GamificationDomain.findEnabledDomainByTitle", DomainEntity.class)
                .setParameter("domainTitle", domainTitle);

        try {
            List<DomainEntity> domainEntities =  query.getResultList();
            return !domainEntities.isEmpty()  ? domainEntities.get(0) : null;
        } catch (NoResultException e) {
            return null;
        }

    }

    public DomainEntity getDomainByTitle(String domainTitle) throws PersistenceException {
        TypedQuery<DomainEntity> query = getEntityManager().createNamedQuery("GamificationDomain.findDomainByTitle", DomainEntity.class)
                .setParameter("domainTitle", domainTitle);
        try {
            List<DomainEntity> domainEntities =  query.getResultList();
            return !domainEntities.isEmpty()  ? domainEntities.get(0) : null;
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<DomainEntity> getAllDomains() throws PersistenceException {

        TypedQuery<DomainEntity> query = getEntityManager().createNamedQuery("GamificationDomain.getAllDomains", DomainEntity.class);

        try {
            return query.getResultList();
        } catch (NoResultException e) {
            return null;
        }

    }

    public List<DomainEntity> getEnabledDomains() throws PersistenceException {

        TypedQuery<DomainEntity> query = getEntityManager().createNamedQuery("GamificationDomain.getEnabledDomains", DomainEntity.class);

        try {
            return query.getResultList();
        } catch (NoResultException e) {
            return null;
        }

    }
    public int deleteDomainByTitle(String domainTitle) throws PersistenceException {
        return getEntityManager().createNamedQuery("GamificationDomain.deleteDomainByTitle")
                .setParameter("domainTitle", domainTitle)
                .executeUpdate();

    }

    public void clear() {
        getEntityManager().clear();
    }
}

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

import org.exoplatform.addons.gamification.entities.domain.configuration.RuleEntity;
import org.exoplatform.commons.api.persistence.GenericDAO;
import org.exoplatform.commons.persistence.impl.GenericDAOJPAImpl;

import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import java.util.List;

public class RuleDAO extends GenericDAOJPAImpl<RuleEntity, Long> implements GenericDAO<RuleEntity, Long> {

    public RuleDAO() {
    }

    public RuleEntity findEnableRuleByTitle(String ruleTitle) throws PersistenceException {

        TypedQuery<RuleEntity> query = getEntityManager().createNamedQuery("Rule.findEnabledRuleByTitle", RuleEntity.class)
                .setParameter("ruleTitle", ruleTitle);

        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }

    }

    public List<RuleEntity> findEnabledRulesByEvent(String event) throws PersistenceException {

        TypedQuery<RuleEntity> query = getEntityManager().createNamedQuery("Rule.findEnabledRulesByEvent", RuleEntity.class)
                .setParameter("event", event);

        try {
            return query.getResultList();
        } catch (NoResultException e) {
            return null;
        }

    }

    public RuleEntity findRuleByTitle(String ruleTitle) throws PersistenceException {

        TypedQuery<RuleEntity> query = getEntityManager().createNamedQuery("Rule.findRuleByTitle", RuleEntity.class)
                .setParameter("ruleTitle", ruleTitle);

        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }

    }

    public RuleEntity findRuleByEventAndDomain(String event, String domain) throws PersistenceException {

        TypedQuery<RuleEntity> query = getEntityManager().createNamedQuery("Rule.findRuleByEventAndDomain", RuleEntity.class)
                .setParameter("event", event)
                .setParameter("domain", domain);

        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }

    }

    public List<RuleEntity> getAllRules() throws PersistenceException {

        TypedQuery<RuleEntity> query = getEntityManager().createNamedQuery("Rule.getAllRules", RuleEntity.class);

        try {
            return query.getResultList();
        } catch (NoResultException e) {
            return null;
        }

    }

    public List<RuleEntity> getAllRulesByDomain(String domain) throws PersistenceException {

        TypedQuery<RuleEntity> query = getEntityManager().createNamedQuery("Rule.getAllRulesByDomain", RuleEntity.class)
                .setParameter("domain", domain);

        try {
            return query.getResultList();
        } catch (NoResultException e) {
            return null;
        }

    }

    public List<RuleEntity> getAllRulesWithNullDomain() throws PersistenceException {

        TypedQuery<RuleEntity> query = getEntityManager().createNamedQuery("Rule.getAllRulesWithNullDomain", RuleEntity.class);
        try {
            return query.getResultList();
        } catch (NoResultException e) {
            return null;
        }

    }

    public List<String>  getDomainList() throws PersistenceException {
        TypedQuery<String> query = getEntityManager().createNamedQuery("Rule.getDomainList", String.class);

        try {
            return query.getResultList();
        } catch (NoResultException e) {
            return null;
        }

    }

    public List<String>  getAllEvents() throws PersistenceException {
        TypedQuery<String> query = getEntityManager().createNamedQuery("Rule.getEventList", String.class);

        try {
            return query.getResultList();
        } catch (NoResultException e) {
            return null;
        }

    }

}

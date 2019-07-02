package org.exoplatform.addons.gamification.storage.dao;

import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;

import org.exoplatform.addons.gamification.entities.domain.configuration.DomainEntity;
import org.exoplatform.commons.api.persistence.GenericDAO;
import org.exoplatform.commons.persistence.impl.GenericDAOJPAImpl;

public class DomainDAO extends GenericDAOJPAImpl<DomainEntity, Long> implements GenericDAO<DomainEntity, Long> {

    public DomainDAO() {
    }

    public DomainEntity findBadgeByTitle(String domainTitle) throws PersistenceException {

        TypedQuery<DomainEntity> query = getEntityManager().createNamedQuery("GamificationDomain.findDomainByTitle", DomainEntity.class)
                .setParameter("domainTitle", domainTitle);

        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }

    }

    public void clear() {
        getEntityManager().clear();
    }
}

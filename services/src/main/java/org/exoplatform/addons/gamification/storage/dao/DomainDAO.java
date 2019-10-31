package org.exoplatform.addons.gamification.storage.dao;

import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;

import org.exoplatform.addons.gamification.entities.domain.configuration.DomainEntity;
import org.exoplatform.commons.api.persistence.GenericDAO;
import org.exoplatform.commons.persistence.impl.GenericDAOJPAImpl;

import java.util.List;

public class DomainDAO extends GenericDAOJPAImpl<DomainEntity, Long> implements GenericDAO<DomainEntity, Long> {

    public DomainDAO() {
    }

    public DomainEntity findDomainByTitle(String domainTitle) throws PersistenceException {

        TypedQuery<DomainEntity> query = getEntityManager().createNamedQuery("GamificationDomain.findDomainByTitle", DomainEntity.class)
                .setParameter("domainTitle", domainTitle);

        try {
            return query.getSingleResult();
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

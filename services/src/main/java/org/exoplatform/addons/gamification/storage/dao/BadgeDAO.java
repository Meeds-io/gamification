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

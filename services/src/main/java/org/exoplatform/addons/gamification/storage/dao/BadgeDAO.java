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

    public BadgeEntity findBadgeByNeededScore(String neededScore) throws PersistenceException {

        TypedQuery<BadgeEntity> query = getEntityManager().createNamedQuery("GamificationBadge.findBadgeByNeededScore", BadgeEntity.class)
                .setParameter("neededScore", neededScore);

        try {
            return query.getSingleResult();
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

    public int deleteBadgeById(int badgeId) throws PersistenceException {
        return getEntityManager().createNamedQuery("GamificationBadge.deleteBadgeById")
                .setParameter("badgeId", badgeId)
                .executeUpdate();

    }

    public int deleteBadgeByTitle(String badgeTitle) throws PersistenceException {
        return getEntityManager().createNamedQuery("GamificationBadge.deleteBadgeByTitle")
                .setParameter("badgeTitle", badgeTitle)
                .executeUpdate();

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

    public void clear() {
        getEntityManager().clear();
    }


}

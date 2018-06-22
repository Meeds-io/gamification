package org.exoplatform.addons.gamification.storage.dao;

import org.exoplatform.commons.persistence.impl.GenericDAOJPAImpl;
import org.exoplatform.addons.gamification.entities.domain.configuration.BadgeEntity;

import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import java.util.List;

public class BadgeDAO extends GenericDAOJPAImpl<BadgeEntity, Long> {

    public BadgeDAO() {

    }

    public BadgeEntity findBadgeByTitle(String badgeTitle) throws PersistenceException {

        TypedQuery<BadgeEntity> query = getEntityManager().createNamedQuery("Badge.findBadgeByTitle", BadgeEntity.class)
                .setParameter("badgeTitle", badgeTitle);

        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }

    }

    public BadgeEntity findBadgeByNeededScore(String neededScore) throws PersistenceException {

        TypedQuery<BadgeEntity> query = getEntityManager().createNamedQuery("Badge.findBadgeByNeededScore", BadgeEntity.class)
                .setParameter("neededScore", neededScore);

        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }

    }

    public List<BadgeEntity> getAllBadges() throws PersistenceException {

        TypedQuery<BadgeEntity> query = getEntityManager().createNamedQuery("Badge.getAllBadges", BadgeEntity.class);

        try {
            return query.getResultList();
        } catch (NoResultException e) {
            return null;
        }

    }

    public int deleteBadgeById(int badgeId) throws PersistenceException {
        return getEntityManager().createNamedQuery("Badge.deleteBadgeById")
                .setParameter("badgeId", badgeId)
                .executeUpdate();

    }

    public int deleteBadgeByTitle(String badgeTitle) throws PersistenceException {
        return getEntityManager().createNamedQuery("Badge.deleteBadgeByTitle")
                .setParameter("badgeTitle", badgeTitle)
                .executeUpdate();

    }

    public void clear() {
        getEntityManager().clear();
    }


}

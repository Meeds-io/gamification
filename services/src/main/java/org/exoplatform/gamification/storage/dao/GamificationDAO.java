package org.exoplatform.gamification.storage.dao;

import org.exoplatform.commons.persistence.impl.GenericDAOJPAImpl;
import org.exoplatform.gamification.entities.domain.effective.GamificationContextEntity;

import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;

public class GamificationDAO extends GenericDAOJPAImpl<GamificationContextEntity, Long> {

    public GamificationDAO() {
    }

    public GamificationContextEntity findGamificationContextByUsername(String username) throws PersistenceException {

        TypedQuery<GamificationContextEntity> query = getEntityManager().createNamedQuery("GamificationContext.findGamificationContextByUsername", GamificationContextEntity.class)
                .setParameter("username", username);

        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }

    }
}

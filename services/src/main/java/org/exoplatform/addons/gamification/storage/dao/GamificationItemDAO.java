package org.exoplatform.addons.gamification.storage.dao;

import org.exoplatform.addons.gamification.entities.domain.effective.GamificationContextItemEntity;
import org.exoplatform.commons.persistence.impl.GenericDAOJPAImpl;

import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import java.util.List;

public class GamificationItemDAO extends GenericDAOJPAImpl<GamificationContextItemEntity, Long> {

    public GamificationItemDAO() {
    }

    /**
     * Get gamification items by userid and by domain
     * @param userid
     * @param domain
     * @return
     * @throws PersistenceException
     */
    public List<GamificationContextItemEntity> findGamificationItemsByUserIdAndDomain(String userid, String domain) throws PersistenceException {

        // TODO : We should load only first 10 users
        List <GamificationContextItemEntity> gamificationItems = getEntityManager().createNamedQuery("GamificationContextItem.findGamificationItemsByUserIdAndDomain")
                .setParameter("userid", Long.parseLong(userid))
                .setParameter("domain", domain)
                .getResultList();

        try {
            return gamificationItems;
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * Delete item
     * @param gamificationContextItemEntity
     * @return
     */
    public boolean deleteItem (GamificationContextItemEntity gamificationContextItemEntity) {
        boolean done = true;

        try {
            getEntityManager().remove(gamificationContextItemEntity);
        } catch (NoResultException e) {

            return false;

        }

        return done;
    }

}

package org.exoplatform.gamification.storage.ogm;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;

public class HibernateOgmFactory {

    private static HibernateOgmFactory instance = null;

    private EntityManager em = null;

    private HibernateOgmFactory() {
        em = Persistence.createEntityManagerFactory("gamification-ogm").createEntityManager();
    }

    public static HibernateOgmFactory getInstance() {
        if(instance == null) {
            instance = new HibernateOgmFactory();
        }
        return instance;
    }

    public EntityManager getOGMEntityManager() {
        return em;
    }
}

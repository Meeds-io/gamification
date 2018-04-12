package org.exoplatform.gamification.storage.ogm;

import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.time.Instant;

public class HibernateOgmFactory {

    private static final Log LOG = ExoLogger.getLogger(HibernateOgmFactory.class);

    private static HibernateOgmFactory singleton = new HibernateOgmFactory();

    private static final String GAMIFICATION_PU = "gamification-ogm";

    public static final boolean DEBUG = true;

    private EntityManagerFactory emf = null;

    private EntityManager em = null;

    private HibernateOgmFactory() {

    }

    public static HibernateOgmFactory getInstance() {

        return singleton;

    }

    public EntityManagerFactory getEntityManagerFactory() {

        if(emf == null) {
            emf = Persistence.createEntityManagerFactory(GAMIFICATION_PU);
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("Hibernate OGM Factory created at: {}", Instant.now());

        }

        return emf;
    }

    public void closeEmf() {
        if(emf.isOpen() || emf != null) {
            emf.close();
        }
        emf = null;

        if (LOG.isDebugEnabled()) {
            LOG.debug("Hibernate OGM Factory closed at : {}", Instant.now());

        }
    }

}

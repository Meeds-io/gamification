package org.exoplatform.gamification.storage.dao;

import org.exoplatform.commons.persistence.impl.GenericDAOJPAImpl;
import org.exoplatform.gamification.entities.domain.configuration.RuleEntity;

import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import java.util.List;

public class RuleDAO extends GenericDAOJPAImpl<RuleEntity, Long> {

    public RuleDAO() {
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

    public List<RuleEntity> getAllRules() throws PersistenceException {

        TypedQuery<RuleEntity> query = getEntityManager().createNamedQuery("Rule.getAllRules", RuleEntity.class);

        try {
            return query.getResultList();
        } catch (NoResultException e) {
            return null;
        }

    }

    public int deleteBadgeById(int ruleId) throws PersistenceException {
        return getEntityManager().createNamedQuery("Rule.deleteRuleById")
                .setParameter("ruleId", ruleId)
                .executeUpdate();

    }

    public int deleteBadgeByTitle(int ruleTitle) throws PersistenceException {
        return getEntityManager().createNamedQuery("Badge.deleterRuleByTitle")
                .setParameter("ruleTitle", ruleTitle)
                .executeUpdate();

    }

    public void clear() {
        getEntityManager().clear();
    }
}

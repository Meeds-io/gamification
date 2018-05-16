package org.exoplatform.gamification.storage.listener;

import org.exoplatform.gamification.entities.domain.configuration.RuleEntity;

import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;

public class AuditingEntityListener {

    public AuditingEntityListener() {

    }

    @PostPersist
    public void methodInvokedAfterPersist(RuleEntity categoryEntity) {

    }
    @PostUpdate
    public void methodInvokedAfterUpdate(RuleEntity categoryEntity) {

    }

    @PostRemove
    public void methodInvokedAfterRemove(RuleEntity categoryEntity) {

    }
}

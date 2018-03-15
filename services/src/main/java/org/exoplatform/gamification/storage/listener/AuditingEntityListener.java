package org.exoplatform.gamification.storage.listener;

import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;
import org.exoplatform.gamification.entities.domain.configuration.RuleEntity;

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

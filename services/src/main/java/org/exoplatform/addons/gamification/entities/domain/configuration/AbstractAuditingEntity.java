package org.exoplatform.addons.gamification.entities.domain.configuration;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.util.Date;

/**
 * Base abstract class for entities which will hold definitions for created, last modified by and created,
 * last modified by date.
 */
@MappedSuperclass
public abstract class AbstractAuditingEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column(name = "CREATED_BY", nullable = false, length = 50, updatable = false)
    protected String createdBy;

    @Column(name = "CREATED_DATE", nullable = false)
    //@JsonIgnore
    //---Can't match java8 API with JPA/hibernate : we have to upgrade to jpa-2.2
    //private Instant createdDate = Instant.now();
    protected Date createdDate = new Date();

    @Column(name = "LAST_MODIFIED_BY", length = 50)
    protected String lastModifiedBy;

    @Column(name = "LAST_MODIFIED_DATE")
    //@JsonIgnore
    //---Can't match java8 API with JPA/hibernate : we have to upgrade to jpa-2.2
    //private Instant lastModifiedDate = Instant.now();
    protected Date lastModifiedDate = new Date ();

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }
}

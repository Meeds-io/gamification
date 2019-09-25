package org.exoplatform.addons.gamification.service.dto.configuration;

import org.exoplatform.addons.gamification.entities.domain.configuration.DomainEntity;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DomainDTO implements Serializable {

    private static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

    protected Long id;

    protected String title;

    protected String description;

    protected int priority;

    private String createdBy;

    private Date createdDate;

    private String lastModifiedBy;

    private Date lastModifiedDate;

    protected boolean deleted;

    protected boolean enabled;


    public DomainDTO() {

    }

    public DomainDTO(DomainEntity domainEntity) {
        this.id = domainEntity.getId();
        this.title = domainEntity.getTitle();
        this.description = domainEntity.getDescription();
        this.createdBy = domainEntity.getCreatedBy();
        this.priority = domainEntity.getPriority();
        this.priority = domainEntity.getPriority();
        this.deleted = domainEntity.isDeleted();
        this.enabled = domainEntity.isEnabled();
        this.createdDate = domainEntity.getCreatedDate();
        this.lastModifiedDate = domainEntity.getLastModifiedDate();

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

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

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public String toString() {
        return "RuleDTO{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", createdBy=" + createdBy +
                ", createdDate=" + createdDate +
                ", lastModifiedBy='" + lastModifiedBy + '\'' +
                ", lastModifiedDate=" + lastModifiedDate +
                ", deleted=" + deleted +
                ", enabled=" + enabled +
                "}";
    }
}

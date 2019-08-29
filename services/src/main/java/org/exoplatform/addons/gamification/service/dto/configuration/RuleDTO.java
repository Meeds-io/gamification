package org.exoplatform.addons.gamification.service.dto.configuration;

import org.exoplatform.addons.gamification.entities.domain.configuration.RuleEntity;

import java.io.Serializable;
import java.util.Date;

public class RuleDTO implements Serializable {


    protected Long id;

    protected String title;

    protected String description;

    protected int score;

    protected String area;

    private DomainDTO domainDTO;

    //protected boolean isEnabled;
    protected boolean enabled;

    private String createdBy;

    private Date createdDate;

    private String lastModifiedBy;

    private Date lastModifiedDate;

    public RuleDTO() {

    }

    public RuleDTO(RuleEntity rule) {
        this.id = rule.getId();
        this.title = rule.getTitle();
        this.description = rule.getDescription();
        this.score = rule.getScore();
        this.area = rule.getArea();
        this.domainDTO = ((rule.getDomainEntity() == null) ? null : new DomainDTO(rule.getDomainEntity()));
        this.enabled = rule.isEnabled();
        this.createdBy = rule.getCreatedBy();
        this.createdDate = rule.getCreatedDate();
        this.lastModifiedBy = rule.getLastModifiedBy();
        this.lastModifiedDate = rule.getLastModifiedDate();

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

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getArea() {
        if(this.domainDTO!=null)return this.domainDTO.getTitle();
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public DomainDTO getDomainDTO() {
        return domainDTO;
    }

    public void setDomainDTO(DomainDTO domainDTO) {
        this.domainDTO = domainDTO;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
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


    @Override
    public String toString() {
        return "RuleDTO{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", score='" + score + '\'' +
                ", area='" + area + '\'' +
                ", createdBy=" + createdBy +
                ", createdDate=" + createdDate +
                ", lastModifiedBy='" + lastModifiedBy + '\'' +
                ", lastModifiedDate=" + lastModifiedDate +
                "}";
    }
}

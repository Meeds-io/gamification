/*
 * This file is part of the Meeds project (https://meeds.io/).
 * Copyright (C) 2020 Meeds Association
 * contact@meeds.io
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
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

    protected boolean deleted;

    private String createdBy;

    private Date createdDate;

    private String lastModifiedBy;

    private String event;

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
        this.event = rule.getEvent();
        this.deleted = rule.isDeleted();

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

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
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
                ", deleted=" + deleted +
                ", enabled=" + enabled +
                "}";
    }
}

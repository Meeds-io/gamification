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
package org.exoplatform.addons.gamification.entities.domain.configuration;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.*;

import org.exoplatform.commons.api.persistence.ExoEntity;

@Entity(name = "GamificationDomain")
@ExoEntity
@Table(name = "GAMIFICATION_DOMAIN")
@NamedQueries({
        @NamedQuery(
                name = "GamificationDomain.getAllDomains",
                query = "SELECT domain FROM GamificationDomain domain  WHERE domain.isDeleted = false"
        ),
        @NamedQuery(
                name = "GamificationDomain.getEnabledDomains",
                query = "SELECT domain FROM GamificationDomain domain  WHERE domain.isDeleted = false AND domain.isEnabled = true "
        ),

        @NamedQuery(
                name = "GamificationDomain.findDomainByTitle",
                query = "SELECT domain FROM GamificationDomain domain where domain.title = :domainTitle"
        ),
        @NamedQuery(
                name = "GamificationDomain.deleteDomainByTitle",
                query = "DELETE FROM GamificationDomain domain WHERE domain.title = :domainTitle"
        )
})
public class DomainEntity extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name="SEQ_GAMIFICATION_DOMAIN_ID", sequenceName="SEQ_GAMIFICATION_DOMAIN_ID")
    @GeneratedValue(strategy=GenerationType.AUTO, generator="SEQ_GAMIFICATION_DOMAIN_ID")
    protected Long id;

    @Column(name = "TITLE", unique = true, nullable = false)
    protected String title;

    @Column(name = "DESCRIPTION")
    protected String description;

    @Column(name = "PRIORITY")
    protected int priority;


    @Column(name = "DELETED", nullable = false)
    protected boolean isDeleted;

    @Column(name = "ENABLED", nullable = false)
    protected boolean isEnabled;


    public DomainEntity() {
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

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        DomainEntity domainEntity = (DomainEntity) o;
        return !(domainEntity.getId() == null || getId() == null) && Objects.equals(getId(), domainEntity.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Badge{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                "}";
    }

}

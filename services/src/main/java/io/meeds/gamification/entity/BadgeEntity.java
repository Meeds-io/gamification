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
package io.meeds.gamification.entity;

import org.exoplatform.commons.api.persistence.ExoEntity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Entity(name = "GamificationBadge")
@ExoEntity
@Table(name = "GAMIFICATION_BADGES")
@NamedQuery(name = "GamificationBadge.getAllBadges", query = "SELECT badge FROM GamificationBadge badge   WHERE badge.isDeleted = false ORDER BY badge.iconFileId ASC ")
@NamedQuery(name = "GamificationBadge.findBadgeByDomain", query = "SELECT badge FROM GamificationBadge badge WHERE badge.domainEntity.id = :domainId  ORDER BY badge.neededScore ASC")
@NamedQuery(name = "GamificationBadge.findEnabledBadgeByDomain", query = "SELECT badge FROM GamificationBadge badge WHERE (badge.domainEntity.id = :domainId) AND (badge.enabled = true) AND badge.isDeleted = false ORDER BY badge.neededScore ASC")
@NamedQuery(name = "GamificationBadge.getEnabledBadges", query = "SELECT badge FROM GamificationBadge badge where badge.enabled = :isEnabled AND badge.isDeleted = false")
@NamedQuery(name = "GamificationBadge.getValidBadges", query = "SELECT badge FROM GamificationBadge badge where (badge.startValidityDate BETWEEN :stDate AND :edDate) AND (badge.endValidityDate BETWEEN :stDate AND :edDate) AND badge.isDeleted = 0")
@NamedQuery(name = "GamificationBadge.findBadgeByNeededScore", query = "SELECT badge FROM GamificationBadge badge where badge.neededScore = :neededScore  AND badge.isDeleted = false")
@NamedQuery(name = "GamificationBadge.findBadgeByTitle", query = "SELECT badge FROM GamificationBadge badge where badge.title = :badgeTitle")
@NamedQuery(name = "GamificationBadge.findBadgeByTitleAndDomain", query = "SELECT badge FROM GamificationBadge badge where badge.title = :badgeTitle and badge.domainEntity.id = :domainId")
@NamedQuery(name = "GamificationBadge.deleteBadgeByTitle", query = "DELETE FROM GamificationBadge badge WHERE badge.title = :badgeTitle")
@NamedQuery(name = "GamificationBadge.deleteBadgeById", query = "DELETE FROM GamificationBadge badge WHERE badge.id = :badgeId")
@NamedQuery(name = "GamificationBadge.getAllBadgesWithNullDomain", query = "SELECT badge FROM GamificationBadge badge where badge.domainEntity IS NULL")
public class BadgeEntity extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 8412261859526217944L;

    @Id
    @SequenceGenerator(name="SEQ_GAMIFICATION_BADGE_ID", sequenceName="SEQ_GAMIFICATION_BADGE_ID", allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.AUTO, generator="SEQ_GAMIFICATION_BADGE_ID")
    @Column(name = "BADGE_ID")
    protected Long id;

    @Column(name = "TITLE", unique = true, nullable = false)
    protected String title;

    @Column(name = "DESCRIPTION")
    protected String description;

    @Column(name = "NEEDED_SCORE")
    protected int neededScore;


    @Column(name="ICON_FILE_ID")
    private long iconFileId;

    @Column(name = "VALIDITY_DATE_START")
    // When I used this annotation I get an issue with serialization within REST services
    //@Temporal(TemporalType.DATE)
    protected Date startValidityDate;

    @Column(name = "VALIDITY_DATE_END")
    // When I used this annotation I get an issue with serialization within REST services
    //@Temporal(TemporalType.DATE)
    protected Date endValidityDate;

    @Column(name = "ENABLED", nullable = false)
    protected boolean enabled;

    @ManyToOne
    @JoinColumn(name = "DOMAIN_ID")
    private ProgramEntity domainEntity;


    @Column(name = "DELETED", nullable = false)
    protected boolean isDeleted;

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

    public int getNeededScore() {
        return neededScore;
    }

    public void setNeededScore(int neededScore) {
        this.neededScore = neededScore;
    }

    public Date getStartValidityDate() {
        return startValidityDate;
    }

    public void setStartValidityDate(Date startValidityDate) {
        this.startValidityDate = startValidityDate;
    }

    public Date getEndValidityDate() {
        return endValidityDate;
    }

    public void setEndValidityDate(Date endValidityDate) {
        this.endValidityDate = endValidityDate;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public long getIconFileId() {
        return iconFileId;
    }

    public void setIconFileId(long iconFileId) {
        this.iconFileId = iconFileId;
    }

    public ProgramEntity getDomainEntity() {
        return domainEntity;
    }

    public void setDomainEntity(ProgramEntity domainEntity) {
        this.domainEntity = domainEntity;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        BadgeEntity badgeEntity = (BadgeEntity) o;
        return !(badgeEntity.getId() == null || getId() == null) && Objects.equals(getId(), badgeEntity.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Badge{" +
                "title='" + title + '\'' +
                ", needed score='" + neededScore + '\'' +
                ", zone='" + domainEntity.getTitle() + '\'' +
                ", iconFileId='" + iconFileId + '\'' +
                ", start validity date='" + startValidityDate + '\'' +
                ", end validity date='" + endValidityDate + '\'' +
                ", enable='" + enabled + '\'' +
                ", description='" + description + '\'' +
                "}";
    }

}

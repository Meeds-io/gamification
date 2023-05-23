/**
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

import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

import javax.persistence.*;

import org.exoplatform.commons.api.persistence.ExoEntity;

import io.meeds.gamification.constant.EntityType;

@Entity(name = "GamificationDomain")
@ExoEntity
@Table(name = "GAMIFICATION_DOMAIN")
@NamedQuery(name = "GamificationDomain.getAllDomains", query = "SELECT domain FROM GamificationDomain domain LEFT JOIN FETCH domain.owners WHERE domain.isDeleted = false")
@NamedQuery(name = "GamificationDomain.getEnabledDomains", query = "SELECT domain FROM GamificationDomain domain LEFT JOIN FETCH domain.owners WHERE domain.isDeleted = false AND domain.isEnabled = true ")
@NamedQuery(name = "GamificationDomain.findByIdWithOwners", query = "SELECT domain FROM GamificationDomain domain LEFT JOIN FETCH domain.owners WHERE domain.id = :id")
@NamedQuery(name = "GamificationDomain.findDomainByTitle", query = "SELECT domain FROM GamificationDomain domain LEFT JOIN FETCH domain.owners WHERE domain.title = :domainTitle")
@NamedQuery(name = "GamificationDomain.deleteDomainByTitle", query = "DELETE FROM GamificationDomain domain WHERE domain.title = :domainTitle")
public class ProgramEntity extends AbstractAuditingEntity implements Serializable {

  private static final long serialVersionUID = 6578902752036385060L;

  @Id
  @SequenceGenerator(name = "SEQ_GAMIFICATION_DOMAIN_ID", sequenceName = "SEQ_GAMIFICATION_DOMAIN_ID", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.AUTO, generator = "SEQ_GAMIFICATION_DOMAIN_ID")
  protected Long            id;

  @Column(name = "TITLE", unique = true, nullable = false)
  protected String          title;

  @Column(name = "DESCRIPTION")
  protected String          description;

  @Column(name = "PRIORITY")
  protected int             priority;

  @Column(name = "DELETED", nullable = false)
  protected boolean         isDeleted;

  @Column(name = "ENABLED", nullable = false)
  protected boolean         isEnabled;

  @Enumerated(EnumType.ORDINAL)
  @Column(name = "TYPE", nullable = false)
  protected EntityType      type;

  @Column(name = "BUDGET")
  protected long            budget;

  @Column(name = "COVER_FILE_ID")
  protected long            coverFileId;

  @Column(name = "AUDIENCE_ID")
  protected Long            audienceId;

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "GAMIFICATION_DOMAIN_OWNERS", joinColumns = @JoinColumn(name = "DOMAIN_ID"))
  @Column(name = "IDENTITY_ID")
  private Set<Long>         owners;

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

  public long getBudget() {
    return budget;
  }

  public void setBudget(long budget) {
    this.budget = budget;
  }

  public long getCoverFileId() {
    return coverFileId;
  }

  public void setCoverFileId(long coverFileId) {
    this.coverFileId = coverFileId;
  }

  public Long getAudienceId() {
    return audienceId;
  }

  public void setAudienceId(Long audience) {
    this.audienceId = audience;
  }

  public Set<Long> getOwners() {
    return owners;
  }

  public void setOwners(Set<Long> owners) {
    this.owners = owners;
  }

  public EntityType getType() {
    return type;
  }

  public void setType(EntityType type) {
    this.type = type;
  }

  @Override
  public int hashCode() {
    return Objects.hash(budget, coverFileId, description, id, isDeleted, isEnabled, owners, priority, title, type);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    ProgramEntity other = (ProgramEntity) obj;
    return Objects.equals(budget, other.budget) && Objects.equals(coverFileId, other.coverFileId)
        && Objects.equals(description, other.description) && Objects.equals(id, other.id) && isDeleted == other.isDeleted
        && isEnabled == other.isEnabled && Objects.equals(owners, other.owners) && priority == other.priority
        && Objects.equals(title, other.title) && type == other.type;
  }

  @Override
  public String toString() {
    return "Domain{" + "title='" + title + '\'' + ", description='" + description + '\'' + "}";
  }

}

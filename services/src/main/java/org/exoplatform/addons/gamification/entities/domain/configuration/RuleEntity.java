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

import org.exoplatform.addons.gamification.service.dto.configuration.constant.EntityType;
import org.exoplatform.commons.api.persistence.ExoEntity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Entity(name = "Rule")
@ExoEntity
@Table(name = "GAMIFICATION_RULE")
@NamedQuery(
  name = "Rule.findRuleByEventAndDomain",
  query = "SELECT rule FROM Rule rule" +
    " WHERE LOWER(rule.event) = LOWER(:event)" +
    " AND rule.domainEntity.id = :domainId" +
    " AND rule.type = :type"
)
@NamedQuery(
  name = "Rule.findActiveRulesByEvent",
  query = "SELECT rule FROM Rule rule" +
    " WHERE LOWER(rule.event) = LOWER(:event)" +
    " AND rule.isEnabled = true" +
    " AND rule.isDeleted = false" +
    " AND (rule.startDate IS NULL OR rule.startDate <= :date)" +
    " AND (rule.endDate IS NULL OR rule.endDate >= :date)" +
    " AND rule.type = :type"
)
@NamedQuery(
  name = "Rule.findRuleByTitle",
  query = "SELECT rule FROM Rule rule"
      + " WHERE LOWER(rule.title) = LOWER(:ruleTitle)"
      + " AND rule.type = :type"
)
@NamedQuery(
  name = "Rule.getEventList",
  query = "SELECT DISTINCT(rule.event) FROM Rule rule" +
      " WHERE rule.type = :type"
)
@NamedQuery(
 name = "Rule.getRulesTotalScoreByDomain",
 query =
    " SELECT SUM(rule.score) FROM Rule rule " +
    " WHERE rule.domainEntity.id = :domainId" +
    " AND rule.isEnabled = true" +
    " AND rule.isDeleted = false" +
    " AND (rule.startDate IS NULL OR rule.startDate <= :date)" +
    " AND (rule.endDate IS NULL OR rule.endDate >= :date)"
)
@NamedQuery(
 name = "Rule.getHighestBudgetDomainIds",
 query =
    " SELECT rule.domainEntity.id, SUM(rule.score) as totalScore FROM Rule rule" +
    " INNER JOIN rule.domainEntity domain" +
    "   ON domain.isEnabled = true" +
    "  AND domain.isDeleted = false" +
    " WHERE rule.isEnabled = true" +
    "   AND rule.isDeleted = false" +
    "   AND (rule.startDate IS NULL OR rule.startDate <= :date)" +
    "   AND (rule.endDate IS NULL OR rule.endDate >= :date)" +
    " GROUP BY rule.domainEntity.id " +
    " ORDER BY totalScore DESC"
)
@NamedQuery(
  name = "Rule.getHighestBudgetDomainIdsBySpacesIds",
  query =
    " SELECT rule.domainEntity.id, SUM(rule.score) as totalScore FROM Rule rule" +
    " INNER JOIN rule.domainEntity domain" +
    "   ON domain.isEnabled = true" +
    "  AND domain.isDeleted = false" +
    "  AND (domain.audienceId IS NULL OR domain.audienceId in (:spacesIds))" +
    " WHERE rule.isEnabled = true" +
    "   AND rule.isDeleted = false" +
    "   AND (rule.startDate IS NULL OR rule.startDate <= :date)" +
    "   AND (rule.endDate IS NULL OR rule.endDate >= :date)" +
    " GROUP BY rule.domainEntity.id " +
    " ORDER BY totalScore DESC"
)
public class RuleEntity extends AbstractAuditingEntity implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @SequenceGenerator(name = "SEQ_GAMIFICATION_RULE_ID", sequenceName = "SEQ_GAMIFICATION_RULE_ID", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.AUTO, generator = "SEQ_GAMIFICATION_RULE_ID")
  protected Long            id;

  @Column(name = "TITLE", unique = true, nullable = false)
  protected String          title;

  @Column(name = "DESCRIPTION")
  protected String          description;

  @Column(name = "SCORE")
  protected int             score;

  @Column(name = "EVENT")
  protected String          event;

  @ManyToOne
  @JoinColumn(name = "DOMAIN_ID")
  private ProgramEntity      domainEntity;

  @Column(name = "ENABLED", nullable = false)
  protected boolean         isEnabled;

  @Column(name = "DELETED", nullable = false)
  protected boolean         isDeleted;

  @Column(name = "START_DATE")
  private Date              startDate;

  @Column(name = "END_DATE")
  private Date              endDate;

  @Enumerated(EnumType.ORDINAL)
  @Column(name = "TYPE", nullable = false)
  protected EntityType      type;

  public EntityType getType() {
    return type;
  }

  public void setType( EntityType type) {
    this.type = type;
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

  public boolean isEnabled() {
    return isEnabled;
  }

  public Date getStartDate() {
    return startDate;
  }

  public void setStartDate(Date startDate) {
    this.startDate = startDate;
  }

  public Date getEndDate() {
    return endDate;
  }

  public void setEndDate(Date endDate) {
    this.endDate = endDate;
  }

  public void setEnabled(boolean enabled) {
    isEnabled = enabled;
  }

  public ProgramEntity getDomainEntity() {
    return domainEntity;
  }

  public void setDomainEntity(ProgramEntity domainEntity) {
    this.domainEntity = domainEntity;
  }

  public String getEvent() {
    return event;
  }

  public void setEvent(String event) {
    this.event = event;
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

    RuleEntity ruleEntity = (RuleEntity) o;
    return !(ruleEntity.getId() == null || getId() == null) && Objects.equals(getId(), ruleEntity.getId());
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(getId());
  }

  @Override
  public String toString() {
    return "Rule{" +
            "title='" + title + '\'' +
            ", score='" + score + '\'' +
            ", domainId='" + domainEntity.id + '\'' +
            ", description='" + description + '\'' +
            ", enable='" + isEnabled + '\'' +
            "}";
  }

}

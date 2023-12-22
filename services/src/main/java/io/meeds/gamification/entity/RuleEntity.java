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

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import org.exoplatform.commons.api.persistence.ExoEntity;

import io.meeds.gamification.constant.EntityType;
import io.meeds.gamification.constant.RecurrenceType;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity(name = "Rule")
@ExoEntity
@Table(name = "GAMIFICATION_RULE")
@NamedQuery(
  name = "Rule.findActiveRuleByEventAndDomain",
  query = "SELECT rule FROM Rule rule" +
    " WHERE LOWER(rule.event) = LOWER(:event)" +
    " AND rule.domainEntity.id = :domainId" +
    " AND rule.isEnabled = true" +
    " AND rule.isDeleted = false" +
    " AND (rule.startDate IS NULL OR rule.startDate <= :date)" +
    " AND (rule.endDate IS NULL OR rule.endDate > :date)" +
    " AND rule.type = :type"
)
@NamedQuery(
  name = "Rule.findRuleByTitle",
  query = "SELECT rule FROM Rule rule"
      + " WHERE LOWER(rule.title) = LOWER(:ruleTitle)"
      + " AND rule.type = :type"
)
@NamedQuery(
 name = "Rule.getRulesTotalScoreByDomain",
 query =
    " SELECT SUM(rule.score) FROM Rule rule " +
    " WHERE rule.domainEntity.id = :domainId" +
    " AND rule.isEnabled = true" +
    " AND rule.isDeleted = false" +
    " AND (rule.startDate IS NULL OR rule.startDate <= :date)" +
    " AND (rule.endDate IS NULL OR rule.endDate > :date)"
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
    "   AND (rule.endDate IS NULL OR rule.endDate > :date)" +
    " GROUP BY rule.domainEntity.id " +
    " ORDER BY totalScore DESC"
)
@NamedQuery(
  name = "Rule.getHighestBudgetOpenDomainIds",
  query =
    " SELECT rule.domainEntity.id, SUM(rule.score) as totalScore FROM Rule rule" +
    " INNER JOIN rule.domainEntity domain" +
    "   ON domain.isEnabled = true" +
    "  AND domain.isDeleted = false" +
    "  AND domain.audienceId IS NULL" +
    " WHERE rule.isEnabled = true" +
    "   AND rule.isDeleted = false" +
    "   AND (rule.startDate IS NULL OR rule.startDate <= :date)" +
    "   AND (rule.endDate IS NULL OR rule.endDate > :date)" +
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
    "   AND (rule.endDate IS NULL OR rule.endDate > :date)" +
    " GROUP BY rule.domainEntity.id " +
    " ORDER BY totalScore DESC"
)
@Data
@EqualsAndHashCode(callSuper = true)
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

  @Column(name = "ACTIVITY_ID")
  protected long            activityId;

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

  @Enumerated(EnumType.ORDINAL)
  @Column(name = "RECURRENCE", nullable = false)
  private RecurrenceType    recurrence;

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "GAMIFICATION_PREREQUISITE_RULES", joinColumns = @JoinColumn(name = "RULE_ID"))
  @Column(name = "PREREQUISITE_RULE_ID")
  private Set<Long>         prerequisiteRules;

}

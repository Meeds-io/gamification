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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.exoplatform.commons.api.persistence.ExoEntity;

import io.meeds.gamification.constant.EntityType;
import io.meeds.gamification.constant.IdentityType;
import io.meeds.gamification.constant.RealizationStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

@ExoEntity
@Entity(name = "RealizationEntity")
@Table(name = "GAMIFICATION_ACTIONS_HISTORY")
@NamedQuery(name = "RealizationEntity.findAllRealizations", query = "SELECT"
    + " new io.meeds.gamification.model.StandardLeaderboard(g.earnerId, SUM(g.actionScore) as total)"
    + " FROM RealizationEntity g WHERE g.earnerType = :earnerType AND g.status = :status GROUP BY  g.earnerId ORDER BY total DESC")
@NamedQuery(name = "RealizationEntity.findRealizationsByEarnerIdSortedByDate", query = "SELECT g FROM RealizationEntity g WHERE g.earnerId = :earnerId AND g.status = :status ORDER BY g.createdDate DESC")
@NamedQuery(name = "RealizationEntity.findAllRealizationsByDateByDomain", query = "SELECT"
    + " new io.meeds.gamification.model.StandardLeaderboard(g.earnerId, SUM(g.actionScore) as total)"
    + " FROM RealizationEntity g WHERE g.createdDate >= :date  AND g.domainEntity.id = :domainId AND g.earnerType = :earnerType  AND g.status = :status GROUP BY  g.earnerId"
    + "     ORDER BY total DESC")
@NamedQuery(
  name = "RealizationEntity.getScoreByIdentityId",
  query = " SELECT SUM(g.actionScore) FROM RealizationEntity g" +
          " WHERE g.earnerId = :earnerId" +
          " AND g.status = :status"
)
@NamedQuery(name = "RealizationEntity.findAllRealizationsByDomain", query = "SELECT"
    + " new io.meeds.gamification.model.StandardLeaderboard(g.earnerId, SUM(g.actionScore) as total)"
    + " FROM RealizationEntity g WHERE g.domainEntity.id = :domainId AND g.earnerType = :earnerType AND g.status = :status GROUP BY  g.earnerId ORDER BY total DESC")
@NamedQuery(name = "RealizationEntity.findRealizationsByDate", query = "SELECT"
    + " new io.meeds.gamification.model.StandardLeaderboard(g.earnerId, SUM(g.actionScore) as total)"
    + " FROM RealizationEntity g  WHERE g.createdDate >= :date  AND g.earnerType = :earnerType AND g.status = :status GROUP BY  g.earnerId ORDER BY total DESC")
@NamedQuery(name = "RealizationEntity.findRealizationsByDateByDomain", query = "SELECT"
    + " new io.meeds.gamification.model.StandardLeaderboard(g.earnerId, SUM(g.actionScore) as total)"
    + " FROM RealizationEntity g" + " WHERE g.createdDate >= :date" + "     AND g.domainEntity.id = :domainId"
    + "     AND g.earnerType = :earnerType" + "     GROUP BY  g.earnerId" + "     ORDER BY total DESC")
@NamedQuery(name = "RealizationEntity.findStatsByUser", query = "SELECT"
    + " new io.meeds.gamification.model.PiechartLeaderboard(g.domainEntity.title,SUM(g.actionScore))"
    + " FROM RealizationEntity g" + " WHERE g.earnerId = :earnerId" + "     GROUP BY  g.domainEntity.title")
@NamedQuery(name = "RealizationEntity.findStatsByUserByDates", query = "SELECT"
    + " new io.meeds.gamification.model.PiechartLeaderboard(g.domainEntity.title,SUM(g.actionScore))"
    + " FROM RealizationEntity g WHERE g.earnerId = :earnerId AND g.createdDate >= :fromDate AND g.createdDate < :toDate"
    + " GROUP BY  g.domainEntity.title"
    + " ORDER BY SUM(g.actionScore) DESC")
@NamedQuery(name = "RealizationEntity.findDomainScoreByUserId", query = "SELECT"
    + " new io.meeds.gamification.model.ProfileReputation(g.domainEntity.id,SUM(g.actionScore))"
    + " FROM RealizationEntity g WHERE g.earnerId = :earnerId AND g.domainEntity IS NOT NULL GROUP BY  g.domainEntity.id")
@NamedQuery(name = "RealizationEntity.findUserReputationScoreBetweenDate", query = "SELECT SUM(g.actionScore) as total"
    + " FROM RealizationEntity g  WHERE g.earnerId = :earnerId AND g.status = :status AND g.createdDate >= :fromDate AND g.createdDate < :toDate")
@NamedQuery(name = "RealizationEntity.findUsersReputationScoreBetweenDate", query = "SELECT g.earnerId,SUM(g.actionScore) as total"
    + " FROM RealizationEntity g  WHERE g.earnerId IN :earnersId AND g.status = :status AND g.createdDate >= :fromDate AND g.createdDate < :toDate GROUP BY g.earnerId")
@NamedQuery(name = "RealizationEntity.findUserReputationScoreByMonth", query = "SELECT SUM(g.actionScore) as total"
    + " FROM RealizationEntity g WHERE g.earnerId = :earnerId AND g.createdDate >= :currentMonth")
@NamedQuery(name = "RealizationEntity.findUserReputationScoreByDomainBetweenDate", query = "SELECT SUM(g.actionScore) as total"
    + " FROM RealizationEntity g" + " WHERE g.earnerId = :earnerId" + "     AND g.domainEntity.id = :domainId"
    + "     AND g.createdDate >= :fromDate AND g.createdDate < :toDate")
@NamedQuery(name = "RealizationEntity.findAllLeaderboardBetweenDate", query = "SELECT"
    + " new io.meeds.gamification.model.StandardLeaderboard(g.earnerId, SUM(g.actionScore) as total)"
    + " FROM RealizationEntity g WHERE g.createdDate >= :fromDate AND g.createdDate < :toDate AND g.earnerType = :earnerType"
    + " GROUP BY  g.earnerId"
    + " ORDER BY total DESC")
@NamedQuery(name = "RealizationEntity.computeTotalScore", query = "SELECT SUM(a.actionScore)"
    + " FROM RealizationEntity a" + " WHERE a.earnerId = :earnerId")
@NamedQuery(name = "RealizationEntity.getAllPointsByDomain", query = "SELECT g"
    + " FROM RealizationEntity g" + " WHERE g.domainEntity.id = :domainId ")
@NamedQuery(name = "RealizationEntity.countRealizationsByRuleId", query = "SELECT COUNT(a) FROM RealizationEntity a where a.ruleEntity.id = :ruleId")
@NamedQuery(name = "RealizationEntity.countRealizationsByRuleIdAndEarnerType", query = "SELECT COUNT(a) FROM RealizationEntity a where a.ruleEntity.id = :ruleId AND a.earnerType = :earnerType")
@NamedQuery(
  name = "RealizationEntity.countRealizationsByRuleIdAndEarnerIdSinceDate",
  query = "SELECT COUNT(a) FROM RealizationEntity a"
      + " WHERE a.ruleEntity.id = :ruleId"
      + " AND a.earnerId = :earnerId"
      + " AND a.status = :status"
      + " AND a.createdDate >= :date"
)
@NamedQuery(
  name = "RealizationEntity.countRealizationsByRuleIdAndEarnerId",
  query = "SELECT COUNT(a) FROM RealizationEntity a"
      + " WHERE a.ruleEntity.id = :ruleId"
      + " AND a.earnerId = :earnerId"
      + " AND a.status = :status"
)
@NamedQuery(name = "RealizationEntity.findRealizationsByRuleId", query = "SELECT a FROM RealizationEntity a where a.ruleEntity.id = :ruleId order by a.id desc")
@NamedQuery(name = "RealizationEntity.findRealizationsByRuleIdAndEarnerType", query = "SELECT a FROM RealizationEntity a where a.ruleEntity.id = :ruleId AND a.earnerType = :earnerType order by a.id desc")
@NamedQuery(name = "RealizationEntity.findRealizationsByRuleIdAndDate", query = "SELECT a FROM RealizationEntity a where a.ruleEntity.id = :ruleId AND a.createdDate >= :fromDate AND a.createdDate < :toDate order by a.id desc")
@NamedQuery(name = "RealizationEntity.findRealizationsByRuleIdAndDateAndEarnerType", query = "SELECT a FROM RealizationEntity a where a.ruleEntity.id = :ruleId AND a.createdDate >= :fromDate AND a.createdDate < :toDate AND a.earnerType = :earnerType order by a.id desc")
@NamedQuery(
  name = "RealizationEntity.findReadlizationsByRuleIdAndEarnerIdAndReceiverAndObjectId",
  query = "SELECT g FROM RealizationEntity g" +
    " WHERE g.ruleEntity.id = :ruleId" +
    " AND g.earnerId = :earnerId" +
    " AND g.receiver = :receiverId" +
    " AND g.objectId = :objectId" +
    " AND g.objectType = :objectType" +
    " ORDER BY g.id DESC"
)

@NamedQuery(
  name = "RealizationEntity.countParticipantsBetweenDates",
  query = "SELECT COUNT(DISTINCT g.earnerId) FROM RealizationEntity g" +
      " WHERE g.createdDate >= :fromDate" +
      " AND g.createdDate < :toDate" +
      " AND g.earnerType = :earnerType" +
      " AND g.status = :status"
)

@NamedQuery(name = "RealizationEntity.getRealizationsByObjectIdAndObjectType", query = "SELECT g FROM RealizationEntity g"
    +
    " WHERE g.objectId = :objectId" +
    " AND g.objectType = :objectType")
@Data
@EqualsAndHashCode(callSuper = true)
public class RealizationEntity extends AbstractAuditingEntity implements Serializable {

  private static final long serialVersionUID = 2554394120711454093L;

  @Id
  @SequenceGenerator(name = "SEQ_GAMIFICATION_SCORE_HISTORY_ID", sequenceName = "SEQ_GAMIFICATION_SCORE_HISTORY_ID", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.AUTO, generator = "SEQ_GAMIFICATION_SCORE_HISTORY_ID")
  @Column(name = "ID")
  protected Long            id;

  @Column(name = "EARNER_ID", nullable = false)
  private String            earnerId;

  @Column(name = "EARNER_TYPE", nullable = false)
  private IdentityType      earnerType;

  @Column(name = "GLOBAL_SCORE", nullable = false)
  protected long            globalScore;

  @Column(name = "ACTION_TITLE", nullable = false)
  private String            actionTitle;

  @Column(name = "DOMAIN", nullable = false)
  private String            domain;

  @Column(name = "CONTEXT", nullable = true)
  private String            context;

  @Column(name = "ACTION_SCORE", nullable = false)
  private long              actionScore;

  @Column(name = "RECEIVER", nullable = false)
  private String            receiver;

  @Column(name = "OBJECT_ID")
  private String            objectId;

  @Column(name = "OBJECT_TYPE")
  private String            objectType;

  @ManyToOne
  @JoinColumn(name = "DOMAIN_ID")
  private ProgramEntity      domainEntity;

  @ManyToOne
  @JoinColumn(name = "RULE_ID")
  private RuleEntity        ruleEntity;

  @Column(name = "ACTIVITY_ID")
  private Long              activityId;

  @Column(name = "COMMENT")
  private String            comment;

  @Column(name = "CREATOR_ID")
  private Long              creator;

  @Enumerated(EnumType.ORDINAL)
  @Column(name = "STATUS", nullable = false)
  private RealizationStatus     status;

  @Enumerated(EnumType.ORDINAL)
  @Column(name = "TYPE", nullable = false)
  private EntityType        type;

}

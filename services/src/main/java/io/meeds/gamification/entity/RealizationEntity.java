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
import javax.persistence.NamedNativeQuery;
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
@NamedNativeQuery(
  name = "RealizationEntity.getLeaderboardRank",
  query = "SELECT LEADERBOARD.rankIndex FROM (" 
    + " SELECT earnerId, ROW_NUMBER() OVER() rankIndex FROM " 
    + " (SELECT g.EARNER_ID earnerId, SUM(g.ACTION_SCORE) total"
    + " FROM GAMIFICATION_ACTIONS_HISTORY g"
    + " WHERE g.EARNER_TYPE = :earnerType"
    + " AND g.STATUS = :status "
    + " GROUP BY g.EARNER_ID"
    + " ORDER BY total DESC, earnerId DESC) GROUPED_LEADERBOARD"
    + ") LEADERBOARD WHERE LEADERBOARD.earnerId = :earnerId"
)
@NamedNativeQuery(
  name = "RealizationEntity.getLeaderboardRankByDateAndProgramId",
  query = "SELECT LEADERBOARD.rankIndex FROM (" 
      + " SELECT earnerId, ROW_NUMBER() OVER() rankIndex FROM " 
      + " (SELECT g.EARNER_ID earnerId, SUM(g.ACTION_SCORE) total"
      + " FROM GAMIFICATION_ACTIONS_HISTORY g"
      + " WHERE g.CREATED_DATE >= :date"
      + " AND g.DOMAIN_ID = :domainId"
      + " AND g.EARNER_TYPE = :earnerType"
      + " AND g.STATUS = :status "
      + " GROUP BY g.EARNER_ID"
      + " ORDER BY total DESC, earnerId DESC) GROUPED_LEADERBOARD"
      + " ) LEADERBOARD WHERE LEADERBOARD.earnerId = :earnerId"
)
@NamedNativeQuery(
  name = "RealizationEntity.getLeaderboardRankByProgramId",
  query = "SELECT LEADERBOARD.rankIndex FROM (" 
    + " SELECT earnerId, ROW_NUMBER() OVER() rankIndex FROM " 
    + " (SELECT g.EARNER_ID earnerId, SUM(g.ACTION_SCORE) total"
    + " FROM GAMIFICATION_ACTIONS_HISTORY g"
    + " WHERE g.DOMAIN_ID = :domainId"
    + " AND g.EARNER_TYPE = :earnerType"
    + " AND g.STATUS = :status "
    + " GROUP BY g.EARNER_ID"
    + " ORDER BY total DESC, earnerId DESC) GROUPED_LEADERBOARD"
    + ") LEADERBOARD WHERE LEADERBOARD.earnerId = :earnerId"
)
@NamedNativeQuery(
  name = "RealizationEntity.getLeaderboardRankByDate",
  query = "SELECT LEADERBOARD.rankIndex FROM (" 
    + " SELECT earnerId, ROW_NUMBER() OVER() rankIndex FROM " 
    + " (SELECT g.EARNER_ID earnerId, SUM(g.ACTION_SCORE) total"
    + " FROM GAMIFICATION_ACTIONS_HISTORY g"
    + " WHERE g.CREATED_DATE >= :date"
    + " AND g.EARNER_TYPE = :earnerType"
    + " AND g.STATUS = :status "
    + " GROUP BY g.EARNER_ID"
    + " ORDER BY total DESC, earnerId DESC) GROUPED_LEADERBOARD"
    + ") LEADERBOARD WHERE LEADERBOARD.earnerId = :earnerId"
)

@NamedQuery(
  name = "RealizationEntity.getLeaderboard",
  query = "SELECT new io.meeds.gamification.model.StandardLeaderboard(g.earnerId as earnerId, SUM(g.actionScore) as total)"
    + " FROM RealizationEntity g"
    + " WHERE g.earnerType = :earnerType"
    + " AND g.status = :status"
    + " GROUP BY g.earnerId"
    + " ORDER BY total DESC, earnerId DESC"
)
@NamedQuery(
  name = "RealizationEntity.getLeaderboardByDateAndProgramId",
  query = "SELECT new io.meeds.gamification.model.StandardLeaderboard(g.earnerId as earnerId, SUM(g.actionScore) as total)"
    + " FROM RealizationEntity g"
    + " WHERE g.createdDate >= :date"
    + " AND g.domainEntity.id = :domainId"
    + " AND g.earnerType = :earnerType"
    + " AND g.status = :status"
    + " GROUP BY  g.earnerId"
    + " ORDER BY total DESC, earnerId DESC"
)
@NamedQuery(
  name = "RealizationEntity.getLeaderboardByProgramId",
  query = "SELECT new io.meeds.gamification.model.StandardLeaderboard(g.earnerId as earnerId, SUM(g.actionScore) as total)"
    + " FROM RealizationEntity g"
    + " WHERE g.domainEntity.id = :domainId"
    + " AND g.earnerType = :earnerType"
    + " AND g.status = :status"
    + " GROUP BY g.earnerId"
    + " ORDER BY total DESC, earnerId DESC"
)
@NamedQuery(
  name = "RealizationEntity.getLeaderboardByDate",
  query = "SELECT new io.meeds.gamification.model.StandardLeaderboard(g.earnerId as earnerId, SUM(g.actionScore) as total)"
    + " FROM RealizationEntity g"
    + " WHERE g.createdDate >= :date"
    + " AND g.earnerType = :earnerType"
    + " AND g.status = :status"
    + " GROUP BY g.earnerId"
    + " ORDER BY total DESC, earnerId DESC"
)

@NamedQuery(
  name = "RealizationEntity.getLeaderboardStatsByIdentityId",
  query = "SELECT new io.meeds.gamification.model.PiechartLeaderboard(g.domainEntity.id as domainId, SUM(g.actionScore) as total)"
    + " FROM RealizationEntity g"
    + " WHERE g.earnerId = :earnerId"
    + " AND g.status = :status"
    + " GROUP BY g.domainEntity.id"
    + " ORDER BY total DESC, domainId DESC"
)
@NamedQuery(
  name = "RealizationEntity.getLeaderboardStatsByIdentityIdAndDates",
  query = "SELECT new io.meeds.gamification.model.PiechartLeaderboard(g.domainEntity.id as domainId, SUM(g.actionScore) as total)"
    + " FROM RealizationEntity g"
    + " WHERE g.earnerId = :earnerId"
    + " AND g.status = :status"
    + " AND g.createdDate >= :fromDate"
    + " AND g.createdDate < :toDate"
    + " GROUP BY  g.domainEntity.id"
    + " ORDER BY total DESC, domainId DESC"
)

@NamedQuery(
  name = "RealizationEntity.getScoreByIdentityId",
  query = " SELECT SUM(g.actionScore) FROM RealizationEntity g" +
          " WHERE g.earnerId = :earnerId" +
          " AND g.status = :status"
)
@NamedQuery(name = "RealizationEntity.getScorePerProgramByIdentityId", query = "SELECT"
    + " new io.meeds.gamification.model.ProfileReputation(g.domainEntity.id,SUM(g.actionScore))"
    + " FROM RealizationEntity g WHERE g.earnerId = :earnerId AND g.status = :status AND g.domainEntity IS NOT NULL GROUP BY g.domainEntity.id")
@NamedQuery(name = "RealizationEntity.getScoreByIdentityIdAndBetweenDates", query = "SELECT SUM(g.actionScore) as total"
    + " FROM RealizationEntity g  WHERE g.earnerId = :earnerId AND g.status = :status AND g.createdDate >= :fromDate AND g.createdDate < :toDate")
@NamedQuery(name = "RealizationEntity.getScoreByIdentityIdsAndBetweenDates", query = "SELECT g.earnerId,SUM(g.actionScore) as total"
    + " FROM RealizationEntity g  WHERE g.earnerId IN :earnersId AND g.status = :status AND g.createdDate >= :fromDate AND g.createdDate < :toDate GROUP BY g.earnerId")
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
@NamedQuery(
  name = "RealizationEntity.findReadlizationsByRuleIdAndEarnerIdAndReceiverAndObjectId",
  query = "SELECT g FROM RealizationEntity g" +
    " WHERE g.ruleEntity.id = :ruleId" +
    " AND g.earnerId = :earnerId" +
    " AND g.receiver = :receiverId" +
    " AND g.objectId = :objectId" +
    " AND g.objectType = :objectType" +
    " AND g.status = :status" +
    " ORDER BY g.id DESC"
)
@NamedQuery(
  name = "RealizationEntity.getRealizationsByObjectIdAndObjectType",
  query = "SELECT g FROM RealizationEntity g" +
    " WHERE g.objectId = :objectId" +
    " AND g.objectType = :objectType"
)
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

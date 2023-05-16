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
import java.util.Objects;

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
import io.meeds.gamification.constant.HistoryStatus;
import io.meeds.gamification.constant.IdentityType;

@ExoEntity
@Entity(name = "RealizationEntity")
@Table(name = "GAMIFICATION_ACTIONS_HISTORY")
@NamedQuery(name = "RealizationEntity.findAllRealizations", query = "SELECT"
    + " new io.meeds.gamification.model.StandardLeaderboard(g.earnerId, SUM(g.actionScore) as total)"
    + " FROM RealizationEntity g WHERE g.earnerType = :earnerType AND g.status = :status GROUP BY  g.earnerId ORDER BY total DESC")
@NamedQuery(name = "RealizationEntity.findRealizationsByEarnerIdSortedByDate", query = "SELECT g FROM RealizationEntity g WHERE g.earnerId = :earnerId AND g.status = :status ORDER BY g.createdDate DESC")
@NamedQuery(name = "RealizationEntity.findRealizationsByEarnerIdAndByType", query = "SELECT g FROM RealizationEntity g WHERE g.earnerId = :earnerId AND g.type = :type")
@NamedQuery(name = "RealizationEntity.findAllRealizationsByDateByDomain", query = "SELECT"
    + " new io.meeds.gamification.model.StandardLeaderboard(g.earnerId, SUM(g.actionScore) as total)"
    + " FROM RealizationEntity g WHERE g.createdDate >= :date  AND g.domainEntity.id = :domainId AND g.earnerType = :earnerType  AND g.status = :status GROUP BY  g.earnerId"
    + "     ORDER BY total DESC")
@NamedQuery(name = "RealizationEntity.findRealizationsByEarnerId", query = "SELECT a"
    + " FROM RealizationEntity a" + " WHERE a.earnerId = :earnerId" + "     ORDER BY a.globalScore DESC")
@NamedQuery(name = "RealizationEntity.findAllRealizationsByDomain", query = "SELECT"
    + " new io.meeds.gamification.model.StandardLeaderboard(g.earnerId, SUM(g.actionScore) as total)"
    + " FROM RealizationEntity g WHERE g.domainEntity.id = :domainId AND g.earnerType = :earnerType AND g.status = :status GROUP BY  g.earnerId ORDER BY total DESC")
@NamedQuery(name = "RealizationEntity.findActionHistoryByDateByEarnerId", query = "SELECT a"
    + " FROM RealizationEntity a" + " WHERE a.createdDate = :date" + "     AND a.earnerId = :earnerId"
    + "     ORDER BY a.globalScore DESC")
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
  name = "RealizationEntity.countRealizationsEarnerIdSinceDate",
  query = "SELECT COUNT(a) FROM RealizationEntity a"
      + " WHERE a.ruleEntity.id = :ruleId"
      + " AND a.earnerId = :earnerId"
      + " AND a.status = :status"
      + " AND a.createdDate >= :date"
)
@NamedQuery(name = "RealizationEntity.findRealizationsByRuleId", query = "SELECT a FROM RealizationEntity a where a.ruleEntity.id = :ruleId order by a.id desc")
@NamedQuery(name = "RealizationEntity.findRealizationsByRuleIdAndEarnerType", query = "SELECT a FROM RealizationEntity a where a.ruleEntity.id = :ruleId AND a.earnerType = :earnerType order by a.id desc")
@NamedQuery(name = "RealizationEntity.findRealizationsByRuleIdAndDate", query = "SELECT a FROM RealizationEntity a where a.ruleEntity.id = :ruleId AND a.createdDate >= :fromDate AND a.createdDate < :toDate order by a.id desc")
@NamedQuery(name = "RealizationEntity.findRealizationsByRuleIdAndDateAndEarnerType", query = "SELECT a FROM RealizationEntity a where a.ruleEntity.id = :ruleId AND a.createdDate >= :fromDate AND a.createdDate < :toDate AND a.earnerType = :earnerType order by a.id desc")
@NamedQuery(name = "RealizationEntity.findMostRealizedRuleIds", query = "SELECT r.id FROM RealizationEntity a" +
    " JOIN a.ruleEntity r " +
    " ON  (r.startDate IS NULL OR r.startDate <= :nowDate)" +
    " AND (r.endDate IS NULL OR r.endDate >= :nowDate)" +
    " AND r.isEnabled = true AND r.isDeleted = false" +
    " JOIN a.domainEntity d " +
    " ON d.audienceId IS NULL OR d.audienceId IN (:spacesIds)" +
    " WHERE a.type= :type" +
    " AND a.createdDate >= :fromDate AND a.createdDate < :toDate" +
    " group by r.id order by count(*) DESC")

@NamedQuery(name = "RealizationEntity.findActionHistoryByActionTitleAndEarnerIdAndReceiverAndObjectId", query = "SELECT g FROM RealizationEntity g"
    +
    " WHERE g.actionTitle = :actionTitle" +
    " AND g.domainEntity.id = :domainId" +
    " AND g.earnerId = :earnerId" +
    " AND g.receiver = :receiverId" +
    " AND g.objectId = :objectId" +
    " AND g.objectType = :objectType")

@NamedQuery(name = "RealizationEntity.getRealizationsByObjectIdAndObjectType", query = "SELECT g FROM RealizationEntity g"
    +
    " WHERE g.objectId = :objectId" +
    " AND g.objectType = :objectType")
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
  private HistoryStatus     status;

  @Enumerated(EnumType.ORDINAL)
  @Column(name = "TYPE", nullable = false)
  private EntityType        type;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getEarnerId() {
    return earnerId;
  }

  public void setEarnerId(String earnerId) {
    this.earnerId = earnerId;
  }

  public IdentityType getEarnerType() {
    return earnerType;
  }

  public void setEarnerType(IdentityType earnerType) {
    this.earnerType = earnerType;
  }

  public long getGlobalScore() {
    return globalScore;
  }

  public void setGlobalScore(long globalScore) {
    this.globalScore = globalScore;
  }

  public String getActionTitle() {
    return actionTitle;
  }

  public void setActionTitle(String actionTitle) {
    this.actionTitle = actionTitle;
  }

  public String getDomain() {
    return domain;
  }

  public void setDomain(String domain) {
    this.domain = domain;
  }

  public String getContext() {
    return context;
  }

  public void setContext(String context) {
    this.context = context;
  }

  public long getActionScore() {
    return actionScore;
  }

  public void setActionScore(long actionScore) {
    this.actionScore = actionScore;
  }

  public String getReceiver() {
    return receiver;
  }

  public void setReceiver(String receiver) {
    this.receiver = receiver;
  }

  public String getObjectId() {
    return objectId;
  }

  public void setObjectId(String objectId) {
    this.objectId = objectId;
  }

  public String getObjectType() {
    return objectType;
  }

  public void setObjectType(String objectType) {
    this.objectType = objectType;
  }

  public ProgramEntity getDomainEntity() {
    return domainEntity;
  }

  public void setDomainEntity(ProgramEntity domainEntity) {
    this.domainEntity = domainEntity;
  }

  public RuleEntity getRuleEntity() {
    return ruleEntity;
  }

  public void setRuleEntity(RuleEntity ruleEntity) {
    this.ruleEntity = ruleEntity;
  }

  public Long getActivityId() {
    return activityId;
  }

  public void setActivityId(Long activityId) {
    this.activityId = activityId;
  }

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  public Long getCreator() {
    return creator;
  }

  public void setCreator(Long creator) {
    this.creator = creator;
  }

  public HistoryStatus getStatus() {
    return status;
  }

  public void setStatus(HistoryStatus status) {
    this.status = status;
  }

  public EntityType getType() {
    return type;
  }

  public void setType(EntityType type) {
    this.type = type;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + Objects.hash(actionScore,
                                           actionTitle,
                                           activityId,
                                           comment,
                                           context,
                                           creator,
                                           domain,
                                           domainEntity,
                                           earnerId,
                                           earnerType,
                                           globalScore,
                                           id,
                                           objectId,
                                           objectType,
                                           receiver,
                                           ruleEntity,
                                           status,
                                           type);
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof RealizationEntity)) {
      return false;
    }
    if (this == obj) {
      return true;
    }
    if (!super.equals(obj)) {
      return false;
    }
    RealizationEntity other = (RealizationEntity) obj;
    return actionScore == other.actionScore && Objects.equals(actionTitle, other.actionTitle)
        && Objects.equals(activityId, other.activityId) && Objects.equals(comment, other.comment)
        && Objects.equals(context, other.context) && Objects.equals(creator, other.creator)
        && Objects.equals(domain, other.domain) && Objects.equals(domainEntity, other.domainEntity)
        && Objects.equals(earnerId, other.earnerId) && earnerType == other.earnerType && globalScore == other.globalScore
        && Objects.equals(id, other.id) && Objects.equals(objectId, other.objectId)
        && Objects.equals(objectType, other.objectType) && Objects.equals(receiver, other.receiver)
        && Objects.equals(ruleEntity, other.ruleEntity) && status == other.status && type == other.type;
  }

}

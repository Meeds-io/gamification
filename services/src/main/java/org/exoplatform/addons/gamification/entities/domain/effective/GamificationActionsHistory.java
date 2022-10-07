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
package org.exoplatform.addons.gamification.entities.domain.effective;

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

import org.exoplatform.addons.gamification.IdentityType;
import org.exoplatform.addons.gamification.entities.domain.configuration.AbstractAuditingEntity;
import org.exoplatform.addons.gamification.entities.domain.configuration.DomainEntity;
import org.exoplatform.addons.gamification.service.dto.configuration.constant.HistoryStatus;
import org.exoplatform.commons.api.persistence.ExoEntity;

@ExoEntity
@Entity(name = "GamificationActionsHistory")
@Table(name = "GAMIFICATION_ACTIONS_HISTORY")
@NamedQuery(
    name = "GamificationActionsHistory.findAllActionsHistory",
    query = "SELECT"
        + " new org.exoplatform.addons.gamification.service.effective.StandardLeaderboard(g.earnerId, SUM(g.actionScore) as total)"
        + " FROM GamificationActionsHistory g WHERE g.earnerType = :earnerType AND g.status <> :status GROUP BY  g.earnerId ORDER BY total DESC"
)
@NamedQuery(
    name = "GamificationActionsHistory.findActionsHistoryByEarnerIdSortedByDate",
    query = "SELECT g FROM GamificationActionsHistory g WHERE g.earnerId = :earnerId AND g.status <> :status ORDER BY g.createdDate DESC"
)
@NamedQuery(
    name = "GamificationActionsHistory.findAllActionsHistoryByDateByDomain",
    query = "SELECT"
        + " new org.exoplatform.addons.gamification.service.effective.StandardLeaderboard(g.earnerId, SUM(g.actionScore) as total)"
        + " FROM GamificationActionsHistory g WHERE g.createdDate >= :date  AND g.domain = :domain AND g.earnerType = :earnerType  AND g.status <> :status GROUP BY  g.earnerId"
        + "     ORDER BY total DESC"
)
@NamedQuery(
    name = "GamificationActionsHistory.findActionsHistoryByEarnerId",
    query = "SELECT a"
        + " FROM GamificationActionsHistory a" + " WHERE a.earnerId = :earnerId" + "     ORDER BY a.globalScore DESC"
)
@NamedQuery(
    name = "GamificationActionsHistory.findAllActionsHistoryByDomain",
    query = "SELECT"
        + " new org.exoplatform.addons.gamification.service.effective.StandardLeaderboard(g.earnerId, SUM(g.actionScore) as total)"
        + " FROM GamificationActionsHistory g WHERE g.domain = :domain AND g.earnerType = :earnerType AND g.status <> :status GROUP BY  g.earnerId ORDER BY total DESC"
)
@NamedQuery(
    name = "GamificationActionsHistory.findActionHistoryByDateByEarnerId",
    query = "SELECT a"
        + " FROM GamificationActionsHistory a" + " WHERE a.createdDate = :date" + "     AND a.earnerId = :earnerId"
        + "     ORDER BY a.globalScore DESC"
)
@NamedQuery(
    name = "GamificationActionsHistory.findActionsHistoryByDate",
    query = "SELECT"
        + " new org.exoplatform.addons.gamification.service.effective.StandardLeaderboard(g.earnerId, SUM(g.actionScore) as total)"
        + " FROM GamificationActionsHistory g  WHERE g.createdDate >= :date  AND g.earnerType = :earnerType AND g.status <> :status GROUP BY  g.earnerId ORDER BY total DESC"
)
@NamedQuery(
    name = "GamificationActionsHistory.findActionsHistoryByDateByDomain",
    query = "SELECT"
        + " new org.exoplatform.addons.gamification.service.effective.StandardLeaderboard(g.earnerId, SUM(g.actionScore) as total)"
        + " FROM GamificationActionsHistory g" + " WHERE g.createdDate >= :date" + "     AND g.domain = :domain"
        + "     AND g.earnerType = :earnerType" + "     GROUP BY  g.earnerId" + "     ORDER BY total DESC"
)
@NamedQuery(
    name = "GamificationActionsHistory.findStatsByUser",
    query = "SELECT"
        + " new org.exoplatform.addons.gamification.service.effective.PiechartLeaderboard(g.domainEntity.title,SUM(g.actionScore))"
        + " FROM GamificationActionsHistory g" + " WHERE g.earnerId = :earnerId" + "     GROUP BY  g.domainEntity.title"
)
@NamedQuery(
    name = "GamificationActionsHistory.findStatsByUserByDates",
    query = "SELECT"
        + " new org.exoplatform.addons.gamification.service.effective.PiechartLeaderboard(g.domainEntity.title,SUM(g.actionScore))"
        + " FROM GamificationActionsHistory g WHERE g.earnerId = :earnerId AND g.createdDate >= :fromDate AND g.createdDate < :toDate"
        + " GROUP BY  g.domainEntity.title")
@NamedQuery(
    name = "GamificationActionsHistory.findDomainScoreByUserId",
    query = "SELECT"
        + " new org.exoplatform.addons.gamification.service.effective.ProfileReputation(g.domain,SUM(g.actionScore))"
        + " FROM GamificationActionsHistory g" + " WHERE g.earnerId = :earnerId" + "     GROUP BY  g.domain"
)
@NamedQuery(
    name = "GamificationActionsHistory.findUserReputationScoreBetweenDate",
    query = "SELECT SUM(g.actionScore) as total"
        + " FROM GamificationActionsHistory g  WHERE g.earnerId = :earnerId AND g.status <> :status AND g.createdDate >= :fromDate AND g.createdDate < :toDate"
)
@NamedQuery(
    name = "GamificationActionsHistory.findUserReputationScoreByMonth",
    query = "SELECT SUM(g.actionScore) as total"
        + " FROM GamificationActionsHistory g WHERE g.earnerId = :earnerId AND g.createdDate >= :currentMonth"
)
@NamedQuery(
    name = "GamificationActionsHistory.findUserReputationScoreByDomainBetweenDate",
    query = "SELECT SUM(g.actionScore) as total"
        + " FROM GamificationActionsHistory g" + " WHERE g.earnerId = :earnerId" + "     AND g.domain = :domain"
        + "     AND g.createdDate >= :fromDate AND g.createdDate < :toDate"
)
@NamedQuery(
    name = "GamificationActionsHistory.findAllLeaderboardBetweenDate",
    query = "SELECT"
        + " new org.exoplatform.addons.gamification.service.effective.StandardLeaderboard(g.earnerId, SUM(g.actionScore) as total)"
        + " FROM GamificationActionsHistory g WHERE g.createdDate >= :fromDate AND g.createdDate < :toDate AND g.earnerType = :earnerType"
        + " GROUP BY  g.earnerId"
        + " ORDER BY total DESC"
)
@NamedQuery(
    name = "GamificationActionsHistory.computeTotalScore",
    query = "SELECT SUM(a.actionScore)"
        + " FROM GamificationActionsHistory a" + " WHERE a.earnerId = :earnerId"
)
@NamedQuery(
    name = "GamificationActionsHistory.getAllPointsByDomain",
    query = "SELECT g"
        + " FROM GamificationActionsHistory g" + " WHERE g.domain = :domain "
)
@NamedQuery(
    name = "GamificationActionsHistory.getAllPointsWithNullDomain",
    query = "SELECT g"
        + " FROM GamificationActionsHistory g" + " WHERE g.domainEntity IS NULL "
)
@NamedQuery(
    name = "GamificationActionsHistory.getDomainList",
    query = "SELECT g.domain"
        + " FROM GamificationActionsHistory g" + "     GROUP BY  g.domain"
)
@NamedQuery(
    name = "GamificationActionsHistory.countAnnouncementsByChallenge",
    query = "SELECT COUNT(a) FROM GamificationActionsHistory a where a.ruleId = :challengeId"
)
@NamedQuery(
    name = "GamificationActionsHistory.findAllAnnouncementByChallenge",
    query = "SELECT DISTINCT a FROM GamificationActionsHistory a where a.ruleId = :challengeId order by a.id desc"
)
@NamedQuery(
    name = "GamificationActionsHistory.findRealizationsByDateAndRules",
    query = "SELECT DISTINCT g FROM GamificationActionsHistory g "
        + " WHERE g.earnerType = :type"
        + " AND g.createdDate >= :fromDate AND g.createdDate < :toDate"
        + " AND ((g.ruleId IS NOT NULL AND g.ruleId IN (:ruleIds)) \n"
        + "      OR (g.actionTitle IS NOT NULL AND g.actionTitle IN (:ruleEventNames))) \n"
        + " ORDER BY g.id DESC"
)
@NamedQuery(
    name = "GamificationActionsHistory.findRealizationsByDateAndRulesSearchByDomainIds",
    query = "SELECT DISTINCT g FROM GamificationActionsHistory g "
        + " WHERE g.earnerType = :type"
        + " AND g.createdDate >= :fromDate AND g.createdDate < :toDate"
        + " AND ( g.domainEntity.id IN (:domainIds) )"
        + " AND ((g.ruleId IS NOT NULL AND g.ruleId IN (:ruleIds)) \n"
        + "      OR (g.actionTitle IS NOT NULL AND g.actionTitle IN (:ruleEventNames))) \n"
        + " ORDER BY g.id DESC"
)
@NamedQuery(
    name = "GamificationActionsHistory.findRealizationsByEarnerAndDateAndRules",
    query = "SELECT DISTINCT g FROM GamificationActionsHistory g "
        + " WHERE g.earnerType = :type"
        + " AND g.earnerId = :earnerId"
        + " AND g.createdDate >= :fromDate AND g.createdDate < :toDate"
        + " AND ((g.ruleId IS NOT NULL AND g.ruleId IN (:ruleIds)) \n"
        + "      OR (g.actionTitle IS NOT NULL AND g.actionTitle IN (:ruleEventNames))) \n"
        + " ORDER BY g.id DESC"
)
@NamedQuery(
    name = "GamificationActionsHistory.findRealizationsByEarnerAndDateAndRulesSearchByDomainIds",
    query = "SELECT DISTINCT g FROM GamificationActionsHistory g "
        + " WHERE g.earnerType = :type"
        + " AND g.earnerId = :earnerId"
        + " AND g.createdDate >= :fromDate AND g.createdDate < :toDate"
        + " AND ( g.domainEntity.id IN (:domainIds) )"
        + " AND ((g.ruleId IS NOT NULL AND g.ruleId IN (:ruleIds)) \n"
        + "      OR (g.actionTitle IS NOT NULL AND g.actionTitle IN (:ruleEventNames))) \n"
        + " ORDER BY g.id DESC"
)
public class GamificationActionsHistory extends AbstractAuditingEntity implements Serializable {
  private static final long serialVersionUID = 1L;

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

  @Column(name = "OBJECT_ID", nullable = false)
  private String            objectId;

  @ManyToOne
  @JoinColumn(name = "DOMAIN_ID")
  private DomainEntity      domainEntity;

  @Column(name = "RULE_ID")
  private Long              ruleId;

  @Column(name = "ACTIVITY_ID")
  private Long              activityId;

  @Column(name = "COMMENT")
  private String            comment;

  @Column(name = "CREATOR_ID")
  private Long              creator;

  @Enumerated(EnumType.ORDINAL)
  @Column(name = "STATUS", nullable = false)
  private HistoryStatus status;

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

  public DomainEntity getDomainEntity() {
    return domainEntity;
  }

  public void setDomainEntity(DomainEntity domainEntity) {
    this.domainEntity = domainEntity;
  }

  public Long getRuleId() {
    return ruleId;
  }

  public void setRuleId(Long ruleId) {
    this.ruleId = ruleId;
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
}

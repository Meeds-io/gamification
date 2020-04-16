package org.exoplatform.addons.gamification.entities.domain.effective;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import org.exoplatform.addons.gamification.IdentityType;
import org.exoplatform.addons.gamification.entities.domain.configuration.AbstractAuditingEntity;
import org.exoplatform.addons.gamification.entities.domain.configuration.DomainEntity;
import org.exoplatform.commons.api.persistence.ExoEntity;

@ExoEntity
@Entity(name = "GamificationActionsHistory")
@Table(name = "GAMIFICATION_ACTIONS_HISTORY")
@NamedQueries({
    @NamedQuery(name = "GamificationActionsHistory.findAllActionsHistory", query = "SELECT" +
        " new org.exoplatform.addons.gamification.service.effective.StandardLeaderboard(g.earnerId, SUM(g.actionScore) as total)"
        +
        " FROM GamificationActionsHistory g" +
        "     WHERE g.earnerType = :earnerType" +
        "     GROUP BY  g.earnerId" +
        "     ORDER BY total DESC"),
    @NamedQuery(name = "GamificationActionsHistory.findActionsHistoryByEarnerIdSortedByDate", query = "SELECT g " +
        " FROM GamificationActionsHistory g" +
        " WHERE g.earnerId = :earnerId" +
        "     ORDER BY g.date DESC"),
    @NamedQuery(name = "GamificationActionsHistory.findAllActionsHistoryByDateByDomain", query = "SELECT" +
        " new org.exoplatform.addons.gamification.service.effective.StandardLeaderboard(g.earnerId, SUM(g.actionScore) as total)"
        +
        " FROM GamificationActionsHistory g "
        +
        " WHERE g.date >= :date" +
        "     AND g.domain = :domain" +
        "     AND g.earnerType = :earnerType" +
        "     GROUP BY  g.earnerId" +
        "     ORDER BY total DESC"),
    @NamedQuery(name = "GamificationActionsHistory.findActionsHistoryByEarnerId", query = "SELECT a" +
        " FROM GamificationActionsHistory a" +
        " WHERE a.earnerId = :earnerId" +
        "     ORDER BY a.globalScore DESC"),
    @NamedQuery(name = "GamificationActionsHistory.findAllActionsHistoryByDomain", query = "SELECT" +
        " new org.exoplatform.addons.gamification.service.effective.StandardLeaderboard(g.earnerId, SUM(g.actionScore) as total)"
        +
        " FROM GamificationActionsHistory g" +
        " WHERE g.domain = :domain" +
        "     AND g.earnerType = :earnerType" +
        "     GROUP BY  g.earnerId" +
        "     ORDER BY total DESC"),
    @NamedQuery(name = "GamificationActionsHistory.findActionHistoryByDateByEarnerId", query = "SELECT a"
        +
        " FROM GamificationActionsHistory a" +
        " WHERE a.date = :date" +
        "     AND a.earnerId = :earnerId" +
        "     ORDER BY a.globalScore DESC"),
    @NamedQuery(name = "GamificationActionsHistory.findActionsHistoryByDate", query = "SELECT"
        +
        " new org.exoplatform.addons.gamification.service.effective.StandardLeaderboard(g.earnerId, SUM(g.actionScore) as total)"
        +
        " FROM GamificationActionsHistory g" +
        " WHERE g.date >= :date" +
        "     AND g.earnerType = :earnerType" +
        "     GROUP BY  g.earnerId" +
        "     ORDER BY total DESC"),
    @NamedQuery(name = "GamificationActionsHistory.findActionsHistoryByDateByDomain", query = "SELECT" +
        " new org.exoplatform.addons.gamification.service.effective.StandardLeaderboard(g.earnerId, SUM(g.actionScore) as total)"
        +
        " FROM GamificationActionsHistory g" +
        " WHERE g.date >= :date" +
        "     AND g.domain = :domain" +
        "     AND g.earnerType = :earnerType" +
        "     GROUP BY  g.earnerId" +
        "     ORDER BY total DESC"),
    @NamedQuery(name = "GamificationActionsHistory.findStatsByUser", query = "SELECT"
        +
        " new org.exoplatform.addons.gamification.service.effective.PiechartLeaderboard(g.domain,SUM(g.actionScore))"
        +
        " FROM GamificationActionsHistory g" +
        " WHERE g.earnerId = :earnerId" +
        "     GROUP BY  g.domain"),
    @NamedQuery(name = "GamificationActionsHistory.findDomainScoreByUserId", query = "SELECT"
        +
        " new org.exoplatform.addons.gamification.service.effective.ProfileReputation(g.domain,SUM(g.actionScore))"
        +
        " FROM GamificationActionsHistory g" +
        " WHERE g.earnerId = :earnerId" +
        "     GROUP BY  g.domain"),
    @NamedQuery(name = "GamificationActionsHistory.findUserReputationScoreBetweenDate", query = "SELECT SUM(g.actionScore) as total"
        +
        " FROM GamificationActionsHistory g" +
        " WHERE g.earnerId = :earnerId" +
        "     AND g.date BETWEEN :fromDate" +
        "     AND :toDate"),
    @NamedQuery(name = "GamificationActionsHistory.findUserReputationScoreByMonth", query = "SELECT SUM(g.actionScore) as total" +
        " FROM GamificationActionsHistory g" +
        " WHERE g.earnerId = :earnerId" +
        "     AND g.date >= :currentMonth"),
    @NamedQuery(name = "GamificationActionsHistory.findUserReputationScoreByDomainBetweenDate", query = "SELECT SUM(g.actionScore) as total"
        +
        " FROM GamificationActionsHistory g" +
        " WHERE g.earnerId = :earnerId" +
        "     AND g.domain = :domain" +
        "     AND g.date BETWEEN :fromDate" +
        "     AND :toDate"),
    @NamedQuery(name = "GamificationActionsHistory.findAllLeaderboardBetweenDate", query = "SELECT" +
        " new org.exoplatform.addons.gamification.service.effective.StandardLeaderboard(g.earnerId, SUM(g.actionScore) as total)"
        +
        " FROM GamificationActionsHistory g" +
        " WHERE g.date BETWEEN :fromDate" +
        "     AND :toDate" +
        "     AND g.earnerType = :earnerType" +
        "     GROUP BY  g.earnerId" +
        "     ORDER BY total DESC"),
    @NamedQuery(name = "GamificationActionsHistory.computeTotalScore", query = "SELECT SUM(a.actionScore)"
        +
        " FROM GamificationActionsHistory a" +
        " WHERE a.earnerId = :earnerId"),
    @NamedQuery(name = "GamificationActionsHistory.getAllPointsByDomain", query = "SELECT g" +
        " FROM GamificationActionsHistory g" +
        " WHERE g.domain = :domain "),
    @NamedQuery(name = "GamificationActionsHistory.getAllPointsWithNullDomain", query = "SELECT g" +
        " FROM GamificationActionsHistory g" +
        " WHERE g.domainEntity IS NULL "),
    @NamedQuery(name = "GamificationActionsHistory.getDomainList", query = "SELECT g.domain" +
        " FROM GamificationActionsHistory g" +
        "     GROUP BY  g.domain")
})
public class GamificationActionsHistory extends AbstractAuditingEntity implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id
  @SequenceGenerator(name = "SEQ_GAMIFICATION_SCORE_HISTORY_ID", sequenceName = "SEQ_GAMIFICATION_SCORE_HISTORY_ID")
  @GeneratedValue(strategy = GenerationType.AUTO, generator = "SEQ_GAMIFICATION_SCORE_HISTORY_ID")
  @Column(name = "ID")
  protected Long            id;

  @Temporal(TemporalType.DATE)
  @Column(name = "ACTION_DATE")
  private Date              date;

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

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Date getDate() {
    return date;
  }

  public void setDate(Date date) {
    this.date = date;
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

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof GamificationActionsHistory)) {
      return false;
    }
    GamificationActionsHistory that = (GamificationActionsHistory) obj;
    EqualsBuilder eb = new EqualsBuilder();
    eb.append(date, that.date);
    eb.append(earnerId, that.earnerId);
    eb.append(globalScore, that.globalScore);
    eb.append(actionTitle, that.actionTitle);
    eb.append(domain, that.domain);
    eb.append(context, that.context);
    eb.append(actionScore, that.actionScore);
    eb.append(receiver, that.receiver);
    eb.append(objectId, that.objectId);
    return eb.isEquals();
  }

  @Override
  public int hashCode() {
    HashCodeBuilder hcb = new HashCodeBuilder();
    hcb.append(date);
    hcb.append(earnerId);
    hcb.append(globalScore);
    hcb.append(actionTitle);
    hcb.append(domain);
    hcb.append(actionScore);
    hcb.append(receiver);
    hcb.append(objectId);
    return hcb.toHashCode();
  }

  @Override
  public String toString() {
    return "Gamification History{" +
        "date=" +
        date +
        ", earner_id=" +
        earnerId +
        ", earner_type=" +
        earnerType +
        ", global_score=" +
        globalScore +
        ", action_title=" +
        actionTitle +
        ", domain=" +
        domain +
        ", context=" +
        context +
        ", action_score=" +
        actionScore +
        ", receiver=" +
        receiver +
        ", objectId=" +
        objectId +
        '}';
  }

}

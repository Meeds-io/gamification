package org.exoplatform.addons.gamification.service.dto.configuration;

import org.exoplatform.addons.gamification.Utils.Utils;
import org.exoplatform.addons.gamification.entities.domain.effective.GamificationActionsHistory;
import java.io.Serializable;

public class GamificationActionsHistoryDTO implements Serializable {

  private Long   id;

  private String date;

  private String earnerId;

  private String earnerType;

  protected long globalScore;

  private String actionTitle;

  private String domain;

  private String context;

  private long   actionScore;

  private String receiver;

  private String objectId;

  private Long   ruleId;

  private Long   activityId;

  private String comment;

  public GamificationActionsHistoryDTO() {
  }

  public GamificationActionsHistoryDTO(GamificationActionsHistory gamificationActionsHistoryEntity) {
    this.id = gamificationActionsHistoryEntity.getId();
    this.date = Utils.toRFC3339Date(gamificationActionsHistoryEntity.getDate());
    this.earnerId = gamificationActionsHistoryEntity.getEarnerId();
    this.earnerType = gamificationActionsHistoryEntity.getEarnerType().toString();
    this.globalScore = gamificationActionsHistoryEntity.getGlobalScore();
    this.actionScore = gamificationActionsHistoryEntity.getActionScore();
    this.actionTitle = gamificationActionsHistoryEntity.getActionTitle();
    this.domain = gamificationActionsHistoryEntity.getDomain();
    this.context = gamificationActionsHistoryEntity.getContext();
    this.receiver = gamificationActionsHistoryEntity.getReceiver();
    this.objectId = gamificationActionsHistoryEntity.getObjectId();
    this.activityId = gamificationActionsHistoryEntity.getActivityId();
    this.context = gamificationActionsHistoryEntity.getComment();
    if (gamificationActionsHistoryEntity.getRuleEntity() != null) {
      this.ruleId = gamificationActionsHistoryEntity.getRuleEntity().getId();
    }
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
  }

  public String getEarnerId() {
    return earnerId;
  }

  public void setEarnerId(String earnerId) {
    this.earnerId = earnerId;
  }

  public String getEarnerType() {
    return earnerType;
  }

  public void setEarnerType(String earnerType) {
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
}

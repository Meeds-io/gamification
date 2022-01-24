package org.exoplatform.addons.gamification.service.dto.effective;

public class GamificationActionsHistoryDTO implements Cloneable {

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

  private Long creator;

  public GamificationActionsHistoryDTO(Long id,
                                       String date,
                                       String earnerId,
                                       String earnerType,
                                       long globalScore,
                                       String actionTitle,
                                       String domain,
                                       String context,
                                       long actionScore,
                                       String receiver,
                                       String objectId,
                                       Long ruleId,
                                       Long activityId,
                                       String comment,
                                       Long creator) {
    this.id = id;
    this.date = date;
    this.earnerId = earnerId;
    this.earnerType = earnerType;
    this.globalScore = globalScore;
    this.actionTitle = actionTitle;
    this.domain = domain;
    this.context = context;
    this.actionScore = actionScore;
    this.receiver = receiver;
    this.objectId = objectId;
    this.ruleId = ruleId;
    this.activityId = activityId;
    this.comment = comment;
    this.creator = creator;
  }

  @Override
  public GamificationActionsHistoryDTO clone() { // NOSONAR
    return new GamificationActionsHistoryDTO(id,
            date,
            earnerId,
            earnerType,
            globalScore,
            actionTitle,
            domain,
            context,
            actionScore,
            receiver,
            objectId,
            ruleId,
            activityId,
            comment,
            creator);
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

  public Long getCreator() {
    return creator;
  }

  public void setCreator(Long creator) {
    this.creator = creator;
  }
}

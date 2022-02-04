package org.exoplatform.addons.gamification.service.dto.configuration;

public class GamificationActionsHistoryRestEntity implements Cloneable {

  private Long     id;

  private UserInfo earner;

  private RuleDTO  action;

  private String  actionLabel;

  private UserInfo receiver;

  private String   comment;

  private UserInfo creator;

  private String   createdDate;

  private String   status;

  private String   space;


  public GamificationActionsHistoryRestEntity(Long id, UserInfo earner, RuleDTO action, String actionLabel, UserInfo receiver, String comment, UserInfo creator, String createdDate, String status, String space) {
    this.id = id;
    this.earner = earner;
    this.action = action;
    this.actionLabel = actionLabel;
    this.receiver = receiver;
    this.comment = comment;
    this.creator = creator;
    this.createdDate = createdDate;
    this.status = status;
    this.space = space;
  }

  public GamificationActionsHistoryRestEntity() {
  }

  @Override
  public GamificationActionsHistoryRestEntity clone() { // NOSONAR
    return new GamificationActionsHistoryRestEntity(id, earner, action, actionLabel, receiver, comment, creator, createdDate, status, space);
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public UserInfo getEarner() {
    return earner;
  }

  public void setEarner(UserInfo earner) {
    this.earner = earner;
  }

  public RuleDTO getAction() {
    return action;
  }

  public void setAction(RuleDTO action) {
    this.action = action;
  }

  public UserInfo getReceiver() {
    return receiver;
  }

  public void setReceiver(UserInfo receiver) {
    this.receiver = receiver;
  }

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  public UserInfo getCreator() {
    return creator;
  }

  public void setCreator(UserInfo creator) {
    this.creator = creator;
  }

  public String getCreatedDate() {
    return createdDate;
  }

  public void setCreatedDate(String createdDate) {
    this.createdDate = createdDate;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getSpace() {
    return space;
  }

  public void setSpace(String space) {
    this.space = space;
  }

  public String getActionLabel() {
    return actionLabel;
  }

  public void setActionLabel(String actionLabel) {
    this.actionLabel = actionLabel;
  }
}

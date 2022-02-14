package org.exoplatform.addons.gamification.service.dto.configuration;

public class GamificationActionsHistoryRestEntity implements Cloneable {

  private Long     id;

  private UserInfo earner;

  private RuleDTO  action;

  private DomainDTO domain;

  private String   actionLabel;

  private Long     score;

  private String   comment;

  private UserInfo creator;

  private String   createdDate;

  private String   status;

  private String   space;

  public GamificationActionsHistoryRestEntity(Long id,
                                              UserInfo earner,
                                              RuleDTO action,
                                              DomainDTO domain,
                                              String actionLabel,
                                              Long score,
                                              String comment,
                                              UserInfo creator,
                                              String createdDate,
                                              String status,
                                              String space) { // NOSONAR
    this.id = id;
    this.earner = earner;
    this.action = action;
    this.domain = domain;
    this.actionLabel = actionLabel;
    this.score = score;
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
    return new GamificationActionsHistoryRestEntity(id,
                                                    earner,
                                                    action,
                                                    domain,
                                                    actionLabel,
                                                    score,
                                                    comment,
                                                    creator,
                                                    createdDate,
                                                    status,
                                                    space);
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

  public String getActionLabel() {
    return actionLabel;
  }

  public void setActionLabel(String actionLabel) {
    this.actionLabel = actionLabel;
  }

  public Long getScore() {
    return score;
  }

  public void setScore(Long score) {
    this.score = score;
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

  public DomainDTO getDomain() {
    return domain;
  }

  public void setDomain(DomainDTO domain) {
    this.domain = domain;
  }
}

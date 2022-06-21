package org.exoplatform.addons.gamification.service.dto.configuration;

public class GamificationActionsHistoryRestEntity implements Cloneable {

  private Long     id;

  private String earner;

  private RuleDTO  action;

  private DomainDTO domain;

  private String   actionLabel;

  private Long     score;

  private String   createdDate;

  private String   status;

  private String url;

  public GamificationActionsHistoryRestEntity(Long id,
                                              String earner,
                                              RuleDTO action,
                                              DomainDTO domain,
                                              String actionLabel,
                                              Long score,
                                              String createdDate,
                                              String status,
                                              String url) { // NOSONAR
    this.id = id;
    this.earner = earner;
    this.action = action;
    this.domain = domain;
    this.actionLabel = actionLabel;
    this.score = score;
    this.createdDate = createdDate;
    this.status = status;
    this.url = url;
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
                                                    createdDate,
                                                    status,
                                                    url);
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getEarner() {
    return earner;
  }

  public void setEarner(String earner) {
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

  public DomainDTO getDomain() {
    return domain;
  }

  public void setDomain(DomainDTO domain) {
    this.domain = domain;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

}

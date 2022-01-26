package org.exoplatform.addons.gamification.service.dto.configuration;

public class Announcement implements Cloneable {
  private long   id;

  private Long   challengeId;

  private Long   assignee;

  private String comment;

  private Long   creator;

  private String createdDate;

  private Long   activityId;

  private String createdBy;

  private String lastModifiedBy;

  private String lastModifiedDate;

  public Announcement(long id,
                      Long challengeId,
                      Long assignee,
                      String comment,
                      Long creator,
                      String createdDate,
                      Long activityId,
                      String createdBy,
                      String lastModifiedBy,
                      String lastModifiedDate) {
    this.id = id;
    this.challengeId = challengeId;
    this.assignee = assignee;
    this.comment = comment;
    this.creator = creator;
    this.createdDate = createdDate;
    this.activityId = activityId;
    this.createdBy = createdBy;
    this.lastModifiedBy = lastModifiedBy;
    this.lastModifiedDate = lastModifiedDate;
  }

  public Announcement() {
  }

  @Override
  public Announcement clone() { // NOSONAR
    return new Announcement(id, challengeId, assignee, comment, creator, createdDate, activityId, createdBy, lastModifiedBy, lastModifiedDate);
  }
  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public Long getChallengeId() {
    return challengeId;
  }

  public void setChallengeId(Long challengeId) {
    this.challengeId = challengeId;
  }

  public Long getAssignee() {
    return assignee;
  }

  public void setAssignee(Long assignee) {
    this.assignee = assignee;
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

  public String getCreatedDate() {
    return createdDate;
  }

  public void setCreatedDate(String createdDate) {
    this.createdDate = createdDate;
  }

  public Long getActivityId() {
    return activityId;
  }

  public void setActivityId(Long activityId) {
    this.activityId = activityId;
  }

  public String getCreatedBy() {
    return createdBy;
  }

  public void setCreatedBy(String createdBy) {
    this.createdBy = createdBy;
  }

  public String getLastModifiedBy() {
    return lastModifiedBy;
  }

  public void setLastModifiedBy(String lastModifiedBy) {
    this.lastModifiedBy = lastModifiedBy;
  }

  public String getLastModifiedDate() {
    return lastModifiedDate;
  }

  public void setLastModifiedDate(String lastModifiedDate) {
    this.lastModifiedDate = lastModifiedDate;
  }
}

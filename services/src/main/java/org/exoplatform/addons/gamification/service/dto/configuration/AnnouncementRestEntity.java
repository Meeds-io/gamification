package org.exoplatform.addons.gamification.service.dto.configuration;

public class AnnouncementRestEntity implements Cloneable {

  private long     id;

  private Long     challengeId;

  private UserInfo assignee;

  private String   comment;

  private UserInfo creator;

  private String   createdDate;

  private Long     activityId;

  public AnnouncementRestEntity(long id,
                                Long challengeId,
                                UserInfo assignee,
                                String comment,
                                UserInfo creator,
                                String createdDate,
                                Long activityId) {
    this.id = id;
    this.challengeId = challengeId;
    this.assignee = assignee;
    this.comment = comment;
    this.creator = creator;
    this.createdDate = createdDate;
    this.activityId = activityId;
  }
  @Override
  public AnnouncementRestEntity clone() { // NOSONAR
    return new AnnouncementRestEntity(id, challengeId, assignee, comment, creator, createdDate, activityId);
  }

  public AnnouncementRestEntity() {
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

  public UserInfo getAssignee() {
    return assignee;
  }

  public void setAssignee(UserInfo assignee) {
    this.assignee = assignee;
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

  public Long getActivityId() {
    return activityId;
  }

  public void setActivityId(Long activityId) {
    this.activityId = activityId;
  }
}

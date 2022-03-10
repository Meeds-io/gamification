package org.exoplatform.addons.gamification.service.dto.configuration;

public class AnnouncementRestEntity implements Cloneable {

  private long     id;

  private String   assignee;

  private String   createdDate;

  private Long     activityId;

  public AnnouncementRestEntity(long id,
                                String assignee,
                                String createdDate,
                                Long activityId) {
    this.id = id;
    this.assignee = assignee;
    this.createdDate = createdDate;
    this.activityId = activityId;
  }
  @Override
  public AnnouncementRestEntity clone() { // NOSONAR
    return new AnnouncementRestEntity(id, assignee, createdDate, activityId);
  }

  public AnnouncementRestEntity() {
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getAssignee() {
    return assignee;
  }

  public void setAssignee(String assignee) {
    this.assignee = assignee;
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

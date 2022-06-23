package org.exoplatform.addons.gamification.service.dto.configuration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Announcement implements Cloneable {
  private long   id;

  private Long   challengeId;

  private String challengeTitle;

  private Long   assignee;

  private String comment;

  private Long   creator;

  private String createdDate;

  private Long   activityId;

  public Announcement(long id,
                      Long challengeId,
                      Long assignee,
                      String comment,
                      Long creator,
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
  public Announcement clone() { // NOSONAR
    return new Announcement(id, challengeId, challengeTitle, assignee, comment, creator, createdDate, activityId);
  }

}

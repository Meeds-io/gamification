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

  @Override
  public Announcement clone() { // NOSONAR
    return new Announcement(id, challengeId, challengeTitle, assignee, comment, creator, createdDate, activityId);
  }

}

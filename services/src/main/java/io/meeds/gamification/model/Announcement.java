package io.meeds.gamification.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Announcement implements Cloneable {

  protected long   id;

  protected Long   challengeId;

  protected String challengeTitle;

  protected Long   assignee;

  protected String comment;

  protected Long   creator;

  protected String createdDate;

  protected Long   activityId;

  @Override
  public Announcement clone() { // NOSONAR
    return new Announcement(id,
                            challengeId,
                            challengeTitle,
                            assignee,
                            comment,
                            creator,
                            createdDate,
                            activityId);
  }

}

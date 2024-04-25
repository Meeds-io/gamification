package io.meeds.gamification.rest.model;

import io.meeds.gamification.model.UserInfo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RealizationRestEntity implements Cloneable {

  private Long              id;

  private UserInfo          earner;

  private RuleRestEntity    action;

  private ProgramRestEntity program;

  private String            programLabel;

  private String            actionLabel;

  private Long              score;

  private String            creator;

  private String            createdDate;

  private String            sendingDate;

  private String            status;

  private String            space;

  private String            objectId;

  private String            objectType;

  private Long              activityId;

  private String            comment;

  private boolean           actionLabelChanged;

  private boolean           programLabelChanged;

  private UserInfo          reviewer;

  @Override
  public RealizationRestEntity clone() { // NOSONAR
    return new RealizationRestEntity(id,
                                     earner,
                                     action,
                                     program,
                                     programLabel,
                                     actionLabel,
                                     score,
                                     creator,
                                     createdDate,
                                     sendingDate,
                                     status,
                                     space,
                                     objectId,
                                     objectType,
                                     activityId,
                                     comment,
                                     actionLabelChanged,
                                     programLabelChanged,
                                     reviewer);
  }
}

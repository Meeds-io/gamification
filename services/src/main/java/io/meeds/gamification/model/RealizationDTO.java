package io.meeds.gamification.model;

import io.meeds.gamification.constant.EntityType;
import lombok.Data;

@Data
public class RealizationDTO implements Cloneable {

  private Long       id;

  private String     earnerId;

  private String     earnerType;

  protected long     globalScore;

  private String     actionTitle;

  private ProgramDTO program;

  private String     programLabel;

  private String     context;

  private long       actionScore;

  private String     receiver;

  private String     objectId;

  private String     objectType;

  private Long       ruleId;

  private Long       activityId;

  private String     comment;

  private Long       creator;

  private String     createdBy;

  private String     createdDate;

  private String     lastModifiedBy;

  private String     sendingDate;

  private String     status;

  private EntityType type;

  private Long       reviewerId;

  public RealizationDTO(Long id, // NOSONAR
                        String earnerId,
                        String earnerType,
                        long globalScore,
                        String actionTitle,
                        ProgramDTO program,
                        String programLabel,
                        String context,
                        long actionScore,
                        String receiver,
                        String objectId,
                        String objectType,
                        Long ruleId,
                        Long activityId,
                        String comment,
                        Long creator,
                        String createdBy,
                        String createdDate,
                        String lastModifiedBy,
                        String sendingDate,
                        String status,
                        EntityType type,
                        Long reviewerId) { // NOSONAR
    this.id = id;
    this.earnerId = earnerId;
    this.earnerType = earnerType;
    this.globalScore = globalScore;
    this.actionTitle = actionTitle;
    this.program = program;
    this.programLabel = programLabel;
    this.context = context;
    this.actionScore = actionScore;
    this.receiver = receiver;
    this.objectId = objectId;
    this.objectType = objectType;
    this.ruleId = ruleId;
    this.activityId = activityId;
    this.comment = comment;
    this.creator = creator;
    this.createdBy = createdBy;
    this.createdDate = createdDate;
    this.lastModifiedBy = lastModifiedBy;
    this.sendingDate = sendingDate;
    this.status = status;
    this.type = type;
    this.reviewerId = reviewerId;
  }

  public RealizationDTO() {
  }

  @Override
  public RealizationDTO clone() { // NOSONAR
    return new RealizationDTO(id,
                              earnerId,
                              earnerType,
                              globalScore,
                              actionTitle,
                              program,
                              programLabel,
                              context,
                              actionScore,
                              receiver,
                              objectId,
                              objectType,
                              ruleId,
                              activityId,
                              comment,
                              creator,
                              createdBy,
                              createdDate,
                              lastModifiedBy,
                              sendingDate,
                              status,
                              type,
                              reviewerId);
  }
}

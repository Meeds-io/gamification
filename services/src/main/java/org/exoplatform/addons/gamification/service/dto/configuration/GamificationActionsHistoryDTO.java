package org.exoplatform.addons.gamification.service.dto.configuration;

import lombok.Getter;
import lombok.Setter;
import org.exoplatform.addons.gamification.service.dto.configuration.constant.EntityType;

@Getter
@Setter
public class GamificationActionsHistoryDTO implements Cloneable {

  private Long       id;

  private String     earnerId;

  private String     earnerType;

  protected long     globalScore;

  private String     actionTitle;

  private DomainDTO  domainDTO;

  private String     context;

  private long       actionScore;

  private String     receiver;

  private String     objectId;

  private Long       ruleId;

  private Long       activityId;

  private String     comment;

  private Long       creator;

  private String     createdBy;

  private String     createdDate;

  private String     lastModifiedBy;

  private String     lastModifiedDate;

  private String     status;

  private EntityType type;

  public GamificationActionsHistoryDTO(Long id, // NOSONAR
                                       String earnerId,
                                       String earnerType,
                                       long globalScore,
                                       String actionTitle,
                                       DomainDTO domainDTO,
                                       String context,
                                       long actionScore,
                                       String receiver,
                                       String objectId,
                                       Long ruleId,
                                       Long activityId,
                                       String comment,
                                       Long creator,
                                       String createdBy,
                                       String createdDate,
                                       String lastModifiedBy,
                                       String lastModifiedDate,
                                       String status,
                                       EntityType type) { // NOSONAR
    this.id = id;
    this.earnerId = earnerId;
    this.earnerType = earnerType;
    this.globalScore = globalScore;
    this.actionTitle = actionTitle;
    this.domainDTO = domainDTO;
    this.context = context;
    this.actionScore = actionScore;
    this.receiver = receiver;
    this.objectId = objectId;
    this.ruleId = ruleId;
    this.activityId = activityId;
    this.comment = comment;
    this.creator = creator;
    this.createdBy = createdBy;
    this.createdDate = createdDate;
    this.lastModifiedBy = lastModifiedBy;
    this.lastModifiedDate = lastModifiedDate;
    this.status = status;
    this.type = type;
  }

  public GamificationActionsHistoryDTO() {
  }

  @Override
  public GamificationActionsHistoryDTO clone() { // NOSONAR
    return new GamificationActionsHistoryDTO(id,
                                             earnerId,
                                             earnerType,
                                             globalScore,
                                             actionTitle,
                                             domainDTO,
                                             context,
                                             actionScore,
                                             receiver,
                                             objectId,
                                             ruleId,
                                             activityId,
                                             comment,
                                             creator,
                                             createdBy,
                                             createdDate,
                                             lastModifiedBy,
                                             lastModifiedDate,
                                             status,
                                             type);
  }
}

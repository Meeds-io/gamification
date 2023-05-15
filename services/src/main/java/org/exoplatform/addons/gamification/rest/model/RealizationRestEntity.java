package org.exoplatform.addons.gamification.rest.model;

import lombok.Getter;
import lombok.Setter;
import org.exoplatform.addons.gamification.service.dto.configuration.ProgramDTO;
import org.exoplatform.addons.gamification.service.dto.configuration.RuleDTO;
import org.exoplatform.social.rest.entity.IdentityEntity;

@Getter
@Setter
public class RealizationRestEntity implements Cloneable {

  private Long           id;

  private IdentityEntity earner;

  private RuleDTO        action;

  private ProgramDTO     domain;

  private String         domainLabel;

  private String         actionLabel;

  private Long           score;

  private String         creator;

  private String         createdDate;

  private String         status;

  private String         space;

  private String         objectId;

  private String         objectType;


  public RealizationRestEntity(Long id, // NOSONAR
                                              IdentityEntity earner,
                                              RuleDTO action,
                                              ProgramDTO domain,
                                              String domainLabel,
                                              String actionLabel,
                                              Long score,
                                              String creator,
                                              String createdDate,
                                              String status,
                                              String space,
                                              String objectId,
                                              String objectType) {
    this.id = id;
    this.earner = earner;
    this.action = action;
    this.domain = domain;
    this.domainLabel = domainLabel;
    this.actionLabel = actionLabel;
    this.score = score;
    this.creator = creator;
    this.createdDate = createdDate;
    this.status = status;
    this.space = space;
    this.objectId = objectId;
    this.objectType = objectType;
  }

  @Override
  public RealizationRestEntity clone() { // NOSONAR
    return new RealizationRestEntity(id,
                                                    earner,
                                                    action,
                                                    domain,
                                                    domainLabel,
                                                    actionLabel,
                                                    score,
                                                    creator,
                                                    createdDate,
                                                    status,
                                                    space,
                                                    objectId,
                                                    objectType);
  }
}

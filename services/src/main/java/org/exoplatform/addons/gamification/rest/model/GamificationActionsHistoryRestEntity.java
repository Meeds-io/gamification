package org.exoplatform.addons.gamification.rest.model;

import lombok.Getter;
import lombok.Setter;
import org.exoplatform.addons.gamification.service.dto.configuration.DomainDTO;
import org.exoplatform.addons.gamification.service.dto.configuration.RuleDTO;
import org.exoplatform.social.rest.entity.IdentityEntity;

@Getter
@Setter
public class GamificationActionsHistoryRestEntity implements Cloneable {

  private Long           id;

  private IdentityEntity earner;

  private RuleDTO        action;

  private DomainDTO      domain;

  private String         domainLabel;

  private String         actionLabel;

  private Long           score;

  private String         creator;

  private String         createdDate;

  private String         status;

  private String         space;

  private String         objectId;

  private String         objectType;


  public GamificationActionsHistoryRestEntity(Long id, // NOSONAR
                                              IdentityEntity earner,
                                              RuleDTO action,
                                              DomainDTO domain,
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
  public GamificationActionsHistoryRestEntity clone() { // NOSONAR
    return new GamificationActionsHistoryRestEntity(id,
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

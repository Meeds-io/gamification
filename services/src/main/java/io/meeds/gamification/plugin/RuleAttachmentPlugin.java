package io.meeds.gamification.plugin;

import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.exoplatform.services.security.Identity;
import org.exoplatform.social.attachment.AttachmentPlugin;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.space.model.Space;
import org.exoplatform.social.core.space.spi.SpaceService;

import io.meeds.gamification.model.RuleDTO;
import io.meeds.gamification.service.ProgramService;
import io.meeds.gamification.service.RuleService;

public class RuleAttachmentPlugin extends AttachmentPlugin {

  public static final String RULE_ATTACHMENT_TYPE = "rule";

  private ProgramService     programService;

  private RuleService        ruleService;

  private SpaceService       spaceService;

  private IdentityManager    identityManager;

  public RuleAttachmentPlugin(ProgramService programService,
                              RuleService ruleService,
                              SpaceService spaceService,
                              IdentityManager identityManager) {
    this.programService = programService;
    this.ruleService = ruleService;
    this.spaceService = spaceService;
    this.identityManager = identityManager;
  }

  @Override
  public String getObjectType() {
    return RULE_ATTACHMENT_TYPE;
  }

  @Override
  public boolean hasAccessPermission(Identity userIdentity, String entityId) throws ObjectNotFoundException {
    try {
      long ruleId = Long.parseLong(entityId);
      String username = userIdentity.getUserId();
      return ruleService.findRuleById(ruleId, username) != null;
    } catch (IllegalAccessException e) {
      return false;
    }
  }

  @Override
  public boolean hasEditPermission(Identity userIdentity, String entityId) throws ObjectNotFoundException {
    try {
      long ruleId = Long.parseLong(entityId);
      String username = userIdentity.getUserId();
      RuleDTO rule = ruleService.findRuleById(ruleId, username);
      return rule != null && programService.isProgramOwner(rule.getProgramId(), username);
    } catch (IllegalAccessException e) {
      return false;
    }
  }

  @Override
  public long getAudienceId(String objectId) throws ObjectNotFoundException {
    long spaceId = getSpaceId(objectId);
    if (spaceId == 0) {
      return 0;
    }
    Space space = spaceService.getSpaceById(String.valueOf(spaceId));
    if (space == null) {
      throw new ObjectNotFoundException(String.format("Space with id %s wasn't found",
                                                      spaceId));
    }
    return Long.parseLong(identityManager.getOrCreateSpaceIdentity(space.getPrettyName()).getId());
  }

  @Override
  public long getSpaceId(String objectId) throws ObjectNotFoundException {
    long ruleId = Long.parseLong(objectId);
    RuleDTO rule = this.ruleService.findRuleById(ruleId);
    if (rule == null) {
      throw new ObjectNotFoundException(String.format("Rule with id %s wasn't found",
                                                      ruleId));
    }
    return rule.getProgram().getSpaceId();
  }

}

package io.meeds.gamification.plugin;

import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.exoplatform.services.security.Identity;
import org.exoplatform.social.attachment.AttachmentPlugin;

public class RuleAttachmentPlugin extends AttachmentPlugin  {

  public static final String    RULE_ATTACHMENT_TYPE = "rule";
  @Override
  public String getObjectType() {
    return RULE_ATTACHMENT_TYPE;
  }

  @Override
  public boolean hasAccessPermission(Identity userIdentity, String entityId) throws ObjectNotFoundException {
    return true;
  }

  @Override
  public boolean hasEditPermission(Identity userIdentity, String entityId) throws ObjectNotFoundException {
    return true;
  }

  @Override
  public long getAudienceId(String objectId) throws ObjectNotFoundException {
    return 0;
  }

  @Override
  public long getSpaceId(String objectId) throws ObjectNotFoundException {
    return 0;
  }
}

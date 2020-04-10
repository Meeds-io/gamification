package org.exoplatform.addons.gamification;

import org.apache.commons.lang3.StringUtils;

import org.exoplatform.social.core.identity.provider.OrganizationIdentityProvider;
import org.exoplatform.social.core.identity.provider.SpaceIdentityProvider;

public enum IdentityType {
  USER,
  SPACE;

  public static IdentityType getType(String type) {
    if (StringUtils.isBlank(type) || StringUtils.equalsIgnoreCase(type, OrganizationIdentityProvider.NAME)) {
      return USER;
    }
    IdentityType identityType = IdentityType.valueOf(type.toUpperCase());
    return identityType == null ? USER : identityType;
  }

  public String getProviderId() {
    switch (this) {
    case SPACE:
      return SpaceIdentityProvider.NAME;
    case USER:
      return OrganizationIdentityProvider.NAME;
    default:
      return OrganizationIdentityProvider.NAME;
    }
  }

  public boolean isUser() {
    return USER.equals(this);
  }

  public boolean isSpace() {
    return SPACE.equals(this);
  }
}

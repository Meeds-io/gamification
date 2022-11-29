/*
 * This file is part of the Meeds project (https://meeds.io/).
 * Copyright (C) 2020 Meeds Association
 * contact@meeds.io
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.exoplatform.addons.gamification.constant;

import org.apache.commons.lang3.StringUtils;

import org.exoplatform.social.core.identity.provider.OrganizationIdentityProvider;
import org.exoplatform.social.core.identity.provider.SpaceIdentityProvider;

public enum IdentityType {
  USER, SPACE;

  public static IdentityType getType(String type) {
    if (StringUtils.isBlank(type) || StringUtils.equalsIgnoreCase(type, OrganizationIdentityProvider.NAME)) {
      return USER;
    }
    IdentityType identityType = IdentityType.valueOf(type.toUpperCase());
    return identityType;
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

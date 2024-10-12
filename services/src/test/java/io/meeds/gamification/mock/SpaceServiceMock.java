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
package io.meeds.gamification.mock;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import org.exoplatform.commons.utils.ListAccess;
import org.exoplatform.commons.utils.ListAccessImpl;
import org.exoplatform.services.security.MembershipEntry;
import org.exoplatform.social.core.application.PortletPreferenceRequiredPlugin;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.space.SpaceApplicationConfigPlugin;
import org.exoplatform.social.core.space.SpaceException;
import org.exoplatform.social.core.space.SpaceFilter;
import org.exoplatform.social.core.space.SpaceListAccess;
import org.exoplatform.social.core.space.SpaceListenerPlugin;
import org.exoplatform.social.core.space.model.Space;
import org.exoplatform.social.core.space.spi.SpaceLifeCycleListener;
import org.exoplatform.social.core.space.spi.SpaceService;

@SuppressWarnings("all")
public class SpaceServiceMock implements SpaceService {

  public static final String       SPACE_PRETTY_NAME  = "space150";

  public static final String       SPACE_PRETTY_NAME2 = "space152";

  public static final String       SPACE_GROUP_ID     = "/spaces/" + SPACE_PRETTY_NAME;

  public static final String       SPACE_DISPLAY_NAME = "test space";

  public static final String       SPACE_ID_1         = "1";

  public static final String       SPACE_ID_2         = "200";

  public static final List<String> SPACE_MEMBERS      = Arrays.asList(new String[] {
                                                                                     "root",
                                                                                     "root10",
                                                                                     "root5",
                                                                                     "root1"
  });

  public static final List<String> SPACE_MANAGERS     = Arrays.asList(new String[] {
                                                                                     "root",
                                                                                     "root5",
                                                                                     "root1"
  });

  public Space getSpaceByDisplayName(String spaceDisplayName) {
    if (SPACE_DISPLAY_NAME.equals(spaceDisplayName)) {
      return getSpace();
    } else {
      throw new UnsupportedOperationException(spaceDisplayName);
    }
  }

  public Space getSpaceByPrettyName(String spacePrettyName) {
    if (SPACE_PRETTY_NAME.equals(spacePrettyName)) {
      return getSpace(SPACE_ID_1);
    } else if (SPACE_PRETTY_NAME2.equals(spacePrettyName)) {
      return getSpace(SPACE_ID_2);
    } else {
      throw new UnsupportedOperationException(spacePrettyName);
    }
  }

  public Space getSpaceByGroupId(String groupId) {
    if (groupId.equals(SPACE_GROUP_ID)) {
      return getSpace();
    } else {
      throw new UnsupportedOperationException(groupId);
    }
  }

  public Space getSpaceById(String spaceId) {
    if (!SPACE_ID_1.equals(spaceId) && !SPACE_ID_2.equals(spaceId)) {
      return null;
    }
    return getSpace(spaceId);
  }

  public boolean isRedactor(Space space, String userId) {
    return StringUtils.equals(userId, "root");
  }

  @Override
  public boolean hasRedactor(Space space) {
    return SPACE_PRETTY_NAME.equals(space.getPrettyName());
  }

  @Override
  public boolean canRedactOnSpace(Space space, org.exoplatform.services.security.Identity viewer) {
    if (viewer == null) {
      throw new IllegalStateException("User ACL Identity is mandatory");
    }
    return space != null && (!SPACE_PRETTY_NAME.equals(space.getPrettyName())
                             || SPACE_MANAGERS.contains(viewer.getUserId()));
  }

  public boolean canPublishOnSpace(Space space, String userId) {
    if (userId == null) {
      throw new IllegalStateException("User ACL Identity is mandatory");
    }
    return space != null && (!SPACE_PRETTY_NAME.equals(space.getPrettyName())
                             || SPACE_MANAGERS.contains(userId));
  }

  public boolean canViewSpace(Space space, String userId) {
    return isMember(space, userId);
  }

  public boolean isContentManager(String username) {
    return isSuperManager(username);
  }

  public boolean isContentPublisher(String username) {
    return isSuperManager(username);
  }

  public boolean isMember(Space space, String userId) {
    return space != null && SPACE_MEMBERS.contains(userId);
  }

  public boolean isManager(Space space, String userId) {
    return space != null && SPACE_MANAGERS.contains(userId);
  }

  public ListAccess<Space> getMemberSpaces(String username) {
    if (SPACE_MEMBERS.contains(username)) {
      return new ListAccessImpl(Space.class, Collections.singletonList(getSpace()));
    } else {
      return new ListAccessImpl(Space.class, Collections.emptyList());
    }
  }

  public List<String> getMemberSpacesIds(String username, int offset, int limit) {
    if (SPACE_MEMBERS.contains(username)) {
      return Collections.singletonList(SPACE_ID_1);
    } else {
      return Collections.emptyList();
    }
  }

  public List<String> getManagerSpacesIds(String username, int offset, int limit) {
    if (SPACE_MANAGERS.contains(username)) {
      return Collections.singletonList(SPACE_ID_1);
    } else {
      return Collections.emptyList();
    }
  }

  public Space updateSpace(Space existingSpace) {
    String SpacePrettyName = existingSpace.getPrettyName();
    String description = "updateSpace";
    existingSpace.setPrettyName(existingSpace.getPrettyName());
    existingSpace.setDescription(description);
    existingSpace.setId(String.valueOf(SpacePrettyName.charAt(SpacePrettyName.length() - 1)));
    existingSpace.setGroupId("/spaces/" + SpacePrettyName);
    return existingSpace;
  }

  public boolean isSuperManager(String userId) {
    if ("root".equals(userId)) {
      return true;
    } else {
      return false;
    }
  }

  @Override
  public List<String> findExternalInvitationsSpacesByEmail(String email) {
    return Collections.emptyList();
  }

  private Space getSpace() {
    return getSpace(SPACE_ID_1);
  }

  private Space getSpace(String spaceId) {
    Space space = new Space();
    space.setId(spaceId);
    space.setPrettyName(SPACE_PRETTY_NAME);
    space.setDisplayName(SPACE_DISPLAY_NAME);
    space.setGroupId("/spaces/" + SPACE_PRETTY_NAME);
    space.setManagers(new String[] {
                                     "root1",
    });
    space.setMembers(SPACE_MEMBERS.toArray(new String[0]));
    space.setRegistration(SPACE_ID_1.equals(spaceId) ? Space.VALIDATION : Space.OPEN);
    return space;
  }

}

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
    if (space == null) {
      throw new IllegalStateException("Space is mandatory");
    }
    return !SPACE_PRETTY_NAME.equals(space.getPrettyName())
        || SPACE_MANAGERS.contains(viewer.getUserId());
  }

  public Space getSpaceByUrl(String spaceUrl) {
    throw new UnsupportedOperationException();
  }

  public ListAccess<Space> getAllSpacesWithListAccess() {
    throw new UnsupportedOperationException();
  }

  public ListAccess<Space> getAllSpacesByFilter(SpaceFilter spaceFilter) {
    throw new UnsupportedOperationException();
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

  public ListAccess<Space> getMemberSpacesByFilter(String userId, SpaceFilter spaceFilter) {
    throw new UnsupportedOperationException();
  }

  public ListAccess<Space> getAccessibleSpacesWithListAccess(String userId) {
    throw new UnsupportedOperationException();
  }

  public ListAccess<Space> getAccessibleSpacesByFilter(String userId, SpaceFilter spaceFilter) {
    throw new UnsupportedOperationException();
  }

  public ListAccess<Space> getSettingableSpaces(String userId) {
    throw new UnsupportedOperationException();
  }

  public ListAccess<Space> getSettingabledSpacesByFilter(String userId, SpaceFilter spaceFilter) {
    throw new UnsupportedOperationException();
  }

  public ListAccess<Space> getInvitedSpacesWithListAccess(String userId) {
    throw new UnsupportedOperationException();
  }

  public ListAccess<Space> getInvitedSpacesByFilter(String userId, SpaceFilter spaceFilter) {
    throw new UnsupportedOperationException();
  }

  public ListAccess<Space> getPublicSpacesWithListAccess(String userId) {
    throw new UnsupportedOperationException();
  }

  public ListAccess<Space> getPublicSpacesByFilter(String userId, SpaceFilter spaceFilter) {
    throw new UnsupportedOperationException();
  }

  public ListAccess<Space> getPendingSpacesWithListAccess(String userId) {
    throw new UnsupportedOperationException();
  }

  public ListAccess<Space> getPendingSpacesByFilter(String userId, SpaceFilter spaceFilter) {
    throw new UnsupportedOperationException();
  }

  public Space createSpace(Space space, String creatorUserId) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Space createSpace(Space space, String creatorUserId, List<Identity> identitiesToInvite) throws SpaceException {
    throw new UnsupportedOperationException();
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

  public Space updateSpaceAvatar(Space existingSpace) {
    throw new UnsupportedOperationException();
  }

  public Space updateSpaceBanner(Space existingSpace) {
    throw new UnsupportedOperationException();
  }

  public void deleteSpace(Space space) {
    throw new UnsupportedOperationException();
  }

  public void addPendingUser(Space space, String userId) {
    throw new UnsupportedOperationException();
  }

  public void removePendingUser(Space space, String userId) {
    throw new UnsupportedOperationException();
  }

  public boolean isPendingUser(Space space, String userId) {
    throw new UnsupportedOperationException();
  }

  public void addInvitedUser(Space space, String userId) {
    throw new UnsupportedOperationException();
  }

  public void removeInvitedUser(Space space, String userId) {
    throw new UnsupportedOperationException();
  }

  public boolean isInvitedUser(Space space, String userId) {
    throw new UnsupportedOperationException();

  }

  public void addMember(Space space, String userId) {
    throw new UnsupportedOperationException();

  }

  public void removeMember(Space space, String userId) {
    throw new UnsupportedOperationException();

  }

  public boolean isMember(Space space, String userId) {
    return SPACE_MEMBERS.contains(userId);
  }

  public void setManager(Space space, String userId, boolean isManager) {
    throw new UnsupportedOperationException();

  }

  public boolean isManager(Space space, String userId) {
    return SPACE_MANAGERS.contains(userId);
  }

  public boolean isOnlyManager(Space space, String userId) {
    throw new UnsupportedOperationException();

  }

  public boolean hasAccessPermission(Space space, String userId) {
    throw new UnsupportedOperationException();

  }

  public boolean hasSettingPermission(Space space, String userId) {
    throw new UnsupportedOperationException();

  }

  public void registerSpaceListenerPlugin(SpaceListenerPlugin spaceListenerPlugin) {
    throw new UnsupportedOperationException();

  }

  public void unregisterSpaceListenerPlugin(SpaceListenerPlugin spaceListenerPlugin) {
    throw new UnsupportedOperationException();

  }

  public void setSpaceApplicationConfigPlugin(SpaceApplicationConfigPlugin spaceApplicationConfigPlugin) {
    throw new UnsupportedOperationException();

  }

  public SpaceApplicationConfigPlugin getSpaceApplicationConfigPlugin() {
    throw new UnsupportedOperationException();

  }

  public List<Space> getAllSpaces() throws SpaceException {
    throw new UnsupportedOperationException();

  }

  public Space getSpaceByName(String spaceName) throws SpaceException {
    throw new UnsupportedOperationException();

  }

  public List<Space> getSpacesByFirstCharacterOfName(String firstCharacterOfName) throws SpaceException {
    throw new UnsupportedOperationException();

  }

  public List<Space> getSpacesBySearchCondition(String condition) throws Exception {
    throw new UnsupportedOperationException();

  }

  public List<Space> getSpaces(String userId) throws SpaceException {
    throw new UnsupportedOperationException();

  }

  public List<Space> getAccessibleSpaces(String userId) throws SpaceException {
    throw new UnsupportedOperationException();

  }

  public List<Space> getVisibleSpaces(String userId, SpaceFilter spaceFilter) throws SpaceException {
    throw new UnsupportedOperationException();

  }

  public SpaceListAccess getVisibleSpacesWithListAccess(String userId, SpaceFilter spaceFilter) {
    throw new UnsupportedOperationException();

  }

  public SpaceListAccess getUnifiedSearchSpacesWithListAccess(String userId, SpaceFilter spaceFilter) {
    throw new UnsupportedOperationException();

  }

  public List<Space> getEditableSpaces(String userId) throws SpaceException {
    throw new UnsupportedOperationException();

  }

  public List<Space> getInvitedSpaces(String userId) throws SpaceException {
    throw new UnsupportedOperationException();

  }

  public List<Space> getPublicSpaces(String userId) throws SpaceException {
    throw new UnsupportedOperationException();

  }

  public List<Space> getPendingSpaces(String userId) throws SpaceException {
    throw new UnsupportedOperationException();

  }

  public Space createSpace(Space space, String creator, String invitedGroupId) throws SpaceException {
    throw new UnsupportedOperationException();

  }

  public void saveSpace(Space space, boolean isNew) throws SpaceException {
    throw new UnsupportedOperationException();

  }

  public void renameSpace(Space space, String newDisplayName) throws SpaceException {
    throw new UnsupportedOperationException();

  }

  public void renameSpace(String remoteId, Space space, String newDisplayName) throws SpaceException {
    throw new UnsupportedOperationException();

  }

  public void deleteSpace(String spaceId) throws SpaceException {
    throw new UnsupportedOperationException();

  }

  public void initApp(Space space) throws SpaceException {
    throw new UnsupportedOperationException();

  }

  public void initApps(Space space) throws SpaceException {
    throw new UnsupportedOperationException();

  }

  public void deInitApps(Space space) throws SpaceException {
    throw new UnsupportedOperationException();

  }

  public void addMember(String spaceId, String userId) throws SpaceException {
    throw new UnsupportedOperationException();

  }

  public void removeMember(String spaceId, String userId) throws SpaceException {
    throw new UnsupportedOperationException();

  }

  public List<String> getMembers(Space space) throws SpaceException {
    throw new UnsupportedOperationException();

  }

  public List<String> getMembers(String spaceId) throws SpaceException {
    throw new UnsupportedOperationException();

  }

  public void setLeader(Space space, String userId, boolean isLeader) throws SpaceException {
    throw new UnsupportedOperationException();

  }

  public void setLeader(String spaceId, String userId, boolean isLeader) throws SpaceException {
    throw new UnsupportedOperationException();

  }

  public boolean isLeader(Space space, String userId) throws SpaceException {
    throw new UnsupportedOperationException();

  }

  public boolean isLeader(String spaceId, String userId) throws SpaceException {
    throw new UnsupportedOperationException();

  }

  public boolean isOnlyLeader(Space space, String userId) throws SpaceException {
    throw new UnsupportedOperationException();

  }

  public boolean isOnlyLeader(String spaceId, String userId) throws SpaceException {
    throw new UnsupportedOperationException();
  }

  public boolean isMember(String spaceId, String userId) throws SpaceException {
    return userId.equals("root1") ? true : false;
  }

  public boolean hasAccessPermission(String spaceId, String userId) throws SpaceException {
    throw new UnsupportedOperationException();
  }

  public boolean hasEditPermission(Space space, String userId) throws SpaceException {
    throw new UnsupportedOperationException();
  }

  public boolean hasEditPermission(String spaceId, String userId) throws SpaceException {
    throw new UnsupportedOperationException();
  }

  public boolean isInvited(Space space, String userId) throws SpaceException {
    throw new UnsupportedOperationException();
  }

  public boolean isInvited(String spaceId, String userId) throws SpaceException {
    throw new UnsupportedOperationException();
  }

  public boolean isPending(Space space, String userId) throws SpaceException {
    throw new UnsupportedOperationException();
  }

  public boolean isPending(String spaceId, String userId) throws SpaceException {
    throw new UnsupportedOperationException();

  }

  public void installApplication(String spaceId, String appId) throws SpaceException {
    throw new UnsupportedOperationException();

  }

  public void installApplication(Space space, String appId) throws SpaceException {
    throw new UnsupportedOperationException();

  }

  public void activateApplication(Space space, String appId) throws SpaceException {
    throw new UnsupportedOperationException();

  }

  public void activateApplication(String spaceId, String appId) throws SpaceException {
    throw new UnsupportedOperationException();

  }

  public void deactivateApplication(Space space, String appId) throws SpaceException {
    throw new UnsupportedOperationException();

  }

  public void deactivateApplication(String spaceId, String appId) throws SpaceException {
    throw new UnsupportedOperationException();

  }

  public void removeApplication(Space space, String appId, String appName) throws SpaceException {
    throw new UnsupportedOperationException();

  }

  public void removeApplication(String spaceId, String appId, String appName) throws SpaceException {
    throw new UnsupportedOperationException();

  }

  public void updateSpaceAccessed(String remoteId, Space space) throws SpaceException {
    throw new UnsupportedOperationException();

  }

  public List<Space> getLastAccessedSpace(String remoteId, String appId, int offset, int limit) throws SpaceException {
    throw new UnsupportedOperationException();
  }

  public List<Space> getLastSpaces(int limit) {
    throw new UnsupportedOperationException();
  }

  public ListAccess<Space> getLastAccessedSpace(String remoteId, String appId) {
    throw new UnsupportedOperationException();
  }

  public void requestJoin(Space space, String userId) throws SpaceException {
    throw new UnsupportedOperationException();

  }

  public void requestJoin(String spaceId, String userId) throws SpaceException {
    throw new UnsupportedOperationException();

  }

  public void revokeRequestJoin(Space space, String userId) throws SpaceException {
    throw new UnsupportedOperationException();

  }

  public void revokeRequestJoin(String spaceId, String userId) throws SpaceException {
    throw new UnsupportedOperationException();

  }

  public void inviteMember(Space space, String userId) throws SpaceException {
    throw new UnsupportedOperationException();

  }

  public void inviteMember(String spaceId, String userId) throws SpaceException {
    throw new UnsupportedOperationException();

  }

  public void revokeInvitation(Space space, String userId) throws SpaceException {
    throw new UnsupportedOperationException();

  }

  public void revokeInvitation(String spaceId, String userId) throws SpaceException {
    throw new UnsupportedOperationException();

  }

  public void acceptInvitation(Space space, String userId) throws SpaceException {
    throw new UnsupportedOperationException();

  }

  public void acceptInvitation(String spaceId, String userId) throws SpaceException {
    throw new UnsupportedOperationException();

  }

  public void denyInvitation(Space space, String userId) throws SpaceException {
    throw new UnsupportedOperationException();

  }

  public void denyInvitation(String spaceId, String userId) throws SpaceException {
    throw new UnsupportedOperationException();

  }

  public void validateRequest(Space space, String userId) throws SpaceException {
    throw new UnsupportedOperationException();

  }

  public void validateRequest(String spaceId, String userId) throws SpaceException {
    throw new UnsupportedOperationException();

  }

  public void declineRequest(Space space, String userId) throws SpaceException {
    throw new UnsupportedOperationException();

  }

  public void declineRequest(String spaceId, String userId) throws SpaceException {
    throw new UnsupportedOperationException();

  }

  public void registerSpaceLifeCycleListener(SpaceLifeCycleListener listener) {
    throw new UnsupportedOperationException();

  }

  public void unregisterSpaceLifeCycleListener(SpaceLifeCycleListener listener) {
    throw new UnsupportedOperationException();

  }

  public void setPortletsPrefsRequired(PortletPreferenceRequiredPlugin portletPrefsRequiredPlugin) {
    // Nothing to do
  }

  public String[] getPortletsPrefsRequired() {
    return new String[0];
  }

  public ListAccess<Space> getVisitedSpaces(String remoteId, String appId) {
    throw new UnsupportedOperationException();
  }

  public boolean isSuperManager(String userId) {
    if ("root".equals(userId)) {
      return true;
    } else {
      return false;
    }
  }

  public List<MembershipEntry> getSuperManagersMemberships() {
    throw new UnsupportedOperationException();
  }

  public void addSuperManagersMembership(String permissionExpression) {
    throw new UnsupportedOperationException();
  }

  public void removeSuperManagersMembership(String permissionExpression) {
    throw new UnsupportedOperationException();
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

/**
 * This file is part of the Meeds project (https://meeds.io/).
 *
 * Copyright (C) 2020 - 2023 Meeds Association contact@meeds.io
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package io.meeds.gamification.mock;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.codec.binary.StringUtils;

import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.identity.model.IdentityWithRelationship;
import org.exoplatform.social.core.identity.model.Profile;
import org.exoplatform.social.core.identity.model.Profile.AttachedActivityType;
import org.exoplatform.social.core.identity.provider.OrganizationIdentityProvider;
import org.exoplatform.social.core.identity.provider.SpaceIdentityProvider;
import org.exoplatform.social.core.profile.ProfileFilter;
import org.exoplatform.social.core.relationship.model.Relationship.Type;
import org.exoplatform.social.core.space.model.Space;
import org.exoplatform.social.core.storage.IdentityStorageException;
import org.exoplatform.social.core.storage.api.IdentityStorage;

public class IdentityStorageMock implements IdentityStorage {

  protected static List<Identity> identities = new ArrayList<>();

  public IdentityStorageMock() {
    for (int i = 0; i < 100; i++) {
      Identity identity = new Identity(String.valueOf(i));
      identity.setDeleted(false);
      identity.setEnable(true);
      identity.setProviderId(OrganizationIdentityProvider.NAME);
      identity.setRemoteId("root" + i);
      Profile profile = new Profile(identity);
      identity.setProfile(profile);
      identities.add(identity);
    }

    for (int i = 100; i < 200; i++) {
      Identity identity = new Identity(String.valueOf(i));
      identity.setDeleted(false);
      identity.setEnable(true);
      identity.setProviderId(SpaceIdentityProvider.NAME);
      identity.setRemoteId("space" + i);
      Profile profile = new Profile(identity);
      identity.setProfile(profile);
      identities.add(identity);
    }
  }

  public void saveIdentity(Identity identity) throws IdentityStorageException {
    // Nothing to change
  }

  public Identity updateIdentity(Identity identity) throws IdentityStorageException {
    return identity;
  }

  public void updateIdentityMembership(String remoteId) throws IdentityStorageException {
    // No change
  }

  public Identity findIdentityById(String identityId) throws IdentityStorageException {
    return identities.stream()
                     .filter(identity -> StringUtils.equals(identity.getId(), identityId))
                     .findFirst()
                     .orElse(null);
  }

  public void deleteIdentity(Identity identity) throws IdentityStorageException {
    // Nothing to change
  }

  public void hardDeleteIdentity(Identity identity) throws IdentityStorageException {
    // Nothing to change
  }

  public Profile loadProfile(Profile profile) throws IdentityStorageException {
    return profile;
  }

  public Identity findIdentity(String providerId, String remoteId) throws IdentityStorageException {
    return identities.stream()
                     .filter(identity -> StringUtils.equals(identity.getProviderId(), providerId)
                         && StringUtils.equals(identity.getRemoteId(), remoteId))
                     .findFirst()
                     .orElse(null);
  }

  public void saveProfile(Profile profile) throws IdentityStorageException {
    // Nothing to change
  }

  public void updateProfile(Profile profile) throws IdentityStorageException {
    // Nothing to change
  }

  public int getIdentitiesCount(String providerId) throws IdentityStorageException {
    // Nothing to change
    return identities.size();
  }

  public List<Identity> getIdentitiesByProfileFilter(String providerId, ProfileFilter profileFilter, long offset, long limit,
                                                     boolean forceLoadOrReloadProfile) throws IdentityStorageException {
    return Collections.emptyList();
  }

  public List<Identity> getIdentitiesForMentions(String providerId, ProfileFilter profileFilter, Type type, long offset,
                                                 long limit, boolean forceLoadOrReloadProfile) throws IdentityStorageException {
    return Collections.emptyList();
  }

  public int getIdentitiesForMentionsCount(String providerId, ProfileFilter profileFilter,
                                           Type type) throws IdentityStorageException {
    return 0;
  }

  public List<Identity> getIdentitiesForUnifiedSearch(String providerId, ProfileFilter profileFilter, long offset,
                                                      long limit) throws IdentityStorageException {
    return Collections.emptyList();
  }

  public int getIdentitiesByProfileFilterCount(String providerId, ProfileFilter profileFilter) throws IdentityStorageException {
    return 0;
  }

  public List<Identity> getSpaceMemberIdentitiesByProfileFilter(Space space, ProfileFilter profileFilter,
                                                                org.exoplatform.social.core.identity.SpaceMemberFilterListAccess.Type type,
                                                                long offset, long limit) throws IdentityStorageException {
    return Collections.emptyList();
  }

  public void updateProfileActivityId(Identity identity, String activityId, AttachedActivityType type) {
    // Nothing to change
  }

  public String getProfileActivityId(Profile profile, AttachedActivityType type) {
    return null;
  }

  public void processEnabledIdentity(Identity identity, boolean isEnable) {
    // Nothing to change
  }

  public List<IdentityWithRelationship> getIdentitiesWithRelationships(String identityId, int offset, int limit) {
    return Collections.emptyList();
  }

  public int countIdentitiesWithRelationships(String identityId) throws Exception {
    return 0;
  }

  public InputStream getAvatarInputStreamById(Identity identity) throws IOException {
    return null;
  }

  public InputStream getBannerInputStreamById(Identity identity) throws IOException {
    return null;
  }

  public int countSpaceMemberIdentitiesByProfileFilter(Space space, ProfileFilter profileFilter,
                                                       org.exoplatform.social.core.identity.SpaceMemberFilterListAccess.Type type) {
    return 0;
  }

  public List<Identity> getIdentities(String providerId, String sortField,
                                      String sortDirection, boolean isEnabled, String userType, Boolean isConnected,
                                      String enrollmentStatus, long offset, long limit) {
    return Collections.emptyList();
  }

}

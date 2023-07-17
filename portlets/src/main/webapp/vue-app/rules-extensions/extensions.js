/**
 *
 * This file is part of the Meeds project (https://meeds.io/).
 *
 * Copyright (C) 2023 Meeds Association contact@meeds.io
 *
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
 *
 */

extensionRegistry.registerExtension('engagementCenterActions', 'user-actions', {
  type: 'Stream',
  options: {
    rank: 10,
    icon: 'fas fa-stream',
    match: (actionLabel) => [
      'postActivity',
      'postActivityComment',
      'likeActivity',
      'likeActivityComment',
      'receiveActivity',
      'receiveCommentOnActivity',
      'receiveLikeOnActivity',
      'receiveLikeOnActivityComment',
    ].includes(actionLabel),
    getLink: (realization) => {
      Vue.prototype.$set(realization, 'link', `${eXo.env.portal.context}/${eXo.env.portal.portalName}/activity?id=${realization?.objectId}`);
    }
  },
});

extensionRegistry.registerExtension('engagementCenterActions', 'user-actions', {
  type: 'Profile',
  options: {
    rank: 20,
    icon: 'fas fa-user',
    match: (actionLabel) => [
      'addUserProfileContactInformation',
      'addUserProfileWorkExperience',
      'addUserProfileAboutMe',
      'addUserProfileAvatar',
      'addUserProfileBanner',
      'addUserProfileNotificationSetting',
      'userLogin',
      'receiveRelationshipRequest',
      'sendRelationshipRequest'
    ].includes(actionLabel),
    getLink: (realization) => {
      if (realization?.objectId && !realization.link) {
        Vue.prototype.$identityService.getIdentityById(realization?.objectId)
          .then(identity => Vue.prototype.$set(realization, 'link', `${eXo.env.portal.context}/${eXo.env.portal.portalName}/profile/${identity.remoteId}`));
      }
    },
  },
});

extensionRegistry.registerExtension('engagementCenterActions', 'user-actions', {
  type: 'Space',
  options: {
    rank: 30,
    icon: 'fas fa-layer-group',
    match: (actionLabel) => [
      'addSpace',
      'updateSpaceAvatar',
      'updateSpaceBanner',
      'updateSpaceDescription',
      'updateSpaceApplications',
      'becomeSpaceManager',
      'inviteUserToSpace',
      'pinActivityOnSpace',
      'joinSpace'
    ].includes(actionLabel),
  },
});

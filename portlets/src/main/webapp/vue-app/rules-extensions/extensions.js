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
      'pinActivityOnSpace',
      'likeActivity',
      'likeActivityComment',
      'receiveActivity',
      'receiveCommentOnActivity',
      'receiveLikeOnActivity',
      'receiveLikeOnActivityComment',
    ].includes(actionLabel),
    getLink: (realization) => {
      Vue.prototype.$set(realization, 'link', `${eXo.env.portal.context}/${eXo.env.portal.defaultPortal}/activity?id=${realization?.objectId}`);
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
    getLink: realization => {
      if (realization?.objectType === 'identity'
          && realization?.action?.event !== 'addUserProfileNotificationSetting'
          && realization?.action?.event !== 'userLogin') {
        if (realization?.objectId === eXo.env.portal.profileOwnerIdentityId) {
          realization.link = `${eXo.env.portal.context}/${eXo.env.portal.defaultPortal}/profile/${eXo.env.portal.profileOwner}`;
          return realization.link;
        } else {
          return window?.eXo?.env?.portal?.userName?.length && Vue.prototype.$identityService.getIdentityById(realization?.objectId)
            .then(identity => {
              realization.link = `${eXo.env.portal.context}/${eXo.env.portal.defaultPortal}/profile/${identity.remoteId}`;
              return realization.link;
            }) || null;
        }
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
      'joinSpace'
    ].includes(actionLabel),
    getLink: realization => {
      if (realization.objectType === 'identity') {
        return window?.eXo?.env?.portal?.userName?.length && Vue.prototype.$identityService.getIdentityById(realization.objectId)
          .then(identity => identity?.remoteId && identity?.providerId === 'space' && Vue.prototype.$spaceService.getSpaceByPrettyName(identity.remoteId))
          .then(space => {
            if (space.groupId) {
              const uri = space.groupId.replace(/\//g, ':');
              realization.link = `${eXo.env.portal.context}/g/${uri}/`;
              return realization.link;
            }
          }) || null;
      } else if (realization.objectType === 'spaceMembership') {
        return window?.eXo?.env?.portal?.userName?.length && realization.objectId.includes('-') && Vue.prototype.$spaceService.getSpaceById(realization.objectId.split('-')[0])
          .then(space => {
            if (space.groupId) {
              const uri = space.groupId.replace(/\//g, ':');
              realization.link = `${eXo.env.portal.context}/g/${uri}/`;
              return realization.link;
            }
          }) || null;
      } else if (realization.space) {
        return window?.eXo?.env?.portal?.userName?.length && Vue.prototype.$spaceService.getSpaceByDisplayName(realization.space)
          .then(space => {
            if (space.groupId) {
              const uri = space.groupId.replace(/\//g, ':');
              realization.link = `${eXo.env.portal.context}/g/${uri}/`;
              return realization.link;
            }
          }) || null;
      } else {
        return null;
      }
    }
  },
});

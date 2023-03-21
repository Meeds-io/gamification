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

const streamUserActions = ['addActivityOnMyStream', 'addActivityOnNetworkStream', 'addActivityOnSpaceStream', 'addActivityOnSpaceStreamTarget',
  'addActivityTargetUserStream', 'addCommentOnNetworkStream', 'addCommentOnSpaceStream', 'receiveCommentOnNetworkStream', 'receiveCommentOnSpaceStream',
  'likeActivityOnNetworkStream', 'likeActivityOnSpaceStream', 'likeActivityOnSpaceStreamTarget', 'likeActivityTargetUserStream', 'likeComment',
  'likeCommentOnNetworkStream', 'likeCommentOnNetworkStreamTarget', 'likeCommentOnSpaceStream', 'likeCommentOnSpaceStreamTarget', 'uploaddocumentOnNetworkStream'];
const profileUserActions = ['addUserProfileAboutMe', 'addUserProfileAvatar', 'addUserProfileBanner', 'userLogin', 'receiveRelationshipRequest', 'sendRelationshipRequest'];

const spaceUserActions = ['addSpace', 'becomeSpaceManager', 'joinSpace' , ];

extensionRegistry.registerExtension('engagementCenterActions', 'user-actions', {
  type: 'Stream',
  options: {
    rank: 10,
    icon: 'fas fa-stream',
    match: (actionLabel) => streamUserActions.includes(actionLabel),
    getObjectURL: (objectId) => {
      return `${eXo.env.portal.context}/${eXo.env.portal.portalName}/activity?id=${objectId}`;
    }
  },
});

extensionRegistry.registerExtension('engagementCenterActions', 'user-actions', {
  type: 'Profile',
  options: {
    rank: 20,
    icon: 'fas fa-user',
    match: (actionLabel) => profileUserActions.includes(actionLabel),
    getObjectURL: (objectId) => {
      return `${eXo.env.portal.context}/${eXo.env.portal.portalName}/profile/${objectId}`;
    }
  },
});

extensionRegistry.registerExtension('engagementCenterActions', 'user-actions', {
  type: 'Space',
  options: {
    rank: 30,
    icon: 'fas fa-layer-group',
    match: (actionLabel) => spaceUserActions.includes(actionLabel),
    getObjectURL: () => null
  },
});

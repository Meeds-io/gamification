/*
 * This file is part of the Meeds project (https://meeds.io/).
 *
 * Copyright (C) 2020 - 2024 Meeds Association contact@meeds.io
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
import '../connector-events-extensions/initComponents';
extensionRegistry.registerComponent('engagementCenterEvent', 'connector-event-extensions', {
  id: 'stream-event',
  name: 'stream',
  vueComponent: Vue.options.components['meeds-stream-event'],
  isEnabled: (params) => [
    'postActivity',
    'postActivityComment',
    'likeActivity',
    'likeActivityComment',
    'receiveCommentOnActivity',
    'receiveLikeOnActivity',
    'receiveLikeOnActivityComment',
  ].includes(params?.trigger),
});

extensionRegistry.registerComponent('engagementCenterEvent', 'connector-event-extensions', {
  id: 'profile-event',
  name: 'profile',
  vueComponent: Vue.options.components['meeds-profile-event'],
  isEnabled: (params) => [
    'addUserProfileContactInformation',
    'addUserProfileWorkExperience',
    'addUserProfileAboutMe',
    'addUserProfileAvatar',
    'addUserProfileBanner',
    'addUserProfileNotificationSetting',
    'receiveRelationshipRequest',
    'sendRelationshipRequest'
  ].includes(params?.trigger),
});

extensionRegistry.registerComponent('engagementCenterEvent', 'connector-event-extensions', {
  id: 'space-event',
  name: 'space',
  vueComponent: Vue.options.components['meeds-space-event'],
  isEnabled: (params) => [
    'joinSpace'
  ].includes(params?.trigger),
});

extensionRegistry.registerComponent('engagementCenterEvent', 'connector-event-extensions', {
  id: 'contribution-event',
  name: 'contribution',
  vueComponent: Vue.options.components['meeds-contribution-event'],
  isEnabled: (params) => [
    'reviewContribution'
  ].includes(params?.trigger),
});

extensionRegistry.registerComponent('engagementCenterEvent', 'connector-event-extensions', {
  id: 'Connector-connect',
  name: 'ConnectorConnect',
  vueComponent: Vue.options.components['meeds-connector-connect-event'],
  isEnabled: (params) => params?.trigger?.startsWith('connectorConnect'),
});

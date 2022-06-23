/**
 * This file is part of the Meeds project (https://meeds.io/).
 * Copyright (C) 2022 Meeds Association
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

extensionRegistry.registerComponent('ActivityContent', 'activity-content-extensions', {
  id: 'announcement',
  isEnabled: (params) => {
    const activity = params && params.activity;
    return activity.type === 'challenges-announcement';
  },
  vueComponent: Vue.options.components['activity-announcement'],
  rank: 5,
});

extensionRegistry.registerExtension('ActivityFavoriteIcon', 'activity-favorite-icon-extensions', {
  id: 'favorite-challenge',
  type: 'challenges-announcement',
  img: '/challenges/images/challengesAppIcon.png',
});


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
import ProfileStats from './components/ProfileStats.vue';
import ConnectionsRequests from './components/ConnectionsRequests.vue';
import GamificationRank from './components/GamificationRank.vue';
import SpacesRequests from './components/SpacesRequests.vue';
import UserPointsWidget from './components/UserPointsWidget.vue';
import UserDashbord from './components/UserDashbord.vue';
import AchievementItem from './components/AchievementItem.vue';
import AchievementsDrawer from './components/AchievementsDrawer.vue';
import ConnectionsDrawer from './components/ConnectionsDrawer.vue';
import ExoSuggestionsPeopleListItem from './components/ExoSuggestionsPeopleListItem.vue';
import PeopleListItem from './components/PeopleListItem.vue';
import SpaceDrawer from './components/SpaceDrawer.vue';
import SpaceDrawerItems from './components/SpaceDrawerItems.vue';
import SuggestionsSpaceListItem from './components/SuggestionsSpaceListItem.vue';

const components = {
  'profile-stats': ProfileStats,
  'connections-requests': ConnectionsRequests,
  'gamification-rank': GamificationRank,
  'spaces-requests': SpacesRequests,
  'user-points-widget': UserPointsWidget,
  'user-dashbord': UserDashbord,
  'achievements-drawer': AchievementsDrawer,
  'achievement-item': AchievementItem,
  'connections-drawer': ConnectionsDrawer,
  'space-drawer': SpaceDrawer,
  'space-drawer-items': SpaceDrawerItems,
  'suggestions-space-list-item': SuggestionsSpaceListItem,
  'exo-suggestions-people-list-item': ExoSuggestionsPeopleListItem,
  'people-list-item': PeopleListItem,

};

for (const key in components) {
  Vue.component(key, components[key]);
}

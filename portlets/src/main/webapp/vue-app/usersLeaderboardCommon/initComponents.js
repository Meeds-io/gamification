/*
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 */
import Tabs from './components/Tabs.vue';
import Profile from './components/Profile.vue';
import Profiles from './components/Profiles.vue';
import ProfileChart from './components/ProfileChart.vue';
import ProfileStats from './components/ProfileStats.vue';
import ProfileRealizations from './components/ProfileRealizations.vue';
import ProfileRealizationItem from './components/ProfileRealizationItem.vue';
import ProfileAchievementsDrawer from './components/ProfileAchievementsDrawer.vue';
import ProgramSelectorDrawer from './components/ProgramSelectorDrawer.vue';
import RulesOverviewListDrawer from './components/RulesOverviewListDrawer.vue';
import RulesOverviewReducedList from './components/RulesOverviewReducedList.vue';
import RulesOverviewFullList from './components/RulesOverviewFullList.vue';
import RulesOverviewSpaceList from './components/RulesOverviewSpaceList.vue';
import RulesOverviewItem from './components/RulesOverviewItem.vue';

const components = {
  'users-leaderboard-tabs': Tabs,
  'users-leaderboard-profiles': Profiles,
  'users-leaderboard-profile': Profile,
  'users-leaderboard-profile-chart': ProfileChart,
  'users-leaderboard-profile-stats': ProfileStats,
  'users-leaderboard-profile-realizations': ProfileRealizations,
  'users-leaderboard-profile-realization-item': ProfileRealizationItem,
  'users-leaderboard-profile-achievements-drawer': ProfileAchievementsDrawer,
  'users-leaderboard-program-selector-drawer': ProgramSelectorDrawer,
  'gamification-rules-overview-list-drawer': RulesOverviewListDrawer,
  'gamification-rules-overview-reduced-list': RulesOverviewReducedList,
  'gamification-rules-overview-full-list': RulesOverviewFullList,
  'gamification-rules-overview-space-list': RulesOverviewSpaceList,
  'gamification-rules-overview-item': RulesOverviewItem,
};

for (const key in components) {
  Vue.component(key, components[key]);
}

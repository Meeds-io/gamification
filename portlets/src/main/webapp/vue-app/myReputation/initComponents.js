/*
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
import MyReputation from './components/MyReputation.vue';
import BadgesOverview from '../badgesOverview/components/BadgesOverview.vue';
import BadgesOverviewItem from '../badgesOverview/components/BadgesOverviewItem.vue';
import BadgesOverviewDrawer from '../badgesOverview/components/BadgesOverviewDrawer.vue';
import BadgesOverviewDrawerItem from '../badgesOverview/components/BadgesOverviewDrawerItem.vue';


const components = {
  'my-reputation': MyReputation,
  'badges-overview': BadgesOverview,
  'badges-overview-item': BadgesOverviewItem,
  'badges-overview-drawer': BadgesOverviewDrawer,
  'badges-overview-drawer-item': BadgesOverviewDrawerItem,
};

for (const key in components) {
  Vue.component(key, components[key]);
}


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
import BadgesOverview from './components/BadgesOverview.vue';
import BadgesOverviewItem from './components/BadgesOverviewItem.vue';
import BadgesOverviewDrawer from './components/BadgesOverviewDrawer.vue';
import BadgesOverviewDrawerItem from './components/BadgesOverviewDrawerItem.vue';
import BadgesOverviewSettingsDrawer from './components/BadgesOverviewSettingsDrawer.vue';

const components = {
  'badges-overview': BadgesOverview,
  'badges-overview-item': BadgesOverviewItem,
  'badges-overview-drawer': BadgesOverviewDrawer,
  'badges-overview-drawer-item': BadgesOverviewDrawerItem,
  'badges-overview-settings-drawer': BadgesOverviewSettingsDrawer,
};

for (const key in components) {
  Vue.component(key, components[key]);
}

/**
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
 */

import RulesOverview from './components/RulesOverview.vue';
import RulesOverviewReducedList from './components/RulesOverviewReducedList.vue';
import RulesOverviewFullList from './components/RulesOverviewFullList.vue';
import RulesOverviewSpaceList from './components/RulesOverviewSpaceList.vue';

const components = {
  'gamification-rules-overview': RulesOverview,
  'gamification-rules-overview-reduced-list': RulesOverviewReducedList,
  'gamification-rules-overview-full-list': RulesOverviewFullList,
  'gamification-rules-overview-space-list': RulesOverviewSpaceList,
};

for (const key in components) {
  Vue.component(key, components[key]);
}

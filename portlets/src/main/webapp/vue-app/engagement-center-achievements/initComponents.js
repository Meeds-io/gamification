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
import Realizations from './components/Realizations.vue';

import RealizationItem from './components/row/RealizationItem.vue';
import RealizationItemMobile from './components/row/RealizationItemMobile.vue';
import ManualActionValue from './components/row/ManualActionValue.vue';
import AutomaticActionValue from './components/row/AutomaticActionValue.vue';

import RealizationExportButton from './components/action/RealizationExportButton.vue';

import RealizationFilterDrawer from './components/filter/RealizationFilterDrawer.vue';
import GranteeAttendeeItem from './components/filter/GranteeAttendeeItem.vue';
import ProgramAttendeeItem from './components/filter/ProgramAttendeeItem.vue';
import ProgramSuggester from './components/filter/ProgramSuggester.vue';
import SelectPeriod from './components/filter/SelectPeriod.vue';

const components = {
  'engagement-center-realizations': Realizations,
  'engagement-center-realization-item': RealizationItem,
  'engagement-center-realization-item-mobile': RealizationItemMobile,
  'engagement-center-realizations-select-period': SelectPeriod,
  'engagement-center-realizations-filter-drawer': RealizationFilterDrawer,
  'engagement-center-realizations-grantee-attendee-item': GranteeAttendeeItem,
  'engagement-center-realizations-program-attendee-item': ProgramAttendeeItem,
  'engagement-center-realizations-program-suggester': ProgramSuggester,
  'engagement-center-realizations-export-button': RealizationExportButton,
  'engagement-center-realizations-manual-action-value': ManualActionValue,
  'engagement-center-realizations-automatic-action-value': AutomaticActionValue,
};

for (const key in components) {
  Vue.component(key, components[key]);
}
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
 
import Programs from './components/Programs.vue';

import ProgramsList from './components/ProgramsList.vue';

import ProgramCard from './components/card/ProgramCard.vue';
import ProgramMenu from './components/card/ProgramMenu.vue';
import ProgramDisabledMaskContent from './components/card/ProgramDisabledMaskContent.vue';

import ProgramDetail from './components/detail/ProgramDetail.vue';
import ProgramCreatedPlaceholder from './components/detail/ProgramCreatedPlaceholder.vue';

import ProgramDrawer from './components/form/ProgramDrawer.vue';
import ProgramOwnerAssignment from './components/form/ProgramOwnerAssignment.vue';
import ProgramImageSelector from './components/form/ProgramImageSelector.vue';
import ProgramImageSelectorButtons from './components/form/ProgramImageSelectorButtons.vue';
import ProgramColorPicker from './components/form/ProgramColorPicker.vue';
import ProgramColorPickerDrawer from './components/form/ProgramColorPickerDrawer.vue';

import RulesToolbar from './components/rules/RulesToolbar.vue';
import NoRuleFound from './components/rules/NoRuleFound.vue';
import RuleTitle from './components/rules/RuleTitle.vue';
import RuleItem from './components/rules/RuleItem.vue';

const components = {
  'engagement-center-programs': Programs,
  'engagement-center-programs-list': ProgramsList,
  'engagement-center-program-card': ProgramCard,
  'engagement-center-program-menu': ProgramMenu,
  'engagement-center-program-drawer': ProgramDrawer,
  'engagement-center-program-detail': ProgramDetail,
  'engagement-center-program-created-placeholder': ProgramCreatedPlaceholder,
  'engagement-center-program-owner-assignment': ProgramOwnerAssignment,
  'engagement-center-program-image-selector': ProgramImageSelector,
  'engagement-center-program-image-selector-buttons': ProgramImageSelectorButtons,
  'engagement-center-program-disabled-mask-content': ProgramDisabledMaskContent,
  'engagement-center-program-color-picker': ProgramColorPicker,
  'engagement-center-program-color-picker-drawer': ProgramColorPickerDrawer,
  'engagement-center-program-rules-toolbar': RulesToolbar,
  'engagement-center-program-rule-item': RuleItem,
  'engagement-center-program-rule-title': RuleTitle,
  'engagement-center-program-no-rule-found': NoRuleFound,
};

for (const key in components) {
  Vue.component(key, components[key]);
}

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
import Widget from './components/overview/Widget.vue';
import WidgetRow from './components/overview/WidgetRow.vue';
import WidgetEmptyRow from './components/overview/WidgetEmptyRow.vue';
import ProgramSuggester from './components/program/ProgramSuggester.vue';
import ProgramItem from './components/program/ProgramItem.vue';
import RuleChip from './components/program/RuleChip.vue';
import ProgramColorPicker from './components/program/ProgramColorPicker.vue';
import ProgramColorPickerDrawer from './components/program/ProgramColorPickerDrawer.vue';
import ProgramDrawer from '../rules/components/drawers/ProgramDrawer.vue';
import ProgramImageSelector from './components/program/ProgramImageSelector.vue';
import ProgramImageSelectorButtons from './components/program/ProgramImageSelectorButtons.vue';
import ProgramOwnerAssignment from './components/program/ProgramOwnerAssignment.vue';

import Programs from './components/program/Programs.vue';
import ProgramsList from './components/program/ProgramsList.vue';
import ProgramCard from './components/program/card/ProgramCard.vue';
import ProgramMenu from './components/program/card/ProgramMenu.vue';
import ProgramDetail from './components/program/detail/ProgramDetail.vue';
import ProgramCreatedPlaceholder from './components/program/detail/ProgramCreatedPlaceholder.vue';
import ProgramDisabledMaskContent from './components/program/card/ProgramDisabledMaskContent.vue';
import RulesToolbar from './components/program/rules/RulesToolbar.vue';
import RuleTitle from './components/program/rules/RuleTitle.vue';
import NoRuleFound from './components/program/rules/NoRuleFound.vue';
import ProgramRuleItem from './components/program/rules/RuleItem.vue';

import Rules from './components/rule/Rules.vue';
import RuleCategory from './components/rule/category/Category.vue';
import RulesList from './components/rule/layout/RulesList.vue';
import RulesByProgram from './components/rule/layout/RulesByProgram.vue';
import RulesByTrend from './components/rule/layout/RulesByTrend.vue';
import RulesFilterDrawer from './components/rule/filter/RulesFilterDrawer.vue';
import RuleCard from './components/rule/card/RuleCard.vue';
import RuleCardMaskContent from './components/rule/card/RuleCardMaskContent.vue';
import RuleCardMaskConnector from './components/rule/card/RuleCardMaskConnector.vue';
import RuleCardMaskPrequisiteRules from './components/rule/card/RuleCardMaskPrequisiteRules.vue';
import RuleCardMaskRemainingDates from './components/rule/card/RuleCardMaskRemainingDates.vue';
import RuleCardMaskRecurrence from './components/rule/card/RuleCardMaskRecurrence.vue';
import RuleCardMaskAudience from './components/rule/card/RuleCardMaskAudience.vue';
import RuleCardMaskWhitelist from './components/rule/card/RuleCardMaskWhitelist.vue';
import RuleCardRecurrence from './components/rule/card/RuleCardRecurrence.vue';
import RuleCardPoints from './components/rule/card/RuleCardPoints.vue';
import RuleCardRemainingDates from './components/rule/card/RuleCardRemainingDates.vue';

import CardMask from './components/common/CardMask.vue';

const components = {
  'gamification-overview-widget': Widget,
  'gamification-overview-widget-row': WidgetRow,
  'gamification-overview-widget-empty-row': WidgetEmptyRow,
  'gamification-program-suggester': ProgramSuggester,
  'gamification-program-item': ProgramItem,
  'gamification-card-mask': CardMask,

  'gamification-program-color-picker': ProgramColorPicker,
  'gamification-program-color-picker-drawer': ProgramColorPickerDrawer,
  'gamification-program-drawer': ProgramDrawer,
  'gamification-program-image-selector': ProgramImageSelector,
  'gamification-program-image-selector-buttons': ProgramImageSelectorButtons,
  'gamification-program-owner-assignment': ProgramOwnerAssignment,
  'gamification-program-rule-item': ProgramRuleItem,

  'gamification-rules': Rules,
  'gamification-rules-category': RuleCategory,
  'gamification-rules-list': RulesList,
  'gamification-rules-by-program': RulesByProgram,
  'gamification-rules-by-trend': RulesByTrend,
  'gamification-rules-filter-drawer': RulesFilterDrawer,
  'gamification-rule-card': RuleCard,
  'gamification-rule-card-mask-content': RuleCardMaskContent,
  'gamification-rule-card-mask-connector': RuleCardMaskConnector,
  'gamification-rule-card-mask-prequisite-rules': RuleCardMaskPrequisiteRules,
  'gamification-rule-card-mask-remaining-dates': RuleCardMaskRemainingDates,
  'gamification-rule-card-mask-recurrence': RuleCardMaskRecurrence,
  'gamification-rule-card-mask-audience': RuleCardMaskAudience,
  'gamification-rule-card-mask-whitelist': RuleCardMaskWhitelist,
  'gamification-rule-card-recurrence': RuleCardRecurrence,
  'gamification-rule-card-points': RuleCardPoints,
  'gamification-rule-card-remaining-dates': RuleCardRemainingDates,

  'gamification-programs': Programs,
  'gamification-programs-list': ProgramsList,
  'gamification-program-card': ProgramCard,
  'gamification-program-menu': ProgramMenu,
  'gamification-program-detail': ProgramDetail,
  'gamification-program-created-placeholder': ProgramCreatedPlaceholder,
  'gamification-program-disabled-mask-content': ProgramDisabledMaskContent,
  'gamification-program-rules-toolbar': RulesToolbar,
  'gamification-rule-chip': RuleChip,
  'gamification-program-rule-title': RuleTitle,
  'gamification-program-no-rule-found': NoRuleFound,
};

for (const key in components) {
  Vue.component(key, components[key]);
}

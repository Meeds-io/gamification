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

import Rules from './components/Rules.vue';

import RuleCard from './components/card/RuleCard.vue';
import RuleCardMaskContent from './components/card/RuleCardMaskContent.vue';
import RuleCardMaskConnector from './components/card/RuleCardMaskConnector.vue';
import RuleCardMaskPrequisiteRules from './components/card/RuleCardMaskPrequisiteRules.vue';
import RuleCardMaskRemainingDates from './components/card/RuleCardMaskRemainingDates.vue';
import RuleCardMaskRecurrence from './components/card/RuleCardMaskRecurrence.vue';
import RuleCardMaskAudience from './components/card/RuleCardMaskAudience.vue';
import RuleCardMaskWhitelist from './components/card/RuleCardMaskWhitelist.vue';
import RuleCardRecurrence from './components/card/RuleCardRecurrence.vue';
import RuleCardPoints from './components/card/RuleCardPoints.vue';
import RuleCardRemainingDates from './components/card/RuleCardRemainingDates.vue';

import RuleCategory from './components/category/Category.vue';

import RulesList from './components/layout/RulesList.vue';
import RulesByProgram from './components/layout/RulesByProgram.vue';
import RulesByTrend from './components/layout/RulesByTrend.vue';

import RulesFilterDrawer from './components/filter/RulesFilterDrawer.vue';

const components = {
  'engagement-center-rules': Rules,
  'engagement-center-rules-category': RuleCategory,
  'engagement-center-rules-list': RulesList,
  'engagement-center-rules-by-program': RulesByProgram,
  'engagement-center-rules-by-trend': RulesByTrend,
  'engagement-center-rules-filter-drawer': RulesFilterDrawer,
  'engagement-center-rule-card': RuleCard,
  'engagement-center-rule-card-mask-content': RuleCardMaskContent,
  'engagement-center-rule-card-mask-connector': RuleCardMaskConnector,
  'engagement-center-rule-card-mask-prequisite-rules': RuleCardMaskPrequisiteRules,
  'engagement-center-rule-card-mask-remaining-dates': RuleCardMaskRemainingDates,
  'engagement-center-rule-card-mask-recurrence': RuleCardMaskRecurrence,
  'engagement-center-rule-card-mask-audience': RuleCardMaskAudience,
  'engagement-center-rule-card-mask-whitelist': RuleCardMaskWhitelist,
  'engagement-center-rule-card-recurrence': RuleCardRecurrence,
  'engagement-center-rule-card-points': RuleCardPoints,
  'engagement-center-rule-card-remaining-dates': RuleCardRemainingDates,
};

for (const key in components) {
  Vue.component(key, components[key]);
}

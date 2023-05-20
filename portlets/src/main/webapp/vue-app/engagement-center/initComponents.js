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
 
import ProgramSuggester from './components/common/ProgramSuggester.vue';
import EngagementCenter from './components/EngagementCenter.vue';
import NoResultFound from './components/common/NoResultFound.vue';
import ResultNotFound from './components/common/ResultNotFound.vue';
import RulesToolbar from './components/programs/RulesToolbar.vue';
import NoRuleFound from './components/programs/NoRuleFound.vue';
import RuleTitle from './components/programs/RuleTitle.vue';
import RuleItem from './components/programs/RuleItem.vue';

import Programs from './components/programs/Programs.vue';
import ProgramCard from './components/programs/ProgramCard.vue';
import ProgramMenu from './components/programs/ProgramMenu.vue';
import ProgramDrawer from './components/programs/ProgramDrawer.vue';
import ProgramOwnersDrawer from './components/programs/ProgramOwnersDrawer.vue';
import ProgramDetail from './components/programs/ProgramDetail.vue';
import ProgramOwnerAssignment from './components/programs/ProgramOwnerAssignment.vue';

import Rules from './components/rules/Rules.vue';
import RuleCard from './components/rules/card/RuleCard.vue';
import RuleCardMask from './components/rules/card/RuleCardMask.vue';
import RuleCardMaskContent from './components/rules/card/RuleCardMaskContent.vue';
import RuleCardMaskPrequisiteRules from './components/rules/card/RuleCardMaskPrequisiteRules.vue';
import RuleCardMaskRemainingDates from './components/rules/card/RuleCardMaskRemainingDates.vue';
import RuleCardMaskRecurrence from './components/rules/card/RuleCardMaskRecurrence.vue';
import RuleCardMenu from './components/rules/card/RuleCardMenu.vue';
import RuleCardRecurrence from './components/rules/card/RuleCardRecurrence.vue';
import RuleCardPoints from './components/rules/card/RuleCardPoints.vue';
import RuleCardRemainingDates from './components/rules/card/RuleCardRemainingDates.vue';

import RuleCategories from './components/rules/category/Categories.vue';
import RuleCategory from './components/rules/category/Category.vue';
import RulesList from './components/rules/category/RulesList.vue';

import Assignment from './components/common/Assignment.vue';
import AvatarsList from './components/common/AvatarsList.vue';
import ImageSelector from './components/common/ImageSelector.vue';
import WelcomeMessage from './components/common/WelcomeMessage.vue';

import RuleActionValue from './components/actionValues/RuleActionValue.vue';
import ChallengeActionValue from './components/actionValues/ChallengeActionValue.vue';

const components = {
  'program-suggester': ProgramSuggester,
  'engagement-center-no-results': NoResultFound,
  'engagement-center-result-not-found': ResultNotFound,
  'engagement-center': EngagementCenter,
  'engagement-center-programs': Programs,
  'engagement-center-program-card': ProgramCard,
  'engagement-center-program-menu': ProgramMenu,
  'engagement-center-program-drawer': ProgramDrawer,
  'engagement-center-program-owners-drawer': ProgramOwnersDrawer,
  'engagement-center-program-detail': ProgramDetail,
  'engagement-center-program-owner-assignment': ProgramOwnerAssignment,
  'engagement-center-rules-toolbar': RulesToolbar,
  'engagement-center-rule-item': RuleItem,
  'engagement-center-rule-title': RuleTitle,
  'engagement-center-no-rule-found': NoRuleFound,
  'engagement-center-rules': Rules,
  'engagement-center-rules-categories': RuleCategories,
  'engagement-center-rules-category': RuleCategory,
  'engagement-center-rules-list': RulesList,
  'engagement-center-rule-card': RuleCard,
  'engagement-center-rule-card-menu': RuleCardMenu,
  'engagement-center-rule-card-mask': RuleCardMask,
  'engagement-center-rule-card-mask-content': RuleCardMaskContent,
  'engagement-center-rule-card-mask-prequisite-rules': RuleCardMaskPrequisiteRules,
  'engagement-center-rule-card-mask-remaining-dates': RuleCardMaskRemainingDates,
  'engagement-center-rule-card-mask-recurrence': RuleCardMaskRecurrence,
  'engagement-center-rule-card-recurrence': RuleCardRecurrence,
  'engagement-center-rule-card-points': RuleCardPoints,
  'engagement-center-rule-card-remaining-dates': RuleCardRemainingDates,
  'engagement-center-assignment': Assignment,
  'engagement-center-avatars-list': AvatarsList,
  'engagement-center-image-selector': ImageSelector,
  'engagement-center-welcome-message': WelcomeMessage,
  'rule-action-value': RuleActionValue,
  'challenge-action-value': ChallengeActionValue,
};

for (const key in components) {
  Vue.component(key, components[key]);
}

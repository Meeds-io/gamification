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
 
import Challenges from './components/challenges/Challenges.vue';
import ChallengeCard from './components/challenges/ChallengeCard.vue';
import ChallengesList from './components/challenges/ChallengesList.vue';
import DomainChallengesList from './components/challenges/DomainChallengesList.vue';
import ChallengeDatePicker from './components/challenges/ChallengeDatePicker.vue';
import WinnersDrawer from './components/challenges/WinnersDrawer.vue';
import ProgramSuggester from './components/common/ProgramSuggester.vue';
import EngagementCenter from './components/EngagementCenter.vue';
import NoResultFound from './components/common/NoResultFound.vue';
import ResultNotFound from './components/common/ResultNotFound.vue';

import Programs from './components/programs/Programs.vue';
import ProgramCard from './components/programs/ProgramCard.vue';
import ProgramMenu from './components/programs/ProgramMenu.vue';
import ProgramDrawer from './components/programs/ProgramDrawer.vue';
import ProgramOwnersDrawer from './components/programs/ProgramOwnersDrawer.vue';
import ProgramDetail from './components/programs/ProgramDetail.vue';
import ProgramOwnerAssignment from './components/programs/ProgramOwnerAssignment.vue';

import RuleTitle from './components/rules/RuleTitle.vue';
import RuleItem from './components/rules/RuleItem.vue';
import RuleFormDrawer from './components/rules/RuleFormDrawer.vue';
import RuleRecurrenceInput from './components/rules/form/RuleRecurrenceInput.vue';
import ButtonGroupItem from './components/rules/form/ButtonGroupItem.vue';
import RuleDetailDrawer from './components/rules/RuleDetailDrawer.vue';
import RulesToolbar from './components/rules/RulesToolbar.vue';
import NoRuleFound from './components/rules/NoRuleFound.vue';

import Assignment from './components/common/Assignment.vue';
import AvatarsList from './components/common/AvatarsList.vue';
import DescriptionEditor from './components/common/DescriptionEditor.vue';
import ImageSelector from './components/common/ImageSelector.vue';
import WelcomeMessage from './components/common/WelcomeMessage.vue';

import RuleActionValue from './components/actionValues/RuleActionValue.vue';
import ChallengeActionValue from './components/actionValues/ChallengeActionValue.vue';

const components = {
  'challenges': Challenges,
  'challenge-card': ChallengeCard,
  'challenges-list': ChallengesList,
  'domain-challenges-list': DomainChallengesList,
  'challenge-date-picker': ChallengeDatePicker,
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
  'engagement-center-rule-title': RuleTitle,
  'engagement-center-rule-item': RuleItem,
  'engagement-center-rule-form-drawer': RuleFormDrawer,
  'engagement-center-rule-recurrence-input': RuleRecurrenceInput,
  'engagement-center-button-group-item': ButtonGroupItem,
  'engagement-center-rule-detail-drawer': RuleDetailDrawer,
  'engagement-center-rules-toolbar': RulesToolbar,
  'engagement-center-no-rule-found': NoRuleFound,
  'engagement-center-winners-details': WinnersDrawer,
  'engagement-center-assignment': Assignment,
  'engagement-center-avatars-list': AvatarsList,
  'engagement-center-description-editor': DescriptionEditor,
  'engagement-center-image-selector': ImageSelector,
  'engagement-center-welcome-message': WelcomeMessage,
  'rule-action-value': RuleActionValue,
  'challenge-action-value': ChallengeActionValue,
};

for (const key in components) {
  Vue.component(key, components[key]);
}

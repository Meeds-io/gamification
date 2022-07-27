/**
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
import Challenges from './components/challenges/Challenges.vue';
import WelcomeMessage from './components/challenges/WelcomeMessage.vue';
import ChallengeCard from './components/challenges/ChallengeCard.vue';
import ChallengesList from './components/challenges/ChallengesList.vue';
import DomainChallengesList from './components/challenges/DomainChallengesList.vue';
import ChallengeDrawer from './components/challenges/ChallengeDrawer.vue';
import Assignment from './components/challenges/Assignment.vue';
import ChallengeDatePicker from './components/challenges/ChallengeDatePicker.vue';
import ChallengeDescription from './components/challenges/ChallengeDescription.vue';
import ChallengeDetailsDrawer from './components/challenges/ChallengeDetailsDrawer.vue';
import AnnouncementDrawer from './components/challenges/AnnouncementDrawer.vue';
import WinnersDrawer from './components/challenges/WinnersDrawer.vue';
import challengeProgram from './components/challenges/ProgramSuggester.vue';
import EngagementCenter from './components/EngagementCenter.vue';
import Programs from './components/programs/Programs.vue';
import ProgramsList from './components/programs/ProgramsList.vue';
import ProgramCard from './components/programs/ProgramCard.vue';

const components = {
  'challenges': Challenges,
  'challenge-welcome-message': WelcomeMessage,
  'challenge-card': ChallengeCard,
  'challenges-list': ChallengesList,
  'domain-challenges-list': DomainChallengesList,
  'challenge-drawer': ChallengeDrawer,
  'challenge-assignment': Assignment,
  'challenge-date-picker': ChallengeDatePicker,
  'challenge-description': ChallengeDescription,
  'challenge-details-drawer': ChallengeDetailsDrawer,
  'announce-drawer': AnnouncementDrawer,
  'challenge-winners-details': WinnersDrawer,
  'challenge-program': challengeProgram,
  'engagement-center': EngagementCenter,
  'engagement-center-programs': Programs,
  'engagement-center-programs-list': ProgramsList,
  'engagement-center-program-card': ProgramCard,
};

for (const key in components) {
  Vue.component(key, components[key]);
}
import * as  challengesServices from './js/challengesServices';

if (!Vue.prototype.$challengesServices) {
  window.Object.defineProperty(Vue.prototype, '$challengesServices', {
    value: challengesServices,
  });
}
import  * as challengeUtils  from './js/challengesUtils';

if (!Vue.prototype.$challengeUtils) {
  window.Object.defineProperty(Vue.prototype, '$challengeUtils', {
    value: challengeUtils,
  });
}
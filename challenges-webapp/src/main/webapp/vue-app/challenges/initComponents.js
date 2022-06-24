import Challenges from './components/Challenges.vue';
import WelcomeMessage from './components/WelcomeMessage.vue';
import ChallengeCard from './components/ChallengeCard.vue';
import ChallengesList from './components/ChallengesList.vue';
import DomainChallengesList from './components/DomainChallengesList.vue';
import ChallengeDrawer from './components/ChallengeDrawer.vue';
import Assignment from './components/Assignment.vue';
import ChallengeDatePicker from './components/ChallengeDatePicker.vue';
import ChallengeDescription from './components/ChallengeDescription.vue';
import ChallengeDetailsDrawer from './components/ChallengeDetailsDrawer.vue';
import AnnouncementDrawer from './components/AnnouncementDrawer.vue';
import WinnersDrawer from './components/WinnersDrawer.vue';
import challengeProgram from './components/ProgramSuggester.vue';

const components = {
  'challenges': Challenges,
  'welcome-message': WelcomeMessage,
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
  'challenge-program': challengeProgram
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
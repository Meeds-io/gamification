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
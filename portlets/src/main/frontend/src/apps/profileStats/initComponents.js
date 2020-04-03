import ProfileStats from './components/ProfileStats.vue';
import ConnectionsRequests from './components/ConnectionsRequests.vue';
import GamificationRank from './components/GamificationRank.vue';
import SpacesRequests from './components/SpacesRequests.vue';
import TotalPoints from './components/TotalPoints.vue';
import UserDashbord from './components/UserDashbord.vue';

const components = {
  'profile-stats': ProfileStats,
  'connections-requests': ConnectionsRequests,
  'gamification-rank': GamificationRank,
  'spaces-requests': SpacesRequests,
  'total-points': TotalPoints,
  'user-dashbord': UserDashbord
};

for(const key in components) {
  Vue.component(key, components[key]);
}

import ProfileStats from './components/ProfileStats.vue';
import ConnectionsRequests from './components/ConnectionsRequests.vue';
import GamificationRank from './components/GamificationRank.vue';
import SpacesRequests from './components/SpacesRequests.vue';
import TotalPoints from './components/TotalPoints.vue';
import UserDashbord from './components/UserDashbord.vue';
import AchievementItem from './components/AchievementItem.vue';
import AchievementsDrawer from './components/AchievementsDrawer.vue';

const components = {
  'profile-stats': ProfileStats,
  'connections-requests': ConnectionsRequests,
  'gamification-rank': GamificationRank,
  'spaces-requests': SpacesRequests,
  'total-points': TotalPoints,
  'user-dashbord': UserDashbord,
  'achievements-drawer': AchievementsDrawer,
  'achievement-item': AchievementItem,
};

for(const key in components) {
  Vue.component(key, components[key]);
}

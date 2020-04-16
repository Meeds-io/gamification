import UsersLeaderboard from './components/UsersLeaderboard.vue';
import UsersLeaderboardDomainOption from './components/UsersLeaderboardDomainOption.vue';
import UsersLeaderboardProfile from './components/UsersLeaderboardProfile.vue';
import UsersLeaderboardChart from './components/UsersLeaderboardChart.vue';

const components = {
  'users-leaderboard': UsersLeaderboard,
  'users-leaderboard-domain-option': UsersLeaderboardDomainOption,
  'users-leaderboard-profile': UsersLeaderboardProfile,
  'users-leaderboard-chart': UsersLeaderboardChart,
};

for(const key in components) {
  Vue.component(key, components[key]);
}

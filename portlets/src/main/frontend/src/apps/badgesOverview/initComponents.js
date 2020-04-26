import BadgesOverview from './components/BadgesOverview.vue';
import BadgesOverviewItem from './components/BadgesOverviewItem.vue';
import BadgesOverviewDrawer from './components/BadgesOverviewDrawer.vue';
import BadgesOverviewDrawerItem from './components/BadgesOverviewDrawerItem.vue';

const components = {
  'badges-overview': BadgesOverview,
  'badges-overview-item': BadgesOverviewItem,
  'badges-overview-drawer': BadgesOverviewDrawer,
  'badges-overview-drawer-item': BadgesOverviewDrawerItem,
};

for(const key in components) {
  Vue.component(key, components[key]);
}

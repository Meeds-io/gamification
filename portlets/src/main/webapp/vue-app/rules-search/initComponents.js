import RuleSearchCard from './components/RuleSearchCard.vue';
import RuleDetailDrawer from '../rules/components/drawers/RuleDetailDrawer.vue';

const components = {
  'engagement-center-rule-search-card': RuleSearchCard,
  'engagement-center-rule-detail-drawer': RuleDetailDrawer,
};

for (const key in components) {
  Vue.component(key, components[key]);
}
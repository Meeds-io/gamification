import RuleSearchCard from './components/RuleSearchCard.vue';

const components = {
  'engagement-center-rule-search-card': RuleSearchCard,
};

for (const key in components) {
  Vue.component(key, components[key]);
}
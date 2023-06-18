import RuleActivity from './components/favorites/RuleActivity.vue';
const components = {
  'rule-activity': RuleActivity,
};

for (const key in components) {
  Vue.component(key, components[key]);
}
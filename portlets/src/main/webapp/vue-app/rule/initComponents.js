import ManageRules from './components/ManageRules.vue';
import RuleList from './components/RuleList.vue';
import SaveRuleForm from './components/SaveRuleForm.vue';

const components = {
  'gamification-manage-rule': ManageRules,
  'gamification-rule-list': RuleList,
  'gamification-save-rule': SaveRuleForm,
};

for (const key in components) {
  Vue.component(key, components[key]);
}

import  * as ruleServices  from './RuleServices';

if (!Vue.prototype.$RuleServices) {
  window.Object.defineProperty(Vue.prototype, '$RuleServices', {
    value: ruleServices,
  });
}
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

import  * as ruleServices  from '../../js/RuleServices.js';

if (!Vue.prototype.$ruleServices) {
  window.Object.defineProperty(Vue.prototype, '$ruleServices', {
    value: ruleServices,
  });
}
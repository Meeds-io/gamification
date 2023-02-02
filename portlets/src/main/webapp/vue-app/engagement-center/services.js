import * as  challengesServices from './js/challengesServices';
import  * as programsServices  from './js/programsServices';
import  * as ruleServices  from './js/RuleServices.js';
import  * as engagementCenterUtils  from './js/engagementCenterUtils';

if (!Vue.prototype.$challengesServices) {
  window.Object.defineProperty(Vue.prototype, '$challengesServices', {
    value: challengesServices,
  });
}

if (!Vue.prototype.$engagementCenterUtils) {
  window.Object.defineProperty(Vue.prototype, '$engagementCenterUtils', {
    value: engagementCenterUtils,
  });
}

if (!Vue.prototype.$programsServices) {
  window.Object.defineProperty(Vue.prototype, '$programsServices', {
    value: programsServices,
  });
}

if (!Vue.prototype.$ruleServices) {
  window.Object.defineProperty(Vue.prototype, '$ruleServices', {
    value: ruleServices,
  });
}
import Realizations from './components/Realizations.vue';
import SelectPeriod from './components/SelectPeriod.vue';

const components = {
  'realizations': Realizations,
  'select-period': SelectPeriod,
};

for (const key in components) {
  Vue.component(key, components[key]);
}
import  * as realizationsServices  from './realizationsServices';

if (!Vue.prototype.$realizationsServices) {
  window.Object.defineProperty(Vue.prototype, '$realizationsServices', {
    value: realizationsServices,
  });
}
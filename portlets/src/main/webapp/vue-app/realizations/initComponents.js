import Realizations from './components/Realizations.vue';
import SelectPeriod from './components/SelectPeriod.vue';
import EditRealizationDrawer  from './components/EditRealizationDrawer.vue';

const components = {
  'realizations': Realizations,
  'select-period': SelectPeriod,
  'edit-realization-drawer': EditRealizationDrawer,
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
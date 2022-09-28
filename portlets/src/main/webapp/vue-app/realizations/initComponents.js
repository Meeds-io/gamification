import Realizations from './components/Realizations.vue';
import RealizationItem from './components/RealizationItem.vue';
import SelectPeriod from './components/SelectPeriod.vue';
import EditRealizationDrawer  from './components/EditRealizationDrawer.vue';
import FilterRealizationsDrawer from './components/FilterRealizationsDrawer.vue';
import FilterRealizationsProgramList from './components/FilterRealizationsProgramList.vue';

const components = {
  'realizations': Realizations,
  'realization-item': RealizationItem,
  'select-period': SelectPeriod,
  'edit-realization-drawer': EditRealizationDrawer,
  'filter-realizations-drawer': FilterRealizationsDrawer,
  'realizations-filter-program-list': FilterRealizationsProgramList,
};

for (const key in components) {
  Vue.component(key, components[key]);
}
import  * as realizationsServices  from './realizationsServices';
import  * as programsServices  from '../engagement-center/js/programsServices';

if (!Vue.prototype.$programsServices) {
  window.Object.defineProperty(Vue.prototype, '$programsServices', {
    value: programsServices,
  });
}
if (!Vue.prototype.$realizationsServices) {
  window.Object.defineProperty(Vue.prototype, '$realizationsServices', {
    value: realizationsServices,
  });
}
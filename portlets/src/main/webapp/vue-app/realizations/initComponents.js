import Realizations from './components/Realizations.vue';
import RealizationItem from './components/RealizationItem.vue';
import RealizationItemMobile from './components/RealizationItemMobile.vue';
import SelectPeriod from './components/SelectPeriod.vue';
import NoResultFound from '../engagement-center/components/common/NoResultFound.vue';
import ProgramSuggester from '../engagement-center/components/common/ProgramSuggester.vue';
import EditRealizationDrawer  from './components/EditRealizationDrawer.vue';
import FilterRealizationsDrawer from './components/FilterRealizationsDrawer.vue';
import FilterRealizationsProgramList from './components/FilterRealizationsProgramList.vue';
import GranteeAttendeeItem from './components/GranteeAttendeeItem.vue';
import ProgramAttendeeItem from './components/ProgramAttendeeItem.vue';

const components = {
  'realizations': Realizations,
  'realization-item': RealizationItem,
  'realization-item-mobile': RealizationItemMobile,
  'select-period': SelectPeriod,
  'edit-realization-drawer': EditRealizationDrawer,
  'filter-realizations-drawer': FilterRealizationsDrawer,
  'realizations-filter-program-list': FilterRealizationsProgramList,
  'engagement-center-no-results': NoResultFound,
  'grantee-attendee-item': GranteeAttendeeItem,
  'program-attendee-item': ProgramAttendeeItem,
  'program-suggester': ProgramSuggester,
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
import Realizations from './components/Realizations.vue';
import RealizationItem from './components/RealizationItem.vue';
import RealizationItemMobile from './components/RealizationItemMobile.vue';
import SelectPeriod from './components/SelectPeriod.vue';
import NoResultFound from '../engagement-center/components/common/NoResultFound.vue';
import ProgramSuggester from '../engagement-center/components/common/ProgramSuggester.vue';
import RealizationFilterDrawer from './components/filter/RealizationFilterDrawer.vue';
import RealizationProgramFilter from './components/filter/RealizationProgramFilter.vue';
import GranteeAttendeeItem from './components/GranteeAttendeeItem.vue';
import ProgramAttendeeItem from './components/ProgramAttendeeItem.vue';
import RealizationExportButton from './components/RealizationExportButton.vue';

const components = {
  'realizations': Realizations,
  'realization-item': RealizationItem,
  'realization-item-mobile': RealizationItemMobile,
  'select-period': SelectPeriod,
  'filter-realizations-drawer': RealizationFilterDrawer,
  'realizations-filter-program-list': RealizationProgramFilter,
  'engagement-center-no-results': NoResultFound,
  'grantee-attendee-item': GranteeAttendeeItem,
  'program-attendee-item': ProgramAttendeeItem,
  'program-suggester': ProgramSuggester,
  'realizations-export-button': RealizationExportButton,
};

for (const key in components) {
  Vue.component(key, components[key]);
}
import Realizations from './components/Realizations.vue';
import EditRealizationDrawer  from './components/body/actions/EditRealizationDrawer.vue';
import RealizationsExportXslx from './components/header/actions/RealizationsExportXslx.vue';
import RealizationsHeader from './components/header/RealizationsHeader.vue';
import RealizationsHeaderLeft from './components/header/RealizationsHeaderLeft.vue';
import RealizationsHeaderRighterLeft from './components/header/RealizationsHeaderLeft.vue';
import RealizationsFilter from './components/header/actions/RealizationsFilter.vue';
import RealizationsLoadMore from './components/body/table/action/RealizationsLoadMore.vue';
import RealizationsTableLoadMore from './components/body/RealizationsTableLoadMore.vue';
import RealizationsTable from './components/body/table/RealizationsTable.vue';
import RealizationsBody from './components/body/RealizationsBody.vue';
const components = {
  'realizations': Realizations,
  'edit-realization-drawer': EditRealizationDrawer,
  'realizations-export-xslx': RealizationsExportXslx,
  'realizations-header': RealizationsHeader,
  'realizations-header-left': RealizationsHeaderLeft,
  'realizations-header-righter-left': RealizationsHeaderRighterLeft,
  'realizations-filter': RealizationsFilter,
  'realizations-load-more': RealizationsLoadMore,
  'realizations-table-load-more': RealizationsTableLoadMore,
  'realizations-table': RealizationsTable,
  'realizations-body': RealizationsBody,
};

for (const key in components) {
  Vue.component(key, components[key]);
}
import  * as realizationsServices  from './js/realizationsServices.js';

if (!Vue.prototype.$realizationsServices) {
  window.Object.defineProperty(Vue.prototype, '$realizationsServices', {
    value: realizationsServices,
  });
}
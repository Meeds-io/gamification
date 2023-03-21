import './initComponents.js';
import './extensions.js';
import * as challengesServices from '../engagement-center/js/challengesServices';

if (!Vue.prototype.$challengesServices) {
  window.Object.defineProperty(Vue.prototype, '$challengesServices', {
    value: challengesServices,
  });
}

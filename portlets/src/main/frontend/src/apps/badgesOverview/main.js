import './initComponents.js';

//get overrided components if exists
if (extensionRegistry) {
  const components = extensionRegistry.loadComponents('BadgesOverview');
  if (components && components.length > 0) {
   components.forEach(cmp => {
     Vue.component(cmp.componentName, cmp.componentOptions);
   });
  }
}

document.dispatchEvent(new CustomEvent('displayTopBarLoading'));

Vue.use(Vuetify);

const vuetify = new Vuetify({
    dark: true,
    iconfont: '',
});

// getting language of user
const lang = eXo && eXo.env && eXo.env.portal && eXo.env.portal.language || 'en';

const resourceBundleName = 'locale.addon.Gamification';
const url = `${eXo.env.portal.context}/${eXo.env.portal.rest}/i18n/bundle/${resourceBundleName}-${lang}.json`;
const appId = 'badgesOverview';

export function init() {
  //getting locale ressources
  exoi18n.loadLanguageAsync(lang, url)
    .then(i18n => {
        // init Vue app when locale ressources are ready
        new Vue({
            template: `<badges-overview id='${appId}'></badges-overview>`,
            i18n,
            vuetify,
        }).$mount(`#${appId}`);
    });
}

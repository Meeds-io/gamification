/*
 * This file is part of the Meeds project (https://meeds.io/).
 * Copyright (C) 2020 Meeds Association
 * contact@meeds.io
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
import './initComponents.js';

//get overrided components if exists
if (extensionRegistry) {
  const components = extensionRegistry.loadComponents('PopularSpaces');
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
const appId = 'popularSpacesApplication';

export function init(period, limit) {
  //getting locale ressources
  exoi18n.loadLanguageAsync(lang, url)
    .then(i18n => {
      const appElement = document.createElement('div');
      appElement.id = appId;
  
      // init Vue app when locale ressources are ready
      new Vue({
        template: `<popular-spaces id="${appId}" v-cacheable period="${period}" limit="${limit}" />`,
        i18n,
        vuetify,
      }).$mount(appElement);
    });
}
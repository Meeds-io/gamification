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
  const components = extensionRegistry.loadComponents('BadgesOverview');
  if (components && components.length > 0) {
    components.forEach(cmp => {
      Vue.component(cmp.componentName, cmp.componentOptions);
    });
  }
}

const vuetify = Vue.prototype.vuetifyOptions;

// getting language of user
const lang = eXo?.env?.portal?.language || 'en';

const urls = [
  `/gamification-portlets/i18n/locale.addon.Gamification?lang=${lang}`,
  `/gamification-portlets/i18n/locale.portlet.Challenges?lang=${lang}`
];

const appId = 'badgesOverview';

export function init(portletStorageId, showName, sortBy, canEdit, pageRef) {
  //getting locale ressources
  exoi18n.loadLanguageAsync(lang, urls)
    .then(i18n => {
      // init Vue app when locale ressources are ready
      Vue.createApp({
        data: {
          portletStorageId,
          showName,
          sortBy,
          canEdit,
          pageRef,
        },
        template: `<badges-overview id='${appId}' />`,
        i18n,
        vuetify,
      }, `#${appId}`, 'Badges Overview');
    });
}

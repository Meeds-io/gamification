/*
 * This file is part of the Meeds project (https://meeds.io/).
 *
 * Copyright (C) 2020 - 2023 Meeds Association contact@meeds.io
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
import './initComponents.js';

const appId = 'connectorAdminSettings';

//getting language of the PLF
const lang = eXo.env.portal.language || 'en';

//should expose the locale ressources as REST API 
const url = `/gamification-portlets/i18n/locale.portlet.Challenges?lang=${lang}`;

export function init() {
  exoi18n.loadLanguageAsync(lang, url)
    .then(i18n => {
      Vue.createApp({
        template: `<gamification-admin-connector-settings id="${appId}" />`,
        i18n,
        vuetify: Vue.prototype.vuetifyOptions,
      }, `#${appId}`, 'Admin Connectors Settings App');
    })
    .finally(() => {
      Vue.prototype.$utils.includeExtensions('gamificationAdminConnectorsExtensions');
      Vue.prototype.$utils.includeExtensions('engagementCenterConnectors');
    });
}

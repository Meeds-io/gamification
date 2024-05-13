/*
 * This file is part of the Meeds project (https://meeds.io/).
 * 
 * Copyright (C) 2023 Meeds Association contact@meeds.io
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 * 
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

const lang = eXo?.env?.portal?.language || 'en';
const urls = [
  `${eXo.env.portal.context}/${eXo.env.portal.rest}/i18n/bundle/locale.portlet.Challenges-${lang}.json`
];
const appId = 'createProgramButton';

export function init(canManagePrograms) {
  if (canManagePrograms) {
    exoi18n.loadLanguageAsync(lang, urls)
      .then(i18n => {
        Vue.createApp({
          template: `<gamification-overview-create-program id="${appId}" />`,
          i18n,
          vuetify: Vue.prototype.vuetifyOptions,
        }, `#${appId}`, 'Create Program');
      });
  } else {
    Vue.prototype.$updateApplicationVisibility(false, document.querySelector(`#${appId}`));
  }
}

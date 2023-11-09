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

const vuetify = Vue.prototype.vuetifyOptions;

// getting language of user
const lang = eXo?.env?.portal?.language || 'en';

const resourceBundleName = 'locale.addon.Gamification';
const urls = [
  `${eXo.env.portal.context}/${eXo.env.portal.rest}/i18n/bundle/${resourceBundleName}-${lang}.json`,
  `${eXo.env.portal.context}/${eXo.env.portal.rest}/i18n/bundle/locale.portlet.social.PeopleListApplication-${lang}.json`
];

const appId = 'profile-stats-portlet';
const cacheId = `${appId}_${eXo.env.portal.profileOwnerIdentityId}`;

export function init() {
  const appElement = document.createElement('div');
  appElement.id = appId;

  //getting locale ressources
  exoi18n.loadLanguageAsync(lang, urls)
    .then(i18n => {
      // init Vue app when locale ressources are ready
      Vue.createApp({
        data: {
          isAnonymous: !eXo.env.portal.userIdentityId?.length,
          now: Date.now(),
          actionValueExtensions: [],
        },
        template: `<profile-stats id="${appId}" v-cacheable="{cacheId: '${cacheId}'}" />`,
        created() {
          window.setInterval(() => this.now = Date.now(), 1000);
        },
        i18n,
        vuetify,
      }, appElement, 'Profile Stats');
    })
    .finally(() => Vue.prototype.$utils?.includeExtensions('engagementCenterActions'));
}

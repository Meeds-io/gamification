/*
 * This file is part of the Meeds project (https://meeds.io/).
 * Copyright (C) 2022 Meeds Association
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

const lang = window?.eXo?.env?.portal?.language || 'en';
const urls = [
  `/gamification-portlets/i18n/locale.addon.Gamification?lang=${lang}`,
  `/gamification-portlets/i18n/locale.portlet.Challenges?lang=${lang}`
];
const appId = 'rulesOverview';

export function init(
  portletStorageId,
  lockedRulesLimit,
  endingRulesLimit,
  availableRulesLimit,
  upcomingRulesLimit,
  rulesSortBy,
  canEdit,
  pageRef) {
  exoi18n.loadLanguageAsync(lang, urls)
    .then(i18n => {
      Vue.createApp({
        data: {
          isAnonymous: !eXo.env.portal.userIdentityId?.length,
          now: Date.now(),
          interval: null,
          portletStorageId,
          lockedRulesLimit,
          endingRulesLimit,
          availableRulesLimit,
          upcomingRulesLimit,
          rulesSortBy,
          canEdit: portletStorageId && canEdit || false,
          pageRef,
          displayNotPublicallyVisible: eXo.env.portal.portalName === 'public' && canEdit,
        },
        template: `<gamification-rules-overview id="${appId}" />`,
        created() {
          this.interval = window.setInterval(() => this.now = Date.now(), 1000);
        },
        beforeDestroy() {
          if (this.interval) {
            window.clearInterval(this.interval);
          }
        },
        i18n,
        vuetify: Vue.prototype.vuetifyOptions,
      }, `#${appId}`, 'Rules Overview');
    }).finally(() => {
      Vue.prototype.$utils?.includeExtensions?.('engagementCenterActions');
      Vue.prototype.$utils?.includeExtensions?.('engagementCenterConnectors');
      Vue.prototype.$utils?.includeExtensions?.('engagementCenterConnectorEvents');
    });
}
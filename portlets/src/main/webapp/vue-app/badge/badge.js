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
import BadgeApp from './BadgeApp.vue';

$(document).ready(() => {
  const lang = window.eXo?.env?.portal?.language;
  const url = `${window.eXo?.env?.portal?.context}/${window.eXo?.env?.portal?.rest}/i18n/bundle/locale.addon.Gamification-${lang}.json`;

  exoi18n.loadLanguageAsync(lang, url).then(i18n => {
    const vueApp = new Vue({
      render: (h) => h(BadgeApp),
      i18n
    }).$mount('#manage-badges-portlet > .container');
    Vue.prototype.$vueT = Vue.prototype.$t;
    Vue.prototype.$t = (key, defaultValue) => {
      const translation = vueApp.$vueT(key);
      return translation !== key && translation || defaultValue;
    };
  });
});






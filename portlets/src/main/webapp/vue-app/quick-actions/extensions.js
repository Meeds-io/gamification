/*
 * This file is part of the Meeds project (https://meeds.io/).
 * 
 * Copyright (C) 2020 - 2025 Meeds Association contact@meeds.io
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

extensionRegistry.registerExtension('QuickAction', 'Extension', {
  id: 'listPrograms',
  icon: 'fa-puzzle-piece',
  name: 'quickActions.listPrograms.name',
  description: 'quickActions.listPrograms.description',
  click: () => new Promise(resolve => {
    window.require(['SHARED/eXoVueI18n', 'PORTLET/gamification-portlets/programsOverview'], exoi18n => initProgramListDrawer(exoi18n, resolve));
  }),
});

extensionRegistry.registerExtension('QuickAction', 'Extension', {
  id: 'listActions',
  icon: 'fa-trophy',
  name: 'quickActions.listActions.name',
  description: 'quickActions.listActions.description',
  click: () => new Promise(resolve => {
    window.require(['SHARED/eXoVueI18n', 'PORTLET/gamification-portlets/challengesOverview'], exoi18n => initActionListDrawer(exoi18n, resolve));
  }),
});

extensionRegistry.registerExtension('QuickAction', 'Extension', {
  id: 'leaderboard',
  icon: 'fa-medal',
  name: 'quickActions.leaderboard.name',
  description: 'quickActions.leaderboard.description',
  click: () => new Promise(resolve => {
    window.require(['SHARED/eXoVueI18n', 'PORTLET/gamification-portlets/topChallengers'], exoi18n => initLeaderboardDrawer(exoi18n, resolve));
  }),
});

extensionRegistry.registerExtension('QuickAction', 'Extension', {
  id: 'achievements',
  icon: 'fa-trophy',
  name: 'quickActions.achievements.name',
  description: 'quickActions.achievements.description',
  click: () => new Promise(resolve => {
    window.require(['SHARED/eXoVueI18n', 'PORTLET/gamification-portlets/myContributions'], exoi18n => initAchievementsDrawer(exoi18n, resolve));
  }),
});

async function initProgramListDrawer(exoi18n, callback) {
  const appId = 'programs-list-actions';
  if (!document.querySelector(`#${appId}`)) {
    const parent = document.createElement('div');
    parent.id = appId;
    document.querySelector('#vuetify-apps').appendChild(parent);
    await initProgramListDrawerApp(appId, exoi18n);
  }
  document.dispatchEvent(new CustomEvent('quick-action-programs-list-drawer', {detail: callback}));
}

async function initActionListDrawer(exoi18n, callback) {
  const appId = 'action-list-actions';
  if (!document.querySelector(`#${appId}`)) {
    const parent = document.createElement('div');
    parent.id = appId;
    document.querySelector('#vuetify-apps').appendChild(parent);
    await initActionListDrawerApp(appId, exoi18n);
  }
  document.dispatchEvent(new CustomEvent('quick-action-actions-list-drawer'));
  callback();
}

async function initLeaderboardDrawer(exoi18n, callback) {
  const appId = 'leaderboard-quick-action';
  if (!document.querySelector(`#${appId}`)) {
    const parent = document.createElement('div');
    parent.id = appId;
    document.querySelector('#vuetify-apps').appendChild(parent);
    await initLeaderboardDrawerApp(appId, exoi18n);
  }
  document.dispatchEvent(new CustomEvent('quick-action-leaderboard-drawer'));
  callback();
}

async function initAchievementsDrawer(exoi18n, callback) {
  const appId = 'achievments-quick-action';
  if (!document.querySelector(`#${appId}`)) {
    const parent = document.createElement('div');
    parent.id = appId;
    document.querySelector('#vuetify-apps').appendChild(parent);
    await initAchievementsDrawerApp(appId, exoi18n);
  }
  document.dispatchEvent(new CustomEvent('quick-action-achievments-drawer'));
  callback();
}

function initAchievementsDrawerApp(appId, exoi18n) {
  const lang = eXo.env.portal.language;
  const urls = [
    `/gamification-portlets/i18n/locale.addon.Gamification?lang=${lang}`,
    `/gamification-portlets/i18n/locale.portlet.Challenges?lang=${lang}`
  ];
  return new Promise(resolve => exoi18n.loadLanguageAsync(lang, urls)
    .then(i18n => Vue.createApp({
      template: `
        <div id="${appId}">
          <users-leaderboard-profile-achievements-drawer ref="drawer" />
          <engagement-center-rule-extensions />
        </div>
      `,
      created() {
        document.addEventListener('quick-action-achievments-drawer', this.openDrawer);
      },
      mounted() {
        document.dispatchEvent(new CustomEvent('hideTopBarLoading'));
        resolve();
      },
      beforeDestroy() {
        document.removeEventListener('quick-action-achievments-drawer', this.openDrawer);
      },
      methods: {
        openDrawer() {
          this.$refs.drawer.openByIdentityId(eXo.env.portal.userIdentityId);
        },
      },
      vuetify: Vue.prototype.vuetifyOptions,
      i18n,
    }, `#${appId}`, 'User Achievments Quick Action')));
}

function initLeaderboardDrawerApp(appId, exoi18n) {
  const lang = eXo.env.portal.language;
  const urls = [
    `/gamification-portlets/i18n/locale.addon.Gamification?lang=${lang}`,
    `/gamification-portlets/i18n/locale.portlet.Challenges?lang=${lang}`
  ];
  return new Promise(resolve => exoi18n.loadLanguageAsync(lang, urls)
    .then(i18n => Vue.createApp({
      template: `
        <div id="${appId}">
          <gamification-overview-leaderboard-drawer ref="drawer" :page-size="20" />
          <engagement-center-rule-extensions />
        </div>
      `,
      created() {
        document.addEventListener('quick-action-leaderboard-drawer', this.openDrawer);
      },
      mounted() {
        document.dispatchEvent(new CustomEvent('hideTopBarLoading'));
        resolve();
      },
      beforeDestroy() {
        document.removeEventListener('quick-action-leaderboard-drawer', this.openDrawer);
      },
      methods: {
        openDrawer() {
          this.$refs.drawer.open();
        },
      },
      vuetify: Vue.prototype.vuetifyOptions,
      i18n,
    }, `#${appId}`, 'Leaderboard Quick Action')));
}

function initActionListDrawerApp(appId, exoi18n) {
  const lang = eXo.env.portal.language;
  const urls = [
    `/gamification-portlets/i18n/locale.addon.Gamification?lang=${lang}`,
    `/gamification-portlets/i18n/locale.portlet.Challenges?lang=${lang}`
  ];
  return new Promise(resolve => exoi18n.loadLanguageAsync(lang, urls)
    .then(i18n => Vue.createApp({
      template: `
        <div id="${appId}">
          <gamification-rules-overview-list-drawer ref="drawer" />
          <engagement-center-rule-extensions />
        </div>
      `,
      created() {
        document.addEventListener('quick-action-actions-list-drawer', this.openDrawer);
      },
      mounted() {
        document.dispatchEvent(new CustomEvent('hideTopBarLoading'));
        resolve();
      },
      beforeDestroy() {
        document.removeEventListener('quick-action-actions-list-drawer', this.openDrawer);
      },
      methods: {
        openDrawer() {
          this.$refs.drawer.open();
        },
      },
      vuetify: Vue.prototype.vuetifyOptions,
      i18n,
    }, `#${appId}`, 'Actions List Quick Action')));
}

function initProgramListDrawerApp(appId, exoi18n) {
  const lang = eXo.env.portal.language;
  const urls = [
    `/gamification-portlets/i18n/locale.addon.Gamification?lang=${lang}`,
    `/gamification-portlets/i18n/locale.portlet.Challenges?lang=${lang}`
  ];
  return new Promise(resolve => exoi18n.loadLanguageAsync(lang, urls)
    .then(i18n => Vue.createApp({
      template: `
        <div id="${appId}">
          <gamification-program-list-drawer ref="drawer" />
          <engagement-center-program-detail-drawer :administrators="administrators" />
          <engagement-center-rule-extensions />
        </div>
      `,
      data: () => ({
        administrators: null,
      }),
      created() {
        document.addEventListener('quick-action-programs-list-drawer', this.openDrawer);
      },
      mounted() {
        document.dispatchEvent(new CustomEvent('hideTopBarLoading'));
        resolve();
      },
      beforeDestroy() {
        document.removeEventListener('quick-action-programs-list-drawer', this.openDrawer);
      },
      methods: {
        async openDrawer(event) {
          try {
            await this.refresh();
            this.$refs.drawer.open();
          } finally {
            const callback = event?.detail;
            if (callback) {
              callback();
            }
          }
        },
        refresh() {
          return this.$programService.getPrograms({
            limit: 1,
            lang: eXo.env.portal.language,
            expand: 'administrators'
          }).then((data) => this.administrators = data?.administrators || []);
        },

      },
      vuetify: Vue.prototype.vuetifyOptions,
      i18n,
    }, `#${appId}`, 'Programs List Quick Action')));
}

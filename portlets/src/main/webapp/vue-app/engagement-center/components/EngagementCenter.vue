<!--
This file is part of the Meeds project (https://meeds.io/).
Copyright (C) 2022 Meeds Association
contact@meeds.io
This program is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 3 of the License, or (at your option) any later version.
This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.
You should have received a copy of the GNU Lesser General Public License
along with this program; if not, write to the Free Software Foundation,
Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
-->
<template>
  <v-app>
    <main v-if="!initialized">
      <v-toolbar color="transparent" flat>
        <v-spacer />
        <v-progress-circular
          color="primary"
          class="mb-2"
          indeterminate />
        <v-spacer />
      </v-toolbar>
    </main>
    <main>
      <v-tabs
        id="engagementCenterTabs"
        v-model="tab"
        slider-size="4"
        class="mb-4">
        <v-tab class="px-5">{{ $t('engagementCenter.label.programs') }}</v-tab>
        <v-tab class="px-5">{{ $t('engagementCenter.label.challenges') }}</v-tab>
        <v-tab class="px-5">{{ $t('engagementCenter.label.achievements') }}</v-tab>
      </v-tabs>
      <v-tabs-items v-model="tab">
        <v-tab-item>
          <engagement-center-programs
            v-show="!displayProgramDetail"
            id="engagementCenterProgramsTab"
            :is-administrator="isAdministrator" />
          <engagement-center-program-detail
            v-if="displayProgramDetail"
            :program="program"
            :events="events"
            :is-administrator="isAdministrator"
            :action-value-extensions="actionValueExtensions" />
        </v-tab-item>
        <v-tab-item>
          <challenges :challenge-id="challengeId" :can-add-challenge="canAddChallenge" />
        </v-tab-item>
        <v-tab-item>
          <realizations
            v-show="!displayProgramDetail"
            id="Realizations"
            :earner-id="earnerId"
            :is-administrator="isAdministrator"
            :action-value-extensions="actionValueExtensions" />
          <engagement-center-program-detail
            v-if="displayProgramDetail"
            :program="program"
            :events="events"
            :is-administrator="isAdministrator"
            :action-value-extensions="actionValueExtensions"
            :tab="2" />
        </v-tab-item>
      </v-tabs-items>
    </main>
    <challenge-drawer
      ref="challengeDrawer"
      :is-administrator="isAdministrator" />
    <engagement-center-rule-detail-drawer
      ref="ruleDetails"
      :tab="tab"
      :action-value-extensions="actionValueExtensions" />
    <engagement-center-winners-details ref="winnersDetails" />
    <engagement-center-program-drawer
      ref="programDrawer"
      :is-administrator="isAdministrator" />
    <engagement-center-program-owners-drawer ref="ownersDetails" />
  </v-app>
</template>

<script>
export default {
  props: {
    isAdministrator: {
      type: Boolean,
      default: false,
    },
  },
  data: () => ({
    initialized: false,
    tab: null,
    earnerId: eXo.env.portal.userIdentityId,
    challengeId: null,
    program: null,
    canAddChallenge: false,
    displayProgramDetail: false,
    events: [],
    avoidAddToHistory: false,
    extensionApp: 'engagementCenterActions',
    actionValueExtensionType: 'user-actions',
    actionValueExtensions: {},
  }),
  watch: {
    tab() {
      if (!this.avoidAddToHistory) {
        if (this.tab === 0) {
          window.history.pushState('Engagement Center', this.$t('engagementCenter.label.programs'), `${eXo.env.portal.context}/${eXo.env.portal.portalName}/contributions/programs`);
        } else if (this.tab === 1) {
          window.history.pushState('Engagement Center', this.$t('engagementCenter.label.challenges'), `${eXo.env.portal.context}/${eXo.env.portal.portalName}/contributions/challenges`);
        } else if (this.tab === 2) {
          window.history.pushState('Engagement Center', this.$t('engagementCenter.label.achievements'), `${eXo.env.portal.context}/${eXo.env.portal.portalName}/contributions/achievements`);
        }
        this.displayProgramDetail = false;
      }
      this.avoidAddToHistory= false;
    },
  },
  created() {
    this.$root.$on('open-program-detail', program => {
      this.program = program;
      this.displayProgramDetail = true;
    });
    this.$root.$on('close-program-detail', () => this.displayProgramDetail = false);
    this.initTabs();
    this.$ruleServices.getEvents()
      .then(events => {
        this.events = events || [];
      });
    this.$challengesServices.canAddChallenge()
      .then(canAddChallenge => this.canAddChallenge = canAddChallenge);
    this.initialized = true;
    window.addEventListener('popstate', (event) => this.initTabs(event));
    document.addEventListener(`extension-${this.extensionApp}-${this.actionValueExtensionType}-updated`, this.refreshActionValueExtensions);
    this.refreshActionValueExtensions();
  },
  methods: {
    initTabs(event) {
      if (event) {
        this.avoidAddToHistory = true;
      }
      this.switchTabs();
    },
    switchTabs() {
      const urlPath = document.location.search || document.location.pathname;
      const id = urlPath.match( /\d+/ ) && urlPath.match( /\d+/ ).join('');
      if (urlPath.indexOf(`${eXo.env.portal.context}/${eXo.env.portal.portalName}/contributions/programs`) > -1) {
        if (id) {
          this.$programsServices.getProgramById(id)
            .then(program => {
              if (program && program.id) {
                this.$root.$emit('open-program-detail', program);
                window.history.replaceState('programs', this.$t('engagementCenter.label.programs'), `${eXo.env.portal.context}/${eXo.env.portal.portalName}/contributions/programs/${program.id}`);
              } 
            });
        }
        this.tab = 0;
      } else if (urlPath.indexOf(`${eXo.env.portal.context}/${eXo.env.portal.portalName}/contributions/challenges`) > -1 || urlPath.indexOf(`${eXo.env.portal.context}/${eXo.env.portal.portalName}/challenges`) > -1) {
        this.id = id;
        this.tab = 1;
      } else if  (urlPath.indexOf(`${eXo.env.portal.context}/${eXo.env.portal.portalName}/contributions/achievements`) > -1) {
        this.tab = 2;
      }
    },
    refreshActionValueExtensions() {
      const extensions = extensionRegistry.loadExtensions(this.extensionApp, this.actionValueExtensionType);
      let changed = false;
      extensions.forEach(extension => {
        if (extension.type && extension.options && (!this.actionValueExtensions[extension.type] || this.actionValueExtensions[extension.type] !== extension.options)) {
          this.actionValueExtensions[extension.type] = extension.options;
          changed = true;
        }
      });
      // force update of attribute to re-render switch new extension type
      if (changed) {
        this.actionValueExtensions = Object.assign({}, this.actionValueExtensions);
      }
    },
  }
};
</script>
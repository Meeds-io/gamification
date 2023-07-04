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
    <main>
      <v-tabs
        id="engagementCenterTabs"
        v-model="tab"
        slider-size="4"
        class="mb-4">
        <v-tab class="px-5" @click="resetSelection">{{ $t('engagementCenter.label.programs') }}</v-tab>
        <v-tab class="px-5" @click="resetSelection">{{ $t('engagementCenter.label.actions') }}</v-tab>
        <v-tab class="px-5" @click="resetSelection">{{ $t('engagementCenter.label.achievements') }}</v-tab>
      </v-tabs>
      <v-tabs-items v-model="tab">
        <v-tab-item>
          <engagement-center-programs
            v-show="!displayProgramDetail"
            id="engagementCenterProgramsTab"
            :is-administrator="isAdministrator"
            :is-program-manager="isProgramManager" />
          <engagement-center-program-detail
            v-if="displayProgramDetail"
            :program="program"
            :newly-created="newlyCreated"
            :is-administrator="isAdministrator" />
        </v-tab-item>
        <v-tab-item>
          <engagement-center-rules
            ref="rules"
            :is-administrator="isAdministrator" />
        </v-tab-item>
        <v-tab-item>
          <realizations
            v-show="!displayProgramDetail"
            id="Realizations"
            :earner-id="earnerId"
            :is-program-manager="isProgramManager" />
          <engagement-center-program-detail
            v-if="displayProgramDetail"
            :program="program"
            :newly-created="newlyCreated"
            :is-administrator="isAdministrator"
            :tab="2" />
        </v-tab-item>
      </v-tabs-items>
    </main>
    <engagement-center-rule-drawers />
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
    isProgramManager: {
      type: Boolean,
      default: false,
    },
  },
  data: () => ({
    tab: null,
    earnerId: eXo.env.portal.userIdentityId,
    program: null,
    displayProgramDetail: false,
    newlyCreated: false,
    avoidAddToHistory: false,
    extensionApp: 'engagementCenterActions',
    actionValueExtensionType: 'user-actions',
    linkBasePath: `${eXo.env.portal.context}/${eXo.env.portal.portalName}/contributions`,
  }),
  computed: {
    programsLinkBasePath() {
      return `${this.linkBasePath}/programs`;
    },
    rulesLinkBasePath() {
      return `${this.linkBasePath}/actions`;
    },
    achievementsLinkBasePath() {
      return `${this.linkBasePath}/achievements`;
    },
  },
  watch: {
    tab() {
      if (!this.avoidAddToHistory) {
        if (this.tab === 0 && window.location.pathname.indexOf(this.programsLinkBasePath) < 0) {
          window.history.pushState('Engagement Center', this.$t('engagementCenter.label.programs'), this.programsLinkBasePath);
        } else if (this.tab === 1 && window.location.pathname.indexOf(this.rulesLinkBasePath) < 0) {
          window.history.pushState('Engagement Center', this.$t('engagementCenter.label.actions'), this.rulesLinkBasePath);
        } else if (this.tab === 2 && window.location.pathname.indexOf(this.achievementsLinkBasePath) < 0) {
          window.history.pushState('Engagement Center', this.$t('engagementCenter.label.achievements'), this.achievementsLinkBasePath);
        }
        this.displayProgramDetail = false;
      }
      this.avoidAddToHistory= false;
    },
  },
  created() {
    this.$root.$on('program-added', this.openCreatedProgramDetail);
    this.$root.$on('rule-created', this.resetNewlyCreated);
    this.$root.$on('rule-updated', this.updateOpenedProgram);
    this.$root.$on('rule-deleted', this.updateOpenedProgram);
    this.$root.$on('open-program-detail', this.openProgramDetail);
    this.$root.$on('open-program-detail-by-id', this.openProgramDetailById);
    this.$root.$on('close-program-detail', () => this.displayProgramDetail = false);
    this.initTabs();
    window.addEventListener('popstate', (event) => this.initTabs(event));
  },
  methods: {
    initTabs(event) {
      if (event) {
        this.avoidAddToHistory = true;
      }
      this.switchTabs();
    },
    resetNewlyCreated() {
      this.newlyCreated = false;
      this.updateOpenedProgram();
    },
    updateOpenedProgram() {
      if (this.displayProgramDetail && this.program?.id) {
        this.openProgramDetailById(this.program.id);
      }
    },
    resetSelection() {
      this.displayProgramDetail = false;
      this.$nextTick().then(() => this.program = null);
      if (this.$refs.rules) {
        this.$refs.rules.reset();
      }
    },
    openProgramDetailById(id, newlyCreated) {
      this.$programService.getProgramById(id, {
        lang: eXo.env.portal.language,
        expand: 'countActiveRulesWhenDisabled',
      })
        .then(program => {
          if (program?.id) {
            this.openProgramDetail(program, newlyCreated);
          }
        });
    },
    openCreatedProgramDetail(program) {
      this.openProgramDetail(program, true);
    },
    openProgramDetail(program, newlyCreated) {
      this.newlyCreated = newlyCreated || false;
      this.program = program;
      this.displayProgramDetail = true;
      window.history.replaceState('programs', this.$t('engagementCenter.label.programs'), `${this.programsLinkBasePath}/${program.id}`);
    },
    switchTabs() {
      const urlPath = document.location.pathname;
      const id = urlPath.match( /\d+/ ) && urlPath.match( /\d+/ ).join('');
      if (urlPath.indexOf(`${eXo.env.portal.context}/${eXo.env.portal.portalName}/contributions/programs`) >= 0) {
        this.tab = 0;
        if (id) {
          this.openProgramDetailById(id);
        }
      } else if (urlPath.indexOf(`${eXo.env.portal.context}/${eXo.env.portal.portalName}/contributions/actions`) >= 0
              || urlPath.indexOf(`${eXo.env.portal.context}/${eXo.env.portal.portalName}/contibutions/challenges`) >= 0) {
        if (urlPath.indexOf(`${eXo.env.portal.context}/${eXo.env.portal.portalName}/contibutions/challenges`) >= 0) {
          window.history.replaceState('Engagement Center', this.$t('engagementCenter.label.actions'), urlPath.replace(`${eXo.env.portal.context}/${eXo.env.portal.portalName}/contibutions/challenges`, this.rulesLinkBasePath));
        }
        this.id = id;
        this.tab = 1;
      } else if  (urlPath.indexOf(`${eXo.env.portal.context}/${eXo.env.portal.portalName}/contributions/achievements`) >= 0) {
        this.tab = 2;
      }
    },
  }
};
</script>
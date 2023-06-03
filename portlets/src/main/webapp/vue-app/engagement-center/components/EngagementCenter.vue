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
            :is-administrator="isAdministrator" />
          <engagement-center-program-detail
            v-if="displayProgramDetail"
            :program="program"
            :newly-created="newlyCreated"
            :is-administrator="isAdministrator"
            :action-value-extensions="actionValueExtensions" />
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
            :action-value-extensions="actionValueExtensions" />
          <engagement-center-program-detail
            v-if="displayProgramDetail"
            :program="program"
            :newly-created="newlyCreated"
            :is-administrator="isAdministrator"
            :action-value-extensions="actionValueExtensions"
            :tab="2" />
        </v-tab-item>
      </v-tabs-items>
    </main>
    <engagement-center-rule-detail-drawer
      ref="ruleDetails"
      :tab="tab"
      :action-value-extensions="actionValueExtensions"
      :is-administrator="isAdministrator" />
    <engagement-center-rule-achievements-drawer
      ref="achievementsDrawer"
      :action-value-extensions="actionValueExtensions" />
    <engagement-center-program-drawer
      ref="programDrawer"
      :is-administrator="isAdministrator" />
    <engagement-center-program-owners-drawer ref="ownersDetails" />
    <engagement-center-rule-form-drawer ref="ruleFormDrawer" />
    <exo-confirm-dialog
      ref="deleteRuleConfirmDialog"
      :title="$t('programs.details.title.confirmDeleteRule')"
      :message="$t('actions.deleteConfirmMessage')"
      :ok-label="$t('programs.details.ok.button')"
      :cancel-label="$t('programs.details.cancel.button')"
      @ok="deleteRule" />
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
    tab: null,
    earnerId: eXo.env.portal.userIdentityId,
    program: null,
    selectedRule: null,
    displayProgramDetail: false,
    newlyCreated: false,
    avoidAddToHistory: false,
    extensionApp: 'engagementCenterActions',
    actionValueExtensionType: 'user-actions',
    actionValueExtensions: {},
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
    this.$root.$on('open-program-detail', this.openProgramDetail);
    this.$root.$on('open-program-detail-by-id', this.openProgramDetailById);
    this.$root.$on('close-program-detail', () => this.displayProgramDetail = false);
    this.$root.$on('rule-delete-confirm', this.confirmDelete);
    this.initTabs();
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
    resetNewlyCreated() {
      this.newlyCreated = false;
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
        lang: eXo.env.portal.language
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
          this.$programService.getProgramById(id, {
            lang: eXo.env.portal.language
          })
            .then(program => {
              if (program && program.id) {
                this.$root.$emit('open-program-detail', program);
              }
            });
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
    deleteRule() {
      this.loading = true;
      this.$ruleService.deleteRule(this.selectedRule.id)
        .then(() => {
          this.$root.$emit('alert-message', this.$t('programs.details.ruleDeleteSuccess'), 'success');
          this.$root.$emit('rule-deleted', this.selectedRule);
        })
        .catch(e => {
          let msg = '';
          if (e.message === '401' || e.message === '403') {
            msg = this.$t('actions.deletePermissionDenied');
          } else if (e.message  === '404') {
            msg = this.$t('actions.notFound');
          } else  {
            msg = this.$t('actions.deleteError');
          }
          this.$root.$emit('alert-message', msg, 'error');
        })
        .finally(() => this.loading = false);
    },
    confirmDelete(rule) {
      this.selectedRule = rule;
      this.$refs.deleteRuleConfirmDialog.open();
    },
  }
};
</script>
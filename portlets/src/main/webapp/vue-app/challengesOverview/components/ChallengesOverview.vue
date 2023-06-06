<!--
  This file is part of the Meeds project (https://meeds.io/).
  Copyright (C) 2022 Meeds Association contact@meeds.io
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
    <gamification-overview-widget
      v-if="!hasRules"
      :loading="loading">
      <template #title>
        {{ $t('gamification.overview.emptyChallengesOverviewTitle') }}
      </template>
      <template #content>
        <gamification-overview-widget-row v-show="!loading" class="my-auto">
          <template #icon>
            <v-icon color="secondary" size="55px">fas fa-rocket</v-icon>
          </template>
          <template #content>
            <span v-html="emptySummaryText"></span>
          </template>
        </gamification-overview-widget-row>
      </template>
    </gamification-overview-widget>
    <gamification-overview-widget
      v-else
      :see-all-url="actionsPageURL"
      :extra-class="'px-0 mt-1'">
      <template #title>
        {{ $t('gamification.overview.challengesOverviewTitle') }}
      </template>
      <template #content>
        <gamification-overview-widget-row
          class="py-auto"                   
          v-for="(item, index) in rules" 
          :key="index"
          :click-event-param="item"
          :is-challenge-id-provided="true">
          <template #icon>
            <v-icon
              color="yellow darken-2"
              size="30px"
              class="ps-4">
              fas fa-trophy
            </v-icon>
          </template>
          <template #content>
            <span>
              <v-list subheader two-line>
                <v-list-item two-line>
                  <v-list-item-content>
                    <v-list-item-title>
                      {{ item.title }}
                    </v-list-item-title>
                    <v-list-item-subtitle v-if="item.realizationsCount === 0">
                      {{ $t('gamification.overview.label.firstAnnounecement') }}
                    </v-list-item-subtitle>
                    <v-list-item-subtitle v-else-if="item.realizationsCount === 1">
                      1 {{ $t('gamification.overview.label.participant') }}
                    </v-list-item-subtitle>
                    <v-list-item-subtitle v-else>
                      {{ item.realizationsCount }} {{ $t('gamification.overview.label.participants') }}
                    </v-list-item-subtitle>
                  </v-list-item-content>
                  <v-list-item-action>
                    <v-list-item-action-text v-text="item.score + ' ' + $t('challenges.label.points') " class="mt-5" />
                  </v-list-item-action>
                </v-list-item>
              </v-list>
            </span>
          </template>
        </gamification-overview-widget-row>
      </template>
    </gamification-overview-widget>
    <engagement-center-rule-detail-drawer
      ref="challengeDetailsDrawer"
      :action-value-extensions="actionValueExtensions"
      :is-overview-displayed="true" />
    <engagement-center-rule-achievements-drawer
      ref="achievementsDrawer"
      :action-value-extensions="actionValueExtensions" />
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
  data: () => ({
    search: '',
    pageSize: 3,
    realizationsPerRule: 3,
    filter: 'STARTED',
    period: 'WEEK',
    loading: true,
    selectedRule: null,
    rules: [],
    orderByRealizations: true,
    extensionApp: 'engagementCenterActions',
    actionValueExtensionType: 'user-actions',
    actionValueExtensions: {},
    actionsPageURL: `${eXo.env.portal.context}/${eXo.env.portal.portalName}/contributions/actions`,
  }),
  computed: {
    emptySummaryText() {
      return this.$t('gamification.overview.challengesOverviewSummary', {
        0: `<a class="primary--text font-weight-bold" href="${this.actionsPageURL}">`,
        1: '</a>',
      });
    },
    hasRules() {
      return this.rules?.length;
    },
  },
  created() {
    document.addEventListener(`extension-${this.extensionApp}-${this.actionValueExtensionType}-updated`, this.refreshActionValueExtensions);
    this.refreshActionValueExtensions();

    document.addEventListener('widget-row-click-event', (event) => {
      if (event?.detail) {
        document.dispatchEvent(new CustomEvent('rule-detail-drawer', { detail: event.detail }));
      }
    });
    this.retrieveRules();
    this.$root.$on('announcement-added', this.retrieveRules);
    this.$root.$on('rule-updated', this.retrieveRules);
    this.$root.$on('rule-deleted', this.retrieveRules);
    this.$root.$on('rule-delete-confirm', this.confirmDelete);
  },
  methods: {
    retrieveRules() {
      this.loading = true;
      return this.$ruleService.getRules({
        term: this.search,
        status: 'ENABLED',
        programStatus: 'ENABLED',
        type: 'MANUAL',
        dateFilter: this.filter,
        offset: 0,
        limit: this.pageSize,
        realizationsLimit: this.realizationsPerRule,
        orderByRealizations: this.orderByRealizations,
        period: this.period,
        expand: 'countRealizations',
        lang: eXo.env.portal.language,
      })
        .then(result => {
          this.rules = result?.rules || [];
          this.rules = this.rules.sort((challenge1, challenge2) => challenge2.score - challenge1.score);
        }).finally(() => this.loading = false);
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
        this.$root.actionValueExtensions = Object.assign({}, this.actionValueExtensions);
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
  },
};
</script>
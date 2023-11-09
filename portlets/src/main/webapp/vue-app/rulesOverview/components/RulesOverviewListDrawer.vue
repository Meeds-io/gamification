<!--
  This file is part of the Meeds project (https://meeds.io/).

  Copyright (C) 2023 Meeds Association contact@meeds.io

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
  <exo-drawer
    id="rulesOverviewListDrawer"
    ref="drawer"
    v-model="drawer"
    :loading="loading"
    :right="!$vuetify.rtl">
    <template #title>
      {{ $t('gamification.overview.actionsList') }}
    </template>
    <template #titleIcons>
      <v-btn
        :href="actionsPageURL"
        icon>
        <v-icon size="24">fa-external-link-alt</v-icon>
      </v-btn>
    </template>
    <template #content>
      <gamification-overview-widget height="auto">
        <template #content>
          <template v-if="endingRulesCount">
            <div class="d-flex align-center">
              <span class="me-2 subtitle-1">{{ $t('gamification.overview.endingActionsTitle') }}</span>
              <v-divider />
            </div>
            <gamification-rules-overview-item
              v-for="rule in endingRulesToDisplay"
              :key="rule.id"
              :rule="rule"
              go-back-button
              dense />
          </template>
          <template v-if="activeRulesCount">
            <div
              v-if="!hasAvailableRulesOnly"
              :class="endingRulesCount && 'pt-5'"
              class="d-flex align-center">
              <span class="me-2 subtitle-1">{{ $t('gamification.overview.availableActionsTitle') }}</span>
              <v-divider />
            </div>
            <gamification-rules-overview-item
              v-for="rule in activeRulesToDisplay"
              :key="rule.id"
              :rule="rule"
              :dense="!hasAvailableRulesOnly"
              go-back-button />
          </template>
          <template v-if="upcomingRulesCount">
            <div class="d-flex align-center pt-5">
              <span class="me-2 subtitle-1">{{ $t('gamification.overview.upcomingActionsTitle') }}</span>
              <v-divider />
            </div>
            <gamification-rules-overview-item
              v-for="rule in upcomingRulesToDisplay"
              :key="rule.id"
              :rule="rule"
              go-back-button
              dense />
          </template>
        </template>
      </gamification-overview-widget>
    </template>
  </exo-drawer>
</template>
<script>
export default {
  data: () => ({
    pageSize: 10,
    drawer: false,
    loading: false,
    rules: [],
    activeRules: [],
    upcomingRules: [],
    endingRules: [],
    weekInMs: 604800000,
  }),
  computed: {
    endingRulesToDisplay() {
      return this.endingRules
        .filter(r => r.endDate && (new Date(r.endDate).getTime() - Date.now()) < this.weekInMs)
        .slice(0, 2);
    },
    endingRulesCount() {
      return this.endingRulesToDisplay.length;
    },
    upcomingRulesToDisplay() {
      return this.upcomingRules
        .filter(r => r.startDate && (new Date(r.startDate).getTime() - Date.now()) < this.weekInMs)
        .slice(0, 2);
    },
    upcomingRulesCount() {
      return this.upcomingRulesToDisplay.length;
    },
    actionsPageURL() {
      return eXo.env.portal.portalName === 'public' && '/portal/public/overview/actions' || `${eXo.env.portal.context}/${eXo.env.portal.engagementSiteName}/contributions/actions`;
    },
    activeRulesToDisplay() {
      return this.activeRules
        .filter(r => !this.endingRulesToDisplay.find(rule => rule.id === r.id))
        .slice(0, this.pageSize - (this.endingRulesCount && 2 || 0) - (this.upcomingRulesCount && 2 || 0));
    },
    activeRulesCount() {
      return this.activeRulesToDisplay.length;
    },
    validRulesCount() {
      return this.activeRulesCount + this.upcomingRulesCount + this.endingRulesCount;
    },
    hasValidRules() {
      return this.validRulesCount > 0;
    },
    hasAvailableRulesOnly() {
      return this.activeRulesCount && !this.endingRulesCount && !this.upcomingRulesCount;
    },
  },
  created() {
    this.$root.$on('rules-overview-list-drawer', this.open);
    this.$root.$on('announcement-added', this.refreshRules);
    this.$root.$on('rule-updated', this.refreshRules);
    this.$root.$on('rule-deleted', this.refreshRules);
  },
  beforeDestroy() {
    this.$root.$off('rules-overview-list-drawer', this.open);
    this.$root.$off('announcement-added', this.refreshRules);
    this.$root.$off('rule-updated', this.refreshRules);
    this.$root.$off('rule-deleted', this.refreshRules);
  },
  methods: {
    open() {
      this.endingRules = [];
      this.retrieveRules();
      this.$refs.drawer.open();
    },
    refreshRules() {
      if (!this.drawer) {
        return false;
      }
      this.retrieveRules();
    },
    retrieveRules() {
      this.loading = true;
      Promise.all([
        this.retrieveEndingRules(),
        this.retrieveActiveRules(),
        this.retrieveUpcomingRules(),
      ]).finally(() => this.loading = false);
    },
    retrieveEndingRules() {
      return this.$ruleService.getRules({
        status: 'ENABLED',
        programStatus: 'ENABLED',
        dateFilter: 'STARTED_WITH_END',
        offset: 0,
        limit: 2,
        sortBy: 'endDate',
        sortDescending: false,
        expand: 'countRealizations',
        lang: eXo.env.portal.language,
        returnSize: false,
      }).then(result => this.endingRules = result?.rules || []);
    },
    retrieveActiveRules() {
      return this.$ruleService.getRules({
        status: 'ENABLED',
        programStatus: 'ENABLED',
        dateFilter: 'STARTED',
        offset: 0,
        limit: this.pageSize,
        sortBy: 'createdDate',
        sortDescending: true,
        expand: 'countRealizations',
        lang: eXo.env.portal.language,
        returnSize: false,
      }).then(result => this.activeRules = result?.rules || []);
    },
    retrieveUpcomingRules() {
      return this.$ruleService.getRules({
        status: 'ENABLED',
        programStatus: 'ENABLED',
        dateFilter: 'UPCOMING',
        offset: 0,
        limit: 2,
        sortBy: 'startDate',
        sortDescending: false,
        expand: 'countRealizations',
        lang: eXo.env.portal.language,
        returnSize: false,
      }).then(result => this.upcomingRules = result?.rules || []);
    },
  },
};
</script>
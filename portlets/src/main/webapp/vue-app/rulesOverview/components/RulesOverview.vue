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
      extra-class="pa-0 justify-space-between">
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
            <v-card
              min-width="35"
              class="d-flex align-center justify-center ms-4"
              flat>
              <rule-icon :rule-event="item.event" size="30" />
            </v-card>
          </template>
          <template #content>
            <v-list-item>
              <v-list-item-content class="py-0 my-auto">
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
                <v-list-item-action-text>
                  <v-chip
                    color="#F57C00"
                    class="content-box-sizing white--text"
                    small>
                    <span class="subtitle-2">+ {{ item.score }}</span>
                  </v-chip>
                </v-list-item-action-text>
              </v-list-item-action>
            </v-list-item>
          </template>
        </gamification-overview-widget-row>
      </template>
    </gamification-overview-widget>
    <engagement-center-rule-drawers />
  </v-app>
</template>
<script>
export default {
  data: () => ({
    search: '',
    pageSize: 4,
    realizationsPerRule: 3,
    filter: 'STARTED',
    period: 'WEEK',
    loading: true,
    rules: [],
    orderByRealizations: true,
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
    document.addEventListener('widget-row-click-event', (event) => {
      if (event?.detail) {
        document.dispatchEvent(new CustomEvent('rule-detail-drawer', { detail: event.detail }));
      }
    });
    this.retrieveRules();
    this.$root.$on('announcement-added', this.retrieveRules);
    this.$root.$on('rule-updated', this.retrieveRules);
    this.$root.$on('rule-deleted', this.retrieveRules);
  },
  methods: {
    retrieveRules() {
      this.loading = true;
      return this.$ruleService.getRules({
        term: this.search,
        status: 'ENABLED',
        programStatus: 'ENABLED',
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
  },
};
</script>
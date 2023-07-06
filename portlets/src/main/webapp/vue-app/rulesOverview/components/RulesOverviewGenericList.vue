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
  <gamification-overview-widget
    v-if="hasRules"
    :see-all-url="actionsPageURL"
    extra-class="pa-0 justify-space-between height-auto">
    <template #title>
      {{ $t('gamification.overview.challengesOverviewTitle') }}
    </template>
    <template #content>
      <gamification-rules-overview-item
        class="py-auto"
        v-for="rule in rulesToDisplay"
        :key="rule.id"
        :rule="rule" />
    </template>
  </gamification-overview-widget>
  <gamification-overview-widget
    v-else
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
          <span v-sanitized-html="emptySummaryText"></span>
        </template>
      </gamification-overview-widget-row>
    </template>
  </gamification-overview-widget>
</template>
<script>
export default {
  props: {
    rules: {
      type: Array,
      default: () => [],
    },
    pageSize: {
      type: Number,
      default: null,
    },
    loading: {
      type: Boolean,
      default: false,
    },
  },
  data: () => ({
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
    rulesToDisplay() {
      return this.hasRules && this.rules.slice(0, this.pageSize) || [];
    },
  },
};
</script>
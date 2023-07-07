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
  <v-app v-if="!hidden">
    <gamification-rules-overview-widget
      v-if="spaceId"
      :rules="rules"
      :page-size="pageSize"
      :loading="loading"
      @hide="hidden = true">
      <template #title>
        {{ $t('gamification.overview.spaceRulesOverviewTitle') }}
      </template>
    </gamification-rules-overview-widget>
    <gamification-rules-overview-generic-list
      v-else
      :rules="rules"
      :page-size="pageSize"
      :loading="loading" />
    <engagement-center-rule-extensions />
  </v-app>
</template>
<script>
export default {
  data: () => ({
    pageSize: 4,
    spaceId: eXo.env.portal.spaceId,
    loading: true,
    hidden: false,
    rules: [],
  }),
  created() {
    this.$root.$on('announcement-added', this.retrieveRules);
    this.$root.$on('rule-updated', this.retrieveRules);
    this.$root.$on('rule-deleted', this.retrieveRules);
    this.retrieveRules();
  },
  beforeDestroy() {
    this.$root.$off('announcement-added', this.retrieveRules);
    this.$root.$off('rule-updated', this.retrieveRules);
    this.$root.$off('rule-deleted', this.retrieveRules);
  },
  methods: {
    retrieveRules() {
      this.loading = true;
      return this.$ruleService.getRules({
        status: 'ENABLED',
        programStatus: 'ENABLED',
        dateFilter: this.spaceId && 'ACTIVE' || 'STARTED',
        spaceId: this.spaceId?.length && [this.spaceId] || null,
        offset: 0,
        limit: this.spaceId && 100 || this.pageSize,
        orderByRealizations: !this.spaceId,
        expand: 'countRealizations',
        lang: eXo.env.portal.language,
        returnSize: true,
      })
        .then(result => {
          this.rules = result?.rules || [];
          this.rules = this.rules.sort((r1, r2) => r2.score - r1.score);
        }).finally(() => this.loading = false);
    },
  },
};
</script>

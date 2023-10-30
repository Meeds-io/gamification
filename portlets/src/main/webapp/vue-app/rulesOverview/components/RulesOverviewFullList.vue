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
  <gamification-rules-overview-widget
    :rules="rules"
    :rules-count="overallActiveRulesCount"
    :page-size="pageSize"
    :loading="loading"
    :see-all-url="actionsPath"
    @load-more="loadMoreActiveRules"
    @hide="$emit('hide')">
    <template #title>
      {{ $t('gamification.overview.suggestedRulesTitle') }}
    </template>
  </gamification-rules-overview-widget>
</template>
<script>
export default {
  data: () => ({
    pageSize: 4,
    overallActiveRulesLimit: 4,
    overallActiveRulesCount: 0,
    upcomingRules: [],
    activeRules: [],
    endingRules: [],
    loading: true,
    hidden: false,
    actionsPath: `${eXo.env.portal.context}/${eXo.env.portal.engagementSiteName}/contributions/actions#all`,
  }),
  computed: {
    rules() {
      const rules = [...this.upcomingRules, ...this.endingRules];
      this.activeRules.forEach(rule => {
        if (!rules.find(r => r.id === rule.id)) {
          rules.push(rule);
        }
      });
      return rules;
    },
  },
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
        limit: this.pageSize,
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
        limit: this.overallActiveRulesLimit,
        sortBy: 'createdDate',
        sortDescending: true,
        expand: 'countRealizations,expandPrerequisites',
        lang: eXo.env.portal.language,
        returnSize: true,
      }).then(result => {
        this.activeRules = result?.rules || [];
        this.overallActiveRulesCount = result?.size || 0;
      });
    },
    retrieveUpcomingRules() {
      return this.$ruleService.getRules({
        status: 'ENABLED',
        programStatus: 'ENABLED',
        dateFilter: 'UPCOMING',
        offset: 0,
        limit: this.pageSize,
        sortBy: 'startDate',
        sortDescending: false,
        expand: 'countRealizations',
        lang: eXo.env.portal.language,
        returnSize: false,
      }).then(result => this.upcomingRules = result?.rules || []);
    },
    loadMoreActiveRules() {
      if (this.loading) {
        return;
      }
      this.overallActiveRulesLimit = this.overallActiveRulesLimit * 2;
      this.loading = true;
      this.retrieveActiveRules()
        .finally(() => this.loading = false);
    },
  },
};
</script>

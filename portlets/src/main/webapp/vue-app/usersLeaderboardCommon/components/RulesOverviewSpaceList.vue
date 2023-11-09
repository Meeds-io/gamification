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
    :page-size="pageSize"
    :loading="loading"
    :title="$t('gamification.overview.spaceRulesOverviewTitle')"
    @hide="$emit('hide')" />
</template>
<script>
export default {
  data: () => ({
    pageSize: 4,
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
        dateFilter: 'ACTIVE',
        spaceId: [eXo.env.portal.spaceId],
        offset: 0,
        limit: 100,
        expand: 'countRealizations',
        lang: eXo.env.portal.language,
        returnSize: false,
      })
        .then(result => {
          this.rules = result?.rules || [];
          this.rules = this.rules.sort((r1, r2) => r2.score - r1.score);
        }).finally(() => this.loading = false);
    },
  },
};
</script>

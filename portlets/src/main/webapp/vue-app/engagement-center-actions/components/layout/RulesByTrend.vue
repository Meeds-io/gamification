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
  <v-card flat>
    <v-list
      v-for="category in categories"
      :key="category.id"
      class="pt-2 pb-6">
      <engagement-center-rules-category
        :category="category"
        disabled-collapsing>
        <v-card-title class="px-0 py-1 text-title text-break">
          {{ category.title }}
        </v-card-title>
      </engagement-center-rules-category>
    </v-list>
  </v-card>
</template>
<script>
export default {
  props: {
    rules: {
      type: Array,
      default: function() {
        return [];
      },
    },
    label: {
      type: Array,
      default: function() {
        return [];
      },
    },
  },
  data: () => ({
    endingSoonRules: [],
    newestRules: [],
    startingSoonRules: [],
    loading: 1,
    limit: 3,
    weekInMs: 604800000,
  }),
  computed: {
    endingSoonRulesToDisplay() {
      return this.endingSoonRules
        .filter(r => r.endDate && (new Date(r.endDate).getTime() - Date.now()) < this.weekInMs);
    },
    newestRulesToDisplay() {
      return this.newestRules
        .filter(r => !this.endingSoonRules?.find(e => e.id === r.id))
        .slice(0, this.limit * 3 - (this.startingSoonCategory && 3 || 0) - (this.endingSoonCategory && 3 || 0));
    },
    startingSoonRulesToDisplay() {
      return this.startingSoonRules
        .filter(r => r.startDate && (new Date(r.startDate).getTime() - Date.now()) < this.weekInMs);
    },
    rulesSize() {
      return (this.startingSoonRules?.length || 0) + (this.newestRules?.length || 0) + (this.endingSoonRules?.length || 0);
    },
    initialized() {
      return this.loading === 0;
    },
    endingSoonCategory() {
      return this.endingSoonRulesToDisplay?.length && {
        id: 'endingSoonRules',
        title: this.$t('gamification.actions.endingSoonIncentivesTitle'),
        rules: this.endingSoonRulesToDisplay,
        size: this.endingSoonRulesToDisplay.length,
        limit: this.endingSoonRulesToDisplay.length,
        offset: 0,
      };
    },
    newestCategory() {
      return this.newestRulesToDisplay?.length && {
        id: 'newestRules',
        title: this.$t('gamification.actions.newestIncentivesTitle'),
        rules: this.newestRulesToDisplay,
        size: this.newestRulesToDisplay.length,
        limit: this.newestRulesToDisplay.length,
        offset: 0,
      };
    },
    startingSoonCategory() {
      return this.startingSoonRulesToDisplay?.length && {
        id: 'startingSoonRules',
        title: this.$t('gamification.actions.startingSoonIncentivesTitle'),
        rules: this.startingSoonRulesToDisplay,
        size: this.startingSoonRulesToDisplay.length,
        limit: this.startingSoonRulesToDisplay.length,
        offset: 0,
      };
    },
    categories() {
      const categories = [];
      if (this.endingSoonCategory) {
        categories.push(this.endingSoonCategory);
      }
      if (this.newestCategory) {
        categories.push(this.newestCategory);
      }
      if (this.startingSoonCategory) {
        categories.push(this.startingSoonCategory);
      }
      return categories;
    },
  },
  watch: {
    initialized() {
      if (this.initialized) {
        this.$emit('initialized', this.rulesSize);
      }
    },
    loading(newVal, oldVal) {
      if (!newVal !== !oldVal) {
        this.$emit('loading', !!this.loading);
      }
    },
  },
  created() {
    this.init()
      .finally(() => this.loading--);
    this.$root.$on('rule-updated', this.init);
    this.$root.$on('rule-deleted', this.init);
  },
  beforeDestroy() {
    this.$root.$off('rule-updated', this.init);
    this.$root.$off('rule-deleted', this.init);
  },
  methods: {
    init() {
      return Promise.all([
        this.retrieveEndingSoonRules(),
        this.retrieveNewestRules(),
        this.retrieveStartingSoonRules()
      ]).finally(() => this.$root.$applicationLoaded());
    },
    retrieveEndingSoonRules() {
      this.loading++;
      return this.$ruleService.getRules({
        dateFilter: 'STARTED_WITH_END',
        status: 'ENABLED',
        programStatus: 'ENABLED',
        offset: 0,
        limit: this.limit,
        sortBy: 'endDate',
        sortDescending: false,
        expand: 'countRealizations,favorites',
        returnSize: false,
        lang: eXo.env.portal.language,
      })
        .then(data => this.endingSoonRules = data.rules || [])
        .finally(() => this.loading--);
    },
    retrieveNewestRules() {
      this.loading++;
      return this.$ruleService.getRules({
        dateFilter: 'STARTED',
        status: 'ENABLED',
        programStatus: 'ENABLED',
        offset: 0,
        limit: this.limit * 3,
        sortBy: 'createdDate',
        sortDescending: true,
        expand: 'countRealizations,favorites',
        returnSize: false,
        lang: eXo.env.portal.language,
      })
        .then(data => this.newestRules = data.rules || [])
        .finally(() => this.loading--);
    },
    retrieveStartingSoonRules() {
      this.loading++;
      return this.$ruleService.getRules({
        dateFilter: 'UPCOMING',
        status: 'ENABLED',
        programStatus: 'ENABLED',
        offset: 0,
        limit: this.limit,
        sortBy: 'startDate',
        sortDescending: true,
        expand: 'countRealizations,favorites',
        returnSize: false,
        lang: eXo.env.portal.language,
      })
        .then(data => this.startingSoonRules = data.rules || [])
        .finally(() => this.loading--);
    },
  },
};
</script>
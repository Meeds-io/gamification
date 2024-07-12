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
  <div>
    <engagement-center-rules-category
      v-for="category in validCategories"
      :key="category.id"
      :category="category"
      :program="category" />
  </div>
</template>
<script>
export default {
  props: {
    term: {
      type: String,
      default: null,
    },
    type: {
      type: String,
      default: () => 'ALL',
    },
    status: {
      type: String,
      default: () => 'STARTED',
    },
    isAdministrator: {
      type: Boolean,
      default: false,
    },
  },
  data: () => ({
    loading: true,
    applyFilters: false,
    categories: [],
    pageSize: 6,
  }),
  computed: {
    validCategories() {
      return this.categories.filter(category => category.rules.length > 0);
    },
    rulesSize() {
      return this.categories.map(cat => cat.size || 0).reduce((sum, v) => sum += v, 0) || 0;
    },
    categoriesById() {
      const categoriesById = {};
      this.categories.forEach(category => {
        categoriesById[category.id] = category;
      });
      return categoriesById;
    },
  },
  watch: {
    term() {
      this.applyFilters = true;
    },
    type() {
      this.applyFilters = true;
    },
    status() {
      this.applyFilters = true;
    },
    applyFilters() {
      if (this.applyFilters) {
        this.retrieveRules()
          .finally(() => this.applyFilters = false);
      }
    },
    rulesSize() {
      this.$emit('initialized', this.rulesSize);
    },
    loading() {
      this.$emit('loading', this.loading);
    },
  },
  created() {
    this.$root.$on('rule-created', this.retrieveCategoryOfRule);
    this.$root.$on('rule-updated', this.retrieveCategoryOfRule);
    this.$root.$on('rule-deleted', this.refreshRules);
    this.$root.$on('rules-category-load-more', this.loadMore);
    this.refreshRules();
  },
  beforeDestroy() {
    this.$root.$off('rule-created', this.retrieveCategoryOfRule);
    this.$root.$off('rule-updated', this.retrieveCategoryOfRule);
    this.$root.$off('rule-deleted', this.refreshRules);
    this.$root.$off('rules-category-load-more', this.loadMore);
  },
  methods: {
    refreshRules() {
      return this.retrieveRules();
    },
    retrieveCategoryOfRule(rule) {
      const categoryId = rule?.program?.id;
      if (categoryId) {
        return this.retrieveRules(categoryId);
      }
    },
    loadMore(categoryId) {
      if (categoryId && this.categoriesById[categoryId]) {
        this.categoriesById[categoryId].limit += this.pageSize;
        return this.retrieveRules(categoryId);
      }
    },
    retrieveRules(categoryId) { // NOSONAR
      const limit = this.pageSize + (categoryId && this.categoriesById[categoryId]?.limit || 0);
      const status = this.isAdministrator ? (this.status === 'DISABLED' && 'DISABLED') || (this.status === 'ALL' && 'ALL') || 'ENABLED'
        : 'ENABLED';
      this.loading = true;
      return this.$ruleService.getRules({
        programId: categoryId,
        term: this.term,
        dateFilter: this.status === 'DISABLED' && 'ALL' || this.status,
        status,
        programStatus: null,
        type: this.type,
        returnSize: true,
        groupByProgram: true,
        sortBy: 'title',
        limit,
        expand: 'countRealizations,favorites',
        lang: eXo.env.portal.language,
      })
        .then(data => {
          if (categoryId) {
            const category = this.categoriesById[categoryId] || {};
            if (category) {
              category.size = data.size || 0;
              category.limit = data.limit;
              category.rules = data.rules || [];
            }
            this.rulesSize = this.categories.map(cat => cat.size || 0).reduce((sum, v) => sum += v, 0) || 0;
          } else {
            this.categories = data;
            this.rulesSize = this.categories.map(cat => cat.size || 0).reduce((sum, v) => sum += v, 0);
          }
        }).finally(() => this.loading = false);
    },
  }
};
</script>
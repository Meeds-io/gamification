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
  <div
    id="rulesList"
    :class="classWelcomeMessage"
    class="rules-list border-box-sizing"
    role="main">
    <v-toolbar flat>
      <v-switch
        v-model="displayManual"
        :label="$t('rules.filter.showChallenges')"
        class="mb-n2 hidden-xs-only" />
      <v-spacer />
      <div class="rules-filter-toolbar text-center d-flex align-center justify-space-around">
        <v-text-field
          id="rulesFilterInput"
          v-model="search"
          :placeholder="$t('challenges.filter.search')"
          prepend-inner-icon="fa-filter"
          single-line
          hide-details
          class="rules-filter-input pa-0 mx-3" />
        <v-tooltip
          id="RulesSearchTooltip"
          :value="displayMinimumCharactersToolTip">
          <span> {{ $t('challenges.filter.searchTooltip') }} </span>
        </v-tooltip>
      </div>
      <div class="pt-1">
        <select
          id="rulesStatusFilterSelect"
          v-model="filter"
          class="rules-quick-filter-select my-auto ignore-vuetify-classes text-truncate mb-3 full-width"
          @change="retrieveRules()">
          <option
            v-for="item in ruleFilters"
            :key="item.value"
            :value="item.value">
            <span class="d-none d-lg-inline">
              {{ item.text }}
            </span>
          </option>
        </select>
      </div>
    </v-toolbar>
    <v-card flat>
      <engagement-center-result-not-found
        v-if="displayWelcomeMessage"
        :display-back-arrow="false"
        :message-title="$t('actions.welcomeMessage')"
        :message-info-one="$t('actions.welcomeMessageForRegularUser')" />
      <engagement-center-result-not-found
        v-else-if="displayNoSearchResult"
        :display-back-arrow="false"
        :message-title="welcomeMessage"
        :message-info-one="notFoundInfoMessage" />
      <engagement-center-rules-categories
        v-else-if="displayList"
        :categories="validCategories"
        class="pt-4" />
    </v-card>
    <exo-confirm-dialog
      ref="deleteRuleConfirmDialog"
      :title="$t('programs.details.title.confirmDeleteRule')"
      :message="$t('actions.deleteConfirmMessage')"
      :ok-label="$t('programs.details.ok.button')"
      :cancel-label="$t('programs.details.cancel.button')"
      @ok="deleteRule" />
  </div>
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
    selectedRuleId: null,
    selectedRule: null,
    loading: false,
    categories: [],
    search: '',
    startSearchAfterInMilliseconds: 600,
    endTypingKeywordTimeout: 50,
    startTypingKeywordTimeout: 0,
    typing: false,
    displayManual: false,
    displayMinimumCharactersToolTip: false,
    filter: 'STARTED',
  }),
  computed: {
    actionType() {
      return this.displayManual && 'MANUAL' || 'ALL';
    },
    classWelcomeMessage() {
      return this.displayWelcomeMessage && 'empty-rules-message' || '';
    },
    validCategories() {
      return this.categories.filter(category => category.rules.length > 0);
    },
    displayWelcomeMessage() {
      return !this.typing && !this.loading && !this.validCategories.length && !this.search?.length && (this.filter === 'ALL' || this.filter === 'STARTED');
    },
    notFoundInfoMessage() {
      if (this.filter === 'NOT_STARTED' && !this.search?.length) {
        return this.$t('actions.filter.upcomingNoResultsMessage');
      } else if (this.filter === 'ENDED' && !this.search?.length) {
        return this.$t('actions.filter.endedNoResultsMessage');
      } else {
        return this.$t('actions.search.noResultsMessage');
      }
    },
    welcomeMessage() {
      if (this.filter === 'NOT_STARTED' && this.filter === 'ENDED' && !this.search?.length) {
        return this.$t('challenges.welcomeMessage');
      } 
      return '';
    },
    displayNoSearchResult() {
      return !this.typing && !this.loading && !this.validCategories.length && (this.search?.length || (this.filter === 'NOT_STARTED' || this.filter === 'ENDED'));
    },
    displayList() {
      return this.validCategories.length;
    },
    rulesByCategoryId() {
      const rulesByCategoryId = {};
      this.validCategories.forEach(category => {
        rulesByCategoryId[category.id] = category.rules;
      });
      return rulesByCategoryId;
    },
    categoriesById() {
      const categoriesById = {};
      this.categories.forEach(category => {
        categoriesById[category.id] = category;
      });
      return categoriesById;
    },
    pageSize() {
      if (this.$vuetify.breakpoint.width <= 768) {
        return 5;
      } else {
        return 6;
      }
    },
    ruleFilters() {
      return this.isAdministrator && this.adminRuleFilters || this.userRuleFilters;
    },
    adminRuleFilters() {
      return [{
        text: this.$t('rules.filter.all'),
        value: 'ALL',
      },{
        text: this.$t('rules.filter.active'),
        value: 'STARTED',
      },{
        text: this.$t('rules.filter.upcoming'),
        value: 'NOT_STARTED',
      },{
        text: this.$t('rules.filter.ended'),
        value: 'ENDED',
      },{
        text: this.$t('programs.details.filter.disabled'),
        value: 'DISABLED',
      }];
    },
    userRuleFilters() {
      return [{
        text: this.$t('rules.filter.all'),
        value: 'ALL',
      },{
        text: this.$t('rules.filter.active'),
        value: 'STARTED',
      },{
        text: this.$t('rules.filter.upcoming'),
        value: 'NOT_STARTED',
      },{
        text: this.$t('rules.filter.ended'),
        value: 'ENDED',
      }];
    },
  },
  watch: {
    search()  {
      this.displayMinimumCharactersToolTip = false;
      if (!this.search?.length) {
        this.retrieveRules();
        return;
      } else if (this.search.length < 3) {
        this.displayMinimumCharactersToolTip = true;
        return;
      }
      this.startTypingKeywordTimeout = Date.now() + this.startSearchAfterInMilliseconds;
      if (!this.typing) {
        this.typing = true;
        this.waitForEndTyping();
      }
    },
    displayManual() {
      this.retrieveRules();
    },
    loading() {
      if (this.loading) {
        document.dispatchEvent(new CustomEvent('displayTopBarLoading'));
      } else {
        document.dispatchEvent(new CustomEvent('hideTopBarLoading'));
      }
    },
  },
  created() {
    this.$root.$on('rule-created', this.retrieveCategoryOfRule);
    this.$root.$on('rule-deleted', this.refreshRules);
    this.$root.$on('rule-delete-confirm', this.confirmDelete);
    this.$root.$on('rules-category-load-more', this.loadMore);

    this.refreshRules()
      .finally(() => this.$root.$applicationLoaded());
  },
  mounted() {
    this.selectedRuleId = this.extractRuleIdFromPath();
    if (this.selectedRuleId) {
      this.$root.$emit('rule-detail-drawer-by-id', this.selectedRuleId);
    }
  },
  beforeDestroy() {
    this.$root.$off('rule-created', this.retrieveCategoryOfRule);
    this.$root.$off('rule-deleted', this.refreshRules);
    this.$root.$off('rule-delete-confirm', this.confirmDelete);
    this.$root.$off('rules-category-load-more', this.loadMore);
  },
  methods: {
    retrieveCategoryOfRule(rule) {
      if (rule?.program?.id) {
        return this.retrieveRules(rule?.program?.id);
      } else {
        return Promise.resolve(null);
      }
    },
    refreshRules() {
      return this.retrieveRules();
    },
    retrieveRules(categoryId) {
      const status = (this.filter === 'DISABLED' && 'DISABLED')
        || (this.isAdministrator && this.filter === 'ALL' && 'ALL')
        || 'ENABLED';
      const dateFilter = this.filter === 'DISABLED' && 'ALL' || this.filter;
      const limit = (this.categoriesById[categoryId]?.limit || 0) + this.pageSize;

      this.loading = true;
      return this.$ruleService.getRules({
        term: this.search,
        domainId: categoryId,
        dateFilter,
        status,
        type: this.actionType,
        offset: 0,
        limit: limit,
        groupByDomain: !categoryId,
        expand: 'countAnnouncements',
        returnSize: true,
      })
        .then(data => {
          if (categoryId) {
            const category = this.categoriesById[categoryId] || {};
            if (category) {
              category.size = data.size || 0;
              category.limit = data.limit || 0;
              category.rules = data.rules || [];
            }
          } else {
            this.categories = data;
          }
        }).finally(() => this.loading = false);
    },
    deleteRule() {
      this.loading = true;
      this.$ruleService.deleteRule(this.selectedRule.id)
        .then(() => {
          this.showAlert('success', this.$t('programs.details.ruleDeleteSuccess'));
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
          this.showAlert('error', msg);
        })
        .finally(() => this.loading = false);
    },
    confirmDelete(rule) {
      this.selectedRule = rule;
      this.$refs.deleteRuleConfirmDialog.open();
    },
    loadMore(categoryId) {
      return this.retrieveRules(categoryId);
    },
    showAlert(alertType, alertMessage){
      this.$engagementCenterUtils.displayAlert(alertMessage, alertType);
    },
    addProgramTitle(program) {
      const key = `exoplatform.gamification.gamificationinformation.domain.${program.title}`;
      if (this.$te(key)) {
        program.title = this.$t(key);
      }
    },
    waitForEndTyping() {
      window.setTimeout(() => {
        if (Date.now() > this.startTypingKeywordTimeout) {
          this.typing = false;
          this.retrieveRules();
        } else {
          this.waitForEndTyping();
        }
      }, this.endTypingKeywordTimeout);
    },
    extractRuleIdFromPath() {
      const urlPath = document.location.pathname;
      if (urlPath.indexOf(`${eXo.env.portal.context}/${eXo.env.portal.portalName}/contributions/actions`) > -1) {
        return urlPath.match( /\d+/ ) && urlPath.match( /\d+/ ).join('');
      }
      return null;
    },
  }
};
</script>
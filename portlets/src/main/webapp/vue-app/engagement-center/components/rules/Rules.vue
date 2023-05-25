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
      <div class="d-flex flex-grow-1 align-center content-box-sizing position-relative">
        <v-card
          v-if="!showFilter || !$root.isMobile"
          flat>
          {{ incentivesCountLabel }}
        </v-card>
        <v-card
          v-else
          flat>
          <v-btn
            class="px-0 me-2"
            small
            icon
            @click="showFilter = !showFilter">
            <v-icon size="26">fa-arrow-left</v-icon>
          </v-btn>
        </v-card>
        <v-spacer class="hidden-md-and-down" />
        <engagement-center-category-switch-buttons
          v-model="groupByProgram"
          :disabled="!!filtered"
          class="hidden-md-and-down absolute-all-center"
          @change="clearFilter" />
        <v-spacer class="hidden-xs-only" />
        <div v-if="!$root.isMobile || showFilter" class="rules-filter-toolbar me-sm-4">
          <v-tooltip :value="displayMinimumCharactersToolTip" bottom>
            <template #activator="{on}">
              <v-text-field
                id="rulesFilterInput"
                ref="rulesFilterInput"
                v-model="term"
                :placeholder="$t('challenges.filter.search')"
                :autofocus="$root.isMobile"
                :height="$root.isMobile && 24 || 36"
                :clear-icon="$root.isMobile && 'fa-times py-0 mt-n2' || 'fa-times pt-2'"
                autocomplete="off"
                prepend-inner-icon="fa-filter"
                class="selected-period-input pa-0 full-width full-height"
                hide-details
                clearable
                v-on="on" />
            </template>
            <span> {{ $t('challenges.filter.searchTooltip') }} </span>
          </v-tooltip>
        </div>
        <v-btn
          v-if="!$root.isMobile || showFilter"
          :class="!$root.isMobile && 'primary-border-color'"
          :color="!$root.isMobile && 'primary'"
          :small="$root.isMobile"
          text
          class="px-0 px-sm-2"
          @click="openRulesFilterDrawer">
          <v-icon :size="$root.isMobile && 24 || 16">fas fa-sliders-h</v-icon>
          <span v-if="!$root.isMobile" class="font-weight-regular caption ms-2">
            {{ $t('profile.label.search.openSearch') }}
          </span>
        </v-btn>
        <template v-if="$root.isMobile && !showFilter">
          <v-spacer />
          <v-btn
            icon
            @click="showFilter = !showFilter">
            <v-icon size="24">fa-filter</v-icon>
          </v-btn>
        </template>
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
      <engagement-center-rules-list
        v-else-if="filtered"
        :loading="loading"
        :category-id="filterCategoryId"
        :rules="rules"
        :size="rulesSize"
        class="pt-4" />
      <engagement-center-rules-by-program
        v-else-if="groupByProgram"
        :categories="validCategories"
        class="pt-4" />
      <engagement-center-rules-by-trend
        v-else
        class="pt-4"
        @initialized="setInitialized" />
    </v-card>
    <engagement-center-rules-filter-drawer
      ref="rulesFilterDrawer"
      :is-administrator="isAdministrator"
      @apply="applyFilter" />
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
    loading: true,
    categories: [],
    hasTrends: false,
    groupByProgram: false,
    filterCategoryId: 'filtered_category_id',
    rules: [],
    rulesSize: 0,
    rulesOffset: 0,
    rulesLimit: 0,
    pageSize: 9,
    term: '',
    startSearchAfterInMilliseconds: 600,
    endTypingKeywordTimeout: 50,
    startTypingKeywordTimeout: 0,
    showFilter: false,
    typing: false,
    displayMinimumCharactersToolTip: false,
    type: 'ALL',
    status: 'STARTED',
  }),
  computed: {
    incentivesCountLabel() {
      if (!this.rulesSize || this.loading || this.typing) {
        return '';
      }
      if (this.term) {
        switch (this.status) {
        case 'ALL': return this.$t('rules.filter.incentivesFound', {0: this.rulesSize});
        case 'STARTED': return this.$t('rules.filter.activeIncentivesFound', {0: this.rulesSize});
        case 'NOT_STARTED': return this.$t('rules.filter.upcomingIncentivesFound', {0: this.rulesSize});
        case 'ENDED': return this.$t('rules.filter.endedIncentivesFound', {0: this.rulesSize});
        case 'DISABLED': return this.$t('rules.filter.disabledIncentivesFound', {0: this.rulesSize});
        default: return '';
        }
      } else {
        switch (this.status) {
        case 'ALL': return this.$t('rules.filter.incentives', {0: this.rulesSize});
        case 'STARTED': return this.$t('rules.filter.activeIncentives', {0: this.rulesSize});
        case 'NOT_STARTED': return this.$t('rules.filter.upcomingIncentives', {0: this.rulesSize});
        case 'ENDED': return this.$t('rules.filter.endedIncentives', {0: this.rulesSize});
        case 'DISABLED': return this.$t('rules.filter.disabledIncentives', {0: this.rulesSize});
        default: return '';
        }
      }
    },
    classWelcomeMessage() {
      return this.displayWelcomeMessage && 'empty-rules-message' || '';
    },
    validCategories() {
      return this.categories.filter(category => category.rules.length > 0);
    },
    filtered() {
      return !!this.term?.length || !!this.filterApplied;
    },
    filterApplied() {
      return this.status !== 'STARTED' || this.type !== 'ALL';
    },
    hasRulesToDisplay() {
      return this.filtered ? this.rules?.length : this.hasTrends;
    },
    hasItemsToDisplay() {
      const groupByProgram = this.groupByProgram && !this.term;
      return groupByProgram ? this.validCategories.length : this.hasRulesToDisplay;
    },
    displayWelcomeMessage() {
      return !this.typing && !this.loading && !this.hasItemsToDisplay && !this.term?.length && (this.status === 'ALL' || this.status === 'STARTED');
    },
    notFoundInfoMessage() {
      if (this.status === 'NOT_STARTED' && !this.term?.length) {
        return this.$t('actions.filter.upcomingNoResultsMessage');
      } else if (this.status === 'ENDED' && !this.term?.length) {
        return this.$t('actions.filter.endedNoResultsMessage');
      } else {
        return this.$t('actions.search.noResultsMessage');
      }
    },
    welcomeMessage() {
      if (this.status === 'NOT_STARTED' && this.status === 'ENDED' && !this.term?.length) {
        return this.$t('actions.welcomeMessage');
      } 
      return '';
    },
    displayNoSearchResult() {
      return !this.typing && !this.loading && !this.hasItemsToDisplay && (this.term?.length || (this.status === 'NOT_STARTED' || this.status === 'ENDED'));
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
  },
  watch: {
    groupByProgram()  {
      if (this.groupByProgram || this.filtered) {
        this.refreshRules();
      }
    },
    filtered: {
      immediate: true,
      handler(newVal, oldVal) {
        if (!newVal !== !oldVal) {
          this.rulesLimit = 0;
          this.rulesSize = 0;
        }
        if (!this.filtered) {
          this.retrieveRulesCount();
        }
      },
    },
    term()  {
      this.displayMinimumCharactersToolTip = false;
      if (!this.term?.length) {
        this.retrieveRules();
        return;
      } else if (this.term.length < 3) {
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
    this.$root.$on('rule-updated', this.onRuleUpdate);
    this.$root.$on('rule-deleted', this.refreshRules);
    this.$root.$on('rule-delete-confirm', this.confirmDelete);
    this.$root.$on('rules-category-load-more', this.loadMore);
    this.reset();
  },
  mounted() {
    this.selectedRuleId = this.extractRuleIdFromPath();
    if (this.selectedRuleId) {
      this.$root.$emit('rule-detail-drawer-by-id', this.selectedRuleId);
    }
  },
  beforeDestroy() {
    this.$root.$off('rule-created', this.retrieveCategoryOfRule);
    this.$root.$off('rule-updated', this.onRuleUpdate);
    this.$root.$off('rule-deleted', this.refreshRules);
    this.$root.$off('rule-delete-confirm', this.confirmDelete);
    this.$root.$off('rules-category-load-more', this.loadMore);
  },
  methods: {
    reset() {
      this.type = 'ALL';
      this.status = 'STARTED';
    },
    setInitialized(hasTrends) {
      this.hasTrends = hasTrends;
      this.loading = false;
    },
    retrieveRulesCount() {
      return this.$ruleService.getRules({
        dateFilter: 'ALL',
        status: 'ENABLED',
        offset: 0,
        limit: 1,
        returnSize: true,
      }).then(data => this.rulesSize = data?.size || 0);
    },
    retrieveRules(categoryId) { // NOSONAR
      const groupByProgram = this.groupByProgram && !this.filtered;
      const limit = (categoryId || groupByProgram) ?
        (categoryId && this.categoriesById[categoryId]?.limit || 0) + this.pageSize
        : this.rulesLimit + this.pageSize;

      this.loading = true;
      return this.$ruleService.getRules({
        term: this.term,
        programId: categoryId,
        dateFilter: this.status === 'DISABLED' && 'ALL' || this.status,
        status: this.status === 'DISABLED' && 'DISABLED' || 'ENABLED',
        type: this.type,
        offset: 0,
        limit,
        groupByProgram,
        expand: 'countAnnouncements',
        returnSize: true,
      })
        .then(data => {
          if (categoryId) {
            const category = this.categoriesById[categoryId] || {};
            if (category) {
              category.size = data.size || 0;
              category.limit = data.limit;
              category.rules = data.rules || [];
            }
          } else if (groupByProgram) {
            this.categories = data;
          } else {
            this.rules = data.rules || [];
            this.rulesSize = data.size;
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
    applyFilter(type, status) {
      this.type = type;
      this.status = status;
      this.retrieveRules();
    },
    openRulesFilterDrawer() {
      this.$refs.rulesFilterDrawer.open();
    },
    retrieveCategoryOfRule(rule) {
      if (this.groupbyProgram && rule?.program?.id) {
        return this.retrieveRules(rule?.program?.id);
      } else {
        return Promise.resolve(null);
      }
    },
    onRuleUpdate(rule) {
      if (this.filtered && this.rules?.length) {
        if (this.rules.findIndex(r => r.id === rule.id) >= 0) {
          this.rules = this.rules.map(r => r.id === rule.id && rule || r);
        }
      }
    },
    refreshRules() {
      return this.retrieveRules();
    },
    loadMore(categoryId) {
      if (categoryId === this.filterCategoryId) {
        this.rulesLimit += this.pageSize;
        return this.retrieveRules();
      } else if (categoryId) {
        return this.retrieveRules(categoryId);
      }
    },
    showAlert(alertType, alertMessage){
      this.$engagementCenterUtils.displayAlert(alertMessage, alertType);
    },
    clearFilter()  {
      this.term = '';
      this.reset();
      this.$refs.rulesFilterDrawer.reset();
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
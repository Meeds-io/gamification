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
    id="RulesList"
    :class="classWelcomeMessage"
    class="rules-list border-box-sizing"
    role="main">
    <v-toolbar flat>
      <v-spacer />
      <div class="text-center d-flex align-center justify-space-around">
        <v-text-field
          id="RulesFilterInput"
          v-model="search"
          :placeholder="$t('challenges.filter.search')"
          prepend-inner-icon="fa-filter"
          single-line
          hide-details
          class="rules-filter-input pa-0 mx-3" />
        <v-tooltip
          id="EngagementCenterApplicationSearchFilterTooltip"
          :value="displayMinimumCharactersToolTip">
          <span> {{ $t('challenges.filter.searchTooltip') }} </span>
        </v-tooltip>
      </div>
      <div class="pt-1">
        <select
          id="rulesQuickFilterSelect"
          v-model="filter"
          class="rules-quick-filter-select my-auto ignore-vuetify-classes text-truncate mb-3"
          @change="getRules">
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
        :message-title="$t('challenges.welcomeMessage')"
        :message-info-one="$t('challenges.welcomeMessageForRegularUser')" />
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
      :title="$t('challenges.delete')"
      :message="$t('challenges.deleteConfirmMessage')"
      :ok-label="$t('challenges.ok')"
      :cancel-label="$t('engagementCenter.button.cancel')"
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
    displayMinimumCharactersToolTip: false,
    filter: 'STARTED',
  }),
  computed: {
    classWelcomeMessage() {
      return this.displayWelcomeMessage && 'emptyChallenges' || '';
    },
    validCategories() {
      return this.categories.filter(category => category.rules.length > 0);
    },
    displayWelcomeMessage() {
      return !this.typing && !this.loading && !this.validCategories.length && !this.search?.length && (this.filter === 'ALL' || this.filter === 'STARTED');
    },
    notFoundInfoMessage() {
      if (this.filter === 'NOT_STARTED' && !this.search?.length) {
        return this.$t('challenges.filter.upcomingNoResultsMessage');
      } else if (this.filter === 'ENDED' && !this.search?.length) {
        return this.$t('challenges.filter.endedNoResultsMessage');
      } else {
        return this.$t('challenges.search.noResultsMessage');
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
        text: this.$t('challenges.filter.allChallenges'),
        value: 'ALL',
      },{
        text: this.$t('challenges.filter.activeChallenges'),
        value: 'STARTED',
      },{
        text: this.$t('challenges.filter.UpcomingChallenges'),
        value: 'NOT_STARTED',
      },{
        text: this.$t('challenges.filter.endedChallenges'),
        value: 'ENDED',
      },{
        text: this.$t('programs.details.filter.disabled'),
        value: 'DISABLED',
      }];
    },
    userRuleFilters() {
      return [{
        text: this.$t('challenges.filter.allChallenges'),
        value: 'ALL',
      },{
        text: this.$t('challenges.filter.activeChallenges'),
        value: 'STARTED',
      },{
        text: this.$t('challenges.filter.UpcomingChallenges'),
        value: 'NOT_STARTED',
      },{
        text: this.$t('challenges.filter.endedChallenges'),
        value: 'ENDED',
      }];
    },
  },
  watch: {
    search()  {
      this.displayMinimumCharactersToolTip = false;
      if (!this.search?.length) {
        this.getRules(false);
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
    loading() {
      if (this.loading) {
        document.dispatchEvent(new CustomEvent('displayTopBarLoading'));
      } else {
        document.dispatchEvent(new CustomEvent('hideTopBarLoading'));
      }
    },
  },
  created() {
    this.$root.$on('rule-deleted', this.getRules);
    this.$root.$on('rule-delete-confirm', this.confirmDelete);
    this.$root.$on('rules-category-load-more', this.loadMore);

    this.selectedRuleId = this.extractRuleIdFromPath();

    const promises = [];
    promises.push(this.getRules(false));
    if (this.selectedRuleId) {
      promises.push(this.getRuleById(this.selectedRuleId));
    }
    Promise.all(promises)
      .finally(() => this.$root.$applicationLoaded());
  },
  beforeDestroy() {
    this.$root.$off('rule-deleted', this.getRules);
    this.$root.$off('rule-delete-confirm', this.confirmDelete);
    this.$root.$off('rules-category-load-more', this.loadMore);
  },
  methods: {
    getRules(append, categoryId) {
      const offset = append && categoryId && this.rulesByCategoryId[categoryId]?.length || 0;
      const status = (this.filter === 'DISABLED' && 'DISABLED')
        || (this.isAdministrator && this.filter === 'ALL' && 'ALL')
        || 'ENABLED';
      const dateFilter = this.filter === 'DISABLED' && 'ALL' || this.filter;

      this.loading = true;
      return this.$ruleService.getRules({
        term: this.search,
        domainId: categoryId,
        dateFilter,
        status,
        type: 'MANUAL',
        offset,
        limit: this.pageSize,
        groupByDomain: !categoryId,
        expand: 'countAnnouncements',
        returnSize: true,
      })
        .then(categories => {
          if (categoryId) {
            const category = this.categoriesById[categoryId] || {};
            if (append) {
              category.rules = (this.rulesByCategoryId[categoryId] || []).concat(categories);
            } else {
              category.rules = categories || [];
            }
          } else {
            this.categories = categories;
          }
        }).finally(() => this.loading = false);
    },
    deleteRule() {
      this.loading = true;
      this.$ruleService.deleteRule(this.selectedRule.id)
        .then(() =>{
          this.showAlert('success', this.$t('challenges.deleteSuccess'));
          this.$root.$emit('rule-deleted');
        })
        .catch(e => {
          let msg = '';
          if (e.message === '401' || e.message === '403') {
            msg = this.$t('challenges.deletePermissionDenied');
          } else if (e.message  === '404') {
            msg = this.$t('challenges.notFound');
          } else  {
            msg = this.$t('challenges.deleteError');
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
      return this.getRules(true, categoryId);
    },
    getRuleById(ruleId) {
      return this.$ruleService.getRuleById(ruleId, 'countAnnouncements')
        .then(rule => {
          if (rule?.id) {
            window.history.replaceState('challenges', this.$t('challenges.challenges'), `${eXo.env.portal.context}/${eXo.env.portal.portalName}/contributions/challenges/${rule.id}`);
            this.$root.$emit('rule-detail-drawer', rule);
          } else {
            window.history.replaceState('challenges', this.$t('challenges.challenges'), `${eXo.env.portal.context}/${eXo.env.portal.portalName}/contributions/challenges`);
          }
        });      
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
          this.getRules();
        } else {
          this.waitForEndTyping();
        }
      }, this.endTypingKeywordTimeout);
    },
    extractRuleIdFromPath() {
      const urlPath = document.location.pathname;
      if (urlPath.indexOf(`${eXo.env.portal.context}/${eXo.env.portal.portalName}/contributions/challenges`) > -1) {
        return urlPath.match( /\d+/ ) && urlPath.match( /\d+/ ).join('');
      }
      return null;
    },
  }
};
</script>
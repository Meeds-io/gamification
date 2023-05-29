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
    <application-toolbar
      :left-text="tabName !== 'TRENDS' && incentivesCountLabel"
      :center-button-toggle="{
        selected: tabName,
        hide: false,
        buttons: [{
          value: 'TRENDS',
          text: $t('gamification.actions.groupBy.trends'),
          icon: 'fa-fire',
        }, {
          value: 'ALL',
          text: $t('gamification.actions.groupBy.all'),
          icon: 'fa-layer-group',
        }]
      }"
      :right-text-filter="tabName !== 'TRENDS' && {
        minCharacters: 3,
        placeholder: $t('challenges.filter.search'),
        tooltip: $t('challenges.filter.searchTooltip'),
      }"
      :right-filter-button="tabName !== 'TRENDS' && {
        text: $t('profile.label.search.openSearch'),
      }"
      :filters-count="filtersCount"
      @toggle-select="tabName = $event"
      @filter-text-input-end-typing="term = $event"
      @filter-button-click="$refs.rulesFilterDrawer.open()" />
    <v-card flat>
      <engagement-center-result-not-found
        v-if="ruleNotFound"
        :display-back-arrow="false"
        :message-title="$t('actions.ruleNotFoundMessage')"
        :button-text="$t('actions.exploreActions')"
        @button-event="ruleNotFound = false" />
      <engagement-center-result-not-found
        v-else-if="displayWelcomeMessage"
        :display-back-arrow="false"
        :message-title="$t('appCenter.welcomeMessage')"
        :message-info-one="$t('actions.welcomeMessageForRegularUser')" />
      <engagement-center-result-not-found
        v-else-if="displayNoSearchResult"
        :display-back-arrow="false"
        :message-title="welcomeMessage"
        :message-info-one="notFoundInfoMessage" />

      <engagement-center-rules-by-program
        v-if="tabName === 'ALL'"
        :term="term"
        :type="type"
        :status="status"
        @loading="loading = $event"
        @initialized="setRulesSize" />
      <engagement-center-rules-by-trend
        v-else-if="tabName === 'TRENDS'"
        @loading="loading = $event"
        @initialized="setRulesSize" />
    </v-card>
    <engagement-center-rules-filter-drawer
      ref="rulesFilterDrawer"
      :is-administrator="isAdministrator"
      @apply="applyFilter" />
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
    loading: true,
    tabName: 'TRENDS',
    rulesSize: 0,
    term: '',
    type: 'ALL',
    status: 'STARTED',
    ruleNotFound: false,
    linkBasePath: `${eXo.env.portal.context}/${eXo.env.portal.portalName}/contributions/actions`,
  }),
  computed: {
    filtersCount() {
      return (this.status !== 'STARTED' && 1 || 0)
        + (this.type !== 'ALL' && 1 || 0);
    },
    incentivesCountLabel() {
      if (!this.rulesSize || this.loading) {
        return '';
      }
      switch (this.status) {
      case 'ALL': return this.$t('rules.filter.incentives', {0: this.rulesSize});
      case 'STARTED': return this.$t('rules.filter.activeIncentives', {0: this.rulesSize});
      case 'NOT_STARTED': return this.$t('rules.filter.upcomingIncentives', {0: this.rulesSize});
      case 'ENDED': return this.$t('rules.filter.endedIncentives', {0: this.rulesSize});
      case 'DISABLED': return this.$t('rules.filter.disabledIncentives', {0: this.rulesSize});
      default: return '';
      }
    },
    classWelcomeMessage() {
      return this.displayWelcomeMessage && 'empty-rules-message' || '';
    },
    hasItemsToDisplay() {
      return this.rulesSize > 0;
    },
    displayWelcomeMessage() {
      return !this.loading && !this.hasItemsToDisplay && !this.term?.length && (this.status === 'ALL' || this.status === 'STARTED');
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
        return this.$t('appCenter.welcomeMessage');
      } 
      return '';
    },
    displayNoSearchResult() {
      return !this.loading && !this.hasItemsToDisplay && (this.term?.length || this.status !== 'STARTED' || this.type !== 'ALL');
    },
  },
  watch: {
    loading() {
      if (this.loading) {
        document.dispatchEvent(new CustomEvent('displayTopBarLoading'));
      } else {
        document.dispatchEvent(new CustomEvent('hideTopBarLoading'));
      }
    },
    tabName() {
      this.loading = true;
      if (this.tabName === 'ALL') {
        window.history.replaceState('challenges', this.$t('program.actions'), `${this.linkBasePath}#all`);
      } else {
        window.history.replaceState('challenges', this.$t('program.actions'), this.linkBasePath);
      }
    },
  },
  created() {
    this.$root.$on('rule-access-denied', this.displayRuleNotFoundMessage);
    this.$root.$on('rule-not-found', this.displayRuleNotFoundMessage);
    if (window.location.hash) {
      this.tabName = this.extractSelectedTabFromPath();
    }
  },
  mounted() {
    this.selectedRuleId = this.extractRuleIdFromPath();
    if (this.selectedRuleId) {
      this.$root.$emit('rule-detail-drawer-by-id', this.selectedRuleId);
    }
  },
  beforeDestroy() {
    this.$root.$off('rule-access-denied', this.displayRuleNotFoundMessage);
    this.$root.$off('rule-not-found', this.displayRuleNotFoundMessage);
  },
  methods: {
    reset() {
      this.tabName = 'TRENDS';
      this.ruleNotFound = false;
    },
    displayRuleNotFoundMessage() {
      this.ruleNotFound = true;
    },
    setRulesSize(rulesSize) {
      this.rulesSize = rulesSize;
    },
    applyFilter(type, status) {
      this.type = type;
      this.status = status;
    },
    extractRuleIdFromPath() {
      const urlPath = document.location.pathname;
      if (urlPath.indexOf(this.linkBasePath) > -1) {
        return urlPath.match( /\d+/ ) && urlPath.match( /\d+/ ).join('');
      }
      return null;
    },
    extractSelectedTabFromPath() {
      const hash = document.location.hash.substring(1);
      return hash === 'all' && 'ALL' || 'TRENDS';
    },
  }
};
</script>
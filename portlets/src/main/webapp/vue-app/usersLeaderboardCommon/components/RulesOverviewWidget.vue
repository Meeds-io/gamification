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
    :loading="loading"
    height="auto"
    min-width="auto">
    <template #title>
      <slot v-if="$slots.title && hasValidRules" name="title"></slot>
      <div v-else class="d-flex flex-grow-1 full-width position-relative">
        <div v-if="hasValidRules" class="widget-text-header text-none text-truncate">
          {{ title }}
        </div>
        <div
          :class="{
            'l-0': $vuetify.rtl,
            'r-0': !$vuetify.rtl,
          }"
          class="position-absolute absolute-vertical-center z-index-one">
          <v-tooltip v-if="$root.displayNotPublicallyVisible && !hubAccessOpen" top>
            <template #activator="{attrs, on}">
              <v-icon
                class="me-2"
                color="warning"
                size="18"
                v-on="on"
                v-bind="attrs">
                fa-exclamation-triangle
              </v-icon>
            </template>
            <span>
              {{ $t('gamification.publicWidgetHiddenTooltipPart1') }}
              <br>
              {{ $t('gamification.publicWidgetHiddenTooltipPart2') }}
            </span>
          </v-tooltip>
          <v-btn
            v-if="hasValidRules"
            :icon="hoverEdit"
            :small="hoverEdit"
            :title="$t('gamification.rules.overviewSettings.title')"
            height="auto"
            min-width="auto"
            class="pa-0"
            text
            @click="$emit('open-list')">
            <v-icon
              v-if="hoverEdit"
              size="18"
              color="primary">
              fa-external-link-alt
            </v-icon>
            <span v-else class="primary--text text-none">{{ $t('rules.seeAll') }}</span>
          </v-btn>
          <v-fab-transition hide-on-leave>
            <v-btn
              v-if="hoverEdit"
              :title="$t('gamification.programs.overviewSettings.editTooltip')"
              class="z-index-one"
              small
              icon
              @click="$root.$emit('rules-overview-settings')">
              <v-icon size="18">fa-cog</v-icon>
            </v-btn>
          </v-fab-transition>
          <v-btn
            v-if="emptyWidget && !!spaceId && activeRulesSize"
            icon
            @click="hideEmptyWidget">
            <v-icon>fa-times</v-icon>
          </v-btn>
        </div>
      </div>
    </template>
    <template #action>
      <slot name="action"></slot>
    </template>
    <template #default>
      <gamification-rules-overview-empty-widget
        v-if="!loading && emptyWidget"
        :display-completed="!!spaceId && activeRulesSize" />
      <div v-else-if="!loading">
        <template v-if="endingRulesCount">
          <div class="d-flex align-center">
            <span class="me-2">{{ $t('gamification.overview.endingActionsTitle') }}</span>
            <v-divider />
          </div>
          <gamification-rules-overview-item
            v-for="rule in endingRulesToDisplay"
            :key="rule.id"
            :rule="rule"
            :go-back-button="goBackButton" />
        </template>
        <template v-if="lockedRulesCount">
          <div class="d-flex align-center">
            <span class="me-2">{{ $t('gamification.overview.firstActionsToDoTitle') }}</span>
            <v-divider />
          </div>
          <gamification-rules-overview-item
            v-for="rule in lockedRulesToDisplay"
            :key="rule.id"
            :rule="rule"
            :go-back-button="goBackButton" />
        </template>
        <template v-if="validRulesCount">
          <div v-if="sectionsCount > 1" class="d-flex align-center">
            <span class="me-2">{{ $t('gamification.overview.availableActionsTitle') }}</span>
            <v-divider />
          </div>
          <gamification-rules-overview-item
            v-for="rule in validRulesToDisplay"
            :key="rule.id"
            :rule="rule"
            :go-back-button="goBackButton" />
        </template>
        <template v-if="upcomingRulesCount">
          <div class="d-flex align-center">
            <span class="me-2">{{ $t('gamification.overview.upcomingActionsTitle') }}</span>
            <v-divider />
          </div>
          <gamification-rules-overview-item
            v-for="rule in upcomingRulesToDisplay"
            :key="rule.id"
            :rule="rule"
            :go-back-button="goBackButton" />
        </template>
      </div>
    </template>
  </gamification-overview-widget>
</template>
<script>
export default {
  props: {
    programId: {
      type: String,
      default: null,
    },
    hideEmptyPlaceholder: {
      type: Boolean,
      default: false,
    },
    goBackButton: {
      type: Boolean,
      default: false,
    },
    hoverEdit: {
      type: Boolean,
      default: false,
    },
    loadSize: {
      type: Boolean,
      default: false,
    },
  },
  data: () => ({
    actionsPageURL: `${eXo.env.portal.context}/${eXo.env.portal.engagementSiteName}/contributions/actions`,
    loading: true,
    hideIfEmpty: false,
    spaceId: eXo.env.portal.spaceId,
    weekInMs: 604800000,
    endingRulesLimit: 2,
    lockedRulesLimit: 0,
    availableRulesLimit: 10,
    upcomingRulesLimit: 2,
    upcomingRulesList: [],
    activeRulesList: [],
    endingRulesList: [],
    activeRulesSize: 0,
    initialized: false,
    registrationSettings: null,
  }),
  computed: {
    title() {
      return this.spaceId?.length
        && this.$t('gamification.overview.spaceRulesOverviewTitle')
        || this.$t('gamification.overview.suggestedRulesTitle');
    },
    activeRulesLimit() {
      return this.availableRulesLimit + this.lockedRulesLimit;
    },
    rules() {
      const rules = [...this.upcomingRulesList, ...this.endingRulesList];
      this.activeRulesList.forEach(rule => {
        if (!rules.find(r => r.id === rule.id)) {
          rules.push(rule);
        }
      });
      return rules;
    },
    emptyWidget() {
      return !this.hasValidRules && !this.isHiddenWidget && !this.isHiddenWhenEmpty;
    },
    hasRules() {
      return this.rules?.length;
    },
    lockedRules() {
      if (!this.hasRules || this.spaceId?.length || !this.lockedRulesLimit) {
        return [];
      }
      const result = [];
      const lockedRules = this.rules.filter(r => this.isRuleValidButLocked(r));
      lockedRules.sort((r1, r2) => r2.title.localeCompare(r1.title));
      lockedRules
        .forEach(r => {
          try {
            this.addLockedRule(result, r);
          } catch (e) {
            // Invalid Rule Tree, avoid adding it in the list of
            // Locked Rules to unlock
            // eslint-disable-next-line no-console
            console.debug(e);
          }
        });
      result.sort((r1, r2) => (!r1.prerequisiteRules?.length && -1) || (!r2.prerequisiteRules?.length && 1) || 0);
      return result;
    },
    endingRules() {
      if (!this.hasRules || !this.endingRulesLimit) {
        return [];
      }
      const lockedRulesToDisplay = this.lockedRulesLimit && this.lockedRules.slice(0, this.lockedRulesLimit) || [];
      const endingRules = this.rules
        .filter(r => r?.userInfo?.context?.valid
            && !lockedRulesToDisplay.find(lr => lr.id === r.id)
            && r.endDate
            && (new Date(r.endDate).getTime() - Date.now()) < this.weekInMs);
      return endingRules;
    },
    validRules() {
      if (!this.hasRules) {
        return [];
      }
      const lockedRulesToDisplay = this.lockedRulesLimit && this.lockedRules.slice(0, this.lockedRulesLimit) || [];
      const endingRulesToDisplay = this.endingRulesLimit && this.endingRules.slice(0, this.endingRulesLimit) || [];
      const validRules = this.rules
        .filter(r => (r?.userInfo?.context?.valid || this.isRuleValidButLocked(r))
            && !lockedRulesToDisplay.find(lr => lr.id === r.id)
            && !endingRulesToDisplay.find(er => er.id === r.id));
      return validRules;
    },
    upcomingRules() {
      if (!this.hasRules || !this.upcomingRulesLimit) {
        return [];
      }
      const upcomingRules = this.rules
        .filter(this.isRuleValidButUpcoming)
        .filter(r => r.startDate && (new Date(r.startDate).getTime() - Date.now()) < this.weekInMs);
      return upcomingRules;
    },
    endingRulesCount() {
      return this.endingRules.length;
    },
    lockedRulesCount() {
      return this.lockedRules.length;
    },
    validRulesCount() {
      return this.validRules.length;
    },
    upcomingRulesCount() {
      return this.upcomingRules.length;
    },
    lockedRulesToDisplay() {
      return this.lockedRulesLimit && this.lockedRules.slice(0, this.lockedRulesLimit) || [];
    },
    endingRulesToDisplay() {
      return this.endingRulesLimit && this.endingRules.slice(0, this.endingRulesLimit) || [];
    },
    upcomingRulesToDisplay() {
      return this.upcomingRulesLimit && this.upcomingRules.slice(0, this.upcomingRulesLimit) || [];
    },
    validRulesToDisplay() {
      return this.availableRulesLimit && this.validRules.slice(0, this.availableRulesLimit) || [];
    },
    sectionsCount() {
      return (this.validRulesCount && 1 || 0)
        + (this.endingRulesCount && 1 || 0)
        + (this.lockedRulesCount && 1 || 0)
        + (this.upcomingRulesCount && 1 || 0);
    },
    hasValidRules() {
      return this.sectionsCount > 0;
    },
    isHiddenWhenEmpty() {
      return this.$root.isAnonymous
        || this.hideEmptyPlaceholder
        || this.hideIfEmpty
        || eXo.env.portal.hiddenGamOverviewEmptyWidgetBySpace?.[this.spaceId]
        || false;
    },
    isHiddenWidget() {
      return this.isHiddenWhenEmpty
        && !this.loading
        && !this.hasValidRules;
    },
    sortBy() {
      return this.$root.rulesSortBy || (this.spaceId?.length && 'score' || 'createdDate');
    },
    limit() {
      return this.$root.lockedRulesLimit + this.$root.endingRulesLimit + this.$root.availableRulesLimit + this.$root.upcomingRulesLimit;
    },
    hubAccessOpen() {
      return !this.registrationSettings || this.registrationSettings?.type === 'OPEN';
    },
  },
  watch: {
    isHiddenWidget: {
      immediate: true,
      handler() {
        if (this.isHiddenWidget) {
          this.$emit('hide');
        }
      },
    },
    loading() {
      if (!this.loading) {
        this.initialized = true;
      }
    },
    limit() {
      if (!this.loading && this.initialized) {
        this.loading = true;
        this.refreshLimit();
        this.retrieveRules();
      }
    },
    sortBy() {
      if (!this.loading && this.initialized) {
        this.loading = true;
        this.refreshLimit();
        this.retrieveRules();
      }
    },
  },
  created() {
    this.$root.$on('announcement-added', this.retrieveRules);
    this.$root.$on('rule-updated', this.retrieveRules);
    this.$root.$on('rule-deleted', this.retrieveRules);
    this.refreshLimit();
    this.retrieveRules();
    if (this.$root.displayNotPublicallyVisible) {
      this.initRegistration();
    }
  },
  beforeDestroy() {
    this.$root.$off('announcement-added', this.retrieveRules);
    this.$root.$off('rule-updated', this.retrieveRules);
    this.$root.$off('rule-deleted', this.retrieveRules);
  },
  methods: {
    refreshLimit() {
      if (typeof this.$root.lockedRulesLimit !== 'undefined') {
        this.lockedRulesLimit = this.$root.lockedRulesLimit || 0;
      }
      if (typeof this.$root.endingRulesLimit !== 'undefined') {
        this.endingRulesLimit = this.$root.endingRulesLimit || 0;
      }
      if (typeof this.$root.availableRulesLimit !== 'undefined') {
        this.availableRulesLimit = this.$root.availableRulesLimit || 0;
      }
      if (typeof this.$root.upcomingRulesLimit !== 'undefined') {
        this.upcomingRulesLimit = this.$root.upcomingRulesLimit || 0;
      }
    },
    retrieveRules() {
      this.loading = true;
      Promise.all([
        this.retrieveActiveRules(),
        this.retrieveEndingRules(),
        this.retrieveUpcomingRules(),
        this.retrieveActiveRulesSize(),
      ]).finally(() => this.loading = false);
    },
    retrieveEndingRules() {
      return this.endingRulesLimit
        && this.$ruleService.getRules({
          status: 'ENABLED',
          programStatus: 'ENABLED',
          dateFilter: 'STARTED_WITH_END',
          spaceId: this.spaceId?.length && [this.spaceId] || null,
          programId: this.programId,
          offset: 0,
          limit: this.endingRulesLimit,
          sortBy: 'endDate',
          sortDescending: false,
          expand: 'countRealizations',
          lang: eXo.env.portal.language,
          returnSize: false,
        }).then(result => this.endingRulesList = result?.rules || [])
        || Promise.resolve();
    },
    retrieveActiveRulesSize() {
      return (this.spaceId?.length || this.loadSize)
        && this.$ruleService.getRules({
          status: 'ENABLED',
          programStatus: 'ENABLED',
          dateFilter: 'STARTED',
          spaceId: this.spaceId?.length && [this.spaceId] || null,
          programId: this.programId,
          offset: 0,
          limit: 1,
          lang: eXo.env.portal.language,
          returnSize: true,
        }).then(result => {
          this.activeRulesSize = result?.size || 0;
          this.$emit('rules-size', this.activeRulesSize);
          this.$emit('has-more', this.activeRulesSize > this.availableRulesLimit);
        })
        || Promise.resolve();
    },
    retrieveActiveRules() {
      return this.activeRulesLimit
        && this.$ruleService.getRules({
          status: 'ENABLED',
          programStatus: 'ENABLED',
          dateFilter: this.spaceId?.length && 'ACTIVE' || 'STARTED',
          spaceId: this.spaceId?.length && [this.spaceId] || null,
          programId: this.programId,
          offset: 0,
          limit: this.activeRulesLimit,
          sortBy: this.sortBy,
          sortDescending: this.sortBy !== 'title',
          expand: 'countRealizations,expandPrerequisites',
          lang: eXo.env.portal.language,
          returnSize: false,
        }).then(result => this.activeRulesList = result?.rules || [])
        || Promise.resolve();
    },
    retrieveUpcomingRules() {
      return this.upcomingRulesLimit
        && this.$ruleService.getRules({
          status: 'ENABLED',
          programStatus: 'ENABLED',
          dateFilter: 'UPCOMING',
          spaceId: this.spaceId?.length && [this.spaceId] || null,
          programId: this.programId,
          offset: 0,
          limit: this.upcomingRulesLimit,
          sortBy: 'startDate',
          sortDescending: false,
          expand: 'countRealizations',
          lang: eXo.env.portal.language,
          returnSize: false,
        }).then(result => this.upcomingRulesList = result?.rules || [])
        || Promise.resolve();
    },
    loadMoreActiveRules() {
      this.availableRulesLimit = this.availableRulesLimit * 2;
      this.retrieveActiveRules();
    },
    hideEmptyWidget() {
      this.$root.$emit('hide-empty-widget');
      this.hideIfEmpty = true;
    },
    addLockedRule(ruleLocks, lockedRule) {
      if (!lockedRule || ruleLocks.find(r => r.id === lockedRule.id)) {
        return;
      } else if (lockedRule?.userInfo?.context?.valid) {
        ruleLocks.unshift(lockedRule);
      } else if (this.isRuleValidButLocked(lockedRule)) {
        lockedRule.prerequisiteRules
          .filter(r => r && lockedRule.userInfo.context.validPrerequisites[r.id] === false)
          .map(prerequisiteRule => {
            if (!prerequisiteRule?.userInfo?.context) {
              const rule = this.rules.find(rule => prerequisiteRule.id === rule.id);
              if (rule) {
                prerequisiteRule = rule;
              }
            }
            if (!prerequisiteRule || this.isRuleValidButLocked(prerequisiteRule)) {
              return prerequisiteRule;
            } else if (!prerequisiteRule?.userInfo?.context?.valid) {
              // The Prerequisite Rule doesn't exist or isn't valid, thus can't be done
              // So, delete the display of Prerequisite Rule
              throw new Error(`Prerequisite rule ${prerequisiteRule.id} seems invalid, avoid adding parent rule to the list`);
            } else {
              return prerequisiteRule;
            }
          })
          .filter(r => r)
          .forEach(r => this.addLockedRule(ruleLocks, r));
      }
    },
    isRuleValidButLocked(rule) {
      return rule?.prerequisiteRules?.length // has locked rules
          // Check that all other rule conditions are valid
          && !rule?.userInfo?.context?.valid
          && Object.keys(rule.userInfo.context)
            .every(prop => !prop.includes('valid')
                || prop === 'valid'
                || !prop.includes('validForIdentity')
                || prop === 'validForIdentity'
                || prop === 'validPrerequisites'
                || rule.userInfo.context[prop]);
    },
    isRuleValidButUpcoming(rule) {
      return !rule.userInfo.context.validDates // not valid dates yet
          && rule.startDate
          // Check that all other rule conditions are valid
          && Object.keys(rule.userInfo.context)
            .every(prop => !prop.includes('valid')
                || prop === 'valid'
                || !prop.includes('validForIdentity')
                || prop === 'validForIdentity'
                || prop === 'validPrerequisites'
                || prop === 'validDates'
                || rule.userInfo.context[prop]);
    },
    initRegistration() {
      return this.getRegistrationSettings()
        .then(data => this.registrationSettings = data);
    },
    getRegistrationSettings() {
      return fetch(`${eXo.env.portal.context}/${eXo.env.portal.rest}/registration/settings`, {
        method: 'GET',
        credentials: 'include',
      }).then((resp) => {
        if (resp?.ok) {
          return resp.json();
        } else {
          throw new Error('Error while getting Registration settings');
        }
      });
    },
  },
};
</script>
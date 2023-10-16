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
    v-if="hasValidRules"
    :action-url="seeAllUrl"
    :title="title"
    height="auto"
    min-width="auto">
    <template #default>
      <template v-if="lockedRulesCount">
        <div class="d-flex align-center mx-4">
          <span class="me-2">{{ $t('gamification.overview.firstActionsToDoTitle') }}</span>
          <v-divider />
        </div>
        <gamification-rules-overview-item
          v-for="rule in lockedRulesToDisplay"
          :key="rule.id"
          :rule="rule" />
      </template>
      <template v-if="endingRulesCount">
        <div class="d-flex align-center mx-4">
          <span class="me-2">{{ $t('gamification.overview.endingActionsTitle') }}</span>
          <v-divider />
        </div>
        <gamification-rules-overview-item
          v-for="rule in endingRulesToDisplay"
          :key="rule.id"
          :rule="rule" />
      </template>
      <template v-if="validRulesCount">
        <div v-if="sectionsCount > 1" class="d-flex align-center mx-4">
          <span class="me-2">{{ $t('gamification.overview.availableActionsTitle') }}</span>
          <v-divider />
        </div>
        <gamification-rules-overview-item
          v-for="rule in validRulesToDisplay"
          :key="rule.id"
          :rule="rule" />
      </template>
      <template v-if="upcomingRulesCount">
        <div class="d-flex align-center mx-4">
          <span class="me-2">{{ $t('gamification.overview.upcomingActionsTitle') }}</span>
          <v-divider />
        </div>
        <gamification-rules-overview-item
          v-for="rule in upcomingRulesToDisplay"
          :key="rule.id"
          :rule="rule" />
      </template>
    </template>
  </gamification-overview-widget>
  <gamification-overview-widget
    v-else-if="hasRules && !isHiddenWhenEmpty"
    :title="$t('gamification.overview.challengesOverviewTitle')"
    :loading="loading"
    height="240">
    <template #action>
      <v-btn
        icon
        @click="hideEmptyWidget">
        <v-icon>fa-times</v-icon>
      </v-btn>
    </template>
    <template #default>
      <gamification-overview-widget-row v-show="!loading" class="my-auto">
        <template #content>
          <div class="d-flex mx-auto align-center justify-center overflow-hidden">
            <v-card
              class="d-flex flex-column flex-grow-0 me-2 ms-n11 flex-shrink-0 border-box-sizing"
              min-width="45"
              max-width="45"
              min-height="45"
              max-height="45"
              color="transparent"
              flat>
              <v-avatar
                size="45"
                class="rule-program-cover border-color primary--text no-border"
                rounded>
                <v-img :src="completedRulesImageUrl" eager />
              </v-avatar>
            </v-card>
            <div class="flex-shrink-1 text-start text-truncate text-sub-title body-1">
              {{ $t('gamification.overview.rulesOverviewCompletedTitle') }}
            </div>
          </div>
          <div class="d-flex mx-auto align-center justify-center text-sub-title body-2 my-4">
            {{ $t('gamification.overview.rulesOverviewCompletedSubtitle') }}
          </div>
        </template>
      </gamification-overview-widget-row>
    </template>
  </gamification-overview-widget>
</template>
<script>
export default {
  props: {
    rules: {
      type: Array,
      default: () => [],
    },
    rulesCount: {
      type: Number,
      default: () => 0,
    },
    pageSize: {
      type: Number,
      default: null,
    },
    loading: {
      type: Boolean,
      default: false,
    },
    hideEmptyPlaceholder: {
      type: Boolean,
      default: false,
    },
    seeAllUrl: {
      type: String,
      default: () => '',
    },
    title: {
      type: String,
      default: () => '',
    },
  },
  data: () => ({
    actionsPageURL: `${eXo.env.portal.context}/${eXo.env.portal.portalName}/contributions/actions`,
    hideIfEmpty: false,
    weekInMs: 604800000,
  }),
  computed: {
    completedRulesImageIndex() {
      return (Number(eXo.env.portal.spaceId || '0') + Number(eXo.env.portal.userIdentityId)) % 8 + 1;
    },
    completedRulesImageUrl() {
      return `/gamification-portlets/skin/images/announcement/${this.completedRulesImageIndex}.webp`;
    },
    hasRules() {
      return this.rules?.length;
    },
    lockedRules() {
      if (!this.hasRules) {
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
      if (!this.hasRules) {
        return [];
      }
      const lockedRulesToDisplay = this.lockedRules.slice(0, 2);
      const endingRules = this.rules
        .filter(r => r?.userInfo?.context?.valid
            && !lockedRulesToDisplay.find(lr => lr.id === r.id)
            && r.endDate
            && (new Date(r.endDate).getTime() - Date.now()) < this.weekInMs);
      endingRules.sort((r1, r2) => new Date(r1.endDate).getTime() - new Date(r2.endDate).getTime());
      return endingRules;
    },
    validRules() {
      if (!this.hasRules) {
        return [];
      }
      const lockedRulesToDisplay = this.lockedRules.slice(0, 2);
      const endingRulesToDisplay = this.endingRules.slice(0, 2);
      const validRules = this.rules
        .filter(r => r?.userInfo?.context?.valid
            && !lockedRulesToDisplay.find(lr => lr.id === r.id)
            && !endingRulesToDisplay.find(er => er.id === r.id));
      validRules.sort((r1, r2) => r2.score - r1.score);
      return validRules;
    },
    upcomingRules() {
      if (!this.hasRules) {
        return [];
      }
      const upcomingRules = this.rules
        .filter(this.isRuleValidButUpcoming)
        .filter(r => r.startDate && (new Date(r.startDate).getTime() - Date.now()) < this.weekInMs);
      upcomingRules.sort((r1, r2) => new Date(r1.startDate).getTime() - new Date(r2.startDate).getTime());
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
    sectionsCount() {
      return (this.validRulesCount && 1 || 0)
        + (this.endingRulesCount && 1 || 0)
        + (this.lockedRulesCount && 1 || 0)
        + (this.upcomingRulesCount && 1 || 0);
    },
    lockedRulesToDisplay() {
      return this.lockedRules.slice(0, (this.sectionsCount === 1) && 4 || 2);
    },
    endingRulesToDisplay() {
      return this.endingRules.slice(0, (this.sectionsCount === 1) && 4 || 2);
    },
    validRulesToDisplay() {
      return this.validRules.slice(0, (this.sectionsCount < 4) && 4 || 2);
    },
    upcomingRulesToDisplay() {
      return this.upcomingRules.slice(0, (this.sectionsCount === 1) && 4 || 2);
    },
    hasValidRules() {
      return this.sectionsCount > 0;
    },
    isHiddenWhenEmpty() {
      return (this.hideIfEmpty && !this.hideEmptyPlaceholder)
        || (eXo.env.portal.spaceId
            && eXo.env.portal.hiddenGamOverviewEmptyWidgetBySpace
            && eXo.env.portal.hiddenGamOverviewEmptyWidgetBySpace[eXo.env.portal.spaceId]);
    },
    isHiddenWidget() {
      return !this.loading && !this.hasValidRules && (!this.hasRules || this.isHiddenWhenEmpty);
    },
    shouldLoadLockedRules() {
      return !this.loading
        && this.hasRules
        && this.rules?.length < this.rulesCount
        && this.lockedRulesCount < 2;
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
    shouldLoadLockedRules() {
      if (this.shouldLoadLockedRules) {
        this.$emit('load-more');
      }
    },
  },
  methods: {
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
                || prop === 'validPrerequisites'
                || prop === 'validDates'
                || rule.userInfo.context[prop]);
    },
  },
};
</script>
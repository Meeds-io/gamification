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
    height="auto"
    min-width="auto"
    extra-class="pa-0 justify-space-between">
    <template #title>
      {{ $t('gamification.overview.spaceRulesOverviewTitle') }}
    </template>
    <template #content>
      <template v-if="lockedRulesCount">
        <div class="d-flex align-center mx-4">
          <span class="me-2">{{ $t('gamification.overview.lockedActionsTitle') }}</span>
          <v-divider />
        </div>
        <gamification-rules-overview-item
          v-for="rule in lockedRules"
          :key="rule.id"
          :rule="rule" />
      </template>
      <template v-if="validRulesCount">
        <div v-if="!hasAvailableRulesOnly" class="d-flex align-center mx-4">
          <span class="me-2">{{ $t('gamification.overview.availableActionsTitle') }}</span>
          <v-divider />
        </div>
        <gamification-rules-overview-item
          v-for="rule in validRules"
          :key="rule.id"
          :rule="rule" />
      </template>
      <template v-if="upcomingRulesCount">
        <div class="d-flex align-center mx-4">
          <span class="me-2">{{ $t('gamification.overview.upcomingActionsTitle') }}</span>
          <v-divider />
        </div>
        <gamification-rules-overview-item
          v-for="rule in upcomingRules"
          :key="rule.id"
          :rule="rule"
          upcoming />
      </template>
    </template>
  </gamification-overview-widget>
  <gamification-overview-widget
    v-else-if="hasRules && !isHiddenWhenEmpty"
    height="240"
    :loading="loading">
    <template #title>
      <div class="d-flex flex-grow-1 align-center overflow-hidden">
        <div class="flex-grow-1 flex-shrink-1 text-start text-truncate">
          {{ $t('gamification.overview.challengesOverviewTitle') }}
        </div>
        <v-btn
          icon
          @click="hideEmptyWidget">
          <v-icon>fa-times</v-icon>
        </v-btn>
      </div>
    </template>
    <template #content>
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
    pageSize: {
      type: Number,
      default: null,
    },
    loading: {
      type: Boolean,
      default: false,
    },
  },
  data: () => ({
    actionsPageURL: `${eXo.env.portal.context}/${eXo.env.portal.portalName}/contributions/actions`,
    hideIfEmpty: false,
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
      const lockedRulesWithDependencies = [];
      const lockedRules = this.rules.filter(r => this.isRuleValidButLocked(r));
      lockedRules.sort((r1, r2) => r2.title.localeCompare(r1.title));
      lockedRules.forEach(r => {
        try {
          const lockedRuleDependencies = [];
          this.addLockedRule(lockedRulesWithDependencies, lockedRuleDependencies, r);
          lockedRulesWithDependencies.unshift(...lockedRuleDependencies);
        } catch (e) {
          // Invalid Rule Tree, avoid adding it in the list of
          // Locked Rules to unlock
          // eslint-disable-next-line no-console
          console.debug(e);
        }
      });
      lockedRulesWithDependencies.sort((r1, r2) => (!r1.prerequisiteRules?.length && -1) || (!r2.prerequisiteRules?.length && 1) || 0);
      return lockedRulesWithDependencies.slice(0, this.pageSize - 1);
    },
    lockedRulesCount() {
      return this.lockedRules.length;
    },
    validRules() {
      if (!this.hasRules) {
        return [];
      }
      return this.rules
        .filter(r => r?.userInfo?.context?.valid
            && !r?.prerequisiteRules?.length
            && (!this.lockedRulesCount || !this.lockedRules.find(lr => lr.id === r.id)))
        .slice(0, this.pageSize - this.lockedRulesCount);
    },
    validRulesCount() {
      return this.validRules.length;
    },
    upcomingRules() {
      if (!this.hasRules) {
        return [];
      }
      const upcomingRules = this.rules.filter(this.isRuleValidButUpcoming);
      upcomingRules.sort((r1, r2) => new Date(r1.startDate).getTime() - new Date(r2.startDate).getTime());
      return upcomingRules.slice(0, this.pageSize - this.validRulesCount - this.lockedRulesCount);
    },
    upcomingRulesCount() {
      return this.upcomingRules.length;
    },
    hasValidRules() {
      return this.lockedRulesCount || this.validRulesCount || this.upcomingRulesCount;
    },
    hasAvailableRulesOnly() {
      return this.validRulesCount && !this.lockedRulesCount && !this.upcomingRulesCount;
    },
    isHiddenWhenEmpty() {
      return this.hideIfEmpty
        || (eXo.env.portal.spaceId
            && eXo.env.portal.hiddenGamOverviewEmptyWidgetBySpace
            && eXo.env.portal.hiddenGamOverviewEmptyWidgetBySpace[eXo.env.portal.spaceId]);
    },
    isHiddenWidget() {
      return !this.loading && !this.hasValidRules && (!this.hasRules || this.isHiddenWhenEmpty);
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
  },
  methods: {
    hideEmptyWidget() {
      this.$root.$emit('hide-empty-widget');
      this.hideIfEmpty = true;
    },
    addLockedRule(lockedRules, rules, lockedRule) {
      if (rules.find(r => r.id === lockedRule.id)) {
        return;
      }
      // 1. Add locked Rule at the beginning of the array
      rules.unshift(lockedRule);
      // 2. Add dependent rules inside the list of lockedRules
      if (lockedRule.prerequisiteRules?.length) {
        lockedRule.prerequisiteRules
          .map(r => {
            const prerequisiteRule = this.rules.find(rule => r.id === rule.id);
            if (prerequisiteRule) {
              if (!lockedRules.find(rule => rule.id === prerequisiteRule.id)) {
                return prerequisiteRule;
              }
            } else {
              // The Prerequisite Rule doesn't exist or isn't valid, thus can't be done
              // So, delete the display of Prerequisite Rule
              throw new Error(`Prerequisite rule ${r.id} seems invalid, avoid adding parent rule to the list`);
            }
          })
          .filter(r => r && lockedRule.userInfo.context.validPrerequisites[r.id] === false)
          .forEach(r => this.addLockedRule(lockedRules,rules, r));
      }
    },
    isRuleValidButLocked(rule) {
      return rule?.prerequisiteRules?.length // has locked rules
          // Check that all other rule conditions are valid
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
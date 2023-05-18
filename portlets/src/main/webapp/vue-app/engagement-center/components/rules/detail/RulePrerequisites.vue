<!--

 This file is part of the Meeds project (https://meeds.io/).

 Copyright (C) 2020 - 2023 Meeds Association contact@meeds.io

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
  <v-list-item
    v-if="prerequisitesCount > 0"
    :class="hasRemainingPrerequisitesCount && 'grey'"
    class="rule-prerequisites pa-0"
    dense>
    <v-list-item-avatar class="me-2 mb-auto">
      <v-avatar size="32" tile>
        <v-icon
          :class="hasRemainingPrerequisitesCount && 'white--text' || 'primary--text'"
          size="30">
          {{ hasRemainingPrerequisitesCount && 'fas fa-lock' || 'fas fa-unlock' }}
        </v-icon>
      </v-avatar>
    </v-list-item-avatar>
    <div class="d-flex flex-column flex-grow-1">
      <v-list-item-content
        :class="hasRemainingPrerequisitesCount && 'mt-3' || 'mt-1'">
        <v-list-item-title
          v-sanitized-html="prerequisitesTitle"
          :class="hasRemainingPrerequisitesCount && 'center white--text px-2'"
          class="text-wrap" />
      </v-list-item-content>
      <v-list-item-content>
        <v-list-item-sub-title class="d-flex flex-column">
          <engagement-center-rule-prerequisite-item
            v-for="r in remainingPrerequisites"
            :key="r.id"
            :rule="rule"
            :prerequisite-rule="r"
            class="mx-auto mb-4" />
          <engagement-center-rule-prerequisite-item
            v-for="r in achievedPrerequisites"
            :key="r.id"
            :rule="rule"
            :prerequisite-rule="r"
            class="mx-auto mb-4" />
        </v-list-item-sub-title>
      </v-list-item-content>
    </div>
  </v-list-item>
</template>
<script>
export default {
  props: {
    rule: {
      type: Object,
      default: null,
    },
  },
  computed: {
    prerequisiteRules() {
      return this.rule.prerequisiteRules;
    },
    prerequisitesCount() {
      return this.rule.prerequisiteRules?.length || 0;
    },
    achievedPrerequisites() {
      const prerequisitesStatus = this.rule?.userInfo?.context?.validPrerequisites;
      return prerequisitesStatus
          && this.prerequisiteRules
          && this.prerequisiteRules.filter(r => prerequisitesStatus[`${r.id}`])
          || [];
    },
    achievedPrerequisitesCount() {
      return this.achievedPrerequisites.length;
    },
    remainingPrerequisites() {
      const prerequisitesStatus = this.rule?.userInfo?.context?.validPrerequisites;
      return prerequisitesStatus
          && this.prerequisiteRules
          && this.prerequisiteRules.filter(r => !prerequisitesStatus[`${r.id}`])
          || [];
    },
    remainingPrerequisitesCount() {
      return this.remainingPrerequisites.length;
    },
    hasRemainingPrerequisitesCount() {
      return this.remainingPrerequisitesCount > 0;
    },
    prerequisitesTitle() {
      if (this.remainingPrerequisitesCount <= 0) {
        if (this.prerequisitesCount === 1) {
          return this.$t('rules.prerequisiteActionMade');
        } else {
          return this.$t('rules.prerequisiteActionsMade', {0: this.prerequisitesCount});
        }
      } else if (this.remainingPrerequisitesCount === 1) {
        return this.$t('rules.doActionToUnlockIncentive');
      } else {
        return this.$t('rules.doActionsToUnlockIncentive', {0: this.remainingPrerequisitesCount});
      }
    },
  },
};
</script>
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
  <v-card
    v-if="prerequisitesTitle"
    max-width="380"
    class="rule-prerequisites grey"
    flat>
    <div class="d-flex align-center pt-4 px-2">
      <v-avatar
        size="32"
        class="me-2"
        tile>
        <v-icon
          class="white--text"
          size="30">
          fas fa-lock
        </v-icon>
      </v-avatar>
      <div
        v-sanitized-html="prerequisitesTitle"
        class="white--text text-wrap font-weight-bold"></div>
    </div>
    <div class="d-flex flex-column align-center justify-center mt-3">
      <engagement-center-rule-prerequisite-item
        v-for="r in remainingPrerequisites"
        :key="r.id"
        :rule="rule"
        :prerequisite-rule="r" />
    </div>
  </v-card>
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
    prerequisitesTitle() {
      if (this.remainingPrerequisitesCount === 1) {
        return this.$t('rules.doActionToUnlockIncentive');
      } else if (this.remainingPrerequisitesCount > 1) {
        return this.$t('rules.doActionsToUnlockIncentive', {0: this.remainingPrerequisitesCount});
      }
      return null;
    },
  },
};
</script>
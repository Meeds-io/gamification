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
  <div
    v-if="hasRemainingPrerequisitesCount"
    class="rule-card-mask-prerequisites d-flex flex-column align-center">
    <v-icon
      class="white--text"
      size="30">
      fas fa-lock
    </v-icon>
    <div
      v-sanitized-html="prerequisitesTitle"
      class="white--text text-wrap title">
    </div>
  </div>
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
    hasRemainingPrerequisitesCount() {
      return this.remainingPrerequisitesCount > 0;
    },
    prerequisitesTitle() {
      if (this.remainingPrerequisitesCount === 1) {
        return this.$t('rules.card.completeActionToUnlock', {0: this.remainingPrerequisites[0].title});
      } else {
        return this.$t('rules.card.completeActionToUnlock', {0: this.remainingPrerequisitesCount});
      }
    },
  },
};
</script>
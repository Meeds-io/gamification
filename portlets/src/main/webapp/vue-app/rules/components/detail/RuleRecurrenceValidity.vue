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
  <div v-if="recurrenceTitle" class="rule-recurrence-validity d-flex align-center justify-center">
    <v-icon size="26" class="primary--text me-2">
      far fa-check-circle
    </v-icon>
    {{ recurrenceTitle }}
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
    recurrence() {
      return this.rule.recurrence;
    },
    recurrenceValid() {
      return this.rule?.userInfo?.context?.validRecurrence;
    },
    nextOccurenceDaysLeft() {
      const diff = this.rule?.userInfo?.context?.nextOccurenceMillis;
      if (diff) {
        const days = Math.floor(diff / 1000 / (60 * 60 * 24));
        return days + 1;
      } else {
        return 0;
      }
    },
    recurrenceTitle() {
      if (!this.nextOccurenceDaysLeft
          && !this.recurrenceValid
          && (this.recurrence === 'DAILY'
              || this.recurrence === 'WEEKLY'
              || this.recurrence === 'MONTHLY')) {
        return this.$t('rules.card.actionCantAchieveAgainBeforeEndDate');
      }
      if (!this.recurrence) {
        return null;
      } else if (!this.recurrenceValid) {
        switch (this.recurrence) {
        case 'ONCE':
          return this.$t('rules.actionAlreadyDone');
        case 'DAILY':
          return this.$t('rules.actionAlreadyDoneEarnTomorrow');
        case 'WEEKLY':
          return this.$t('rules.actionAlreadyDoneEarnInDays', {0: this.nextOccurenceDaysLeft});
        case 'MONTHLY':
          return this.$t('rules.actionAlreadyDoneEarnInDays', {0: this.nextOccurenceDaysLeft});
        default:
          return null;
        }
      } else {
        return null;
      }
    },
  },
};
</script>
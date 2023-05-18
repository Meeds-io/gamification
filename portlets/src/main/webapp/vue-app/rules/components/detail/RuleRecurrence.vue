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
    v-if="recurrence"
    class="rule-recurrence pa-0"
    dense>
    <v-list-item-avatar class="me-2">
      <v-avatar size="32" tile>
        <v-icon
          size="30"
          class="primary--text">
          {{ recurrenceIcon }}
        </v-icon>
      </v-avatar>
    </v-list-item-avatar>
    <v-list-item-content>
      <v-list-item-title
        v-sanitized-html="recurrenceTitle"
        class="text-wrap" />
    </v-list-item-content>
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
    recurrenceIcon() {
      return (this.recurrenceValid || this.recurrence !== 'ONCE') && 'fas fa-redo-alt' || 'fas fa-check';
    },
    recurrence() {
      return this.rule.recurrence;
    },
    recurrenceValid() {
      return this.rule?.userInfo?.context?.validRecurrence;
    },
    nextOccurenceDaysLeft() {
      return this.rule?.userInfo?.context?.nextOccurenceDaysLeft;
    },
    recurrenceTitle() {
      if (!this.recurrence) {
        return null;
      } else if (this.recurrenceValid) {
        switch (this.recurrence) {
        case 'ONCE':
          return this.$t('rules.recurrenceDoItOnce', {0: '<strong>', 1: '</strong>'});
        case 'DAILY':
          return this.$t('rules.recurrenceDoItOncePerDay', {0: '<strong>', 1: '</strong>'});
        case 'WEEKLY':
          return this.$t('rules.recurrenceDoItOncePerWeek', {0: '<strong>', 1: '</strong>'});
        case 'MONTHLY':
          return this.$t('rules.recurrenceDoItOncePerMonth', {0: '<strong>', 1: '</strong>'});
        default:
          return null;
        }
      } else {
        switch (this.recurrence) {
        case 'ONCE':
          return this.$t('rules.actionAlreadyDoneOnce');
        case 'DAILY':
          return this.$t('rules.actionAlreadyDoneOncePerDay', {0: '<strong>', 1: '</strong>'});
        case 'WEEKLY':
          return this.$t('rules.actionAlreadyDoneOncePerWeek', {0: '<strong>', 1: '</strong>', 2: this.nextOccurenceDaysLeft});
        case 'MONTHLY':
          return this.$t('rules.actionAlreadyDoneOncePerMonth', {0: '<strong>', 1: '</strong>', 2: this.nextOccurenceDaysLeft});
        default:
          return null;
        }
      }
    },
  },
};
</script>
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
  <div v-if="alreadyEnded" class="d-flex align-center justify-center">
    <v-icon
      class="primary--text me-2 mb-1"
      size="18">
      fas fa-calendar-check
    </v-icon>
    <span class="error--text">{{ $t('challenges.label.over') }}</span>
  </div>
  <div v-else-if="notStartedYet || hasEndDate" class="d-flex align-center justify-center">
    <template v-if="!lessThanADay">
      <v-card
        min-height="25"
        min-width="25"
        class="medium-grey rounded white--text caption px-1px align-center justify-center d-flex"
        flat>
        {{ $t('rules.card.daysShort', {0: remainingDays}) }}
      </v-card>
      <span v-if="separator" class="mx-1">-</span>
      <span v-else class="mx-1"></span>
    </template>
    <v-card
      :class="backgroundColor"
      min-width="25"
      min-height="25"
      class="rounded white--text caption px-1px align-center justify-center d-flex"
      flat>
      {{ $t('rules.card.hoursShort', {0: remainingHours}) }}
    </v-card>
    <span v-if="separator" class="mx-1">-</span>
    <span v-else class="mx-1"></span>
    <v-card
      :class="backgroundColor"
      class="rounded white--text caption px-1px align-center justify-center d-flex"
      min-width="25"
      min-height="25"
      flat>
      {{ $t('rules.card.minutesShort', {0: remainingMinutes}) }}
    </v-card>
    <template v-if="lessThanADay">
      <span v-if="separator" class="mx-1">-</span>
      <span v-else class="mx-1"></span>
      <v-card
        min-width="25"
        min-height="25"
        class="error-color-background rounded white--text caption px-1px align-center justify-center d-flex"
        flat>
        {{ $t('rules.card.secondsShort', {0: remainingSeconds}) }}
      </v-card>
    </template>
  </div>
</template>
<script>
export default {
  props: {
    rule: {
      type: Object,
      default: null,
    },
    separator: {
      type: Boolean,
      default: false,
    },
  },
  computed: {
    startDateMillis() {
      return this.rule?.startDate && new Date(this.rule?.startDate).getTime() || 0;
    },
    endDateMillis() {
      return this.rule?.endDate && new Date(this.rule?.endDate).getTime() || 0;
    },
    notStartedYet() {
      return this.startDateMillis && this.startDateMillis > this.$root.now;
    },
    hasEndDate() {
      return !this.alreadyEnded && this.endDateMillis;
    },
    alreadyEnded() {
      return this.endDateMillis && this.endDateMillis < this.$root.now;
    },
    remainingDays() {
      if (this.alreadyEnded) {
        return 0;
      } else if (this.notStartedYet) {
        return this.getRemainingDays(this.startDateMillis, this.$root.now);
      } else if (this.endDateMillis) {
        return this.getRemainingDays(this.endDateMillis, this.$root.now);
      }
      return 0;
    },
    remainingHours() {
      if (this.notStartedYet) {
        return this.getRemainingHours(this.startDateMillis, this.$root.now);
      } else if (this.hasEndDate) {
        return this.getRemainingHours(this.endDateMillis, this.$root.now);
      }
      return 0;
    },
    remainingMinutes() {
      if (this.notStartedYet) {
        return this.getRemainingMinutes(this.startDateMillis, this.$root.now);
      } else if (this.hasEndDate) {
        return this.getRemainingMinutes(this.endDateMillis, this.$root.now);
      }
      return 0;
    },
    remainingSeconds() {
      if (this.notStartedYet) {
        return this.getRemainingSeconds(this.startDateMillis, this.$root.now);
      } else if (this.hasEndDate) {
        return this.getRemainingSeconds(this.endDateMillis, this.$root.now);
      }
      return 0;
    },
    lessThanADay() {
      return this.remainingDays <= 0;
    },
    backgroundColor() {
      return this.lessThanADay && 'error-color-background' || 'medium-grey';
    },
  },
  methods: {
    computeLabel() {
      if (this.alreadyEnded) {
        this.label = null;
      } else if (this.notStartedYet) {
        this.label = this.$t('actions.label.opensInShort');
      } else if (this.endDateMillis) {
        this.label = this.$t('actions.label.endsInShort');
      } else {
        this.label = null;
      }
    },
    getRemainingDays(timeInMs, now) {
      const remainingSeconds = parseInt((timeInMs - now) / 1000);
      return Math.floor(remainingSeconds / (60 * 60 * 24));
    },
    getRemainingHours(timeInMs, now) {
      const remainingSeconds = parseInt((timeInMs - now) / 1000);
      return Math.floor((remainingSeconds % (60 * 60 * 24)) / (60 * 60));
    },
    getRemainingMinutes(timeInMs, now) {
      const remainingSeconds = parseInt((timeInMs - now) / 1000);
      return Math.floor((remainingSeconds % (60 * 60)) / 60);
    },
    getRemainingSeconds(timeInMs, now) {
      const remainingSeconds = parseInt((timeInMs - now) / 1000);
      return Math.floor(remainingSeconds % 60);
    },
  },
};
</script>
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
  <div :class="lessThanADay && 'error--text'">
    {{ datesInfo }}
  </div>
</template>
<script>
export default {
  props: {
    rule: {
      type: Object,
      default: null,
    },
    small: {
      type: Boolean,
      default: false,
    },
  },
  data: () => ({
    label: null,
  }),
  computed: {
    startDateMillis() {
      return this.rule?.startDate && new Date(this.rule?.startDate).getTime() || 0;
    },
    endDateMillis() {
      return this.rule?.endDate && new Date(this.rule?.endDate).getTime() || 0;
    },
    notStartedYet() {
      return this.startDateMillis && this.startDateMillis > Date.now();
    },
    alreadyEnded() {
      return this.endDateMillis && this.endDateMillis < Date.now();
    },
    lessThanADay() {
      if (this.alreadyEnded) {
        return false;
      } else if (this.notStartedYet) {
        return this.getRemainingDays(this.startDateMillis) <= 0;
      } else if (this.endDateMillis) {
        return this.getRemainingDays(this.endDateMillis) <= 0;
      }
      return false;
    },
    datesInfo() {
      if (this.alreadyEnded) {
        return !this.small && this.$t('challenges.label.over');
      } else if (this.notStartedYet) {
        return this.small
            && this.getRemainingDateLabel(this.startDateMillis)
            || this.$t('actions.label.opensIn', {0: this.getRemainingDateLabel(this.startDateMillis)});
      } else if (this.endDateMillis) {
        return this.small
            && this.getRemainingDateLabel(this.endDateMillis)
            || this.$t('actions.label.endsIn', {0: this.getRemainingDateLabel(this.endDateMillis)});
      }
      return null;
    },
  },
  watch: {
    datesInfo: {
      immediate: true,
      handler() {
        this.computeLabel();
        this.$emit('input', this.datesInfo);
      },
    },
    label: {
      immediate: true,
      handler() {
        this.$emit('update:label', this.label);
      },
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
    getRemainingDays(timeInMs) {
      const remainingSeconds = parseInt((timeInMs - Date.now()) / 1000);
      return Math.floor(remainingSeconds / (60 * 60 * 24));
    },
    getRemainingDateLabel(timeInMs) {
      const remainingSeconds = parseInt((timeInMs - Date.now()) / 1000);
      const days = Math.floor(remainingSeconds / (60 * 60 * 24));
      const hours = Math.floor((remainingSeconds % (60 * 60 * 24)) / (60 * 60));
      if (days === 0) {
        const minutes = Math.floor((remainingSeconds % (60 * 60)) / 60);
        return this.$t('rules.card.timerShortHoursMinutes', {0: hours, 1: minutes});
      } else {
        return this.$t('rules.card.timerShortDaysHours', {0: days, 1: hours});
      }
    },
  },
};
</script>
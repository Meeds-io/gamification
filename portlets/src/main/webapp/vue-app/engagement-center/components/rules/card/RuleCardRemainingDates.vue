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
  <div v-if="datesInfo" class="rule-card-dates d-flex align-center">
    <v-icon
      class="primary--text me-2 mb-1"
      size="18">
      fas fa-calendar-check
    </v-icon>
    <div>
      {{ datesInfo }}
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
    datesInfo() {
      if (this.notStartedYet) {
        const remainingSeconds = parseInt((this.startDateMillis - Date.now()) / 1000);
        const days = Math.floor(remainingSeconds / (60 * 60 * 24));
        const hours = Math.floor((remainingSeconds % (60 * 60 * 24)) / (60 * 60));
        const minutes = Math.floor((remainingSeconds % (60 * 60)) / 60);
        return this.$t('rules.card.timerShort', {0: days, 1: hours, 2: minutes});
      } else if (this.endDateMillis && !this.alreadyEnded) {
        const remainingSeconds = parseInt((this.endDateMillis - Date.now()) / 1000);
        const days = Math.floor(remainingSeconds / (60 * 60 * 24));
        const hours = Math.floor((remainingSeconds % (60 * 60 * 24)) / (60 * 60));
        const minutes = Math.floor((remainingSeconds % (60 * 60)) / 60);
        return this.$t('rules.card.timerShort', {0: days, 1: hours, 2: minutes});
      }
      return null;
    },
  },
};
</script>
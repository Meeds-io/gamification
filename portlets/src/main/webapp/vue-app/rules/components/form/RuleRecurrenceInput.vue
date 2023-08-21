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
  <v-card max-width="450" flat>
    <div class="d-flex flex-grow-1 text-left px-0 pt-4 pb-2">
      {{ $t('gamification.achievementCanBeSubmitted') }}
    </div>

    <v-btn-toggle
      v-model="period"
      class="flex-grow-1 d-flex flex-wrap justify-space-between"
      mandatory
      outlined
      dense
      group>
      <engagement-center-button-group-item
        :selected-value="period"
        :title="$t('gamification.achivementRecurrence.onceTooltip')"
        value="ONCE"
        selected-color="primary"
        class="d-flex justify-center">
        {{ $t('gamification.achivementRecurrence.once') }}
      </engagement-center-button-group-item>
      <engagement-center-button-group-item
        :selected-value="period"
        :title="$t('gamification.achivementRecurrence.dailyTooltip')"
        value="DAILY"
        selected-color="primary"
        class="d-flex justify-center">
        {{ $t('gamification.achivementRecurrence.daily') }}
      </engagement-center-button-group-item>
      <engagement-center-button-group-item
        :selected-value="period"
        :title="$t('gamification.achivementRecurrence.weeklyTooltip')"
        value="WEEKLY"
        selected-color="primary"
        class="d-flex justify-center">
        {{ $t('gamification.achivementRecurrence.weekly') }}
      </engagement-center-button-group-item>
      <engagement-center-button-group-item
        :selected-value="period"
        :title="$t('gamification.achivementRecurrence.monthlyTooltip')"
        value="MONTHLY"
        selected-color="primary"
        class="d-flex justify-center">
        {{ $t('gamification.achivementRecurrence.monthly') }}
      </engagement-center-button-group-item>
    </v-btn-toggle>
  </v-card>
</template>
<script>
export default {
  props: {
    value: {
      type: String,
      default: null,
    },
  },
  data: () => ({
    period: null,
  }),
  created() {
    this.period = this.value;
  },
  watch: {
    period() {
      this.$emit('input', this.period);
    },
    value() {
      if (this.period !== this.value) {
        this.period = this.value;
      }
    },
  }
};
</script>
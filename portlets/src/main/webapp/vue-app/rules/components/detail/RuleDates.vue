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
    v-if="show"
    class="rule-dates pa-0"
    :class="notStartedYet && 'grey'"
    dense>
    <v-list-item-avatar class="me-2">
      <v-avatar size="32" tile>
        <v-icon
          :class="notStartedYet && 'white--text' || 'primary--text'"
          size="30">
          {{ notStartedYet && 'fas fa-calendar-plus' || 'fas fa-calendar-day' }}
        </v-icon>
      </v-avatar>
    </v-list-item-avatar>
    <v-list-item-content>
      <v-list-item-title :class="notStartedYet && 'white--text'">
        <engagement-center-rule-date-info
          v-model="datesInfo"
          :rule="rule"
          @input="initialized = true" />
      </v-list-item-title>
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
  data: () => ({
    datesInfo: null,
    initialized: false,
  }),
  computed: {
    show() {
      return !this.initialized || this.datesInfo;
    },
    startDateMillis() {
      return this.rule?.startDate && new Date(this.rule?.startDate).getTime() || 0;
    },
    notStartedYet() {
      return this.startDateMillis && this.startDateMillis > Date.now();
    },
  },
};
</script>
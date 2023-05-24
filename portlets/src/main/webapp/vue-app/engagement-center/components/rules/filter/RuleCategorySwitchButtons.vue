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
  <v-btn-toggle
    v-model="filter"
    :mandatory="!disabled"
    color="primary"
    outlined
    dense
    @change="$emit('change')">
    <v-btn
      value="TRENDS"
      text>
      <v-icon
        :color="isTrends && 'primary'"
        class="me-2"
        small>
        fa-fire
      </v-icon>
      {{ $t('gamification.actions.groupBy.trends') }}
    </v-btn>
    <v-btn
      value="PROGRAMS"
      text>
      <v-icon
        :color="isPrograms && 'primary'"
        class="me-2"
        small>
        fa-layer-group
      </v-icon>
      {{ $t('gamification.actions.groupBy.programs') }}
    </v-btn>
  </v-btn-toggle>
</template>
<script>
export default {
  props: {
    value: {
      type: Boolean,
      default: false,
    },
    disabled: {
      type: Boolean,
      default: false,
    },
  },
  data: () => ({
    filter: 'TRENDS',
    originalFilter: 'TRENDS',
  }),
  computed: {
    isPrograms() {
      return this.filter === 'PROGRAMS';
    },
    isTrends() {
      return this.filter === 'TRENDS';
    },
  },
  watch: {
    filter() {
      if (!this.disabled) {
        this.$nextTick().then(() => this.$emit('input', this.isPrograms));
      }
    },
    disabled() {
      if (this.disabled) {
        this.originalFilter = this.filter || this.originalFilter;
        this.$nextTick().then(() => this.filter = '');
      } else {
        this.$nextTick().then(() => this.filter = this.originalFilter);
      }
    },
  },
};
</script>
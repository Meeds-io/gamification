<!--
  This file is part of the Meeds project (https://meeds.io/).
  Copyright (C) 2022 Meeds Association
  contact@meeds.io
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
  <v-flex class="d-flex content-box-sizing justify-end">
    <select
      v-model="filter"
      class="width-auto my-auto ignore-vuetify-classes d-none d-sm-inline"
      @change="applyFilter">
      <option
        v-for="ruleFilter in ruleFilters"
        :key="ruleFilter.value"
        :value="ruleFilter.value">
        {{ ruleFilter.text }}
      </option>
    </select>
    <v-icon
      class="d-sm-none"
      size="18"
      @click="openBottomMenuFilter">
      fa-filter
    </v-icon>
    <v-bottom-sheet v-model="bottomMenu" class="pa-0">
      <v-sheet class="text-center">
        <v-toolbar
          color="primary"
          dark
          class="border-box-sizing">
          <v-btn text @click="bottomMenu = false">
            {{ $t('programs.details.filter.cancel') }}
          </v-btn>
          <v-spacer />
          <v-toolbar-title>
            <v-icon>fa-filter</v-icon>
            {{ $t('programs.details.filter.cancel') }}
          </v-toolbar-title>
          <v-spacer />
          <v-btn text @click="changeFilterSelection">
            {{ $t('programs.details.filter') }}
          </v-btn>
        </v-toolbar>
        <v-list>
          <v-list-item
            v-for="ruleFilter in ruleFilters"
            :key="ruleFilter"
            @click="filterToChange = ruleFilter.value">
            <v-list-item-title class="align-center d-flex">
              <v-icon v-if="filterToChange === ruleFilter.value">fa-check</v-icon>
              <span v-else class="me-6"></span>
              <v-spacer />
              <div>
                {{ ruleFilter.text }}
              </div>
              <v-spacer />
              <span class="me-6"></span>
            </v-list-item-title>
          </v-list-item>
        </v-list>
      </v-sheet>
    </v-bottom-sheet>
  </v-flex>
</template>
<script>
export default {
  data: () => ({
    filterToChange: null,
    bottomMenu: false,
    filter: 'ENABLED',
  }),
  computed: {
    ruleFilters() {
      return [{
        text: this.$t('programs.details.filter.all'),
        value: 'ALL',
      },{
        text: this.$t('programs.details.filter.enabled'),
        value: 'ENABLED',
      },{
        text: this.$t('programs.details.filter.disabled'),
        value: 'DISABLED',
      }];
    },
  },
  methods: {
    applyFilter() {
      this.$emit('filter-applied', this.filter);
    },
    openBottomMenuFilter() {
      this.filterToChange = this.filter;
      this.bottomMenu = true;
    },
    changeFilterSelection() {
      this.bottomMenu = false;
      this.filter = this.filterToChange;
      this.applyFilter();
    },
  },
};
</script>
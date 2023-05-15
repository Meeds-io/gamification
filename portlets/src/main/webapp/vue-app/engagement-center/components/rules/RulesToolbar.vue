<!--
This file is part of the Meeds project (https://meeds.io/).
Copyright (C) 2023 Meeds Association
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
  <div class="d-flex flex align-center mt-4">
    <template v-if="canManageRule">
      <v-icon
        v-if="menuHeaderChanged"
        @click="changeHeaderMenu">
        fas fa-arrow-left
      </v-icon>
      <v-btn
        v-else
        class="btn btn-primary"
        small
        @click="$root.$emit('rule-form-drawer')">
        <v-icon dark>
          mdi-plus
        </v-icon>
        <span class="ms-2 d-none d-lg-inline subtitle-1">
          {{ $t('programs.details.rule.button.addRule') }}
        </span>
      </v-btn>
    </template>
    <v-spacer v-if="!isMobile" />
    <v-card
      width="220"
      max-width="100%"
      flat>
      <v-text-field
        v-if="!isMobile || menuHeaderChanged"
        v-model="keyword"
        :placeholder="$t('programs.details.filter.filterRules')"
        prepend-inner-icon="fa-filter"
        clear-icon="fa-times fa-1x"
        class="pa-0 me-3 my-auto"
        clearable
        hide-details />
    </v-card>
    <v-spacer v-if="isMobile" />
    <v-scale-transition>
      <select
        v-if="canManageRule"
        id="rulesStatusFilter"
        v-model="status"
        class="width-auto my-auto ignore-vuetify-classes d-none d-sm-inline"
        @change="$root.$emit('program-rules-update-status', status)">
        <option
          v-for="item in ruleFilters"
          :key="item.value"
          :value="item.value">
          {{ item.text }}
        </option>
      </select>
      <select
        v-else
        id="rulesDateFilter"
        v-model="dateFilter"
        class="my-auto ignore-vuetify-classes text-truncate challengeQuickFilter width-auto"
        @change="$root.$emit('program-rules-update-date-filter', dateFilter)">
        <option
          v-for="item in rulesDateFilter"
          :key="item.value"
          :value="item.value">
          <span class="d-none d-lg-inline">
            {{ item.text }}
          </span>
        </option>
      </select>
    </v-scale-transition>
    <div class="d-sm-none">
      <v-icon
        v-if="!menuHeaderChanged"
        @click="changeHeaderMenu">
        fa-filter
      </v-icon>
      <v-icon
        v-else-if="canManageRule"
        @click="openBottomMenuFilter">
        fa-sliders-h
      </v-icon>
    </div>
    <v-bottom-sheet
      v-if="canManageRule"
      v-model="bottomMenu"
      class="pa-0">
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
  </div>
</template>

<script>

export default {
  props: {
    canManageRule: {
      type: Boolean,
      default: false,
    },
    keyword: {
      type: String,
      default: null,
    },
  },
  data: () => ({
    filterToChange: null,
    status: 'ENABLED',
    dateFilter: 'STARTED',
    bottomMenu: false,
    menuHeaderChanged: false,
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
    rulesDateFilter() {
      return [{
        text: this.$t('rules.filter.all'),
        value: 'ALL',
      },{
        text: this.$t('rules.filter.active'),
        value: 'STARTED',
      },{
        text: this.$t('rules.filter.upcoming'),
        value: 'NOT_STARTED',
      },{
        text: this.$t('rules.filter.ended'),
        value: 'ENDED',
      }];
    },
    isMobile() {
      return this.$vuetify.breakpoint.xs;
    }
  },
  watch: {
    keyword() {
      this.$emit('keyword-changed', this.keyword);
    },
    filter() {
      this.$emit('filter-changed', this.filter);
    },
  },
  methods: {
    applyFilter() {
      this.$emit('filter-changed', this.filter);
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
    changeHeaderMenu() {
      this.menuHeaderChanged = !this.menuHeaderChanged;
    }
  }
};
</script>
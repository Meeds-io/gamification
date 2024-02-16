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
        class="icon-default-color"
        @click="changeHeaderMenu">
        fas fa-arrow-left
      </v-icon>
      <v-btn
        v-else
        class="btn btn-primary"
        small
        @click="$root.$emit('rule-form-drawer', null, program)">
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
        prepend-inner-icon="fa-filter icon-default-color"
        clear-icon="fa-times fa-1x"
        class="pa-0 me-3 my-auto"
        clearable
        hide-details />
    </v-card>
    <v-spacer v-if="isMobile" />
    <v-scale-transition>
      <select
        id="rulesStatusFilter"
        v-model="filter"
        class="width-auto my-auto ignore-vuetify-classes d-none d-sm-inline">
        <option
          v-for="item in ruleFilters"
          :key="item.value"
          :value="item.value">
          {{ item.text }}
        </option>
      </select>
    </v-scale-transition>
    <div class="d-sm-none">
      <v-icon
        v-if="!menuHeaderChanged"
        class="icon-default-color"
        @click="changeHeaderMenu">
        fa-filter
      </v-icon>
      <v-icon
        v-else-if="canManageRule"
        class="icon-default-color"
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
            <v-icon class="icon-default-color">fa-filter</v-icon>
            {{ $t('programs.details.filter.cancel') }}
          </v-toolbar-title>
          <v-spacer />
          <v-btn text @click="changeFilterSelection">
            {{ $t('programs.details.filter') }}
          </v-btn>
        </v-toolbar>
        <v-list>
          <v-list-item
            v-for="item in ruleFilters"
            :key="item.value"
            @click="filterToChange = item.value">
            <v-list-item-title class="align-center d-flex">
              <v-icon v-if="filterToChange === item.value">fa-check</v-icon>
              <span v-else class="me-6"></span>
              <v-spacer />
              <div>
                {{ item.text }}
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
    program: {
      type: Object,
      default: null,
    },
  },
  data: () => ({
    filterToChange: null,
    filter: 'STARTED',
    bottomMenu: false,
    menuHeaderChanged: false,
  }),
  computed: {
    ruleFilters() {
      return this.canManageRule && this.adminRuleFilters || this.userRuleFilters;
    },
    adminRuleFilters() {
      return [{
        text: this.$t('rules.filter.all'),
        value: 'ALL',
      },{
        text: this.$t('rules.filter.active'),
        value: 'STARTED',
      },{
        text: this.$t('rules.filter.upcoming'),
        value: 'UPCOMING',
      },{
        text: this.$t('rules.filter.ended'),
        value: 'ENDED',
      },{
        text: this.$t('programs.details.filter.disabled'),
        value: 'DISABLED',
      }];
    },
    userRuleFilters() {
      return [{
        text: this.$t('rules.filter.all'),
        value: 'ALL',
      },{
        text: this.$t('rules.filter.active'),
        value: 'STARTED',
      },{
        text: this.$t('rules.filter.upcoming'),
        value: 'UPCOMING',
      },{
        text: this.$t('rules.filter.ended'),
        value: 'ENDED',
      }];
    },
    isMobile() {
      return this.$vuetify.breakpoint.sm;
    }
  },
  watch: {
    keyword() {
      this.$emit('keyword-changed', this.keyword);
    },
    filter() {
      const status = (this.filter === 'DISABLED' && 'DISABLED')
        || (this.canManageRule && this.filter === 'ALL' && 'ALL')
        || 'ENABLED';
      const dateFilter = this.filter === 'DISABLED' && 'ALL' || this.filter;
      this.$emit('filter-changed', status, dateFilter);
    },
  },
  methods: {
    openBottomMenuFilter() {
      this.filterToChange = this.filter;
      this.bottomMenu = true;
    },
    changeFilterSelection() {
      this.bottomMenu = false;
      this.filter = this.filterToChange;
    },
    changeHeaderMenu() {
      this.menuHeaderChanged = !this.menuHeaderChanged;
    }
  }
};
</script>

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
  <v-app
    class="Realizations border-box-sizing">
    <div class="d-flex px-7 pt-5" flat>
      <v-toolbar-title class="d-flex" v-if="!isMobile">
        <v-btn class="btn btn-primary export" @click="exportFile()">
          <span class="ms-2 d-none d-lg-inline">
            {{ $t("realization.label.export") }}
          </span>
        </v-btn>
      </v-toolbar-title>
      <v-spacer v-if="!isMobile" />
      <div class="mt-1 ml-n4 pe-3">
        <select-period
          v-model="selectedPeriod"
          :left="!isMobile"
          class="mx-2" />
      </div>
      <v-spacer v-if="isMobile" />
      <div>
        <v-btn
          class="btn px-2 mt-1 btn-primary filterTasksSetting"
          outlined
          @click="openRealizationsFilterDrawer">
          <i class="uiIcon uiIcon24x24 settingsIcon primary--text mr-1"></i>
          <span class="d-none font-weight-regular caption d-sm-inline">
            {{ $t('profile.label.search.openSearch') }}
          </span>
        </v-btn>
      </div>
    </div>
    <engagement-center-no-results
      v-if="!displaySearchResult"
      :info="$t('exoplatform.gamification.gamificationinformation.domain.search.noResults')"
      :info-message="$t('exoplatform.gamification.gamificationinformation.domain.search.noResultsMessage')" />
    <v-data-table
      v-if="displaySearchResult && !isMobile"
      :headers="realizationsHeaders"
      :items="realizationsToDisplay"
      :loading="loading"
      :sort-by.sync="sortBy"
      :sort-desc.sync="sortDescending"
      disable-pagination
      hide-default-footer
      must-sort
      class="mx-6 mt-6 realizationsTable">
      <template slot="item" slot-scope="props">
        <realization-item
          :realization="props.item"
          :date-format="dateFormat"
          :is-administrator="isAdministrator"
          :action-value-extensions="actionValueExtensions"
          @updated="realizationUpdated" />
      </template>
    </v-data-table>
    <v-card
      v-if="displaySearchResult && isMobile"
      flat
      width="auto"
      class="ms-3 me-7 mb-4">
      <v-select
        ref="select"
        class="pt-0"
        v-model="selected"
        :items="availableSortBy"
        :label="$t('realization.label.sortBy')" />
    </v-card>
    <template v-for="item in realizationsToDisplay">
      <realization-item-mobile
        :key="item.id"
        v-if="displaySearchResult && isMobile"
        :headers="realizationsHeaders"
        :realization="item"
        :is-administrator="isAdministrator" 
        :date-format="mobileDateFormat"
        :action-value-extensions="actionValueExtensions" />
    </template>
    <v-toolbar
      color="transparent"
      flat
      class="pa-2 mb-4">
      <v-btn
        v-if="hasMore"
        class="btn"
        :loading="loading"
        :disabled="loading"
        @click="loadMore"
        block>
        <span class="ms-2 d-inline">
          {{ $t("realization.label.loadMore") }}
        </span>
      </v-btn>
    </v-toolbar>
    <edit-realization-drawer
      ref="editRealizationDrawer"
      @updated="realizationUpdated" />
    <filter-realizations-drawer
      :is-administrator="isAdministrator"
      @selectionConfirmed="filterByPrograms" />
  </v-app>
</template>
<script>
export default {
  props: {
    earnerId: {
      type: Number,
      default: () => 0,
    },
    isAdministrator: {
      type: Boolean,
      default: false,
    },
    actionValueExtensions: {
      type: Object,
      default: function() {
        return null;
      },
    },
  },
  data: () => ({
    displaySearchResult: false,
    realizations: [],
    availableSortBy: [],
    searchList: [],
    earnerIds: [],
    offset: 0,
    limit: 25,
    pageSize: 25,
    totalSize: 0,
    loading: true,
    sortBy: 'date',
    sortDescending: true,
    limitReached: false,
    toDate: new Date().toISOString(),
    fromDate: null ,
    selectedPeriod: null,
    dateFormat: {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
    },
    mobileDateFormat: {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: 'numeric',
      minute: 'numeric',
    },
    isMobile: false,
    selected: 'Date',
  }),
  beforeDestroy () {
    if (typeof window === 'undefined') {return;}
    window.removeEventListener('resize', this.onResize, { passive: true });
  },
  mounted () {
    this.onResize();
    window.addEventListener('resize', this.onResize, { passive: true });
  },
  created() {
    this.realizationsHeaders.map((header) => {if (header.sortable && header.value !== 'type') {this.availableSortBy.push(header);}});
    // Workaround to fix closing menu when clicking outside
    $(document).mousedown(() => {
      if (this.$refs.select) {
        window.setTimeout(() => {
          this.$refs.select.blur();
        }, 200);
      }
    });
  },
  computed: {
    hasMore() {
      return this.limit < this.totalSize;
    },
    earnerIdToRetrieve() {
      return this.isAdministrator ?  this.earnerIds : [this.earnerId];
    },
    realizationsToDisplay() {
      return this.realizations.slice(0, this.limit);
    },
    realizationsHeaders() {
      const realizationsHeaders = [
        {
          text: this.$t('realization.label.actionLabel'),
          sortable: false,
          value: 'actionLabel',
          class: 'actionHeader',
          width: '188'
        },
        {
          text: this.$t('realization.label.programLabel'),
          sortable: false,
          value: 'programLabel',
          class: 'actionHeader',
          width: '110'
        },
        {
          text: this.$t('realization.label.date'),
          sortable: true,
          value: 'date',
          class: 'actionHeader',
          width: '120'
        },
        {
          text: this.$t('realization.label.actionType'),
          sortable: true,
          align: 'center',
          value: 'type',
          class: 'actionHeader',
          width: '85',
        },
        {
          text: this.$t('realization.label.points'),
          sortable: false,
          align: 'center',
          value: 'points',
          class: 'actionHeader',
          width: '80',
        },
        {
          text: this.$t('realization.label.status'),
          sortable: true,
          align: 'center',
          value: 'status',
          class: 'actionHeader',
          width: '95',
        },
      ];
      if (this.isAdministrator) {
        realizationsHeaders.push({
          text: this.$t('realization.label.actions'),
          sortable: false,
          class: 'actionHeader',
          width: '80',
        });
        realizationsHeaders.splice(3, 0,         
          {
            text: this.$t('realization.label.grantee'),
            sortable: false,
            align: 'center',
            value: 'grantee',
            class: 'actionHeader',
            width: '70',
          },);
      }
      return realizationsHeaders;
    },
  },
  watch: {
    selectedPeriod(newValue) {
      if (newValue) {
        this.fromDate = new Date(newValue.min).toISOString() ;
        this.toDate = new Date(newValue.max).toISOString();
        this.loadRealizations();
      }
    },
    sortBy(newVal, oldVal) {
      if (newVal !== oldVal) {
        if (this.sortDescending){
          this.sortDescending = false;
        }
        this.sortUpdated();
      }
    },
    sortDescending(newVal, oldVal) {
      if (newVal !== oldVal) {
        this.sortUpdated();
      }
    },
    selected(oldVal, newVal) {
      if (newVal !== oldVal) {
        this.sortBy = newVal;
      }
    },
    isMobile(newVal) {
      if (newVal) {
        this.limit = 9;
        this.pageSize = 9;
      } else {
        this.limit = 25;
        this.pageSize = 25;
      }
    }
  },
  methods: {
    sortUpdated() {
      if (!this.loading) {
        this.loading = true;
        this.loadRealizations();
      }
    },
    loadMore() {
      this.limit += this.pageSize;
      return this.loadRealizations();
    },
    loadRealizations() {
      this.loading = true;
      return this.getRealizations()
        .finally(() => {
          this.loading = false;
          this.$root.$applicationLoaded();
        });
    },
    getRealizations() {
      return this.$realizationsServices.getAllRealizations(this.fromDate, this.toDate, this.earnerIdToRetrieve, this.sortBy, this.sortDescending, this.offset, this.limit + 1, this.searchList)
        .then(realizations => {
          this.realizations = realizations?.realizations || [];
          this.totalSize = realizations?.size || this.totalSize;
          this.displaySearchResult = this.searchList?.length >= 0 && this.realizations.length > 0;
        });
    },
    exportFile() {
      return this.$realizationsServices.exportFile(this.fromDate, this.toDate, this.earnerIdToRetrieve);
    },
    realizationUpdated(updatedRealization){
      const index = this.realizations && this.realizations.findIndex((realization) => { return  realization.id === updatedRealization.id;});
      this.realizations[index] = updatedRealization;
      this.$set(this.realizations,index,updatedRealization);
    },
    openRealizationsFilterDrawer() {
      this.$root.$emit('realization-open-filter-drawer');
    },
    filterByPrograms(programs, grantees) {
      this.searchList = programs.map(program => program.id);
      this.earnerIds = grantees.map(grantee => grantee.identity.identityId);
      this.loadRealizations();
    },
    onResize () {
      this.isMobile = window.innerWidth < 1020;
    },
  }
};
</script>

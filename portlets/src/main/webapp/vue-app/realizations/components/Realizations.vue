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
  <div class="Realizations border-box-sizing">
    <v-toolbar class="z-index-one px-2" flat>
      <div class="d-flex flex-grow-1 align-center content-box-sizing">
        <template v-if="!isMobile">
          <realizations-export-button :link="exportFileLink" />
          <v-spacer />
          <template v-if="isRealizationManager">
            <realizations-owner-switch-buttons
              id="realizationAdministrationSwitch"
              v-model="administrationMode"
              class="mx-auto" />
            <v-spacer />
          </template>
        </template>
        <div class="ml-n4 pe-3 d-flex align-center">
          <select-period
            v-model="selectedPeriod"
            :left="!isMobile"
            class="mx-2" />
        </div>
        <v-spacer v-if="isMobile" />
        <v-btn
          color="primary"
          text
          class="primary-border-color px-2"
          @click="openRealizationsFilterDrawer">
          <v-icon size="16">fas fa-sliders-h</v-icon>
          <span class="d-none d-sm-inline font-weight-regular caption ms-2">
            {{ $t('profile.label.search.openSearch') }}
          </span>
        </v-btn>
      </div>
    </v-toolbar>
    <v-progress-linear
      v-if="!initialized"
      indeterminate
      height="2"
      color="primary" />
    <div v-else-if="!displaySearchResult">
      <engagement-center-result-not-found 
        v-if="filterActivated"
        :display-back-arrow="false"
        :message-info-one="$t('challenge.realization.noFilterResult.messageOne')"
        :message-info-two="$t('challenge.realization.noFilterResult.messageTwo')"
        :button-text="$t('challenge.button.resetFilter')"
        @button-event="reset" />
      <engagement-center-result-not-found 
        v-else
        :display-back-arrow="false"
        :message-title="$t('actions.welcomeMessage')"
        :message-info-one="$t('challenge.realization.noPeriodResult.messageOne')"
        :message-info-two="$t('challenge.realization.noPeriodResult.messageTwo')"
        :button-text="$t('programs.details.programDeleted.explore')"
        :button-url="programsUrl" />
    </div>
    <v-data-table
      v-else-if="displaySearchResult && !isMobile"
      :headers="realizationsHeaders"
      :items="realizationsToDisplay"
      :sort-by.sync="sortBy"
      :sort-desc.sync="sortDescending"
      :loading="loading"
      disable-pagination
      hide-default-footer
      must-sort
      class="mx-6 mt-6 realizationsTable">
      <template slot="item" slot-scope="props">
        <realization-item
          :realization="props.item"
          :date-format="dateFormat"
          :is-administrator="administrationMode"
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
        :is-administrator="administrationMode" 
        :date-format="mobileDateFormat"
        :action-value-extensions="actionValueExtensions" />
    </template>
    <v-toolbar
      color="transparent"
      flat
      class="pa-2 mb-4">
      <v-btn
        v-if="hasMore && displaySearchResult"
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
    <filter-realizations-drawer
      ref="filterRealizationDrawer"
      :is-administrator="administrationMode"
      :administration-mode="administrationMode"
      @selectionConfirmed="filterByPrograms" />
  </div>
</template>
<script>
export default {
  props: {
    earnerId: {
      type: Number,
      default: () => 0,
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
    ownedPrograms: [],
    earnerIds: [],
    offset: 0,
    limit: 25,
    pageSize: 25,
    totalSize: 0,
    loading: true,
    initialized: false,
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
    filterActivated: false,
    selected: 'Date',
    programsUrl: `${eXo.env.portal.context}/${eXo.env.portal.portalName}/contributions/programs`,
    administrationMode: false,
    isRealizationManager: false,
  }),
  computed: {
    hasMore() {
      return this.limit < this.totalSize;
    },
    earnerIdToRetrieve() {
      return this.earnerIds?.length && this.earnerIds || (!this.administrationMode && [this.earnerId] || null);
    },
    realizationsToDisplay() {
      return this.realizations.slice(0, this.limit);
    },
    realizationsFilter() {
      return {
        fromDate: this.fromDate,
        toDate: this.toDate,
        earnerIds: this.earnerIdToRetrieve,
        sortBy: this.sortBy,
        sortDescending: this.sortDescending,
        programIds: this.searchList,
        owned: this.administrationMode,
      };
    },
    exportFileLink() {
      return this.$realizationService.getRealizationsExportLink(this.realizationsFilter);
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
      if (this.administrationMode) {
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
        this.filterActivated = false;
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
    },
    administrationMode() {
      this.realizations = [];
      this.loadRealizations();
    },
    loading() {
      if (this.loading) {
        document.dispatchEvent(new CustomEvent('displayTopBarLoading'));
      } else {
        document.dispatchEvent(new CustomEvent('hideTopBarLoading'));
      }
    },
  },
  created() {
    this.realizationsHeaders.map((header) => {
      if (header.sortable && header.value !== 'type') {
        this.availableSortBy.push(header);
      }
    });
    this.$realizationService.isRealizationManager()
      .then(manager => this.isRealizationManager = manager);
    // Workaround to fix closing menu when clicking outside
    $(document).mousedown(() => {
      if (this.$refs.select) {
        window.setTimeout(() => {
          this.$refs.select.blur();
        }, 200);
      }
    });
  },
  mounted () {
    this.onResize();
    window.addEventListener('resize', this.onResize, { passive: true });
  },
  beforeDestroy () {
    if (typeof window === 'undefined') {return;}
    window.removeEventListener('resize', this.onResize, { passive: true });
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
      if (this.filterActivated && this.searchList.length === 0 && this.administrationMode) {
        this.searchList = this.ownedPrograms;
      }
      return this.getRealizations()
        .finally(() => {
          this.loading = false;
          this.initialized = true;
          this.$root.$applicationLoaded();
        });
    },
    getRealizations() {
      return this.$realizationService.getRealizations(Object.assign({
        offset: this.offset,
        limit: this.limit,
        returnSize: true,
      }, this.realizationsFilter))
        .then(realizations => {
          this.realizations = realizations?.realizations || [];
          this.totalSize = realizations?.size || this.totalSize;
          this.displaySearchResult = this.searchList?.length >= 0 && this.realizations.length > 0;
        });
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
      this.filterActivated = true;
      this.searchList = programs.map(program => program.id);
      this.earnerIds = grantees.map(grantee => grantee.identity.identityId);
      this.loadRealizations();
    },
    onResize () {
      this.isMobile = window.innerWidth < 1020;
    },
    reset() {
      this.searchList = [];
      this.earnerIds = [];
      this.loadRealizations();
      this.$root.$emit('reset-filter-values');
    }
  }
};
</script>

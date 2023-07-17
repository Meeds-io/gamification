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
  <v-app>
    <main
      id="rulesList"
      :class="classWelcomeMessage"
      class="Realizations border-box-sizing"
      role="main">
      <application-toolbar
        :center-button-toggle="isProgramManager && !isMobile && {
          selected: tabName,
          hide: false,
          buttons: [{
            value: 'YOURS',
            text: $t('gamification.achievement.yours'),
            icon: 'fa-user',
          }, {
            value: 'OWNED',
            text: $t('gamification.achievement.owned'),
            icon: 'fa-users-cog',
          }]
        }"
        :right-filter-button="{
          text: $t('profile.label.search.openSearch'),
        }"
        :filters-count="filtersCount"
        hide-cone-button
        @toggle-select="tabName = $event"
        @filter-button-click="$root.$emit('realization-open-filter-drawer')">
        <template v-if="!$vuetify.breakpoint.mobile" #left>
          <engagement-center-realizations-export-button :link="exportFileLink" />
        </template>
        <template #right>
          <engagement-center-realizations-select-period
            v-model="selectedPeriod"
            :left="!$vuetify.breakpoint.mobile"
            class="mx-2" />
        </template>
      </application-toolbar>
  
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
          :message-title="$t('appCenter.welcomeMessage')"
          :message-info-one="$t('challenge.realization.noPeriodResult.messageOne')"
          :message-info-two="$t('challenge.realization.noPeriodResult.messageTwo')"
          :button-text="$t('programs.details.programDeleted.explore')"
          :button-url="programsUrl" />
      </div>
      <v-data-table
        v-else-if="initialized && displaySearchResult && !isMobile"
        :headers="realizationsHeaders"
        :items="realizationsToDisplay"
        :sort-by.sync="sortBy"
        :sort-desc.sync="sortDescending"
        disable-pagination
        hide-default-footer
        must-sort
        class="mx-3 mt-2 realizationsTable">
        <template slot="item" slot-scope="props">
          <engagement-center-realization-item
            :realization="props.item"
            :date-format="dateFormat"
            :is-administrator="administrationMode"
            @updated="realizationUpdated" />
        </template>
      </v-data-table>
      <v-card
        v-if="displaySearchResult && isMobile"
        flat
        width="auto"
        class="px-4 mb-4">
        <v-select
          ref="select"
          class="pt-0"
          v-model="selected"
          :items="availableSortBy"
          :label="$t('realization.label.sortBy')" />
      </v-card>
      <template v-for="item in realizationsToDisplay">
        <engagement-center-realization-item-mobile
          :key="item.id"
          v-if="displaySearchResult && isMobile"
          :headers="realizationsHeaders"
          :realization="item"
          :is-administrator="administrationMode" 
          :date-format="mobileDateFormat" />
      </template>
      <v-toolbar
        color="transparent"
        flat>
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
      <engagement-center-realizations-filter-drawer
        ref="filterRealizationDrawer"
        :is-administrator="administrationMode"
        :administration-mode="administrationMode"
        @selectionConfirmed="filterByPrograms" />
    </main>
    <engagement-center-rule-extensions />
  </v-app>
</template>
<script>
export default {
  props: {
    isProgramManager: {
      type: Boolean,
      default: false,
    },
    earnerId: {
      type: Number,
      default: () => eXo.env.portal.userIdentityId,
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
    tabName: window.location.hash === '#hosted' ? 'OWNED' : 'YOURS',
  }),
  computed: {
    administrationMode() {
      return this.tabName === 'OWNED';
    },
    filtersCount() {
      return (this.searchList?.length && 1 || 0)
        + (this.earnerIds?.length && 1 || 0);
    },
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
          value: 'date',
          sortable: true,
          class: 'actionHeader',
          width: '120'
        },
        {
          text: this.$t('realization.label.actionType'),
          value: 'type',
          sortable: true,
          align: 'center',
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
          value: 'status',
          sortable: true,
          align: 'center',
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
            value: 'grantee',
            text: this.$t('realization.label.grantee'),
            sortable: false,
            align: 'center',
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
        this.realizations = [];
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
    tabName() {
      if (this.tabName === 'YOURS') {
        window.location.hash = '#yours';
      } else if (this.tabName === 'OWNED') {
        window.location.hash = '#hosted';
      }
    },
  },
  created() {
    this.realizationsHeaders.map((header) => {
      if (header.sortable && header.value !== 'type') {
        this.availableSortBy.push(header);
      }
    });
    if (this.administrationMode && !this.isProgramManager) {
      this.tabName = 'YOURS';
    } else if (this.selectedPeriod) {
      this.loadRealizations();
    }
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
        this.realizations = [];
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
    filterByPrograms(programs, grantees) {
      this.filterActivated = true;
      this.initialized = false;
      this.realizations = [];
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
      this.realizations = [];
      this.loadRealizations();
      this.$root.$emit('reset-filter-values');
    }
  }
};
</script>

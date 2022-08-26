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
    <v-toolbar
      color="transparent"
      flat
      class="pa-4 mb-4">
      <div class="border-box-sizing clickable">
        <v-btn class="btn btn-primary export" @click="exportFile()">
          <span class="ms-2 d-none d-lg-inline">
            {{ $t("realization.label.export") }}
          </span>
        </v-btn>
      </div>
      <div class="selected-period-menu mt-6 px-3">
        <select-period v-model="selectedPeriod" class="mx-2" />
      </div>
    </v-toolbar>
    <v-data-table
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
          @updated="realizationUpdated" />
      </template>
    </v-data-table>
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
        <span class="ms-2 d-none d-lg-inline">
          {{ $t("realization.label.loadMore") }}
        </span>
      </v-btn>
    </v-toolbar>
    <edit-realization-drawer
      ref="editRealizationDrawer"
      @updated="realizationUpdated" />
  </v-app>
</template>
<script>
export default {
  data: () => ({
    realizations: [],
    offset: 0,
    limit: 10,
    pageSize: 10,
    loading: true,
    sortBy: 'date',
    sortDescending: true,
    limitReached: false,
    toDate: new Date().toISOString(),
    fromDate: null ,
    selectedPeriod: null,
    dateFormat: {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
      hour: 'numeric',
      minute: 'numeric',
    },
  }),
  computed: {
    hasMore() {
      return this.limit <= this.realizations.length;
    },
    realizationsToDisplay() {
      return this.realizations.slice(0, this.limit);
    },
    realizationsHeaders() {
      return [
        {
          text: this.$t('realization.label.date'),
          align: 'center',
          sortable: true,
          value: 'date',
          class: 'actionHeader px-2',
        },
        {
          text: this.$t('realization.label.grantee'),
          align: 'center',
          sortable: false,
          value: 'grantee',
          class: 'actionHeader px-1'
        },
        {
          text: this.$t('realization.label.actionLabel'),
          align: 'center',
          sortable: false,
          value: 'actionLabel',
          class: 'actionHeader px-1'
        },
        {
          text: this.$t('realization.label.actionType'),
          align: 'center',
          sortable: true,
          value: 'actionType',
          class: 'actionHeader px-1'
        },
        {
          text: this.$t('realization.label.programLabel'),
          align: 'center',
          sortable: false,
          value: 'programLabel',
          class: 'actionHeader px-0'
        },
        {
          text: this.$t('realization.label.points'),
          align: 'center',
          sortable: false,
          value: 'points',
          class: 'actionHeader px-1'
        },
        {
          text: this.$t('realization.label.status'),
          align: 'center',
          sortable: false,
          value: 'status',
          class: 'actionHeader px-1'
        },
        {
          text: this.$t('realization.label.actions'),
          align: 'center',
          sortable: false,
          value: '',
          class: 'actionHeader px-2'
        },
      ];
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
      return this.$realizationsServices.getAllRealizations(this.fromDate, this.toDate, this.sortBy, this.sortDescending, this.offset, this.limit + 1)
        .then(realizations => {
          this.realizations = realizations || [];
        });
    },
    exportFile() {
      return this.$realizationsServices.exportFile(this.fromDate, this.toDate);
    },
    realizationUpdated(updatedRealization){
      const index = this.realizations && this.realizations.findIndex((realization) => { return  realization.id === updatedRealization.id;});
      this.realizations[index] = updatedRealization;
      this.$set(this.realizations,index,updatedRealization);
    }
  }
};
</script>

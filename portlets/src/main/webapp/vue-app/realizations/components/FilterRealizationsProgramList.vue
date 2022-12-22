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
  <v-list>
    <v-list-item class="d-flex align-start">
      <v-list-item-content class="align-content-center">
        <v-checkbox
          v-model="selectAll"
          :indeterminate="partiallySelected"
          color="primary"
          class="pt-2" />
        <v-text-field
          id="EngagementCenterApplicationSearchFilter"
          v-model="search"
          :placeholder="$t('realization.label.filterByProgram')"
          prepend-inner-icon="fa-filter"
          single-line
          hide-details
          class="pa-0 mx-3 " />
      </v-list-item-content>
    </v-list-item>
    <v-list-item>
      <v-list-item-content class="flex-column d-flex align-start">
        <v-checkbox   
          v-for="program in programs"
          v-model="selected"
          class="pt-2 pb-2 justify-content-start"
          :key="Object.keys(program)"
          :label="Object.values(program)"  
          :value="Object.keys(program)"
          v-bind="$attrs"
          @click="filterByProgram" />
      </v-list-item-content>
    </v-list-item>
  </v-list>
</template>

<script>
export default {
  data: () => ({
    selectionType: 'all',
    selectAll: true,
    loading: false,
    limit: 20,
    pageSize: 20,
    totalSize: 0,
    selected: [],
    numberOfPrograms: 7,
    search: '',
    startSearchAfterInMilliseconds: 600,
    endTypingKeywordTimeout: 50,
    startTypingKeywordTimeout: 0,
    typing: false,
    programsList: [],
    loadedMoreProgramsList: [],
    type: 'ALL',
    status: 'ALL',
    searchingKey: '',
    size: 0,
    noProgramsFound: false,
  }),
  created() {
    this.retrievePrograms(false, '');
    this.$root.$on('reset-selection', this.reset);
  },
  watch: {
    selected(oldVal, newVal) {
      if ( newVal.length === 0 || (oldVal.length === 0 && newVal.length !== 0)) {
        this.$emit('empty-list', newVal.length === 0);
      }
      this.$emit('selected-programs', this.selectedPrograms);
    },
    selectAll(newVal) {
      this.selected =  newVal ? this.programsList.map( Object.keys ) : [];
    },    
    search()  {
      this.startTypingKeywordTimeout = Date.now() + this.startSearchAfterInMilliseconds;
      if (!this.typing) {
        this.typing = true;
        this.waitForEndTyping();
      }
    },
  },
  computed: {
    programs() {
      return this.programsList;
    },
    hasMore() {
      return this.size > this.programsList?.length ;
    },
    selectedPrograms() {
      return  (this.selectAll && !this.partiallySelected && this.search === '') ? [] : this.selected ;
    },
    partiallySelected () {
      return this.programsList && this.programsList.length && (this.hasMore || this.selected.length !== this.programsList.length);
    },
  },
  methods: {
    reset() {
      this.search = '';
      this.selectAll = true;
      this.selected = this.programsList.map(Object.keys);
    },
    waitForEndTyping() {
      window.setTimeout(() => {
        if (Date.now() > this.startTypingKeywordTimeout) {
          this.typing = false;
          this.retrievePrograms(false, this.search);
          this.noProgramsFound = true;
          this.selected = [];
        } else {
          this.waitForEndTyping();
        }
      }, this.endTypingKeywordTimeout);
    },
    retrievePrograms(append, searchingKey) {
      this.loading = true;
      this.$programsServices
        .retrievePrograms(null, null, this.type, this.status, searchingKey, true)
        .then((programsList) => {
          this.size = programsList.domainsSize;
          if (append) {
            this.loadedMoreProgramsList = programsList?.domains.map(program => ({[program.id]: program.title}));
            this.programsList = this.programsList?.concat(this.loadedMoreProgramsList);
          } else {
            this.programsList = programsList?.domains.map( program => ({[program.id]: program.title}));
            this.loadedMoreProgramsList = this.programsList;
          }
          if (this.selectAll) {
            this.selected = this.selected.concat(this.loadedMoreProgramsList.map(Object.keys));
          }
        }
        )
        .finally(() => this.loading = false);},
  },
};
</script>
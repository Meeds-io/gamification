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
          class="pt-2"
          @click="changeAllSelection" />
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

    <v-flex v-if="hasMore" class=" d-flex my-4 border-box-sizing">
      <v-btn
        :loading="loading"
        :disabled="loading"
        class="btn mx-auto"
        @click="loadMore">
        {{ $t('realization.label.loadMore') }}
      </v-btn>
    </v-flex>
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
    partiallySelected: false,
    selected: [],
    numberOfPrograms: 5,
    search: '',
    startSearchAfterInMilliseconds: 600,
    endTypingKeywordTimeout: 50,
    startTypingKeywordTimeout: 0,
    typing: false,
    programsList: [[],[]],
    status: 'ALL',
    searchingKey: '',
    size: 0,
  }),
  created() {
    this.retrievePrograms(false, '');
    this.$root.$on('reset-selection', this.reset);
  },
  watch: {
    selected() {
      this.partiallySelected = this.selected.length !== this.programsList.length;
      this.$emit('selected-programs', this.selectedPrograms);
    },
    selectAll(newVal) {
      return newVal ? this.selected = this.programsList.map( Object.keys ) : this.selected = [];
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
  },
  methods: {
    loadMore() {
      this.retrievePrograms(true ,this.search);
    },
    reset() {
      this.selected = [];
    },
    waitForEndTyping() {
      window.setTimeout(() => {
        if (Date.now() > this.startTypingKeywordTimeout) {
          this.typing = false;
          this.retrievePrograms(false, this.search);
          this.selected = [];
        } else {
          this.waitForEndTyping();
        }
      }, this.endTypingKeywordTimeout);
    },
    retrievePrograms(append, searchingKey) {
      this.loading = true;
      const offset = append && this.programsList?.domains?.length || 0;
      const returnSize = append ?  false : true;
      this.$programsServices
        .retrievePrograms(offset, this.numberOfPrograms, this.type, this.status, returnSize, searchingKey)
        .then((programsList) => {
          this.size = programsList.domainsSize;
          this.numberOfPrograms += 5;
          this.programsList = programsList?.domains.map( program => ({ [program.id]: program.title }));
          if (this.selectAll || !append) {
            this.selected = this.programsList.map( Object.keys );}
        }
        )
        .finally(() => this.loading = false);},
  },
};
</script>
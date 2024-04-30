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
  <v-flex :id="id">
    <v-autocomplete
      ref="selectAutoComplete"
      v-model="value"
      :label="labels.label"
      :placeholder="labels.placeholder"
      :items="programItems"
      :loading="loadingSuggestions"
      :multiple="multiple"
      :hide-no-data="hideNoData"
      append-icon=""
      menu-props="closeOnClick, closeOnContentClick, maxHeight = 100"
      class="identitySuggester identitySuggesterInputStyle my-0"
      content-class="identitySuggesterContent"
      width="100%"
      max-width="100%"
      item-text="title"
      item-value="id"
      return-object
      persistent-hint
      hide-selected
      cache-items
      chips
      dense
      flat
      required
      @update:search-input="searchTerm = $event">
      <template slot="no-data">
        <v-list-item class="pa-0">
          <v-list-item-title
            v-if="displaySearchPlaceHolder"
            class="px-2">
            {{ labels.searchPlaceholder }}
          </v-list-item-title>
        </v-list-item>
      </template>
      <template slot="selection" slot-scope="{item}">
        <gamification-program-item :program="item" @remove-attendee="remove(item)" />
      </template>
      <template slot="item" slot-scope="data">
        <v-list-item-title
          class="text-truncate identitySuggestionMenuItemText"
          v-text="data.item.title" />
      </template>
    </v-autocomplete>
  </v-flex>
</template>
<script>

export default {
  props: {
    multiple: {
      type: Boolean,
      default: function() {
        return false;
      },
    },
    onlyOwned: {
      type: Boolean,
      default: false,
    },
    value: {
      type: Object,
      default: null,
    },
    excludedIds: {
      type: Array,
      default: null,
    },
    labels: {
      type: Object,
      default: () => ({
        label: '',
        placeholder: '',
        searchPlaceholder: '',
        noDataLabel: '',
      }),
    },
    includeDeleted: {
      type: Boolean,
      default: function() {
        return false;
      },
    },
    includeDisabled: {
      type: Boolean,
      default: function() {
        return false;
      },
    },
  },
  data() {
    return {
      id: `ProgramSuggester${parseInt(Math.random() * 10000)}`,
      programs: [],
      searchTerm: null,
      searchStarted: false,
      loadingSuggestions: false,
      broadcast: true,
      startSearchAfterInMilliseconds: 300,
      endTypingKeywordTimeout: 50,
      startTypingKeywordTimeout: 0,
      typing: false,
    };
  },
  computed: {
    displaySearchPlaceHolder() {
      return this.labels.searchPlaceholder && !this.searchStarted;
    },
    hideNoData() {
      return !this.searchStarted && this.programs.length === 0;
    },
    programItems() {
      return this.programs && this.excludedIds?.length && this.programs.filter(r => !this.excludedIds.find(id => id === r.id)) || this.programs || [];
    },
  },
  watch: {
    loadingSuggestions() {
      if (this.loadingSuggestions && !this.searchStarted) {
        this.searchStarted = true;
      }
    },
    searchTerm() {
      this.startTypingKeywordTimeout = Date.now() + this.startSearchAfterInMilliseconds;
      if (!this.typing) {
        this.typing = true;
        this.waitForEndTyping();
      }
    },
    value() {
      this.$emit('input', this.value);
      this.init();
    },
    includeDeleted() {
      this.$refs.selectAutoComplete.cachedItems = [];
    },
  },
  mounted() {
    $(`#${this.id} input`).on('blur', () => {
      this.$refs.selectAutoComplete.isFocused = false;
    });
  },
  methods: {
    init() {
      if (this.value && this.value.length) {
        this.value.forEach(item => {
          if (item.id) {
            this.programs.push(item);
          }
        });
      } else if (this.value && this.value.id){
        this.programs = [this.value];
      }
    },
    remove(item) {
      if (this.value) {
        if (this.value.splice) {
          const index = this.value.findIndex(val => val.id === item.id);
          if (index >= 0){
            this.value.splice(index, 1);
          }
        } else {
          this.value = null;
        }
      }
      this.$emit('removeProgram',item);
    },
    waitForEndTyping() {
      window.setTimeout(() => {
        if (Date.now() > this.startTypingKeywordTimeout) {
          this.typing = false;
          this.searchPrograms();
        } else {
          this.waitForEndTyping();
        }
      }, this.endTypingKeywordTimeout);
    },
    focus() {
      this.$refs.selectAutoComplete.focus();
    },
    searchPrograms() {
      if (this.searchTerm?.length) {
        this.focus();
        if (!this.previousSearchTerm || this.previousSearchTerm !== this.searchTerm) {
          this.loadingSuggestions = true;
          this.programs = [];
          this.$programService.getPrograms({
            limit: 10,
            type: 'ALL',
            status: this.includeDisabled ? 'ALL' : 'ENABLED',
            query: this.searchTerm,
            includeDeleted: this.includeDeleted,
            owned: this.onlyOwned,
            lang: eXo.env.portal.language,
          })
            .then(data => this.programs = data.programs)
            .finally(() => this.loadingSuggestions = false);
        }
        this.previousSearchTerm = this.searchTerm;
      } else {
        this.searchStarted = false;
      }
    },
    clear() {
      this.programs = [];
      this.value = null;
      this.loadingSuggestions = false;
      this.$refs.selectAutoComplete.cachedItems = [];
    },
  },
};
</script>
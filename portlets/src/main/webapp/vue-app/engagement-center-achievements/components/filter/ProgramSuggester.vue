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
      v-model="program"
      :label="labels.label"
      :placeholder="labels.placeholder"
      :items="programItems"
      :loading="loadingSuggestions"
      :multiple="multiple"
      hide-no-data
      append-icon=""
      menu-props="closeOnClick, closeOnContentClick, maxHeight = 100"
      class="identitySuggester identitySuggesterInputStyle"
      content-class="identitySuggesterContent"
      width="100%"
      max-width="100%"
      item-text="title"
      item-value="id"
      return-object
      persistent-hint
      hide-selected
      chips
      dense
      flat
      required
      @update:search-input="searchTerm = $event"
      attach>
      <template slot="no-data">
        <v-list-item class="pa-0">
          <v-list-item-title
            class="px-2">
            {{ labels.searchPlaceholder }}
          </v-list-item-title>
        </v-list-item>
      </template>

      <template slot="selection" slot-scope="{item, selected}">
        <v-chip
          :input-value="selected"
          :close="true"
          class="identitySuggesterItem"
          @click:close="program = null">
          <span class="text-truncate">
            {{ item.title }}
          </span>
        </v-chip>
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
      program: null,
      searchTerm: null,
      loadingSuggestions: false,
      broadcast: true,
      startSearchAfterInMilliseconds: 300,
      endTypingKeywordTimeout: 50,
      startTypingKeywordTimeout: 0,
      typing: false,
    };
  },
  computed: {
    programItems() {
      return this.programs && this.excludedIds?.length && this.programs.filter(r => !this.excludedIds.find(id => id === r.id)) || this.programs || [];
    },
  },
  watch: {
    searchTerm() {
      this.startTypingKeywordTimeout = Date.now() + this.startSearchAfterInMilliseconds;
      if (!this.typing) {
        this.typing = true;
        this.waitForEndTyping();
      }
    },
    program() {
      if (this.program !== this.value) {
        this.$emit('input', this.program);
      }
    },
    value() {
      if (!this.programs.length && this.value) {
        this.programs.push(this.value);
      }
    },
    includeDeleted() {
      this.$refs.selectAutoComplete.cachedItems = [];
    },
  },
  created() {
    this.program = this.value;
    if (this.program) {
      this.programs = [this.program];
    }
  },
  mounted() {
    $(`#${this.id} input`).on('blur', () => {
      this.$refs.selectAutoComplete.isFocused = false;
    });
  },
  methods: {
    remove(item) {
      this.value = null;
      this.$emit('removeProgram', item.title);
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
        this.programs = [];
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
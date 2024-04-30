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
  <v-flex :id="id">
    <v-autocomplete
      ref="selectAutoComplete"
      v-model="value"
      :label="labels.label"
      :placeholder="labels.placeholder"
      :items="ruleItems"
      :loading="!!loadingSuggestions"
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
      cache-items
      return-object
      persistent-hint
      hide-selected
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
        <gamification-rule-item :rule="item" @remove="remove(item)" />
      </template>
      <template slot="item" slot-scope="{ item }">
        <v-list-item-title class="text-truncate identitySuggestionMenuItemText">
          {{ item.title }}
        </v-list-item-title>
      </template>
    </v-autocomplete>
  </v-flex>
</template>
<script>

export default {
  props: {
    value: {
      type: Object,
      default: null,
    },
    programId: {
      type: String,
      default: null,
    },
    excludedIds: {
      type: Array,
      default: null,
    },
    multiple: {
      type: Boolean,
      default: false,
    },
    excludePrerequisites: {
      type: Boolean,
      default: false,
    },
    includeDeleted: {
      type: Boolean,
      default: function() {
        return false;
      },
    },
    labels: {
      type: Object,
      default: () => ({
        label: null,
        placeholder: this.$t('rules.label.placeholder'),
        noDataLabel: this.$t('rules.label.noDataLabel'),
        searchPlaceholder: null,
      }),
    },
  },
  data() {
    return {
      id: `RuleSuggester${parseInt(Math.random() * 10000)}`,
      rules: [],
      searchTerm: null,
      loadingSuggestions: 0,
      searchStarted: false,
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
      return !this.searchStarted && this.rules.length === 0;
    },
    ruleItems() {
      return this.rules && this.excludedIds?.length && this.rules?.filter(r => !this.excludedIds.find(id => id === r.id)) || this.rules || [];
    },
  },
  watch: {
    loadingSuggestions() {
      if (this.loadingSuggestions > 0 && !this.searchStarted) {
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
            this.rules.push(item);
          }
        });
      } else if (this.value && this.value.id){
        this.rules = [this.value];
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
      this.$emit('removeRule',item);
    },
    waitForEndTyping() {
      window.setTimeout(() => {
        if (Date.now() > this.startTypingKeywordTimeout) {
          this.typing = false;
          this.searchRules();
        } else {
          this.waitForEndTyping();
        }
      }, this.endTypingKeywordTimeout);
    },
    focus() {
      this.$refs.selectAutoComplete.focus();
    },
    searchRules() {
      if (this.searchTerm && this.searchTerm.length) {
        this.focus();
        if (!this.previousSearchTerm || this.previousSearchTerm !== this.searchTerm) {
          this.retrieveRules();
        }
        this.previousSearchTerm = this.searchTerm;
      } else {
        this.searchStarted = false;
        this.rules = [];
      }
    },
    retrieveRules(limit) {
      this.loadingSuggestions++;
      this.rules = [];
      return this.$ruleService.getRules({
        term: this.searchTerm || null,
        programId: this.programId,
        excludedRuleIds: this.excludedIds || [],
        includeDeleted: this.includeDeleted,
        dateFilter: this.includeDeleted && 'ALL' || 'ACTIVE',
        status: this.includeDeleted && 'ALL' || 'ENABLED',
        programStatus: this.includeDeleted && 'ALL' || 'ENABLED',
        offset: 0,
        limit: limit || 20,
        returnSize: false,
        lang: eXo.env.portal.language,
      })
        .then(data => {
          this.rules = this.excludePrerequisites
              && this.excludedIds?.length
              && data.rules?.filter(r => !r.prerequisiteRules?.find(p => p.id === this.excludedIds[0]))
              || data.rules;
        })
        .finally(() => this.loadingSuggestions--);
    },
  },
};
</script>
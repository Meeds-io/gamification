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
      :rules="rules"
      :items="domains"
      :loading="loadingSuggestions"
      :prepend-inner-icon="prependInnerIcon"
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
      cache-items
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
          @click:close="remove(item)">
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
    canAddChallenge: {
      type: Boolean,
      default: false
    },
  },
  data() {
    return {
      id: `AutoComplete${parseInt(Math.random() * 10000)}`,
      domains: [],
      searchTerm: null,
      program: null,
      loadingSuggestions: false,
      broadcast: true,
    };
  },
  computed: {
    labels() {
      return {
        searchPlaceholder: this.$t('challenges.programSuggester.searchPlaceholder'),
        placeholder: this.$t('challenges.programSuggester.placeholder'),
        noDataLabel: this.$t('challenges.programSuggester.noDataLabel'),
      };
    },
  },
  watch: {
    searchTerm(value) {
      this.loadingSuggestions = true;
      let domains = this.domains.slice();
      if (value && value.trim().length) {
        const searchTerm = value.trim().toLowerCase();
        domains = domains.slice().filter(domain => (domain.title && domain.title.toLowerCase().indexOf(searchTerm) >= 0 || domain.description.toLowerCase().indexOf(searchTerm)) >= 0 );
      }
      this.loadingSuggestions = false;
      return domains;
    },
    program() {
      if (this.program && this.broadcast){
        this.$emit('addProgram',this.program.title);
      } else if (!this.broadcast) {
        this.broadcast = true;
      }
    },
  },
  mounted() {
    $(`#${this.id} input`).on('blur', () => {
      this.$refs.selectAutoComplete.isFocused = false;
    });
  },
  created() {
    if (this.canAddChallenge) {
      this.getAllDomains();
    }
  },
  methods: {
    getAllDomains() {
      this.$challengesServices.getAllDomains()
        .then(domains => this.domains =  domains.slice().filter(domain => domain.enabled));
    },
    remove(item) {
      this.program = null;
      this.$emit('removeProgram', item.title);
    },
  },
};
</script>
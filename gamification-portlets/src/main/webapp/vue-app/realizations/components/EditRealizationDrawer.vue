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
  <exo-drawer
    ref="editRealizationDrawer"
    class="editRealizationDrawer"
    :right="!$vuetify.rtl"
    @closed="close"
    eager>
    <template slot="title">
      <span class="pb-2"> {{ $t('realization.label.editRealization') }} </span>
    </template>
    <template slot="content">
      <v-card-text>
        <div class="mb-2">
          <span class="subtitle-1 labels"> {{ $t('challenges.label.reward') }}</span>
          <v-text-field
            v-model="points"
            :label="$t('realization.label.points')"
            :rules="[rules.value]"
            :placeholder="$t('realization.label.points')"
            class="pointsRealization"
            type="number"
            outlined
            required />
        </div>
        <span class="subtitle-1 labels"> {{ $t('realization.label.actionLabel') }}</span>
        <div class="actionLabel">
          <v-textarea
            v-model="actionLabel"
            :placeholder="$t('realization.label.actionLabel')"
            :rules="[rules.length]"
            name="actionLabel"
            class="pl-0 pt-0 actionLabel"
            auto-grow
            required
            autofocus
            rows="5" />
          <span class="tooManyChars mt-n2">
            {{ charsCount }}{{ maxLength > -1 ? ' / ' + maxLength : '' }}
            <i class="uiIconMessageLength"></i>
          </span>
        </div>
        <div class="mt-2">
          <span class="subtitle-1 labels"> {{ $t('realization.label.program') }}</span>
          <v-flex>
            <v-autocomplete
              ref="selectAutoComplete"
              v-model="program"
              :label="labels.label"
              :placeholder="labels.placeholder"
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
              @update:search-input="searchTerm = $event">
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
        </div>
      </v-card-text>
    </template>
    <template slot="footer">
      <div class="d-flex mr-2">
        <v-spacer />
        <button
          class="ignore-vuetify-classes btn mx-1"
          @click="close">
          {{ $t('realization.label.cancel') }}
        </button>
        <button
          class="ignore-vuetify-classes btn btn-primary"
          :disabled="charsCount > 1300"
          @click="updateRealization">
          {{ $t('realization.label.save') }}
        </button>
      </div>
    </template>
  </exo-drawer>
</template>
<script>
export default {
  data() {
    return {
      rules: {
        length: (v) => (v && v.length < 1301) || this.$t('realization.label.lengthExceed'),
        value: (v) => (v >= 0 && v <= 9999) || this.$t('realization.label.pointsValidation')
      },
      labels: {
        searchPlaceholder: this.$t('challenges.programSuggester.searchPlaceholder'),
        placeholder: this.$t('challenges.programSuggester.placeholder'),
        noDataLabel: this.$t('challenges.programSuggester.noDataLabel'),
      },
      domains: [],
      searchTerm: null,
      program: null,
      loadingSuggestions: false,
      realizationId: null,
      actionLabel: null,
      points: null,
      maxLength: 1300,
      status: null,
    };
  },
  computed: {
    charsCount(){
      return this.actionLabel && this.actionLabel.length;
    }
  },
  created() {
    this.$root.$on('realization-open-edit-drawer', this.open);

    this.getAllDomains();
  },
  methods: {
    open(realization, actionLabel) {
      this.points = realization.score;
      this.actionLabel = actionLabel || this.realization.actionLabel;
      this.program = realization.domain;
      this.realizationId = realization.id;
      this.status = realization.status;
      this.$refs.editRealizationDrawer.open();
    },
    close() {
      this.$refs.editRealizationDrawer.close();
    },
    getAllDomains() {
      this.$realizationsServices.getAllDomains().then(response => this.domains = response.domains);
    },
    updateRealization() {
      this.$realizationsServices.updateRealization(this.realizationId, this.status, this.actionLabel, this.program.title, this.points)
        .then((realization) => {
          this.$emit('updated',realization);
          this.close();
        });
    },
  },
};
</script>
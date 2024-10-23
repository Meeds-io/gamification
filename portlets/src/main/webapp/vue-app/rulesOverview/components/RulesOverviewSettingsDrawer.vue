<!--

 This file is part of the Meeds project (https://meeds.io/).

 Copyright (C) 2020 - 2024 Meeds Association contact@meeds.io

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
    ref="drawer"
    v-model="drawer"
    :right="!$vuetify.rtl"
    :loading="loading"
    eager>
    <template slot="title">
      {{ $t('gamification.rules.overviewSettings.title') }}
    </template>
    <template v-if="drawer" #content>
      <div class="d-flex align-center pa-5">
        <div>{{ $t('gamification.rules.overviewSettings.sortItemsBy') }}</div>
        <select
          v-model="sortBy"
          :aria-label="$t('gamification.rules.overviewSettings.sortItemsBy')"
          class="width-auto ms-auto my-auto ignore-vuetify-classes">
          <option
            v-for="s in sortTypes"
            :key="s.value"
            :value="s.value">
            {{ s.label }}
          </option>
        </select>
      </div>
      <div class="d-flex align-center px-5 pb-5">
        <div>{{ $t('gamification.rules.overviewSettings.itemsCountLabel') }}</div>
      </div>
      <div class="d-flex align-center px-5">
        <div class="font-weight-bold">{{ $t('gamification.rules.overviewSettings.actionsEndingSoon') }}</div>
        <number-input
          v-model="endingRulesLimit"
          :min="0"
          :max="25"
          :step="1"
          class="ms-auto me-n1 my-0 pa-0" />
      </div>
      <div class="d-flex align-center px-5">
        <div class="font-weight-bold">{{ $t('gamification.rules.overviewSettings.actionsToDoFirst') }}</div>
        <number-input
          v-model="lockedRulesLimit"
          :min="0"
          :max="25"
          :step="1"
          class="ms-auto me-n1 my-0 pa-0" />
      </div>
      <div class="d-flex align-center px-5">
        <div class="font-weight-bold">{{ $t('gamification.rules.overviewSettings.actionsAvailable') }}</div>
        <number-input
          v-model="availableRulesLimit"
          :min="1"
          :max="25"
          :step="1"
          class="ms-auto me-n1 my-0 pa-0" />
      </div>
      <div class="d-flex align-center px-5">
        <div class="font-weight-bold">{{ $t('gamification.rules.overviewSettings.actionsSoonAvailable') }}</div>
        <number-input
          v-model="upcomingRulesLimit"
          :min="0"
          :max="25"
          :step="1"
          class="ms-auto me-n1 my-0 pa-0" />
      </div>
      <div class="d-flex align-center px-5 mt-4">
        <div>{{ $t('gamification.rules.overviewSettings.hideWhenEmpty') }}</div>
        <v-switch
          v-model="hideWidgetWhenEmpty"
          class="ms-auto my-0 pa-0" />
      </div>
    </template>
    <template #footer>
      <div class="d-flex align-center">
        <v-btn
          :disabled="loading"
          :title="$t('links.label.cancel')"
          class="btn ms-auto me-2"
          @click="close()">
          {{ $t('gamification.rules.overviewSettings.cancel') }}
        </v-btn>
        <v-btn
          :loading="loading"
          color="primary"
          elevation="0"
          @click="save()">
          {{ $t('gamification.rules.overviewSettings.save') }}
        </v-btn>
      </div>
    </template>
  </exo-drawer>
</template>
<script>
export default {
  data: () => ({
    drawer: false,
    loading: false,
    lockedRulesLimit: 0,
    endingRulesLimit: 0,
    availableRulesLimit: 0,
    upcomingRulesLimit: 0,
    hideWidgetWhenEmpty: false,
    sortBy: 'score',
  }),
  computed: {
    sortTypes() {
      return [{
        value: 'score',
        label: this.$t('gamification.rules.overviewSettings.sortByScore'),
      }, {
        value: 'title',
        label: this.$t('gamification.rules.overviewSettings.sortByTitle'),
      }, {
        value: 'createdDate',
        label: this.$t('gamification.rules.overviewSettings.sortByCreatedDate'),
      }, {
        value: 'modifiedDate',
        label: this.$t('gamification.rules.overviewSettings.sortByModifiedDate'),
      }];
    },
  },
  created() {
    this.$root.$on('rules-overview-settings', this.open);
    this.reset();
  },
  beforeDestroy() {
    this.$root.$off('rules-overview-settings', this.open);
  },
  methods: {
    open() {
      this.$refs.drawer.open();
    },
    reset() {
      this.sortBy = this.$root.rulesSortBy || 'score';
      this.lockedRulesLimit = this.$root.lockedRulesLimit;
      this.endingRulesLimit = this.$root.endingRulesLimit;
      this.availableRulesLimit = this.$root.availableRulesLimit;
      this.upcomingRulesLimit = this.$root.upcomingRulesLimit;
      this.hideWidgetWhenEmpty = this.$root.hideWidgetWhenEmpty;
      this.loading = false;
    },
    close() {
      this.$refs.drawer.close();
    },
    save() {
      const formData = new FormData();
      formData.append('pageRef', this.$root.pageRef);
      formData.append('applicationId', this.$root.portletStorageId);
      const params = new URLSearchParams(formData).toString();

      this.loading = true;
      return fetch(`/layout/rest/pages/application/preferences?${params}`, {
        method: 'PATCH',
        credentials: 'include',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          preferences: [{
            name: 'lockedRulesLimit',
            value: String(this.lockedRulesLimit),
          }, {
            name: 'endingRulesLimit',
            value: String(this.endingRulesLimit),
          }, {
            name: 'availableRulesLimit',
            value: String(this.availableRulesLimit),
          }, {
            name: 'upcomingRulesLimit',
            value: String(this.upcomingRulesLimit),
          }, {
            name: 'hideWidgetWhenEmpty',
            value: String(this.hideWidgetWhenEmpty),
          }, {
            name: 'rulesSortBy',
            value: this.sortBy || 'modifiedDate',
          }],
        }),
      })
        .then(() => {
          this.$root.rulesSortBy = this.sortBy || 'score';
          this.$root.lockedRulesLimit = this.lockedRulesLimit;
          this.$root.endingRulesLimit = this.endingRulesLimit;
          this.$root.availableRulesLimit = this.availableRulesLimit;
          this.$root.upcomingRulesLimit = this.upcomingRulesLimit;
          window.setTimeout(() => this.$root.hideWidgetWhenEmpty = this.hideWidgetWhenEmpty, 500);
          this.close();
        })
        .then(() => this.$nextTick())
        .finally(() => this.loading = false);
    },
  },
};
</script>

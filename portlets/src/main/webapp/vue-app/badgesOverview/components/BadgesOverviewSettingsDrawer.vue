<!--

 This file is part of the Meeds project (https://meeds.io/).

 Copyright (C) 2020 - 2023 Meeds Association contact@meeds.io

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
    eager
    @closed="reset">
    <template slot="title">
      {{ $t('gamification.badges.badgesOverviewSettings.title') }}
    </template>
    <template v-if="drawer" #content>
      <div class="d-flex align-center pa-5">
        <div>{{ $t('gamification.badges.badgesOverviewSettings.sortItemsBy') }}</div>
        <select
          v-model="sortBy"
          class="width-auto ms-auto my-auto ignore-vuetify-classes">
          <option
            v-for="s in sortTypes"
            :key="s.value"
            :value="s.value">
            {{ s.label }}
          </option>
        </select>
      </div>
      <div class="d-flex align-center px-5">
        <div>{{ $t('gamification.badges.badgesOverviewSettings.displayBadgeName') }}</div>
        <v-switch
          v-model="showName"
          class="ms-auto me-n1 my-0 pa-0" />
      </div>
    </template>
    <template #footer>
      <div class="d-flex align-center">
        <v-btn
          :disabled="loading"
          :title="$t('links.label.cancel')"
          class="btn ms-auto me-2"
          @click="close()">
          {{ $t('gamification.badges.badgesOverviewSettings.cancel') }}
        </v-btn>
        <v-btn
          :loading="loading"
          color="primary"
          elevation="0"
          @click="save()">
          {{ $t('gamification.badges.badgesOverviewSettings.save') }}
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
    showName: false,
    sortBy: 'score',
  }),
  computed: {
    sortTypes() {
      return [{
        value: 'score',
        label: this.$t('gamification.badges.badgesOverviewSettings.sortByScore'),
      }, {
        value: 'alphanumeric',
        label: this.$t('gamification.badges.badgesOverviewSettings.sortByAlphanumeric'),
      }];
    },
  },
  created() {
    this.$root.$on('badges-overview-settings', this.open);
  },
  beforeDestroy() {
    this.$root.$off('badges-overview-settings', this.open);
  },
  methods: {
    open() {
      this.reset();
      this.$refs.drawer.open();
    },
    reset() {
      this.sortBy = this.$root.sortBy || 'score';
      this.showName = this.$root.showName;
      this.loading = false;
    },
    close() {
      this.$refs.drawer.close();
    },
    save() {
      this.loading = true;
      const formData = new FormData();
      formData.append('pageRef', this.$root.pageRef);
      formData.append('applicationId', this.$root.portletStorageId);
      const params = new URLSearchParams(formData).toString();
      return fetch(`/layout/rest/pages/application/preferences?${params}`, {
        method: 'PATCH',
        credentials: 'include',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          preferences: [{
            name: 'showName',
            value: !!this.showName,
          }, {
            name: 'sortBy',
            value: this.sortBy || 'score',
          }],
        }),
      })
        .then(() => {
          this.$root.sortBy = this.sortBy || 'score';
          this.$root.showName = this.showName;
          this.close();
        })
        .finally(() => this.loading = false);
    },
  },
};
</script>

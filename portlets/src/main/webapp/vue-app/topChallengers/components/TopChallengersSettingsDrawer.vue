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
    eager
    @closed="reset">
    <template slot="title">
      {{ $t('gamification.topChallengers.settings.title') }}
    </template>
    <template v-if="drawer" #content>
      <div class="d-flex align-center pa-5">
        <div>{{ $t('gamification.topChallengers.settings.periodItems') }}</div>
        <select
          v-model="topChallengersPeriod"
          :aria-label="$t('gamification.topChallengers.settings.periodItems')"
          class="width-auto ms-auto my-auto ignore-vuetify-classes">
          <option
            v-for="s in periods"
            :key="s.value"
            :value="s.value">
            {{ s.label }}
          </option>
        </select>
      </div>
      <div class="d-flex align-center px-5">
        <div>{{ $t('gamification.topChallengers.settings.displayCurrentPosition') }}</div>
        <v-switch
          v-model="topChallengersCurrentPosition"
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
          {{ $t('gamification.topChallengers.settings.cancel') }}
        </v-btn>
        <v-btn
          :loading="loading"
          color="primary"
          elevation="0"
          @click="save()">
          {{ $t('gamification.topChallengers.settings.save') }}
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
    topChallengersPeriod: 'week',
    topChallengersCurrentPosition: false,
  }),
  computed: {
    periods() {
      return [{
        value: 'week',
        label: this.$t('gamification.topChallengers.settings.weekPeriod'),
      }, {
        value: 'month',
        label: this.$t('gamification.topChallengers.settings.monthPeriod'),
      }, {
        value: 'quarter',
        label: this.$t('gamification.topChallengers.settings.quarterPeriod'),
      }, {
        value: 'all',
        label: this.$t('gamification.topChallengers.settings.allPeriod'),
      }];
    },
  },
  created() {
    this.$root.$on('topChallengers-overview-settings', this.open);
  },
  beforeDestroy() {
    this.$root.$off('topChallengers-overview-settings', this.open);
  },
  methods: {
    open() {
      this.reset();
      this.$refs.drawer.open();
    },
    reset() {
      this.topChallengersPeriod = this.$root.topChallengersPeriod || 'week';
      this.topChallengersCurrentPosition = this.$root.topChallengersCurrentPosition || false;
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
            name: 'topChallengersPeriod',
            value: this.topChallengersPeriod || 'modifiedDate',
          }, {
            name: 'topChallengersCurrentPosition',
            value: String(this.topChallengersCurrentPosition),
          }],
        }),
      })
        .then(() => {
          this.$root.topChallengersPeriod = this.topChallengersPeriod || 'week';
          this.$root.topChallengersCurrentPosition = this.topChallengersCurrentPosition || false;
          this.close();
        })
        .finally(() => this.loading = false);
    },
  },
};
</script>

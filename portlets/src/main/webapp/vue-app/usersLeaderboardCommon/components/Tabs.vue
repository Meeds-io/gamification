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
  <div>
    <div class="d-flex">
      <v-tabs v-model="selectedPeriod" class="flex-grow-1 flex-shrink-1 width-auto">
        <v-tab
          v-for="period in periods"
          :key="period.value"
          :href="`#${period.value}`"
          class="overflow-hidden px-2">
          <span class="text-none">{{ period.text }}</span>
        </v-tab>
        <v-tabs-slider color="tertiary" />
      </v-tabs>
      <v-btn
        small
        :icon="isAllPrograms"
        :class="!isAllPrograms && 'pa-0' || 'my-auto'"
        height="auto"
        width="auto"
        text
        class="ms-auto flex-grow-0 flex-shrink-0"
        @click="$refs.programSelectorDrawer.open()">
        <v-icon :class="!isAllPrograms && 'primary--text me-1' || 'icon-default-color'" size="16">
          fa-sliders-h
        </v-icon>
        <span
          v-if="!isAllPrograms"
          class="caption">
          (1)
        </span>
      </v-btn>
    </div>
    <v-tabs-items v-model="selectedPeriod">
      <v-tab-item
        v-for="period in periods"
        :id="period.value"
        :key="period.value"
        :value="period.value">
        <v-progress-linear v-if="loading" indeterminate />
        <users-leaderboard-profiles
          v-if="!selectionChanged"
          :users="users"
          :current-rank="!loading && currentRank"
          :period="period.value" />
      </v-tab-item>
    </v-tabs-items>
    <v-card-actions v-if="canLoadMore && !embedded">
      <v-spacer />
      <v-btn
        :loading="loading"
        class="primary--text"
        outlined
        link
        @click="loadMore">
        {{ $t('exoplatform.gamification.leaderboard.showMore') }}
      </v-btn>
      <v-spacer />
    </v-card-actions>
    <users-leaderboard-program-selector-drawer
      ref="programSelectorDrawer"
      v-model="programId"
      :go-back-button="embedded" />
  </div>
</template>
<script>
export default {
  props: {
    embedded: {
      type: Boolean,
      default: false,
    },
  },
  data: () => ({
    users: [],
    pageSize: 10,
    limit: 10,
    programId: '0',
    currentRank: null,
    loading: false,
    selectionChanged: false,
    selectedPeriod: 'WEEK',
  }),
  computed: {
    periods() {
      return [{
        value: 'WEEK',
        text: this.$t('exoplatform.gamification.leaderboard.selectedPeriod.WEEK'),
      },{
        value: 'MONTH',
        text: this.$t('exoplatform.gamification.leaderboard.selectedPeriod.MONTH'),
      },{
        value: 'ALL',
        text: this.$t('exoplatform.gamification.leaderboard.selectedPeriod.ALL'),
      }];
    },
    hasMore() {
      return this.users && this.limit <= this.users.length;
    },
    canLoadMore() {
      return !this.selectionChanged && this.hasMore;
    },
    isAllPrograms() {
      return this.programId === '0';
    },
  },
  watch: {
    selectedPeriod(newVal, oldVal) {
      if (oldVal) {
        this.selectionChanged = true;
        this.refreshBoard(this.pageSize);
      }
    },
    programId(newVal, oldVal) {
      if (oldVal) {
        this.selectionChanged = true;
        this.refreshBoard(this.pageSize);
      }
    },
    loading() {
      this.$emit('loading', this.loading);
    },
    hasMore() {
      this.$emit('has-more', this.hasMore);
    },
  },
  created() {
    this.loading = true;
    this.refreshBoard()
      .then(() => this.$nextTick())
      .finally(() => this.loading = false);
  },
  methods: {
    refreshBoard(limit) {
      if (!limit) {
        limit = this.limit;
      }

      this.loading = true;
      return this.$leaderboardService.getLeaderboard({
        programId: this.programId,
        period: this.selectedPeriod,
        limit,
      })
        .then(data => {
          this.users = data?.filter?.(user => user.socialId) || [];
          this.currentRank = !this.$root.isAnonymous && this.users.find(user => !user.socialId)?.rank;
          this.limit = limit;
        })
        .finally(() => {
          this.loading = false;
          this.selectionChanged = false;
        });
    },
    loadMore() {
      this.refreshBoard(this.limit + this.pageSize);
    },
  },
};
</script>
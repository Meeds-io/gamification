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
    <v-tabs v-model="selectedPeriod">
      <v-tab
        v-for="period in periods"
        :key="period.value"
        :href="`#${period.value}`"
        class="overflow-hidden px-2">
        <span class="text-none">{{ period.text }}</span>
      </v-tab>
      <v-tabs-slider color="tertiary" />
      <slot name="tabButton"></slot>
    </v-tabs>
    <v-tabs-items v-model="selectedPeriod">
      <v-tab-item
        v-for="period in periods"
        :id="period.value"
        :key="period.value"
        :value="period.value">
        <v-progress-linear v-if="loading" indeterminate />
        <v-list v-if="!selectionChanged">
          <users-leaderboard-profile
            v-for="(user, index) in sortedUsers"
            :key="user.remoteId"
            :user="user"
            :rank="index + 1"
            :programs="programs" />
          <template v-if="currentRank">
            <v-divider class="ma-0" />
            <v-list-item class="disabled-background">
              <div class="me-4">
                {{ $t('exoplatform.gamification.leaderboard.rank') }}
              </div>
              <div>
                <v-avatar color="tertiary" size="32">
                  {{ currentRank }}
                </v-avatar>
              </div>
            </v-list-item>
          </template>
        </v-list>
      </v-tab-item>
    </v-tabs-items>
    <v-card-actions v-if="canLoadMore && !hideLoadMore">
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
  </div>
</template>
<script>
export default {
  props: {
    programId: {
      type: String,
      default: () => '0',
    },
    hideLoadMore: {
      type: Boolean,
      default: false,
    },
  },
  data: () => ({
    users: [],
    pageSize: 10,
    limit: 10,
    currentRank: null,
    loading: false,
    selectionChanged: false,
    selectedPeriod: 'WEEK',
  }),
  computed: {
    canLoadMore() {
      return !this.selectionChanged && this.hasMore;
    },
    hasMore() {
      return this.users && this.limit <= this.users.length;
    },
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
    sortedUsers() {
      return this.users.slice().sort((a, b) => b.score - a.score);
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
      const formData = new FormData();
      if (this.programId && this.programId !== '0') {
        formData.append('programId', this.programId);
      }
      formData.append('period', this.selectedPeriod || 'WEEK');
      formData.append('capacity', limit);
      const params = decodeURIComponent(new URLSearchParams(formData).toString());

      this.loading = true;
      return fetch(`${eXo.env.portal.context}/${eXo.env.portal.rest}/gamification/leaderboard/filter?${params}`, {
        credentials: 'include',
      }).then(resp => resp?.ok && resp.json())
        .then(data => {
          const currentUser = !this.$root.isAnonymous && data?.find?.(user => !user.socialId);
          this.currentRank = currentUser?.rank;
          this.users = this.$root.isAnonymous && data || data.filter(user => user.socialId);
          this.limit = limit;
        })
        .finally(() => {
          this.loading = false;
          this.selectionChanged = false;
        });
    },
    loadMore() {
      this.loading = true;
      this.refreshBoard(this.limit + this.pageSize)
        .then(() => this.$nextTick())
        .finally(() => this.loading = false);
    },
  },
};
</script>
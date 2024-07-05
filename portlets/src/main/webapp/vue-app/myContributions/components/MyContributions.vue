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
  <v-app>
    <gamification-overview-widget :loading="loading > 0">
      <template #title>
        <div v-if="!hasZeroPoints && !loading" class="d-flex flex-grow-1 full-width">
          <div class="widget-text-header text-truncate">
            {{ $t('overview.myContributions.title') }}
          </div>
          <div class="spacer"></div>
          <v-btn
            height="auto"
            min-width="auto"
            class="pa-0"
            text
            @click="$refs.detailsDrawer.open(user, period)">
            <span class="primary--text text-none">{{ $t('rules.seeAll') }}</span>
          </v-btn>
        </div>
      </template>
      <template #default>
        <div v-if="!loading && hasZeroPoints" class="d-flex flex-column full-width full-height align-center justify-center">
          <v-icon color="tertiary" size="54">fa-chart-pie</v-icon>
          <span class="mt-7">{{ $t('gamification.overview.weeklyAchievements') }}</span>
        </div>
        <users-leaderboard-profile-stats
          v-else-if="!loading && user && programs"
          :user="user"
          :programs="programs"
          :period="period"
          :program-id="programId"
          central-points
          @open="$refs.detailsDrawer.open(user, period)" />
        <users-leaderboard-profile-achievements-drawer
          ref="detailsDrawer" />
        <engagement-center-rule-extensions />
      </template>
    </gamification-overview-widget>
  </v-app>
</template>
<script>
export default {
  data: () => ({
    loading: 2,
    programs: null,
    user: null,
    period: 'WEEK',
  }),
  computed: {
    hasZeroPoints() {
      return !this.user?.score;
    },
  },
  created() {
    this.init();    
  },
  methods: {
    init() {
      this.retrieveUserStats();
      this.retrievePrograms();
    },
    retrieveUserStats() {
      return this.$leaderboardService.getLeaderboard({
        identityId: eXo.env.portal.profileOwnerIdentityId,
        period: this.period,
        limit: 0,
      })
        .then(data => this.user = data?.[0] || null)
        .finally(() => this.loading--);
    },
    retrievePrograms() {
      return this.$leaderboardService.getPrograms()
        .then(data => this.programs = data?.programs || [])
        .finally(() => this.loading--);
    },
  }
};
</script>

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
  <v-app>
    <v-hover v-model="hover">
      <gamification-overview-widget :loading="loading">
        <template #title>
          <div class="d-flex flex-grow-1 full-width position-relative">
            <div v-if="!neverContributed && !loading" class="widget-text-header text-truncate">
              {{ $t('gamification.myContributions.title') }}
            </div>
            <div class="spacer"></div>
            <div
              :class="neverContributed && 'mt-2 me-2'"
              class="position-absolute t-0 r-0 z-index-one">
              <v-btn
                v-if="!neverContributed"
                :icon="hoverEdit"
                :small="hoverEdit"
                height="auto"
                min-width="auto"
                class="pa-0"
                text
                @click="$refs.detailsDrawer.open(user, period)">
                <v-icon
                  v-if="hoverEdit"
                  size="18"
                  color="primary">
                  fa-external-link-alt
                </v-icon>
                <span v-else class="primary--text text-none">{{ $t('rules.seeAll') }}</span>
              </v-btn>
              <v-fab-transition hide-on-leave>
                <v-btn
                  v-show="hoverEdit"
                  :title="$t('gamification.programs.overviewSettings.editTooltip')"
                  :class="neverContributed && 'mt-n4 me-n2 z-index-one'"
                  small
                  icon
                  @click="$root.$emit('my-contributions-overview-settings')">
                  <v-icon size="18">fa-cog</v-icon>
                </v-btn>
              </v-fab-transition>
            </div>
          </div>
        </template>
        <template #default>
          <div v-if="!loading && hasZeroPoints" class="d-flex flex-column full-width full-height align-center justify-center">
            <v-icon color="tertiary" size="54">fa-chart-pie</v-icon>
            <span class="mt-7">{{ placeholder }}</span>
          </div>
          <my-contributions-profile-chart
            v-else-if="!loading && user"
            :identity-id="user.identityId"
            :score="user.score"
            :period="period"
            :program-id="programId"
            central-points
            @open="$refs.detailsDrawer.open(user, period)" />
          <users-leaderboard-profile-achievements-drawer
            ref="detailsDrawer" />
          <my-contributions-settings-drawer
            v-if="$root.canEdit" />
          <engagement-center-rule-extensions />
        </template>
      </gamification-overview-widget>
    </v-hover>
  </v-app>
</template>
<script>
export default {
  data: () => ({
    loading: true,
    user: null,
    userScore: null,
    hover: false,
    initialized: false,
  }),
  computed: {
    hasZeroPoints() {
      return !this.user?.score;
    },
    neverContributed() {
      return this.hasZeroPoints && !this.userScore;
    },
    period() {
      return this.$root.myContributionsPeriod || 'week';
    },
    limit() {
      return this.$root.myContributionsProgramLimit || 0;
    },
    displayLegend() {
      return this.$root.myContributionsDisplayLegend || false;
    },
    hoverEdit() {
      return this.hover && this.$root.canEdit;
    },
    title() {
      return this.$t(`gamification.overview.${this.period}lyLeaderboard`);
    },
    placeholder() {
      return this.$t(`gamification.overview.${this.period}lyAchievements`);
    },
  },
  watch: {
    period() {
      if (this.initialized) {
        this.init();
      }
    },
    loading() {
      if (!this.loading) {
        this.initialized = true;
      }
    },
  },
  created() {
    this.init();
  },
  methods: {
    init() {
      this.retrieveUserStats();
    },
    retrieveAllPeriodUserStats() {
      return this.$leaderboardService.getLeaderboard({
        identityId: eXo.env.portal.profileOwnerIdentityId,
        period: 'ALL',
        limit: 0,
      })
        .then(data => this.userScore = data?.[0]?.score || 0);
    },
    retrieveUserStats() {
      this.loading = true;
      return this.$leaderboardService.getLeaderboard({
        identityId: eXo.env.portal.profileOwnerIdentityId,
        period: this.$root.myContributionsPeriod,
        limit: this.limit,
      })
        .then(data => {
          this.user = data?.[0] || null;
          if (!this.user?.score) {
            return this.retrieveAllPeriodUserStats();
          }
        })
        .then(() => this.$nextTick())
        .finally(() => this.loading = false);
    },
  }
};
</script>

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
  <exo-drawer
    ref="drawer"
    v-model="drawer"
    :loading="loading"
    :go-back-button="goBackButton"
    :allow-expand="!relative"
    class="user-achievements-drawer"
    fixed
    right>
    <template #title>
      {{ $t('gamification.overview.userAchievementsList.drawer.title') }}
    </template>
    <template v-if="drawer && user && programs" #content>
      <div class="pa-5">
        <users-leaderboard-profile
          :user="user"
          no-action />

        <div class="d-flex align-center mb-2 mt-5">
          <div class="text-header me-2">
            {{ $t(`gamification.profileStats.${period}`) }}
          </div>
          <v-divider />
        </div>
        <users-leaderboard-profile-stats
          :user="user"
          :programs="programs"
          :period="period"
          :program-id="programId"
          @select="programId = $event" />

        <div class="d-flex align-center mb-2 mt-5">
          <div class="text-header me-2">
            {{ $t('gamification.profileStats.achievements') }}
          </div>
          <v-divider />
        </div>
        <users-leaderboard-profile-realizations
          ref="realizations"
          :identity-id="user.identityId"
          :program-id="programId"
          :page-size="pageSize"
          @loading="loading = $event"
          @has-more="hasMore = $event" />
      </div>
    </template>
    <template v-else-if="drawer && !loading" #content>
      <div class="d-flex flex-column full-width full-height align-center justify-center">
        <v-icon color="tertiary" size="54">fa-chart-pie</v-icon>
        <span v-html="noResultsText" class="text-body mt-7"></span>
      </div>
      <gamification-rules-overview-list-drawer
        ref="actionsList"
        go-back-button />
    </template>
    <template #footer>
      <div v-if="hasMore" class="d-flex">
        <v-btn
          :loading="loading"
          class="btn"
          block
          @click="$refs.realizations.loadMore()">
          {{ $t('gamification.showMore') }}
        </v-btn>
      </div>
    </template>
  </exo-drawer>
</template>
<script>
export default {
  props: {
    goBackButton: {
      type: Boolean,
      default: false,
    },
    relative: {
      type: Boolean,
      default: false,
    },
  },
  data: () => ({
    drawer: false,
    loading: false,
    hasMore: false,
    user: null,
    period: null,
    programs: null,
    programId: null,
    pageSize: Math.max(10, parseInt((window.innerHeight - 560) / 62)),
  }),
  computed: {
    noResultsText() {
      return this.$t('gamification.overview.emptyAchievementsMessage', {
        0: '<a class="primary--text font-weight-bold" onclick="window.dispatchEvent(new CustomEvent(\'rules-list-drawer-open\'))">',
        1: '</a>',
      });
    },
  },
  created() {
    window.addEventListener('rules-list-drawer-open', this.openActionsList);
  },
  beforeDestroy() {
    window.removeEventListener('rules-list-drawer-open', this.openActionsList);
  },
  methods: {
    openByIdentityId(identityId, period) {
      this.period = period || 'WEEK';
      this.user = null;
      this.programId = null;

      this.$refs.drawer.open();
      this.loading = true;
      this.$leaderboardService.getLeaderboard({
        identityId,
        period,
        limit: 0,
      })
        .then(data => this.user = data.find(u => Number(u.identityId) === Number(identityId)))
        .then(() => this.retrievePrograms())
        .finally(() => this.loading = false);
    },
    open(user, period) {
      this.period = period || 'WEEK';
      this.user = user;
      this.programId = null;

      this.$refs.drawer.open();
      this.loading = true;
      return this.retrievePrograms()
        .finally(() => this.loading = false);
    },
    openActionsList() {
      this.$refs?.actionsList?.open();
    },
    retrievePrograms() {
      if (this.programs) {
        return Promise.resolve(this.programs);
      } else {
        return this.$leaderboardService.getPrograms()
          .then(data => this.programs = data?.programs || [])
          .finally(() => this.loading = false);
      }
    },
    close() {
      this.$refs.drawer.close();
    },
  },
};
</script>
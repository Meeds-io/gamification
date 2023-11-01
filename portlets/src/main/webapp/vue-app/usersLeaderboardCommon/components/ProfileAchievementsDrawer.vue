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
    class="user-achievements-drawer"
    allow-expand
    fixed
    right>
    <template #title>
      {{ $t('gamification.overview.userAchievementsList.drawer.title') }}
    </template>
    <template v-if="drawer && user && programs" #content>
      <users-leaderboard-profile-stats
        class="pa-5"
        :user="user"
        :programs="programs"
        :period="period" />
      <users-leaderboard-profile-realizations
        ref="realizations"
        :identity-id="user.socialId"
        class="px-5 pb-5 pt-0"
        @loading="loading = $event"
        @has-more="hasMore = $event" />
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
  },
  data: () => ({
    drawer: false,
    loading: false,
    hasMore: false,
    user: null,
    period: null,
    programs: null,
  }),
  methods: {
    open(user, period) {
      this.user = user;
      this.period = period;
      this.$refs.drawer.open();
      if (!this.programs) {
        this.loading = true;
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
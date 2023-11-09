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
    id="leaderboardDrawer"
    class="leaderboard-drawer"
    allow-expand
    fixed
    right>
    <template #title>
      {{ $t('gamification.overview.leaderboard.drawer.title') }}
    </template>
    <template v-if="drawer" #content>
      <users-leaderboard-tabs
        ref="leaderboard"
        class="px-5 pb-5"
        embedded
        @loading="loading = $event"
        @has-more="hasMore = $event" />
    </template>
    <template #footer>
      <div v-if="hasMore" class="d-flex">
        <v-btn
          :loading="loading"
          class="btn"
          block
          @click="$refs.leaderboard.loadMore()">
          {{ $t('exoplatform.gamification.leaderboard.showMore') }}
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
    hasMore: false,
    programId: '0',
  }),
  methods: {
    open() {
      this.$refs.drawer.open();
    },
    close() {
      this.$refs.drawer.close();
    },
  },
};
</script>
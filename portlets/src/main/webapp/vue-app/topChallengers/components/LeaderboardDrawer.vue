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
    right>
    <template #title>
      {{ $t('gamification.overview.leaderboard.drawer.title') }}
    </template>
    <template v-if="drawer" #content>
      <users-leaderboard-tabs
        ref="leaderboard"
        :program-id="programId"
        class="px-5 pb-5"
        hide-load-more
        @loading="loading = $event"
        @has-more="hasMore = $event">
        <template #tabButton>
          <v-btn
            small
            :icon="isAllPrograms"
            :class="!isAllPrograms && 'pa-0' || 'my-auto'"
            height="auto"
            width="auto"
            text
            class="ms-auto"
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
        </template>
      </users-leaderboard-tabs>
      <gamification-overview-program-selector-drawer
        ref="programSelectorDrawer"
        v-model="programId" />
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
  computed: {
    isAllPrograms() {
      return this.programId === '0';
    },
  },
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
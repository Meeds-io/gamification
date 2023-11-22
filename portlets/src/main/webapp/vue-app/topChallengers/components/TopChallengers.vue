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
  <v-app>
    <gamification-overview-widget
      :loading="loading"
      class="d-flex">
      <template #title>
        <div v-if="!displayPlaceholder && !loading" class="d-flex flex-grow-1 full-width">
          <div class="widget-text-header text-capitalize-first-letter text-truncate">
            {{ $t('gamification.overview.topChallengersTitle') }}
          </div>
          <div class="spacer"></div>
          <v-btn
            height="auto"
            min-width="auto"
            class="pa-0"
            text
            @click="$refs.detailsDrawer.open()">
            <span class="primary--text text-none">{{ $t('rules.seeAll') }}</span>
          </v-btn>
        </div>
      </template>
      <gamification-rank is-overview-display />
    </gamification-overview-widget>
    <gamification-overview-leaderboard-drawer
      ref="detailsDrawer"
      :page-size="pageSize" />
    <engagement-center-rule-extensions />
  </v-app>
</template>
<script>
export default {
  data: () => ({
    rankDisplayed: false,
    loading: true,
    pageSize: Math.max(10, parseInt((window.innerHeight - 122) / 45)),
  }),
  computed: {
    displayPlaceholder() {
      return !this.rankDisplayed && !this.loading;
    },
  },
  created() {
    document.addEventListener('listOfRankedConnections', (event) => {
      if (event) {
        this.rankDisplayed = event.detail > 0;
        this.loading = false;
      }
    });
  },
};
</script>


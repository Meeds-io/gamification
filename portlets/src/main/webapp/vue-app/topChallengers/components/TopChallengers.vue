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
      :loading="loading">
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
      <div v-if="displayPlaceholder" class="d-flex flex-column align-center justify-center full-width full-height">
        <v-icon color="secondary" size="54">fa-trophy</v-icon>
        <span class="subtitle-1 font-weight-bold mt-7">{{ $t('gamification.overview.weeklyLeaderboard') }}</span>
      </div>
      <gamification-overview-widget-row
        v-show="!displayPlaceholder"
        class="my-auto"
        dense>
        <template #content>
          <gamification-rank :is-overview-display="true" />
        </template>
      </gamification-overview-widget-row>
    </gamification-overview-widget>
    <gamification-overview-leaderboard-drawer
      ref="detailsDrawer" />
    <engagement-center-rule-extensions />
  </v-app>
</template>
<script>
export default {
  data: () => ({
    rankDisplayed: false,
    loading: true,
  }),
  computed: {
    displayPlaceholder() {
      return !this.rankDisplayed && !this.loading;
    }
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


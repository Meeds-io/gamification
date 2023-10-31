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
      <template v-if="!displayPlaceholder" #title>
        <div class="d-flex flex-grow-1 full-width">
          <div class="widget-text-header text-capitalize-first-letter text-truncate">
            {{ $t('gamification.overview.topChallengersTitle') }}
          </div>
          <div class="spacer"></div>
          <v-btn
            height="auto"
            min-width="auto"
            class="pa-0"
            text
            @click="openDetails">
            <span class="primary--text text-none">{{ $t('rules.seeAll') }}</span>
          </v-btn>
        </div>
      </template>
      <template #content>
        <gamification-overview-widget-row
          v-show="displayPlaceholder"
          disabled
          class="my-auto">
          <template #content>
            <div class="d-flex flex-column align-center justify-center">
              <v-icon color="secondary" size="54">fa-trophy</v-icon>
              <span class="subtitle-1 font-weight-bold mt-7">{{ $t('gamification.overview.leaderboard') }}</span>
            </div>
          </template>
        </gamification-overview-widget-row>
        <gamification-overview-widget-row class="my-auto" v-show="rankDisplayed && !isExternal">
          <template #content>
            <gamification-rank :is-overview-display="true" />
          </template>
        </gamification-overview-widget-row>
      </template>
    </gamification-overview-widget>
    <gamification-overview-leaderboard-drawer
      v-if="details"
      ref="detailsDrawer" />
  </v-app>
</template>
<script>
export default {
  data: () => ({
    rankDisplayed: false,
    loading: true,
    openingDetails: false,
    details: false,
  }),
  computed: {
    peopleURL() {
      return `${eXo.env.portal.context}/${eXo.env.portal.defaultPortal}/people`;
    },
    isExternal() {
      return eXo.env.portal.isExternal === 'true';
    },
    displayPlaceholder() {
      return (this.isExternal || !this.rankDisplayed) && !this.loading;
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
  methods: {
    openDetails() {
      this.openingDetails = true;
      window.require(['PORTLET/gamification-portlets/UsersLeaderboard'], () => {
        this.details = true;
        this.$nextTick().then(() => {
          this.$refs?.detailsDrawer?.open?.();
          this.openingDetails = false;
        });
      });
    },
  },
};
</script>


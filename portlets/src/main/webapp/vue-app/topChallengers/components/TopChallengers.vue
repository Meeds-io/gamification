<!--
  This file is part of the Meeds project (https://meeds.io/).
  Copyright (C) 2022 Meeds Association contact@meeds.io
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
      :see-all-url="!isExternal && peopleURL || ''">
      <template #title>
        {{ $t('gamification.overview.topChallengersTitle') }}
      </template>
      <template #content>
        <gamification-overview-widget-row
          v-show="displayPlaceholder"
          disabled
          class="my-auto">
          <template #icon>
            <v-icon color="secondary" size="55px">fas fa-trophy</v-icon>
          </template>
          <template #content>
            <span v-sanitized-html="$t('gamification.overview.topChallengersSummary')"></span>
          </template>
        </gamification-overview-widget-row>
        <gamification-overview-widget-row class="my-auto" v-show="rankDisplayed && !isExternal">
          <template #content>
            <gamification-rank :is-overview-display="true" />
          </template>
        </gamification-overview-widget-row>
      </template>
    </gamification-overview-widget>
  </v-app>
</template>
<script>
export default {
  data: () => ({
    rankDisplayed: false,
    loading: true,
  }),
  computed: {
    peopleURL() {
      return `${eXo.env.portal.context}/${eXo.env.portal.portalName}/people`;
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
};
</script>


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
      class="d-flex hover-reveal-parent hover-swap-parent">
      <template #title>
        <div class="d-flex flex-grow-1 min-width-0 full-width position-relative">
          <div v-if="!displayPlaceholder && !loading" class="text-header">
            <span class="text-truncate">{{ spaceId && $t('gamification.overview.space.topChallengersTitle') || $t('gamification.overview.topChallengersTitle') }}</span>
          </div>
          <div class="spacer"></div>
          <div
            :class="{
              'mt-2 me-2': displayPlaceholder,
              'l-0': $vuetify.rtl,
              'r-0': !$vuetify.rtl,
            }"
            class="position-absolute absolute-vertical-center z-index-one">
            <public-widget-hidden-warning v-if="$root.canEdit" />
            <v-btn
              v-if="!displayPlaceholder && !loading"
              :aria-label="$t('rules.seeAll')"
              height="auto"
              min-width="auto"
              class="pa-0"
              text
              @click="$refs.detailsDrawer.open(period)">
              <span
                :class="$root.canEdit && 'hover-swap-default'"
                class="primary--text text-none">{{ $t('rules.seeAll') }}</span>
              <v-icon
                v-if="$root.canEdit"
                size="18"
                color="primary"
                class="hover-swap-alt">
                fa-external-link-alt
              </v-icon>
            </v-btn>
            <v-fab-transition hide-on-leave>
              <v-btn
                v-show="$root.canEdit"
                :title="$t('gamification.topChallengers.settings.editTooltip')"
                :aria-label="$t('gamification.topChallengers.settings.editTooltip')"
                :class="{
                  'me-n2 z-index-one': displayPlaceholder,
                }"
                class="hover-reveal"
                small
                icon
                @click="$root.$emit('topChallengers-overview-settings')">
                <v-icon size="18">fa-cog</v-icon>
              </v-btn>
            </v-fab-transition>
          </div>
        </div>
      </template>
      <template #default>
        <gamification-rank is-overview-display />
      </template>
    </gamification-overview-widget>
    <gamification-overview-leaderboard-drawer
      ref="detailsDrawer"
      :page-size="pageSize" />
    <gamification-overview-top-challengers-settings-drawer
      v-if="$root.canEdit" />
    <engagement-center-rule-extensions />
  </v-app>
</template>
<script>
export default {
  data: () => ({
    rankDisplayed: false,
    loading: true,
    spaceId: eXo.env.portal.spaceId,
    pageSize: Math.max(10, parseInt((window.innerHeight - 122) / 45)),
  }),
  computed: {
    period() {
      return this.$root.topChallengersPeriod || 'week';
    },
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


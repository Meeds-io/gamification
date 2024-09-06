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
    <v-hover v-model="hover">
      <gamification-overview-widget
        :loading="loading"
        class="d-flex">
        <template #title>
          <div class="d-flex flex-grow-1 full-width position-relative">
            <div v-if="!displayPlaceholder && !loading" class="widget-text-header text-capitalize-first-letter text-truncate">
              {{ $t('gamification.overview.topChallengersTitle') }}
            </div>
            <div class="spacer"></div>
            <div
              :class="{
                'mt-2 me-2': displayPlaceholder,
                'l-0': $vuetify.rtl,
                'r-0': !$vuetify.rtl,
              }"
              class="position-absolute absolute-vertical-center z-index-one">
              <v-tooltip v-if="$root.displayNotPublicallyVisible" top>
                <template #activator="{attrs, on}">
                  <v-icon
                    size="18"
                    color="warning"
                    class="me-2"
                    v-on="on"
                    v-bind="attrs">
                    fa-exclamation-triangle
                  </v-icon>
                </template>
                <span>
                  {{ $t('gamification.publicWidgetHiddenTooltipPart1') }}
                  <br>
                  {{ $t('gamification.publicWidgetHiddenTooltipPart2') }}
                </span>
              </v-tooltip>
              <v-btn
                v-if="!displayPlaceholder && !loading"
                :icon="hoverEdit"
                :small="hoverEdit"
                height="auto"
                min-width="auto"
                class="pa-0"
                text
                @click="$refs.detailsDrawer.open(period)">
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
                  :title="$t('gamification.topChallengers.settings.editTooltip')"
                  :class="displayPlaceholder && 'mt-n4 me-n2 z-index-one'"
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
    </v-hover>
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
    hover: false,
    rankDisplayed: false,
    loading: true,
    pageSize: Math.max(10, parseInt((window.innerHeight - 122) / 45)),
  }),
  computed: {
    period() {
      return this.$root.topChallengersPeriod || 'week';
    },
    displayPlaceholder() {
      return !this.rankDisplayed && !this.loading;
    },
    hoverEdit() {
      return this.hover && this.$root.canEdit;
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


<!--
This file is part of the Meeds project (https://meeds.io/).
Copyright (C) 2020 Meeds Association
contact@meeds.io
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
  <v-flex
    d-flex
    flex-column
    xs12
    sm12>
    <v-toolbar
      height="64"
      color="white"
      flat
      class="border-box-sizing profile-card-header flex-column flex-shrink-1 flex-grow-0">
      <div class="widget-text-header text-truncate">
        {{ title }}
      </div>
    </v-toolbar>
    <v-layout 
      row 
      wrap 
      mx-0>
      <v-flex d-flex xs12>
        <v-layout
          row
          mx-0>
          <v-flex
            id="profile-stats-spacesCount"
            xs6
            d-flex
            justify-center
            align-center>
            <v-card flat>
              <a class="white--text">
                <v-badge :value="isCurrentUserProfile && spacesRequestsSize > 0" class="badge-color">
                  <v-btn
                    slot="badge"
                    icon
                    dark
                    height="22"
                    width="22"
                    @click="openSpaceDrawer">{{ spacesRequestsSize }}
                  </v-btn>
                  <div
                    class="headline text-color font-weight-bold pa-1"
                    @click="openSpaceDrawer">
                    <span>{{ isCurrentUserProfile ? spacesSize : commonsSpaceDefaultSize }}</span>
                  </div>
                </v-badge>
              </a>
              <v-card-text class="pa-1 subtitle-1 text-color">
                <span>{{ isCurrentUserProfile ? $t('homepage.profileStatus.spaces') : $t('homepage.profileStatus.Commonspaces') }}</span>
              </v-card-text>
            </v-card>
          </v-flex>
          <v-flex
            id="profile-stats-connectionsCount"
            d-flex
            xs6
            justify-center
            align-center>
            <v-card tile flat>
              <a class="white--text">
                <v-badge
                  :value="isCurrentUserProfile && connectionsRequestsSize > 0"
                  pa-0
                  class="badge-color">
                  <v-btn
                    slot="badge"
                    icon
                    dark
                    height="20"
                    width="20"
                    @click="openConnectionsDrawer">{{ connectionsRequestsSize }}</v-btn>
                  <div class="headline text-color font-weight-bold pa-1" @click="openConnectionsDrawer">
                    <span>{{ isCurrentUserProfile ? connectionsSize : commonConnectionsSize }}</span>
                  </div>
                </v-badge>
              </a>
              <v-card-text class="pa-1 subtitle-1 text-color">
                <span>{{ isCurrentUserProfile ? $t('homepage.profileStatus.connections') : $t('homepage.commonConnections.label') }}</span>
              </v-card-text>
            </v-card>
          </v-flex>
        </v-layout>
      </v-flex>
      <v-flex
        v-if="loadingWidgets || userPoints"
        d-flex
        xs12 
        align-center>
        <v-layout row mx-0>
          <v-flex
            id="profile-stats-weeklyPoints"
            xs6
            d-flex
            justify-center
            align-center>
            <v-card
              flat
              @click="openAchievementsDrawer()">
              <v-card-text class="headline text-color font-weight-bold pa-1">
                <span v-show="!loadingWidgets">{{ userPoints || '-' }}</span>
              </v-card-text>
              <v-card-text class="pa-1 subtitle-1 text-color">
                <span>{{ $t('homepage.profileStatus.weeklyPoints') }}</span>
              </v-card-text>
            </v-card>
          </v-flex>
          <v-flex
            id="profile-stats-weeklyRank"
            d-flex
            xs6
            justify-center
            align-center>
            <v-card
              align-center
              flat
              @click="$emit('flip')">
              <v-card-text class="headline text-color font-weight-bold pa-1">
                <span v-show="!loadingWidgets">{{ gamificationRank || '-' }}</span>
              </v-card-text>
              <v-card-text class="pa-1 subtitle-1 text-color">
                <span>{{ $t('homepage.profileStatus.weeklyRank') }}</span>
              </v-card-text>
            </v-card>
          </v-flex>
        </v-layout>
      </v-flex>
      <v-flex
        v-else
        d-flex
        xs12
        align-center
        justify-center>
        <div class="d-flex flex-column align-center justify-center">
          <v-icon color="secondary" size="54">fa-chart-pie</v-icon>
          <span class="subtitle-1 mt-3 text-wrap">{{ $t('gamification.overview.noWeeklyAchievements') }}</span>
        </div>
      </v-flex>
    </v-layout>
  </v-flex>
</template>
<script>
import {getUserInformations, getSpacesRequests, getConnectionsRequests, getGamificationPoints, getReputationStatus} from '../profilStatsAPI';
export default {
  props: {
    commonsSpaceDefaultSize: {
      type: Number,
      default: 0,
    },
    isCurrentUserProfile: {
      type: Boolean,
      default: false,
    },
    commonConnectionsSize: {
      type: Number,
      default: 0,
    },
  },
  data() {
    return {
      period: 'WEEK',
      firstName: '',
      avatar: '',
      spacesSize: '',
      spacesRequestsSize: '',
      connectionsSize: '',
      connectionsRequestsSize: '',
      gamificationRank: '',
      userPoints: '',
      loadingWidgets: 5,
      profileUrl: `${ eXo.env.portal.context }/${ eXo.env.portal.metaPortalName}/profile`,
      spacesUrl: `${ eXo.env.portal.context }/${ eXo.env.portal.metaPortalName}/spaces`,
      connexionsUrl: `${ eXo.env.portal.context }/${eXo.env.portal.metaPortalName}/profile`,
      firstLoadingName: true,
      firstLoadingSpaces: true,
      firstLoadingConnexion: true,
      firstLoadingUserPoints: true,
      firstLoadingRank: true,
    };
  },
  computed: {
    title() {
      return this.isCurrentUserProfile && this.$t('homepage.profileStatus.yourActivity') || this.$t('homepage.profileStatus.myActivity');
    },
  },
  watch: {
    loadingWidgets(newVal, oldVal) {
      if (!newVal && newVal !== oldVal) {
        this.$nextTick().then(() => this.$root.$applicationLoaded());
      }
    },
  },
  created() {
    document.addEventListener('userModified', event => {
      if (event && event.detail) {
        this.avatar = event.detail.avatar || event.detail.avatarUrl;
        this.firstName = event.detail.firstname;
      }
    });

    this.profileUrl = this.profileUrl + (this.isCurrentUserProfile ? '' : `/${ eXo.env.portal.profileOwner}`);

    this.loadingWidgets = 5;
    this.retrieveUserData().finally(() => this.loadingWidgets--);
    this.getSpacesRequestsSize().finally(() => this.loadingWidgets--);
    this.getConnectionsRequestsSize().finally(() => this.loadingWidgets--);
    this.getGamificationRank().finally(() => this.loadingWidgets--);
    this.getGamificationPoints().finally(() => this.loadingWidgets--);
  },
  methods: {
    retrieveUserData() {
      return getUserInformations().then(data => {
        this.avatar = data.avatar || data.avatarUrl;
        this.firstName = data.firstname;
        if (this.firstLoadingName) {
          this.firstLoadingName = false;
        }
        this.spacesSize = data.spacesCount;
        if (this.firstLoadingSpaces) {
          this.firstLoadingSpaces = false;
        }
        this.connectionsSize = data.connectionsCount;
        if (this.firstLoadingConnexion) {
          this.firstLoadingConnexion = false;
        }
      });
    },
    getSpacesRequestsSize() {
      return getSpacesRequests().then(
        (data) => {
          this.spacesRequestsSize = data.size;
          this.$emit('showRequestsSpace', this.spacesRequestsSize);
        }
      );
    },
    getConnectionsRequestsSize() {
      return getConnectionsRequests().then(
        (data) => {
          this.connectionsRequestsSize = data.size;
          this.$emit('shouldShowRequests', this.connectionsRequestsSize);
        }
      );
    },
    getGamificationRank() {
      return getReputationStatus().then(
        (data) => {
          this.gamificationRank = data.rank;
          if (this.firstLoadingRank) {
            this.firstLoadingRank = false;
          }
        }
      );
    },
    getGamificationPoints() {
      return getGamificationPoints(this.period)
        .then(points => {
          this.userPoints = points && Number(points) || 0;
          if (this.firstLoadingUserPoints) {
            this.firstLoadingUserPoints = false;
          }
        });
    },
    openAchievementsDrawer() {
      this.$emit('openAchievementsDrawer');
    },
    openConnectionsDrawer() {
      this.$emit('openConnectionsDrawer');
    },
    openSpaceDrawer() {
      this.$emit('openSpaceDrawer');
    },
  }
};
</script>

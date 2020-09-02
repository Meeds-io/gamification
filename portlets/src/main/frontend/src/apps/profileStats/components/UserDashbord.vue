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
    xs12 
    sm12>
    <v-layout 
      row 
      wrap 
      mx-0>
      <v-flex 
        d-flex 
        justify-center
        pt-4
        xs12>
        <v-card
          flat>
          <v-list-item>
            <a :href="profileUrl">
              <v-list-item-avatar>
                <v-img
                  :src="!firstLoadingName && avatar || ''"
                  :class="firstLoadingName && 'skeleton-background'" />
              </v-list-item-avatar>
            </a>
            <v-list-item-content>
              <v-list-item-title class="text-uppercase subtitle-1 profile-card-header">
                <span :class="firstLoadingName && 'skeleton-background skeleton-text skeleton-header skeleton-border-radius'">{{ $t('homepage.profileStatus.header') }} {{ firstName }}</span>
              </v-list-item-title>
            </v-list-item-content>
          </v-list-item>
        </v-card>
      </v-flex>
      <v-flex d-flex xs12>
        <v-layout
          row
          mx-0>
          <v-flex
            xs6
            d-flex
            justify-center
            align-center>
            <v-card
              flat
              :class="firstLoadingSpaces && 'skeleton-background skeleton-border-radius'">
              <a class="white--text">
                <v-badge :value="spacesRequestsSize > 0" class="badge-color">
                  <v-btn
                    slot="badge"
                    icon
                    dark
                    height="22"
                    width="22"
                    @click="getSpecificCard('spaces-requests')">{{ spacesRequestsSize }}
                  </v-btn>
                  <a
                    class="headline text-color font-weight-bold pa-1"
                    :href="spacesUrl">
                    <span :class="firstLoadingSpaces && 'skeleton-text'">{{ spacesSize }}</span>
                  </a>
                </v-badge>
              </a>
              <v-card-text class="pa-1 subtitle-1 text-color">
                <span :class="firstLoadingSpaces && 'skeleton-text'">{{ $t('homepage.profileStatus.spaces') }}</span>
              </v-card-text>
            </v-card>
          </v-flex>
          <v-flex
            d-flex
            xs6
            justify-center
            align-center>
            <v-card
              tile
              flat
              :class="firstLoadingConnexion && 'skeleton-background skeleton-border-radius'">
              <a class="white--text">
                <v-badge
                  :value="connectionsRequestsSize > 0"
                  pa-0
                  class="badge-color">
                  <v-btn
                    slot="badge"
                    icon
                    dark
                    height="20"
                    width="20"
                    @click="getSpecificCard('connections-requests')">{{ connectionsRequestsSize }}</v-btn>
                  <div class="headline text-color font-weight-bold pa-1" @click="openConnectionsDrawer">
                    <span :class="firstLoadingConnexion && 'skeleton-text'">{{ connectionsSize }}</span>
                  </div>
                </v-badge>
              </a>
              <v-card-text class="pa-1 subtitle-1 text-color">
                <span :class="firstLoadingConnexion && 'skeleton-text'">{{ $t('homepage.profileStatus.connections') }}</span>
              </v-card-text>
            </v-card>
          </v-flex>
        </v-layout>
      </v-flex>
      <v-flex 
        d-flex 
        xs12 
        align-center>
        <v-layout row mx-0>
          <v-flex
            xs6
            d-flex
            justify-center
            align-center>
            <v-card
              flat
              :class="firstLoadingUserPoints && 'skeleton-background skeleton-border-radius'">
              <a @click="getSpecificCard('user-points-widget')">
                <v-card-text class="headline text-color font-weight-bold pa-1">
                  <span :class="firstLoadingUserPoints && 'skeleton-text'">{{ userPoints }}</span>
                </v-card-text>
                <v-card-text class="pa-1 subtitle-1 text-color">
                  <span :class="firstLoadingUserPoints && 'skeleton-text'">{{ $t('homepage.profileStatus.weeklyPoints') }}</span>
                </v-card-text>
              </a>
            </v-card>
          </v-flex>
          <v-flex
            d-flex
            xs6
            justify-center
            align-center>
            <v-card
              flat
              :class="firstLoadingRank && 'skeleton-background skeleton-border-radius'"
              align-center>
              <a @click="getSpecificCard('gamification-rank')">
                <v-card-text class="headline text-color font-weight-bold pa-1">
                  <span :class="firstLoadingRank && 'skeleton-text'">{{ gamificationRank }}</span>
                </v-card-text>
                <v-card-text class="pa-1 subtitle-1 text-color">
                  <span :class="firstLoadingRank && 'skeleton-text'">{{ $t('homepage.profileStatus.weeklyRank') }}</span>
                </v-card-text>
              </a>
            </v-card>
          </v-flex>
        </v-layout>
      </v-flex>
    </v-layout>
  </v-flex>
</template>
<script>
  import {getUserInformations, getSpaces, getSpacesRequests, getConnections, getConnectionsRequests, getGamificationPoints, getReputationStatus} from '../profilStatsAPI'
  export default {
    data() {
      return {
        period: 'WEEK',
        firstName: '',
        avatar:'',
        spacesSize: '',
        spacesRequestsSize: '',
        connectionsSize: '',
        connectionsRequestsSize: '',
        gamificationRank:'',
        userPoints: '',
        profileUrl: `${ eXo.env.portal.context }/${ eXo.env.portal.portalName }/profile`,
        spacesUrl: `${ eXo.env.portal.context }/${ eXo.env.portal.portalName }/spaces`,
        connexionsUrl: `${ eXo.env.portal.context }/${ eXo.env.portal.portalName }/connexions/network`,
        firstLoadingName: true,
        firstLoadingSpaces: true,
        firstLoadingConnexion: true,
        firstLoadingUserPoints: true,
        firstLoadingRank: true
      }
    },
    
    created(){
      document.addEventListener('userModified', event => {
        if (event && event.detail) {
          this.avatar = event.detail.avatar || event.detail.avatarUrl;
          this.firstName = event.detail.firstname;
        }
      });

      this.retrieveUserData();
      this.getSpacesRequestsSize();
      this.getConnectionsRequestsSize();
      this.getGamificationRank();
      this.getGamificationPoints();
    },
    
    methods: {
      retrieveUserData() {
        return getUserInformations().then(data => {
          this.avatar = data.avatar || data.avatarUrl;
          this.firstName = data.firstname;
          if(this.firstLoadingName) {
            this.firstLoadingName = false;
          }
          this.spacesSize = data.spacesCount;
          if(this.firstLoadingSpaces) {
             this.firstLoadingSpaces = false;
          }
          this.connectionsSize = data.connectionsCount;
          if(this.firstLoadingConnexion) {
            this.firstLoadingConnexion = false;
          }
        })
      },
      getSpacesRequestsSize() {
        getSpacesRequests().then(
          (data) => {
            this.spacesRequestsSize = data.size;
          }
        )
      },
      getConnectionsRequestsSize() {
        getConnectionsRequests().then(
          (data) => {
            this.connectionsRequestsSize = data.size;
            this.$emit('shouldShowRequests', this.connectionsRequestsSize > 0)
          }
        )
      },
      getGamificationRank() {
          getReputationStatus().then(
              (data) => {
                  this.gamificationRank = data.rank;
                  if(this.firstLoadingRank) {
                    this.firstLoadingRank = false;
                  }
              }
          )
      },
      getGamificationPoints() {
        getGamificationPoints(this.period).then(
          (data) => {
            this.userPoints = data.points;
             if(this.firstLoadingUserPoints) {
               this.firstLoadingUserPoints = false;
             }
          }
        )
      },
      getSpecificCard(component) {
        this.$emit('specific-card',component);
      },
      openConnectionsDrawer() {
        this.$emit('openConnectionsDrawer');
      },
      toProfileStats() {
        this.$emit('isProfileStats');
      },
    }
  }
</script>
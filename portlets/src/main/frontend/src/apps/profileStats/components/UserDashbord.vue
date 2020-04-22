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
                  :class="firstLoadingName && 'skeleton-background'"/>
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
                  <a class="headline text-color font-weight-bold pa-1"
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
                  <a class="headline text-color font-weight-bold pa-1" :href="connexionsUrl">
                    <span :class="firstLoadingConnexion && 'skeleton-text'">{{ connectionsSize }}</span>
                  </a>
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
              :class="firstLoadingTotalPoints && 'skeleton-background skeleton-border-radius'">
              <a @click="getSpecificCard('total-points')">
                <v-card-text class="headline text-color font-weight-bold pa-1">
                  <span :class="firstLoadingTotalPoints && 'skeleton-text'">{{ totalPoints }}</span>
                </v-card-text>
                <v-card-text class="pa-1 subtitle-1 text-color">
                  <span :class="firstLoadingTotalPoints && 'skeleton-text'">{{ $t('homepage.profileStatus.totalPoints') }}</span>
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
                  <span :class="firstLoadingRank && 'skeleton-text'">{{ $t('homepage.profileStatus.totalRank') }}</span>
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
        firstName: '',
        avatar:'',
        spacesSize: '',
        spacesRequestsSize: '',
        connectionsSize: '',
        connectionsRequestsSize: '',
        gamificationRank:'',
        totalPoints: '',
        profileUrl: `${ eXo.env.portal.context }/${ eXo.env.portal.portalName }/profile`,
        spacesUrl: `${ eXo.env.portal.context }/${ eXo.env.portal.portalName }/spaces`,
        connexionsUrl: `${ eXo.env.portal.context }/${ eXo.env.portal.portalName }/connexions/network')`,
        firstLoadingName: true,
        firstLoadingSpaces: true,
        firstLoadingConnexion: true,
        firstLoadingTotalPoints: true,
        firstLoadingRank: true
      }
    },
    
    created(){
      this.avatar=`/portal/rest/v1/social/users/${eXo.env.portal.profileOwner}/avatar`;
      this.getFirstName();
      this.getSpacesSize();
      this.getSpacesRequestsSize();
      this.getConnectionsSize();
      this.getConnectionsRequestsSize();
      this.getGamificationRank();
      this.getGamificationPoints();

    },
    
    methods: {
      getFirstName() {
        getUserInformations().then(
          (data) => {
            this.firstName = data.firstname;
            if(this.firstLoadingName) {
              this.firstLoadingName = false;
            }
          }
        )
      },
      getSpacesSize() {
        getSpaces().then(
          (data) => {
            this.spacesSize = data.size;
             if(this.firstLoadingSpaces) {
              this.firstLoadingSpaces = false;
            }
          }
        )
      },
      getSpacesRequestsSize() {
        getSpacesRequests().then(
          (data) => {
            this.spacesRequestsSize = data.size;
          }
        )
      },
      getConnectionsSize() {
        getConnections().then(
          (data) => {
            this.connectionsSize = data.size;
             if(this.firstLoadingConnexion) {
              this.firstLoadingConnexion = false;
            }
          }
        )
      },
      getConnectionsRequestsSize() {
        getConnectionsRequests().then(
          (data) => {
            this.connectionsRequestsSize = data.size;
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
        getGamificationPoints().then(
          (data) => {
            this.totalPoints = data.points;
             if(this.firstLoadingTotalPoints) {
               this.firstLoadingTotalPoints = false;
             }
          }
        )
      },
      getSpecificCard(component) {
        this.$emit('specific-card',component);
      },
      toProfileStats() {
        this.$emit('isProfileStats');
      },
    }
  }
</script>
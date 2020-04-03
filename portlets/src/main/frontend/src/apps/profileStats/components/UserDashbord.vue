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
                  :src="avatar" />
              </v-list-item-avatar>
            </a>
            <v-list-item-content>
              <v-list-item-title class="text-uppercase subtitle-1 profile-card-header">{{ this.$t('homepage.profileStatus.header') }} {{ firstName }}</v-list-item-title>
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
              flat>
              <a class="white--text">
                <v-badge :value="spacesRequestsSize > 0" class="badge-color">
                  <v-btn
                    slot="badge"
                    icon
                    dark
                    height="22"
                    width="22"
                    @click="getSpecificCard('spaces-requests')">{{ spacesRequestsSize }}</v-btn>
                  <a class="headline blue-grey--text font-weight-bold pa-1" :href="spacesUrl">{{ spacesSize }}</a>
                </v-badge>
              </a>
              <v-card-text class="pa-1 subtitle-1 blue-grey--text">{{ this.$t('homepage.profileStatus.spaces') }}</v-card-text>
            </v-card>
          </v-flex>
          <v-flex
            d-flex
            xs6
            justify-center
            align-center>
            <v-card
              tile
              flat>
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
                  <a class="headline blue-grey--text font-weight-bold pa-1" :href="connexionsUrl">{{ connectionsSize }}</a>
                </v-badge>
              </a>
              <v-card-text class="pa-1 subtitle-1 blue-grey--text">{{ this.$t('homepage.profileStatus.connections') }}</v-card-text>
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
              flat>
              <a @click="getSpecificCard('total-points')">
                <v-card-text class="headline blue-grey--text font-weight-bold pa-1">{{ totalPoints }}</v-card-text>
                <v-card-text class="pa-1 subtitle-1 blue-grey--text">{{ this.$t('homepage.profileStatus.totalPoints') }}</v-card-text>
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
              color="transparent"
              align-center>
              <a @click="getSpecificCard('gamification-rank')">
                <v-card-text class="headline blue-grey--text font-weight-bold pa-1">{{ gamificationRank }}</v-card-text>
                <v-card-text class="pa-1 subtitle-1 blue-grey--text">{{ this.$t('homepage.profileStatus.totalRank') }}</v-card-text>
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
        connexionsUrl: `${ eXo.env.portal.context }/${ eXo.env.portal.portalName }/connexions/network')`
      }
    },
    
    created(){
      this.avatar=`/portal/rest/v1/social/users/${eXo.env.portal.userName}/avatar`;
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
          }
        )
      },
      getSpacesSize() {
        getSpaces().then(
          (data) => {
            this.spacesSize = data.size;
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
              }
          )
      },
      getGamificationPoints() {
        getGamificationPoints().then(
          (data) => {
            this.totalPoints = data.points;
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
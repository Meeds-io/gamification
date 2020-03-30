<template>
  <v-layout
    row
    wrap
    mx-0>
    <v-flex
      d-flex
      xs12
      my-5>
      <v-layout
        row
        wrap
        mx-2
        align-start>
        <v-flex
          d-flex
          xs12
          mt-n2
          justify-center>
          <div>
            <span class="pr-2 text-uppercase subtitle-2 profile-card-header">{{ this.$t('homepage.profileStatus.connectionsRequests') }}</span>
            <v-btn
              fab
              depressed
              dark
              height="20"
              width="20"
              class="mb-1 header-badge-color">
              <span class="white--text caption">{{ connectionsRequestsSize }}</span>
            </v-btn>
          </div>
        </v-flex>
        <v-flex
          d-flex
          xs12
          mt-n6>
          <v-icon
            color="grey darken-2"
            size="20"
            @click="toProfileStats()">mdi-arrow-left</v-icon>
        </v-flex>
      </v-layout>
    </v-flex>
    <v-flex
      xs12>
      <v-list height="180">
        <template v-for="item in sort(connectionsRequests)">
          <v-list-item
            :key="item.id"
            class="py-0 px-2">
            <v-list-item-avatar class="my-1 mr-2" size="30">
              <v-img :src="item.senderAvatar"/>
            </v-list-item-avatar>

            <v-list-item-content class="py-0">
              <v-list-item-title class="font-weight-bold subtitle-2 request-user-name darken-2" v-html="item.senderFullName"/>
              <v-list-item-subtitle class="caption grey-color" v-text="item.commonConnections+ ' ' + $t('homepage.profileStatus.commonConnections')"/>
            </v-list-item-content>
            <v-list-item-action>
              <v-btn-toggle
                class="transparent"
                dark>
                <v-btn
                  text
                  icon
                  small
                  min-width="auto"
                  class="px-0 connexion-accept-btn"
                  @click="replyInvitationToConnect(item.id, 'confirmed')">
                  <v-icon size="20">mdi-checkbox-marked-circle</v-icon>
                </v-btn>
                <v-btn
                  text
                  small
                  min-width="auto"
                  class="px-0 connexion-refuse-btn"
                  @click="replyInvitationToConnect(item.id, 'ignored')">
                  <v-icon size="20">mdi-close-circle</v-icon>
                </v-btn>
              </v-btn-toggle>
            </v-list-item-action>
          </v-list-item>
        </template>
      </v-list>
    </v-flex>
    <v-flex
      d-flex
      xs12
      px-4
      pb-2
      justify-end>
      <v-btn
        v-if="connectionsRequestsSize > 3"
        depressed
        small
        class="caption text-uppercase grey--text"
        @click="navigateTo('connexions/receivedInvitations')">{{ this.$t('homepage.seeAll') }}</v-btn>
    </v-flex>
  </v-layout>

</template>
<script>
  import {getConnectionsRequests, getCommonConnections, replyInvitationToConnect} from '../profilStatsAPI'
  export default {
    data() {
      return {
        connectionsRequests: [],
        connectionsRequestsSize: '',
        items: []
      }
    },
    created(){
      this.getConnectionsRequests();
    },

    methods: {
      getConnectionsRequests() {
        this.connectionsRequests = [];
        getConnectionsRequests().then(
          (data) => {
            this.connectionsRequestsSize = data.size;
            if (this.connectionsRequestsSize === 0) {
              this.toProfileStats();
            } 
            else {
              for (let i = 0; i < data.relationships.length; i++) {
                const connection = {};
                connection.id = data.relationships[i].id;
                fetch(`${data.relationships[i].sender}`, {
                  method: 'GET',
                }).then((resp) => {
                  if(resp && resp.ok) {
                    return resp.json();
                  } 
                  else {
                    throw new Error ('Error when getting connection request sender');
                  }
                }).then((data) => {
                  connection.senderAvatar = data.avatar !== undefined ? data.avatar : `/rest/v1/social/users/${data.username}/avatar`;
                  connection.senderFullName = data.fullname;
                  getCommonConnections(data.id).then((data) => {
                    connection.commonConnections = data.size;
                    this.connectionsRequests.splice(i, 0, connection);
                  });
                })
              }
            }
          }
        )
      },
      toProfileStats() {
        this.$emit('isProfileStats');
      },
      sort(array) {
        return array.slice().sort(function(a, b) {
          return a.senderFullName.localeCompare(b.senderFullName);
        });
      },
      replyInvitationToConnect(relationId, reply) {
        replyInvitationToConnect(relationId, reply).then(
          (data) => {
            if (this.connectionsRequestsSize === 1) {
              this.toProfileStats();
            } 
            else {
              this.getConnectionsRequests();
            }
          }
        )
      },
      navigateTo(pagelink) {
        location.href=`${ eXo.env.portal.context }/${ eXo.env.portal.portalName }/${ pagelink }` ;
      },
    }
  }
</script>

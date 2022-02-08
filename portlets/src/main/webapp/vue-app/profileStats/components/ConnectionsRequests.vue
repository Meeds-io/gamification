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
  <v-layout
    row
    wrap
    mx-0>
    <v-flex
      d-flex
      xs12
      my-3>
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
          <span class="pe-2 text-uppercase subtitle-2 profile-card-header">{{ $t('homepage.profileStatus.connectionsRequests') }}</span>
          <v-btn
            fab
            depressed
            dark
            height="20"
            width="20"
            class="mb-1 header-badge-color"
            :href="receivedInvitationsUrl">
            <span class="white--text caption">{{ connectionsRequestsSize }}</span>
          </v-btn>
        </v-flex>
      </v-layout>
    </v-flex>
    <v-flex
      xs12>
      <v-list>
        <template v-for="item in sort(connectionsRequests)">
          <v-list-item
            :key="item.id"
            class="py-0 px-2">
            <v-list-item-avatar class="my-1 me-2" size="30">
              <v-img :src="item.senderAvatar" />
            </v-list-item-avatar>

            <v-list-item-content class="py-0">
              <v-list-item-title class="font-weight-bold subtitle-2 request-user-name darken-2" v-html="item.senderFullName" />
              <v-list-item-subtitle class="caption grey-color" v-text="item.commonConnections+ ' ' + $t('homepage.profileStatus.commonConnections')" />
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
                  icon
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
  </v-layout>
</template>
<script>
import {getConnectionsRequests, getCommonConnections, replyInvitationToConnect} from '../profilStatsAPI';
export default {
  data() {
    return {
      connectionsRequests: [],
      connectionsRequestsSize: '',
      items: [],
      receivedInvitationsUrl: `${ eXo.env.portal.context }/${ eXo.env.portal.portalName }/connexions/receivedInvitations`,
    };
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
          this.$emit('shouldShowRequests', this.connectionsRequestsSize);
          if (this.connectionsRequestsSize > 0) {
            for (let i = 0; i < data.relationships.length; i++) {
              const connection = {};
              connection.id = data.relationships[i].id;
              fetch(`${data.relationships[i].sender}`, {
                method: 'GET',
                credentials: 'include',
              }).then((resp) => {
                if (resp && resp.ok) {
                  return resp.json();
                } 
                else {
                  throw new Error ('Error when getting connection request sender');
                }
              }).then((data) => {
                connection.senderAvatar = data.avatar || data.avatarUrl || `/rest/v1/social/users/${data.username}/avatar`;
                connection.senderFullName = data.fullname;
                getCommonConnections(data.id).then((data) => {
                  connection.commonConnections = data.size;
                  this.connectionsRequests.splice(i, 0, connection);
                });
              });
            }
          }
        }
      );
    },
    sort(array) {
      return array.slice().sort(function(a, b) {
        return a.senderFullName.localeCompare(b.senderFullName);
      });
    },
    replyInvitationToConnect(relationId, reply) {
      replyInvitationToConnect(relationId, reply)
        .then(() => {
          if (reply === 'confirmed') {
            const confirmedRequest = this.connectionsRequests.filter(request => request.id === relationId)[0];
            const connection = {
              id: confirmedRequest.id,
              fullname: confirmedRequest.senderFullName,
              avatar: confirmedRequest.senderAvatar,
                
            };
            this.$emit('invitationReplied', connection);
          } 
          this.getConnectionsRequests();
        }
        );
    },
  }
};
</script>

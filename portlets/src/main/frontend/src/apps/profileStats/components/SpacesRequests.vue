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
            <span class="pe-2 text-uppercase spaceRequestedTitle subtitle-2 profile-card-header" @click="openSpaceRequests()">{{ this.$t('homepage.profileStatus.spaceRequests') }}</span>
            <v-btn
              fab
              depressed
              dark
              height="20"
              width="20"
              class="mb-1 header-badge-color">
              <span class="white--text caption">{{ spacesRequestsSize }}</span>
            </v-btn>
          </div>
        </v-flex>
      </v-layout>
    </v-flex>
    <v-flex
      xs12>
      <v-list>
        <template v-for="item in sort(spacesRequests)">
          <v-list-item
            :key="item.id"
            class="py-0 px-2">
            <v-list-item-avatar class="my-1 me-2" size="30">
              <v-img :src="item.avatar" />
            </v-list-item-avatar>

            <v-list-item-content class="py-0">
              <v-list-item-title class="font-weight-bold subtitle-2 request-user-name darken-2" v-text="item.displayName" />
              <v-list-item-subtitle class="caption grey-color" v-text="item.description" />
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
                  @click="replyInvitationToJoinSpace(item.id, 'approved')">
                  <v-icon color="primary-color" size="20">mdi-checkbox-marked-circle</v-icon>
                </v-btn>
                <v-btn 
                  text
                  icon
                  small
                  min-width="auto"
                  class="px-0 connexion-refuse-btn"
                  @click="replyInvitationToJoinSpace(item.id, 'ignored')">
                  <v-icon color="grey lighten-1" size="20">mdi-close-circle</v-icon>
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
      justify-center>
      <v-btn
        v-if="spacesRequestsSize > 3"
        depressed
        small
        class="caption text-uppercase grey--text"
        :href="invitationSpaceUrl">
        {{ this.$t('homepage.seeAll') }}
      </v-btn>
    </v-flex>
  </v-layout>
</template>
<script>
import {getSpacesRequests, replyInvitationToJoinSpace} from '../profilStatsAPI';
export default {
  data() {
    return {
      spacesRequests: [],
      spacesRequestsSize: '',
      invitationSpaceUrl: `${ eXo.env.portal.context }/${ eXo.env.portal.portalName }/invitationSpace`
    };
  },
  created(){
    this.getSpacesRequests();
  },
  
  methods: {
    getSpacesRequests() {
      this.spacesRequests = [];
      getSpacesRequests().then(
        (data) => {
          this.spacesRequestsSize = data.size;
          this.$emit('showRequestsSpace', this.spacesRequestsSize);
          if (this.spacesRequestsSize > 0) {
            for (let i = 0; i < data.spacesMemberships.length; i++) {
              const spaceRequest = {};
              spaceRequest.id = data.spacesMemberships[i].id;
              fetch(`${data.spacesMemberships[i].space}`, {
                method: 'GET',
                credentials: 'include',
              }).then((resp) => {
                if (resp && resp.ok) {
                  return resp.json();
                }
                else {
                  throw new Error ('Error when getting space');
                }
              }).then((data) => {
                spaceRequest.avatar = data.avatarUrl || data.avatarUrl || `/portal/rest/v1/social/spaces/${spaceRequest.id.split(':')[0]}/avatar`;
                spaceRequest.displayName = data.displayName;
                this.spacesRequests.splice(i, 0, spaceRequest);
              });
            }
          }
        }
      );
    },
    sort(array) {
      return array.slice().sort(function(a, b) {
        return a.displayName.localeCompare(b.displayName);
      });
    },
    openSpaceRequests() {
      window.location.href =  `${this.invitationSpaceUrl}`;
    },
    replyInvitationToJoinSpace(spaceId, reply) {
      replyInvitationToJoinSpace(spaceId, reply)
        .then(() => {
          if (reply === 'approved') {
            const confirmedRequest = this.spacesRequests.filter(request => request.id === spaceId)[0];
            const space = {
              id: confirmedRequest.id,
              displayName: confirmedRequest.displayName,
              avatarUrl: confirmedRequest.avatar,

            };
            this.$emit('invitationReplied', space);
          }
          this.getSpacesRequests();
        });
    },
  }
};
</script>

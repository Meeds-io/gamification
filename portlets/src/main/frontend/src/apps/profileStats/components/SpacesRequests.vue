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
            <span class="pr-2 text-uppercase subtitle-2 profile-card-header">{{ this.$t('homepage.profileStatus.spaceRequests') }}</span>
            <v-btn
              color="primary-color"
              fab
              depressed
              dark
              height="20"
              width="20"
              class="mb-1">
              <span class="white--text caption">{{ spacesRequestsSize }}</span>
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
      xs12 
      style="height: 180px">
      <v-list>
        <template v-for="item in sort(spacesRequests)">
          <v-list-item
            :key="item.id"
            class="py-0 px-2">
            <v-list-item-avatar class="my-1 mr-2" size="30">
              <v-img :src="item.avatar"/>
            </v-list-item-avatar>

            <v-list-item-content class="py-0">
              <v-list-item-title class="font-weight-bold subtitle-2 request-user-name darken-2" v-text="item.displayName"/>
              <v-list-item-subtitle class="caption grey-color" v-text="item.description"/>
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
      justify-end>
      <v-btn
        v-if="spacesRequestsSize > 3"
        depressed
        small
        class="caption text-uppercase grey--text"
        :href="invitationSpaceUrl">{{ this.$t('homepage.seeAll') }}</v-btn>
    </v-flex>
  </v-layout>
</template>
<script>
  import {getSpacesRequests, replyInvitationToJoinSpace} from '../profilStatsAPI'
  export default {
    data() {
      return {
        spacesRequests: [],
        spacesRequestsSize: '',
        invitationSpaceUrl: `${ eXo.env.portal.context }/${ eXo.env.portal.portalName }/invitationSpace`
      }
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
            if (this.spacesRequestsSize === 0) {
              this.toProfileStats();
            } 
            else {
              for (let i = 0; i < data.spacesMemberships.length; i++) {
                const spaceRequest = {};
                spaceRequest.id = data.spacesMemberships[i].id;
                fetch(`${data.spacesMemberships[i].space}`, {
                  method: 'GET',
                }).then((resp) => {
                  if(resp && resp.ok) {
                    return resp.json();
                  } 
                  else {
                    throw new Error ('Error when getting space');
                  }
                }).then((data) => {
                  spaceRequest.avatar = data.avatarUrl !== undefined ? data.avatarUrl : `/portal/rest/v1/social/spaces/${spaceRequest.id.split(":")[0]}/avatar`;
                  spaceRequest.displayName = data.displayName;
                  spaceRequest.description = data.description;
                  this.spacesRequests.splice(i, 0, spaceRequest);
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
          return a.displayName.localeCompare(b.displayName);
        });
      },
      replyInvitationToJoinSpace(spaceId, reply) {
        replyInvitationToJoinSpace(spaceId, reply).then(
          (data) => {
            if (this.spacesRequestsSize === 1) {
              this.toProfileStats();
            } 
            else {
              this.getSpacesRequests();
            }
          }
        )
      },
    }
  }
</script>

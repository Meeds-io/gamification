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
            <span class="pe-2 spaceRequestedTitle profile-card-header" @click="openSpaceRequests()">{{ $t('homepage.profileStatus.spaceRequests') }}</span>
            <v-btn
              fab
              depressed
              dark
              height="20"
              width="20"
              class="mb-1 header-badge-color">
              <span class="white--text text-subtitle">{{ spacesRequestsSize }}</span>
            </v-btn>
          </div>
        </v-flex>
      </v-layout>
    </v-flex>
    <v-flex
      xs12>
      <v-list>
        <v-list-item
          v-for="item in sort(spacesRequests)"
          :key="item.id"
          class="py-0 px-2">
          <v-list-item-avatar class="my-1 me-2" size="30">
            <v-img :src="item.avatarUrl" />
          </v-list-item-avatar>

          <v-list-item-content class="py-0">
            <v-list-item-title class="request-user-name darken-2" v-text="item.displayName" />
            <v-list-item-subtitle v-sanitized-html="item.description" />
          </v-list-item-content>
          <v-list-item-action>
            <v-btn-toggle
              class="transparent"
              dark>
              <v-btn
                :loading="saving"
                text
                icon
                small
                min-width="auto"
                class="px-0 connexion-accept-btn"
                @click="replyInvitationToJoinSpace(item, 'approved')">
                <v-icon color="success" size="20">mdi-checkbox-marked-circle</v-icon>
              </v-btn>
              <v-btn
                :loading="saving"
                text
                icon
                small
                min-width="auto"
                class="px-0 connexion-refuse-btn"
                @click="replyInvitationToJoinSpace(item, 'ignored')">
                <v-icon color="error" size="20">mdi-close-circle</v-icon>
              </v-btn>
            </v-btn-toggle>
          </v-list-item-action>
        </v-list-item>
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
        {{ $t('homepage.seeAll') }}
      </v-btn>
    </v-flex>
  </v-layout>
</template>
<script>
export default {
  data() {
    return {
      spacesRequests: [],
      spacesRequestsSize: '',
      saving: false,
      invitationSpaceUrl: `${eXo.env.portal.context}/${eXo.env.portal.metaPortalName}/spaces/receivedInvitations`
    };
  },
  created(){
    this.getSpacesRequests();
  },
  methods: {
    getSpacesRequests() {
      this.spacesRequests = [];
      return this.$spaceService.getSpaceMemberships({
        user: eXo.env.portal.userName,
        status: 'invited',
        expand: 'spaces',
        returnSize: true,
        limit: 3,
      }).then((data) => {
        this.spacesRequestsSize = data.size;
        this.$emit('showRequestsSpace', this.spacesRequestsSize);
        this.spacesRequests = data.spacesMemberships?.map?.(m => m.space);
      });
    },
    sort(array) {
      return array.slice().sort(function(a, b) {
        return a.displayName.localeCompare(b.displayName);
      });
    },
    openSpaceRequests() {
      window.location.href = `${this.invitationSpaceUrl}`;
    },
    async replyInvitationToJoinSpace(item, reply) {
      this.saving = true;
      try {
        if (reply === 'approved') {
          await this.$spaceService.accept(item.id);
          this.$emit('invitationReplied', {
            id: item.id,
            displayName: item.displayName,
            avatarUrl: item.avatarUrl,
          });
        } else if (reply === 'ignored') {
          await this.$spaceService.deny(item.id);
        }
        this.getSpacesRequests();
      } finally {
        this.saving = false;
      }
    },
  }
};
</script>

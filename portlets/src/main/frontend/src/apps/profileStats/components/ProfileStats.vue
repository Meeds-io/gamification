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
  <v-app
    class="ms-md-2"
    flat
    dark>
    <v-container pa-0>
      <v-layout
        row
        wrap
        mx-0
        class="white profileCard">
        <user-dashbord
          v-if="!isFlipped"
          :key="userDashBordKey"
          class="profileFlippedCard profileStats"
          :commons-space-default-size="commonsSpaceDefaultSize"
          :is-current-user-profile="isCurrentUserProfile"
          :common-connections-size="commonConnections.length"
          @specific-card="setFlippedCard"
          @openConnectionsDrawer="openConnectionsDrawer"
          @openSpaceDrawer="openSpaceDrawer"
          @shouldShowRequests="shouldShowRequests"
          @showRequestsSpace="showRequestsSpace" />
        <v-flex
          :is="currentComponent"
          v-if="isFlipped"
          d-flex
          xs12
          sm12
          class="profileFlippedCard ConnexionsRequests"
          @isProfileStats="setFlippedCard" />
      </v-layout>
    </v-container>
    <achievements-drawer ref="achievementsDrawer" :user-points="userPoints" />
    <connections-drawer
      :connections-drawer="connectionsDrawer"
      :connection-requests="connectionRequests"
      :is-current-user-profile="isCurrentUserProfile"
      :common-connections="commonConnections"
      @closeDrawer="closeConnectionsDrawer" />
    <space-drawer
      :space-drawer="spaceDrawer"
      :space-requests="spaceRequests"
      :is-current-user-profile="isCurrentUserProfile"
      :commons-space-default-size="commonsSpaceDefaultSize"
      @closeDrawer="closeSpaceDrawer" />
  </v-app>
</template>
<script>
import {getCommonsSpaces, getCommonConnections} from '../profilStatsAPI';
export default {
  data: function() {
    return {
      currentComponent: null,
      isFlipped: false,
      connectionsDrawer: false,
      spaceDrawer: false,
      connectionRequests: null,
      spaceRequests: null,
      userDashBordKey: 0,
      commonsSpaceDefaultSize: 0,
      isCurrentUserProfile: false,
      commonConnections: [],
      PROFILE_URI: `${eXo.env.portal.context}/${eXo.env.portal.portalName}/profile/`,
    };
  },
  created() {
    this.$root.$on('open-achievement', (userPoints) => {
      this.$refs.achievementsDrawer.open(userPoints);
    });
    this.isCurrentUserProfile = eXo.env.portal.userName === eXo.env.portal.profileOwner;
    if (!this.isCurrentUserProfile) {
      this.commonsSpaces();
      this.retrieveCommonConnections(parseInt(eXo.env.portal.profileOwnerIdentityId));
    }
  },
  methods: {
    setFlippedCard(component) {
      const profileWrapper = document.querySelector('.profileCard');
      profileWrapper.classList.toggle('is-flipped');
      this.currentComponent = component;
      this.isFlipped = !this.isFlipped;
    },
    commonsSpaces() {
      getCommonsSpaces(this.offset, this.limitToFetch).then(data => {
        this.commonsSpaceDefaultSize = data.size;
      });
    },
    retrieveCommonConnections(id) {
      getCommonConnections(id)
        .then(data => {
          const identities = [];
          identities.push(...data.identities);
          this.commonConnections = identities.map(identity => identity.profile);
          this.commonConnections.forEach(commonConnection => {
            commonConnection.profileLink = this.PROFILE_URI + commonConnection.username;
          });
          return this.$nextTick();
        })
        .finally(() => this.$root.$emit('application-loaded'));
    },
    openConnectionsDrawer() {
      this.connectionsDrawer = true;
    },
    closeConnectionsDrawer() {
      this.reRenderUserDashBord();
      this.connectionsDrawer = false;
    },
    shouldShowRequests(requests) {
      this.connectionRequests = requests;
    },
    showRequestsSpace(spaces) {
      this.spaceRequests = spaces;
    },
    reRenderUserDashBord() {
      this.userDashBordKey += 1;
    },
    openSpaceDrawer() {
      this.spaceDrawer = true;
    },
    closeSpaceDrawer() {
      this.reRenderUserDashBord();
      this.spaceDrawer = false;
    },
  }
};
</script>

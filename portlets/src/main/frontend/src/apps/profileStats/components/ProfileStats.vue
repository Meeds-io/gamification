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
    id="profile-stats-portlet"
    class="ml-md-2"
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
          class="profileFlippedCard profileStats"
          @specific-card="setFlippedCard" />
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
  </v-app>
</template>
<script>
    export default {
        data: function() {
          return {
            currentComponent: null,
            isFlipped: false
          };
        },
        created() {
          this.$root.$on('open-achievement', (userPoints) => {
            this.$refs.achievementsDrawer.open(userPoints);
          });
        },
        methods: {
            setFlippedCard(component) {
                const profileWrapper = document.querySelector('.profileCard');
                profileWrapper.classList.toggle('is-flipped');
                this.currentComponent = component;
                this.isFlipped = !this.isFlipped;
            },
        }
    }
</script>
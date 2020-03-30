<template>
  <v-app
    id="profile-stats-portlet"
    class="ml-md-2"
    flat
    dark>
    <main>
      <v-container pa-0>
        <v-layout
          row
          wrap
          mx-0
          class="white profileCard">
          <user-dashboard
            v-if="!isFlipped"
            class="profileFlippedCard profileStats"
            @specificCard="setFlippedCard"/>
          <v-flex
            v-if="isFlipped"
            :is="currentComponent"
            d-flex
            xs12
            sm12
            class="profileFlippedCard ConnexionsRequests"
            @isProfileStats="setFlippedCard"/>
        </v-layout>
      </v-container>
    </main>
  </v-app>
</template>
<script>
    import UserDashboard from './UserDashbord.vue';
    import ConnectionsRequests from './ConnectionsRequests.vue';
    import TotalPoints from './TotalPoints.vue';
    import SpacesRequests from './SpacesRequests.vue';
    import GamificationRank from "./GamificationRank.vue";
    export default {
        components: {
            UserDashboard,
            ConnectionsRequests,
            TotalPoints,
            SpacesRequests,
            GamificationRank
        },

        data: function() {
          return {
            currentComponent: null,
            isFlipped: false
          };
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
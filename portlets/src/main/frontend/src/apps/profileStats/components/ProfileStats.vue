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
    </main>
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
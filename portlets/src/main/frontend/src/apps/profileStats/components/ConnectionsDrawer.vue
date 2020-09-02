<template>
  <v-navigation-drawer
    v-model="connectionsDrawer"
    absolute
    right
    stateless
    temporary
    width="420"
    max-width="100vw"
    max-height="100vh"
    class="appCenterDrawer"
  >
  </v-navigation-drawer>
</template>

<script>
  export default {
    props: {
      connectionsDrawer: {
        type: Boolean,
        default: false,
      },
    },
    watch: {
      connectionsDrawer() {
        if (this.connectionsDrawer) {
          $('body').addClass('hide-scroll');
          console.log('### Works: ', $('#profile-stats-portlet .v-application--wrap .v-overlay'));

          this.$nextTick().then(() => {
            $('#profile-stats-portlet .v-overlay').click(() => {
              this.closeDrawer();
            });
          });
        } else {
          $('body').removeClass('hide-scroll');
        }
      },
    },
    methods: {
      closeDrawer() {
        this.$emit('closeDrawer');  
      },
    }
  }
</script>
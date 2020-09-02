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
    class="connectionsDrawer"
  >
    <v-row class="mx-0 title">
      <v-list-item>
        <v-list-item-content>
            <span class="connectionsDrawerTitle">{{
              $t("homepage.profileStatus.connections")
            }}</span>
        </v-list-item-content>
        <v-list-item-action>
          <v-btn
            icon
            class="rightIcon"
            @click="closeDrawer">
            <v-icon
              small
              class="closeIcon">
              close
            </v-icon>
          </v-btn>
        </v-list-item-action>
      </v-list-item>
    </v-row>
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
    data: () => ({
      connections: null,
    }),
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
    created() {
      console.log('#### Created !!!');
    },
      methods: {
      closeDrawer() {
        this.$emit('closeDrawer');  
      },
    }
  }
</script>
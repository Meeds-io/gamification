<template>
  <v-navigation-drawer
    v-model="connectionsDrawer"
    absolute
    right
    stateless
    temporary
    class="connectionsDrawer"
  >
    <v-row class="mx-0 title">
      <v-list-item>
        <v-list-item-content>
          <span class="connectionsDrawerTitle">{{ $t("homepage.profileStatus.connections") }}</span>
        </v-list-item-content>
        <v-list-item-action>
          <v-text-field
            v-model="search"
            :placeholder="$t(`profile.label.search.connections`)"
            class="connectionsSearch"
            single-line
            solo
            flat
            hide-details>
          </v-text-field>
        </v-list-item-action>
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

    <v-divider class="my-0" />

    <div class="content">
      <v-row v-if="showConnectionRequests" class="px-4">
        <v-col>
          <connections-requests @invitationReplied="refreshConnections" @shouldShowRequests="updateRequestsSize" ></connections-requests>
        </v-col>
      </v-row>
      <v-row v-if="showConnectionRequests" class="px-4">
        <v-col>
          <v-divider class="my-0" />
        </v-col>
      </v-row>
      <v-row class="px-4">
        <v-col>
          <v-list-item
            v-if="showConnection"
            v-for="item in filteredConnections"
            :key="item.id"
            class="py-0 px-2">
            <v-list-item-avatar class="my-1 mr-2" size="30">
              <v-img :src="item.avatar" />
            </v-list-item-avatar>

            <v-list-item-content class="py-0">
              <v-list-item-title class="font-weight-bold subtitle-2 request-user-name darken-2" v-html="item.fullname" />
            </v-list-item-content>
          </v-list-item>
          <v-row v-else class="d-flex text-center noPeopleYetBlock my-12">
            <div class="ma-auto noPeopleYet">
              <p class="noPeopleYetIcons">
                <v-icon>fa-users</v-icon>
              </p>
              <p class="title font-weight-bold">
                {{ $t('peopleList.label.noConnection') }}
              </p>
            </div>
          </v-row>
        </v-col>
      </v-row>
    </div>
  </v-navigation-drawer>
</template>

<script>
  import {getUserConnections} from '../profilStatsAPI';
  export default {
    props: {
      connectionsDrawer: {
        type: Boolean,
        default: false,
      },
      connectionRequests: {
        type: Number,
        default: 0,
      },
    },
    data: () => ({
      connections: [],
      search: null,
    }),
    computed: {
      filteredConnections() {
        if (this.search) {
          return this.connections.filter(item => item.fullname.toLowerCase().match(this.search.toLowerCase())); 
        } else {
          return this.connections;
        }
      },
      showConnectionRequests() {
        return this.connectionRequests > 0;
      },
      showConnection() {
        return this.connections.length > 0;
      },
    },
    watch: {
      connectionsDrawer() {
        if (this.connectionsDrawer) {
          $('body').addClass('hide-scroll');

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
      this.initConnections();
    },
      methods: {
      closeDrawer() {
        this.$emit('closeDrawer');  
      },
      initConnections() {
        getUserConnections().then(data => {
          this.connections = data.users;
        });
      },
      refreshConnections(connection) {
        this.connections.unshift(connection);
      },
      updateRequestsSize(requests) {
        this.connectionRequests = requests;
      },
    }
  }
</script>
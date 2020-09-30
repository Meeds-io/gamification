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
      <v-list-item class="pr-0">
        <v-list-item-content  class="ma-0 pa-0">
          <template v-if="!showSearch">
            <span class="connectionsDrawerTitle">{{ $t("homepage.profileStatus.connections") }}</span>
          </template>
          <template v-else>
            <v-row>
              <v-text-field
                v-model="search"
                :placeholder="$t(`profile.label.search.connections`)"
                class="connectionsSearch pa-0"
                single-line
                solo
                flat
                hide-details>
              </v-text-field>
            </v-row>
          </template>
        </v-list-item-content>
        <v-list-item-action class="ma-0">
          <template v-if = "!showSearch">
            <v-icon size="20" @click="openConnectionSearch">mdi-filter</v-icon>
          </template>
          <template v-else>
            <v-icon size="20" @click="closeConnectionSearch">mdi-filter-remove</v-icon>
          </template>
        </v-list-item-action>
        <v-list-item-action class="ma-0">
          <v-btn
            icon
            class="rightIcon"
            @click="closeDrawer">
            <v-icon
              class="closeIcon"
              size="20">
              close
            </v-icon>
          </v-btn>
        </v-list-item-action>
      </v-list-item>
    </v-row>

    <v-divider class="my-0" />

    <div class="content">
      <v-row v-if="showConnectionRequests && !showSearch" class="px-4">
        <v-col>
          <connections-requests @invitationReplied="refreshConnections" @shouldShowRequests="updateRequestsSize" ></connections-requests>
        </v-col>
      </v-row>
      <v-row v-if="showConnectionRequests && !showSearch" class="px-4">
        <v-col class="pb-0">
          <v-divider class="my-0" />
        </v-col>
      </v-row>
      <v-row class="px-4">
        <v-col>
          <div v-if="showConnections">
            <v-row align="center">
              <v-col>
                <span class="pr-2 text-uppercase subtitle-2 profile-card-header">{{ $t('homepage.profileStatus.connectionsList') }}</span>
                <v-btn
                  fab
                  depressed
                  dark
                  height="20"
                  width="20"
                  class="mb-1 header-badge-color">
                  <span class="white--text caption">{{ this.connections.length }}</span>
                </v-btn>
              </v-col>
            </v-row>
          </div>
          <div v-if="showConnections">
            <v-list-item
              v-for="item in filteredConnections"
              :key="item.id"
              class="py-0 px-2">
              <a class="connectionProfileLink" :href="PROFILE_URI + item.id">              
                <v-list-item-avatar class="my-1 mr-2" size="30">
                  <v-img :src="item.avatar" />
                </v-list-item-avatar>
  
                <v-list-item-content class="py-0">
                  <v-list-item-title class="font-weight-bold subtitle-2 request-user-name darken-2" v-html="item.fullname" />
                </v-list-item-content>
              </a>
            </v-list-item>
          </div>
          <div v-else>
            <v-row class="d-flex text-center noPeopleYetBlock my-12">
              <div class="ma-auto noPeopleYet">
                <p class="noPeopleYetIcons">
                  <v-icon>fa-users</v-icon>
                </p>
                <p class="title font-weight-bold">
                  {{ $t('peopleList.label.noConnection') }}
                </p>
              </div>
            </v-row>
          </div>
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
      showSearch: false,
      search: null,
      PROFILE_URI: `${eXo.env.portal.context}/${eXo.env.portal.portalName}/profile`,
    }),
    computed: {
      filteredConnections() {
        if (this.search) {
          return this.connections.filter(item => item.fullname.toLowerCase().match(this.search.toLowerCase())); 
        } else {
          return this.connections;
        }
      },
      showConnections() {
        return this.connections && this.connections.length > 0;
      },
      showConnectionRequests() {
        return this.connectionRequests > 0;
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
        this.showSearch = false;
      },
      initConnections() {
        getUserConnections().then(data => {
          this.connections = data.users;
          console.log('connections: ', this.connections);
        });
      },
      refreshConnections(connection) {
        this.connections.unshift(connection);
      },
      updateRequestsSize(requests) {
        this.connectionRequests = requests;
      },
      closeConnectionSearch() {
        this.showSearch = false;
      },
      openConnectionSearch() {
        this.showSearch = true;
      },
    }
  }
</script>
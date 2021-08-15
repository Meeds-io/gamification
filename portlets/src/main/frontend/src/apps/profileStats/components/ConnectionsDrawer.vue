<template>
  <v-navigation-drawer
    v-model="connectionsDrawer"
    absolute
    right
    stateless
    temporary
    class="connectionsDrawer"
    max-height="100vh">
    <v-row v-if="connectionsDrawer" class="mx-0 title">
      <v-list-item class="pe-0">
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
                hide-details />
            </v-row>
          </template>
        </v-list-item-content>
        <v-list-item-action class="ma-0">
          <template v-if="!showSearch">
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

    <v-progress-linear
      v-if="loading"
      color="primary"
      height="2" />
    <v-divider class="my-0" />

    <div class="content">
      <div v-if="isCurrentUserProfile">
        <div v-if="showConnectionRequests && !showSearch">        
          <v-row class="connectionsRequests px-4">
            <v-col class="pb-0">
              <connections-requests @invitationReplied="refreshConnections" @shouldShowRequests="updateRequestsSize" />
            </v-col>
          </v-row>
          <v-row class="seeAllRequests">
            <v-col class="align-center py-0">
              <v-btn
                depressed
                small
                class="caption text-uppercase grey--text ma-0"
                :href="receivedInvitationsUrl">
                {{ $t('homepage.seeAll') }}
              </v-btn>
            </v-col>
          </v-row>
        </div>
      </div>
      <div v-else>
        <div v-if="showSuggestions && !showSearch">
          <v-row class="peopleSuggestions px-4">
            <v-col>
              <v-row class="align-center">
                <v-col>
                  <span class="pe-2 text-uppercase subtitle-2 profile-card-header">{{ $t('homepage.suggestions.label') }}</span>
                </v-col>
              </v-row>
              <v-list>
                <exo-suggestions-people-list-item
                  v-for="people in peoplesToDisplay"
                  :key="people.suggestionId"
                  :people="people"
                  :people-suggestions-list="peopleSuggestionsList" />
              </v-list>
            </v-col>
          </v-row>
        </div>
      </div>
      <v-row v-if="isCurrentUserProfile && showConnectionRequests && !showSearch || !isCurrentUserProfile && showSuggestions && !showSearch" class="px-4">
        <v-col class="pb-0">
          <v-divider class="my-0" />
        </v-col>
      </v-row>
      <v-row class="connectionsList px-4">
        <v-col class="py-1">
          <div v-if="showConnections">
            <v-row align="center">
              <v-col>
                <span class="pe-2 text-uppercase subtitle-2 profile-card-header">{{ isCurrentUserProfile ? $t('homepage.profileStatus.connectionsList') : $t('homepage.commonConnections.label') }}</span>
                <v-btn
                  fab
                  depressed
                  dark
                  height="20"
                  width="20"
                  class="mb-1 header-badge-color">
                  <span class="white--text caption">{{ isCurrentUserProfile ? this.connections.length : this.commonConnections.length }}</span>
                </v-btn>
              </v-col>
            </v-row>
          </div>
          <div v-if="isCurrentUserProfile">
            <div 
              v-if="showConnections" 
              class="connectionsItems" 
              :class="(showMore ? 'showMore ' : '').concat(showConnectionRequests && !showSearch ? 'requestsNotEmpty' : '')">
              <people-list-item
                v-for="person in filteredConnections"
                :key="person.id"
                :person="person"
                :people="filteredConnections" />
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
          </div>
          
          <div
            v-else
            class="connectionsItems"
            :class="showSuggestions && !showSearch ? 'suggestionsNotEmpty' : ''">
            <people-list-item
              v-for="person in filteredCommonConnections"
              :key="person.id"
              :person="person"
              :people="filteredCommonConnections" />
          </div>
        </v-col>
      </v-row>
    </div>
    
    <v-row v-if="isCurrentUserProfile && connections && showMore" class="loadMoreFooterAction mx-0">
      <v-col>
        <v-btn
          class="loadMoreBtn"
          block
          @click="getConnections(connections.length)">
          {{ $t('homepage.loadMore') }}
        </v-btn>
      </v-col>
    </v-row>
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
    isCurrentUserProfile: {
      type: Boolean,
      default: false,
    },
    commonConnections: {
      type: Array,
      default: function() {
        return [];
      },
    },
  },
  data() {
    return {
      connections: [],
      showSearch: false,
      search: null,
      PROFILE_URI: `${eXo.env.portal.context}/${eXo.env.portal.portalName}/profile/`,
      receivedInvitationsUrl: `${ eXo.env.portal.context }/${ eXo.env.portal.portalName }/connexions/receivedInvitations`,
      loading: false,
      connexionsSize: 0,
      limit: 20,
      peopleSuggestionsList: [],
    };
  },
  computed: {
    filteredConnections() {
      if (this.search) {
        return this.connections.filter(item => item.fullname.toLowerCase().match(this.search.toLowerCase())); 
      } else {
        return this.connections;
      }
    },
    filteredCommonConnections() {
      if (this.search) {
        return this.commonConnections.filter(item => item.fullname.toLowerCase().match(this.search.toLowerCase()));
      } else {
        return this.commonConnections;
      }
    },
    showConnections() {
      return this.connections && this.connections.length > 0;
    },
    showConnectionRequests() {
      return this.connectionRequests > 0;
    },
    showSuggestions() {
      return this.peopleSuggestionsList.length > 0;
    },
    showMore() {
      return this.connexionsSize > this.connections.length;
    },
    peoplesToDisplay() {
      return this.peopleSuggestionsList.slice(0, 2);
    },
  },
  watch: {
    connectionsDrawer() {
      if (this.connectionsDrawer) {
        this.loading = true;
        Promise.resolve(this.init())
          .then(() => this.loading = false);
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
  methods: {
    init() {
      if (this.isCurrentUserProfile) {
        return this.getConnections(0);
      } else {
        return this.getConnections(0, 0).then(() => this.initPeopleSuggestionsList());
      }
    },
    closeDrawer() {
      this.$emit('closeDrawer');
      this.showSearch = false;
    },
    getConnections(offset, limit) {
      return getUserConnections('', offset, limit >= 0 ? limit : this.limit).then(data => {
        this.connexionsSize = data.size;
        this.connections.push(...data.users);
        this.connections.forEach(connection => {
          connection.profileLink = this.PROFILE_URI + connection.username;
        });
      });
    },
    refreshConnections(connection) {
      this.connections.unshift(connection);
    },
    updateRequestsSize(requests) {
      this.connectionRequests = requests;
    },
    closeConnectionSearch() {
      this.search = null;
      this.showSearch = false;
    },
    openConnectionSearch() {
      this.showSearch = true;
    },
    initPeopleSuggestionsList() {
      this.$userService.getSuggestionsUsers().then(data => {
        this.peopleSuggestionsList = data.items;
          
        // get suggestions from profile owner's connections
        const connectionsIds = this.connections.map(connection => connection.username);
        this.peopleSuggestionsList.filter(suggestion => {
          connectionsIds.some(connectionId => suggestion.username === connectionId);
        });          
      });
    },
  }
};
</script>
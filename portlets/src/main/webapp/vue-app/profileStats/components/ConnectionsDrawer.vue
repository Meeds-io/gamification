<template>
  <exo-drawer
    ref="connectionsDrawer"
    v-model="connectionsDrawer"
    class="connectionsDrawer"
    body-classes="hide-scroll decrease-z-index-more"
    right
    @closed="$emit('closed')">
    <template #title>
      <span v-if="!showSearch" class="connectionsDrawerTitle">{{ $t("homepage.profileStatus.connections") }}</span>
      <v-row v-else>
        <v-text-field
          v-model="search"
          :placeholder="$t(`profile.label.search.connections`)"
          class="connectionsSearch pa-0 my-n2"
          single-line
          solo
          flat
          hide-details />
      </v-row>
    </template>
    <template #titleIcons>
      <v-btn
        v-if="!showSearch"
        icon
        @click="openConnectionSearch">
        <v-icon size="20">mdi-filter</v-icon>
      </v-btn>
      <v-btn
        v-else
        icon
        @click="closeConnectionSearch">
        <v-icon size="20">mdi-filter-remove</v-icon>
      </v-btn>
    </template>
    <template #content>
      <div class="overflow-hidden">
        <div v-if="isCurrentUserProfile">
          <div v-if="showConnectionRequests && !showSearch">
            <v-row class="connectionsRequests px-2">
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
            <v-row class="peopleSuggestions px-2">
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
        <v-row v-if="isCurrentUserProfile && showConnectionRequests && !showSearch || !isCurrentUserProfile && showSuggestions && !showSearch" class="px-2">
          <v-col class="pb-0">
            <v-divider class="my-0" />
          </v-col>
        </v-row>
        <v-row class="ma-0 px-2">
          <v-col class="py-1 px-0">
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
                    <span class="white--text caption">{{ isCurrentUserProfile ? connections.length : commonConnections.length }}</span>
                  </v-btn>
                </v-col>
              </v-row>
            </div>
            <div v-if="isCurrentUserProfile">
              <div
                v-if="showConnections"
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
    </template>

    <template v-if="isCurrentUserProfile && connections && showMore" #footer>
      <v-btn
        class="text-uppercase caption ma-auto btn"
        block
        @click="loadNextPage">
        {{ $t('homepage.loadMore') }}
      </v-btn>
    </template>
  </exo-drawer>
</template>

<script>
import {getUserConnections} from '../profilStatsAPI';
export default {
  props: {
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
      connectionsDrawer: false,
      search: null,
      PROFILE_URI: `${eXo.env.portal.context}/${eXo.env.portal.portalName}/profile/`,
      receivedInvitationsUrl: `${ eXo.env.portal.context }/${ eXo.env.portal.portalName }/connexions/receivedInvitations`,
      connexionsSize: 0,
      limit: 10,
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
  methods: {
    open() {
      this.$refs.connectionsDrawer.startLoading();
      this.$refs.connectionsDrawer.open();
      this.init().finally(() => this.$refs.connectionsDrawer.endLoading());
    },
    close() {
      this.showSearch = false;
      this.$refs.connectionsDrawer.close();
    },
    loadNextPage() {
      this.limit += 10;
      this.$refs.connectionsDrawer.startLoading();
      this.init().finally(() => this.$refs.connectionsDrawer.endLoading());
    },
    init() {
      if (this.isCurrentUserProfile) {
        this.connections = [];
        return this.getConnections(0);
      } else {
        return this.getConnections(0, 0).then(() => this.initPeopleSuggestionsList());
      }
    },
    getConnections(offset, limit) {
      return getUserConnections('', offset, limit >= 0 ? limit : this.limit)
        .then(data => {
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
      this.$userService.getUserSuggestions().then(data => {
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
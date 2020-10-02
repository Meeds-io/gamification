<template>
  <v-navigation-drawer
    v-model="connectionsDrawer"
    absolute
    right
    stateless
    temporary
    class="connectionsDrawer"
    max-height="100vh"
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
      <div v-if="isCurrentUserProfile">
        <v-row v-if="showConnectionRequests && !showSearch" class="connectionsRequests px-4">
          <v-col>
            <connections-requests @invitationReplied="refreshConnections" @shouldShowRequests="updateRequestsSize" ></connections-requests>
          </v-col>
        </v-row>
      </div>
      <div v-else>
        <div v-if="showSuggestions">
          <v-row class="peopleSuggestions px-4">
            <v-col>
              <v-row class="align-center">
                <v-col>
                  <span class="pr-2 text-uppercase subtitle-2 profile-card-header">{{ $t('homepage.suggestions.label') }}</span>
                </v-col>
              </v-row>
              <v-list>
                <exo-suggestions-people-list-item
                  v-for="people in peoplesToDisplay"
                  :key="people.suggestionId"
                  :people="people"
                  :people-suggestions-list="peopleSuggestionsList"/>
              </v-list>
            </v-col>
          </v-row>
        </div>
      </div>
      <v-row v-if="showConnectionRequests && !showSearch || !isCurrentUserProfile" class="px-4">
        <v-col class="pb-0">
          <v-divider class="my-0" />
        </v-col>
      </v-row>
      <v-row class="connectionsList px-4">
        <v-col>
          <div v-if="showConnections">
            <v-row align="center">
              <v-col>
                <span class="pr-2 text-uppercase subtitle-2 profile-card-header">{{ isCurrentUserProfile ? $t('homepage.profileStatus.connectionsList') : $t('homepage.profileStatus.commonConnections') }}</span>
                <v-btn
                  fab
                  depressed
                  dark
                  height="20"
                  width="20"
                  class="mb-1 header-badge-color"
                  @click="test"
                >
                  <span class="white--text caption">{{ isCurrentUserProfile ? this.connections.length : this.commonConnections.length }}</span>
                </v-btn>
              </v-col>
            </v-row>
          </div>
          <div v-if="isCurrentUserProfile">
            <div 
              v-if="showConnections" 
              class="connectionsItems" 
              :class="(showMore ? 'showMore ' : '').concat(connectionRequests > 0 ? 'requestsNotEmpty' : '')"
            >
              <v-list-item
                v-for="item in filteredConnections"
                :key="item.id"
                class="py-0 px-2">
                <v-list-item-avatar class="my-1 mr-2" size="30">
                  <v-img :src="item.avatar" />
                </v-list-item-avatar>
  
                <v-list-item-content class="py-0">
                  <a :id="cmpId" class="connectionProfileLink" :href="item.profileLink" rel="nofollow">
                    <v-list-item-title class="font-weight-bold subtitle-2 request-user-name darken-2" v-html="item.fullname" />
                  </a>
                </v-list-item-content>
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
          </div>
          
          <div
            v-else
            class="connectionsItems"
            :class="(showMore ? 'showMore ' : '').concat(connectionRequests > 0 ? 'requestsNotEmpty' : '')"
          >
            <v-list-item
                v-for="identity in commonConnections"
                :key="identity.profile.id"
                class="py-0 px-2">
              <v-list-item-avatar class="my-1 mr-2" size="30">
                <v-img :src="identity.profile.avatar" />
              </v-list-item-avatar>

              <v-list-item-content class="py-0">
                <a :id="cmpId" class="connectionProfileLink" :href="identity.profile.profileLink" rel="nofollow">
                  <v-list-item-title class="font-weight-bold subtitle-2 request-user-name darken-2" v-html="identity.profile.fullname" />
                </a>
              </v-list-item-content>
            </v-list-item>
          </div>
        </v-col>
      </v-row>
    </div>

    <v-divider v-if="connections && showMore" class="my-0" />
    
    <v-row v-if="connections && showMore" class="loadMoreFooterAction mx-0">
      <v-col>
        <v-btn
          class="loadMoreBtn"
          block
          @click="getConnections(connections.length)"
        >
          {{ $t('homepage.loadMore') }}
        </v-btn>
      </v-col>
    </v-row>
  </v-navigation-drawer>
</template>

<script>
  import {getUserConnections, getCommonConnections} from '../profilStatsAPI';
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
    },
    data() {
      return {
        cmpId: `react${parseInt(Math.random() * 10000)
          .toString()}`,
        labels: {
          CancelRequest: this.$t('profile.label.CancelRequest'),
          Confirm: this.$t('profile.label.Confirm'),
          Connect: this.$t('profile.label.Connect'),
          Ignore: this.$t('.profile.Ignore'),
          RemoveConnection: this.$t('profile.label.RemoveConnection'),
          StatusTitle: `${this.$t('profile.label.StatusTitle')}...`,
        },
        connections: [],
        showSearch: false,
        search: null,
        PROFILE_URI: `${eXo.env.portal.context}/${eXo.env.portal.portalName}/profile/`,
        connexionsSize: 0,
        limit: 20,
        peopleSuggestionsList: [],
        commonConnections: [],
      }
    },
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
      this.getConnections(0);
      this.initTiptip();
      this.initPeopleSuggestionsList();
      if (!this.isCurrentUserProfile) {
        this.retrieveCommonConnections(parseInt(eXo.env.portal.profileOwnerIdentityId));
      }
    },
    methods: {
      initTiptip() {
        this.$nextTick(() => {
          $(`#${this.cmpId}`).userPopup({
            restURL: '/portal/rest/social/people/getPeopleInfo/{0}.json',
            userId: this.id,
            labels: this.labels,
            content: false,
            keepAlive: true,
            defaultPosition: 'top_left',
            maxWidth: '240px',
          });
        });
      },
      closeDrawer() {
        this.$emit('closeDrawer');
        this.showSearch = false;
      },
      getConnections(offset) {
        // this.connections.length
        getUserConnections('', offset, this.limit).then(data => {
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
        this.showSearch = false;
      },
      openConnectionSearch() {
        this.showSearch = true;
      },
      initPeopleSuggestionsList() {
        this.$userService.getSuggestionsUsers().then(data => {
          this.peopleSuggestionsList = data.items;
        });
      },
      retrieveCommonConnections(id) {
        getCommonConnections(id).then(data => {
          this.commonConnections = data.identities;
          this.commonConnections.forEach(identity => {
              identity.profile.profileLink = this.PROFILE_URI + identity.profile.username;
          });
        });
      },
    }
  }
</script>
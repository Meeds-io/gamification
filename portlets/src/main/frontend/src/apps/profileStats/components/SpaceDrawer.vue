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
  <v-navigation-drawer
    v-model="spaceDrawer"
    absolute
    right
    stateless
    temporary
    max-height="100vh"
    class="spaceDrawer">
    <v-row class="mx-0 title">
      <v-list-item class="pr-0">
        <v-list-item-content class="ma-0 pa-0">
          <template v-if="!showSearch">
            <span class="spaceDrawerTitle">{{
              $t("homepage.profileStatus.spaces")
            }}</span>
          </template>
          <template v-else>
            <v-row>
              <v-text-field
                v-model="search"
                :placeholder="$t(`profile.label.search.connections`)"
                class="spacesSearch pa-0"
                single-line
                solo
                flat
                hide-details />
            </v-row>
          </template>
        </v-list-item-content>
        <v-list-item-action class="ma-0">
          <template v-if="!showSearch">
            <a v-exo-tooltip.left.body="$t(`profile.label.search.openSearch`)">
              <v-icon
                class="openIconSearch"
                @click="openSpacesSearch">mdi-filter</v-icon>
            </a>
          </template>
          <template v-else>
            <a v-exo-tooltip.bottom.body="$t(`profile.label.search.closeSearch`)">
              <v-icon
                class="closeIconSearch"
                @click="closeSpacesSearch">mdi-filter-remove</v-icon>
            </a>
          </template>
        </v-list-item-action>
        <v-list-item-action class="ma-0">
          <v-btn
            icon
            class="rightIcon"
            @click="closeDrawer">
            <v-icon
              small
              class="closeIcon"
              @click="closeDrawer">
              close
            </v-icon>
          </v-btn>
        </v-list-item-action>
      </v-list-item>
    </v-row>

    <v-divider class="my-0" />

    <div class="content">
      <template v-if="isCurrentUserProfile">
        <v-row v-if="showSpacesRequests" class="px-4">
          <v-col>
            <spaces-requests @invitationReplied="refreshSpaces" @showRequestsSpace="updateRequestsSize" />
          </v-col>
        </v-row>
        <v-row v-if="showSpacesRequests" class="px-4">
          <v-col>
            <v-divider class="my-0" />
          </v-col>
        </v-row>
        <v-row v-if="showSpaces" class="px-4">
          <v-col>
            <v-flex
              d-flex
              xs12
              justify-center>
              <span class="pr-2 text-uppercase spaceListTitle subtitle-2 profile-card-header" @click="openSpace()">{{ this.$t('homepage.profileStatus.spaceList') }}</span>
              <v-btn
                fab
                depressed
                dark
                height="20"
                width="20"
                class="mb-1 header-badge-color"
                @click="openSpace()">
                <span class="white--text caption">{{ spacesSize }}</span>
              </v-btn>
            </v-flex>
            <template>
              <space-drawer-items
                v-for="space in filteredSpaces"
                :key="space.id"
                :space="space"
                :skeleton="firstLoadingSpaces" />
            </template>
          </v-col>
        </v-row>
      </template>

      <template v-if="!isCurrentUserProfile">
        <v-row class="px-4">
          <v-col>
            <template v-if="showSpaces">
              <v-list
                v-if="spacesSuggestionsList.length > 0 && suggestionsType !== 'people' && !this.showSearch"
                dense
                class="py-4">
                <v-flex
                  d-flex
                  xs12
                  my-5>
                  <v-layout
                    row
                    wrap
                    mx-2
                    align-start>
                    <v-flex
                      d-flex
                      xs12
                      mt-n2
                      justify-center>
                      <div>
                        <span class="pr-2 text-uppercase spaceRequestedTitle subtitle-2 profile-card-header">{{ this.$t('homepage.profileStatus.SuggestionsSpaces') }}</span>
                        <v-btn
                          fab
                          depressed
                          dark
                          height="20"
                          width="20"
                          class="mb-1 header-badge-color">
                          <span class="white--text caption">{{ spacesSuggestionsList.length > 3 ? 3 : spacesSuggestionsList.length }}</span>
                        </v-btn>
                      </div>
                    </v-flex>
                  </v-layout>
                </v-flex>
                <suggestions-space-list-item
                  v-for="spaceSuggestion in suggestionSpacesToDisplay"
                  :key="spaceSuggestion.spaceId"
                  :space-suggestion="spaceSuggestion"
                  :spaces-suggestions-list="spacesSuggestionsList"
                  :skeleton="firstLoadingSpaces" />
              </v-list>
              <v-row v-if="spacesSuggestionsList.length > 0 && !this.showSearch" class="px-4">
                <v-col>
                  <v-divider class="my-0" />
                </v-col>
              </v-row>

              <v-flex
                d-flex
                xs12
                justify-center>
                <span class="pr-2 text-uppercase spaceListTitle subtitle-2 profile-card-header" @click="openSpace()">{{ this.$t('homepage.profileStatus.commonSpaceList') }}</span>
                <v-btn
                  fab
                  depressed
                  dark
                  height="20"
                  width="20"
                  class="mb-1 header-badge-color">
                  <span class="white--text caption">{{ commonSpacesSize }}</span>
                </v-btn>
              </v-flex>
              <space-drawer-items
                v-for="commonSpace in filteredCommonsSpaces"
                :key="commonSpace.id"
                :space="commonSpace"
                :skeleton="firstLoadingSpaces" />
            </template>
          </v-col>
        </v-row>
      </template>

      <template v-if="!showSpaces">
        <v-row class="d-flex text-center noSpaceYetBlock my-12">
          <div class="ma-auto noSpaceYet">
            <p class="noSpaceYetIcons">
              <v-icon>fa-users</v-icon>
            </p>
            <p class="title font-weight-bold">
              {{ isCurrentUserProfile ? $t('homepage.profileStatus.noSpaces') : $t('homepage.profileStatus.noCommonSpaces') }}
            </p>
          </div>
        </v-row>
      </template>
    </div>

    <template v-if="showSpaces && showLoadMoreSpaces">
      <v-divider class="my-0" />
      <v-row class="mx-0 loadMoreButton">
        <v-col>
          <v-btn
            class="text-uppercase caption ma-auto btn"
            block
            @click="loadNextPage">
            {{ $t('spacesList.button.showMore') }}
          </v-btn>
        </v-col>
      </v-row>
    </template>
  </v-navigation-drawer>
</template>

<script>

  import {getSpacesOfUser, getCommonsSpaces, getSuggestionsSpace} from '../profilStatsAPI';
  export default {
    props: {
      spaceDrawer: {
        type: Boolean,
        default: false,
      },
      suggestionsType: {
        type: String,
        default: 'all',
      },
      spaceRequests: {
        type: Number,
        default: 0,
      },
      isCurrentUserProfile: {
        type: Boolean,
        default: false,
      },
      commonsSpaceDefaultSize: {
        type: Number,
        default: 0,
      },
    },
    data() {
     return {
      spaces: [],
      spacesSuggestionsList: [],
      commonsSpaces: [],
      SpaceUrl: `${ eXo.env.portal.context }/${ eXo.env.portal.portalName }/spaces`,
      offset: 0,
      spaceSize: 0,
      commonsSpaceSize: 0,
      limit: 10,
      limitToFetch: 0,
      showSearch:false,
      firstLoadingSpaces: true,
      search: null,
     }
    },
    computed: {
      filteredSpaces() {
        if (this.search) {
          return this.spaces.filter(item => item.displayName.toLowerCase().match(this.search.toLowerCase()));
        } else {
          return this.spaces;
        }
      },
      displaySpacesSuggestions() {
        return !this.suggestionsType || this.suggestionsType === 'all' || this.suggestionsType === 'space';
      },
      suggestionSpacesToDisplay() {
        return this.spacesSuggestionsList.slice(0, 3);
      },
      filteredCommonsSpaces() {
        if (this.search) {
          return this.commonsSpaces.filter(item => item.displayName.toLowerCase().match(this.search.toLowerCase()));
        } else {
          return this.commonsSpaces;
        }
      },
      showSpaces() {
        return this.spaces && this.spaces.length > 0 || this.commonsSpaces && this.commonsSpaces.length ;
      },
      showLoadMoreSpaces() {
        return this.spaceSize > this.limitToFetch || this.commonsSpaceDefaultSize > this.limitToFetch;
      },
      spacesSize() {
        if (this.search) {
          return this.spaces.filter(item => item.displayName.toLowerCase().match(this.search.toLowerCase())).length;
        } else {
          return this.spaces.length;
        }
      },
      commonSpacesSize() {
        if (this.search) {
          return this.commonsSpaces.filter(item => item.displayName.toLowerCase().match(this.search.toLowerCase())).length;
        } else {
          return this.commonsSpaces.length;
        }
      },
      showSpacesRequests() {
        return this.spaceRequests > 0 && !this.showSearch;
      },
    },
    watch: {
      spaceDrawer() {
        if (this.spaceDrawer) {
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
      this.limitToFetch = this.limit;
      if (this.isCurrentUserProfile){
         this.getMySpaces();}
      else {
       this.CommonsSpaces();
        if (this.displaySpacesSuggestions) {
         this. initSpaceSuggestionsList();
         }
      }
    },
    methods: {
      closeDrawer() {
        this.$emit('closeDrawer');
        this.showSearch = false;
      },
      getMySpaces() {
        this.firstLoadingSpaces = false;
        getSpacesOfUser(this.offset, this.limitToFetch).then(data => {
          this.spaces = data.spaces;
          this.spaceSize = data.size;
        });
      },
       CommonsSpaces() {
        this.firstLoadingSpaces = false;
         getCommonsSpaces(this.offset, this.limitToFetch).then(data => {
            this.commonsSpaces = data.spaces.slice(0, this.limitToFetch);
            this.commonsSpaceSize = data.size;
         });
       },
      openSpace() {
        window.location.href =  `${this.SpaceUrl}`;
      },
      updateRequestsSize(spacesRequestsSize) {
        this.spaceRequests = spacesRequestsSize;
      },
      refreshSpaces(spaceList) {
        this.spaces.unshift(spaceList);
      },
      openSpacesSearch() {
        this.showSearch = true;
      },
      closeSpacesSearch() {
        this.showSearch = false;
        this.search = '';
      },
      loadNextPage() {
      if(this.isCurrentUserProfile){
        if(this.limitToFetch <= this.spaceSize) {
          this.limitToFetch = this.limitToFetch += this.limit;
          getSpacesOfUser(this.offset, this.limitToFetch).then(data => {
             this.spaces = data.spaces;
        });
        }
       }else if(this.limitToFetch <= this.commonsSpaceDefaultSize){
        this.limitToFetch = this.limitToFetch += this.limit;
        getCommonsSpaces(this.offset, this.limitToFetch).then(data => {
            this.commonsSpaces = data.spaces.slice(0, this.limitToFetch);
            this.commonsSpaceSize = data.size;
         });
       }
      },
      initSpaceSuggestionsList() {
       this.firstLoadingSpaces = false;
        getSuggestionsSpace().then(data => {
          this.spacesSuggestionsList = data.items;
        });
      },
    }
  }

</script>

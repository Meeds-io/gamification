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
  <exo-drawer
    ref="spaceDrawer"
    v-model="spaceDrawer"
    class="spaceDrawer"
    body-classes="hide-scroll decrease-z-index-more"
    right
    @closed="$emit('closed')">
    <template #title>
      <span v-if="!showSearch" class="spaceDrawerTitle">{{ $t("homepage.profileStatus.spaces") }}</span>
      <v-row v-else>
        <v-text-field
          v-model="search"
          :placeholder="$t(`profile.label.search.connections`)"
          class="spacesSearch pa-0 my-n2"
          single-line
          solo
          flat
          hide-details />
      </v-row>
    </template>
    <template #titleIcons>
      <v-btn
        v-if="!showSearch"
        :title="$t(`profile.label.search.openSearch`)"
        icon
        @click="openSpacesSearch">
        <v-icon size="20">mdi-filter</v-icon>
      </v-btn>
      <v-btn
        v-else
        :title="$t(`profile.label.search.closeSearch`)"
        icon
        @click="closeSpacesSearch">
        <v-icon size="20">mdi-filter-remove</v-icon>
      </v-btn>
    </template>
    <template #content>
      <div class="overflow-hidden">
        <template v-if="isCurrentUserProfile">
          <v-row v-if="showSpacesRequests" class="px-4 ma-0">
            <v-col>
              <spaces-requests @invitationReplied="refreshSpaces" @showRequestsSpace="updateRequestsSize" />
            </v-col>
          </v-row>
          <v-row v-if="showSpacesRequests" class="px-4 ma-0">
            <v-col>
              <v-divider class="my-0" />
            </v-col>
          </v-row>
          <v-row v-if="showSpaces" class="px-4 ma-0">
            <v-col>
              <v-flex
                d-flex
                xs12
                justify-center>
                <span class="pe-2 text-uppercase spaceListTitle subtitle-2 profile-card-header" @click="openSpace()">{{ $t('homepage.profileStatus.spaceList') }}</span>
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
                  @removeLeavedSpace="removeLeftSpace"
                  @closeDrawer="close" />
              </template>
            </v-col>
          </v-row>
        </template>

        <v-row v-if="!isCurrentUserProfile" class="px-4 ma-0">
          <v-col>
            <template v-if="showSpaces">
              <v-list
                v-if="spacesSuggestionsList.length > 0 && suggestionsType !== 'people' && !showSearch"
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
                        <span class="pe-2 text-uppercase spaceRequestedTitle subtitle-2 profile-card-header">{{ $t('homepage.profileStatus.SuggestionsSpaces') }}</span>
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
                  :spaces-suggestions-list="spacesSuggestionsList" />
              </v-list>
              <v-row v-if="spacesSuggestionsList.length > 0 && !showSearch" class="px-4 ma-0">
                <v-col>
                  <v-divider class="my-0" />
                </v-col>
              </v-row>

              <v-flex
                d-flex
                xs12
                justify-center>
                <span class="pe-2 text-uppercase spaceListTitle subtitle-2 profile-card-header" @click="openSpace()">{{ $t('homepage.profileStatus.commonSpaceList') }}</span>
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
                :space="commonSpace" />
            </template>
          </v-col>
        </v-row>

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
    </template>
    <template v-if="showSpaces && showLoadMoreSpaces" #footer>
      <v-btn
        class="text-uppercase caption ma-auto btn"
        block
        @click="loadNextPage">
        {{ $t('spacesList.button.showMore') }}
      </v-btn>
    </template>
  </exo-drawer>
</template>

<script>
import {getSpacesOfUser, getCommonsSpaces, getSuggestionsSpace} from '../profilStatsAPI';
export default {
  props: {
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
      spaceDrawer: false,
      offset: 0,
      spaceSize: 0,
      commonsSpaceSize: 0,
      limit: 10,
      limitToFetch: 0,
      showSearch: false,
      firstLoadingSpaces: true,
      search: null,
    };
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
      return this.spaces && this.spaces.length > 0 || this.commonsSpaces && this.commonsSpaces.length || this.spacesSuggestionsList && this.spacesSuggestionsList.length;
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
  methods: {
    open() {
      this.$refs.spaceDrawer.startLoading();
      this.$refs.spaceDrawer.open();
      this.init().finally(() => this.$refs.spaceDrawer.endLoading());
    },
    close() {
      this.showSearch = false;
      this.$refs.spaceDrawer.close();
    },
    init() {
      this.limitToFetch = this.limit;
      if (this.isCurrentUserProfile){
        return this.getMySpaces();
      } else {
        return this.getCommonsSpaces()
          .then(() => {
            if (this.displaySpacesSuggestions) {
              this. initSpaceSuggestionsList();
            }
          });
      }
    },
    getMySpaces() {
      this.firstLoadingSpaces = false;
      return getSpacesOfUser(this.offset, this.limitToFetch)
        .then(data => {
          this.spaces = data.spaces;
          this.spaceSize = data.size;
        });
    },
    getCommonsSpaces() {
      this.firstLoadingSpaces = false;
      return getCommonsSpaces(this.offset, this.limitToFetch)
        .then(data => {
          this.commonsSpaces = data.spaces.slice(0, this.limitToFetch);
          this.commonsSpaceSize = data.size;
        });
    },
    openSpace() {
      window.location.href = `${eXo.env.portal.context}/${eXo.env.portal.portalName}/spaces`;
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
      if (this.isCurrentUserProfile) {
        if (this.limitToFetch <= this.spaceSize) {
          this.limitToFetch = this.limitToFetch += this.limit;
          this.$refs.spaceDrawer.startLoading();
          return getSpacesOfUser(this.offset, this.limitToFetch)
            .then(data => this.spaces = data.spaces)
            .finally(() => this.$refs.spaceDrawer.endLoading());
        }
      } else if (this.limitToFetch <= this.commonsSpaceDefaultSize) {
        this.limitToFetch = this.limitToFetch += this.limit;
        this.$refs.spaceDrawer.startLoading();
        return getCommonsSpaces(this.offset, this.limitToFetch)
          .then(data => {
            this.commonsSpaces = data.spaces.slice(0, this.limitToFetch);
            this.commonsSpaceSize = data.size;
          })
          .finally(() => this.$refs.spaceDrawer.endLoading());
      }
    },
    initSpaceSuggestionsList() {
      this.firstLoadingSpaces = false;
      getSuggestionsSpace().then(data => {
        this.spacesSuggestionsList = data.items;
      });
    },
    removeLeftSpace(spaceName) {
      const filtered = this.spaces.filter(space => space.prettyName !== spaceName);
      this.spaces =  [];
      this.spaces.push(...filtered);
    },
  }
};

</script>

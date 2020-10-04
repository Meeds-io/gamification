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
      <v-row v-if="showSpacesRequests && checkCurrentUserProfile" class="px-4">
        <v-col>
          <spaces-requests @invitationReplied="refreshSpaces" @showRequestsSpace="updateRequestsSize" />
        </v-col>
      </v-row>
      <v-row v-if="showSpacesRequests && checkCurrentUserProfile" class="px-4">
        <v-col>
          <v-divider class="my-0" />
        </v-col>
      </v-row>
      <v-row class="px-4">
        <v-col>
          <template v-if="showSpaces">
            <v-flex
              d-flex
              xs12
              justify-center>
              <span class="pr-2 text-uppercase spaceListTitle subtitle-2 profile-card-header" @click="openSpace()">{{ checkCurrentUserProfile ? this.$t('homepage.profileStatus.spaceList') : this.$t('homepage.profileStatus.commonSpaceList')}}</span>
              <v-btn
                fab
                depressed
                dark
                height="20"
                width="20"
                class="mb-1 header-badge-color">
                <span class="white--text caption">{{ checkCurrentUserProfile ? spacesSize : commonsSpaces.length  }}</span>
              </v-btn>
            </v-flex>
               <space-drawer-items
                     v-if="checkCurrentUserProfile"
                     v-for="space in filteredSpaces"
                    :key="space.id"
                    :space="space"
                    :skeleton="firstLoadingSpaces"
               />
               <commons-space-items
                    v-if="!checkCurrentUserProfile"
                    v-for="commonSpace in filteredCommonsSpaces"
                    :key="commonSpace.id"
                    :commonSpace="commonSpace"
                    :skeleton="firstLoadingSpaces"
               />
          </template>
          <template v-else>
            <v-row class="d-flex text-center noSpaceYetBlock my-12">
              <div class="ma-auto noSpaceYet">
                <p class="noSpaceYetIcons">
                  <v-icon>fa-users</v-icon>
                </p>
                <p class="title font-weight-bold">
                  {{ $t('homepage.profileStatus.noSpaces') }}
                </p>
              </div>
            </v-row>
          </template>
        </v-col>
      </v-row>
    </div>

   <template v-if="showSpaces && showLoadMoreSpaces">
    <v-divider class="my-0"/>
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

  import {getSpacesOfUser, getCommonsSpaces} from '../profilStatsAPI';
  export default {
    props: {
      spaceDrawer: {
        type: Boolean,
        default: false,
      },
      spaceRequests: {
        type: Number,
        default: 0,
      },
      checkCurrentUserProfile: {
        type: Boolean,
        default: false,
      },
      commonsSpaces: {
        type: Array,
        default: function() {
          return [];
        },
      },
    },
    data() {
     return {
      spaces: [],
      SpaceUrl: `${ eXo.env.portal.context }/${ eXo.env.portal.portalName }/spaces`,
      offset: 0,
      spaceSize: 0,
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
      filteredCommonsSpaces() {
        if (this.search) {
          return this.commonsSpaces.filter(item => item.displayName.toLowerCase().match(this.search.toLowerCase()));
        } else {
          return this.commonsSpaces;
        }
      },
      showSpaces() {
        return this.spaces && this.spaces.length > 0;
      },
      showLoadMoreSpaces() {
        return this.spaceSize > this.limitToFetch;
      },
      spacesSize() {
        if (this.search) {
          return this.spaces.filter(item => item.displayName.toLowerCase().match(this.search.toLowerCase())).length;
        } else {
          return this.spaces.length;
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
      this.getMySpaces();
    },
    methods: {
      closeDrawer() {
        this.$emit('closeDrawer');
        getSpacesOfUser(this.offset, this.limit).then(data => {
          this.spaces = data.spaces;
          this.spaceSize = data.size;
        });
        this.limitToFetch = this.limit;
        this.showSearch = false;
        this.showLoadMoreSpaces = true;
      },
      getMySpaces() {
        this.firstLoadingSpaces = false;
        getSpacesOfUser(this.offset, this.limitToFetch).then(data => {
          this.spaces = data.spaces;
          this.spaceSize = data.size;
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
        if(this.limitToFetch <= this.spaceSize) {
          this.limitToFetch = this.limitToFetch += this.limit;
          getSpacesOfUser(this.offset, this.limitToFetch).then(data => {
             this.spaces = data.spaces;
        });
        }
      },
      getUrl(groupId) {
         window.location.href = `${eXo.env.portal.context}/g/${groupId.replace(/\//g, ':')}`;
      }
    }
  }

</script>

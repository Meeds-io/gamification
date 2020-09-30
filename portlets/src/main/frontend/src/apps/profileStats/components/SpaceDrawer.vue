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

    <template class="content">
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
      <v-row class="px-4">
        <v-col>
          <template v-if="showSpaces">
            <v-flex
              d-flex
              xs12
              mt-n2
              justify-center>
              <span class="pr-2 text-uppercase spaceListTitle subtitle-2 profile-card-header">{{ this.$t('homepage.profileStatus.spaceList') }}</span>
              <v-btn
                fab
                depressed
                dark
                height="20"
                width="20"
                class="mb-1 header-badge-color">
                <span class="white--text caption">{{ spacesSize }}</span>
              </v-btn>
            </v-flex>
            <v-list-item
              v-for="item in filteredSpaces"
              :id="id"
              :key="item.id"
              class="py-0 px-2">
              <v-list-item-avatar
                class="my-1 mr-2"
                size="30"
                @click="getUrl(item.groupId)">
                <v-img :src="item.avatarUrl" />
              </v-list-item-avatar>

              <v-list-item-content class="py-0" @click="getUrl(item.groupId)">
                  <v-list-item-title class="font-weight-bold subtitle-2 request-user-name darken-2" v-html="item.displayName" />
              </v-list-item-content>
            </v-list-item>
          </template>
          <div v-else>
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
          </div>
        </v-col>
      </v-row>
    </template>

    <v-divider class="my-0" />
    <template v-if="showLoadMoreSpaces">
      <v-row class="mt-3 mb-3">
        <v-card
          flat
          class="d-flex flex justify-center px-1">
          <v-btn
            class="text-uppercase caption ma-auto btn"
            depressed
            height="36"
            width="350"
            @click="loadNextPage">
            {{ $t('spacesList.button.showMore') }}
          </v-btn>
        </v-card>
      </v-row>
    </template>
  </v-navigation-drawer>
</template>

<script>
  const randomMax = 10000;
  import {getSpacesOfUser} from '../profilStatsAPI';
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
    },
    data() {
     return {
      spaces: [],
      offset: 0,
      spaceSize: 0,
      limit: 10,
      limitToFetch: 0,
      showSearch:false,
      showLoadMoreSpaces:true,
      search: null,
      id: `react${parseInt(Math.random() * randomMax)
        .toString()
        }`,
     }
    },
    computed: {
    labels() {
      return {
        CancelRequest: this.$t('profile.label.CancelRequest'),
        Confirm: this.$t('profile.label.Confirm'),
        Connect: this.$t('profile.label.Connect'),
        Ignore: this.$t('profile.label.Ignore'),
        RemoveConnection: this.$t('profile.label.RemoveConnection'),
        StatusTitle: this.$t('profile.label.StatusTitle'),
        join: this.$t('profile.label.join'),
        leave: this.$t('profile.label.leave'),
        members: this.$t('profile.label.members'),
      };
    },
      filteredSpaces() {
        if (this.search) {
          return this.spaces.filter(item => item.displayName.toLowerCase().match(this.search.toLowerCase()));
        } else {
          if((this.spaceSize <= this.limit) || (this.limitToFetch >= this.spaceSize)) {
            this.showLoadMoreSpaces = false;
          }
          return this.spaces;
        }
      },
      showSpaces() {
        return this.spaces && this.spaces.length > 0;
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
      this.initSpaces();
      this.initTiptip();
    },
    methods: {
    initTiptip() {
      console.log('test idddd: ', this.id);
      this.$nextTick(() => {
        $(`#${this.id}`).spacePopup({
          userName: eXo.env.portal.userName,
          spaceID: this.id,
          restURL: '/portal/rest/v1/social/spaces/{0}',
          membersRestURL: '/portal/rest/v1/social/spaces/{0}/users?returnSize=true',
          managerRestUrl: '/portal/rest/v1/social/spaces/{0}/users?role=manager&returnSize=true',
          membershipRestUrl: '/portal/rest/v1/social/spacesMemberships?space={0}&returnSize=true',
          defaultAvatarUrl: this.avatar,
          deleteMembershipRestUrl: '/portal/rest/v1/social/spacesMemberships/{0}:{1}:{2}',
          labels: this.labels,
          content: false,
          keepAlive: true,
          defaultPosition: 'left_bottom',
          maxWidth: '420px',
        });
      });
    },
      closeDrawer() {
        this.$emit('closeDrawer');
        getSpacesOfUser(this.offset, this.limit).then(data => {
          this.spaces = data.spaces;
          this.spaceSize = data.size;
        });
        this.limitToFetch = 0;
        this.showSearch = false;
        this.showLoadMoreSpaces = true;
      },
      initSpaces() {
        this.showLoadMoreSpaces = true;
        getSpacesOfUser(this.offset, this.limitToFetch).then(data => {
          this.spaces = data.spaces;
          this.spaceSize = data.size;
        });
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

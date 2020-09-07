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
          class="spaceDrawer"
  >
    <v-row class="mx-0 title">
      <v-list-item>
        <v-list-item-content>
            <span class="spaceDrawerTitle">{{
              $t("homepage.profileStatus.spaces")
            }}</span>
        </v-list-item-content>
        <v-list-item-action>
          <v-text-field
                  v-model="search"
                  :placeholder="$t(`profile.label.search.connections`)"
                  class="spacesSearch"
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

    <div class="content">
      <v-row v-if="showSpacesRequests" class="px-4">
        <v-col>
          <spaces-requests @invitationReplied="refreshSpaces" @showRequestsSpace="updateRequestsSize" ></spaces-requests>
        </v-col>
      </v-row>
      <v-row v-if="showSpacesRequests" class="px-4">
        <v-col>
          <v-divider class="my-0" />
        </v-col>
      </v-row>
      <v-row class="px-4">
        <v-col>
          <div v-if="showSpaces">
            <v-flex
                    d-flex
                    xs12
                    mt-n2
                    justify-center>
            <span class="pr-2 text-uppercase subtitle-2 profile-card-header">{{ this.$t('homepage.profileStatus.spaceList') }}</span>
              <v-btn
                      color="primary-color"
                      fab
                      depressed
                      dark
                      height="20"
                      width="20"
                      class="mb-1">
                <span class="white--text caption">{{ spacesSize }}</span>
              </v-btn>
            </v-flex>
              <v-list-item
                    v-for="item in filteredSpaces"
                    :key="item.id"
                    class="py-0 px-2">
              <v-list-item-avatar class="my-1 mr-2" size="30">
                <v-img :src="item.avatarUrl" />
              </v-list-item-avatar>

              <v-list-item-content class="py-0">
                <v-list-item-title class="font-weight-bold subtitle-2 request-user-name darken-2" v-html="item.displayName" />
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
                  {{ $t('homepage.profileStatus.noSpaces') }}
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
  import {getUserInformations, getSpacesOfUser} from '../profilStatsAPI';
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
    data: () => ({
      spaces: [],
      spacesSize: '',
      search: null,
    }),
    computed: {
      filteredSpaces() {
        if (this.search) {
          return this.spaces.filter(item => item.displayName.toLowerCase().match(this.search.toLowerCase()));
        } else {
          return this.spaces;
        }
      },
      showSpaces() {
        this.spacesSize = this.spaces.length;
        return this.spaces && this.spaces.length > 0;
      },
      showSpacesRequests() {
        return this.spaceRequests > 0;
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
      this.initSpaces();
      this.getSpaceSize();
    },
    methods: {
      closeDrawer() {
        this.$emit('closeDrawer');
      },
      initSpaces() {
        getSpacesOfUser().then(data => {
          this.spaces = data.spaces;

        });
      },
      updateRequestsSize(spacesRequestsSize) {
        this.spaceRequests = spacesRequestsSize;
      },
      getSpaceSize() {
        return getUserInformations().then(data => {
          this.spacesSize = data.spacesCount;
          })
      },
      refreshSpaces(spaceList) {
        this.spaces.unshift(spaceList);
      },
    }
  }

</script>

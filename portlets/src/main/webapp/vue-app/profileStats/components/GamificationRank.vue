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
  <v-layout class="row wrap mx-0 fill-height">
    <div v-if="!isOverviewDisplay && !loading" class="d-flex align-center full-width mt-3 ms-n2">
      <v-btn
        class="pa-0 my-auto"
        icon
        @click="$emit('flip')">
        <v-icon size="20" class="icon-default-color">
          {{ $vuetify.rtl && 'mdi-arrow-right' || 'mdi-arrow-left' }}
        </v-icon>
      </v-btn>
      <div class="d-flex flex-grow-1 flex-shrink-1 text-truncate">
        <span class="widget-text-header text-truncate">{{ $t('homepage.profileStatus.weeklyRank') }}</span>
      </div>
    </div>
    <div
      v-if="displayPlaceholder"
      :class="!isOverviewDisplay && 'mt-n7'"
      class="d-flex flex-column align-center justify-center full-width fill-height">
      <v-icon color="secondary" size="54">fa-trophy</v-icon>
      <span class="subtitle-1 font-weight-bold mt-7">{{ $t('gamification.overview.weeklyLeaderboard') }}</span>
    </div>
    <template v-else-if="!loading">
      <v-flex class="xs12 mb-1">
        <v-layout class="podium-layout row wrap mx-0">
          <v-flex
            :class="isOverviewDisplay && 'mb-7'"
            d-flex
            justify-center
            align-end>
            <v-card
              v-if="podium[1]"
              class="transparent mx-1 align-center"
              flat
              @click.prevent.stop="$refs.profileStatsDrawer.open(podium[1], period)">
              <user-avatar
                :profile-id="podium[1].remoteId"
                :name="podium[1].fullname"
                :avatar-url="podium[1].avatarUrl"
                :popover="podium[1].remoteId"
                :size="40"
                extra-class="me-2 ml-2 pa-0 mt-0 mb-1 rounded-circle elevation-1 d-inline-block"
                avatar
                popover-left-position />
              <v-card-text
                class="top2 grey lighten-1 px-3 py-2 flex d-flex white--text justify-center font-weight-bold"
                style="height: 40px">
                {{ podium[1].score }}
              </v-card-text>
            </v-card>
            <v-card
              v-if="podium[0]"
              class="transparent mx-1 align-center"
              flat
              @click.prevent.stop="$refs.profileStatsDrawer.open(podium[0], period)">
              <user-avatar
                :profile-id="podium[0].remoteId"
                :name="podium[0].fullname"
                :avatar-url="podium[0].avatarUrl"
                :popover="podium[0].remoteId"
                :size="40"
                extra-class="ml-2 me-2 pa-0 mt-0 mb-1 rounded-circle elevation-1 d-inline-block"
                avatar
                popover-left-position />
              <v-card-text
                class="top1 yellow darken-1 px-3 py-2 flex d-flex white--text justify-center font-weight-bold"
                style="height: 55px">
                {{ podium[0].score }}
              </v-card-text>
            </v-card>
            <v-card
              v-if="podium[2]"
              class="transparent mx-1 align-center"
              flat
              @click.prevent.stop="$refs.profileStatsDrawer.open(podium[2], period)">
              <user-avatar
                :profile-id="podium[2].remoteId"
                :name="podium[2].fullname"
                :avatar-url="podium[2].avatarUrl"
                :popover="podium[2].remoteId"
                :size="40"
                extra-class="me-2 ml-2 pa-0 mt-0 mb-1 rounded-circle elevation-1 d-inline-block"
                avatar
                popover-left-position />
              <v-card-text
                class="top3 amber darken-1 px-3 pb-1 flex d-flex white--text justify-center font-weight-bold pt-2px"
                style="height: 25px">
                {{ podium[2].score }}
              </v-card-text>
            </v-card>
          </v-flex>
        </v-layout>
      </v-flex>
      <v-flex xs12>
        <v-list :class="!isOverviewDisplay && 'pt-0'" min-height="110">
          <users-leaderboard-profile
            v-for="user in listBelowPoduim"
            :key="user.identityId"
            :user="user"
            @open="$refs.profileStatsDrawer.open(user, period)" />
        </v-list>
      </v-flex>
      <users-leaderboard-profile-achievements-drawer
        ref="profileStatsDrawer" />
    </template>
  </v-layout>
</template>
<script>
export default {
  props: {
    isOverviewDisplay: {
      type: Boolean,
      default: () => false,
    },
  },
  data: () => ({
    users: [],
    limit: 6,
    identityId: eXo.env.portal.profileOwnerIdentityId,
    loading: true,
  }),
  computed: {
    podium() {
      return this.users.slice(0, 3);
    },
    displayPlaceholder() {
      return !this.loading && !this.users?.length;
    },
    listBelowPoduim() {
      if (this.users?.length <= 3) {
        return this.users;
      } else {
        return this.users.slice(this.users?.length - 3);
      }
    },
  },
  created() {
    this.getLeaderboard();
  },
  methods: {
    getLeaderboard() {
      this.loading = true;
      return this.$leaderboardService.getLeaderboard({
        identityId: eXo.env.portal.profileOwnerIdentityId,
        period: 'WEEK',
        limit: this.limit,
      })
        .then(data => {
          this.users = data.slice(0, this.limit);
          document.dispatchEvent(new CustomEvent('listOfRankedConnections', {detail: this.users.length}));
        })
        .finally(() => this.loading = false);
    },
  },
};
</script>

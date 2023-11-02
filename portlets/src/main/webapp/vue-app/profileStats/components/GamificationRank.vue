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
  <v-layout
    row
    wrap
    mx-0>
    <v-flex
      v-if="!isOverviewDisplay"
      d-flex
      xs12
      mt-5
      mb-2>
      <v-layout
        row
        wrap
        mx-2
        align-start
        px12>
        <v-flex
          d-flex
          xs12
          mt-n2
          justify-center>
          <div>
            <span class="pe-2 text-uppercase subtitle-2 profile-card-header">{{ $t('homepage.profileStatus.weeklyRank') }}</span>
          </div>
        </v-flex>
        <v-flex
          d-flex
          xs12
          mt-n6>
          <v-icon
            color="grey darken-2"
            size="20"
            @click="$emit('isProfileStats')">
            {{ $vuetify.rtl && 'mdi-arrow-right' || 'mdi-arrow-left' }}
          </v-icon>
        </v-flex>
      </v-layout>
    </v-flex>
    <v-flex
      xs12
      mb-1>
      <v-layout
        row
        wrap
        mx-0
        class="podium-layout">
        <v-flex
          :class="isOverviewDisplay && 'mb-7' || ''"
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
      <v-list min-height="110">
        <users-leaderboard-profile
          v-for="user in listBelowPoduim"
          :key="user.identityId"
          :user="user"
          @open="$refs.profileStatsDrawer.open(user, period)" />
      </v-list>
    </v-flex>
    <users-leaderboard-profile-achievements-drawer
      ref="profileStatsDrawer" />
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
    identityId: eXo.env.portal.profileOwnerIdentityId,
  }),
  computed: {
    podium() {
      return this.users.slice(0, 3);
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
      return this.$leaderboardService.getLeaderboard({
        identityId: eXo.env.portal.profileOwnerIdentityId,
        period: 'WEEK',
        limit: 6,
      })
        .then(data => {
          this.users = data.slice();
          document.dispatchEvent(new CustomEvent('listOfRankedConnections', {detail: this.users.length}));
        });
    },
  }
};
</script>

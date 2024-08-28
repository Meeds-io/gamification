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
  <div class="my-auto">
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
        <span class="widget-text-header text-truncate">{{ title }}</span>
      </div>
    </div>
    <div
      v-if="displayPlaceholder"
      :class="!isOverviewDisplay && 'mt-n7'"
      class="d-flex flex-column align-center justify-center full-width fill-height">
      <v-icon color="tertiary" size="54">fa-trophy</v-icon>
      <span class="text-body mt-7">{{ placeholder }}</span>
    </div>
    <template v-else-if="!loading">
      <v-flex class="xs12 mb-1">
        <v-layout class="podium-layout row wrap mx-0">
          <v-flex
            :class="isOverviewDisplay && 'mb-4'"
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
              <v-img
                src="/gamification-portlets/images/podium/2.webp"
                class="top2 d-flex white--text justify-center align-center text-title"
                width="80">
                {{ podium[1].score }}
              </v-img>
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
              <v-img
                src="/gamification-portlets/images/podium/1.webp"
                class="top1 d-flex white--text justify-center align-center text-title"
                width="80">
                {{ podium[0].score }}
              </v-img>
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
              <v-img
                src="/gamification-portlets/images/podium/3.webp"
                class="top3 d-flex white--text justify-center align-center text-title"
                width="80">
                {{ podium[2].score }}
              </v-img>
            </v-card>
          </v-flex>
        </v-layout>
      </v-flex>
      <v-flex xs12>
        <v-list class="py-0" min-height="110">
          <users-leaderboard-profile
            v-for="user in listBelowPoduim"
            :key="user.identityId"
            :user="user"
            :selected-identity-id="identityId"
            @open="$refs.profileStatsDrawer.open(user, period)" />
        </v-list>
      </v-flex>
      <users-leaderboard-profile-achievements-drawer
        ref="profileStatsDrawer" />
    </template>
  </div>
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
    listBelowPoduimLimit: 3,
    podiumLimit: 3,
    identityId: eXo.env.portal.profileOwnerIdentityId,
    loading: true,
  }),
  computed: {
    period() {
      return this.isOverviewDisplay && this.$root.topChallengersPeriod || 'week';
    },
    title() {
      return this.period === 'all' ?
          this.$t('homepage.profileStatus.allTimeRank')
          : this.$t(`homepage.profileStatus.${this.period.toLowerCase()}lyRank`);
    },
    placeholder() {
      return this.period === 'all' ?
          this.$t('gamification.overview.allTimeLeaderboard')
          : this.$t(`gamification.overview.${this.period.toLowerCase()}lyLeaderboard`);
    },
    displayPodium() {
      return !this.isOverviewDisplay;
    },
    displayCurrentPosition() {
      return this.isOverviewDisplay ? (this.$root.topChallengersCurrentPosition || false) : true;
    },
    podium() {
      return this.users.slice(0, this.podiumLimit);
    },
    displayPlaceholder() {
      return !this.loading && !this.users?.length;
    },
    belowPoduimLimit() {
      return this.isOverviewDisplay ? (this.displayCurrentPosition ? 1 : 0) : this.listBelowPoduimLimit;
    },
    limit() {
      return this.belowPoduimLimit + this.podiumLimit;
    },
    listBelowPoduim() {
      if (this.users?.length <= this.podiumLimit) {
        return this.isOverviewDisplay ? [] : this.users;
      } else {
        return this.users.slice(this.users?.length - this.belowPoduimLimit);
      }
    },
  },
  watch: {
    period() {
      this.getLeaderboard();
    },
    displayCurrentPosition() {
      this.getLeaderboard();
    },
  },
  created() {
    this.getLeaderboard();
  },
  methods: {
    getLeaderboard() {
      this.loading = true;
      return this.$leaderboardService.getLeaderboard({
        identityId: this.identityId,
        period: this.period,
        limit: this.limit,
      })
        .then(users => {
          const currentUser = this.identityId && users.find(u => u.identityId === Number(this.identityId));
          if (this.belowPoduimLimit > 0
              && currentUser?.rank
              && currentUser.rank > (this.limit - 1)) {
            const listBelowPoduimOffset = currentUser.rank - this.belowPoduimLimit;
            const listBelowPoduimLimit = parseInt((this.belowPoduimLimit - 1) / 2) * 3 + 1;
            return this.$leaderboardService.getLeaderboard({
              period: this.period,
              offset: listBelowPoduimOffset,
              limit: listBelowPoduimLimit,
            })
              .then(otherUsers => {
                this.users = [...users, ...otherUsers];
              });
          } else {
            this.users = users.slice(0, this.limit);
          }
        })
        .then(() => document.dispatchEvent(new CustomEvent('listOfRankedConnections', {detail: this.users.length})))
        .finally(() => this.loading = false);
    },
  },
};
</script>

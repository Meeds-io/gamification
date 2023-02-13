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
    :class="isOverviewDisplay && 'mt-n10 mb-5' || ''"
    row
    wrap
    mx-0>
    <v-flex
      d-flex
      xs12
      my-5>
      <v-layout
        row
        wrap
        mx-2
        align-start
        px12>
        <v-flex
          v-if="!isOverviewDisplay"
          d-flex
          xs12
          mt-n2
          justify-center>
          <div>
            <span class="pe-2 text-uppercase subtitle-2 profile-card-header">{{ this.$t('homepage.profileStatus.weeklyRank') }}</span>
          </div>
        </v-flex>
        <v-flex
          v-if="!isOverviewDisplay"
          d-flex
          xs12
          mt-n6>
          <v-icon
            color="grey darken-2"
            size="20"
            @click="toProfileStats()">
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
          <div v-if="leaderBoardArray[1]" class="transparent mx-1 align-center">
            <exo-user-avatar
              :profile-id="leaderBoardArray[1].remoteId"
              :size="40"
              extra-class="me-2 ml-2 pa-0 mt-0 mb-1 rounded-circle elevation-1 d-inline-block"
              avatar
              popover
              popover-left-position />
            <v-card-text
              class="top2 grey lighten-1 px-3 py-2 flex d-flex white--text justify-center font-weight-bold"
              style="height: 40px">
              {{ leaderBoardArray[1].score }}
            </v-card-text>
          </div>
          <div v-if="leaderBoardArray[0]" class="transparent mx-1 align-center">
            <exo-user-avatar
              :profile-id="leaderBoardArray[0].remoteId"
              :size="40"
              extra-class="ml-2 me-2 pa-0 mt-0 mb-1 rounded-circle elevation-1 d-inline-block"
              avatar
              popover
              popover-left-position />
            <v-card-text
              class="top1 yellow darken-1 px-3 py-2 flex d-flex white--text justify-center font-weight-bold"
              style="height: 55px">
              {{ leaderBoardArray[0].score }}
            </v-card-text>
          </div>
          <div v-if="leaderBoardArray[2]" class="transparent mx-1 align-center">
            <exo-user-avatar
              :profile-id="leaderBoardArray[2].remoteId"
              :size="40"
              extra-class="me-2 ml-2 pa-0 mt-0 mb-1 rounded-circle elevation-1 d-inline-block"
              avatar
              popover
              popover-left-position />
            <v-card-text
              class="top3 amber darken-1 px-3 pb-1 flex d-flex white--text justify-center font-weight-bold pt-2px"
              style="height: 25px">
              {{ leaderBoardArray[2].score }}
            </v-card-text>
          </div>
        </v-flex>
      </v-layout>
    </v-flex>
    <v-flex
      xs12>
      <v-list height="110">
        <template v-for="item in listBelowPoduim">
          <template v-if="item">
            <v-list-item
              :key="item.remoteId"
              class="py-0 px-4 mt-n3">
              <v-card
                min-width="16"
                class="me-2"
                flat>
                {{ item.rank }}
              </v-card>
              <exo-user-avatar
                :profile-id="item.remoteId"
                :size="25"
                :bold-title="item.socialId === identityId"
                extra-class="me-0 pa-0 my-0 text-truncate-2"
                popover-left-position
                offset-x
                popover />
              <v-list-item-action class="ml-auto">
                <span>{{ item.score }}</span>
              </v-list-item-action>
            </v-list-item>
          </template>
        </template>
      </v-list>
    </v-flex>
  </v-layout>
</template>

<script>
import {getUsersByGamificationRank} from '../profilStatsAPI';
export default {
  props: {
    isOverviewDisplay: {
      type: Boolean,
      default: () => false,
    },
  },
  data() {
    return {
      leaderBoardArray: [],
      listBelowPoduim: [],
      identityId: eXo.env.portal.profileOwnerIdentityId,
    };
  },
  created() {
    this.getUsersByGamificationRank();
  },

  methods: {
    getUsersByGamificationRank() {
      getUsersByGamificationRank('WEEK').then(
        (data) => {
          document.dispatchEvent(new CustomEvent('listOfRankedConnections', {detail: data.length}));
          const currentUser = eXo.env.portal.profileOwner;
          const index = data.findIndex(item => item.remoteId === currentUser) + 1;
          for (const element of data) {
            this.leaderBoardArray.push(element);
          }
          if ((data.length === 6) || (data.length > 6 && index < 7)) {
            for (let i = 3; i < 6; i++) {
              this.listBelowPoduim.push(data[i]);
            }
          } else if (data.length <= 3) {
            for (const element of data) {
              this.listBelowPoduim.push(element);
            }
          } else if ((data.length > 3 && data.length < 6) || index === data.length) {
            for (let i = data.length - 3; i < data.length; i++) {
              this.listBelowPoduim.push(data[i]);
            }
          } else {
            for (let i = index - 2; i < index + 1; i++) {
              this.listBelowPoduim.push(data[i]);
            }
          }
        });
    },
    toProfileStats() {
      this.$emit('isProfileStats');
    }
  }
};
</script>

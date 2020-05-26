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
          d-flex
          xs12
          mt-n2
          justify-center>
          <div>
            <span class="pr-2 text-uppercase subtitle-2 profile-card-header">{{ this.$t('homepage.profileStatus.Rank') }}</span>
          </div>
        </v-flex>
        <v-flex
          d-flex
          xs12
          mt-n6>
          <v-icon
            color="grey darken-2"
            size="20"
            @click="toProfileStats()">
            mdi-arrow-left
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
          d-flex
          justify-center
          align-end>
          <div v-if="(typeof leaderBoardArray[1] != 'undefined')" class="transparent mx-1 align-center">
            <a ref="tooltip" :title="leaderBoardArray[1].fullname">
              <v-avatar 
                size="35" 
                class="mb-1 elevation-1" 
                v-on="on">
                <img
                  :src="getUserAvatar(leaderBoardArray[1].remoteId)">
              </v-avatar>
            </a>
            <v-card-text
              class="top2 grey lighten-1 px-3 py-2 flex d-flex white--text align-center font-weight-bold"
              style="height: 40px">
              {{ leaderBoardArray[1].score }}
            </v-card-text>
          </div>

          <div v-if="(typeof leaderBoardArray[0] != 'undefined')" class="transparent mx-1 align-center">
            <a ref="tooltip" :title="leaderBoardArray[0].fullname">
              <v-avatar 
                size="35" 
                class="mb-1 elevation-1" 
                v-on="on">
                <img
                  :src="getUserAvatar(leaderBoardArray[0].remoteId)">
              </v-avatar>
            </a>
            <v-card-text
              class="top1 yellow darken-1 px-3 py-2 flex d-flex white--text  align-center font-weight-bold"
              style="height: 55px">
              {{ leaderBoardArray[0].score }}
            </v-card-text>
          </div>

          <div v-if="(typeof leaderBoardArray[2] != 'undefined')" class="transparent mx-1 align-center">
            <a ref="tooltip" :title="leaderBoardArray[2].fullname">
              <v-avatar 
                size="35" 
                class="mb-1 elevation-1" 
                v-on="on1">
                <img :src="getUserAvatar(leaderBoardArray[2].remoteId)">
              </v-avatar>
            </a>
            <v-card-text
              class="top3 amber darken-1 px-3 py-2 flex d-flex white--text align-center font-weight-bold"
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
          <v-list-item
            v-if="(typeof item != 'undefined')"
            :key="item.remoteId"
            class="py-0 px-4 mt-n3">
            <span v-if="(typeof item != 'undefined')" class="mr-2">{{ item.rank }}</span>
            <v-list-item-avatar size="25" class="my-0 mr-2">
              <v-img :src="getUserAvatar(item.remoteId)" />
            </v-list-item-avatar>

            <v-list-item-content v-if="(typeof item != 'undefined')" class="py-0">
              <v-list-item-title class="body-2 " v-html="item.fullname" />
            </v-list-item-content>
            <v-list-item-action v-if="(typeof item != 'undefined')" class="my-0">
              <span>{{ item.score }}</span>
            </v-list-item-action>
          </v-list-item>
        </template>
      </v-list>
    </v-flex>
  </v-layout>
</template>

<script>
  import {getUsersByGamificationRank} from "../profilStatsAPI";
  export default {
    data() {
      return {
        leaderBoardArray: [],
        listBelowPoduim: []
      }
    },
    created() {
      this.getUsersByGamificationRank();
    },

    methods: {
      getUsersByGamificationRank() {
        getUsersByGamificationRank('WEEK').then(
          (data) => {
            const currentUser = eXo.env.portal.profileOwner;
            const index = data.findIndex(function (item, i) {
              return item.remoteId === currentUser
            }) + 1;
            for (let i = 0; i < data.length; i++) {
              this.leaderBoardArray.push(data[i])
            }
            if (data.length === 6) {
              for (let i = 3; i < 6; i++) {
                this.listBelowPoduim.push(data[i])
              }
            } else if (data.length === 3) {
              for (let i = 0; i < data.length; i++) {
                this.listBelowPoduim.push(data[i])
              }
            } else if ((data.length > 3 && data.length < 6) || index === data.length) {
              for (let i = data.length - 3; i < data.length; i++) {
                this.listBelowPoduim.push(data[i])
              }
            } else if (index < 7) {
              for (let i = 3; i < 6; i++) {
                this.listBelowPoduim.push(data[i])
              }
            } else {
              for (let i = index - 2; i < index + 1; i++) {
                this.listBelowPoduim.push(data[i])
              }
            }
        })
      },
      getUserAvatar(username) {
        return `/rest/v1/social/users/${username}/avatar`;
      },
      toProfileStats() {
        this.$emit('isProfileStats');
      }
    }
  }
</script>

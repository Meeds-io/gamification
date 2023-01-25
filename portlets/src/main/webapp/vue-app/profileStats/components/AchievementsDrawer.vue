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
    ref="achievementsDrawer"
    class="achievementsDrawer"
    body-classes="hide-scroll decrease-z-index-more"
    right>
    <template slot="titleIcons">
      <a
        :href="infoUrl"
        :title="$t('exoplatform.gamification.leaderboard.Howearnpoints')"
        class="d-inline-block mb-1">
        <i class="uiIconInformation text-sub-title"></i>
      </a>
    </template>
    <template slot="title">
      {{ $t('exoplatform.gamification.gamificationinformation.achievements') }}
    </template>
    <template slot="content">
      <div class="d-flex">
        <div class="d-block text-center pa-5 mx-auto my-5 border-radius border-color">
          <div class="d-block-inline pull-left mt-2">
            <v-icon color="tertiary" size="36">fa-trophy</v-icon>
          </div>
          <div class="d-block-inline pull-right px-2">
            <div class="caption">
              {{ $t('homepage.profileStatus.weeklyPoints') }}
            </div>
            <div class="title">
              {{ userPoints }}
            </div>
          </div>
        </div>
      </div>
      <v-list v-if="achievements">
        <achievement-item
          v-for="(achievement, i) in achievements"
          :key="i"
          :achievement="achievement" />
      </v-list>
    </template>
    <template v-if="hasMore" slot="footer">
      <v-spacer />
      <v-btn
        :loading="loading"
        :disabled="loading"
        class="loadMoreButton ma-auto btn"
        block
        @click="loadMore">
        {{ $t('exoplatform.gamification.leaderboard.showMore') }}
      </v-btn>
      <v-spacer />
    </template>
  </exo-drawer>
</template>

<script>
import {getAchievements} from '../profilStatsAPI';

export default {
  data: () => ({
    pageSize: 20,
    userPoints: 0,
    loading: false,
    limit: 20,
    achievements: [],
  }),
  computed: {
    infoUrl() {
      return `${eXo.env.portal.context}/${eXo.env.portal.portalName}/contributions/programs`;
    },
    hasMore() {
      return this.loading || this.achievements.length >= this.limit;
    },
  },
  methods: {
    reset() {
      this.limit = 20;
      this.achievements = [];
    },
    open(userPoints) {
      this.reset();
      this.retrieveList();
      this.userPoints = userPoints;
      this.$refs.achievementsDrawer.open();
    },
    loadMore() {
      this.loading = true;
      this.limit += this.pageSize;
      this.retrieveList();
    },
    retrieveList() {
      this.$refs.achievementsDrawer.startLoading();
      return getAchievements('user', eXo.env.portal.profileOwner, this.limit)
        .then(data => this.achievements = data || [])
        .finally(() => {
          this.loading = false;
          this.$refs.achievementsDrawer.endLoading();
        });
    },
  }
};
</script>
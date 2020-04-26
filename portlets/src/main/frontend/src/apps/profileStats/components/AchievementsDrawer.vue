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
        class="d-inline-block">
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
              {{ $t('homepage.profileStatus.totalPoints') }}
            </div>
            <div class="title">
              {{ totalPoints }}
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
import {getAchievements} from '../profilStatsAPI'

export default {
  data: () => ({
    pageSize: 20,
    totalPoints: 0,
    loading: false,
    limit: 20,
    achievements: [],
  }),
  computed: {
    infoUrl() {
      return `${eXo.env.portal.context}/${eXo.env.portal.portalName}/gamification-earn-points`;
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
    open(totalPoints) {
      this.reset();
      this.retrieveList();
      this.totalPoints = totalPoints;
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
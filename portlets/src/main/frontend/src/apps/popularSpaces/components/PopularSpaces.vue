<template>
  <v-app v-if="spaces && spaces.length || firstLoading">
    <v-card flat>
      <v-card-title class="text-sub-title subtitle-1 text-uppercase pb-2">
        {{ $t('popularSpaces.title') }}
      </v-card-title>
      <v-list
        v-if="firstLoading"
        two-line
        class="mx-3 pt-0">
        <popular-spaces-item
          v-for="i in 5"
          :key="i"
          :space="{}"
          skeleton
          @refresh="refresh" />
      </v-list>
      <v-list
        v-else-if="spaces && spaces.length"
        two-line
        class="mx-3 pt-0">
        <popular-spaces-item
          v-for="space in spaces"
          :key="space.technicalId"
          :space="space"
          @refresh="refresh" />
      </v-list>
    </v-card>
  </v-app>
</template>
<script>
import * as popularSpacesService from '../js/PopularSpacesService.js';

export default {
  props: {
    limit: {
      type: Number,
      default: () => 5,
    },
    period: {
      type: String,
      default: () => 'WEEK',
    },
  },
  data: () => ({
    spaces: [],
    firstLoading: true,
  }),
  created() {
    this.refresh();
  },
  methods: {
    refresh() {
      return popularSpacesService.getSpaceLeaderBord(this.period, this.limit)
        .then((spacesByPoints) => {
          const promises = [];
          spacesByPoints
            .filter(spaceByPoints => spaceByPoints.technicalId)
            .forEach(spaceByPoints => {
              promises.push(
                popularSpacesService.getSpace(spaceByPoints.technicalId)
                  .then(space => {
                    if (space && space.id) {
                      Object.assign(spaceByPoints, space);
                      return spaceByPoints;
                    }
                  })
              );
            });
          return Promise.all(promises);
        }).then((spacesByPoints) => {
          this.spaces = spacesByPoints.sort((s1, s2) => s1.rank - s2.rank);
          this.firstLoading = false;
          return spacesByPoints;
        }).finally(() => document.dispatchEvent(new CustomEvent('hideTopBarLoading')));
    },
  },
}
</script>
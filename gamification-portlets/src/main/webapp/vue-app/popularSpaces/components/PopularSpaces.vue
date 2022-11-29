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
  <v-app>
    <v-card flat>
      <v-card-title class="text-sub-title subtitle-1 text-uppercase pb-2">
        {{ $t('popularSpaces.title') }}
      </v-card-title>
      <v-layout>
        <v-col>
          <v-list
            v-if="spaces && spaces.length"
            two-line
            class="pt-0 px-3">
            <popular-spaces-item
              v-for="space in spaces"
              :key="space.technicalId"
              :space="space"
              @refresh="refresh" />
          </v-list>
          <div
            v-else
            class="d-flex justify-center">
            <span class="emptySpacesLeaderboardIcon mb-2">
              Ø
            </span>
          </div>
        </v-col>
      </v-layout>
    </v-card>
  </v-app>
</template>
<script>
import * as popularSpacesService from '../js/PopularSpacesService.js';

export default {
  props: {
    limit: {
      type: Number,
      default: () => 10,
    },
    period: {
      type: String,
      default: () => 'WEEK',
    },
  },
  data: () => ({
    spaces: [],
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
          return this.$nextTick();
        }).finally(() => this.$root.$applicationLoaded());
    },
  },
};
</script>
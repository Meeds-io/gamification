<!--
This file is part of the Meeds project (https://meeds.io/).
Copyright (C) 2022 Meeds Association
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
  <v-card
    :loading="loading"
    height="280"
    width="30%"
    min-width="350"
    flat>
    <div class="d-flex flex-row pa-4">
      <h4 class="hidden-space my-0"> {{ $t('overview.myContributions.title') }} </h4>
      <v-spacer />
      <a v-if="seeAllDisplay" :href="achievementsURL"> <h5 class="text-font-size primary--text my-0"> {{ $t('overview.myContributions.seeAll') }} </h5> </a>
    </div>
    <div class="mt-n2">
      <user-points-widget
        :overview-display="true"
        @seeAll="updateDisplaySeeAll($event)"
        @loadingData="updateLoading($event)" />
    </div>
  </v-card>
</template>
<script>
export default {
  data() {
    return {
      seeAllDisplay: false,
      loading: true
    };
  },
  computed: {
    achievementsURL() {
      return `${eXo.env.portal.context}/${eXo.env.portal.portalName}/contributions/achievements`;
    }
  },
  methods: {
    updateDisplaySeeAll(value) {
      this.seeAllDisplay = value;
    },
    updateLoading(value) {
      this.loading = value;
    },
  }
};
</script>
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
  <gamification-overview-widget :see-all-url="achievementsURL" :loading="loading">
    <template #title>
      {{ $t('overview.myContributions.title') }}
    </template>
    <template #content>
      <user-points-widget
        v-show="!loading"
        overview-display
        @seeAll="updateDisplaySeeAll($event)"
        @loaded="loading = false" />
    </template>
  </gamification-overview-widget>
</template>
<script>
export default {
  data() {
    return {
      seeAllDisplay: false,
      loading: true,
    };
  },
  computed: {
    achievementsURL() {
      return this.seeAllDisplay && !this.loading ? `${eXo.env.portal.context}/${eXo.env.portal.portalName}/contributions/achievements` : '';
    }
  },
  methods: {
    updateDisplaySeeAll(value) {
      this.seeAllDisplay = value;
    },
  }
};
</script>
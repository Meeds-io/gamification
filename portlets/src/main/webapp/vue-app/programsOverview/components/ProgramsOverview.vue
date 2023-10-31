<!--
  This file is part of the Meeds project (https://meeds.io/).
  Copyright (C) 2022 Meeds Association contact@meeds.io
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
    <gamification-overview-widget
      :title="programsDisplayed && $t('gamification.overview.programsOverviewTitle')"
      :action-url="programsDisplayed && programLink"
      :loading="loading">
      <div v-if="programsDisplayed">
        <gamification-overview-program-item
          v-for="program in programs" 
          :key="program.id"
          :program="program"
          class="flex-grow-1" />
        <template v-if="remainingCount">
          <gamification-overview-widget-empty-row
            v-for="index in remainingCount"
            :key="index"
            class="flex-grow-1" />
        </template>
      </div>
      <gamification-overview-widget-row v-else-if="!loading" class="my-auto">
        <template #content>
          <div class="d-flex flex-column align-center justify-center">
            <v-icon color="secondary" size="54">fa-puzzle-piece</v-icon>
            <span class="subtitle-1 font-weight-bold mt-7">{{ $t('gamification.overview.programs') }}</span>
          </div>
        </template>
      </gamification-overview-widget-row>
    </gamification-overview-widget>
    <gamification-program-detail-drawer v-if="programsDisplayed" />
    <engagement-center-rule-extensions />
  </v-app>
</template>
<script>
export default {
  data: () => ({
    programs: [],
    limitToLoad: 4,
    loading: true,
    programsDisplayed: false
  }),
  computed: {
    programURL() {
      return `${eXo.env.portal.context}/${eXo.env.portal.engagementSiteName}/contributions/programs`;
    },
    programLink() {
      return this.programsDisplayed && this.programURL || null;
    },
    remainingCount() {
      return this.limitToLoad - (this.programs?.length || 0);
    },
  },
  created() {
    this.retrievePrograms();
  },
  methods: {
    retrievePrograms() {
      return this.$programService.getPrograms({
        limit: this.limitToLoad,
        type: 'ALL',
        status: 'ENABLED',
        expand: 'countActiveRules',
        sortBy: 'modifiedDate',
        sortDescending: true,
        lang: eXo.env.portal.language,
      })
        .then((data) => {
          this.programs = data?.programs || [];
          this.programsDisplayed = data.size > 0;
          this.loading = false;
        });
    },
  },
};
</script>
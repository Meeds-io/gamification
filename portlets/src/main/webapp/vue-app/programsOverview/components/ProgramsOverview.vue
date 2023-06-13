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
      :see-all-url="programLink"
      :loading="loading"
      extra-class="px-0">
      <template #title>
        <span class="text-truncate">{{ $t('gamification.overview.programsOverviewTitle') }}</span>
      </template>
      <template #content>
        <gamification-overview-widget-row v-show="!programsDisplayed && !loading" class="my-auto mx-4">
          <template #icon>
            <v-icon color="secondary" size="55px">fas fa-bullhorn</v-icon>
          </template>
          <template #content>
            <span v-sanitized-html="emptySummaryText"></span>
          </template>
        </gamification-overview-widget-row>
        <gamification-overview-widget-row
          v-show="programsDisplayed"
          class="py-auto"                   
          v-for="(program, index) in programs" 
          :key="index"
          :redirection-url="`${programURL}/${program.id}`">
          <template #content>
            <gamification-overview-program-item :program="program" />
          </template>
        </gamification-overview-widget-row>
      </template>
    </gamification-overview-widget>
  </v-app>
</template>
<script>
export default {
  data: () => ({
    programs: [],
    status: 'ENABLED',
    type: 'ALL',
    loading: true,
    programsDisplayed: false
  }),
  computed: {
    emptySummaryText() {
      return this.$t('gamification.overview.programsOverviewSummary', {
        0: `<a class="primary--text font-weight-bold" href="${this.programURL}">`,
        1: '</a>',
      });
    },
    programURL() {
      return `${eXo.env.portal.context}/${eXo.env.portal.portalName}/contributions/programs`;
    },
    programLink() {
      return this.programsDisplayed && this.programURL || null;
    },
  },
  created() {
    this.retrievePrograms();
  },
  methods: {
    retrievePrograms() {
      return this.$programService.getPrograms({
        limit: 3,
        type: this.type,
        status: this.status,
        sortByBudget: true,
        lang: eXo.env.portal.language,
      })
        .then((data) => {
          this.programs = (data?.programs || []).sort((p1, p2) => p2.rulesTotalScore - p1.rulesTotalScore);
          this.programsDisplayed = data.size > 0;
          this.loading = false;
        });
    },
  },
};
</script>
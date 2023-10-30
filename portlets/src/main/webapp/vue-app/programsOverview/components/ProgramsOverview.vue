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
    <gamification-overview-widget :loading="loading">
      <template v-if="programsDisplayed || loading" #title>
        <div v-if="programsDisplayed" class="d-flex flex-grow-1 full-width">
          <div class="widget-text-header text-none text-truncate">
            {{ $t('gamification.overview.programsOverviewTitle') }}
          </div>
          <v-spacer />
          <v-btn
            height="auto"
            min-width="auto"
            class="pa-0"
            text
            @click="$refs.listDrawer.open()">
            <span class="primary--text text-none">{{ $t('rules.seeAll') }}</span>
          </v-btn>
        </div>
      </template>
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
      </template>
      <template v-else-if="!loading" #content>
        <gamification-overview-widget-row class="my-auto">
          <template #content>
            <div class="d-flex flex-column align-center justify-center">
              <v-icon color="secondary" size="54">fa-puzzle-piece</v-icon>
              <span class="subtitle-1 font-weight-bold mt-7">{{ $t('gamification.overview.programs') }}</span>
            </div>
          </template>
        </gamification-overview-widget-row>
      </template>
    </gamification-overview-widget>
    <gamification-program-detail-drawer v-if="programsDisplayed" />
    <engagement-center-rule-extensions v-if="programsDisplayed" />
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
      this.loading = true;
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
        })
        .finally(() => this.loading = false);
    },
  },
};
</script>
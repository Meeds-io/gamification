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
        {{ $t('gamification.overview.programsOverviewTitle') }}
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
          v-for="(item, index) in programs" 
          :key="index"
          :redirection-url="`${programURL}/${item.id}`">
          <template #content>
            <span>
              <v-list
                class="pb-0"
                subheader
                two-line>
                <v-list-item
                  two-line>
                  <v-list-item-icon class="mx-2">
                    <v-img
                      :src="item.coverUrl"
                      height="40"
                      width="55" />
                  </v-list-item-icon>
                  <v-list-item-content>
                    <v-list-item-title>
                      {{ item.title }}
                    </v-list-item-title>
                    <v-list-item-subtitle> 
                      <span class="text-light-color" v-sanitized-html="$t('programs.budget', {0: `<span>${item.rulesTotalScore} ${$t('programs.details.label.points')}</span>`})"></span>
                    </v-list-item-subtitle>
                  </v-list-item-content>
                </v-list-item>
              </v-list>
            </span>
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
      return this.$programsServices
        .retrievePrograms(0, 3, this.type, this.status, '', false, true)
        .then((data) => {
          this.programs = data.domains;
          this.programsDisplayed = data.domainsSize > 0;
          this.loading = false;
        });
    },
  },
};
</script>
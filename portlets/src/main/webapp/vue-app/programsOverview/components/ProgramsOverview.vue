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
    <gamification-overview-widget>
      <template #title>
        {{ $t('gamification.overview.programsOverviewTitle') }}
      </template>
      <template #content>
        <gamification-overview-widget-row class="my-auto">
          <template #icon>
            <v-icon color="secondary" size="55px">fas fa-bullhorn</v-icon>
          </template>
          <template #content>
            <span v-html="emptySummaryText"></span>
          </template>
        </gamification-overview-widget-row>
      </template>
    </gamification-overview-widget>
  </v-app>
</template>
<script>
export default {
  data: () => ({
    emptyActionName: 'gamification-programsOverview-check-action',
  }),
  computed: {
    emptySummaryText() {
      return this.$t('gamification.overview.programsOverviewSummary', {
        0: `<a class="primary--text font-weight-bold" href="javascript:void(0)" onclick="document.dispatchEvent(new CustomEvent('${this.emptyActionName}'))">`,
        1: '</a>',
      });
    },
  },
  created() {
    document.addEventListener(this.emptyActionName, this.clickOnEmptyActionLink);
  },
  beforeDestroy() {
    document.removeEventListener(this.emptyActionName, this.clickOnEmptyActionLink);
  },
  methods: {
    clickOnEmptyActionLink() {
      window.location.href = `${eXo.env.portal.context}/${eXo.env.portal.portalName}/contributions/challenges`;
    },
  },
};
</script>
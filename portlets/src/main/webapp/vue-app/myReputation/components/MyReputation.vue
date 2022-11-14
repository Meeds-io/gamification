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
  <gamification-overview-widget :loading="loading">
    <template #title>
      {{ $t('gamification.myReputation.title') }}
    </template>
    <template #content>
      <gamification-overview-widget-row v-show="kudosDisplayed">
        <template #title>
          {{ $t('gamification.myReputation.KudosTitle') }}
        </template>
        <template #content>
          <extension-registry-components
            :params="params"
            name="my-reputation-overview-kudos"
            type="my-reputation-item"
            class="d-flex flex-column mx-n4 mt-n4" />
        </template>
      </gamification-overview-widget-row>
      <gamification-overview-widget-row v-show="!kudosDisplayed">
        <template #title>
          <div class="mb-4">
            {{ $t('gamification.myReputation.KudosTitleNoData') }}
          </div>
        </template>
        <template #icon>
          <v-icon color="secondary" size="55px">fas fa-award</v-icon>
        </template>
        <template #content>
          <span v-html="emptyKudosSummaryText"></span>
        </template>
      </gamification-overview-widget-row>
      <gamification-overview-widget-row :class="kudosDisplayed &&  'mt-n3' || 'mt-11'" v-show="badgesDisplayed">
        <template #title>
          {{ $t('gamification.myReputation.badgesTitle') }}
        </template>
        <template #content>
          <extension-registry-components
            :params="params"
            name="my-reputation-overview-badges"
            type="my-reputation-item"
            class="d-flex flex-column mx-n4 mt-n4" />
        </template>
      </gamification-overview-widget-row>
      <gamification-overview-widget-row :class="kudosDisplayed &&  'mt-n3' || 'mt-11'" v-show="!badgesDisplayed">
        <template #title>
          <div class="mb-3">
            {{ $t('gamification.myReputation.badgesTitle') }}
          </div>
        </template>
        <template #icon>
          <v-icon color="secondary" size="55px">fas fa-graduation-cap</v-icon>
        </template>
        <template #content>
          <span v-html="emptyBadgesSummaryText"></span>
        </template>
      </gamification-overview-widget-row>
    </template>
  </gamification-overview-widget>
</template>
<script>
export default {
  data: () => ({
    emptyKudosActionName: 'gamification-myReputation-kudos-check-actions',
    receivedKudosCount: 0,
    kudosDisplayed: false,
    badgesDisplayed: false,
    loading: true,
  }),
  computed: {
    params() {
      return {
        isOverviewDisplay: true,
      };
    },
    emptyKudosSummaryText() {
      return this.$t('gamification.overview.reputationKudosSummary', {
        0: `<a class="primary--text font-weight-bold" href="javascript:void(0)" onclick="document.dispatchEvent(new CustomEvent('${this.emptyKudosActionName}'))">`,
        1: '</a>',
      });
    },
    emptyBadgesSummaryText() {
      return this.$t('gamification.overview.reputationBadgesSummary');
    },
    kudosData() {
      return (this.sentKudosSize + this.receivedKudosSize) > 0;
    }
  },
  created() {
    document.addEventListener(this.emptyKudosActionName, this.clickOnKudosEmptyActionLink);
    document.addEventListener('kudosCount', (event) => {
      if (event) {
        this.kudosDisplayed = event.detail > 0;
        this.loading = false;
      }
    });
    document.addEventListener('badgesCount', (event) => {
      if (event) {
        this.badgesDisplayed = event.detail > 0;
        this.loading = false;
      }
    });
    this.$root.$applicationLoaded();
  },
  beforeDestroy() {
    document.removeEventListener(this.emptyKudosActionName, this.clickOnKudosEmptyActionLink);
  },
  methods: {
    clickOnKudosEmptyActionLink() {
      window.location.href = `${eXo.env.portal.context}/${eXo.env.portal.portalName}/people`;
    },
  },
};
</script>
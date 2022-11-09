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
  <gamification-overview-widget>
    <template #title>
      {{ $t('gamification.myReputation.title') }}
    </template>
    <template #content>
      <gamification-overview-widget-row v-if="!kudosData">
        <template #title>
          <div class="mb-4">
            {{ $t('gamification.myReputation.KudosTitle') }}
          </div>
        </template>
        <template #icon>
          <v-icon color="secondary" size="55px">fas fa-award</v-icon>
        </template>
        <template #content>
          <span v-html="emptyKudosSummaryText"></span>
        </template>
      </gamification-overview-widget-row>
      <gamification-overview-widget-row v-else>
        <template #title>
          {{ $t('gamification.myReputation.KudosTitle') }}
        </template>
        <template #content>
          <extension-registry-components
            :params="params"
            name="my-reputation-overview-kudos"
            type="my-reputation-item"
            id="kudos-reputation-overview"
            class="d-flex flex-column mx-n4 mt-n4" />
        </template>
      </gamification-overview-widget-row>
    </template>
  </gamification-overview-widget>
</template>
<script>
export default {
  props: {
    sentKudosSize: {
      type: Number,
      default: 0,
    },
    receivedKudosSize: {
      type: Number,
      default: 0,
    },
  },
  data: () => ({
    emptyKudosActionName: 'gamification-myReputation-kudos-check-actions',
    receivedKudosCount: 0,
  }),
  computed: {
    params() {
      return {
        isOverviewDisplay: true,
      };
    },
    emptyKudosSummaryText() {
      return this.$t('gamification.overview.reputationKudosSummary', {
        0: `<a href="javascript:void(0)" onclick="document.dispatchEvent(new CustomEvent('${this.emptyKudosActionName}'))">`,
        1: '</a>',
      });
    },
    kudosData() {
      return (this.sentKudosSize + this.receivedKudosSize) > 0;
    }
  },
  created() {
    document.addEventListener(this.emptyKudosActionName, this.clickOnKudosEmptyActionLink);
    document.addEventListener('availableKudosSentOrReceived', event => {
      if (event?.detail) {
        this.noData = event.detail === 0;
      }});
  },
  beforeDestroy() {
    document.removeEventListener(this.emptyWalletActionName, this.clickOnKudosEmptyActionLink);
  },
  methods: {
    clickOnKudosEmptyActionLink() {
      window.location.href = `${eXo.env.portal.context}/${eXo.env.portal.portalName}/people`;
    },
  },
};
</script>

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
        {{ $t('gamification.overview.rewardsTitle') }}
      </template>
      <template #content>
        <gamification-overview-widget-row>
          <template #title>
            <div class="mb-4">
              {{ $t('gamification.overview.rewardsWalletSubtitle') }}
            </div>
          </template>
          <template #icon>
            <v-icon color="secondary" size="55px">fas fa-wallet</v-icon>
          </template>
          <template #content>
            <span v-html="emptyWalletSummaryText"></span>
          </template>
        </gamification-overview-widget-row>
        <gamification-overview-widget-row class="my-auto">
          <template #title>
            <div class="mb-4">
              {{ $t('gamification.overview.rewardsPerkstoreSubtitle') }}
            </div>
          </template>
          <template #icon>
            <v-icon color="secondary" size="55px">fas fa-shopping-cart</v-icon>
          </template>
          <template #content>
            <span v-html="emptyPerkstoreSummaryText"></span>
          </template>
        </gamification-overview-widget-row>
      </template>
    </gamification-overview-widget>
  </v-app>
</template>
<script>
export default {
  data: () => ({
    emptyWalletActionName: 'gamification-topContributors-wallet-check-actions',
    emptyPerkstoreActionName: 'gamification-topContributors-perk-store-check-actions',
  }),
  computed: {
    emptyWalletSummaryText() {
      return this.$t('gamification.overview.rewardsWalletSummary', {
        0: `<a href="javascript:void(0)" onclick="document.dispatchEvent(new CustomEvent('${this.emptyWalletActionName}'))">`,
        1: '</a>',
      });
    },
    emptyPerkstoreSummaryText() {
      return this.$t('gamification.overview.rewardsPerkstoreSummary', {
        0: `<a href="javascript:void(0)" onclick="document.dispatchEvent(new CustomEvent('${this.emptyPerkstoreActionName}'))">`,
        1: '</a>',
      });
    },
  },
  created() {
    document.addEventListener(this.emptyWalletActionName, this.clickOnWalletEmptyActionLink);
    document.addEventListener(this.emptyPerkstoreActionName, this.clickOnPerkstoreEmptyActionLink);
  },
  beforeDestroy() {
    document.removeEventListener(this.emptyWalletActionName, this.clickOnWalletEmptyActionLink);
    document.removeEventListener(this.emptyPerkstoreActionName, this.clickOnPerkstoreEmptyActionLink);
  },
  methods: {
    clickOnWalletEmptyActionLink() {
      // TODO
      console.warn('clickOnWalletEmptyActionLink');//eslint-disable-line no-console
    },
    clickOnPerkstoreEmptyActionLink() {
      // TODO
      console.warn('clickOnPerkstoreEmptyActionLink');//eslint-disable-line no-console
    },
  },
};
</script>
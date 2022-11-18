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
    <gamification-overview-widget :see-all-url="walletURL" :loading="loading">
      <template #title>
        {{ $t('gamification.overview.rewardsTitle') }}
      </template>
      <template #content>
        <v-card flat height="100">
          <gamification-overview-widget-row v-show="!rewardDisplayed">
            <template #title>
              <div class="mb-4">
                {{ $t('gamification.overview.rewards.walletTitle') }}
              </div>
            </template>
            <template #icon>
              <v-icon class="secondary--text" size="55">fas fa-wallet</v-icon>
            </template>
            <template #content>
              <span v-html="emptyWalletSummaryText"></span>
            </template>
          </gamification-overview-widget-row>
          <div class="d-flex">
            <gamification-overview-widget-row v-show="rewardDisplayed" class="col col-6">
              <template #title>
                {{ $t('gamification.overview.rewards.earningsTitle') }}
              </template>
              <template #content>
                <extension-registry-components
                  :params="params"
                  name="my-rewards-overview"
                  type="my-rewards-item"
                  class="d-flex flex-row mt-5" />
              </template>
            </gamification-overview-widget-row>
            <gamification-overview-widget-row v-show="rewardDisplayed" class="col col-6">
              <template #title>
                {{ $t('gamification.overview.rewards.walletTitle') }}
              </template>
              <template #content>
                <extension-registry-components
                  name="my-rewards-wallet-overview"
                  type="my-rewards-wallet-item"
                  class="d-flex flex-row mt-5" />
              </template>
            </gamification-overview-widget-row>
          </div>
        </v-card>
        <gamification-overview-widget-row class="mt-7">
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
    emptyWalletActionName: 'gamification-wallet-check-actions',
    emptyPerkstoreActionName: 'gamification-perk-store-check-actions',
    loading: true,
    rewardDisplayed: false,
  }),
  computed: {
    emptyWalletSummaryText() {
      return this.$t('gamification.overview.rewardsWalletSummary', {
        0: `<a class="primary--text font-weight-bold" href="javascript:void(0)" onclick="document.dispatchEvent(new CustomEvent('${this.emptyWalletActionName}'))">`,
        1: '</a>',
      });
    },
    emptyPerkstoreSummaryText() {
      return this.$t('gamification.overview.rewardsPerkstoreSummary', {
        0: `<a class="primary--text font-weight-bold" href="javascript:void(0)" onclick="document.dispatchEvent(new CustomEvent('${this.emptyPerkstoreActionName}'))">`,
        1: '</a>',
      });
    },
    params() {
      return {
        isOverviewDisplay: true,
      };
    },
    walletURL() {
      return this.rewardDisplayed ? `${eXo.env.portal.context}/${eXo.env.portal.portalName}/wallet` : '';
    }
  },
  created() {
    document.addEventListener(this.emptyWalletActionName, this.clickOnWalletEmptyActionLink);
    document.addEventListener('countReward', (event) => {
      if (event) {
        this.rewardDisplayed = event.detail > 0;
        this.loading = false;
      }
    });
    document.addEventListener(this.emptyPerkstoreActionName, this.clickOnWalletEmptyActionLink);
  },
  beforeDestroy() {
    document.removeEventListener(this.emptyWalletActionName, this.clickOnWalletEmptyActionLink);
    document.removeEventListener(this.emptyPerkstoreActionName, this.clickOnWalletEmptyActionLink);
  },
  methods: {
    clickOnWalletEmptyActionLink() {
      window.location.href = `${eXo.env.portal.context}/${eXo.env.portal.portalName}/wallet`;
    },
  },
};
</script>
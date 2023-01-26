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
      <template v-if="!loading" #content>
        <v-card
          min-height="114"
          max-height="114"
          flat>
          <gamification-overview-widget-row v-if="!hasConfiguredWallet" class="flex-grow-1">
            <template #title>
              <div class="mb-6">
                {{ $t('gamification.overview.rewards.walletTitle') }}
              </div>
            </template>
            <template #icon>
              <v-icon class="secondary--text" size="55">fas fa-wallet</v-icon>
            </template>
            <template #content>
              <span v-sanitized-html="emptyWalletSummaryText"></span>
            </template>
          </gamification-overview-widget-row>
          <div class="d-flex flex-grow-1" v-else>
            <gamification-overview-widget-row class="col col-6 px-0" normal-height>
              <template #title>
                <div class="d-flex">
                  {{ $t('gamification.overview.rewards.earningsTitle') }}
                  <v-tooltip
                    z-index="4"
                    max-width="300px"
                    bottom>
                    <template #activator="{ on, attrs }">
                      <v-icon
                        size="18"
                        class="primary--text mx-2"
                        v-bind="attrs"
                        v-on="on">
                        fas fa-info-circle
                      </v-icon>
                    </template>
                    <span>{{ $t('gamification.overview.rewards.earningsTooltip') }}</span>
                  </v-tooltip>
                </div>
              </template>
              <template #content>
                <extension-registry-components
                  :params="params"
                  name="my-rewards-overview"
                  type="my-rewards-item"
                  class="d-flex flex-row mt-4" />
              </template>
            </gamification-overview-widget-row>
            <gamification-overview-widget-row class="col col-6 px-0" normal-height>
              <template #title>
                {{ $t('gamification.overview.rewards.walletTitle') }}
              </template>
              <template #content>
                <extension-registry-components
                  :params="params"
                  name="my-rewards-wallet-overview"
                  type="my-rewards-wallet-item"
                  class="d-flex flex-row mt-4" />
              </template>
            </gamification-overview-widget-row>
          </div>
        </v-card>
        <gamification-overview-widget-row class="mt-5 fill-height d-flex flex-column flex-grow-1">
          <template #title>
            <div class="d-flex mb-n1">
              {{ $t('gamification.overview.rewardsPerkstoreSubtitle') }}
              <div v-if="productsLoaded && hasConfiguredWallet" class="ms-auto">
                <a :href="perkstoreLink">
                  <span class="text-font-size primary--text my-0">{{ $t('overview.myContributions.seeAll') }}</span>
                </a>
              </div>
            </div>
          </template>
          <template v-if="displayPerkstorePlaceholder" #icon>
            <v-icon color="secondary" size="55px">fas fa-shopping-cart</v-icon>
          </template>
          <template v-if="!loading" #content>
            <span v-if="displayPerkstorePlaceholder" v-sanitized-html="emptyPerkstoreSummaryText"></span>
            <extension-registry-components
              v-if="hasConfiguredWallet"
              name="my-rewards-perkstore-overview"
              type="my-rewards-perkstore-item" />
          </template>
        </gamification-overview-widget-row>
      </template>
    </gamification-overview-widget>
    <div id="WalletAPIApp"></div>
  </v-app>
</template>
<script>
export default {
  data: () => ({
    walletLink: `${eXo.env.portal.context}/${eXo.env.portal.portalName}/wallet`,
    perkstoreLink: `${eXo.env.portal.context}/${eXo.env.portal.portalName}/perkstore`,
    emptyPerkstoreActionName: 'gamification-perk-store-check-actions',
    loading: true,
    hasConfiguredWallet: false,
    productsLoaded: false,
  }),
  computed: {
    displayPerkstorePlaceholder() {
      return !this.loading && (!this.hasConfiguredWallet || !this.productsLoaded);
    },
    emptyWalletSummaryText() {
      return this.$t('gamification.overview.rewardsWalletSummary', {
        0: `<a class="primary--text font-weight-bold" href="${this.walletLink}">`,
        1: '</a>',
      });
    },
    emptyPerkstoreSummaryText() {
      const labelKey = this.hasConfiguredWallet && 'gamification.overview.rewardsPerkstoreNoProductsSummary' || 'gamification.overview.rewardsPerkstoreSummary';
      const link = this.hasConfiguredWallet && this.perkstoreLink || this.walletLink;
      return this.$t(labelKey, {
        0: `<a class="primary--text font-weight-bold" href="${link}">`,
        1: '</a>',
      });
    },
    params() {
      return {
        isOverviewDisplay: true,
      };
    },
    walletURL() {
      return this.hasConfiguredWallet && this.walletLink || null;
    },
  },
  created() {
    document.addEventListener('exo-wallet-api-initialized', this.init);
    document.addEventListener('exo-wallet-settings-loaded', this.checkUserWalletStatus);
    document.addEventListener('perk-store-products-loaded', this.handleProductsLoaded);
  },
  mounted() {
    this.init();
  },
  beforeDestroy() {
    document.removeEventListener('exo-wallet-api-initialized', this.init);
    document.removeEventListener('exo-wallet-settings-loaded', this.checkUserWalletStatus);
    document.removeEventListener('perk-store-products-loaded', this.handleProductsLoaded);
  },
  methods: {
    handleProductsLoaded() {
      this.productsLoaded = true;
    },
    init() {
      if (!window.walletAPIInitialized) {
        window.require(['SHARED/WalletAPIBundle'], () => {
          document.dispatchEvent(new CustomEvent('exo-wallet-init'));
        });
      }
    },
    checkUserWalletStatus() {
      this.hasConfiguredWallet = !!window?.walletSettings?.wallet.address;
      this.loading = false;
    },
  },
};
</script>
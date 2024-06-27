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
    <gamification-overview-widget v-if="!hasConfiguredWallet" :loading="loading">
      <div v-if="!loading" class="d-flex flex-column align-center justify-center full-height full-width">
        <v-icon color="tertiary" size="54">fa-money-bill</v-icon>
        <span
          v-html="noWalletSummaryText"
          class="text-body mt-7"></span>
      </div>
    </gamification-overview-widget>
    <gamification-overview-widget
      v-else
      :title="$t('gamification.overview.rewardsTitle')"
      :loading="loading">
      <v-card
        v-if="!loading"
        min-height="114"
        class="fill-height transparent"
        flat>
        <div class="d-flex flex-grow-1 fill-height">
          <gamification-overview-widget-row class="d-flex flex-column col col-6 px-0" normal-height>
            <template #title>
              <div class="d-flex text-body">
                {{ $t('gamification.overview.rewards.earningsTitle') }}
              </div>
            </template>
            <extension-registry-components
              :params="params"
              name="my-rewards-overview"
              type="my-rewards-item"
              class="d-flex flex-row my-auto" />
          </gamification-overview-widget-row>
          <gamification-overview-widget-row class="d-flex flex-column col col-6 px-0" normal-height>
            <template #title>
              <div class="text-body d-flex">
                <div>{{ $t('gamification.overview.rewards.walletTitle') }}</div>
                <v-spacer />
                <v-btn
                  :href="walletURL"
                  height="auto"
                  min-width="auto"
                  class="pa-0"
                  text>
                  <span class="primary--text text-none mt-n1 pt-2px">{{ $t('rules.seeAll') }}</span>
                </v-btn>
              </div>
            </template>
            <extension-registry-components
              :params="params"
              name="my-rewards-wallet-overview"
              type="my-rewards-wallet-item"
              class="d-flex flex-row my-auto" />
          </gamification-overview-widget-row>
        </div>
      </v-card>
      <gamification-overview-widget-row class="fill-height d-flex flex-column flex-grow-1">
        <template v-if="!displayPerkstorePlaceholder && productsLoaded" #title>
          <div class="d-flex mb-n1">
            {{ $t('gamification.overview.rewardsPerkstoreSubtitle') }}
            <div v-if="hasProducts" class="ms-auto">
              <a :href="perkstoreLink">
                <span class="text-font-size primary--text my-0">{{ $t('overview.myContributions.seeAll') }}</span>
              </a>
            </div>
          </div>
        </template>
        <template v-if="!loading">
          <div v-if="displayPerkstorePlaceholder && productsLoaded" class="d-flex flex-column align-center justify-center">
            <v-icon color="tertiary" size="54">fa-shopping-cart</v-icon>
            <span
              v-sanitized-html="emptyPerkstoreSummaryText"
              class="text-body mt-7"></span>
          </div>
          <extension-registry-components
            v-if="hasConfiguredWallet"
            name="my-rewards-perkstore-overview"
            type="my-rewards-perkstore-item" />
        </template>
      </gamification-overview-widget-row>
    </gamification-overview-widget>
    <div id="WalletAPIApp"></div>
  </v-app>
</template>
<script>
export default {
  data: () => ({
    walletLink: `${eXo.env.portal.context}/${eXo.env.portal.myCraftSiteName}/wallet`,
    perkstoreLink: `${eXo.env.portal.context}/${eXo.env.portal.engagementSiteName}/perkstore`,
    createPerkstoreProductLink: `${eXo.env.portal.context}/${eXo.env.portal.engagementSiteName}/perkstore/catalog#create`,
    emptyPerkstoreActionName: 'gamification-perk-store-check-actions',
    loading: true,
    hasConfiguredWallet: false,
    hasProducts: false,
    canAddProduct: false,
    productsLoaded: false,
  }),
  computed: {
    displayPerkstorePlaceholder() {
      return !this.loading && (!this.hasConfiguredWallet || !this.hasProducts);
    },
    noWalletSummaryText() {
      return this.$t('gamification.overview.noWalletMessage', {
        0: `<a class="primary--text font-weight-bold" href="${this.walletLink}">`,
        1: '</a>',
      });
    },
    emptyWalletSummaryText() {
      return this.$t('gamification.overview.rewardsWalletSummary', {
        0: `<a class="primary--text font-weight-bold" href="${this.walletLink}">`,
        1: '</a>',
      });
    },
    emptyPerkstoreSummaryText() {
      if (this.canAddProduct) {
        return this.$t('gamification.overview.noProducts', {
          0: `<a class="primary--text font-weight-bold" href="${this.createPerkstoreProductLink}">`,
          1: '</a>',
        });
      } else {
        return this.$t('gamification.overview.noProductsNoCreate');
      }
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
    handleProductsLoaded(event) {
      this.canAddProduct = event?.detail?.canAddProduct;
      this.hasProducts = event?.detail?.hasProducts;
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
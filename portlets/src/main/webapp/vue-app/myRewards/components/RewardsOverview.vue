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
      <template #title>
        <span></span>
      </template>
      <template #default>
        <div v-show="!loading" class="d-flex flex-column align-center justify-center full-height full-width">
          <v-icon color="tertiary" size="60">fa-money-bill</v-icon>
          <span
            v-html="noWalletSummaryText"
            class="text-body mt-5"></span>
        </div>
      </template>
    </gamification-overview-widget>
    <gamification-overview-widget
      v-else
      :title="$t('gamification.overview.rewardsTitle')"
      :action-url="walletURL"
      :loading="loading">
      <v-card
        v-if="!loading"
        class="my-auto"
        flat>
        <div class="d-flex">
          <gamification-overview-widget-row
            class="d-flex flex-column col col-6 px-0"
            normal-height
            dense>
            <div class="d-flex flex-column align-center justify-center">
              <extension-registry-components
                :params="params"
                name="my-rewards-overview"
                type="my-rewards-item"
                class="d-flex flex-row my-auto" />
              <div class="text-body text-body-1">{{ $t('gamification.overview.rewards.earningsTitle') }}</div>
            </div>
          </gamification-overview-widget-row>
          <gamification-overview-widget-row
            class="d-flex flex-column col col-6 px-0"
            normal-height
            dense>
            <div class="d-flex flex-column align-center justify-center">
              <extension-registry-components
                :params="params"
                name="my-rewards-wallet-overview"
                type="my-rewards-wallet-item"
                class="d-flex flex-row my-auto" />
              <div class="text-body text-body-1">{{ $t('gamification.overview.rewards.walletTitle') }}</div>
            </div>
          </gamification-overview-widget-row>
        </div>
      </v-card>
    </gamification-overview-widget>
    <div id="WalletAPIApp"></div>
  </v-app>
</template>
<script>
export default {
  data: () => ({
    walletLink: `${eXo.env.portal.context}/${eXo.env.portal.myCraftSiteName}/wallet`,
    loading: true,
    hasConfiguredWallet: false,
  }),
  computed: {
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
  },
  mounted() {
    this.init();
  },
  beforeDestroy() {
    document.removeEventListener('exo-wallet-api-initialized', this.init);
    document.removeEventListener('exo-wallet-settings-loaded', this.checkUserWalletStatus);
  },
  methods: {
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
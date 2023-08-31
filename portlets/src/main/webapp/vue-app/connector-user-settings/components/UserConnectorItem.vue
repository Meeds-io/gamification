<!--
This file is part of the Meeds project (https://meeds.io/).
Copyright (C) 2020 - 2023 Meeds Association
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
  <v-app class="full-width">
    <v-card
      flat>
      <div class="d-flex flex-row">
        <div class="d-flex">
          <div class="d-flex align-center">
            <v-img
              :src="logo"
              :alt="title"
              height="40"
              width="40" />
          </div>
          <v-list class="d-none d-sm-inline ms-3">
            <v-list-item-title>
              {{ title }}
            </v-list-item-title>
            <v-list-item-subtitle class="text-truncate d-flex caption mt-1">{{ description }}</v-list-item-subtitle>
          </v-list>
          <a
            v-if="connectorRemoteIdentifier"
            class="ps-1 subtitle-1 text-decoration-underline d-sm-none d-flex align-center"
            :href="connectorRemoteIdentifierLink"
            target="_blank">{{ connectorRemoteIdentifier }}</a>
        </div>
        <v-spacer />
        <div class="d-flex align-center">
          <template v-if="connectorRemoteIdentifier">
            <a
              class="pe-4 text-decoration-underline d-none d-sm-inline"
              :href="connectorRemoteIdentifierLink"
              target="_blank">{{ connectorRemoteIdentifier }}</a>
            <v-btn
              :loading="loading"
              class="btn"
              small
              @click="disconnect">
              <span class="mx-sm-2 text-capitalize-first-letter subtitle-1">
                {{ $t('gamification.connectors.label.disconnect') }}
              </span>
            </v-btn>
          </template>
          <v-btn
            v-else
            :loading="loading"
            class="btn"
            small
            @click="connect">
            <span class="mx-sm-2 text-capitalize-first-letter subtitle-1">
              {{ $t('gamification.connectors.label.connect') }}
            </span>
          </v-btn>
        </div>
      </div>
    </v-card>
  </v-app>
</template>

<script>
export default {
  props: {
    connector: {
      type: Object,
      default: null
    },
    connectorExtensions: {
      type: Array,
      default: () => [],
    },
  },
  data: () => ({
    loading: false,
  }),
  computed: {
    connectorExtension() {
      return this.connectorExtensions.find(c => c.name === this.connector?.name);
    },
    connectorRemoteIdentifier() {
      return this.connector?.identifier || '';
    },
    connectorRemoteIdentifierLink() {
      return `${this.connectorExtension?.PROFILE_BASER_URL}/${this.connectorRemoteIdentifier}`;
    },
    logo() {
      return this.connectorExtension?.logo || '';
    },
    title() {
      return this.$t(`${this.connectorExtension?.title}`);
    },
    description() {
      return this.$t(`${this.connectorExtension?.description}`);
    },
  },
  methods: {
    disconnect() {
      this.loading = true;
      return this.$gamificationConnectorService.disconnect(this.connector.name, this.connectorRemoteIdentifier)
        .then(() => this.$root.$emit('gamification-connectors-refresh'))
        .finally(() => this.loading = false);
    },
    connect() {
      /*
       * TODO, replace with:
       * this.loading = true;
       * this.connectorExtension.connect(this.connector)
       *  .then((accessToken, remoteId) => this.$gamificationConnectorService.connect(this.connector.name, accessToken, remoteId))
       *  .then(() => this.$root.$emit('gamification-connectors-refresh'))
       *  .finally(() => this.loading = false);
       */
      this.loading = true;
      const popup = this.connectorExtension.openOauthPopup(this.connector);
      // Listen for changes in the popup window's URL
      const popupInterval = setInterval(() => {
        if (popup.location.href.startsWith(this.connector.redirectUrl)) {
          clearInterval(popupInterval);
          popup.close();
          this.handleConnectCallback(popup.location.href, this.connector)
            .finally(() => this.loading = false);
        }
      }, 500);
    },
    handleConnectCallback(callbackUrl) {
      const url = new URL(callbackUrl);
      const searchParams = new URLSearchParams(url.search);
      const accessToken = searchParams.get('code');
      return this.$gamificationConnectorService.connect(this.connector.name, accessToken, this.connector.identifier)
        .then(() => this.$root.$emit('gamification-connectors-refresh'))
        .catch(e => {
          if (e.message === 'AccountAlreadyUsed') {
            document.dispatchEvent(new CustomEvent('notification-alert', {detail: {
              message: this.$t('gamification.connectors.error.alreadyUsed',  {
                0: this.connector.name,
              }),
              type: 'error',
            }}));
          }
        });
    },
  }
};
</script>
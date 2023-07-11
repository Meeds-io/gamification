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
<script>
export default {
  props: {
    connectors: {
      type: Array,
      default: () => [],
    },
    connectorExtensions: {
      type: Array,
      default: () => [],
    },
  },
  data() {
    return {
      loading: false,
      username: eXo.env.portal.userName,
    };
  },
  created() {
    document.addEventListener('gamification-connectors-refresh', this.refreshUserConnectorList);
    this.$root.$on('gamification-connector-connect', this.connect);
    this.$root.$on('gamification-connector-disconnect', this.disconnect);
    document.addEventListener('extension-gamification-connectors-updated', this.refreshUserConnectorList);
    this.init();
  },
  methods: {
    init() {
      this.refreshUserConnectorList();
      this.connectorExtensions.forEach(extension => {
        if (extension.init) {
          const initPromise = extension.init();
          if (initPromise?.then) {
            return initPromise
              .then(() => this.$nextTick());
          }
        }
      });
      // Check connectors status from store
      this.loading = true;
      this.$gamificationConnectorService.getConnectors(this.username, 'userIdentifier')
        .then(connectors => {
          this.connectors = connectors;
          this.$emit('connectors-loaded', this.connectors, this.connectorExtensions);
        }).finally(() => this.loading = false);
    },
    refreshUserConnectorList() {
      // Get list of connectors from extensionRegistry
      this.connectorExtensions = extensionRegistry.loadExtensions('gamification', 'connectors') || [];
    },
    connect(connector, connectorExtension) {
      this.$set(connector, 'loading', true);
      const popup = connectorExtension.openOauthPopup(connector);
      // Listen for changes in the popup window's URL
      const popupInterval = setInterval(() => {
        if (popup.location.href.startsWith(connector.redirectUrl)) {
          clearInterval(popupInterval);
          popup.close();
          this.handleCallback(popup.location.href, connector).then(() => {
            connector.loading = false;
          });
        }
      }, 500);
    },
    handleCallback(callbackUrl, connector) {
      const url = new URL(callbackUrl);
      const searchParams = new URLSearchParams(url.search);
      const accessToken = searchParams.get('code');
      return this.$gamificationConnectorService.connect(connector.name, accessToken).then((identifier) => {
        console.warn('identifier' ,identifier);
        this.$set(connector, 'identifier', identifier);
        this.$root.$emit('gamification-connectors-refresh');
      }).catch(e => {
        if (e.message === 'AccountAlreadyUsed') {
          document.dispatchEvent(new CustomEvent('notification-alert', {detail: {
            message: this.$t('gamification.connectors.error.alreadyUsed',  {
              0: connector.name,
            }),
            type: 'error',
          }}));
        }})
        .finally(() => this.$set(connector, 'loading', false));
    },
    disconnect(connector) {
      if (connector.identifier) {
        return this.$gamificationConnectorService.disconnect(connector.name).then(() => {
          this.$set(connector, 'identifier', null);
          this.$root.$emit('gamification-connectors-refresh');
        })
          .finally(() => this.$set(connector, 'loading', false));
      }
    },
  },
};
</script>

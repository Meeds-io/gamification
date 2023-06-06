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
      type: Object,
      default: () => null,
    },
  },
  data() {
    return {
      loading: false,
    };
  },
  created() {
    this.refreshConnectorsList();
    document.addEventListener('gamification-connectors-refresh', this.refreshConnectorsList);
    this.$root.$on('gamification-connector-connect', this.addAccount);
    this.$root.$on('gamification-connector-disconnect', this.removeAccount);
  },
  mounted() {
    this.refreshConnectorsList();
  },
  methods: {
    refreshConnectorsList() {
      // Get list of connectors from extensionRegistry
      const connectors = extensionRegistry.loadExtensions('gamification', 'connectors') || [];
      // Check connectors status from store
      this.$connectorService.getConnectors().then(connectorsList => {
        if (connectorsList && connectorsList.length) {
          connectors.forEach(connector => {
            const connectorObj = connectorsList.find(connectorSettings => connectorSettings.name === connector.name);
            connector.apiKey = connectorObj && connectorObj.apiKey || '';
            connector.redirectURL = connectorObj && connectorObj.redirectURL || '';
            connector.enabled = connectorObj != null;
            connector.isSignedIn = connectorObj.connected;
            connector.identifier = connectorObj.identifier || '';
            connector.user = eXo.env.portal.userName;
          });
        } else {
          connectors.forEach(connector => (connector.enabled = false));
        }
      });

      this.$emit('connectors-loaded', connectors);
    },
    addAccount(connector) {
      this.$set(connector, 'loading', true);
      const popup = connector.openOauthPopup(connector);
      // Listen for changes in the popup window's URL
      const popupInterval = setInterval(() => {
        if (popup.location.href.startsWith(connector.redirectURL)) {
          clearInterval(popupInterval);
          this.handleCallback(popup.location.href, connector).then(() => {
            connector.loading = false;
          }).finally(() => popup.close());
        }
      }, 500);
    },
    handleCallback(callbackUrl, connector) {
      const url = new URL(callbackUrl);
      const searchParams = new URLSearchParams(url.search);
      const code = searchParams.get('code');
      const connectorLoginRequest = {
        accessToken: code,
        connectorName: connector.name
      };
      return this.$connectorService.saveUserConnector(connectorLoginRequest)
        .then(githubIdentifier => {
          connector.user = githubIdentifier || null;
          document.dispatchEvent(new CustomEvent('gamification-connectors-refresh'));
        });
    },
    removeAccount(connector) {
      if (connector.isSignedIn) {
        return this.$connectorService.removeUserConnector(connector).then(() => {
          this.$set(connector, 'isSignedIn', false);
          this.$set(connector, 'user', null);
          this.$root.$emit('gamification-connectors-refresh');
        })
          .finally(() => {
            this.$set(connector, 'loading', false);
          });
      }
    },
  },
};
</script>

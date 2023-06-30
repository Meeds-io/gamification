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
      username: eXo.env.portal.userName,
    };
  },
  created() {
    document.addEventListener('gamification-connectors-refresh', this.refreshUserConnectorList);
    this.$root.$on('gamification-connector-connect', this.connect);
    this.$root.$on('gamification-connector-disconnect', this.disconnect);
  },
  mounted() {
    this.refreshUserConnectorList();
  },
  methods: {
    refreshUserConnectorList() {
      // Get list of connectors from extensionRegistry
      const connectors = extensionRegistry.loadExtensions('gamification', 'connectors') || [];
      // Check connectors status from store
      this.$userConnectorService.getUsersConnectorsSetting(this.username).then(connectorsList => {
        if (connectorsList?.length) {
          connectors.forEach(connector => {
            const connectorObj = connectorsList.find(connectorSettings => connectorSettings.name === connector.name);
            this.$set(connector, 'apiKey', connectorObj?.apiKey || '');
            this.$set(connector, 'redirectUrl', connectorObj?.redirectUrl || '');
            this.$set(connector, 'enabled', connectorObj?.enabled && connectorObj?.apiKey !== '' && connectorObj?.redirectUrl !== '');
            this.$set(connector, 'identifier', connectorObj?.identifier || '');
            this.$set(connector, 'user', eXo.env.portal.userName);
          });
        } else {
          connectors.forEach(connector => (connector.enabled = false));
        }
      });
      this.$emit('connectors-loaded', connectors);
    },
    connect(connector) {
      this.$set(connector, 'loading', true);
      const popup = connector.openOauthPopup(connector);
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
      return this.$userConnectorService.connect(connector.name, accessToken).catch(e => {
        if (e.message === 'AccountAlreadyUsed') {
          document.dispatchEvent(new CustomEvent('notification-alert', {detail: {
            message: this.$t('gamification.connectors.error.alreadyUsed',  {
              0: connector.name,
            }),
            type: 'error',
          }}));
        }})
        .then(() => {
          document.dispatchEvent(new CustomEvent('gamification-connectors-refresh'));
        });
    },
    disconnect(connector) {
      if (connector.identifier) {
        return this.$userConnectorService.disconnect(connector.name).then(() => {
          this.$set(connector, 'identifier', null);
          this.$root.$emit('gamification-connectors-refresh');
        })
          .finally(() => this.$set(connector, 'loading', false));
      }
    },
  },
};
</script>

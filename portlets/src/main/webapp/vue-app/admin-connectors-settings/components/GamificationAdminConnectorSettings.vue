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
  <v-app>
    <v-card class="my-3 border-radius" flat>
      <extension-registry-component
        v-if="displayConnectorSettings"
        :params="params"
        :component="connectorExtension" />
      <gamification-admin-connector-detail
        v-else-if="displayConnectorDetails"
        class="px-4"
        :connector-setting="connectorExtension" />

      <v-list v-else class="pa-0">
        <v-list-item>
          <v-list-item-content class="pa-0">
            <v-list-item-subtitle class="my-3 text-sub-title font-italic">
              <gamification-admin-connector-card-list :enabled-connectors-extensions="enabledConnectorsExtensions" />
            </v-list-item-subtitle>
          </v-list-item-content>
        </v-list-item>
      </v-list>
    </v-card>
  </v-app>
</template>

<script>

export default {
  data() {
    return {
      connectors: [],
      adminConnectorsExtensionsSettings: [],
      displayConnectorDetails: false,
      displayConnectorSettings: false,
      connectorExtension: null
    };
  },
  computed: {
    enabledConnectorsExtensions() {
      if (!this.adminConnectorsExtensionsSettings) {
        return [];
      }
      return this.adminConnectorsExtensionsSettings.slice().sort((extension1, extension2) => {
        return extension1.componentOptions.rank - extension2.componentOptions.rank;
      });
    },
    params() {
      return {
        apiKey: this.connectorExtension?.componentOptions?.apiKey,
        secretKey: this.connectorExtension?.componentOptions?.secretKey,
        redirectUrl: this.connectorExtension?.componentOptions.redirectUrl || '',
        enabled: this.connectorExtension?.componentOptions.enabled || '',
      };
    },
  },
  created() {
    this.$root.$on('open-connector-detail', this.openConnectorDetail);
    this.$root.$on('close-connector-detail', () => this.displayConnectorDetails = false);
    this.$root.$on('open-connector-settings', this.openConnectorSettings);
    document.addEventListener('close-connector-settings', this.closeConnectorSettings);
    document.addEventListener('save-connector-settings', this.saveConnectorSetting);
    document.addEventListener('delete-connector-settings', this.deleteConnectorSetting);
    this.refreshConnectors();
  },
  methods: {
    refreshConnectors() {
      this.adminConnectorsExtensionsSettings = [];
      this.adminConnectorsExtensionsSettings = extensionRegistry.loadComponents('gamification-admin-connector') || [];
      // Check connectors from settings
      this.$adminConnectorService.getConnectorsSettings().then(connectorsSettings => {
        if (connectorsSettings?.length) {
          this.adminConnectorsExtensionsSettings.forEach(connector => {
            const connectorObj = connectorsSettings.find(connectorSettings => connectorSettings.name === connector.componentOptions.name);
            connector.componentOptions.apiKey = connectorObj?.apiKey || '';
            connector.componentOptions.secretKey = connectorObj?.secretKey || '';
            connector.componentOptions.redirectUrl = connectorObj?.redirectUrl || '';
            connector.componentOptions.enabled = connectorObj != null;
          });
        } else {
          this.adminConnectorsExtensionsSettings.forEach(connector => (connector.enabled = false));
        }
      });
    },
    openConnectorDetail(connector) {
      this.connectorExtension = connector;
      this.displayConnectorDetails = true;
    },
    openConnectorSettings(connector) {
      this.connectorExtension = connector;
      this.displayConnectorSettings = true;
    },
    closeConnectorSettings() {
      this.displayConnectorSettings = false;
    },
    saveConnectorSetting(event) {
      if (event?.detail) {
        const setting = event?.detail;
        this.$adminConnectorService.saveConnectorSettings(setting?.name, setting?.apiKey, setting?.secretKey, setting?.redirectUrl, setting?.enabled);
      }
    },
    deleteConnectorSetting(event) {
      if (event?.detail) {
        this.$adminConnectorService.deleteConnectorSetting(event?.detail);
      }
    },
  }
};
</script>

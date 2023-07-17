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
      <template v-if="selectedConnector">
        <extension-registry-component
          v-if="editSettings"
          :params="selectedConnector"
          :component="connectorExtension" />
        <gamification-admin-connector-detail
          v-else
          class="px-4"
          :connector="selectedConnector"
          :connector-extension="connectorExtension" />
      </template>
      <v-list v-else class="pa-0">
        <v-list-item>
          <v-list-item-content class="pa-0">
            <v-list-item-subtitle class="my-3 text-sub-title">
              <gamification-admin-connector-card-list
                v-if="connectors.length"
                :enabled-connectors="connectors"
                :connector-extensions="adminConnectorsExtensions" />
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
      adminConnectorsExtensions: [],
      editSettings: false,
      connectorExtension: null,
      selectedConnector: null
    };
  },
  created() {
    this.$root.$on('open-connector-detail', this.openConnectorDetail);
    this.$root.$on('close-connector-detail', () => this.selectedConnector = null);
    this.$root.$on('open-connector-settings', this.openConnectorSettings);
    document.addEventListener('close-connector-settings', this.closeConnectorSettings);
    document.addEventListener('save-connector-settings', this.saveConnectorSetting);
    document.addEventListener('delete-connector-settings', this.deleteConnectorSetting);
    this.init();
  },
  methods: {
    init() {
      this.refreshUserConnectorList();
      this.adminConnectorsExtensions.forEach(extension => {
        if (extension?.componentOptions?.init) {
          const initPromise = extension.componentOptions.init();
          if (initPromise?.then) {
            return initPromise
              .then(() => this.$nextTick());
          }
        }
      });
      // Check connectors status from store
      this.loading = true;
      this.$gamificationConnectorService.getConnectors(eXo.env.portal.userName, 'secretKey')
        .then(connectors => this.connectors = connectors)
        .finally(() => this.loading = false);
    },
    refreshUserConnectorList() {
      // Get list of connectors from extensionRegistry
      this.adminConnectorsExtensions = extensionRegistry.loadComponents('gamification-admin-connector') || [];
    },
    openConnectorDetail(connector, connectorExtension) {
      this.connectorExtension = connectorExtension;
      this.selectedConnector = connector;
      this.selectedConnector = Object.assign({}, this.selectedConnector);
    },
    openConnectorSettings(connector, connectorExtension) {
      this.connectorExtension = connectorExtension;
      this.selectedConnector= connector;
      this.selectedConnector = Object.assign({}, this.selectedConnector);
      this.editSettings = true;
    },
    closeConnectorSettings() {
      this.editSettings = false;
    },
    saveConnectorSetting(event) {
      if (event?.detail) {
        const setting = event?.detail;
        this.$gamificationConnectorService.saveConnectorSettings(setting?.name, setting?.apiKey, setting?.secretKey, setting?.redirectUrl, setting?.enabled).then(() => {
          this.selectedConnector = Object.assign(this.selectedConnector, setting);
        });
      }
    },
    deleteConnectorSetting(event) {
      if (event?.detail) {
        return this.$gamificationConnectorService.deleteConnectorSetting(event?.detail).then(() => {
          const setting = {
            apiKey: '',
            secretKey: '',
            redirectUrl: '',
            enabled: false
          };
          this.selectedConnector = Object.assign(this.selectedConnector, setting);
        });
      }
    },
  }
};
</script>

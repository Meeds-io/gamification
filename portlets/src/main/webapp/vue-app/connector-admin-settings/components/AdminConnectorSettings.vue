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
    <v-card class="card-border-radius overflow-hidden" flat>
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
                :connectors="connectors"
                :connector-extensions="adminConnectorsExtensions" />
            </v-list-item-subtitle>
          </v-list-item-content>
        </v-list-item>
      </v-list>
      <engagement-center-rule-extensions />
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
      connectorProjectId: null,
      connectorExtension: null,
      selectedConnector: null,
      connectorsLinkBasePath: '/portal/g/:platform:rewarding/gamificationConnectorsAdministration',
    };
  },
  watch: {
    selectedConnector() {
      if (this.selectedConnector?.name) {
        window.history.replaceState('gamification connectors', this.$t('gamification.connectors.label.connectors'), `${this.connectorsLinkBasePath}#${this.selectedConnector?.name}`);
      } else {
        window.history.replaceState('gamification connectors', this.$t('gamification.connectors.label.connectors'), `${this.connectorsLinkBasePath}`);
      }
    },
    editSettings() {
      if (this.connectorProjectId) {
        window.history.replaceState('gamification connectors', this.$t('gamification.connectors.label.connectors'), `${this.connectorsLinkBasePath}#${this.selectedConnector?.name}-${this.connectorProjectId}`);
      } else if (this.editSettings && this.selectedConnector?.name) {
        window.history.replaceState('gamification connectors', this.$t('gamification.connectors.label.connectors'), `${this.connectorsLinkBasePath}#${this.selectedConnector?.name}-configuration`);
      } else if (this.selectedConnector?.name){
        window.history.replaceState('gamification connectors', this.$t('gamification.connectors.label.connectors'), `${this.connectorsLinkBasePath}#${this.selectedConnector?.name}`);
      } else {
        window.history.replaceState('gamification connectors', this.$t('gamification.connectors.label.connectors'), `${this.connectorsLinkBasePath}`);
      }
    },
  },
  created() {
    this.$root.$on('open-connector-detail', this.openConnectorDetail);
    this.$root.$on('close-connector-detail', () => this.selectedConnector = null);
    this.$root.$on('open-connector-settings', this.openConnectorSettings);
    document.addEventListener('close-connector-settings', this.closeConnectorSettings);
    document.addEventListener('save-connector-settings', this.saveConnectorSetting);
    document.addEventListener('delete-connector-settings', this.deleteConnectorSetting);
    document.addEventListener(`extension-${this.extensionApp}-${this.connectorExtensionType}-updated`, this.refreshUserConnectorList);
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
        .then(connectors => {
          const filteredList = this.adminConnectorsExtensions.filter(connectorExtension => !connectors.some(item => item.name === connectorExtension.componentOptions.name)).map(item => ({
            name: item?.componentOptions?.name,
            title: item?.componentOptions?.title,
            comingSoon: item?.componentOptions?.comingSoon,
          })) || [];
          this.connectors.push(...filteredList);
          this.connectors.push(...connectors);
          const promises = [];
          this.connectors.forEach(connector => {
            const promise = this.$gamificationConnectorService.getTriggers(connector.name)
              .then(triggers => {
                this.$set(connector, 'triggers', triggers);
              });
            promises.push(promise);
          });
          return Promise.all(promises);
        })
        .then(() => {
          const fragment = document.location.hash.substring(1);
          const match = fragment.split('-');
          if (match) {
            const connectorName = match[0];
            const prefix = match[1];
            const connector = this.connectors.find(c => c.name === connectorName);
            if (connector && !prefix) {
              this.openConnector(connector);
            } else {
              if (Number(prefix)) {
                this.connectorProjectId = Number(prefix);
              }
              const connectorSetting = this.connectors.find(c => c.name === connectorName);
              if (connectorSetting) {
                const connectorExtension = this.adminConnectorsExtensions.find(c => c?.componentOptions?.name === connectorSetting.name);
                this.openConnectorSettings(connectorSetting, connectorExtension);
              }
            }
          }
        })
        .finally(() => {
          this.loading = false;
        });
    },
    openConnector(connector) {
      const connectorExtension = this.adminConnectorsExtensions.find(c => c?.componentOptions?.name === connector.name);
      this.openConnectorDetail(connector, connectorExtension);
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
      if (event?.detail && event?.detail?.name === this.selectedConnector?.name) {
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

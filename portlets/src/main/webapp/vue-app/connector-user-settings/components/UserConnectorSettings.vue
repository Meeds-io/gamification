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
    <v-card
      v-if="displayUserSetting"
      class="card-border-radius"
      flat>
      <v-list>
        <v-list-item>
          <v-list-item-content>
            <v-list-item-title class="title text-color">
              {{ $t('gamification.connectors.label.thirdPartyApps') }}
            </v-list-item-title>
            <v-list-item-subtitle class="my-3 text-sub-title font-italic">
              <gamification-user-connector-setting-list :connected-connectors="enabledConnectors" :connector-extensions="connectorExtensions" />
            </v-list-item-subtitle>
          </v-list-item-content>
        </v-list-item>
      </v-list>
    </v-card>
    <gamification-user-connector-setting
      :connectors="connectors"
      :connector-extensions="connectors"
      @connectors-loaded="connectorsLoaded" />
  </v-app>
</template>

<script>
export default {
  data: () => ({
    displayed: true,
    connectors: [],
    connectorExtensions: [],
  }),
  computed: {
    enabledConnectors() {
      return  this.connectors?.filter(connector => connector.enabled) || [];
    },
    displayUserSetting() {
      return this.displayed && this.enabledConnectors?.length > 0;
    }
  },
  created() {
    document.addEventListener('hideSettingsApps', (event) => {
      if (event?.detail && this.id !== event.detail) {
        this.displayed = false;
      }
    });
    document.addEventListener('showSettingsApps', () => this.displayed = true);
  },
  methods: {
    connectorsLoaded(connectors, connectorExtensions) {
      this.connectors = connectors;
      this.connectorExtensions = connectorExtensions;
    }
  }
};
</script>
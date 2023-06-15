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
  <v-app v-if="displayed">
    <v-card class="my-3 border-radius" flat>
      <v-list two-line>
        <v-list-item>
          <v-list-item-content>
            <v-list-item-title class="title text-color">
              {{ $t('gamification.connectors.label.connectors') }}
            </v-list-item-title>
            <v-list-item-subtitle class="my-3 text-sub-title font-italic">
              <gamification-connectors-status :connected-connectors="enabledConnectors" />
            </v-list-item-subtitle>
          </v-list-item-content>
          <gamification-connector
            :connectors="connectors"
            @connectors-loaded="connectors = $event" />
        </v-list-item>
      </v-list>
    </v-card>
  </v-app>
</template>

<script>
export default {
  data: () => ({
    displayed: true,
    connectors: [],
    enabledConnectors: [],
  }),
  watch: {
    connectors: {
      immediate: true,
      deep: true,
      handler: function() {
        if (this.connectors?.length) {
          this.enabledConnectors = this.connectors?.filter(connector => connector.enabled) || [];
        }
      }
    },
  },
  created() {
    document.addEventListener('hideSettingsApps', (event) => {
      if (event?.detail && this.id !== event.detail) {
        this.displayed = false;
      }
    });
    document.addEventListener('showSettingsApps', () => this.displayed = true);
  },
};
</script>
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
  <v-card v-if="enabledConnectors && enabledConnectors.length" flat>
    <div class="text-header-title py-2 py-sm-5 d-flex align-center">{{ $t('gamification.connectors.label.connectors') }}</div>
    <v-card-text class="text-font-size pb-2 pt-0 pb-sm-5 px-0 d-flex align-center">{{ $t('gamification.connectors.label.valueContributions') }}</v-card-text>
    <v-card-text class="pa-0">
      <v-item-group>
        <v-container class="pa-0">
          <div class="ma-0 d-flex flex-wrap">
            <div
              v-for="enabledConnector in enabledConnectors"
              :key="enabledConnector.id"
              class="pa-0 me-2 my-2 d-flex flex-column col-4">
              <gamification-admin-connector-card
                class="full-height"
                :connector="enabledConnector"
                :connector-extensions="connectorExtensions" />
            </div>
          </div>
        </v-container>
      </v-item-group>
    </v-card-text>
  </v-card>
</template>

<script>
export default {
  props: {
    enabledConnectors: {
      type: Array,
      default: () => [],
    },
    connectorExtensions: {
      type: Array,
      default: () => [],
    },
  },
};
</script>
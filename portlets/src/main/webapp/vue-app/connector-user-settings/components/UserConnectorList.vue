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
  <v-list v-if="connectedConnectors && connectedConnectors.length !== 0" class="pa-0">
    <gamification-user-connector-setting-item
      v-for="connector in enabledConnectors"
      :key="connector.name"
      :connector="connector"
      :connector-extensions="connectorExtensions" />
  </v-list>
</template>

<script>
export default {
  props: {
    connectedConnectors: {
      type: Array,
      default: () => [],
    },
    connectorExtensions: {
      type: Array,
      default: () => [],
    },
  },
  computed: {
    enabledConnectors() {
      return this.connectedConnectors.slice().sort((connector1, connector2) => {
        return this.getConnectorRank(connector1) - this.getConnectorRank(connector2);
      });
    },
  },
  methods: {
    getConnectorRank(connector) {
      const extension = this.connectorExtensions.find(item => connector.name === item.name);
      return extension ? extension.rank : 0;
    },
  },
};
</script>
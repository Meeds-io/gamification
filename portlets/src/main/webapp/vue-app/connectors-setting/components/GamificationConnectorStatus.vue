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
  <v-list-item class="pa-0">
    <v-list-item-content transition="fade-transition">
      <v-list-item-title class="text-color">
        <div class="d-flex">
          <v-icon
            size="40">
            {{ connectedConnectorIcon }}
          </v-icon>
          <v-list class="ms-3">
            <v-list-item-title>
              {{ connectorTitle }}
            </v-list-item-title>
            <v-list-item-subtitle class="text-truncate d-flex caption mt-1">{{ connectorDescription }}</v-list-item-subtitle>
          </v-list>
        </div>
      </v-list-item-title>
    </v-list-item-content>
    <v-list-item-action>
      <div class="d-flex align-center">
        <a
          v-if="connected"
          class="pe-4 text-decoration-underline"
          :href="connectorIdentifierLink"
          target="_blank">{{ connectorIdentifier }}</a>
        <v-btn
          v-if="connected"
          :loading="connector.loading"
          class="btn"
          small
          @click="disconnect">
          <span class="mx-2 text-capitalize-first-letter subtitle-1">
            {{ $t('gamification.connectors.label.disconnect') }}
          </span>
        </v-btn>
        <v-btn
          v-else
          :loading="connector.loading"
          class="btn"
          small
          @click="connect">
          <span class="mx-2 text-capitalize-first-letter subtitle-1">
            {{ $t('gamification.connectors.label.connect') }}
          </span>
        </v-btn>
      </div>
    </v-list-item-action>
  </v-list-item>
</template>

<script>
export default {
  props: {
    connector: {
      type: Object,
      default: null
    },
  },
  computed: {
    connected() {
      return this.connector?.isSignedIn;
    },
    connectorIdentifier() {
      return this.connector?.identifier || '';
    },
    connectorIdentifierLink() {
      return this.connectorIdentifier && `https://github.com/${this.connectorIdentifier}`;
    },
    connectedConnectorIcon() {
      return this.connector?.icon || '';
    },
    connectorTitle() {
      return this.connector?.title || '';
    },
    connectorDescription() {
      return this.connector?.description || '';
    },
  },
  methods: {
    connect() {
      this.$root.$emit('gamification-connector-connect', this.connector);
    },
    disconnect() {
      this.$root.$emit('gamification-connector-disconnect', this.connector);
    },
  }
};
</script>
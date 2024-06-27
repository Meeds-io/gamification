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
  <v-card
    id="gamificationConnectorCard"
    class="card engagement-center-card d-flex flex-column overflow-hidden rounded"
    :class="comingSoon && 'not-clickable'"
    height="230"
    max-height="230"
    outlined
    hover
    v-on="!comingSoon ? { click: openDetail } : {}">
    <v-card-text class="d-flex flex-row">
      <div class="d-flex align-center">
        <v-icon
          v-if="icon"
          size="28"
          :class="iconColorClass">
          {{ icon }}
        </v-icon>
        <img
          v-else
          :src="image"
          :alt="name"
          width="28">
        <v-list class="ms-3">
          <v-list-item-title class="font-weight-bold">
            {{ title }}
          </v-list-item-title>
        </v-list>
      </div>
    </v-card-text>
    <v-card-text class="py-0 text-truncate-3">{{ description }}</v-card-text>
    <v-spacer />
    <div v-if="comingSoon" class="d-flex flex-row pa-4">
      <v-icon size="20" class="primary--text">fas fa-bolt</v-icon>
      <div class="text-body ps-3">{{ $t('challenges.label.comingSoon') }}</div>
    </div>
    <div v-if="triggersSize" class="d-flex flex-row pa-4">
      <v-icon size="20" class="primary--text">fas fa-bolt</v-icon>
      <div class="text-subtitle ps-3">{{ triggersSize }} {{ $t('gamification.label.events') }}</div>
    </div>
  </v-card>
</template>
<script>

export default {
  props: {
    connector: {
      type: Object,
      default: null
    },
    connectorExtensions: {
      type: Array,
      default: () => [],
    },
  },
  data: () => ({
    initialized: false,
  }),
  computed: {
    connectorExtension() {
      return this.connectorExtensions.find(extension => extension?.name === this.connector?.name);
    },
    icon() {
      return this.connectorExtension?.icon;
    },
    image() {
      return this.connectorExtension?.image;
    },
    iconColorClass() {
      return this.connectorExtension?.iconColorClass;
    },
    name() {
      return this.connectorExtension?.name || '';
    },
    title() {
      return this.connectorExtension?.title || '';
    },
    description() {
      return this.$t(`${this.connectorExtension?.description}`);
    },
    comingSoon() {
      return this.connectorExtension?.comingSoon;
    },
    triggersSize() {
      return this.connector?.triggers?.length;
    },
  },
  watch: {
    connectorExtension: {
      immediate: true,
      handler(extension) {
        if (extension) {
          const result = extension?.init?.();
          if (result?.then) {
            return result.finally(() => this.initialized = true);
          }
          this.initialized = true;
        }
      },
    },
  },
  methods: {
    openDetail() {
      this.$root.$emit('open-connector-detail', this.connector, this.connectorExtension);
    },
  }
};
</script>
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
      id="gamificationConnectorCard"
      class="card engagement-center-card d-flex flex-column overflow-hidden rounded"
      height="230"
      max-height="230"
      outlined
      hover
      @click="openDetail">
      <v-card-text class="d-flex flex-row">
        <div class="d-flex align-center">
          <v-icon
            size="28"
            :class="iconColorClass">
            {{ icon }}
          </v-icon>
          <v-list class="ms-3">
            <v-list-item-title class="font-weight-bold">
              {{ title }}
            </v-list-item-title>
          </v-list>
        </div>
      </v-card-text>
      <v-card-text class="py-0 text-truncate-3">{{ description }}</v-card-text>
    </v-card>
  </v-app>
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
  computed: {
    connectorExtension() {
      return this.connectorExtensions.find(c => c?.componentOptions?.name === this.connector?.name);
    },
    icon() {
      return this.connectorExtension?.componentOptions?.icon;
    },
    iconColorClass() {
      return this.connectorExtension?.componentOptions?.iconColorClass;
    },
    name() {
      return this.connectorExtension?.componentOptions?.name || '';
    },
    title() {
      return this.connectorExtension?.componentOptions?.title || '';
    },
    description() {
      return this.$t(`${this.connectorExtension?.componentOptions?.description}`);
    },
  },
  methods: {
    openDetail() {
      this.$root.$emit('open-connector-detail', this.connector, this.connectorExtension);
    },
  }
};
</script>
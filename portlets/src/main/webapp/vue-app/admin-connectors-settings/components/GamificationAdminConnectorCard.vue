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
      class="pa-3 full-height"
      outlined>
      <div class="d-flex flex-row">
        <div class="d-flex align-center">
          <v-img
            :src="logo"
            :alt="name"
            height="30"
            width="30" />
          <v-list class="ms-3">
            <v-list-item-title class="font-weight-bold">
              {{ title }}
            </v-list-item-title>
          </v-list>
        </div>
        <v-spacer />
        <v-btn
          small
          icon
          @click="openDetail">
          <v-icon size="20" class="text-sub-title">
            {{ $vuetify.rtl && 'fa-caret-left' || 'fas fa-arrow-right' }}
          </v-icon>
        </v-btn>
      </div>
      <div class="mt-4 text-truncate-4">{{ description }}</div>
    </v-card>
  </v-app>
</template>
<script>

export default {
  props: {
    connectorExtension: {
      type: Object,
      default: null
    },
  },
  computed: {
    logo() {
      return this.connectorExtension?.componentOptions.logo || '';
    },
    name() {
      return this.connectorExtension?.componentOptions.name || '';
    },
    title() {
      return this.connectorExtension?.componentOptions.title || '';
    },
    description() {
      return this.$t(`${this.connectorExtension?.componentOptions.description}`);
    },
  },
  methods: {
    openDetail() {
      this.$root.$emit('open-connector-detail', this.connectorExtension);
    },
  }
};
</script>
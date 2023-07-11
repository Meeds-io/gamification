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
  <v-app class="full-width">
    <v-card
      flat>
      <div class="d-flex flex-row">
        <div class="d-flex">
          <div class="d-flex align-center">
            <v-img
              :src="logo"
              :alt="title"
              height="40"
              width="40" />
          </div>
          <v-list class="ms-3">
            <v-list-item-title>
              {{ title }}
            </v-list-item-title>
            <v-list-item-subtitle class="text-truncate d-flex caption mt-1">{{ description }}</v-list-item-subtitle>
          </v-list>
        </div>
        <v-spacer />
        <div class="d-flex align-center">
          <template v-if="identifier">
            <a
              class="pe-4 text-decoration-underline"
              :href="identifierLink"
              target="_blank">{{ identifier }}</a>
            <v-btn
              :loading="connector.loading"
              class="btn"
              small
              @click="disconnect">
              <span class="mx-2 text-capitalize-first-letter subtitle-1">
                {{ $t('gamification.connectors.label.disconnect') }}
              </span>
            </v-btn>
          </template>
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
      </div>
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
      return this.connectorExtensions.find(c => c.name === this.connector?.name);
    },
    identifier() {
      return this.connector?.identifier || '';
    },
    identifierLink() {
      return `${this.connectorExtension?.PROFILE_BASER_URL}/${this.identifier}`;
    },
    logo() {
      return this.connectorExtension?.logo || '';
    },
    title() {
      return this.$t(`${this.connectorExtension?.title}`);
    },
    description() {
      return this.$t(`${this.connectorExtension?.description}`);
    },
  },
  methods: {
    connect() {
      this.$root.$emit('gamification-connector-connect', this.connector, this.connectorExtension);
    },
    disconnect() {
      this.$root.$emit('gamification-connector-disconnect', this.connector);
    },
  }
};
</script>
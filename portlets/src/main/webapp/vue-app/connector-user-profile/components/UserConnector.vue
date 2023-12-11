<!--

  This file is part of the Meeds project (https://meeds.io/).

  Copyright (C) 2020 - 2023 Meeds Association contact@meeds.io

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
  <div class="d-flex">
    <v-list v-show="initialized" class="full-width">
      <v-list-item
        :href="connectorRemoteIdentifierLink"
        target="_blank"
        rel="nofollow noreferrer noopener">
        <v-list-item-avatar class="ms-0 my-0">
          <v-icon
            v-if="icon"
            size="33"
            :class="iconColorClass">
            {{ icon }}
          </v-icon>
          <img
            v-else
            :src="image"
            :alt="name">
        </v-list-item-avatar>
        <span
          class="subtitle-1 text-color my-0">{{ connectorRemoteIdentifier }} </span>
      </v-list-item>
    </v-list>
  </div>
</template>

<script>
export default {
  props: {
    connector: {
      type: Object,
      default: () => null,
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
    connectorRemoteIdentifier() {
      return this.connector?.identifier;
    },
    name() {
      return this.connector?.name || '';
    },
    connectorExtension() {
      return this.connectorExtensions.find(c => c.name === this.name);
    },
    connectorRemoteIdentifierLink() {
      return `${this.connectorExtension?.PROFILE_BASER_URL}/${this.connectorRemoteIdentifier}`;
    },
    icon() {
      return this.connectorExtension?.icon || '';
    },
    iconColorClass() {
      return this.connectorExtension?.iconColorClass;
    },
    image() {
      return this.connectorExtension?.image;
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
};
</script>
<!--

 This file is part of the Meeds project (https://meeds.io/).

 Copyright (C) 2020 - 2024 Meeds Association contact@meeds.io

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
  <v-list-item dense class="border-color-grey border-radius my-2 ps-3">
    <v-list-item-avatar
      class="mx-2"
      size="25"
      tile>
      <v-icon
        v-if="icon"
        :class="iconColorClass">
        {{ icon }}
      </v-icon>
      <img
        v-else
        :src="image"
        :alt="name">
    </v-list-item-avatar>
    <v-list-item-content>
      <v-list-item-title v-text="label" />
    </v-list-item-content>
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
    name() {
      return this.connector?.name || '';
    },
    label() {
      return this.connector?.actionLabel ? this.$t(`${this.connector?.actionLabel}`) : this.connector?.title;
    },
    icon() {
      return this.connector?.icon || '';
    },
    iconColorClass() {
      return this.connector?.iconColorClass;
    },
    image() {
      return this.connector?.image;
    },
  }
};
</script>
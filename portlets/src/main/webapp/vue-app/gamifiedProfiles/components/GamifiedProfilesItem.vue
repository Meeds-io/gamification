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
  <div class="d-flex">
    <v-list-item>
      <v-list-item-avatar class="ms-0 my-0">
        <v-img
          :src="logo"
          height="30"
          width="30" />
      </v-list-item-avatar>
      <a
        :href="identifierLink"
        class="subtitle-1 text-color my-0"
        target="_blank">{{ identifier }} </a>
    </v-list-item>
  </div>
</template>

<script>
export default {
  props: {
    connector: {
      type: Object,
      default: () => null,
    },
  },
  computed: {
    identifier() {
      return this.connector?.identifier || '';
    },
    identifierLink() {
      return `${this.connector?.PROFILE_BASER_URL}/${this.identifier}`;
    },
    logo() {
      return this.connector?.logo || '';
    },
  },
};
</script>

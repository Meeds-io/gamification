<!--

  This file is part of the Meeds project (https://meeds.io/).

  Copyright (C) 2023 Meeds Association contact@meeds.io

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
  <v-list-group
    v-if="size"
    v-model="open"
    :prepend-icon="open && 'fa-chevron-up fa-lg ms-2' || 'fa-chevron-down fa-lg ms-2'"
    color="grey darken-1"
    append-icon=""
    dense>
    <template #activator>
      <v-list-item-title class="text-color d-flex align-center ms-n4">
        {{ category.title }} ( {{ size }} )
        <v-divider class="ms-4" />
      </v-list-item-title>
    </template>
    <v-list-item class="my-4">
      <engagement-center-rules-list
        :rules="rules"
        :category-id="category.id" />
    </v-list-item>
  </v-list-group>
</template>
<script>
export default {
  props: {
    category: {
      type: Object,
      default: null,
    },
  },
  data: () => ({
    open: true,
  }),
  computed: {
    size() {
      return this.category?.size || this.rules?.length || 0;
    },
    rules() {
      return this.category?.rules || [];
    },
    hasMore() {
      return this.size > this.category?.rules?.length;
    },
  },
};
</script>
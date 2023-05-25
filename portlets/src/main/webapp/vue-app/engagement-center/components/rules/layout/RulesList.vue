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
  <v-container>
    <v-row class="mx-n3">
      <v-col
        v-for="rule in rules"
        :key="rule.id"
        class="rule-column"
        cols="12"
        sm="6"
        lg="4">
        <engagement-center-rule-card
          :rule="rule"
          :program="program"
          :category-id="categoryId" />
      </v-col>
      <v-col
        v-if="hasMore"
        cols="12"
        class="px-3">
        <v-btn
          :loading="loading"
          class="loadMoreButton btn"
          block
          @click="$root.$emit('rules-category-load-more', categoryId)">
          {{ $t('engagementCenter.button.ShowMore') }}
        </v-btn>
      </v-col>
    </v-row>
  </v-container>
</template>
<script>
export default {
  props: {
    categoryId: {
      type: String,
      default: null,
    },
    size: {
      type: Number,
      default: 0,
    },
    rules: {
      type: Array,
      default: null,
    },
    program: {
      type: Object,
      default: null,
    },
    loading: {
      type: Boolean,
      default: false,
    },
  },
  computed: {
    hasMore() {
      return this.size > this.rules.length;
    },
  },
};
</script>

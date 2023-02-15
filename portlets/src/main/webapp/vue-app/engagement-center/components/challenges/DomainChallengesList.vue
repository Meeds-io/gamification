<!--
This file is part of the Meeds project (https://meeds.io/).
Copyright (C) 2022 Meeds Association
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
  <v-expansion-panel v-if="size">
    <v-expansion-panel-header class="pa-3" hide-actions>
      <template #default="{open}">
        <v-list-item flat dense>
          <v-list-item-icon class="me-2">
            <v-icon size="16" v-text="open && 'fa-chevron-up' || 'fa-chevron-down'" />
          </v-list-item-icon>
          <v-list-item-content class="flex flex-grow-0 flex-shrink-0 me-2">
            {{ title }}
          </v-list-item-content>
          <v-list-item-content class="flex flex-grow-0 flex-shrink-0 me-2">
            ( {{ size }} )
          </v-list-item-content>
          <v-list-item-content class="mr-7">
            <v-divider />
          </v-list-item-content>
        </v-list-item>
      </template>
    </v-expansion-panel-header>
    <v-expansion-panel-content>
      <v-container class="pa-0">
        <v-row no-gutters>
          <v-col
            v-for="challenge in challenges"
            :key="challenge.id"
            class="mb-4 challenge-column"
            cols="12"
            sm="6"
            lg="4">
            <challenge-card
              :domain="domain"
              :challenge="challenge"
              :can-edit-challenge="canEditChallenge" />
          </v-col>
        </v-row>
        <v-row v-if="hasMore" class="ml-6 mr-6 mb-6 mt-n4">
          <v-btn
            class="loadMoreButton ma-auto mt-4 btn"
            block
            @click="$root.$emit('challenge-load-more', domain.id)">
            {{ $t('engagementCenter.button.ShowMore') }}
          </v-btn>
        </v-row>
      </v-container>
    </v-expansion-panel-content>
  </v-expansion-panel>
</template>
<script>
export default {
  props: {
    domain: {
      type: Array,
      default: function() {
        return [];
      },
    },
    challenges: {
      type: Object,
      default: function() {
        return {};
      },
    },
    canEditChallenge: {
      type: Boolean,
      default: false,
    }
  },
  computed: {
    title() {
      const key = `exoplatform.gamification.gamificationinformation.domain.${this.domain.title}`;
      return this.$te(key) && this.$t(key) || this.domain.title;
    },
    size() {
      return this.domain.challengesSize;
    },
    hasMore() {
      return this.domain.challengesSize > this.domain.challenges.length;
    },
  },
};
</script>

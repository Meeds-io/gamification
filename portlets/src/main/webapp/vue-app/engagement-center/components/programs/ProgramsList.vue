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
  <v-container>
    <v-row no-gutters>
      <v-col
        v-for="program in programs"
        :key="program.id"
        class="mb-4"
        cols="12"
        sm="6"
        lg="4">
        <engagement-center-program-card
          :program="program"
          :is-administrator="isAdministrator"
          class="mx-2" />
      </v-col>
    </v-row>
    <v-row v-if="hasMore" class="ml-6 mr-6 mb-6 mt-n4">
      <v-btn
        :loading="loading"
        :disabled="loading"
        class="loadMoreButton ma-auto mt-4 btn"
        block
        @click="$root.$emit('program-load-more')">
        {{ $t('engagementCenter.button.ShowMore') }}
      </v-btn>
    </v-row>
  </v-container>
</template>

<script>
export default {
  props: {
    programsList: {
      type: Array,
      default: function() {
        return [];
      },
    },
    loading: {
      type: Boolean,
      default: false,
    },
    isAdministrator: {
      type: Boolean,
      default: false,
    }
  },
  computed: {
    programs() {
      return this.programsList?.domains;
    },
    hasMore() {
      return this.programsList?.domainsSize > this.programs?.length ;
    },
  }
};
</script>

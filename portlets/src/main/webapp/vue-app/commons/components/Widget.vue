<!--
  This file is part of the Meeds project (https://meeds.io/).
  Copyright (C) 2022 Meeds Association contact@meeds.io
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
  <v-card
    :loading="loading"
    :height="height"
    :min-width="minWidth"
    class="white d-flex flex-column"
    flat>
    <v-card-title class="subtitle-1 text-sub-title justify-space-between flex-nowrap">
      <slot name="title"></slot>
      <a 
        v-if="seeAllUrl" 
        class="flex-shrink-0" 
        :href="seeAllUrl"> <h5 class="text-font-size primary--text my-0"> {{ $t('overview.myContributions.seeAll') }} </h5> </a>
    </v-card-title>
    <v-card-text class="d-flex flex-column flex-grow-1" :class="extraClass || ''">
      <slot name="content"></slot>
    </v-card-text>
  </v-card>
</template>
<script>
export default {
  props: {
    loading: {
      type: Boolean,
      default: () => false,
    },
    seeAllUrl: {
      type: String,
      default: () => '',
    },
    extraClass: {
      type: String,
      default: () => '',
    },
    height: {
      type: String,
      default: () => '338px',
    },
    minWidth: {
      type: String,
      default: () => '290px',
    },
  },
};
</script>

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
  <div class="d-flex flex-column align-center flex ma-auto">
    <img
      v-if="image"
      :src="image"
      class="mb-4"
      alt=""
      width="50">
    <v-icon
      v-else
      class="white--text mb-4"
      size="52">
      {{ icon }}
    </v-icon>
    <div
      v-if="title"
      class="white--text text-wrap title full-width px-2">
      {{ title }}
    </div>
    <div
      v-sanitized-html="prerequisitesTitle"
      class="white--text text-wrap subtitle-1 px-2 text-truncate-2">
    </div>
  </div>
</template>
<script>
export default {
  props: {
    extension: {
      type: String,
      default: null,
    },
    title: {
      type: String,
      default: null,
    },
  },
  computed: {
    icon() {
      return this.extension?.icon;
    },
    iconColorClass() {
      return this.iconColorClass || this.extension?.iconColorClass || 'white';
    },
    image() {
      return this.extension?.imageCard || this.extension?.image;
    },
    connectorName() {
      return  this.extension?.name.charAt(0).toUpperCase() +  this.extension?.name.slice(1);
    },
    prerequisitesTitle() {
      return this.$t('rules.card.connectYourProfile', {0: this.connectorName});
    }
  }
};
</script>
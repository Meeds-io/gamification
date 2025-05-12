<!--

 This file is part of the Meeds project (https://meeds.io/).

 Copyright (C) 2020 - 2025 Meeds Association contact@meeds.io

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
  <div class="d-flex flex-row justify-end">
    <div class="d-flex position-absolute z-index-one pa-1">
      <div
        v-show="!isDefault"
        class="changeBannerButtonIcon me-1">
        <v-btn
          :title="removeButtonTooltip"
          id="programCoverDeleteButton"
          class="border-color mask-color"
          outlined
          small
          icon
          dark
          @click="$emit('remove')">
          <v-icon size="16">mdi-delete</v-icon>
        </v-btn>
      </div>
      <div class="changeBannerButtonIcon">
        <v-btn
          :title="changeButtonTooltip"
          id="programCoverEditButton"
          class="border-color mask-color"
          outlined
          icon
          small
          dark
          @click="$emit('edit')">
          <v-icon size="16">fas fa-file-image</v-icon>
        </v-btn>
      </div>
    </div>
  </div>
</template>
<script>
export default {
  props: {
    isDefault: {
      type: Boolean,
      default: false,
    },
    imageType: {
      type: String,
      default: null,
    },
  },
  computed: {
    isAvatar() {
      return this.imageType === 'avatar';
    },
    changeButtonTooltip() {
      return this.isAvatar && this.$t('programs.details.tooltip.changeProgramAvatar') || this.$t('programs.details.tooltip.changeProgramCover');
    },
    removeButtonTooltip() {
      return this.isAvatar && this.$t('programs.details.tooltip.removeProgramAvatar') || this.$t('programs.details.tooltip.removeProgramCover');
    },
  },
};
</script>
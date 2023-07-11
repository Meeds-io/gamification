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
  <v-card
    id="engagementCenterImgSelectorcard"
    elevation="0"
    class="EngagementCenterImageSelector align-center">
    <engagement-center-program-image-selector-buttons
      :is-default="isDefault"
      :image-type="imageType"
      @edit="$refs.imageCropDrawer.open()"
      @remove="reset" />
    <v-tooltip :disabled="$root.isMobile" bottom>
      <template #activator="{ on }">
        <v-card flat v-on="on">
          <v-avatar
            id="engagementCenterImgSelectorAvatar"
            :height="isAvatar && 50 || 'auto'"
            :max-width="isAvatar && 50 || '100%'"
            :min-width="isAvatar && 50 || '100%'"
            class="align-start flex-grow-0 content-box-sizing border-color rounded position-relative"
            tile>
            <img
              id="engagementCenterImgSelectorImg"
              :src="imageSrc"
              class="full-width"
              role="presentation"
              alt="">
          </v-avatar>
        </v-card>
      </template>
      <span>{{ tooltip }}</span>
    </v-tooltip>
    <image-crop-drawer
      ref="imageCropDrawer"
      :src="imageCropperSrc"
      :crop-options="imageCropperOptions"
      :drawer-title="imageCropperDrawerTitle"
      :max-file-size="maxUploadSizeInBytes"
      :max-image-width="maxImageWidth"
      :no-expand-icon="noExpand"
      back-icon
      @input="updateUploadId"
      @data="imageData = $event" />
  </v-card>
</template>
<script>
export default {
  props: {
    value: {
      type: String,
      default: null,
    },
    imageType: {
      type: String,
      default: null,
    },
    maxUploadSize: {
      type: Number,
      default: () => 2,
    },
    noExpand: {
      type: Boolean,
      default: false,
    },
  },
  data: () => ({
    error: '',
    imageData: null,
    defaultCoverUrl: `${eXo.env.portal.context}/${eXo.env.portal.rest}/gamification/programs/default-cover/cover`,
    defaultAvatarUrl: `${eXo.env.portal.context}/${eXo.env.portal.rest}/gamification/programs/default-avatar/avatar`,
    now: Date.now(),
    random: Math.round(Math.random() * 100000),
    originalUrl: null,
  }),
  computed: {
    isAvatar() {
      return this.imageType === 'avatar';
    },
    defaultImageUrl() {
      return this.isAvatar && this.defaultAvatarUrl || this.defaultCoverUrl;
    },
    maxImageWidth() {
      return this.isAvatar && 500 || 1280;
    },
    imageAspectRatio() {
      return this.isAvatar && 1 || (1248 / 225);
    },
    imageCropperOptions() {
      return {
        aspectRatio: this.imageAspectRatio,
        viewMode: 1,
      };
    },
    imageCropperSrc() {
      return this.isAvatar && this.avatar || this.imageSrc;
    },
    imageCropperDrawerTitle() {
      return this.isAvatar && this.$t('programs.details.addProgramAvatar') || this.$t('programs.details.addProgramCover');
    },
    tooltip() {
      return this.isAvatar && this.$t('programs.label.programAvatarTooltip') || this.$t('programs.label.programCoverTooltip');
    },
    maxUploadSizeInBytes() {
      return this.maxUploadSize * 1024 * 1024;
    },
    imageSrc() {
      return this.imageData || this.value || this.defaultImageUrl;
    },
    isDefault() {
      return this.imageSrc.includes('default');
    },
  },
  created() {
    this.originalUrl = this.value;
  },
  methods: {
    updateUploadId(uploadId) {
      this.$emit('updated', uploadId);
      this.$emit('input', this.originalUrl);
    },
    reset() {
      this.imageData = null;
      this.$emit('input', this.defaultImageUrl);
      this.$emit('deleted');
    },
  },
};
</script>
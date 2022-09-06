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
    elevation="0"
    height="140px"
    class="EngagementCenterImageSelector">
    <v-avatar
      width="100%"
      height="140px"
      class="align-start flex-grow-0"
      tile>
      <v-img
        :lazy-src="coverUrl"
        :src="coverUrl"
        transition="none"
        eager
        role="presentation" />
      <v-file-input
        ref="avatarInput"
        prepend-icon="mdi-image"
        class="changeImgButton"
        accept="image/*"
        clearable
        @change="uploadCover" />
    </v-avatar>
  </v-card>
</template>

<script>

export default {
  props: {
    value: {
      type: Object,
      default: () => {
        return {};
      }
    },
    maxUploadsSizeInMb: {
      type: Number,
      default: function() {
        return 0;
      },
    },
    cover: {
      type: String,
      default: '/gamification-portlets/skin/images/program_default_cover_back.png'
    }
  },
  data() {
    return {
      error: '',
      coverData: null,
      MAX_RANDOM_NUMBER: 100000,
      random: Math.round(Math.random() * this.MAX_RANDOM_NUMBER),
      now: Date.now(),
      uploadId: `${this.random}-${this.now}`,
    };
  },

  computed: {
    coverUrl() {
      return this.coverData || this.cover;
    },
  },
  methods: {
    uploadCover(file) {
      if (file && file.size) {
        if (file.type && file.type.indexOf('image/') !== 0) {
          this.$emit('error', this.$t('engagementCenter.error.uploadUnsupportedFileType'));
          return;
        }
        if (file.size > this.maxUploadsSizeInMb * 1024) {
          this.$emit('error', this.$uploadService.avatarExcceedsLimitError);
          return;
        }
        const thiss = this;
        return this.$uploadService.upload(file)
          .then(uploadId => {
            const reader = new FileReader();
            reader.onload = (e) => {
              thiss.coverData = e.target.result;
              thiss.$forceUpdate();
            };
            reader.readAsDataURL(file);
            this.$emit('updated', uploadId);
          })
          .catch(error => this.$emit('error', error));
      }
    },
    reset() {
      this.coverData = null;
    },
  },
};
</script>
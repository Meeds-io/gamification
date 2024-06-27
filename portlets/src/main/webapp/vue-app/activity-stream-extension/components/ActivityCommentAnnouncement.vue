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
  <div class="d-flex mb-2">
    <v-card
      class="d-flex flex-column flex-grow-0 me-3 flex-shrink-0 border-box-sizing"
      min-width="45"
      max-width="45"
      min-height="45"
      max-height="45"
      color="transparent"
      flat>
      <v-avatar
        size="45"
        class="rule-program-cover border-color primary--text no-border"
        rounded>
        <v-img :src="announcementImageUrl" eager />
      </v-avatar>
    </v-card>
    <div class="flex-grow-1 flex-shrink-1">
      <v-card
        color="transparent"
        flat>
        <div :class="realizationStatusClass" class="text-truncate font-weight-bold text-color text-wrap text-break mb-1">
          {{ realizationStatusLabel }}
        </div>
        <div class="text-subtitle text-wrap text-break rich-editor-content reset-style-box">
          <span v-sanitized-html="announcementComment"></span>
        </div>
      </v-card>
    </div>
  </div>
</template>
<script>
export default {
  props: {
    activity: {
      type: Object,
      default: null,
    },
    activityTypeExtension: {
      type: String,
      default: null,
    },
  },
  computed: {
    announcementId() {
      return this.activity?.templateParams?.announcementId || 0;
    },
    realizationStatus() {
      return this.activity?.templateParams?.realizationStatus || 'ACCEPTED';
    },
    realizationStatusLabel() {
      return this.$t(`announcement.detail.${this.realizationStatus.toLowerCase()}`);
    },
    realizationStatusClass() {
      switch (this.realizationStatus) {
      case 'PENDING': return 'orange--text text--darken-2';
      case 'ACCEPTED': return 'success-color';
      case 'REJECTED': return 'error-color';
      }
      return 'success-color';
    },
    announcementComment() {
      return (this.activity?.templateParams?.comment)
      || this.activity?.title
          || (!this.activity?.originalActivity && this.activity?.body)
          || '';
    },
    announcementImageIndex() {
      return Number(this.announcementId) % 8 + 1;
    },
    announcementImageUrl() {
      return `/gamification-portlets/skin/images/announcement/${this.announcementImageIndex}.webp`;
    },
  },
  created() {
    document.addEventListener('contribution-status-updated', this.refreshComment);
  },
  beforeDestroy() {
    document.removeEventListener('contribution-status-updated', this.refreshComment);
  },
  methods: {
    refreshComment(event) {
      if (this.announcementId === event?.detail?.announcementId) {
        this.activity.templateParams.realizationStatus = event?.detail?.status;
        this.$root.$emit('alert-message',  this.$t('announcement.detail.statusUpdateSuccess'), 'success');
      }
    },
  },
};
</script>
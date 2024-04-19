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
  <user-notification-template
    :loading="loading"
    :notification="notification"
    :message="$t('Notification.gamification.webNotif.contributionRejected')"
    :url="activityUrl">
    <template #avatar>
      <v-badge
          overlap
          bottom
          offset-y="15"
          offset-x="17"
          color="grey-background">
        <v-list-item-avatar
            class="border-color rounded ma-0"
            tile>
          <v-img :src="programAvatarUrl" />
        </v-list-item-avatar>
        <template #badge>
          <v-icon class="error-color">fas fa-times</v-icon>
        </template>
      </v-badge>
    </template>
    <template #actions>
      <div class="d-flex flex-row mb-1">
        <v-icon size="14" class="me-1">fas fa-trophy</v-icon>
        <div class="text-truncate">{{ actionTitle }}
        </div>
      </div>
      <div class="d-flex flex-row">
        <v-icon size="14" class="me-1">far fa-comment</v-icon>
        <div class="text-truncate" v-sanitized-html="comment">
        </div>
      </div>
    </template>
  </user-notification-template>
</template>
<script>
export default {
  props: {
    notification: {
      type: Object,
      default: null,
    },
  },
  data: () => ({
    loading: true,
    realization: null,
  }),
  computed: {
    realizationId() {
      return this.notification?.parameters?.REALIZATION_ID;
    },
    comment() {
      return this.realization?.comment;
    },
    activityId() {
      return this.notification?.parameters?.activityId;
    },
    activityUrl() {
      return this.activityId && `${eXo.env.portal.context}/${eXo.env.portal.metaPortalName}/activity?id=${this.activityId}`;
    },
    replyUrl() {
      return this.activityId && `${eXo.env.portal.context}/${eXo.env.portal.metaPortalName}/activity?id=${this.activityId}#comment-reply`
          || '#';
    },
    programAvatarUrl() {
      return this.realization?.program?.avatarUrl;
    },
    actionTitle() {
      return this.realization?.action?.title;
    },
  },
  created() {
    this.$realizationService.getRealizationById(this.realizationId)
      .then(realization => {
        this.realization = realization;
        return this.$nextTick();
      })
      .finally(() => this.loading = false);
  },
};
</script>
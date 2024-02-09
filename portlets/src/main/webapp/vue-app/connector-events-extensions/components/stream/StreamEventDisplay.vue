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
  <v-app>
    <template v-if="activityId">
      <div class="subtitle-1 font-weight-bold mb-2">
        {{ $t('gamification.event.display.doItNow') }}
      </div>
      <v-list-item class="clickable" :href="activityUrl">
        <v-list-item-icon class="me-3 my-auto">
          <v-icon class="primary--text">fas fa-stream</v-icon>
        </v-list-item-icon>
        <v-list-item-content>
          <v-list-item-title class="text-color body-2">
            <p
              class="ma-auto text-truncate"
              v-sanitized-html="activityTitle"></p>
          </v-list-item-title>
        </v-list-item-content>
      </v-list-item>
    </template>
    <template v-else-if="space">
      <div class="subtitle-1 font-weight-bold mb-2">
        {{ $t('gamification.event.display.goThere') }}
      </div>
      <v-list-item class="clickable" :href="spaceUrl">
        <v-list-item-icon class="me-3 my-auto">
          <exo-space-avatar
            :space="space"
            :size="25"
            avatar />
        </v-list-item-icon>
        <v-list-item-content>
          <v-list-item-title class="text-color body-2">
            <p
              class="ma-auto text-truncate"
              v-sanitized-html="spaceName"></p>
          </v-list-item-title>
        </v-list-item-content>
      </v-list-item>
    </template>
    <template v-else>
      <div class="subtitle-1 font-weight-bold mb-2">
        {{ $t('gamification.event.display.goThere') }}
      </div>
      <div class="d-flex justify-center">
        <v-btn
          max-width="250"
          class="ignore-vuetify-classes text-capitalize btn btn-primary">
          {{ $t('gamification.event.display.browseActivities') }}
        </v-btn>
      </div>
    </template>
  </v-app>
</template>

<script>
export default {
  props: {
    properties: {
      type: Object,
      default: null
    },
    trigger: {
      type: String,
      default: null
    },
  },
  data() {
    return {
      activity: null,
      space: null,
    };
  },
  computed: {
    spaceId() {
      return this.properties?.spaceId;
    },
    spaceName() {
      return this.space?.displayName;
    },
    spaceUrl() {
      return `${eXo.env.portal.context}/g/${this.space?.groupId?.replace(/\//g, ':')}`;
    },
    activityId() {
      return this.properties?.activityId;
    },
    activityTitle() {
      return this.activity?.title;
    },
    activityUrl() {
      return this.activityId && `${eXo.env.portal.context}/${eXo.env.portal.metaPortalName}/activity?id=${this.activityId}`;
    },
  },
  created() {
    if (this.activityId) {
      this.loadActivity();
    } else if (this.spaceId) {
      this.$spaceService.getSpaceById(this.spaceId)
        .then(space => this.space = space);
    }
  },
  methods: {
    loadActivity() {
      return this.$activityService.getActivityById(this.activityId)
        .then(fullActivity => {
          this.activity = fullActivity;
        });
    },
  }
};
</script>

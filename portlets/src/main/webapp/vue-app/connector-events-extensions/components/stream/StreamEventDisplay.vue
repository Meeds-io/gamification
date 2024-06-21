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
  <div>
    <template v-if="activityId">
      <div class="subtitle-1 font-weight-bold mb-2">
        {{ $t('gamification.event.display.doItNow') }}
      </div>
      <v-list-item class="clickable" :href="activityUrl">
        <v-list-item-icon class="me-3 my-auto">
          <v-img
            v-if="activityImg"
            :src="activityImg"
            max-height="28"
            max-width="25" />
          <v-icon
            v-else-if="activityIcon"
            size="24"
            :class="activityIconClass">
            {{ activityIcon }}
          </v-icon>
          <v-icon
            v-else
            size="24"
            class="primary--text">
            fas fa-stream
          </v-icon>
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
    <template v-else-if="spaces.length">
      <div class="subtitle-1 font-weight-bold mb-2">
        {{ $t('gamification.event.display.goThere') }}
      </div>
      <v-progress-linear
        v-if="!initialized"
        indeterminate
        height="2"
        color="primary" />
      <meeds-stream-event-space-item
        v-for="space in spaces"
        :key="space.spaceId"
        :space="space" />
    </template>
    <template v-else>
      <div class="subtitle-1 font-weight-bold mb-2">
        {{ $t('gamification.event.display.goThere') }}
      </div>
      <div class="d-flex justify-center">
        <v-btn
          :href="streamLink"
          max-width="250"
          class="ignore-vuetify-classes text-capitalize btn btn-primary">
          {{ $t('gamification.event.display.browseActivities') }}
        </v-btn>
      </div>
    </template>
  </div>
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
      spaces: [],
      extensionApp: 'engagementCenterActions',
      activityIconExtension: 'activity-icon',
      activityExtensions: [],
      initialized: false
    };
  },
  computed: {
    spaceIds() {
      return this.properties?.spaceIds;
    },
    activityId() {
      return this.properties?.activityId;
    },
    activityTitle() {
      return this.activity?.body || (this.activityTypeExtension?.title && this.activityTypeExtension?.title(this.activity)) || this.activity?.title;
    },
    activityUrl() {
      return this.activityId && `${eXo.env.portal.context}/${eXo.env.portal.metaPortalName}/activity?id=${this.activityId}`;
    },
    streamLink() {
      return `${eXo.env.portal.context}/${eXo.env.portal.portalName}/stream`;
    },
    activityTypeExtension() {
      if (this.activity?.type) {
        return this.activityExtensions.find(extension => extension.type === this.activity.type);
      }
      return null;
    },
    activityIcon() {
      return this.activityTypeExtension?.icon;
    },
    activityIconClass() {
      return this.activityTypeExtension?.class;
    },
    activityImg() {
      return this.activityTypeExtension?.img;
    }

  },
  created() {
    if (this.activityId) {
      document.addEventListener(`extension-${this.extensionApp}-${this.activityIconExtension}-updated`, this.refreshActivityIcon);
      this.refreshActivityIcon();
      this.loadActivity();
    } else if (this.spaceIds) {
      this.loadSpaces();
    }
  },
  beforeDestroy() {
    document.removeEventListener(`extension-${this.extensionApp}-${this.activityIconExtension}-updated`, this.refreshActivityIcon);
  },
  methods: {
    loadActivity() {
      return this.$activityService.getActivityById(this.activityId)
        .then(fullActivity => {
          this.activity = fullActivity;
        });
    },
    loadSpaces() {
      this.spaceIds.split(',').forEach((spaceId, index) => {
        this.$spaceService.getSpaceById(spaceId)
          .then((space) => {
            this.spaces.push(space);
          }).finally(() => {
            if (index === this.spaceIds.split(',').length - 1) {
              this.initialized = true;
            }
          });
      });
    },
    refreshActivityIcon() {
      const extensions = extensionRegistry.loadExtensions(this.extensionApp, this.activityIconExtension);
      extensions.forEach(extension => {
        if (extension.id) {
          this.activityExtensions.push(extension);
        }
      });
    },
  }
};
</script>

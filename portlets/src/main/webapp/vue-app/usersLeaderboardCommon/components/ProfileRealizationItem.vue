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
  <v-list-item
    :href="hasAccess && realizationLink"
    :target="hasAccess && realizationLinkTarget"
    class="pa-0 rounded">
    <v-list-item-icon class="me-2 my-auto">
      <rule-icon :rule-event="actionEventName" :size="28" />
    </v-list-item-icon>
    <v-list-item-content class="me-2">
      <v-list-item-title>
        <v-tooltip bottom>
          <template #activator="{ on }">
            <div v-on="on" class="text-truncate">
              {{ actionLabel || $t('gamification.hiddenAction') }}
            </div>
          </template>
          <span>{{ actionLabel || $t('gamification.hiddenActionTooltip') }}</span>
        </v-tooltip>
      </v-list-item-title>
      <v-list-item-subtitle>
        <date-format
          :format="dateFormat"
          :value="createdDate" />
      </v-list-item-subtitle>
    </v-list-item-content>
    <v-list-item-action class="ma-0 pa-0 justify-end">
      <span class="primary--text font-weight-bold">{{ realization.score }}</span>
    </v-list-item-action>
  </v-list-item>
</template>
<script>
export default {
  props: {
    realization: {
      type: Object,
      default: null,
    },
  },
  data: () => ({
    realizationLink: null,
    dateFormat: {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: 'numeric',
      minute: 'numeric',
    },
  }),
  computed: {
    isManualType() {
      return this.realization?.action?.type === 'MANUAL';
    },
    createdDate() {
      return this.realization?.sendingDate || this.realization?.createdDate;
    },
    hasAccess() {
      return !!this.actionLabel;
    },
    actionLabel() {
      return this.realization?.action?.title || this.realization?.actionLabel;
    },
    actionEventName() {
      return this.realization?.action?.event?.title;
    },
    score() {
      return this.realization?.score || '-';
    },
    objectId() {
      return this.realization?.objectId;
    },
    objectType() {
      return this.realization?.objectType;
    },
    actionValueExtension() {
      return this.actionEventName
        && Object.values(this.$root.actionValueExtensions)
          .sort((ext1, ext2) => (ext1.rank || 0) - (ext2.rank || 0))
          .find(extension => extension?.match?.(this.actionEventName))
        || null;
    },
    linkExtensionMethod() {
      return this.actionValueExtension?.getLink;
    },
    realizationLinkTarget() {
      if (!this.realizationLink) {
        return null;
      } else if (this.realizationLink.indexOf('/') === 0 || this.realizationLink.indexOf(window.location.origin) === 0) {
        return '_self';
      } else {
        return '_blank';
      }
    },
  },
  created() {
    Promise.resolve(this.retrieveRealizationLink())
      .finally(() => {
        if (!this.realizationLink) {
          this.realizationLink = this.realization?.link || this.realization?.url;
        }
      });
  },
  methods: {
    retrieveRealizationLink() {
      if (this.isManualType) {
        this.realizationLink = `${eXo.env.portal.context}/${eXo.env.portal.metaPortalName}/activity?id=${this.objectId}`;
      } else if (this.linkExtensionMethod) {
        return this.linkExtensionMethod(this.realization);
      } else if (this.realization?.objectId?.startsWith?.('http')) {
        this.realizationLink = this.realization.objectId;
      }
    },
  }
};
</script>
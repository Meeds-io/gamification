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
  <v-list-item @click="$emit('open')">
    <v-list-item-content class="d-inline">
      <user-avatar
        :name="earnerFullName"
        :avatar-url="earnerAvatarUrl"
        :size="44"
        bold-title
        link-style>
        <template slot="subTitle">
          <relative-date-format
            :value="realization.createdDate"
            class="text-none text-light-color text-truncate" />
        </template>
      </user-avatar>
    </v-list-item-content>
    <v-list-item-action v-if="!$root.isAnonymous">
      <v-tooltip :disabled="$root.isMobile" bottom>
        <template #activator="{ on }">
          <div v-on="on">
            <v-btn
              :href="realizationLink"
              :disabled="!canAccess || !realizationLink"
              icon
              @click.stop="0">
              <v-icon
                size="16"
                class="icon-default-color">
                fas fa-eye
              </v-icon>
            </v-btn>
          </div>
        </template>
        <span>{{ tooltip }}</span>
      </v-tooltip>
    </v-list-item-action>
  </v-list-item>
</template>
<script>
export default {
  props: {
    rule: {
      type: Object,
      default: null,
    },
    realization: {
      type: Object,
      default: null,
    },
  },
  data: () => ({
    realizationLink: null,
    basePath: `${eXo.env.portal.context}/${eXo.env.portal.engagementSiteName}`,
  }),
  computed: {
    earnerFullName() {
      return this.realization?.earner?.fullName;
    },
    earnerAvatarUrl() {
      return this.realization?.earner?.avatarUrl;
    },
    earnerRemoteId() {
      return this.realization?.earner?.remoteId;
    },
    tooltip() {
      if (this.realizationLink && this.canAccess) {
        return this.$t('program.winner.label.checkActivity');
      } else if (!this.canAccess) {
        return this.$t('gamification.hiddenActionTooltip');
      } else {
        return this.$t('program.winner.label.activityNotAccessible');
      }
    },
    canAccess() {
      return this.realization?.program?.userInfo?.canView;
    },
    isManualType() {
      return this.rule?.type === 'MANUAL';
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
    actionEventName() {
      return this.realization?.action?.event;
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
        this.realizationLink = this.realization.activityId && `${eXo.env.portal.context}/${eXo.env.portal.metaPortalName}/activity?id=${this.realization.activityId}`;
      } else if (this.linkExtensionMethod) {
        return this.linkExtensionMethod(this.realization);
      } else if (this.realization?.objectId?.startsWith?.('http')) {
        this.realizationLink = this.realization.objectId;
      } else {
        this.realizationLink = this.realization?.link || this.realization?.url;
      }
    },
  },
};
</script>
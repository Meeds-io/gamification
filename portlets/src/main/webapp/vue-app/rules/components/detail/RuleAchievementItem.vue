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
          <a :href="activityUrl">
            <relative-date-format
              class="text-capitalize-first-letter text-light-color text-truncate"
              :value="realization.createDate" />
          </a>
        </template>
      </user-avatar>
    </v-list-item-content>
    <v-list-item-action v-if="!$root.isAnonymous">
      <v-tooltip :disabled="$root.isMobile" bottom>
        <template #activator="{ on }">
          <div v-on="on">
            <v-btn
              :href="activityUrl"
              :disabled="!realization.activityId"
              icon>
              <v-icon
                size="16"
                class="icon-default-color">
                fas fa-eye
              </v-icon>
            </v-btn>
          </div>
        </template>
        <span>{{ realization.activityId && $t('program.winner.label.checkActivity') || noActivityLabel }}</span>
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
  data() {
    return {
      basePath: `${eXo.env.portal.context}/${eXo.env.portal.engagementSiteName}`,
    };
  },
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
    noActivityLabel() {
      return this.$t(`program.winner.label.${this.automaticRule ? 'noActivity' : 'activityDeleted'}`);
    },
    automaticRule() {
      return this.rule?.type === 'AUTOMATIC';
    },
    activityUrl() {
      return this.realization.activityId && `${this.basePath}/activity?id=${this.realization.activityId}`;
    },
  },
};
</script>
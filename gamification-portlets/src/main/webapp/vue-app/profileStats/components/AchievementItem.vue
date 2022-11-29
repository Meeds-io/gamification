<!--
This file is part of the Meeds project (https://meeds.io/).
Copyright (C) 2020 Meeds Association
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
  <v-list-item
    :href="achievementUrl"
    two-line
    class="ma-4 px-0">
    <exo-space-avatar
      v-if="isSpace"
      :space-pretty-name="remoteId"
      extra-class="me-2"
      avatar
      popover />
    <exo-user-avatar
      v-else
      :profile-id="remoteId"
      extra-class="me-2"
      avatar
      popover />
    <v-list-item-content class="py-0">
      <v-list-item-title :title="actionTitle">
        {{ actionTitle }}
      </v-list-item-title>
      <v-list-item-subtitle :title="actionDetail">
        {{ actionDetail }}
      </v-list-item-subtitle>
    </v-list-item-content>
    <v-list-item-icon class="my-auto">
      <v-avatar
        color="primary"
        size="32">
        {{ achievement.actionScore }}
      </v-avatar>
    </v-list-item-icon>
  </v-list-item>
</template>

<script>
const randomMax = 10000;

export default {
  props: {
    achievement: {
      type: Object,
      default: () => ({}),
    },
  },
  data: () => ({
    id: `avatar${parseInt(Math.random() * randomMax)
      .toString()
      .toString()}`,
    lang: eXo.env.portal.language,
    dateFormat: {
      dateStyle: 'long',
      timeStyle: 'short',
    },
  }),
  computed: {
    isSender() {
      return Number(this.identityId) === Number(eXo.env.portal.profileOwnerIdentityId);
    },
    isSpace() {
      return this.achievement.space;
    },
    avatar() {
      return this.achievement.avatarUrl || this.achievement.avatar;
    },
    fullName() {
      return this.achievement.fullname;
    },
    remoteId() {
      return this.achievement.remoteId;
    },
    identityId() {
      return this.achievement.socialId;
    },
    achievementUrl() {
      return this.achievement.objectId && this.achievement.objectId.replace('/portal/intranet/', `${eXo.env.portal.context}/${eXo.env.portal.portalName}/`);
    },
    domain() {
      return this.getLabel('exoplatform.gamification.leaderboard.domain', this.achievement.domain);
    },
    actionDetail() {
      return `${this.dateTime} - ${this.$t('exoplatform.gamification.gamificationinformation.Domain')}: ${this.domain}`;
    },
    actionTitle() {
      return this.getLabel('exoplatform.gamification.gamificationinformation.rule.title', this.achievement.actionTitle);
    },
    dateTime() {
      const dateTime = new Date(this.achievement.createdDate);
      return new window.Intl.DateTimeFormat(this.lang, this.dateFormat).format(dateTime);
    },
  },
  methods: {
    getLabel(base, key) {
      const label = `${base}.${key}`;
      const translation = this.$t(label);
      return translation === label && key || translation;
    },
  }
};
</script>

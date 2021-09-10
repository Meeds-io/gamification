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
    class="ma-4">
    <v-list-item-avatar :tile="isSpace" :class="isSpace && 'spaceAvatar'" :id="id">
      <v-img :src="avatar" />
    </v-list-item-avatar>
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
    labels() {
      return {
        join: this.$t('profile.label.join'),
        leave: this.$t('profile.label.leave'),
        members: this.$t('profile.label.members'),
      };
    },
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
  mounted() {
    if (this.identityId) {
      // TODO disable tiptip because of high CPU usage using its code
      this.initTiptip();
    }
  },
  methods: {
    getLabel(base, key) {
      const label = `${base}.${key}`;
      const translation = this.$t(label);
      return translation === label && key || translation;
    },
    initTiptip() {
      if (this.isSpace) {
        this.$nextTick(() => {
          $(`#${this.id}`).spacePopup({
            userName: eXo.env.portal.userName,
            spaceID: this.identityId,
            restURL: '/portal/rest/v1/social/spaces/{0}',
            membersRestURL: '/portal/rest/v1/social/spaces/{0}/users?returnSize=true',
            managerRestUrl: '/portal/rest/v1/social/spaces/{0}/users?role=manager&returnSize=true',
            membershipRestUrl: '/portal/rest/v1/social/spacesMemberships?space={0}&returnSize=true',
            defaultAvatarUrl: this.avatar,
            deleteMembershipRestUrl: '/portal/rest/v1/social/spacesMemberships/{0}:{1}:{2}',
            labels: this.labels,
            content: false,
            keepAlive: true,
            defaultPosition: 'left_bottom',
            maxWidth: '420px',
          });
        });
      } else {
        this.$nextTick(() => {
          $(`#${this.id}`).userPopup({
            restURL: '/portal/rest/social/people/getPeopleInfo/{0}.json',
            userId: this.remoteId,
            labels: this.labels,
            keepAlive: true,
          });
        });
      }
    },
  }
};
</script>

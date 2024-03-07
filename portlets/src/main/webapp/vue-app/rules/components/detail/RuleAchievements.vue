<!--

 This file is part of the Meeds project (https://meeds.io/).

 Copyright (C) 2020 - 2023 Meeds Association contact@meeds.io

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
  <div class="rule-realizations">
    <div class="text-sub-title font-italic mb-1">
      {{ $t('rules.participations') }}
    </div>
    <engagement-center-avatars-list
      v-if="realizationsCount"
      :avatars="users"
      :max-avatars-to-show="4"
      :avatars-count="realizationsCount"
      :size="avatarSize"
      @open-avatars-drawer="openAchievementsDrawer" />
    <div v-else class="d-flex justify-center">
      <v-avatar :size="avatarSize" class="my-auto me-2">
        <img
          :src="defaultAvatarUrl"
          class="object-fit-cover ma-auto"
          loading="lazy"
          alt=""
          role="presentation">
      </v-avatar>
      {{ $t('rules.beTheFirstToDoIt') }}
    </div>
  </div>
</template>
<script>
export default {
  props: {
    rule: {
      type: Object,
      default: null,
    },
  },
  data: () => ({
    avatarSize: 27,
    defaultAvatarUrl: `${eXo.env.portal.context}/${eXo.env.portal.rest}/v1/social/users/default-image/avatar`,
  }),
  computed: {
    realizationsCount() {
      return this.rule?.realizationsCount || 0;
    },
    users() {
      return this.rule?.realizations?.filter(realization => realization?.earner?.id)
        .map(realization => ({
          userName: realization?.earner?.remoteId,
          fullname: realization?.earner?.fullName,
          avatar: realization?.earner?.avatarUrl,
          enabled: true,
        })) || [];
    },
  },
  methods: {
    openAchievementsDrawer() {
      document.dispatchEvent(new CustomEvent('open-achievements-drawer', {detail: {
        rule: this.rule,
        backIcon: true,
      }}));
    },
  },
};
</script>

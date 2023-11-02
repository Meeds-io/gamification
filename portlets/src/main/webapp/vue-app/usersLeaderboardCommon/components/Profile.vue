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
    :id="id"
    v-on="!noAction && {
      click: () => $emit('open'),
    }"
    class="px-0"
    dense>
    <div class="me-3">
      <v-avatar
        :color="currentUser && 'tertiary' || ''"
        :class="!currentUser && 'border-color'"
        class="my-auto"
        size="32">
        {{ user.rank }}
      </v-avatar>
    </div>
    <v-list-item-avatar size="32" class="me-4 my-auto">
      <v-img
        :lazy-src="userAvatar"
        :src="userAvatar"
        transition="none"
        eager />
    </v-list-item-avatar>
    <v-list-item-content>
      <v-list-item-title>
        <user-avatar
          v-if="noAction && !$root.isAnonymous"
          :profile-id="user.remoteId"
          :name="user.fullname"
          :popover="user.remoteId"
          :avatar="false"
          :size="25"
          fullname
          extra-class="me-0 pa-0 my-0 text-truncate-2"
          popover-left-position
          offset-x />
        <span v-else class="text-color subtitle-2">
          {{ user.fullname }}
        </span>
      </v-list-item-title>
    </v-list-item-content>
    <v-list-item-action class="justify-end">
      <span class="primary--text font-weight-bold">{{ user.score }}</span>
    </v-list-item-action>
  </v-list-item>
</template>
<script>
export default {
  props: {
    user: {
      type: Object,
      default: () => null,
    },
    noAction: {
      type: Boolean,
      default: false,
    },
  },
  data: () => ({
    chartData: null,
    menu: false,
  }),
  computed: {
    id() {
      return this.user && `users-leaderboard-${this.user.identityId}` || '';
    },
    userAvatar() {
      return this.user && this.user.avatarUrl || `${eXo.env.portal.context}/${eXo.env.portal.rest}/v1/social/users/${this.user.username}/avatar`;
    },
    profileUrl() {
      return this.username && `${eXo.env.portal.context}/${eXo.env.portal.defaultPortal}/profile/${this.username}`;
    },
    username() {
      return this.user && this.user.remoteId;
    },
    currentUser() {
      return this.username === eXo.env.portal.userName;
    },
  },
};
</script>
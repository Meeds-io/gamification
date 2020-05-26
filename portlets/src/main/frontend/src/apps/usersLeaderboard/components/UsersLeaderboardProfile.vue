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
  <v-menu
    v-model="menu"
    :open-on-content-click="false"
    :disabled="skeleton"
    content-class="userLeaderboardMenu"
    max-width="250"
    open-on-hover
    offset-x
    left>
    <template v-slot:activator="{ on }">
      <v-list-item
        :id="id"
        v-on="on"
        @hover="$refs.chart.init()">
        <div class="mr-3">
          <v-avatar
            :color="!skeleton && currentUser && 'tertiary' || ''"
            size="32">
            <span :class="skeleton && 'skeleton-background skeleton-border-radius'">
              {{ skeleton && '&nbsp;&nbsp;&nbsp;' || user.rank }}
            </span>
          </v-avatar>
        </div>
        <v-list-item-avatar
          :class="skeleton && 'skeleton-background'"
          class="mr-4">
          <v-img :src="!skeleton && userAvatar || ''" />
        </v-list-item-avatar>
        <v-list-item-content>
          <v-list-item-title>
            <a
              :href="profileUrl"
              :class="skeleton && 'skeleton-background skeleton-text skeleton-text-width skeleton-text-height d-inline-block'"
              class="text-color">
              {{ !skeleton && user.fullname || '&nbsp;' }}
            </a>
          </v-list-item-title>
        </v-list-item-content>
        <v-list-item-action
          :class="skeleton && 'skeleton-background skeleton-border-radius skeleton-text skeleton-text-width skeleton-text-height d-inline-block'"
          class="mr-4">
          {{ !skeleton && user.score || '&nbsp;&nbsp;&nbsp;&nbsp;' }}
        </v-list-item-action>
      </v-list-item>
    </template>

    <users-leaderboard-chart
      ref="chart"
      :username="username"
      :domains="domains" />
  </v-menu>
</template>
<script>
export default {
  props: {
    skeleton: {
      type: Boolean,
      default: () => false,
    },
    user: {
      type: Object,
      default: () => null,
    },
    domains: {
      type: Array,
      default: () => [],
    },
  },
  data: () => ({
    chartData: null,
  }),
  computed: {
    id() {
      return this.user && `users-leaderboarditem-${this.user.socialId}` || '';
    },
    userAvatar() {
      return this.user && this.user.avatarUrl || `${eXo.env.portal.context}/${eXo.env.portal.rest}/v1/social/users/${this.user.username}/avatar`;
    },
    profileUrl() {
      return this.username && `${eXo.env.portal.context}/${eXo.env.portal.portalName}/profile/${this.username}`;
    },
    username() {
      return this.user && this.user.remoteId;
    },
    currentUser() {
      return this.username === eXo.env.portal.userName;
    },
  },
}
</script>
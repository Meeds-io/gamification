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
    content-class="userLeaderboardMenu"
    max-width="250"
    open-on-hover
    offset-x
    left>
    <template #activator="{ on }">
      <v-list-item
        :id="id"
        v-on="on"
        @hover="$refs.chart.init()">
        <div class="me-3">
          <v-avatar
            :color="currentUser && 'tertiary' || ''"
            size="32">
            {{ user.rank }}
          </v-avatar>
        </div>
        <v-list-item-avatar class="me-4">
          <v-img
            :lazy-src="userAvatar"
            :src="userAvatar"
            transition="none"
            eager />
        </v-list-item-avatar>
        <v-list-item-content>
          <v-list-item-title>
            <a :href="profileUrl" class="text-color">
              {{ user.fullname }}
            </a>
          </v-list-item-title>
        </v-list-item-content>
        <v-list-item-action class="me-4">
          {{ user.score }}
        </v-list-item-action>
      </v-list-item>
    </template>

    <users-leaderboard-chart
      ref="chart"
      :open="menu"
      :username="username"
      :domains="domains" />
  </v-menu>
</template>
<script>
export default {
  props: {
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
    menu: false,
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
};
</script>
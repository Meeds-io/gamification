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
  <div>
    <v-list-item class="pa-0">
      <v-avatar
        v-if="user.rank"
        color="tertiary"
        class="me-2"
        size="32">
        {{ user.rank }}
      </v-avatar>
      <user-avatar
        :profile-id="user.remoteId"
        :name="user.fullname"
        :avatar-url="user.avatarUrl"
        :popover="user.remoteId"
        :size="25"
        extra-class="me-0 pa-0 my-0 text-truncate-2"
        popover-left-position
        offset-x />
      <v-list-item-action class="ml-auto">
        <span>{{ user.score }}</span>
      </v-list-item-action>
    </v-list-item>
    <users-leaderboard-profile-chart
      :identity-id="user.socialId"
      :programs="programs"
      :period="period" />
  </div>
</template>

<script>
export default {
  props: {
    user: {
      type: Object,
      default: null,
    },
    programs: {
      type: Array,
      default: null,
    },
    period: {
      type: String,
      default: null,
    },
  },
  data: () => ({
    drawer: false,
    loading: false,
    hasMore: false,
  }),
  methods: {
    open(user) {
      this.user = user;
      this.$refs.drawer.open();
    },
    close() {
      this.$refs.drawer.close();
    },
  },
};
</script>
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
  <v-list>
    <users-leaderboard-profile
      v-for="user in users"
      :key="user.identityId"
      :user="user"
      @open="$refs.profileStatsDrawer.open(user, period)" />
    <template v-if="currentRank && currentRank > users.length">
      <v-divider class="ma-0" />
      <v-list-item class="disabled-background">
        <div class="me-4">
          {{ $t('exoplatform.gamification.leaderboard.rank') }}
        </div>
        <div>
          <v-avatar color="tertiary" size="32">
            {{ currentRank }}
          </v-avatar>
        </div>
      </v-list-item>
    </template>
    <users-leaderboard-profile-achievements-drawer
      ref="profileStatsDrawer"
      :go-back-button="embedded" />
  </v-list>
</template>
<script>
export default {
  props: {
    users: {
      type: Array,
      default: null,
    },
    period: {
      type: String,
      default: null,
    },
    currentRank: {
      type: Number,
      default: null,
    },
    embedded: {
      type: Boolean,
      default: false,
    },
  },
};
</script>
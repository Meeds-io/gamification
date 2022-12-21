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
  <v-tooltip bottom>
    <template #activator="{ on }">
      <div class="px-2" v-on="on">
        <v-avatar
          class="d-flex flex-column BadgeItemAvatar content-box-sizing clickable mx-auto"
          :tile="!skeleton"
          @click="openDrawer">
          <img
            :class="skeleton && 'skeleton-background no-border'"
            :src="badge.avatar">
        </v-avatar>
        <div
          :class="skeleton && 'skeleton-text skeleton-background skeleton-text-height skeleton-border-radius px-6'"
          class="d-block text-center mt-2 clickable"
          @click="openDrawer">
          {{ skeleton && '&nbsp;' || badgeLabel }}
        </div>
      </div>
    </template>
    <span>{{ badgeLabel }}</span>
  </v-tooltip>
</template>

<script>
export default {
  props: {
    badge: {
      type: Object,
      default: () => ({}),
    },
    skeleton: {
      type: Boolean,
      default: false,
    },
  },
  computed: {
    badgeLabel() {
      return this.badge?.badgeLabel;
    }
  },
  methods: {
    openDrawer() {
      this.$root.$emit('open-badge-drawer', this.badge);
    },
  },
};
</script>

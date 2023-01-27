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
  <div class="d-flex align-center">
    <div class="d-flex">
      <exo-user-avatar
        v-for="avatarToDisplay in avatarsTodisplay"
        :key="avatarToDisplay.userName"
        :profile-id="avatarToDisplay.userName"
        :size="size"
        popover
        avatar 
        extra-class="me-1" />
    </div>
    <a>
      <v-avatar
        v-if="seeMoreAvatarsToDisplay"
        class="light-black-background icon-mini-size white--text font-weight-bold"
        :size="size"
        @click="$emit('open-avatars-drawer')">
        +{{ showMoreAvatarsNumber }}
      </v-avatar>
    </a>
  </div>
</template>
<script>
export default {
  props: {
    avatars: {
      type: Array,
      default: () => [],
    },
    maxAvatarsToShow: {
      type: Number,
      default: null
    },
    avatarsCount: {
      type: Number,
      default: null
    },
    size: {
      type: Number,
      default: null
    },
  },
  computed: {
    avatarsTodisplay() {
      return this.avatars.slice(0, this.maxAvatarsToShow - 1);
    },
    seeMoreAvatarsToDisplay () {
      return this.avatarsCount >= this.maxAvatarsToShow || null;
    },
    remainingAvatarsCount() {
      return this.avatarsCount - this.maxAvatarsToShow + 1;
    },
    showMoreAvatarsNumber() {
      return this.remainingAvatarsCount > 99 ? 99 : this.remainingAvatarsCount;
    },
  },
  methods: {
    openDrawer() {
      this.$root.$emit('open-avatars-drawer', this.avatars);
    },
  }
};
</script>

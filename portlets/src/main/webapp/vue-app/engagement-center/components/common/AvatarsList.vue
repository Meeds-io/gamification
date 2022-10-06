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
  <div class="d-flex">
    <div class="d-flex">
      <exo-user-avatar
        v-for="avatarToDisplay in avatarsTodisplay"
        :key="avatarToDisplay.id"
        :profile-id="avatarToDisplay.remoteId"
        :size="25"
        popover
        avatar 
        extra-class="me-1" />
    </div>
    <v-avatar
      v-if="seeMoreAvatarsToDisplay"
      size="25"
      class="light-black-background">
      <span class="white--text font-weight-bold icon-mini-size" @click="openDrawer">+{{ showMoreAvatarsNumber }}</span>
    </v-avatar>
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
    }
  },
  computed: {
    avatarsCount () {
      return this.avatars?.length;
    },
    avatarsTodisplay() {
      return this.avatars.slice(0, this.maxAvatarsToShow - 1);
    },
    seeMoreAvatarsToDisplay () {
      return this.avatarsCount >= this.maxAvatarsToShow && this.avatars[this.maxAvatarsToShow - 1] || null;
    },
    showMoreAvatarsNumber() {
      return this.avatarsCount - this.maxAvatarsToShow + 1;
    },
  },
  methods: {
    openDrawer() {
      this.$root.$emit('open-avatars-drawer', this.avatars);
    },
  }
};
</script>

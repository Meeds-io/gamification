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
  <exo-drawer
    ref="ownersDetails"
    :go-back-button="backIcon"
    right
    @closed="close">
    <template #title>
      {{ $t('programs.label.programOwners') }}
    </template>
    <template #content>
      <exo-user-avatar
        v-for="owner in ownersToDisplay" 
        :key="owner.userName"
        :profile-id="owner.userName"
        :size="44"
        extra-class="px-4 py-3"
        bold-title
        link-style
        popover /> 
    </template>
    <template #footer>
      <v-btn
        v-if="hasMore"
        class="btn"
        block
        text
        @click="loadMore">
        {{ $t('rules.loadMore') }}
      </v-btn>
    </template>
  </exo-drawer>
</template>
<script>
export default {
  data() {
    return {
      listOwners: [],
      limit: 10,
      program: null,
      backIcon: false,
    };
  },
  computed: {
    ownersToDisplay() {
      return this.listOwners.slice(0, this.limit);
    },
    hasMore() {
      return this.ownersToDisplay.length < this.listOwners.length;
    },
  },
  created() {
    this.$root.$on('open-owners-drawer-event', this.open);
  },
  methods: {
    close() {
      this.$refs.ownersDetails.close();
    },
    open(avatars, backIcon) {
      this.listOwners = avatars;
      this.backIcon = backIcon;
      this.$refs.ownersDetails.open();
    },
    loadMore() {
      this.limit += 10;
    },
  }
};
</script>
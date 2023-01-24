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
  <v-app id="ownersDetails">
    <exo-drawer
      ref="ownersDetails"
      right
      @closed="close">
      <template slot="title">
        <div>
          <v-icon
            v-if="back"
            @click="close">
            fas fa-arrow-left
          </v-icon>
          <span class="pb-2"> {{ $t('programs.label.programOwners') }}</span>
        </div>
      </template>
      <template slot="content">
        <exo-user-avatar
          v-for="owner in listOwners" 
          :key="owner.id"
          :profile-id="owner.userName"
          :size="44"
          extra-class="px-4 py-3"
          bold-title
          link-style
          popover /> 
      </template>
    </exo-drawer>
  </v-app>
</template>

<script>

export default {
  data() {
    return {
      listOwners: [],
      program: null
    };
  },
  created() {
    this.$root.$on('open-owners-drawer', this.open);
  },
  methods: {
    close() {
      this.$refs.ownersDetails.close();
    },
    open(avatars) {
      this.listOwners = avatars;
      this.$refs.ownersDetails.open();
    },
  }
};
</script>
<!--
  This file is part of the Meeds project (https://meeds.io/).

  Copyright (C) 2020 - 2025 Meeds Association contact@meeds.io

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
  <v-list-item class="py-0 px-2">
    <v-list-item-avatar class="my-1 me-2" size="30">
      <v-img :src="avatarUrl" />
    </v-list-item-avatar>
    <v-list-item-content class="py-0">
      <v-list-item-title class="request-user-name darken-2" v-text="displayName" />
      <v-list-item-subtitle v-text="description" />
    </v-list-item-content>
    <v-list-item-action>
      <v-btn-toggle
        class="transparent"
        dark>
        <v-btn
          :loading="saving"
          text
          icon
          small
          min-width="auto"
          class="px-0 connexion-accept-btn"
          @click="replyInvitation(true)">
          <v-icon color="success" size="20">mdi-checkbox-marked-circle</v-icon>
        </v-btn>
        <v-btn
          :loading="saving"
          text
          icon
          small
          min-width="auto"
          class="px-0 connexion-refuse-btn"
          @click="replyInvitation(false)">
          <v-icon color="error" size="20">mdi-close-circle</v-icon>
        </v-btn>
      </v-btn-toggle>
    </v-list-item-action>
  </v-list-item>
</template>

<script>
export default {
  props: {
    space: {
      type: Object,
      default: () => null,
    },
    saving: {
      type: Boolean,
      default: () => false,
    },
  },
  computed: {
    avatarUrl() {
      return this.space?.avatarUrl;
    },
    displayName() {
      return this.space?.displayName;
    },
    description() {
      return this.$utils.htmlToText(this.space?.description);
    },
  },
  methods: {
    replyInvitation(accept) {
      this.$emit('replyInvitation', this.space, accept);
    }
  }
};
</script>
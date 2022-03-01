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
  <v-row user-item>
    <template>
      <v-list-item
        :class="spaceItemClass"
        :key="space.id"
        :href="url"
        class="py-0 px-2">
        <v-list-item-avatar
          class="my-1 me-2"
          size="30">
          <v-img :src="avatarUrl" />
        </v-list-item-avatar>

        <v-list-item-content class="py-0" :href="url">
          <v-list-item-title>
            <a
              class="font-weight-bold subtitle-2 request-user-name darken-2"
              :href="url">
              {{ space.displayName }}
            </a>
          </v-list-item-title>
        </v-list-item-content>
      </v-list-item>
    </template>
  </v-row>
</template>

<script>
const randomMax = 10000;
export default {
  props: {
    space: {
      type: Object,
      default: () => null,
    },
  },
  data() {
    return {
      spaceItemClass: `spaceItem${parseInt(Math.random() * randomMax)
        .toString()
        .toString()}`,
    };
  },
  computed: {
    url() {
      if (!this.space || !this.space.groupId) {
        return '#';
      }
      const uri = this.space.groupId.replace(/\//g, ':');
      return `${eXo.env.portal.context}/g/${uri}/${this.space.prettyName}`;
    },
    avatarUrl() {
      return this.space && this.space.avatarUrl || `${eXo.env.portal.context}/${eXo.env.portal.rest}/v1/social/spaces/${this.space.avatarUrl}/avatar`;
    },
  },
  mounted() {
    if (this.space.id && this.space.groupId) {
      document.addEventListener('space_membership_updated', (event) => {
        this.$emit('removeLeavedSpace', event.detail);
      });
      document.addEventListener('exo-chat-room-open-requested', () => {
        this.$emit('closeDrawer');
      });
    }
  },
};

</script>

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
    <v-tooltip :disabled="$root.isMobile" bottom>
      <template #activator="{on}">
        <div v-on="on">
          <exo-user-avatars-list
            :users="avatars"
            :icon-size="size"
            :max="maxAvatarsToShow"
            :default-length="avatarsCount"
            :compact="avatars.length > 1"
            :margin-left="avatars.length > 1 && 'ml-n5' || ''"
            clickable
            avatar-overlay-position
            retrieve-extra-information
            @open-detail="$emit('open-avatars-drawer')" />
        </div>
      </template>
      <span>{{ $t('engagementCenter.avatarsTooltip') }}</span>
    </v-tooltip>
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
      default: () => 27,
    },
  },
};
</script>

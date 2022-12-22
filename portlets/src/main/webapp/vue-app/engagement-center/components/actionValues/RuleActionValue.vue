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
  <v-tooltip bottom>
    <template #activator="{ on }">
      <div v-on="on">
        <a class="flex-nowrap flex-grow-1 d-flex text-truncate container--fluid text-truncate" :href="actionURL">
          <v-icon size="15" class="primary--text">{{ actionIcon }}</v-icon>
          <div class="ps-2 text-truncate">{{ actionLabel }}
          </div>
        </a>
      </div>
    </template>
    <span>{{ actionLabel }}</span>
  </v-tooltip>
</template>

<script>
export default {
  props: {
    actionLabel: {
      type: String,
      default: null
    },
    actionURL: {
      type: String,
      default: null
    },
    actionIcon: {
      type: String,
      default: null
    },
  },
};
</script>
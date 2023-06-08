<!--
  This file is part of the Meeds project (https://meeds.io/).
  Copyright (C) 2022 Meeds Association contact@meeds.io
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
  <span>
    <v-list
      class="pb-0 my-n1"
      subheader
      two-line>
      <v-list-item
        two-line>
        <v-list-item-avatar
          :style="programStyle"
          height="48"
          width="48"
          class="border-color rounded mx-2"
          tile>
          <v-img :src="program.avatarUrl" />
        </v-list-item-avatar>
        <v-list-item-content>
          <v-list-item-title>
            {{ program.title }}
          </v-list-item-title>
          <v-list-item-subtitle> 
            <span class="text-light-color" v-sanitized-html="$t('programs.budget', {0: `<span>${program.rulesTotalScore} ${$t('programs.details.label.points')}</span>`})"></span>
          </v-list-item-subtitle>
        </v-list-item-content>
      </v-list-item>
    </v-list>
  </span>
</template>
<script>
export default {
  props: {
    program: {
      type: Object,
      default: null,
    },
  },
  computed: {
    programStyle() {
      return this.program?.color && `border: 1px solid ${this.program.color} !important;` || '';
    },
  },
};
</script>
<!--

 This file is part of the Meeds project (https://meeds.io/).
 
 Copyright (C) 2020 - 2023 Meeds Association contact@meeds.io
 
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
  <div>
    <v-list-item
      two-line
      class="px-0"
      dense
      @click="$refs.programColorDrawer.open()">
      <v-list-item-action class="me-2">
        <v-card
          :color="color"
          height="50px"
          width="50px"
          v-on="on" />
      </v-list-item-action>
      <v-list-item-content>
        <v-list-item-title>
          {{ $t('program.form.programColorSubTitle') }}
        </v-list-item-title>
        <v-spacer class="my-1" />
        <v-list-item-subtitle class="grey--text">
          {{ color }}
        </v-list-item-subtitle>
      </v-list-item-content>
    </v-list-item>
    <engagement-center-program-color-picker-drawer
      ref="programColorDrawer"
      :value="value"
      :program="program"
      :allow-expand="allowExpand"
      @input="$emit('input', $event)" />
  </div>
</template>
<script>
export default {
  props: {
    value: {
      type: String,
      default: null,
    },
    program: {
      type: Object,
      default: null,
    },
    allowExpand: {
      type: Boolean,
      default: false,
    },
  },
  computed: {
    color() {
      return this.value || '#707070';
    },
  },
};
</script>

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
    <v-tooltip :value="title" bottom>
      <template #activator="{ on }">
        <v-btn
          :disabled="disabled"
          :outlined="outlined"
          :dark="dark"
          :value="value"
          :color="contextualColor"
          :class="`${borderClass} ${paddingClass} text-color`"
          :small="small"
          class="elevation-0"
          v-on="on">
          <slot></slot>
        </v-btn>
      </template>
      <span>{{ title }}</span>
    </v-tooltip>
  </div>
</template>
<script>
export default {
  props: {
    value: {
      type: String,
      default: null,
    },
    selectedValue: {
      type: String,
      default: null,
    },
    selectedValues: {
      type: Array,
      default: null,
    },
    color: {
      type: String,
      default: null,
    },
    selectedColor: {
      type: String,
      default: null,
    },
    small: {
      type: Boolean,
      default: false,
    },
    disabled: {
      type: Boolean,
      default: false,
    },
    title: {
      type: String,
      default: null,
    },
  },
  computed: {
    isSelected() {
      return (this.value && this.value === this.selectedValue) || (this.selectedValues?.indexOf(this.value) >= 0) || false;
    },
    contextualColor() {
      return this.isSelected && this.selectedColor || this.color || '';
    },
    dark() {
      return this.isSelected;
    },
    outlined() {
      return !this.isSelected;
    },
    paddingClass() {
      return this.small && 'px-1' || 'px-2';
    },
    borderClass() {
      return this.isSelected && `${this.contextualColor}-border-color` || 'border-color';
    },
  },
};
</script>
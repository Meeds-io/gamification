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
  <widget-wrapper
    :loading="loading"
    :action-url="actionUrl"
    :title="title"
    :subtite="subtitle"
    :height="height"
    :min-width="minWidth"
    :extra-class="extraClass">
    <template #title>
      <slot name="title"></slot>
    </template> 
    <template #default>
      <slot></slot>
    </template>
  </widget-wrapper>
</template>
<script>
export default {
  props: {
    loading: {
      type: Boolean,
      default: () => false,
    },
    title: {
      type: String,
      default: () => '',
    },
    subtitle: {
      type: String,
      default: () => '',
    },
    actionUrl: {
      type: String,
      default: () => '',
    },
    extraClass: {
      type: String,
      default: () => '',
    },
    height: {
      type: String,
      default: () => '348px',
    },
    minWidth: {
      type: String,
      default: () => '290px',
    },
  },
};
</script>

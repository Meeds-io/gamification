<!--
 This file is part of the Meeds project (https://meeds.io/).

 Copyright (C) 2020 - 2024 Meeds Association contact@meeds.io

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
  <v-list-item class="clickable mb-2" @click="openRule">
    <v-list-item-icon class="me-3 my-auto">
      <v-avatar
        class="border-color white"
        left
        dark>
        <rule-icon :rule-event="ruleEvent" />
      </v-avatar>
    </v-list-item-icon>
    <v-list-item-content>
      <v-list-item-title class="text-color body-2">
        <p
          class="ma-auto text-truncate"
          v-sanitized-html="ruleTitle"></p>
      </v-list-item-title>
    </v-list-item-content>
  </v-list-item>
</template>

<script>
export default {
  props: {
    rule: {
      type: Object,
      default: () => ({}),
    },
  },
  computed: {
    ruleId() {
      return this.rule?.id;
    },
    ruleTitle() {
      return this.rule?.title;
    },
    ruleEvent() {
      return this.rule?.event?.title;
    },
    program() {
      return this.rule?.program;
    },
  },
  methods: {
    openRule() {
      if (this.rule?.userInfo) {
        this.$root.$emit('rule-detail-drawer-by-id', this.ruleId);
      }
    },
  }
};
</script>

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
  <div class="d-flex mb-1">
    <rule-icon
      v-if="reduced || expanded"
      :size="reduced && 60 || 45"
      :rule-event="eventName"
      class="align-start mb-1" />
    <div v-else class="d-flex flex-column pe-4">
      <v-avatar
        :size="programCoverSize"
        :style="programStyle"
        class="rule-program-cover border-color primary--text"
        rounded>
        <v-img :src="programAvatarUrl" />
      </v-avatar>
      <v-avatar
        :size="programCoverSize"
        class="rule-icon border-color grey lighten-2 mt-n4 ms-auto me-n4">
        <rule-icon
          :rule-event="eventName"
          size="24" />
      </v-avatar>
    </div>
    <div class="d-flex flex-column ms-2 my-auto">
      <div class="rule-title font-weight-bold text-truncate-2 ma-0">
        {{ ruleTitle }}
      </div>
      <div v-if="reduced" class="rule-score text-subtitle">
        <v-chip
          color="#F57C00"
          class="content-box-sizing white--text"
          small>
          <span>+ {{ rule.score }}</span>
        </v-chip>
      </div>
    </div>
  </div>
</template>
<script>
export default {
  props: {
    rule: {
      type: Object,
      default: null,
    },
    expanded: {
      type: Boolean,
      default: false,
    },
    reduced: {
      type: Boolean,
      default: false,
    },
  },
  data: () => ({
    programCoverSize: 35,
  }),
  computed: {
    ruleTitle() {
      return this.rule.title;
    },
    eventName() {
      return this.rule.event?.title;
    },
    programAvatarUrl() {
      return this.rule?.program?.avatarUrl;
    },
    programStyle() {
      return this.rule?.program?.color && `border: 1px solid ${this.rule.program.color} !important;` || '';
    },
  },
};
</script>
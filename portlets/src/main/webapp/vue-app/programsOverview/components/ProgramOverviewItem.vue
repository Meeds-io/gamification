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
  <gamification-overview-widget-row
    clickable
    @open="$root.$emit('program-detail-drawer', program, goBackButton)">
    <template #icon>
      <v-list-item-avatar
        :style="programStyle"
        height="48"
        width="48"
        class="border-color rounded my-auto me-auto"
        tile>
        <v-img :src="program.avatarUrl" />
      </v-list-item-avatar>
    </template>
    <v-list-item class="px-0">
      <v-list-item-content class="py-0 my-auto">
        <v-list-item-title class="subtitle-2">
          {{ program.title }}
        </v-list-item-title>
        <v-list-item-subtitle class="d-flex flex-nowrap align-center subtitle-2">
          {{ program.activeRulesCount }} {{ $t('gamification.overview.label.actionsAvailable') }}
        </v-list-item-subtitle>
      </v-list-item-content>
      <v-list-item-action>
        <v-list-item-action-text>
          <v-chip
            color="#F57C00"
            class="content-box-sizing white--text"
            small>
            <span class="subtitle-2">+ {{ programTotalScore }}</span>
          </v-chip>
        </v-list-item-action-text>
      </v-list-item-action>
    </v-list-item>
  </gamification-overview-widget-row>
</template>
<script>
export default {
  props: {
    program: {
      type: Object,
      default: null,
    },
    goBackButton: {
      type: Boolean,
      default: false,
    },
  },
  computed: {
    programStyle() {
      return this.program?.color && `border: 1px solid ${this.program.color} !important;` || '';
    },
    programURL() {
      return `${eXo.env.portal.context}/${eXo.env.portal.engagementSiteName}/contributions/programs`;
    },
    programTotalScore() {
      return new Intl.NumberFormat(eXo.env.portal.language, {
        style: 'decimal',
        minimumFractionDigits: 0,
        maximumFractionDigits: 0,
      }).format(this.program.rulesTotalScore);
    },
  },
};
</script>
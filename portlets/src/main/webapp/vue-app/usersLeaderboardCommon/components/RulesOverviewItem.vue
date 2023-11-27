<!--
  This file is part of the Meeds project (https://meeds.io/).

  Copyright (C) 2023 Meeds Association contact@meeds.io

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
    :dense="dense"
    class="flex"
    clickable
    @open="$root.$emit('rule-detail-drawer', rule, false, goBackButton, updateUrl)">
    <template #icon>
      <v-card
        min-width="35"
        class="d-flex align-center justify-center transparent"
        flat>
        <rule-icon :rule-event="eventTitle" size="30" />
      </v-card>
    </template>
    <v-list-item class="px-0">
      <v-list-item-content class="py-0 my-auto">
        <v-list-item-title class="subtitle-2">
          {{ ruleTitle }}
        </v-list-item-title>
        <v-list-item-subtitle v-if="upcoming || ending" class="d-flex flex-nowrap align-center subtitle-2">
          <engagement-center-rule-date-info-chip
            :rule="rule"
            size="18" />
        </v-list-item-subtitle>
        <v-list-item-subtitle v-else-if="realizationsCount === 0" class="subtitle-2">
          {{ $t('gamification.overview.label.firstAnnounecement') }}
        </v-list-item-subtitle>
        <v-list-item-subtitle v-else-if="realizationsCount === 1" class="subtitle-2">
          1 {{ $t('gamification.overview.label.participation') }}
        </v-list-item-subtitle>
        <v-list-item-subtitle v-else class="subtitle-2">
          {{ realizationsCount }} {{ $t('gamification.overview.label.participations') }}
        </v-list-item-subtitle>
      </v-list-item-content>
      <v-list-item-action>
        <v-list-item-action-text>
          <v-chip
            color="#F57C00"
            class="content-box-sizing white--text"
            small>
            <span class="subtitle-2">+ {{ ruleScore }}</span>
          </v-chip>
        </v-list-item-action-text>
      </v-list-item-action>
    </v-list-item>
  </gamification-overview-widget-row>
</template>
<script>
export default {
  props: {
    rule: {
      type: Object,
      default: null,
    },
    dense: {
      type: Boolean,
      default: false,
    },
    updateUrl: {
      type: Boolean,
      default: false,
    },
    goBackButton: {
      type: Boolean,
      default: false,
    },
  },
  data: () => ({
    weekInMs: 604800000,
  }),
  computed: {
    ruleScore() {
      return new Intl.NumberFormat(eXo.env.portal.language, {
        style: 'decimal',
        minimumFractionDigits: 0,
        maximumFractionDigits: 0,
      }).format(this.rule.score);
    },
    ruleTitle() {
      return this.rule?.title;
    },
    eventTitle() {
      return this.rule?.event?.title;
    },
    realizationsCount() {
      return this.rule?.realizationsCount;
    },
    upcoming() {
      const startDate = this.rule?.startDate && new Date(this.rule?.startDate).getTime();
      return startDate && startDate > Date.now() && (startDate - Date.now()) < this.weekInMs;
    },
    ending() {
      const startDate = this.rule?.startDate && new Date(this.rule?.startDate).getTime();
      const endDate = this.rule?.endDate && new Date(this.rule?.endDate).getTime();
      return endDate
        && endDate > Date.now()
        && (!startDate || startDate > Date.now())
        && (endDate - Date.now()) < this.weekInMs;
    },
  },
};
</script>
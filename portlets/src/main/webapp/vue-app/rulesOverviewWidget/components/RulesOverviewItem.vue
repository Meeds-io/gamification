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
    @open="$root.$emit('rule-detail-drawer', rule)">
    <template #icon>
      <v-card
        min-width="35"
        class="d-flex align-center justify-center ms-4 transparent"
        flat>
        <rule-icon :rule-event="rule.event" size="30" />
      </v-card>
    </template>
    <template #content>
      <v-list-item class="ps-0">
        <v-list-item-content class="py-0 my-auto">
          <v-list-item-title>
            {{ rule.title }}
          </v-list-item-title>
          <v-list-item-subtitle v-if="upcoming || ending" class="d-flex flex-nowrap align-center">
            <engagement-center-rule-date-info-chip
              :rule="rule"
              size="18" />
          </v-list-item-subtitle>
          <v-list-item-subtitle v-else-if="rule.realizationsCount === 0">
            {{ $t('gamification.overview.label.firstAnnounecement') }}
          </v-list-item-subtitle>
          <v-list-item-subtitle v-else-if="rule.realizationsCount === 1">
            1 {{ $t('gamification.overview.label.participation') }}
          </v-list-item-subtitle>
          <v-list-item-subtitle v-else>
            {{ rule.realizationsCount }} {{ $t('gamification.overview.label.participations') }}
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
    </template>
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
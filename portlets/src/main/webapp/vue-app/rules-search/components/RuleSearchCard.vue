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
  <v-card
    class="d-flex flex-column border-radius box-shadow rule-search-card overflow-hidden"
    flat
    min-height="227">
    <v-card-text class="px-2 pt-2 pb-0">
      <div class="d-flex flex-nowrap align-center overflow-hidden">
        <rule-icon
          :size="36"
          :rule-event="rule.event"
          class="align-start me-3 flex-grow-0 flex-shrink-0" />
        <div
          v-sanitized-html="ruleTitle"
          :title="rule.title"
          class="text-truncate-2 text-start text-color flex-grow-1 flex-shrink-1 me-3"></div>
        <favorite-button
          :favorite="isFavorite"
          :id="rule.id"
          type="rule"
          type-label="rules"
          top="0"
          right="0"
          absolute
          class="align-start"
          @removed="$emit('refresh-favorite')" />
      </div>
    </v-card-text>
    <div class="d-flex flex-column flex-grow-1 flex-shrink-1 full-width mx-auto px-3 pt-2 overflow-hidden">
      <div
        v-sanitized-html="ruleDescription"
        class="text-wrap text-break caption text-truncate-3">
      </div>
      <div class="d-flex flex-nowrap align-center overflow-hidden mt-auto pb-2 pt-1">
        <v-chip
          color="#F57C00"
          class="content-box-sizing white--text me-2"
          small>
          <span class="subtitle-2">+ {{ rule.score }}</span>
        </v-chip>
        <div class="d-flex flex-nowrap justify-center">
          <v-tooltip top>
            <template #activator="{on}">
              <div
                v-sanitized-html="recurrenceTitle"
                class="text-wrap font-weight-bold"
                v-on="on">
              </div>
            </template>
            <span>{{ recurrenceTooltip }}</span>
          </v-tooltip>
        </div>
      </div>
    </div>
    <v-list class="light-grey-background flex-grow-0 border-top-color no-border-radius pa-0">
      <v-list-item :href="ruleLink" class="px-0 pt-1 pb-2">
        <v-list-item-avatar
          class="mx-3 my-auto"
          tile
          rounded>
          <v-img
            src="/gamification-portlets/skin/images/contributionCenterIcon.webp"
            contain />
        </v-list-item-avatar>
        <v-list-item-content>
          <v-list-item-title :title="activityReactions">
            {{ realizationsCount }} {{ $t('gamification.overview.label.participations') }}
          </v-list-item-title>
          <v-list-item-subtitle>
            {{ programTitle }}
          </v-list-item-subtitle>
        </v-list-item-content>
      </v-list-item>
    </v-list>
    <engagement-center-rule-extensions />
  </v-card>
</template>

<script>
export default {
  props: {
    term: {
      type: String,
      default: null,
    },
    result: {
      type: Object,
      default: null,
    },
  },
  computed: {
    isComment() {
      return this.result && this.result.comment;
    },
    rule() {
      return this.result || {};
    },
    ruleTitle() {
      return this.rule.titleExcerpt || this.rule.title || '';
    },
    ruleDescription() {
      return this.rule.descriptionExcerpt || this.rule.description || '';
    },
    ruleLink() {
      return `/${eXo.env.portal.containerName}/${eXo.env.portal.portalName}/contributions/actions/${this.rule.id}`;
    },
    isFavorite() {
      return this.rule.favorite;
    },
    program() {
      return this.rule.program;
    },
    programTitle() {
      return this.program?.title || '';
    },
    realizationsCount() {
      return this.rule.realizationsCount || 0;
    },
    recurrenceTitle() {
      switch (this.rule?.recurrence) {
      case 'ONCE':
        return this.$t('gamification.achivementRecurrence.onceTitle', {0: '<strong>', 1: '</strong>'});
      case 'DAILY':
        return this.$t('gamification.achivementRecurrence.dailyTitle', {0: '<strong>', 1: '</strong>'});
      case 'WEEKLY':
        return this.$t('gamification.achivementRecurrence.weeklyTitle', {0: '<strong>', 1: '</strong>'});
      case 'MONTHLY':
        return this.$t('gamification.achivementRecurrence.monthlyTitle', {0: '<strong>', 1: '</strong>'});
      default:
        return null;
      }
    },
    recurrenceTooltip() {
      switch (this.rule?.recurrence) {
      case 'ONCE':
        return this.$t('gamification.achivementRecurrence.onceTooltip', {0: '<strong>', 1: '</strong>'});
      case 'DAILY':
        return this.$t('gamification.achivementRecurrence.dailyTooltip', {0: '<strong>', 1: '</strong>'});
      case 'WEEKLY':
        return this.$t('gamification.achivementRecurrence.weeklyTooltip', {0: '<strong>', 1: '</strong>'});
      case 'MONTHLY':
        return this.$t('gamification.achivementRecurrence.monthlyTooltip', {0: '<strong>', 1: '</strong>'});
      default:
        return null;
      }
    },
  },
};
</script>

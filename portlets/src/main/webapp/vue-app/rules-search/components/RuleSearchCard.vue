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
  <v-hover v-slot="{ hover }">
    <v-card
      flat
      class="pa-0"
      @click="openRuleDetailsDrawer()">
      <v-list class="pa-0" :class="hover && 'light-grey-background-color no-border-radius' || ''">
        <v-list-item>
          <v-list-item-icon class="ms-n1 me-2">
            <v-icon size="32" class="icon-default-color mt-2">fas fa-trophy</v-icon>
          </v-list-item-icon>

          <v-list-item-content>
            <v-list-item-title class="d-flex flex-row full-width align-center">
              <p
                :title="ruleTitle"
                class="flex-grow-1 title pt-1 mb-0 ps-0 my-auto align-center text-start text-truncate"
                v-sanitized-html="ruleTitle"></p>
              <div v-show="hover || isMobile" class="ml-2 pt-1">
                <span  class="d-flex d-inline-flex">
                <v-btn icon small @click="openRuleDetailsDrawer(true)">
                  <v-icon class="icon-default-color" size="16">
                    fa fa-bullhorn
                  </v-icon>
                </v-btn>
                <favorite-button
                    :favorite="isFavorite"
                    :id="rule.id"
                    type="rule"
                    type-label="rules"
                    class="ps-1"
                    @removed="$emit('refresh-favorite')" />
              </span>
              </div>
            </v-list-item-title>

            <v-list-item-subtitle class="d-flex flex-column">
              <span class="d-flex flex-row align-center mx-auto full-width">
                <v-avatar
                  v-if="!isMobile"
                  size="24"
                  rounded
                  class="rule-program-cover border-color primary--text">
                  <img
                    class="object-fit-cover ma-auto"
                    :style="programStyle"
                    :src="programAvatarUrl">
                </v-avatar>
                <span v-if="!isMobile" class="text-subtitle px-2">{{ programTitle }}</span>
                <v-icon
                  v-if="!isMobile"
                  size="3"
                  class="icon-default-color mx-3">
                  fas fa-circle
                </v-icon>
                <v-chip
                  color="tertiary"
                  class="content-box-sizing white--text me-2"
                  justify-center
                  x-small>
                  <span>+ {{ rule.score }}</span>
                </v-chip>
                <v-icon
                  v-if="!isMobile && recurrenceTitle"
                  size="3"
                  class="icon-default-color mx-3">
                  fas fa-circle
                </v-icon>
                <v-tooltip top v-if="!isMobile && recurrenceTitle">
                  <template #activator="{on}">
                    <div
                      v-sanitized-html="recurrenceTitle"
                      class="text-subtitle"
                      v-on="on">
                    </div>
                    <v-icon size="12" class="pe-1">
                      fas fa-redo
                    </v-icon>
                  </template>
                  <span>{{ recurrenceTooltip }}</span>
                </v-tooltip>
              </span>
              <div
                class="pt-2 text-wrap text-body text-break"
                :title="ruleDescription"
                :class="{
                  'text-truncate-2': isMobile,
                  'text-truncate-3': !isMobile,
                }"
                v-sanitized-html="ruleDescription"></div>
            </v-list-item-subtitle>
          </v-list-item-content>
        </v-list-item>
      </v-list>
      <div v-if="drawer">
        <engagement-center-rule-detail-drawer ref="searchResultRuleDetailsDrawer" @closed="close" />
        <engagement-center-rule-extensions />
      </div>
    </v-card>
  </v-hover>
</template>

<script>
export default {
  data: () => ({
    drawer: false,
  }),
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
    programAvatarUrl() {
      return this.result?.program?.avatarUrl;
    },
    rule() {
      return this.result || {};
    },
    ruleTitle() {
      return this.rule.titleExcerpt || this.rule.title || '';
    },
    ruleDescription() {
      return this.$utils.htmlToText(this.rule.descriptionExcerpt || this.rule.description || '');
    },
    isFavorite() {
      return this.rule.favorite;
    },
    program() {
      return this.rule?.program;
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
    isMobile() {
      return this.$vuetify?.breakpoint?.smAndDown;
    },
    programStyle() {
      return this.rule?.program?.color && `border: 1px solid ${this.rule.program.color} !important;` || '';
    },
  },
  methods: {
    async openRuleDetailsDrawer(displayAnnouncementForm) {
      this.drawer = true;
      await this.$nextTick();
      this.$refs.searchResultRuleDetailsDrawer.open(this.rule, displayAnnouncementForm || false);
    },
    async close() {
      this.$refs.searchResultRuleDetailsDrawer.close();
      await this.$nextTick();
      this.drawer = false;
    },
  }
};
</script>

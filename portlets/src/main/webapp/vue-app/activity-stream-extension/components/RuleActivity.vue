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
    v-on="isActivityOfRule && {
      click: () => openRule()
    }"
    class="d-flex my-4"
    flat>
    <v-card
      class="d-flex flex-column flex-grow-0 me-4 flex-shrink-0 border-box-sizing"
      min-width="90"
      max-width="90"
      min-height="90"
      max-height="90"
      color="transparent"
      flat>
      <v-card
        width="100%"
        height="100%"
        class="position-relative"
        color="transparent"
        flat>
        <v-avatar
          :size="programCoverSize"
          :style="programStyle"
          class="rule-program-cover border-color primary--text"
          rounded>
          <v-img :src="programAvatarUrl" />
        </v-avatar>
        <v-btn
          :disabled="!isActivityOfRule"
          :width="ruleIconSize"
          :max-width="ruleIconSize"
          :height="ruleIconSize"
          :max-height="ruleIconSize"
          :class="$vuetify.rtl && 'l-0' || 'r-0'"
          class="rule-icon border-color grey lighten-2 elevation-2 ms-auto mt-auto position-absolute b-0"
          icon>
          <rule-icon :rule-event="ruleEvent" :size="ruleIconSize - 20" />
        </v-btn>
      </v-card>
    </v-card>
    <div class="flex-grow-1 flex-shrink-1 overflow-hidden">
      <v-card
        color="transparent"
        flat>
        <div class="text-truncate font-weight-bold text-color text-wrap text-break mb-1">
          {{ ruleTitle }}
        </div>
        <div class="text-truncate-2 text-light-color caption text-wrap text-break mb-4 rich-editor-content reset-style-box">
          {{ ruleDescription }}
        </div>
        <v-chip
          v-if="ruleScore"
          color="#F57C00"
          class="content-box-sizing white--text"
          small>
          <span class="subtitle-2">+ {{ ruleScore }}</span>
        </v-chip>
      </v-card>
    </div>
  </v-card>
</template>
<script>
export default {
  props: {
    activity: {
      type: Object,
      default: null,
    },
    isActivityDetail: {
      type: Boolean,
      default: false,
    },
    programCoverSize: {
      type: Number,
      default: () => 70,
    },
    ruleIconSize: {
      type: Number,
      default: () => 45,
    },
  },
  computed: {
    ruleId() {
      return Number(this.activity.templateParams.ruleId);
    },
    activityId() {
      return this.activity.id;
    },
    rule() {
      return this.activity.rule;
    },
    isActivityOfRule() {
      return parseInt(this.activity.id) === this.rule?.activityId && !this.rule?.deleted && this.rule?.enabled;
    },
    ruleTitle() {
      return this.$utils.htmlToText(this.rule?.title || this.activity.templateParams.ruleTitle);
    },
    ruleDescription() {
      return this.rule?.description && this.$utils.htmlToText(this.rule.description);
    },
    ruleScore() {
      return this.rule?.score || this.activity.templateParams.ruleScore;
    },
    ruleEvent() {
      return this.rule?.event;
    },
    program() {
      return this.rule?.program;
    },
    programAvatarUrl() {
      return this.program?.avatarUrl || `${eXo.env.portal.context}/${eXo.env.portal.rest}/gamification/programs/default-avatar/avatar?lastModified=1687151006488`;
    },
    programStyle() {
      return this.program?.color && `border: 1px solid ${this.rule?.program.color} !important;` || '';
    },
  },
  created() {
    this.$root.$on('rule-updated', this.updateRule);
    this.$root.$on('rule-deleted', this.deleteRule);
    this.$root.$on('announcement-added', this.refreshComments);
  },
  beforeDestroy() {
    this.$root.$off('rule-updated', this.updateRule);
    this.$root.$off('rule-deleted', this.deleteRule);
    this.$root.$off('announcement-added', this.refreshComments);
  },
  methods: {
    openRule() {
      if (this.rule?.userInfo) {
        this.$root.$emit('rule-detail-drawer-by-id', this.ruleId);
      }
    },
    refreshComments(event) {
      if (this.ruleId === event?.detail?.challengeId) {
        this.$activityService.getActivityById(`comment${event.detail.announcement.activityId}`)
          .then(comment => this.$root.$emit('activity-comment-created', comment));
      }
    },
    updateRule(rule) {
      if (this.ruleId === rule?.id) {
        this.$ruleService.getRuleById(this.ruleId, {
          lang: eXo.env.portal.language
        })
          .then(rule => this.$set(this.activity, 'rule', rule))
          .finally(() => document.dispatchEvent(new CustomEvent('activity-updated', {
            detail: this.activityId
          })));
      }
    },
    deleteRule(rule) {
      if (this.ruleId === rule?.id) {
        document.dispatchEvent(new CustomEvent('activity-deleted', {
          detail: this.activityId
        }));
      }
    },
  },
};
</script>
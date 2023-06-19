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
  <div class="d-flex my-4">
    <v-card
      class="d-flex flex-column flex-grow-0 me-4 flex-shrink-0 border-box-sizing"
      min-width="90"
      max-width="90"
      min-height="90"
      max-height="90"
      flat>
      <v-card
        width="100%"
        height="100%"
        class="position-relative"
        flat>
        <v-card flat @click="openRule">
          <v-avatar
            :size="programCoverSize"
            :style="programStyle"
            class="rule-program-cover border-color primary--text"
            rounded>
            <v-img :src="programAvatarUrl" />
          </v-avatar>
        </v-card>
        <v-btn
          :width="ruleIconSize"
          :max-width="ruleIconSize"
          :height="ruleIconSize"
          :max-height="ruleIconSize"
          :class="$vuetify.rtl && 'l-0' || 'r-0'"
          class="rule-icon border-color grey lighten-2 elevation-2 ms-auto mt-auto position-absolute b-0"
          icon
          @click="openRule">
          <v-icon :size="ruleIconSize - 20" class="rule-icon primary--text">
            {{ ruleIcon }}
          </v-icon>
        </v-btn>
      </v-card>
    </v-card>
    <div class="flex-grow-1 flex-shrink-1">
      <v-card
        flat
        @click="openRule">
        <div class="text-truncate font-weight-bold text-color text-wrap text-break mb-2">
          {{ ruleTitle }}
        </div>
        <div class="text-truncate-2 text-light-color caption text-wrap text-break mb-2 rich-editor-content reset-style-box">
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
  </div>
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
    ruleTitle() {
      const key = `exoplatform.gamification.gamificationinformation.rule.title.${this.rule.title}`;
      if (this.$te(key)) {
        return this.$t(key);
      } else {
        return this.rule.title;
      }
    },
    ruleDescription() {
      const key = `exoplatform.gamification.gamificationinformation.rule.description.${this.rule.title}`;
      if (this.$te(key)) {
        return this.$t(key);
      } else {
        return this.$utils.htmlToText(this.rule.description);
      }
    },
    ruleScore() {
      return this.rule?.score;
    },
    ruleIcon() {
      return this.rule?.type === 'AUTOMATIC' && this.extension?.icon || 'fas fa-trophy';
    },
    actionValueExtensions() {
      return this.$root.actionValueExtensions;
    },
    program() {
      return this.rule?.program;
    },
    programAvatarUrl() {
      return this.program?.avatarUrl || `${eXo.env.portal.context}/${eXo.env.portal.rest}/gamification/programs/default-avatar/avatar?lastModified=1687151006488`;
    },
    programStyle() {
      return this.program?.color && `border: 1px solid ${this.rule.program.color} !important;` || '';
    },
    extension() {
      if (this.actionValueExtensions) {
        return Object.values(this.actionValueExtensions)
          .sort((ext1, ext2) => (ext1.rank || 0) - (ext2.rank || 0))
          .find(extension => extension.match && extension.match(this.rule.event)) || null;
      }
      return null;
    },
  },
  created() {
    this.$root.$on('rule-updated', this.updateRule);
    this.$root.$on('rule-deleted', this.deleteRule);
  },
  beforeDestroy() {
    this.$root.$off('rule-updated', this.updateRule);
    this.$root.$off('rule-deleted', this.deleteRule);
  },
  methods: {
    openRule() {
      if (this.rule?.userInfo) {
        this.$root.$emit('rule-detail-drawer-by-id', this.ruleId);
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
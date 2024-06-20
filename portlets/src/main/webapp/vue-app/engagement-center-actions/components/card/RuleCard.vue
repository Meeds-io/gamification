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
      :style="programStyle"
      class="mx-auto d-flex flex-column rule-card-info mx-2 overflow-hidden"
      height="230"
      max-height="230"
      outlined
      hover
      @click="$root.$emit('rule-detail-drawer', ruleWithProgram)">
      <div v-if="!isValid">
        <div v-show="hover" class="d-flex position-absolute full-width z-index-drawer">
          <div class="ms-auto mb-auto mt-4 me-4">
            <div
              v-if="isProgramMember"
              v-show="hover"
              :class="hover && 'd-inline-flex'"
              class="my-auto">
              <rule-favorite-button
                :rule-id="rule.id"
                :space-id="rule.spaceId"
                :favorite.sync="rule.favorite"
                type="rule"
                type-label="rules" />
            </div>
            <engagement-center-rule-menu
              v-if="canEdit && hover"
              :rule="ruleWithProgram"
              dark
              small />
          </div>
        </div>
        <engagement-center-card-mask class="z-index-one pa-2">
          <engagement-center-rule-card-mask-content
            v-if="!isEnabled"
            :title="rule.title"
            :text="rule.enabled && $t('actions.label.disabledProgram') || $t('actions.label.disabled')"
            class="rule-card-mask-disabled" />
          <engagement-center-rule-card-mask-audience
            v-else-if="!isValidAudience"
            :rule="ruleWithProgram" />
          <engagement-center-rule-card-mask-whitelist
            v-else-if="!isValidWhitelist"
            :rule="ruleWithProgram" />
          <engagement-center-rule-card-mask-remaining-dates
            v-else-if="!isValidDates"
            :rule="ruleWithProgram" />
          <engagement-center-rule-card-mask-recurrence
            v-else-if="!isValidRecurrence"
            :rule="ruleWithProgram" />
          <engagement-center-rule-card-mask-prequisite-rules
            v-else-if="!isValidPrerequities"
            :rule="ruleWithProgram" />
          <engagement-center-rule-card-mask-connector
            v-else-if="isRequireConnectorConnection"
            :extension="connectorValueExtension"
            :title="rule.title" />
        </engagement-center-card-mask>
      </div>
      <v-card
        :class="!isValid && 'filter-blur-3'"
        class="d-flex flex-column full-height"
        flat>
        <v-card-title class="rule-card-title text-body font-weight-bold d-flex flex-nowrap pb-4 text-break">
          <div :title="title" class="d-flex align-center flex-grow-1 text-truncate">
            <rule-icon :rule-event="ruleEvent" class="me-2" />
            <span class="text-truncate pt-2px">
              {{ title }}
            </span>
          </div>
          <div v-if="isValid" class="flex-grow-0 d-flex align-center">
            <div
              v-show="hover"
              :class="hover && 'd-inline-flex'"
              class="my-auto">
              <rule-favorite-button
                v-if="isProgramMember"
                :rule-id="rule.id"
                :space-id="rule.spaceId"
                :favorite.sync="rule.favorite"
                type="rule"
                type-label="rules" />
            </div>
            <engagement-center-rule-menu
              v-if="canEdit && hover"
              :rule="ruleWithProgram"
              small />
          </div>
        </v-card-title>
        <v-card-text
          v-sanitized-html-no-embed="description"
          :class="rule.recurrence && 'text-truncate-2' || 'text-truncate-3'"
          class="rule-card-description rich-editor-content pb-0" />
        <template v-if="isEnabled">
          <v-spacer />
          <v-card-text class="d-flex align-center pt-0">
            <engagement-center-rule-card-points
              :rule="ruleWithProgram"
              :target-item-label="targetItemLabel" />
            <engagement-center-rule-card-recurrence
              v-if="rule.recurrence" 
              :rule="ruleWithProgram"
              class="ms-2" />
            <v-spacer />
            <engagement-center-rule-card-remaining-dates
              :rule="ruleWithProgram" />
          </v-card-text>
        </template>
      </v-card>
    </v-card>
  </v-hover>
</template>
<script>
export default {
  props: {
    rule: {
      type: Object,
      default: null
    },
    program: {
      type: Object,
      default: null
    },
    noValidation: {
      type: Boolean,
      default: false,
    },
  },
  data: () => ({
    showMenu: false,
    connectorValueExtensions: []
  }),
  computed: {
    ruleProgram() {
      return this.rule?.program || this.program;
    },
    ruleEvent() {
      return this.rule?.event?.title;
    },
    ruleWithProgram() {
      return this.rule?.program && this.rule || Object.assign(this.rule, {
        program: this.ruleProgram,
      });
    },
    canEdit() {
      return this.rule?.userInfo?.canEdit;
    },
    realizationsCount() {
      return this.rule?.realizationsCount || 0;
    },
    title() {
      return this.rule?.title;
    },
    description() {
      return this.rule?.description;
    },
    isValid() {
      return (this.noValidation || this.rule?.userInfo?.context?.valid) && !this.isRequireConnectorConnection;
    },
    isValidDates() {
      return this.rule?.userInfo?.context?.validDates;
    },
    isEnabled() {
      return this.rule?.enabled && !this.rule?.deleted && this.ruleProgram?.enabled && !this.ruleProgram?.deleted;
    },
    isValidPrerequities() {
      const prerequisitesStatus = this.rule?.userInfo?.context?.validPrerequisites;
      return !prerequisitesStatus || Object.values(prerequisitesStatus).filter(v => !v).length === 0;
    },
    isValidRecurrence() {
      return this.rule?.userInfo?.context?.validRecurrence;
    },
    isValidAudience() {
      return this.rule?.userInfo?.context?.validAudience;
    },
    isValidWhitelist() {
      return this.rule?.userInfo?.context?.validWhitelist;
    },
    eventType() {
      return this.rule?.event?.type;
    },
    connectorValueExtension() {
      if (this.connectorValueExtensions) {
        return this.rule?.event?.type
            && Object.values(this.connectorValueExtensions)
              .find(extension => extension?.name === this.eventType);
      }
      return null;
    },
    isRequireConnectorConnection() {
      return this.ruleEvent && this.connectorValueExtension?.identifier === null;
    },
    programStyle() {
      return this.ruleProgram?.color && `border: 1px solid ${this.ruleProgram.color} !important;` || '';
    },
    isProgramMember() {
      return this.rule?.userInfo?.member;
    },
    actionValueExtension() {
      return this.ruleEvent
          && Object.values(this.$root.actionValueExtensions)
            .sort((ext1, ext2) => (ext1.rank || 0) - (ext2.rank || 0))
            .find(extension => extension?.match?.(this.ruleEvent))
          || null;
    },
    targetItemLabel() {
      return this.actionValueExtension?.targetItemLabel;
    },
  },
  created() {
    // Workaround to fix closing menu when clicking outside
    $(document).mousedown(() => {
      if (this.showMenu) {
        window.setTimeout(() => {
          this.showMenu = false;
        }, 200);
      }
    });
    this.$root.$on('connector-value-extensions-updated', (connectorValueExtensions) => {
      this.connectorValueExtensions = connectorValueExtensions;
    });
    this.connectorValueExtensions = this.$root.connectorValueExtensions;
  },
};
</script>

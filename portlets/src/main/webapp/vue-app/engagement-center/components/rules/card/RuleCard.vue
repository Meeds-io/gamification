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
      id="ruleCard"
      class="mx-auto d-flex flex-column rule-card-info mx-2"
      :width="challengeCardWidth"
      height="230"
      max-height="230"
      outlined
      hover
      @click="$root.$emit('rule-detail-drawer', rule)">
      <div v-if="!isValid">
        <div v-if="canEdit && hover" class="d-flex position-absolute full-width z-index-modal">
          <div class="ms-auto mb-auto mt-4 me-4">
            <engagement-center-rule-card-menu
              :rule="rule"
              dark />
          </div>
        </div>
        <engagement-center-rule-card-mask>
          <div v-if="!rule.enabled" class="title font-weight-text">
            {{ $t('challenges.label.disabled') }}
          </div>
          <engagement-center-rule-card-mask-remaining-dates
            v-else-if="!isValidDates"
            :rule="rule" />
          <engagement-center-rule-card-mask-recurrence
            v-else-if="!isValidRecurrence"
            :rule="rule" />
          <engagement-center-rule-card-mask-prequisite-rules
            v-else-if="!isValidPrerequities"
            :rule="rule" />
        </engagement-center-rule-card-mask>
      </div>
      <v-card-title class="text-truncate d-flex">
        <div class="flex-grow-1">
          {{ title }}
        </div>
        <div v-if="canEdit && hover && isValid" class="flex-grow-0">
          <engagement-center-rule-card-menu :rule="rule" />
        </div>
      </v-card-title>
      <v-card-text
        v-sanitized-html="description"
        class="text-truncate-2" />
      <template v-if="rule.enabled">
        <v-spacer />
        <v-card-text v-if="rule.recurrence">
          <engagement-center-rule-card-recurrence :rule="rule" />
        </v-card-text>
        <v-spacer />
        <v-card-text class="d-flex align-center">
          <engagement-center-rule-card-points :rule="rule" />
          <v-spacer />
          <engagement-center-rule-card-remaining-dates :rule="rule" />
        </v-card-text>
      </template>
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
  },
  data: () => ({
    showMenu: false,
  }),
  computed: {
    canEdit() {
      return this.rule?.userInfo?.canEdit;
    },
    announcementsCount() {
      return this.rule?.announcementsCount || 0;
    },
    title() {
      return this.rule?.title;
    },
    description() {
      return this.rule?.description;
    },
    isValid() {
      console.warn('this.rule?.userInfo?.context?.valid', this.rule.title, this.rule?.userInfo?.context?.valid, this.rule);
      return this.rule?.userInfo?.context?.valid;
    },
    isValidDates() {
      return this.rule?.userInfo?.context?.validDates;
    },
    isValidPrerequities() {
      const prerequisitesStatus = this.rule?.userInfo?.context?.validPrerequisites;
      return !prerequisitesStatus || Object.values(prerequisitesStatus).filter(v => !v).length === 0;
    },
    isValidRecurrence() {
      return this.rule?.userInfo?.context?.validRecurrence;
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
  },
  methods: {
    editChallenge(event) {
      if (event) {
        event.preventDefault();
        event.stopPropagation();
      }
      this.$ruleService.getRuleById(this.challenge?.id)
        .then(rule => this.$root.$emit('rule-form-drawer', rule));
    },
    deleteRule(event) {
      if (event) {
        event.preventDefault();
        event.stopPropagation();
      }
      this.$root.$emit('rule-delete-confirm', this.challenge);
    },
  }
};
</script>

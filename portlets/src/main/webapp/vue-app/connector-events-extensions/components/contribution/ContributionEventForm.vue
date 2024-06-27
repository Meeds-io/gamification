<!--
This file is part of the Meeds project (https://meeds.io/).

Copyright (C) 2020 - 2024 Meeds Association contact@meeds.io

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
  <div>
    <v-card-text class="px-0 pb-0">
      {{ $t('gamification.event.detail.action.label') }}
    </v-card-text>
    <v-radio-group v-model="rule" @change="changeSelection">
      <v-radio
        value="ANY"
        :label="$t('gamification.event.detail.anyAction.label')" />
      <v-radio value="ANY_IN_PROGRAM" :label="$t('gamification.event.detail.onlyInProgram.label')" />
      <gamification-program-suggester
        v-if="rule === 'ANY_IN_PROGRAM'"
        ref="projectsSuggester"
        v-model="selectedProgram"
        :labels="programSuggesterLabels"
        multiple />
      <v-radio value="IN_ACTIONS" :label="$t('gamification.event.detail.onlyFewActions.label')" />
      <rule-suggester
        v-if="rule === 'IN_ACTIONS'"
        ref="ruleAutoComplete"
        v-model="selectedRule"
        :labels="ruleSuggesterLabels"
        multiple />
    </v-radio-group>
  </div>
</template>

<script>

export default {
  props: {
    properties: {
      type: Object,
      default: null
    },
    trigger: {
      type: String,
      default: null
    },
  },
  data() {
    return {
      anySpace: true,
      selectedProgram: [],
      selectedRule: [],
      rule: 'ANY',
      activityLink: null,
      isValidLink: true,
      startTypingKeywordTimeout: 0,
      startSearchAfterInMilliseconds: 300,
      endTypingKeywordTimeout: 50,
    };
  },
  computed: {
    programSuggesterLabels() {
      return {
        searchPlaceholder: this.$t('programs.label.spaces.noDataLabel'),
        placeholder: this.$t('programs.label.spaces.placeholder'),
      };
    },
    ruleSuggesterLabels() {
      return {
        searchPlaceholder: this.$t('realization.label.filter.action.searchPlaceholder'),
        placeholder: this.$t('realization.label.filter.action.placeholder'),
        noDataLabel: this.$t('realization.label.filter.action.noDataLabel'),
      };
    },
  },
  watch: {
    selectedProgram() {
      if (this.selectedProgram?.length) {
        const eventProperties = {
          programIds: this.selectedProgram.map(program => program.id).toString(),
        };
        document.dispatchEvent(new CustomEvent('event-form-filled', {detail: eventProperties}));
      } else if (this.rule === 'ANY_IN_PROGRAM') {
        document.dispatchEvent(new CustomEvent('event-form-unfilled'));
      }
    },
    selectedRule() {
      if (this.selectedRule?.length) {
        const eventProperties = {
          ruleIds: this.selectedRule.map(rule => rule.id).toString(),
        };
        document.dispatchEvent(new CustomEvent('event-form-filled', {detail: eventProperties}));
      } else if (this.rule === 'IN_ACTIONS') {
        document.dispatchEvent(new CustomEvent('event-form-unfilled'));
      }
    },
    trigger() {
      this.rule = 'ANY';
      document.dispatchEvent(new CustomEvent('event-form-filled', {detail: {rule: 'any'}}));
    },
  },
  created() {
    if (this.properties?.programIds) {
      this.rule = 'ANY_IN_PROGRAM';
      this.properties?.programIds.split(',').forEach(programId => {
        this.$programService.getProgramById(programId)
          .then((program) => {
            this.selectedProgram.push(program);
          });
      });
    } else if (this.properties?.ruleIds) {
      this.rule = 'IN_ACTIONS';
      this.properties?.ruleIds.split(',').forEach(ruleId => {
        this.$ruleService.getRuleById(ruleId)
          .then((rule) => {
            this.selectedRule.push(rule);
          });
      });
    } else {
      document.dispatchEvent(new CustomEvent('event-form-filled', {detail: {rule: 'any'}}));
    }
  },
  methods: {
    changeSelection() {
      this.selectedProgram = null;
      this.selectedRule = null;
      if (this.rule === 'ANY') {
        document.dispatchEvent(new CustomEvent('event-form-filled', {detail: {rule: 'any'}}));
      } else {
        document.dispatchEvent(new CustomEvent('event-form-unfilled'));
      }
    },
  }
};
</script>
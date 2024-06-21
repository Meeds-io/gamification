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
    <template v-if="programs.length">
      <div class="subtitle-1 font-weight-bold mb-2">
        {{ $t('gamification.event.display.goThere') }}
      </div>
      <v-progress-linear
        v-if="!initialized"
        indeterminate
        height="2"
        color="primary" />
      <meeds-contribution-event-program-item
        v-for="program in programs"
        :key="program.id"
        :program="program" />
    </template>
    <template v-if="rules.length">
      <div class="subtitle-1 font-weight-bold mb-2">
        {{ $t('gamification.event.display.goThere') }}
      </div>
      <v-progress-linear
        v-if="!initialized"
        indeterminate
        height="2"
        color="primary" />
      <meeds-contribution-event-rule-item
        v-for="rule in rules"
        :key="rule.id"
        :rule="rule" />
    </template>
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
      programs: [],
      rules: [],
      initialized: false
    };
  },
  computed: {
    programIds() {
      return this.properties?.programIds;
    },
    ruleIds() {
      return this.properties?.ruleIds;
    },
  },
  created() {
    if (this.programIds) {
      this.retrievePrograms();
    } else if (this.ruleIds) {
      this.retrieveRules();
    }
  },
  methods: {
    retrievePrograms() {
      this.programIds.split(',').forEach((programId, index) => {
        this.$programService.getProgramById(programId)
          .then((program) => {
            this.programs.push(program);
          }).finally(() => {
            if (index === this.programIds.split(',').length - 1) {
              this.initialized = true;
            }
          });
      });
    },
    retrieveRules() {
      this.ruleIds.split(',').forEach((ruleId, index) => {
        this.$ruleService.getRuleById(ruleId)
          .then((rule) => {
            this.rules.push(rule);
          }).finally(() => {
            if (index === this.ruleIds.split(',').length - 1) {
              this.initialized = true;
            }
          });
      });
    },
  }
};
</script>

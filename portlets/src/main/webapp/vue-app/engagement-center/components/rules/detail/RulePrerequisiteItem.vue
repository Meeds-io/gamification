<!--

 This file is part of the Meeds project (https://meeds.io/).

 Copyright (C) 2020 - 2023 Meeds Association contact@meeds.io

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
  <v-tooltip bottom>
    <template #activator="{ on, attrs }">
      <v-btn
        :loading="loading"
        :class="prerequisiteRuleValid && 'btn' || 'btn-primary'"
        class="mx-auto mb-4"
        v-bind="attrs"
        v-on="on"
        @click="openRule">
        <v-card
          max-width="250"
          color="transparent"
          class="text-truncate"
          flat>
          {{ buttonLabel }}
        </v-card>
      </v-btn>
    </template>
    <span>{{ buttonLabel }}</span>
  </v-tooltip>
</template>
<script>
export default {
  props: {
    rule: {
      type: Object,
      default: null,
    },
    prerequisiteRule: {
      type: Object,
      default: null,
    },
  },
  data: () => ({
    loading: false,
  }),
  computed: {
    prerequisiteRuleValid() {
      return this.rule?.userInfo?.context?.validPrerequisites
        && this.rule?.userInfo?.context?.validPrerequisites[this.prerequisiteRule.id];
    },
    buttonLabel() {
      return this.$t(this.prerequisiteRuleValid
        && 'rules.seeAlreadyAchievedAction'
        || 'rules.seeAction',
      {0: this.prerequisiteRule.title}
      );
    },
  },
  methods: {
    openRule() {
      this.loading = true;
      this.$ruleService.getRuleById(this.prerequisiteRule.id)
        .then(rule => this.$root.$emit('rule-detail-drawer', rule))
        .finally(() => this.loading = false);
    },
  },
};
</script>
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
  <v-list-group
    v-if="sizeToDisplay"
    v-model="open"
    :prepend-icon="prependIcon"
    :disabled="disabledCollapsing"
    :ripple="!disabledCollapsing"
    color="grey darken-1"
    append-icon=""
    dense>
    <template #activator>
      <slot v-if="$slots.default"></slot>
      <v-list-item-title
        v-else
        :disabled="disabledCollapsing"
        class="text-color d-flex align-center ms-n6">
        {{ title }}
        <v-divider v-if="!disabledCollapsing" class="ms-4" />
      </v-list-item-title>
    </template>
    <v-list-item class="ma-0">
      <engagement-center-rules-list
        :category-id="category.id"
        :rules="rulesToDisplay"
        :program="program"
        :size="sizeToDisplay"
        :action-value-extensions="actionValueExtensions"
        class="pa-0" />
    </v-list-item>
  </v-list-group>
</template>
<script>
export default {
  props: {
    category: {
      type: Object,
      default: null,
    },
    program: {
      type: Object,
      default: null,
    },
    disabledCollapsing: {
      type: Boolean,
      default: false,
    },
    actionValueExtensions: {
      type: Object,
      default: null,
    },
  },
  data: () => ({
    open: true,
    deletedRuleIds: {},
    updatedRules: {},
  }),
  computed: {
    title() {
      return !this.disabledCollapsing && this.size && `${this.category.title} ( ${this.sizeToDisplay} )` || this.category.title;
    },
    prependIcon() {
      if (this.disabledCollapsing) {
        return;
      }
      return this.open && 'fa-chevron-up fa-lg icon-default-color' || 'fa-chevron-down fa-lg icon-default-color';
    },
    rules() {
      return this.category?.rules || [];
    },
    rulesToDisplay() {
      return this.rules
        .filter(r => !this.deletedRuleIds[r.id])
        .map(r => this.updatedRules[r.id] || r);
    },
    size() {
      return this.category?.size || this.rules?.length || 0;
    },
    sizeToDisplay() {
      return this.size - Object.keys(this.deletedRuleIds).length;
    },
  },
  created() {
    this.$root.$on('rule-updated', this.onRuleUpdated);
    this.$root.$on('rule-deleted', this.onRuleDeleted);
  },
  methods: {
    onRuleUpdated(rule) {
      if (this.rules.find(r => r.id === rule.id)) {
        return this.$ruleService.getRuleById(rule.id, {
          lang: eXo.env.portal.language,
          expand: 'countRealizations',
          realizationsLimit: 3,
        })
          .then(r => this.$set(this.updatedRules, r.id, r));
      }
    },
    onRuleDeleted(rule) {
      if (this.rules.find(r => r.id === rule.id)) {
        this.$set(this.deletedRuleIds, rule.id, true);
      }
    },
  },
};
</script>

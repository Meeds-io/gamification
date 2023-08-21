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
  <div>
    <v-card-text class="d-flex flex-grow-1 text-left px-0 pt-4 pb-2">
      {{ $t('rule.form.label.action.title') }}
    </v-card-text>
    <rule-suggester
      v-model="rules"
      :program-id="programId"
      :excluded-ids="excludedIds"
      :labels="labels"
      multiple
      exclude-prerequisites />
  </div>
</template>
<script>
export default {
  props: {
    value: {
      type: String,
      default: null,
    },
    excludedIds: {
      type: Array,
      default: null,
    },
    programId: {
      type: String,
      default: null,
    },
  },
  data: () => ({
    rules: null,
  }),
  computed: {
    labels() {
      return {
        placeholder: this.$t('rule.form.label.actionPlaceholder'),
        noDataLabel: this.$t('rule.form.label.actionNoData'),
        searchPlaceholder: this.$t('rule.form.label.actionNotFound'),
      };
    },
  },
  created() {
    this.rules = this.value;
  },
  watch: {
    rules() {
      this.$emit('input', this.rules);
    },
    value() {
      this.rules = this.value;
    },
  }
};
</script>
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
  <v-progress-circular
    v-if="loading"
    size="24"
    color="primary"
    indeterminate />
  <div v-else-if="error || !rule" class="d-flex">
    <v-icon
      v-if="error"
      :title="$t('analytics.errorRetrievingDataForValue', {0: ruleId})"
      color="error"
      class="my-auto"
      size="18">
      fa-exclamation-circle
    </v-icon>
    {{ ruleId }}
  </div>
  <v-chip
    v-else-if="rule"
    :href="ruleLink"
    class="text-truncate"
    outlined>
    {{ rule.title }}
  </v-chip>
</template>
<script>
export default {
  props: {
    ruleId: {
      type: Number,
      default: null,
    },
  },
  data: () => ({
    loading: true,
    error: false,
    rule: null,
  }),
  computed: {
    ruleLink() {
      return `${eXo.env.portal.context}/${eXo.env.portal.engagementSiteName}/contributions/actions/${this.ruleId}`;
    },
  },
  created() {
    if (this.ruleId) {
      this.loading = true;
      this.error = false;
      this.$ruleService.getRuleById(this.ruleId, {
        lang: eXo.env.portal.language,
      })
        .then(rule => this.rule = rule)
        .catch(() => this.error = true)
        .finally(() => this.loading = false);
    } else {
      this.loading = false;
    }
  },
};
</script>
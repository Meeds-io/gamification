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
  <div v-else-if="error || !program" class="d-flex">
    <v-icon
      v-if="error"
      :title="$t('analytics.errorRetrievingDataForValue', {0: programId})"
      color="error"
      class="my-auto"
      size="18">
      fa-exclamation-circle
    </v-icon>
    {{ programId }}
  </div>
  <v-chip
    v-else-if="program"
    :href="programLink"
    class="text-truncate"
    outlined>
    {{ program.title }}
  </v-chip>
</template>
<script>
export default {
  props: {
    programId: {
      type: Number,
      default: null,
    },
  },
  data: () => ({
    loading: true,
    error: false,
    program: null,
  }),
  computed: {
    programLink() {
      return `${eXo.env.portal.context}/${eXo.env.portal.engagementSiteName}/contributions/programs/${this.programId}`;
    },
  },
  created() {
    if (this.programId) {
      this.loading = true;
      this.error = false;
      this.$programService.getProgramById(this.programId, {
        lang: eXo.env.portal.language
      })
        .then(program => this.program = program)
        .catch(() => this.error = true)
        .finally(() => this.loading = false);
    } else {
      this.loading = false;
    }
  },
};
</script>
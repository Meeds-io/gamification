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
  <v-icon
    :size="size"
    :class="ruleIconColorClass"
    class="rule-icon">
    {{ ruleIcon }}
  </v-icon>
</template>
<script>
export default {
  props: {
    ruleEvent: {
      type: String,
      default: () => null,
    },
    iconColorClass: {
      type: String,
      default: null,
    },
    size: {
      type: Number,
      default: () => 16,
    },
  },
  data: () => ({
    actionValueExtensions: {},
  }),
  computed: {
    extension() {
      if (this.actionValueExtensions) {
        return Object.values(this.actionValueExtensions)
          .sort((ext1, ext2) => (ext1.rank || 0) - (ext2.rank || 0))
          .find(extension => extension.match && extension.match(this.ruleEvent)) || null;
      }
      return null;
    },
    ruleIcon() {
      return this.extension?.icon || 'fas fa-trophy';
    },
    ruleIconColorClass() {
      return this.iconColorClass || this.extension?.iconColorClass || 'primary--text';
    },
  },
  created() {
    this.actionValueExtensions = this.$root.actionValueExtensions || {};
    this.$root.$on('rule-actions-updated', this.refreshExtensions);
  },
  beforeDestroy() {
    this.$root.$off('rule-actions-updated', this.refreshExtensions);
  },
  methods: {
    refreshExtensions() {
      this.actionValueExtensions = this.$root.actionValueExtensions || {};
    },
  },
};
</script>
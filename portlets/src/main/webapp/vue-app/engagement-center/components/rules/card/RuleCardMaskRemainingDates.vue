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
  <engagement-center-rule-card-mask-content
    :title="rule.title"
    :icon="alreadyEnded && 'fas fa-calendar-check' || 'fas fa-calendar-plus'"
    class="rule-card-mask-dates">
    <engagement-center-rule-date-info-chip
      :rule="rule"
      class="subtitle-1" />
  </engagement-center-rule-card-mask-content>
</template>
<script>
export default {
  props: {
    rule: {
      type: Object,
      default: null,
    },
  },
  computed: {
    endDateMillis() {
      return this.rule?.endDate && new Date(this.rule?.endDate).getTime() || 0;
    },
    alreadyEnded() {
      return this.endDateMillis && this.endDateMillis < this.$root.now;
    },
  },
};
</script>
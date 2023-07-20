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
  <div v-if="show" class="rule-card-dates d-flex align-center">
    <engagement-center-rule-date-info-chip
      v-model="datesInfo"
      :rule="rule"
      @input="initialized = true" />
  </div>
</template>
<script>
export default {
  props: {
    rule: {
      type: Object,
      default: null,
    },
  },
  data: () => ({
    datesInfo: null,
    initialized: false,
  }),
  computed: {
    show() {
      return !this.initialized || this.datesInfo;
    },
  },
};
</script>
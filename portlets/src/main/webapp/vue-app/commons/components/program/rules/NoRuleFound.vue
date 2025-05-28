<!--
This file is part of the Meeds project (https://meeds.io/).
Copyright (C) 2023 Meeds Association
contact@meeds.io
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
    <div class="pt-8 d-flex align-center justify-center">
      <v-icon
        class="px-8 tertiary--text"
        size="30">
        fas fa-award
      </v-icon>
      <v-icon
        class="px-4 px-sm-12 tertiary--text"
        size="30">
        fas fa-stream
      </v-icon>
      <v-icon
        class="px-8 tertiary--text"
        size="30">
        fas fa-trophy
      </v-icon>
    </div>
    <div class="d-flex flex-column justify-center align-center py-2">
      <span class="mx-2 py-3 text-wrap" v-html="noResultsText">
      </span>
    </div>
  </div>
</template>
<script>
export default {
  computed: {
    noResultsText() {
      return this.$t('programs.details.noRuleFound', {
        0: '<a class="primary--text font-weight-bold" onclick="window.dispatchEvent(new CustomEvent(\'reset-keyword\'))">',
        1: '</a>',
      });
    },
  },
  created() {
    window.addEventListener('reset-keyword', () => {
      this.$emit('keyword-changed', null);
    });
  },
};
</script>
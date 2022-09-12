<!--
This file is part of the Meeds project (https://meeds.io/).
Copyright (C) 2022 Meeds Association
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
    <v-card
      id="engagementCenterProgramCard"
      class="mx-auto card"
      height="240"
      max-height="240"
      outlined>
      <div class="contentCard">
        <div class="d-flex flex-grow-1 pa-0">
          <v-img
            id="engagementCenterProgramCardImg"
            :src="programCover"
            :alt="$t('programs.cover.default')"
            aspect-ratio="1"
            min-height="70px"
            min-width="70px"
            height="100%"
            width="100%"
            max-height="160px"
            class="primary--text" />
        </div>
      </div>
      <div class="mt-2">
        <div class="center">
          <span class="text-header-title dark-grey-color text-truncate">  {{ program.title }} </span>
        </div>
        <div class="center">
          <span> <v-icon class="fas fa-trophy trophy small" /> {{ programBudget }} </span>
        </div>
      </div>
    </v-card>
  </div>
</template>
<script>
export default {
  props: {
    program: {
      type: Object,
      default: null
    }
  },
  computed: {
    programCover(){
      return this.program?.coverUrl || '';
    },
    programBudget(){
      return this.$t('programs.budget', {0: this.program?.budget || 0});
    }
  }
};
</script>

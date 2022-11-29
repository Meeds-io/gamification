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
  <v-flex class="searchNoResults justify-center d-flex border-box-sizing emptyChallenges">
    <div class="d-flex flex-column">
      <img
        src="/gamification-portlets/skin/images/challenge_not_found_filter_image.png"
        :alt="$t('challenges.search.noResults')"
        width="230px"
        class="mx-auto mb-2"
        loading="lazy">
      <div class="d-flex flex-column text-center text-sub-title">
        <span v-if="info" class="text-header-title text-light-color">{{ info }}</span>
        <span v-if="infoMessage" class="text-sub-title subtitle-1 text-light-color">{{ infoMessage }}</span>
      </div>
    </div>
  </v-flex>
</template>
<script>

export default {
  props: {
    info: {
      type: String,
      default: ''
    },
    infoMessage: {
      type: String,
      default: ''
    },
  },
};
</script>
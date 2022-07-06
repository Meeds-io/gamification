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
  <div class="welcomeMessage">
    <img
      src="/challenges/images/Challenge_app_backg_image.png"
      :alt="$t('challenges.welcomeMessageImgAlt')"
      class="mx-4"
      loading="lazy">
    <div class="mx-4 my-6">
      <p class="title"> {{ $t('challenges.welcomeMessage') }} </p>
      <p v-if="canAddChallenge"> {{ $t('challenges.welcomeMessageForManager') }} </p>
      <p v-else> {{ $t('challenges.welcomeMessageForUser') }} </p>
    </div>
  </div>
</template>
<script>
export default {
  props: {
    canAddChallenge: {
      type: Boolean,
      default: false,
    },
  },
};
</script>

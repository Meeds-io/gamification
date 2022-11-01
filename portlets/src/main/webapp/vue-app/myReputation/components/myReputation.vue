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
  <v-card
    height="338"
    flat>
    <div class="d-flex flex-row px-4 pt-4">
      <h4 class="hidden-space my-0"> {{ $t('gamification.myReputation.title') }} </h4>
    </div>
    <div>
      <div class="d-flex flex-row px-4 pt-4">
        <h4 class="subtitle-2 align-self-start my-0 black--text"> {{ $t('gamification.myReputation.KudosTitle') }} </h4>
      </div>
      <extension-registry-components
        :params="params"
        name="my-reputation-overview"
        type="my-reputation-item"
        class="d-flex flex-column" />
    </div>
  </v-card>
</template>
<script>
export default {
  computed: {
    params() {
      return {
        isOverviewDisplay: true,
      };
    }
  }
};
</script>
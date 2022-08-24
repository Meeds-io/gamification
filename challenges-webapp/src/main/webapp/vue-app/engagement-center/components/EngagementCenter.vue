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
  <v-app>
    <main v-if="engagementCenterEnabled">
      <v-tabs
        v-model="tab"
        slider-size="4"
        class="mb-4">
        <v-tab class="px-5">{{ $t('engagementCenter.label.programs') }}</v-tab>
        <v-tab class="px-5">{{ $t('engagementCenter.label.challenges') }}</v-tab>
        <v-tab class="px-5" v-if="isAdministrator">{{ $t('engagementCenter.label.achievements') }}</v-tab>
      </v-tabs>
      <v-tabs-items v-model="tab">
        <v-tab-item>
          <engagement-center-programs />
        </v-tab-item>
        <v-tab-item>
          <challenges />
        </v-tab-item>
        <v-tab-item v-if="isAdministrator">
          <realizations id="Realizations" />
        </v-tab-item>
        <v-tab-item v-if="isAdministrator">
          <realizations id="Realizations" />
        </v-tab-item>
      </v-tabs-items>
    </main>
    <main v-else>
      <challenges />
    </main>
  </v-app>
</template>

<script>
export default {
  props: {
    isAdministrator: {
      type: Boolean,
      default: false,
    },
  },
  data: () => ({
    engagementCenterEnabled: eXo.env.portal.engagementCenterEnabled,
    tab: null,
  }),
};
</script>
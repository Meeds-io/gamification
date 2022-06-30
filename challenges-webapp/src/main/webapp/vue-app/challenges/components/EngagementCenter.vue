<!--
This file is part of the Meeds project (https://meeds.io/).
Copyright (C) 2020 Meeds Association
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
        slider-size="4">
        <v-tab>{{ $t('engagementCenter.label.realizations') }}</v-tab>
        <v-tab>{{ $t('engagementCenter.label.programs') }}</v-tab>
      </v-tabs>
      <v-tabs-items v-model="tab" class="tabs-content">
        <v-tab-item class="pa-4" eager>
          <challenges />
        </v-tab-item>
        <v-tab-item eager />
      </v-tabs-items>
    </main>
    <main v-else>
      <challenges />
    </main>
  </v-app>
</template>

<script>
export default {
  data: () => ({
    engagementCenterEnabled: eXo.env.portal.engagementcenterEnabled,
    tab: null,
  }),
  watch: {
    tab() {
      if (this.tab === 0) {
        // window.history.pushState('engagementcenter', 'My engagement center', `${eXo.env.portal.context}/${eXo.env.portal.portalName}/engagement/realizations`);
      } else if (this.tab === 1) {
        // window.history.pushState('engagementcenter', 'My engagement center', `${eXo.env.portal.context}/${eXo.env.portal.portalName}/engagement/programs`);
      }
    },
  },
};
</script>
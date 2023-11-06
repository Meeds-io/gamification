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
  <exo-drawer
    id="programsOverviewListDrawer"
    ref="drawer"
    v-model="drawer"
    :loading="loading"
    :right="!$vuetify.rtl">
    <template #title>
      {{ $t('gamification.overview.programsList') }}
    </template>
    <template #titleIcons>
      <v-btn
        :href="actionsPageURL"
        icon>
        <v-icon size="24">fa-external-link-alt</v-icon>
      </v-btn>
    </template>
    <template #content>
      <gamification-overview-widget height="auto">
        <gamification-overview-program-item
          v-for="program in programs" 
          :key="program.id"
          :program="program"
          class="flex-grow-1"
          go-back-button />
      </gamification-overview-widget>
    </template>
  </exo-drawer>
</template>
<script>
export default {
  data: () => ({
    programs: [],
    limitToLoad: -1,
    loading: false,
  }),
  computed: {
    actionsPageURL() {
      return eXo.env.portal.portalName === 'public' && '/portal/public/overview/actions' || `${eXo.env.portal.context}/${eXo.env.portal.engagementSiteName}/contributions/actions`;
    },
  },
  created() {
    this.$root.$on('programs-overview-list-drawer', this.open);
  },
  beforeDestroy() {
    this.$root.$off('programs-overview-list-drawer', this.open);
  },
  methods: {
    open() {
      this.programs = [];
      this.retrievePrograms();
      this.$refs.drawer.open();
    },
    retrievePrograms() {
      this.loading = true;
      return this.$programService.getPrograms({
        limit: this.limitToLoad,
        type: 'ALL',
        status: 'ENABLED',
        expand: 'countActiveRules',
        sortBy: 'modifiedDate',
        sortDescending: true,
        lang: eXo.env.portal.language,
      })
        .then((data) => this.programs = data?.programs || [])
        .finally(() => this.loading = false);
    },
  },
};
</script>
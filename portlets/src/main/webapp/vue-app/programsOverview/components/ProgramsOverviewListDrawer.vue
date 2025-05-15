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
    :loading="loading"
    :right="!$vuetify.rtl"
    allow-expand
    @expand-updated="expanded = $event">
    <template #title>
      {{ spaceId && $t('gamification.overview.space.programsList') || $t('gamification.overview.programsList') }}
    </template>
    <template #titleIcons>
      <v-btn
        v-if="canAddProgram"
        icon
        @click="$root.$emit('program-form-open')">
        <v-icon size="24">fa-plus</v-icon>
      </v-btn>
    </template>
    <template #content>
      <engagement-center-programs
        v-if="expanded"
        :is-administrator="isAdministrator"
        :is-program-manager="isProgramManager"
        :can-add-program="canAddProgram" />
      <gamification-overview-widget v-else height="auto">
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
    spaceId: eXo.env.portal.spaceId,
    expanded: false,
  }),
  computed: {
    canAddProgram() {
      return this.$root.canAddProgram;
    },
    isAdministrator() {
      return this.$root.isAdministrator;
    },
    isProgramManager() {
      return this.$root.isProgramManager;
    }
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
        spaceId: this.spaceId,
        type: 'ALL',
        status: 'ENABLED',
        expand: 'countActiveRules',
        sortBy: this.$root.programsSortBy || 'modifiedDate',
        sortDescending: true,
        lang: eXo.env.portal.language,
      })
        .then((data) => this.programs = data?.programs || [])
        .finally(() => this.loading = false);
    },
  },
};
</script>
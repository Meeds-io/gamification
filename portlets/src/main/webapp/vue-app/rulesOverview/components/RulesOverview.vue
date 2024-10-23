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
  <v-app v-if="!hidden">
    <v-hover v-model="hover">
      <gamification-rules-overview-widget
        :hover-edit="hoverEdit"
        :hide-empty-placeholder="$root.hideWidgetWhenEmpty"
        @open-list="$refs.listDrawer.open()"
        @hide="hidden = true" />
    </v-hover>
    <gamification-rules-overview-list-drawer
      ref="listDrawer" />
    <gamification-rules-overview-settings-drawer
      v-if="$root.canEdit && !hidden" />
    <engagement-center-rule-extensions />
  </v-app>
</template>
<script>
export default {
  data: () => ({
    hidden: false,
    hover: false,
  }),
  computed: {
    hoverEdit() {
      return this.$root.canEdit && this.hover;
    }
  },
  watch: {
    hidden() {
      this.$root.$updateApplicationVisibility(!this.hidden);
    }
  },
  mounted() {
    if (this.hidden) {
      this.$el.closest('.PORTLET-FRAGMENT').classList.add('hidden');
    }
  }
};
</script>

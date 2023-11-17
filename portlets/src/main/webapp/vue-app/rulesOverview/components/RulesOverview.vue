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
    <gamification-rules-overview-space-list
      v-if="spaceId"
      @hide="hidden = true" />
    <gamification-rules-overview-full-list
      v-else-if="$root.showLocked" />
    <gamification-rules-overview-reduced-list
      v-else
      @open-list="$refs.listDrawer.open()" />
    <gamification-rules-overview-list-drawer
      ref="listDrawer" />
    <engagement-center-rule-extensions />
  </v-app>
</template>
<script>
export default {
  data: () => ({
    spaceId: eXo.env.portal.spaceId,
    hidden: false,
  }),
  watch: {
    hidden() {
      if (this.hidden) {
        this.$el.closest('.PORTLET-FRAGMENT').classList.add('hidden');
      } else {
        this.$el.closest('.PORTLET-FRAGMENT').classList.remove('hidden');
      }
    }
  },
  mounted() {
    this.$el.closest('.PORTLET-FRAGMENT').classList.add('hidden');
  }
};
</script>

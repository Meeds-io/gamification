<!--
  This file is part of the Meeds project (https://meeds.io/).

  Copyright (C) 2020 - 2023 Meeds Association contact@meeds.io

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
    ref="drawer"
    v-model="drawer"
    :right="!$vuetify.rtl">
    <template slot="title">
      <span class="pb-2"> {{ $t(eventTitleLabel) }} cancelled by </span>
    </template>
    <template #content>
      <v-list class="pt-6">
        <template v-for="item in cancellerEvents">
          <template v-if="item">
            <v-list-item :key="item">
              <v-list-item-title class="text-subtitle-1 dark-grey-color">{{ item }}</v-list-item-title>
            </v-list-item>
          </template>
        </template>
      </v-list>
    </template>
  </exo-drawer>
</template>
<script>
export default {
  props: {
    event: {
      type: Object,
      default: () => null
    },
  },
  data: () => ({
    drawer: false,
  }),
  computed: {
    eventTitle() {
      return this.event?.title;
    },
    eventTitleLabel() {
      return this.$t(`gamification.event.title.${this.eventTitle}`);
    },
    cancellerEvents() {
      return this.event?.cancellerEvents || [];
    },
  },
  methods: {
    open() {
      this.$refs.drawer.open();
    },
  },
};
</script>
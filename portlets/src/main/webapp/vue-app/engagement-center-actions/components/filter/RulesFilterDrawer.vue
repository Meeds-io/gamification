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
    ref="RealizationsFilterDrawer"
    class="RealizationsFilterDrawer"
    :right="!$vuetify.rtl">
    <template #title>
      {{ $t('gamification.actions.title.filterActions') }}
    </template>
    <template #content>
      <v-card flat>
        <v-card-title class="pb-0 text-subtitle-1 text-color font-weight-bold">
          {{ $t('gamification.actions.filter.filterByType') }}
        </v-card-title>
        <v-card-text class="pb-0">
          <v-radio-group v-model="type" class="ms-n1">
            <v-radio
              :label="$t('gamification.actions.filter.anyAction')"
              value="ALL" />
            <v-radio
              :label="$t('gamification.actions.filter.manualAction')"
              value="MANUAL" />
            <v-radio
              :label="$t('gamification.actions.filter.automaticAction')"
              value="AUTOMATIC" />
          </v-radio-group>
        </v-card-text>
        <v-card-title class="pb-0 text-subtitle-1 text-color font-weight-bold">
          {{ $t('gamification.actions.filter.filterByStatus') }}
        </v-card-title>
        <v-card-text class="pb-0">
          <v-radio-group v-model="status" class="ms-n1">
            <v-radio
              :label="$t('gamification.actions.filter.allActions')"
              value="ALL" />
            <v-radio
              :label="$t('gamification.actions.filter.activeActions')"
              value="STARTED" />
            <v-radio
              :label="$t('gamification.actions.filter.upcomingActions')"
              value="UPCOMING" />
            <v-radio
              :label="$t('gamification.actions.filter.endedActions')"
              value="ENDED" />
            <v-radio
              v-if="isAdministrator"
              :label="$t('gamification.actions.filter.disabledActions')"
              value="DISABLED" />
          </v-radio-group>
        </v-card-text>
      </v-card>
    </template>
    <template #footer>
      <div class="d-flex mr-2">
        <v-btn
          class="dark-grey-color px-0 hiddent-xs-only"
          text
          outlined
          @click="reset">
          <v-icon size="18" class="icon-default-color me-2">fa-redo</v-icon>
          {{ $t('challenge.button.resetFilter') }}
        </v-btn>
        <v-spacer />
        <v-btn
          class="btn mx-1"
          @click="close">
          {{ $t('programs.details.filter.cancel') }}
        </v-btn>
        <v-btn
          class="btn btn-primary"
          @click="apply">
          {{ $t('gamification.apply') }}
        </v-btn>
      </div>
    </template>
  </exo-drawer>
</template>
<script>
export default {
  props: {
    isAdministrator: {
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {
      type: 'ALL',
      status: 'STARTED',
    };
  },
  created() {
    this.$root.$on('rules-filter-drawer', this.open);
  },
  methods: {
    open() {
      this.$refs.RealizationsFilterDrawer.open();
    },
    close() {
      this.$refs.RealizationsFilterDrawer.close();
    },
    reset() {
      this.type = 'ALL';
      this.status = 'STARTED';
      this.apply();
    },
    apply() {
      this.$emit('apply', this.type, this.status);
      this.close();
    },
  }
};
</script>
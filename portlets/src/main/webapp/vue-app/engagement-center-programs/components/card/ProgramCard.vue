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
    id="engagementCenterProgramCard"
    :style="programStyle"
    class="card engagement-center-card d-flex flex-column overflow-hidden rounded"
    outlined
    hover>
    <div @click="openProgramDetail" class="d-flex flex-column flex-grow-1">
      <v-card
        class="overflow-hidden position-relative d-flex align-center justify-center"
        height="100"
        max-height="100"
        flat
        tile>
        <img
          :src="programCover"
          :alt="$t('programs.cover.default')"
          class="no-max-width full-height"
          width="auto"
          height="100%">
        <div class="position-absolute r-0 t-0">
          <engagement-center-program-menu
            :is-administrator="isAdministrator"
            :program="program" />
        </div>
      </v-card>
      <v-spacer />
      <v-list
        min-height="70"
        class="d-flex py-1"
        dense>
        <v-list-item class="px-3">
          <v-list-item-content class="align-center text-wrap text-break py-0">
            <v-tooltip :disabled="$root.isMobile" bottom>
              <template #activator="{ on, attrs }">
                <span
                  class="d-flex-inline position-relative text-truncate-2 text-header" 
                  v-bind="attrs"
                  v-on="on">
                  {{ program.title }}
                </span>
              </template>
              {{ program.title }}
            </v-tooltip>
          </v-list-item-content>
        </v-list-item>
      </v-list>
      <v-spacer />
      <div class="d-flex justify-center py-2">
        <v-icon size="18" class="pe-2 primary--text">fas fa-trophy</v-icon>
        <span class="text-light-color text-caption" v-sanitized-html="$t('programs.budget', $t(programBudgetLabel))"></span>
      </div>
    </div>
    <div class="d-flex ma-2">
      <div class="pa-1 d-none d-sm-inline">
        <span class="my-auto caption text-light-color"> {{ $t('programs.details.label.hosts') }} </span>
      </div>
      <v-spacer />
      <engagement-center-avatars-list
        v-if="owners.length"
        :avatars="owners"
        :max-avatars-to-show="4"
        :avatars-count="ownersCount"
        :size="25"
        class="my-auto"
        @open-avatars-drawer="$root.$emit('open-owners-drawer', owners)" />
      <v-chip
        v-else
        small
        class="ms-sm-auto my-auto">
        {{ $t('programs.label.rewardAdmins') }}
      </v-chip>
    </div>
  </v-card>
</template>
<script>
export default {
  props: {
    program: {
      type: Object,
      default: null
    },
    administrators: {
      type: Array,
      default: null
    },
    isAdministrator: {
      type: Boolean,
      default: false,
    }
  },
  data: () => ({
    showMenu: false,
  }),
  computed: {
    programCover() {
      return this.program?.coverUrl || '';
    },
    programBudgetLabel() {
      return {0: `<span>${this.programBudget} ${this.$t('programs.details.label.points')}</span>`};
    },
    programBudget() {
      return this.program?.rulesTotalScore || 0;
    },
    addedOwners() {
      return (this.program?.owners || []).map(owner => ({
        userName: owner.remoteId
      }));
    },
    spaceManagers() {
      return this.program?.space?.managers;
    },
    spaceManagersList() {
      return (this.spaceManagers || []).map(owner => ({
        userName: owner
      }));
    },
    administratorUsernames() {
      return (this.program?.administrators || this.administrators || []).map(admin => ({
        userName: admin
      }));
    },
    owners() {
      return [...this.addedOwners, ...this.spaceManagersList, ...this.administratorUsernames]
        .filter((v, i, array) => array.findIndex(v2 => v?.userName === v2?.userName) === i);
    },
    ownersCount() {
      return this.owners?.length;
    },
    programStyle() {
      return this.program?.color && `border: 1px solid ${this.program.color} !important;` || '';
    },
  },
  methods: {
    openProgramDetail() {
      this.$root.$emit('open-program-detail', this.program, this.administrators);
    },
  }
};
</script>

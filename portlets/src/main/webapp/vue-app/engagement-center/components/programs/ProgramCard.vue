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
    class="card engagement-center-card"
    height="240"
    max-height="240"
    outlined
    hover>
    <div @click="openProgramDetail">
      <v-img
        :src="programCover"
        :alt="$t('programs.cover.default')"
        width="100%"
        aspect-ratio="1"
        min-height="70"
        min-width="70"
        max-height="100"
        class="primary--text">
        <engagement-center-program-menu :is-administrator="isAdministrator" :program="program" />
      </v-img>
      <v-list
        min-height="70"
        class="d-flex py-1"
        dense>
        <v-list-item class="px-3">
          <v-list-item-content class="align-center text-wrap text-break py-0">
            <v-tooltip bottom>
              <template #activator="{ on, attrs }">
                <span
                  class="d-flex-inline position-relative text-truncate-2 font-weight-bold text-subtitle-1 text--secondary" 
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
      <div class="d-flex justify-center pb-4 mt-n2">
        <v-icon size="18" class="pe-2 primary--text">fas fa-trophy</v-icon>
        <span class="text-light-color text-caption" v-sanitized-html="$t('programs.budget', $t(programBudgetLabel))"></span>
      </div>
    </div>
    <div class="d-flex mb-0 mx-2">
      <div class="pa-1">
        <span class="my-auto caption text-light-color"> {{ this.$t('programs.details.label.hosts') }} </span>
      </div>
      <v-spacer />
      <engagement-center-avatars-list
        :avatars="owners"
        :max-avatars-to-show="4"
        :avatars-count="ownersCount"
        :size="25"
        @open-avatars-drawer="$root.$emit('open-owners-drawer', owners)" />
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
    addedOwnersList() {
      return (this.program?.owners || []).filter(owner => owner.domainOwner && !this.program?.space?.managers.includes(owner.remoteId)).map(owner => ({
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
    owners() {
      return this.addedOwnersList.concat(this.spaceManagersList);
    },
    ownersCount() {
      return this.owners?.length;
    }
  },
  methods: {
    openProgramDetail() {
      this.$root.$emit('open-program-detail', this.program);
      window.history.replaceState('programs', this.$t('engagementCenter.label.programs'), `${eXo.env.portal.context}/${eXo.env.portal.portalName}/contributions/programs/${this.program.id}`);
    },
  }
};
</script>

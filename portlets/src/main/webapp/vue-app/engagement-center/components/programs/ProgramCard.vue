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
        max-height="140"
        class="primary--text">
        <v-toolbar
          color="transparent"
          flat>
          <v-spacer />
          <v-menu
            v-if="showActionsMenu"
            v-model="showMenu"
            :left="!$vuetify.rtl"
            :right="$vuetify.rtl"
            bottom
            offset-y
            attach>
            <template #activator="{ on, attrs }">
              <v-btn
                icon
                small
                class="headerProgramCard mr-0"
                v-bind="attrs"
                v-on="on">
                <v-icon size="16" color="white">fas fa-ellipsis-v</v-icon>
              </v-btn>
            </template>
            <v-list dense class="pa-0">
              <v-list-item
                class="editList"
                dense
                @click="editProgram">
                <v-icon small class="me-2 mb-1"> fas fa-edit </v-icon>
                <v-list-item-title class="editLabel">{{ $t('programs.button.editProgram') }}</v-list-item-title>
              </v-list-item>
            </v-list>
          </v-menu>
        </v-toolbar>
      </v-img>
      <div class="mt-1">
        <div class="center">
          <span class="text-header-title programTitle text-truncate">  {{ program.title }} </span>
        </div>
        <div class="center">
          <v-icon size="16" class="pe-2 triumphProgramlayout">fas fa-trophy</v-icon>
          <span class="text-light-color" v-sanitized-html="$t('programs.budget', $t(programBudgetLabel))"></span>
        </div>
      </div>
    </div>
    
    <div class="d-flex mt-2 mx-2">
      <div class="pa-1">
        <span class=" my-auto hostsLayout"> {{ this.$t('programs.details.label.hosts') }} </span>
      </div>
      <v-spacer />
      <div class="d-flex">
        <div class="d-flex">
          <exo-user-avatar
            v-for="owner in ownersToDisplay"
            :key="owner.id"
            :profile-id="owner.remoteId"
            :size="25"
            popover
            avatar 
            extra-class="me-1" />
        </div>
        <div
          v-if="seeMoreOwnerToDisplay"
          class="seeMoreOwnersIconLayout">
          <span class="seeMoreOwnersTextLayout" @click="openDrawer">+{{ showMoreOwnersNumber }}</span>
        </div>
      </div>
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
    maxOwnersToShow: 4,
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
    showActionsMenu() {
      return this.isAdministrator || this.program?.userInfo?.domainOwner;
    },
    owners() {
      return this.program?.owners;
    },
    ownersToDisplay() {
      return this.owners.slice(0, this.maxOwnersToShow - 1);
    },
    ownersCount() {
      return this.program?.owners.length;
    },
    seeMoreOwnerToDisplay () {
      return this.ownersCount >= this.maxOwnersToShow && this.owners[this.maxOwnersToShow - 1] || null;
    },
    showMoreOwnersNumber() {
      return this.ownersCount - this.maxOwnersToShow + 1;
    },
  },
  created() {
    $(document).mousedown(() => {
      if (this.showMenu) {
        window.setTimeout(() => {
          this.showMenu = false;
        }, 200);
      }
    });
  },
  methods: {
    editProgram(event) {
      if (event) {
        event.preventDefault();
        event.stopPropagation();
      }
      this.$root.$emit('edit-program-details', this.program);
    },
    openProgramDetail() {
      this.$root.$emit('open-program-detail', this.program);
    },
    openDrawer() {
      this.$root.$emit('open-owners-drawer', this.program);
    },
  }
};
</script>

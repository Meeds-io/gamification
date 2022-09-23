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
    class="mx-auto card engagement-center-card"
    height="240"
    max-height="240"
    @click="openProgramDetail"
    outlined>
    <div class="contentCard">
      <v-row
        color="transparent"
        flat
        class="edit px-0 py-0"
        height="35px">
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
              class="my-1 me-3"
              v-bind="attrs"
              v-on="on">
              <v-icon size="16" class="icon-default-color">fas fa-ellipsis-v</v-icon>
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
      </v-row>
    </div>
    <div class="d-flex flex-grow-1 pa-0">
      <v-img
        :src="programCover"
        :alt="$t('programs.cover.default')"
        width="100%"
        aspect-ratio="1"
        min-height="70"
        min-width="70"
        max-height="140"
        class="primary--text" />
    </div>
    <div class="mt-1">
      <div class="center">
        <span class="text-header-title dark-grey-color text-truncate">  {{ program.title }} </span>
      </div>
      <div class="center">
        <span> <v-icon class="fas fa-trophy trophy small" /> {{ programBudget }} </span>
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
  }),
  computed: {
    programCover(){
      return this.program?.coverUrl || '';
    },
    programBudget(){
      return this.$t('programs.budget', {0: this.program?.budget || 0});
    },
    showActionsMenu(){
      return this.isAdministrator || this.program?.userInfo?.domainOwner;
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
  }
};
</script>

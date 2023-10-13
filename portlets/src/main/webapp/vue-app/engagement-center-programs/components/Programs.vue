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
  <v-app class="card-border-radius overflow-hidden">
    <main>
      <engagement-center-programs-list
        v-show="!displayProgramDetail"
        id="engagementCenterProgramsTab"
        :is-administrator="isAdministrator"
        :is-program-manager="isProgramManager" />
      <engagement-center-program-detail
        v-if="displayProgramDetail"
        :program="program"
        :newly-created="newlyCreated"
        :is-administrator="isAdministrator" />
    </main>
    <engagement-center-rule-extensions />
    <engagement-center-program-drawer
      ref="programDrawer"
      :is-administrator="isAdministrator" />
  </v-app>
</template>

<script>
export default {
  props: {
    isAdministrator: {
      type: Boolean,
      default: false,
    },
    isProgramManager: {
      type: Boolean,
      default: false,
    },
  },
  data: () => ({
    tab: null,
    program: null,
    displayProgramDetail: false,
    newlyCreated: false,
    programsLinkBasePath: `${eXo.env.portal.context}/${eXo.env.portal.engagementSiteName}/contributions/programs`,
  }),
  watch: {
    displayProgramDetail() {
      if (this.displayProgramDetail && this.program?.id) {
        window.history.replaceState('programs', this.$t('engagementCenter.label.programs'), `${this.programsLinkBasePath}/${this.program.id}`);
      } else {
        window.history.replaceState('programs', this.$t('engagementCenter.label.programs'), `${this.programsLinkBasePath}`);
      }
    },
  },
  created() {
    this.$root.$on('program-added', this.openCreatedProgramDetail);
    this.$root.$on('rule-created', this.resetNewlyCreated);
    this.$root.$on('rule-updated', this.updateOpenedProgram);
    this.$root.$on('rule-deleted', this.updateOpenedProgram);
    this.$root.$on('open-program-detail', this.openProgramDetail);
    this.$root.$on('open-program-detail-by-id', this.openProgramDetailById);
    this.$root.$on('close-program-detail', () => this.displayProgramDetail = false);
    this.init();
  },
  methods: {
    init() {
      const urlPath = document.location.pathname;
      const id = urlPath.match( /\d+/ ) && urlPath.match( /\d+/ ).join('');
      if (id) {
        this.openProgramDetailById(id);
      }
    },
    resetNewlyCreated() {
      this.newlyCreated = false;
      this.updateOpenedProgram();
    },
    updateOpenedProgram() {
      if (this.displayProgramDetail && this.program?.id) {
        this.openProgramDetailById(this.program.id);
      }
    },
    openProgramDetailById(id, newlyCreated) {
      this.$programService.getProgramById(id, {
        lang: eXo.env.portal.language,
        expand: 'countActiveRulesWhenDisabled',
      })
        .then(program => {
          if (program?.id) {
            this.openProgramDetail(program, newlyCreated);
          }
        });
    },
    openCreatedProgramDetail(program) {
      this.openProgramDetail(program, true);
    },
    openProgramDetail(program, newlyCreated) {
      this.newlyCreated = newlyCreated || false;
      this.program = program;
      this.displayProgramDetail = true;
    },
  }
};
</script>
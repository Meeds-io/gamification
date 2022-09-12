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
  <div
    id="EngagementCenterPrograms"
    class="border-box-sizing"
    role="main"
    flat>
    <v-toolbar
      v-if="canAddProgram"
      color="transparent"
      flat
      class="pa-4">
      <div class="border-box-sizing clickable">
        <v-btn
          v-if="canAddProgram"
          class="btn btn-primary"
          @click="$root.$emit('open-program-drawer')">
          <v-icon small>fas fa-plus</v-icon>
          <span class="mx-2 d-none d-lg-inline text-capitalize-first-letter subtitle-1">
            {{ $t('programs.button.addProgram') }}
          </span>
        </v-btn>
      </div>
      <v-spacer />
    </v-toolbar>

    <engagement-center-programs-list
      :loading="loading"
      :programs-list="programsList"
      :is-administrator="isAdministrator"
      class="py-10" />
    <engagement-center-program-drawer ref="programDrawer" />
  </div>
</template>

<script>
export default {
  props: {
    isAdministrator: {
      type: Boolean,
      default: false,
    }
  },
  data() {
    return {
      programsList: null,
      loading: false,
      canAddProgram: false,
      type: 'ALL',
      status: 'ENABLED',
    };
  },
  computed: {
    programsPerPage() {
      if (this.$vuetify.breakpoint.width <= 768) {
        return 5;
      } else {
        return 6;
      }
    },
  },
  watch: {
    loading() {
      if (this.loading) {
        document.dispatchEvent(new CustomEvent('displayTopBarLoading'));
      } else {
        document.dispatchEvent(new CustomEvent('hideTopBarLoading'));
      }
    },
  },
  created() {
    const promises = [];
    promises.push(this.computeCanAddProgram());
    promises.push(this.retrievePrograms(false));
    this.$root.$on('program-load-more', this.loadMore);
    this.$root.$on('program-added', this.refreshPrograms);
    Promise.all(promises)
      .finally(() => this.$root.$applicationLoaded());
  },
  methods: {
    computeCanAddProgram() {
      return this.$programsServices.canAddProgram()
        .then(canAddProgram => this.canAddProgram = canAddProgram);
    },
    retrievePrograms(append) {
      this.loading = true;
      const offset = append && this.programsList?.domains?.length || 0;
      const returnSize = append ?  false : true;
      this.$programsServices
        .retrievePrograms(offset, this.programsPerPage, this.type, this.status, returnSize)
        .then((programsList) => {
          if (append) {
            this.programsList.domains = this.programsList?.domains.concat(programsList.domains);
          } else {
            this.programsList = programsList;
          }
        })
        .finally(() => this.loading = false);
    },
    loadMore() {
      return this.retrievePrograms(true);
    },
    refreshPrograms() {
      return this.retrievePrograms(false);
    },
  },
};
</script>
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
      v-if="isAdministrator"
      color="transparent"
      max-height="64"
      flat
      class="px-4 mt-4">
      <div class="border-box-sizing clickable">
        <v-btn
          id="engagementCenterAddProgramBtn"
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
      <select
        id="EngagementCenterApplicationCProgramsQuickFilter"
        v-model="status"
        class="my-auto ignore-vuetify-classes text-truncate challengeQuickFilter width-auto"
        @change="filter">
        <option
          v-for="stat in programStatus"
          :key="stat.value"
          :value="stat.value">
          <span class="d-none d-lg-inline">
            {{ stat.text }}
          </span>
        </option>
      </select>
    </v-toolbar>
    <v-layout class="pa-3">
      <v-row no-gutters>
        <v-col
          v-for="program in programs"
          :key="program.id"
          class="mb-4"
          cols="12"
          sm="6"
          lg="4">
          <engagement-center-program-card
            :program="program"
            :is-administrator="isAdministrator"
            class="mx-2" />
        </v-col>
      </v-row>
    </v-layout>
    <v-row v-if="hasMore" class="ml-6 mr-6 mb-6 mt-n4">
      <v-btn
        :loading="loading"
        :disabled="loading"
        class="loadMoreButton my-4 btn"
        block
        @click="$root.$emit('program-load-more')">
        {{ $t('engagementCenter.button.ShowMore') }}
      </v-btn>
    </v-row>
    <exo-confirm-dialog
      v-if="confirmDelete"
      ref="deleteProgramConfirmDialog"
      :message="deleteConfirmMessage"
      :title="$t('programs.label.title.confirmDeleteRule')"
      :ok-label="$t('programs.label.ok.button')"
      :cancel-label="$t('programs.label.cancel.button')"
      @ok="deleteProgram" />
    <engagement-center-welcome-message
      v-if="displayNoSearchResult && !isStatusDisabled"
      class="mx-16 mb-5">
      <template #content>
        <div class="mx-4 my-6 dark-grey-color">
          <p class="align-center font-weight-bold mb-5"> {{ $t('programs.label.welcome') }} </p>
          <p class="align-center mb-5" v-sanitized-html="welcomeMessage"></p>
          <p class="align-center"> {{ $t('programs.label.seeYouSoon') }} </p>
        </div>
      </template>
    </engagement-center-welcome-message>
    <engagement-center-no-results
      v-else-if="displayNoSearchResult && isStatusDisabled" 
      :info="$t('program.filter.noResults')"
      class="mt-11" />
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
      programs: [],
      totalSize: 0,
      pageSize: 9,
      loading: false,
      canAddProgram: false,
      type: 'ALL',
      status: 'ENABLED',
      deleteConfirmMessage: '',
      selectedProgram: {},
      initialized: false,
      offset: 0,
      limit: 9,
      users: [],
      limitToFetch: 0,
      originalLimitToFetch: 0,
    };
  },
  computed: {
    programStatus() {
      return [{
        text: this.$t('programs.status.allPrograms'),
        value: 'ALL',
      },{
        text: this.$t('programs.status.enabledPrograms'),
        value: 'ENABLED',
      },{
        text: this.$t('programs.status.disabledPrograms'),
        value: 'DISABLED',
      }];
    },
    displayNoSearchResult() {
      return !this.loading && this.totalSize === 0;
    },
    welcomeMessage() {
      return this.$t('programs.label.welcomeMessage', {
        0: `<a class="primary--text font-weight-bold" href="${eXo.env.portal.context}/${eXo.env.portal.portalName}/spaces/">`,
        1: '</a>',
      });
    },
    hasMore() {
      return this.loading || this.limitToFetch < this.totalSize;
    },
    isStatusDisabled() {
      return this.status === 'DISABLED';
    }
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
    this.limitToFetch = this.originalLimitToFetch = this.limit;
    const promises = [];
    promises.push(this.computeCanAddProgram());
    promises.push(this.retrievePrograms());
    this.$root.$on('program-load-more', this.loadMore);
    this.$root.$on('program-added', this.refreshPrograms);
    this.$root.$on('program-updated', this.refreshPrograms);
    this.$root.$on('delete-program', this.confirmDelete);
    Promise.all(promises)
      .finally(() => this.$root.$applicationLoaded());
  },
  methods: {
    computeCanAddProgram() {
      return this.$programsServices.canAddProgram()
        .then(canAddProgram => this.canAddProgram = canAddProgram);
    },
    retrievePrograms() {
      this.loading = true;
      return this.$programsServices
        .retrievePrograms(this.offset, this.limitToFetch, this.type, this.status)
        .then((data) => {
          this.programs = data.domains;
          this.totalSize = data.domainsSize || 0;
        })
        .finally(() => {
          if (!this.initialized) {
            this.$root.$applicationLoaded();
          }
          this.loading = false;
          this.initialized = true;
        });
    },
    loadMore() {
      if (this.hasMore) {
        this.limitToFetch += this.pageSize;
      }
      return this.retrievePrograms();
    },
    refreshPrograms() {
      return this.retrievePrograms();
    },
    filter() {
      this.resetSearch();
      return this.retrievePrograms();
    },
    resetSearch() {
      if (this.limitToFetch !== this.originalLimitToFetch) {
        this.limitToFetch = this.originalLimitToFetch;
      }
    },
    deleteProgram() {
      this.loading = true;
      this.$programsServices.deleteProgram(this.selectedProgram.id)
        .then((deletedProgram) => {
          this.$root.$emit('program-deleted', deletedProgram);
          this.$engagementCenterUtils.displayAlert(this.$t('programs.programDeleteSuccess'));
          this.retrievePrograms();
        })
        .finally(() => this.loading = false);
    },
    confirmDelete(program) {
      this.selectedProgram = program;
      this.deleteConfirmMessage = this.$t('programs.label.message.confirmDeleteRule', {0: program.title});
      this.$refs.deleteProgramConfirmDialog.open();
    },
  },
};
</script>
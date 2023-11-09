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
    <application-toolbar
      v-if="isProgramManager"
      :left-button="isAdministrator && !displayNoSearchResult && {
        icon: 'fa-plus',
        text: $t('programs.button.addProgram'),
      }"
      :right-select-box="{
        selected: status,
        items: programStatus,
      }"
      hide-cone-button
      @left-button-click="$root.$emit('program-form-open')"
      @filter-select-change="status = $event" />

    <v-layout class="py-3 px-4">
      <v-row class="mx-n3 overflow-hidden">
        <v-col
          v-for="program in programs"
          :key="program.id"
          cols="12"
          sm="6"
          lg="4">
          <engagement-center-program-card
            :program="program"
            :is-administrator="isAdministrator"
            :administrators="administrators" />
        </v-col>
        <v-col
          v-if="hasMore"
          cols="12"
          class="px-3">
          <v-btn
            :loading="loading"
            :disabled="loading"
            class="loadMoreButton my-4 btn"
            block
            @click="$root.$emit('program-load-more')">
            {{ $t('engagementCenter.button.ShowMore') }}
          </v-btn>
        </v-col>
      </v-row>
    </v-layout>
    <exo-confirm-dialog
      v-if="confirmDelete"
      ref="deleteProgramConfirmDialog"
      :message="deleteConfirmMessage"
      :title="$t('programs.label.title.confirmDeleteRule')"
      :ok-label="$t('programs.label.ok.button')"
      :cancel-label="$t('programs.label.cancel.button')"
      @ok="deleteProgram" />
    <div v-if="displayNoSearchResult">
      <div v-if="isAdministrator">
        <engagement-center-result-not-found 
          :display-back-arrow="false"
          :message-title="$t('appCenter.welcomeMessage')"
          :message-info-one="$t('programs.label.welcomeMessageForManager')"
          :message-info-three="$t('programs.label.seeYouSoon')"
          :button-text="$t('programs.button.addProgram')"
          @button-event="$root.$emit('program-form-open')" />
      </div>
      <div v-else>
        <div v-if="isExternal">
          <engagement-center-result-not-found 
            :display-back-arrow="false"
            :message-title="$t('appCenter.welcomeMessage')"
            :message-info-one="$t('programs.label.welcomeMessageForExternal')" />
        </div>
        <div v-else>
          <engagement-center-result-not-found 
            :display-back-arrow="false"
            :message-title="$t('appCenter.welcomeMessage')"
            :button-text="$t('programs.label.joinSpace')"
            :message-info-one="$t('programs.label.welcomeMessageForUser')"
            :message-info-three="$t('programs.label.welcomeMessageTwoForUser')"
            :button-url="spacesURL" />
        </div>
      </div>
    </div>
  </div>
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
  data() {
    return {
      programs: [],
      administrators: null,
      totalSize: 0,
      pageSize: 9,
      loading: true,
      type: 'ALL',
      status: 'ENABLED',
      deleteConfirmMessage: '',
      selectedProgram: {},
      offset: 0,
      limit: 9,
      users: [],
      limitToFetch: 0,
      originalLimitToFetch: 0,
      spacesURL: `${eXo.env.portal.context}/${eXo.env.portal.defaultPortal}/spaces/`,
      isExternal: eXo.env.portal.isExternal === 'true',
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
        0: `<a class="primary--text font-weight-bold" href="${eXo.env.portal.context}/${eXo.env.portal.defaultPortal}/spaces/">`,
        1: '</a>',
      });
    },
    hasMore() {
      return this.loading || this.limitToFetch < this.totalSize;
    },
    isStatusDisabled() {
      return this.status === 'DISABLED';
    },
  },
  watch: {
    administrators() {
      this.$emit('administrators-loaded', this.administrators);
    },
    loading() {
      if (this.loading) {
        document.dispatchEvent(new CustomEvent('displayTopBarLoading'));
      } else {
        document.dispatchEvent(new CustomEvent('hideTopBarLoading'));
      }
    },
    status() {
      this.retrievePrograms();
    },
  },
  created() {
    this.limitToFetch = this.originalLimitToFetch = this.limit;
    this.$root.$on('program-load-more', this.loadMore);
    this.$root.$on('program-updated', this.refreshPrograms);
    this.$root.$on('delete-program', this.confirmDelete);
    this.retrievePrograms()
      .finally(() => this.$root.$applicationLoaded());
  },
  methods: {
    retrievePrograms() {
      this.loading = true;
      return this.$programService.getPrograms({
        offset: this.offset,
        limit: this.limitToFetch,
        type: this.type,
        status: this.status,
        owned: !this.isAdministrator && this.isStatusDisabled,
        sortBy: 'title',
        sortDescending: false,
        expand: 'countActiveRulesWhenDisabled,administrators',
        lang: eXo.env.portal.language,
      })
        .then((data) => {
          this.administrators = data.administrators;
          this.programs = data.programs;
          this.totalSize = data.size || 0;
        })
        .finally(() => this.loading = false);
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
      this.$programService.deleteProgram(this.selectedProgram.id)
        .then(() => this.retrievePrograms())
        .then(() => {
          this.$root.$emit('alert-message', this.$t('programs.programDeleteSuccess'), 'success');
          this.$root.$emit('program-deleted', this.selectedProgram);
        })
        .catch(() => this.$root.$emit('alert-message', this.$t('programs.programDeleteError'), 'success'))
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
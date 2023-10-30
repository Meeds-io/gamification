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
  <div id="engagementCenterProgramDetail" class="px-4">
    <div v-if="!isDeleted">
      <div class="py-2 py-sm-5 d-flex align-center">
        <v-tooltip :disabled="$root.isMobile" bottom>
          <template #activator="{ on }">
            <v-card
              class="d-flex align-center"
              flat
              v-on="on"
              @click="backToProgramList">
              <v-btn
                class="width-auto ms-n3"
                icon>
                <v-icon size="18" class="icon-default-color mx-2">fa-arrow-left</v-icon>
              </v-btn>
              <div class="text-header-title"> {{ programTitle }}</div>
            </v-card>
          </template>
          <span>{{ $t('programs.details.label.BackToList') }}</span>
        </v-tooltip>
        <v-spacer />
        <span class="text-header-title d-none d-sm-block" v-sanitized-html="$t('programs.budget', $t(programBudgetLabel))"></span>
      </div>
      <div class="position-relative">
        <engagement-center-card-mask v-if="!program.enabled" class="z-index-one rounded">
          <engagement-center-program-disabled-mask-content
            :is-administrator="isAdministrator"
            :program="program" />
        </engagement-center-card-mask>
        <div class="d-flex flex-column flex-grow-1">
          <div class="position-relative d-flex flex-column align-end full-width">
            <div class="position-absolute z-index-one">
              <engagement-center-program-menu
                :is-administrator="isAdministrator"
                :program="program" />
            </div>
          </div>
          <v-avatar
            :style="programStyle"
            class="d-flex align-center justify-center flex-grow-1 overflow-hidden border-color rounded"
            height="auto"
            max-height="225"
            min-width="100%"
            max-width="100%"
            tile>
            <img
              id="engagementCenterCoverImage"
              :src="programCover"
              :alt="$t('programs.cover.default')"
              height="auto"
              width="100%"
              class="full-width"
              role="presentation">
          </v-avatar>
        </div>
        <div class="d-sm-flex">
          <div class="me-auto pe-sm-6 pt-5">
            <v-list-item two-line class="px-0">
              <v-list-item-content class="pa-0">
                <div class="text-subtitle-1 dark-grey-color font-weight-bold mb-0">
                  {{ $t('programs.details.label.description') }}
                </div>
                <v-list-item-subtitle class="text-color pt-2">
                  <div class="d-flex flex-grow-0 flex-shrink-1 pb-sm-5 rich-editor-content">
                    <span
                      class="mt-1 align-self-center text-wrap text-left text-break"
                      v-sanitized-html="programDescription"></span>
                  </div>
                </v-list-item-subtitle>
              </v-list-item-content>
            </v-list-item>
          </div>
          <div class="d-flex flex-column pt-sm-5 px-0 col-sm-3">
            <div class="dark-grey-color text-subtitle-1 font-weight-bold width-fit-content ms-sm-auto">
              {{ $t('programs.details.label.programOwners') }}
            </div>
            <div v-if="owners.length">
              <engagement-center-avatars-list
                :avatars="owners"
                :max-avatars-to-show="3"
                :avatars-count="ownersCount"
                :size="25"
                class="d-flex justify-sm-end pt-2"
                @open-avatars-drawer="$root.$emit('open-owners-drawer', owners)" />
              <template v-if="space">
                <div class="dark-grey-color text-subtitle-1 font-weight-bold pt-3 width-fit-content ms-sm-auto">
                  {{ $t('programs.details.label.audienceSpace') }}
                </div>
                <exo-space-avatar
                  :space="space"
                  :size="32"
                  class="d-flex justify-sm-end pt-2"
                  popover />
              </template>
            </div>
            <v-chip v-else class="ms-sm-auto mt-2">
              {{ $t('programs.label.rewardAdmins') }}
            </v-chip>
          </div>
        </div>
      </div>
      <div class="pt-5">
        <v-list-item two-line class="px-0">
          <v-list-item-content>
            <span class="text-header-title subtitle-1 d-sm-none mb-5" v-sanitized-html="$t('programs.budget', $t(programBudgetLabel))"></span>
            <v-list-item-title class="dark-grey-color font-weight-bold">
              {{ $t('programs.details.label.rulesOfProgram') }}
            </v-list-item-title>
            <div v-if="newlyCreated" class="d-flex flex-column align-center justify-center">
              <span class="subtitle-1 pt-14">
                {{ $t('programs.details.label.newProgramIntroduction') }}
              </span>
              <span class="py-10">
                <v-btn
                  color="primary"
                  depressed
                  large
                  @click="$root.$emit('rule-form-drawer', null, program)">
                  <v-icon class="font-weight-bold" dark>
                    mdi-plus
                  </v-icon>
                  <span class="ms-2 d-none d-lg-inline title font-weight-bold">
                    {{ $t('programs.details.rule.button.addIncentive') }}
                  </span>
                </v-btn>
              </span>
              <div class="pb-12 subtitle-1 text-wrap">
                <div class="d-flex align-center">
                  <v-card
                    class="d-flex justify-center me-3"
                    min-width="50"
                    flat>
                    <v-icon color="primary" size="30">fa-trophy</v-icon>
                  </v-card>
                  {{ $t('programs.details.label.manualActionIntroduction') }}
                </div>
                <div class="d-flex align-center pt-4">
                  <v-card
                    class="d-flex justify-center me-3"
                    min-width="50"
                    flat>
                    <v-icon color="primary" size="30">fa-award</v-icon>
                  </v-card>
                  {{ $t('programs.details.label.automaticActionIntroduction') }}
                </div>
              </div>
            </div>
            <template v-else>
              <engagement-center-program-rules-toolbar
                :can-manage-rule="canManageRule"
                :keyword="keyword"
                :program="program"
                @keyword-changed="keyword = $event"
                @filter-changed="updateFilter" />
              <v-list-item-subtitle class="text-color pt-4">
                <v-data-table
                  :headers="rulesHeaders"
                  :items="programRulesToDisplay"
                  :options.sync="options"
                  :server-items-length="totalSize"
                  :show-rows-border="false"
                  mobile-breakpoint="0"
                  hide-default-footer
                  disable-sort>
                  <template slot="item" slot-scope="props">
                    <engagement-center-program-rule-item
                      :rule="props.item"
                      :can-manage-rule="canManageRule" />
                  </template>
                  <template slot="no-data">
                    <engagement-center-program-no-rule-found
                      v-if="keyword"
                      @keyword-changed="keyword = $event" />
                    <span v-else>
                      {{ $t('programs.details.rules.noRules') }}
                    </span>
                  </template>
                  <template v-if="displayFooter" #footer="{props}">
                    <v-divider />
                    <div class="text-center">
                      <v-pagination
                        v-model="options.page"
                        :length="props.pagination.pageCount"
                        circle
                        light
                        flat
                        @input="retrieveProgramRules" />
                    </div>
                  </template>
                </v-data-table>
              </v-list-item-subtitle>
            </template>
          </v-list-item-content>
        </v-list-item>
      </div>
    </div>
    <engagement-center-result-not-found
      v-else
      :message-title="$t('programs.details.programDeleted')"
      :button-text="$t('programs.details.programDeleted.explore') "
      :button-url="programsUrl"
      @back-to-list="backToProgramList" />
  </div>
</template>

<script>
export default {
  props: {
    program: {
      type: Object,
      default: null
    },
    tab: {
      type: Number,
      default: () => 0,
    },
    newlyCreated: {
      type: Boolean,
      default: false,
    },
    isAdministrator: {
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {
      displayProgramDetail: false,
      programRules: [],
      options: {
        page: 1,
        itemsPerPage: 25,
      },
      startSearchAfterInMilliseconds: 600,
      endTypingKeywordTimeout: 50,
      startTypingKeywordTimeout: 0,
      totalSize: 0,
      loadingRules: false,
      deleteConfirmMessage: '',
      dateFilter: 'STARTED',
      status: 'ENABLED',
      keyword: null,
      realizationsLimit: 3,
      programsUrl: `${eXo.env.portal.context}/${eXo.env.portal.engagementSiteName}/contributions/programs`,
    };
  },
  computed: {
    programRulesToDisplay() {
      return this.programRules.filter(rule => !rule.deleted);
    },
    programTitle() {
      return this.program?.title;
    },
    programBudget() {
      return this.program?.rulesTotalScore || 0;
    },
    programBudgetLabel() {
      return {0: `<span class="font-weight-bold success-color">${this.programBudget} ${this.$t('programs.details.label.points')}</span>`};
    },
    programCover() {
      return this.program?.coverUrl || '';
    },
    programDescription() {
      return this.program?.description || '';
    },
    programId() {
      return this.program?.id;
    },
    spaceId() {
      return this.program?.space?.id;
    },
    isDeleted() {
      return this.program?.deleted;
    },
    rulesHeaders() {
      return [
        {text: this.$t('programs.details.rules.action'), align: 'start', width: '70%'},
        {text: '', align: 'center', width: '15%'},
        {text: this.$t('programs.details.rules.points'), align: 'center', width: '15%', enabled: !this.isMobile},
        {text: this.$t('gamification.overview.label.participations'), align: 'center', width: '25%', enabled: !this.isMobile}].filter(filter => filter.enabled == null || filter.enabled === true);
    },
    displayFooter() {
      return this.totalSize > this.options.itemsPerPage;
    },
    canManageRule() {
      return this.isAdministrator || this.program?.userInfo?.canEdit;
    },
    height() {
      return this.isMobile ? 70 : 173;
    },
    isMobile() {
      return this.$vuetify.breakpoint.xsOnly;
    },
    space() {
      return this.program?.space;
    },
    spaceManagers() {
      return this.space?.managers;
    },
    spaceManagersList() {
      return (this.spaceManagers || []).map(owner => ({
        userName: owner
      }));
    },
    addedOwners() {
      return (this.program?.owners || []).filter(owner => !this.program?.space || !this.program?.space?.managers.includes(owner.remoteId))
        .map(owner => ({
          userName: owner.remoteId
        }));
    },
    owners() {
      return this.addedOwners.concat(this.spaceManagersList);
    },
    ownersCount() {
      return this.owners?.length;
    },
    programStyle() {
      return this.program?.color && `border: 1px solid ${this.program.color} !important;` || '';
    },
  },
  watch: {
    program() {
      this.retrieveProgramRules();
    },
    options() {
      this.retrieveProgramRules();
    },
    loadingRules() {
      if (this.loadingRules) {
        document.dispatchEvent(new CustomEvent('displayTopBarLoading'));
      } else {
        document.dispatchEvent(new CustomEvent('hideTopBarLoading'));
      }
    },
    keyword() {
      if (!this.keyword) {
        this.retrieveProgramRules();
        return;
      }
      this.startTypingKeywordTimeout = Date.now();
      if (!this.typing) {
        this.typing = true;
        this.waitForEndTyping();
      }
    },
  },
  created() {
    this.$root.$on('program-deleted', this.backToProgramList);
    this.$root.$on('program-updated', this.programUpdated);
    this.$root.$on('rule-created', this.retrieveProgramRules);
    this.$root.$on('rule-updated', this.retrieveProgramRules);
    this.$root.$on('rule-deleted', this.retrieveProgramRules);
    this.$root.$on('announcement-added', this.retrieveProgramRules);
    window.addEventListener('popstate', this.backToProgramList);
  },
  mounted() {
    if (this.programId) {
      document.dispatchEvent(new CustomEvent('exo-statistic-message', {
        detail: {
          module: 'gamification',
          subModule: 'program',
          userId: eXo.env.portal.userIdentityId,
          userName: eXo.env.portal.userName,
          spaceId: this.spaceId || 0,
          operation: 'viewProgram',
          timestamp: Date.now(),
          parameters: {
            programId: this.programId,
            programTitle: this.programTitle,
            programBudget: this.programBudget,
            programType: this.program.type,
            portalName: eXo.env.portal.portalName,
            portalUri: eXo.env.server.portalBaseURL,
            pageUrl: window.location.pathname,
            pageTitle: eXo.env.portal.pageTitle,
            pageUri: eXo.env.portal.selectedNodeUri,
          },
        }
      }));
    }
  },
  beforeDestroy() {
    this.$root.$off('program-deleted', this.backToProgramList);
    this.$root.$off('program-updated', this.programUpdated);
    this.$root.$off('rule-created', this.retrieveProgramRules);
    this.$root.$off('rule-updated', this.retrieveProgramRules);
    this.$root.$off('rule-deleted', this.retrieveProgramRules);
    this.$root.$off('announcement-added', this.retrieveProgramRules);
    window.removeEventListener('popstate', this.backToProgramList);
  },
  methods: {
    programUpdated(program) {
      if (program.id === this.program.id) {
        this.program = program;
      }
    },
    updateFilter(status, dateFilter) {
      this.status = status;
      this.dateFilter = dateFilter;
      this.retrieveProgramRules();
    },
    retrieveProgramRules() {
      const page = this.options?.page || 0;
      const itemsPerPage = this.options?.itemsPerPage || 10;
      const offset = (page - 1) * itemsPerPage;
      this.loadingRules = true;
      return this.$ruleService.getRules({
        term: this.keyword,
        programId: this.programId,
        status: this.status,
        programStatus: 'ALL',
        dateFilter: this.dateFilter,
        offset,
        limit: itemsPerPage,
        realizationsLimit: this.realizationsLimit,
        returnSize: true,
        lang: eXo.env.portal.language,
      })
        .then((data) => {
          this.programRules = data.rules || [];
          this.programRules.forEach(rule => rule.program = this.program);
          this.totalSize = data.size || 0;
          return this.$nextTick();
        })
        .finally(() => this.loadingRules = false);
    },
    backToProgramList() {
      this.options.page = 1;
      this.options.itemsPerPage = 10;
      this.$root.$emit('close-program-detail');
      if (this.tab === 0) {
        window.history.replaceState('Engagement Center', this.$t('engagementCenter.label.programs'), `${eXo.env.portal.context}/${eXo.env.portal.portalName}/contributions/programs`);
      } else if (this.tab === 2) {
        window.history.replaceState('Engagement Center', this.$t('engagementCenter.label.achievements'), `${eXo.env.portal.context}/${eXo.env.portal.portalName}/contributions/achievements`);
      }
    },
    waitForEndTyping() {
      window.setTimeout(() => {
        if (Date.now() - this.startTypingKeywordTimeout > this.startSearchAfterInMilliseconds) {
          this.typing = false;
          this.retrieveProgramRules();
        } else {
          this.waitForEndTyping();
        }
      }, this.endTypingKeywordTimeout);
    },
  }
};
</script>
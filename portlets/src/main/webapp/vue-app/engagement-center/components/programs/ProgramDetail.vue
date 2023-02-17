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
  <div id="engagementCenterProgramDetail" class="pa-2 pa-sm-5">
    <div v-if="!isDeleted">
      <div class="py-2 py-sm-5 d-flex">
        <v-tooltip bottom>
          <template #activator="{ on }">
            <div
              v-on="on"
              class="d-flex my-auto clickable"
              @click="backToProgramList()">
              <v-icon
                class="px-3"
                size="18">
                fas fa-arrow-left
              </v-icon>
              <div class="text-header-title"> {{ programTitle }} </div>
            </div>
          </template>
          <span>{{ $t('programs.details.label.BackToList') }}</span>
        </v-tooltip>
        <v-spacer />
        <span class="text-header-title d-none d-sm-block" v-sanitized-html="$t('programs.budget', $t(programBudgetLabel))"></span>
      </div>
      <div class="d-flex flex-grow-1">
        <v-img
          :src="programCover"
          :alt="$t('programs.cover.default')"
          :min-height="36"
          :max-height="height"
          height="auto"
          min-width="100%"
          width="100%"
          class="d-flex primary--text border-color">
          <engagement-center-program-menu :is-administrator="isAdministrator" :program="program" />
        </v-img>
      </div>
      <div class="d-sm-flex">
        <div class="me-auto pe-sm-6 pt-5">
          <v-list-item two-line class="px-0">
            <v-list-item-content class="pa-0">
              <div class="text-subtitle-1 text-color font-weight-bold mb-0">
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
        <div class="pt-sm-5 px-0 col-sm-3">
          <div class="text-color text-subtitle-1 font-weight-bold width-fit-content ms-sm-auto">
            {{ $t('programs.details.label.programOwners') }}
          </div>
          <engagement-center-avatars-list
            :avatars="owners"
            :max-avatars-to-show="3"
            :avatars-count="ownersCount"
            :size="25"
            class="justify-sm-end pt-2"
            @open-avatars-drawer="$root.$emit('open-owners-drawer', owners)" />
          <div class="text-color text-subtitle-1 font-weight-bold pt-3 width-fit-content ms-sm-auto">
            {{ $t('programs.details.label.audienceSpace') }}
          </div>
          <exo-space-avatar
            :space="space"
            :size="32"
            class="d-flex justify-sm-end pt-2"
            popover />
        </div>
      </div>
      <div class="pt-5">
        <v-list-item two-line class="px-0">
          <v-list-item-content>
            <span class="text-header-title subtitle-1 d-sm-none mb-5" v-sanitized-html="$t('programs.budget', $t(programBudgetLabel))"></span>
            <v-list-item-title class="text-color font-weight-bold">
              {{ $t('programs.details.label.rulesOfProgram') }}
            </v-list-item-title>
            <v-flex v-if="canManageRule" class="d-flex  mt-5">
              <div class="text-no-wrap mt-sm-3">
                <div
                  class="d-inline-block">
                  <v-btn
                    class="btn btn-primary"
                    small
                    @click="openNewRuleForm">
                    <v-icon dark>
                      mdi-plus
                    </v-icon>
                    <span class="ms-2 d-none d-lg-inline subtitle-1">
                      {{ $t('programs.details.rule.button.addRule') }}
                    </span>
                  </v-btn>
                </div>
              </div>
              <div
                class="d-flex ms-auto">
                <engagement-center-rule-filter @filter-applied="applyFilter" />
              </div>
            </v-flex>
            <v-list-item-subtitle class="text-color pt-4">
              <v-data-table
                :headers="rulesHeaders"
                :items="programRulesToDisplay"
                :options.sync="options"
                :server-items-length="totalSize"
                :no-data-text="$t('programs.details.rules.noRules')"
                :loading="loadingRules"
                :show-rows-border="false"
                mobile-breakpoint="0"
                hide-default-footer
                disable-sort>
                <template slot="item" slot-scope="props">
                  <engagement-center-rule-item
                    :rule="props.item"
                    :can-manage-rule="canManageRule"
                    :action-value-extensions="actionValueExtensions"
                    @delete-rule="confirmDelete" />
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
          </v-list-item-content>
        </v-list-item>
        <engagement-center-rule-form-drawer
          :events="events"
          :program="program" />
        <exo-confirm-dialog
          v-if="confirmDelete"
          ref="deleteRuleConfirmDialog"
          :message="deleteConfirmMessage"
          :title="$t('programs.details.title.confirmDeleteRule')"
          :ok-label="$t('programs.details.ok.button')"
          :cancel-label="$t('programs.details.cancel.button')"
          @ok="deleteRule" />
      </div>
    </div>
    <engagement-center-program-deleted v-else @back-to-list="backToProgramList" />
  </div>
</template>

<script>
export default {
  props: {
    program: {
      type: Object,
      default: null
    },
    events: {
      type: Array,
      default: () => [],
    },
    tab: {
      type: Number,
      default: () => 0,
    },
    isAdministrator: {
      type: Boolean,
      default: false,
    },
    actionValueExtensions: {
      type: Object,
      default: function() {
        return null;
      },
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
      totalSize: 0,
      loadingRules: false,
      deleteConfirmMessage: '',
      filter: 'ENABLED',
      expand: 'userAnnouncements'
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
        {text: this.$t('gamification.overview.label.participants'), align: 'center', width: '25%', enabled: !this.isMobile}].filter(filter => filter.enabled == null || filter.enabled === true);
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
      return (this.program?.owners || []).filter(owner => owner.domainOwner && !this.program?.space?.managers.includes(owner.remoteId)).map(owner => ({
        userName: owner.remoteId
      }));
    },
    owners() {
      return this.addedOwners.concat(this.spaceManagersList);
    },
    ownersCount() {
      return this.owners?.length;
    }
  },
  watch: {
    program() {
      this.retrieveProgramRules();
    },
    options() {
      this.retrieveProgramRules();
    },
  },
  created() {
    this.$root.$on('challenge-delete-confirm', this.confirmDelete);
    this.$root.$on('program-rules-refresh', this.retrieveProgramRules);
    this.$root.$on('program-deleted', this.backToProgramList);
    this.$root.$on('program-updated', this.programUpdated);
    window.addEventListener('popstate', () => {
      this.backToProgramList();
    });
  },
  methods: {
    programUpdated(program) {
      if (program.id === this.program.id) {
        this.program = program;
      }
    },
    applyFilter(filter) {
      this.filter = filter;
      this.retrieveProgramRules();
    },
    retrieveProgramRules() {
      const page = this.options && this.options.page;
      let itemsPerPage = this.options && this.options.itemsPerPage;
      if (itemsPerPage <= 0) {
        itemsPerPage = this.totalSize || 10;
      }
      const offset = (page - 1) * itemsPerPage;
      this.loadingRules = true;
      return this.$ruleServices.getRules(null, this.programId, this.filter, 'ALL', offset, itemsPerPage, this.expand)
        .then((data) => {
          this.programRules = data.rules;
          this.totalSize = data.size || 0;
          return this.$nextTick();
        })
        .finally(() => {
          this.loadingRules = false;
        });
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
    openNewRuleForm(){
      this.$root.$emit('rule-form-drawer', null);
    },
    confirmDelete(rule) {
      this.selectedRule = rule;
      this.deleteConfirmMessage = this.$t('programs.details.message.confirmDeleteRule', {0: this.ruleTitle(this.selectedRule)});
      this.$refs.deleteRuleConfirmDialog.open();
    },
    deleteRule() {
      this.loading = true;
      this.$ruleServices.deleteRule(this.selectedRule.id)
        .then((deletedRule) => {
          this.$root.$emit('program-rule-deleted', deletedRule);
          this.$engagementCenterUtils.displayAlert(this.$t('programs.details.ruleDeleteSuccess'));
          this.retrieveProgramRules();
        })
        .finally(() => this.loading = false);
    },
    ruleTitle(rule) {
      let fieldLabelI18NKey = `exoplatform.gamification.gamificationinformation.rule.title.${rule?.title}`;
      let fieldLabelI18NValue = this.$t(fieldLabelI18NKey);
      if (fieldLabelI18NValue === fieldLabelI18NKey) {
        fieldLabelI18NKey = `exoplatform.gamification.gamificationinformation.rule.title.def_${rule?.title}`;
        fieldLabelI18NValue = this.$t(fieldLabelI18NKey);
      }
      return fieldLabelI18NValue === fieldLabelI18NKey ? rule?.title : fieldLabelI18NValue;
    }
  }
};
</script>
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
  <div id="engagementCenterProgramDetail" class="pa-5">
    <div class="py-5">
      <v-btn icon class="mb-1">
        <v-icon
          size="18"
          @click="backToProgramList()">
          fas fa-arrow-left
        </v-icon>
      </v-btn>
      <span class="text-header-title ps-1"> {{ programTitle }} : </span><span v-sanitized-html="$t('programs.budget', $t(programBudgetLabel))"></span>
    </div>
    <div class="d-flex flex-grow-1">
      <v-img
        :src="programCover"
        :alt="$t('programs.cover.default')"
        aspect-ratio="1"
        width="100%"
        min-height="70"
        min-width="70"
        max-height="140"
        class="primary--text border-color" />
    </div>
    <div class="pt-5">
      <v-list-item two-line class="px-0">
        <v-list-item-content>
          <v-list-item-title class="text-color font-weight-bold">
            {{ $t('programs.details.label.description') }}
          </v-list-item-title>
          <v-list-item-subtitle class="text-color pt-4">
            <div class="d-flex flex-grow-0 flex-shrink-1 pb-5">
              <span
                class="mt-1 align-self-center text-wrap text-left text-break"
                v-sanitized-html="programDescription"></span>
            </div>
          </v-list-item-subtitle>
        </v-list-item-content>
      </v-list-item>
    </div>
    <div class="pt-5">
      <v-list-item two-line class="px-0">
        <v-list-item-content>
          <v-list-item-title class="text-color font-weight-bold">
            {{ $t('programs.details.label.rulesOfProgram') }}
          </v-list-item-title>
          <div v-if="canManageRule" class="text-no-wrap mt-5">
            <div
              class="d-inline-block">
              <v-btn
                class="btn btn-primary"
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
          <v-list-item-subtitle class="text-color pt-4">
            <v-data-table
              :headers="rulesHeaders"
              :items="programRulesToDisplay"
              :options.sync="options"
              :server-items-length="totalSize"
              :no-data-text="$t('programs.details.rules.noRules')"
              :loading="loadingRules"
              hide-default-footer
              disable-sort>
              <template slot="item" slot-scope="props">
                <engagement-center-rule-item
                  :rule="props.item"
                  :can-manage-rule="canManageRule"
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
    canManageRule: {
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
        itemsPerPage: 10,
      },
      totalSize: 0,
      loadingRules: false,
      deleteConfirmMessage: ''
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
    rulesHeaders() {
      return [
        {text: this.$t('programs.details.rules.action'), align: 'start'},
        {text: this.$t('programs.details.rules.type'), align: 'center'},
        {text: this.$t('programs.details.rules.points'), align: 'center'},
        {text: '', align: 'center', enabled: this.canManageRule},
      ].filter(filter => filter.enabled == null || filter.enabled === true);
    },
    displayFooter() {
      return this.totalSize > this.options.itemsPerPage;
    },
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
    window.addEventListener('popstate', () => {
      this.backToProgramList();
    });
  },
  methods: {
    retrieveProgramRules() {
      const page = this.options && this.options.page;
      let itemsPerPage = this.options && this.options.itemsPerPage;
      if (itemsPerPage <= 0) {
        itemsPerPage = this.totalSize || 10;
      }
      const offset = (page - 1) * itemsPerPage;
      this.loadingRules = true;
      return this.$ruleServices.getRules(null, this.programId, 'ENABLED', 'ALL', offset, itemsPerPage)
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
      window.history.replaceState('programs', this.$t('engagementCenter.label.programs'), `${eXo.env.portal.context}/${eXo.env.portal.portalName}/contributions/programs/`);
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
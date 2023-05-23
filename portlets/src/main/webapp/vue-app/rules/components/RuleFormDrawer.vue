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
  <exo-drawer
    ref="ruleFormDrawer"
    v-model="drawer"
    body-classes="hide-scroll decrease-z-index-more"
    class="EngagementCenterDrawer"
    right
    allow-expand
    @opened="stepper = 1"
    @closed="clear">
    <template slot="title">
      {{ drawerTitle }}
    </template>
    <template v-if="drawer" slot="content">
      <v-stepper
        v-model="stepper"
        vertical
        flat
        class="ma-0 py-0 me-4">
        <v-stepper-step
          :complete="stepper > 1"
          step="1"
          class="ma-0">
          <span class="font-weight-bold dark-grey-color text-subtitle-1">{{ $t('rule.form.label.stepOne') }}</span>
        </v-stepper-step>
        <v-stepper-content step="1" class="pe-0 mx-0 py-0">
          <v-form
            ref="RuleForm"
            v-model="isValidForm"
            class="form-horizontal pt-0 pb-4"
            flat
            @submit="saveRule">
            <v-card-text class="d-flex flex-grow-1 text-left text-subtitle-1 px-0 py-2">
              {{ $t('rule.form.label.program') }}
            </v-card-text>
            <v-card-text class="d-flex pa-0">
              <v-img
                :src="programCover"
                :height="programCoverSize"
                :width="programCoverSize"
                :max-height="programCoverSize"
                :max-width="programCoverSize" /><span class="my-auto ms-3">{{ programTitle }}</span>
            </v-card-text>
            <v-card-text class="d-flex flex-grow-1 text-no-wrap text-left text-subtitle-1 px-0 pb-1">
              {{ $t('rule.form.label.rules') }}
            </v-card-text>
            <v-card-text class="d-flex pa-0">
              <input
                id="ruleTitle"
                ref="ruleTitle"
                v-model="rule.title"
                :placeholder="$t('rule.form.label.rules.placeholder')"
                type="text"
                maxlength="50"
                class="ignore-vuetify-classes flex-grow-1"
                required>
            </v-card-text>
            <v-card-text class="pa-0">
              <engagement-center-description-editor
                id="ruleDescription"
                ref="ruleDescription"
                v-model="rule.description"
                :label="$t('rule.form.label.description')"
                :placeholder="$t('rule.form.label.description.placeholder')"
                :max-length="500"
                :visible="drawer"
                ck-editor-type="rule"
                @addDescription="addDescription($event)"
                @validity-updated=" validDescription = $event" />
            </v-card-text>
            <v-card-text class="d-flex flex-grow-1 text-no-wrap text-left text-subtitle-1 px-0 pb-2">
              {{ $t('rule.form.label.rewards') }}
            </v-card-text>
            <v-card
              flat
              width="120"
              class="d-flex flex-grow-1">
              <v-text-field
                v-model="rule.score"
                :rules="scoreRules"
                class="mt-0 pt-0 me-2"
                type="number"
                hide-details
                outlined
                dense
                required />
              <label class="my-auto">{{ $t('rule.form.label.points') }}</label>
            </v-card>
            <v-card-text class="d-flex flex-grow-1 text-no-wrap text-left text-subtitle-1 px-0 pb-2">
              {{ $t('rule.form.label.type') }}
            </v-card-text>
            <div class="d-flex flex-row pb-4">
              <v-btn
                class="btn me-2 not-clickable"
                :class="automaticType && 'btn-primary'"
                @click="setAutomatic">
                {{ $t('rule.form.label.type.automatic') }}
              </v-btn>
              <v-btn
                class="btn not-clickable"
                :class="manualType && 'btn-primary'"
                @click="setManual">
                {{ $t('rule.form.label.type.declarative') }}
              </v-btn>
            </div>
            <div v-if="automaticType">
              <v-card-text class="d-flex flex-grow-1 text-no-wrap text-left text-subtitle-1 pb-2">
                {{ $t('rule.form.label.selectEvent') }}
              </v-card-text>
              <v-card-text class="py-0">
                <v-autocomplete
                  id="EventSelectAutoComplete"
                  ref="EventSelectAutoComplete"
                  v-model="value"
                  :placeholder="$t('rule.form.label.selectEvent.placeholder')"
                  :items="eventNames"
                  item-text="label"
                  class="pa-0"
                  filled
                  persistent-hint
                  dense>
                  <template #selection="data">
                    <v-chip
                      v-bind="data.attrs"
                      :input-value="data.selected"
                      :title="data.item && data.item.label || data.item"
                      @click="data.select">
                      {{ data.item && data.item.label || data.item }}
                    </v-chip>
                  </template>
                  <template #item="data">
                    <v-list-item-content v-text="data.item.label" />
                  </template>
                </v-autocomplete>
              </v-card-text>
              <v-card-text v-if="eventExist" class="error--text py-0">
                {{ $t('rule.form.error.sameEventExistsInProgram') }}
              </v-card-text>
            </div>
            <div v-if="ruleId">
              <v-card-text class="d-flex flex-grow-1 text-no-wrap text-left text-subtitle-1 pb-2">
                {{ $t('rule.form.label.status') }}
              </v-card-text>
              <div class="d-flex flex-row">
                <label class="subtitle-1 text-light-color mt-1 pe-3">{{ $t('rule.form.label.enabled') }}</label>
                <v-switch
                  id="allowAttendeeToUpdateRef"
                  ref="allowAttendeeToUpdateRef"
                  v-model="rule.enabled"
                  class="mt-0 ms-4" />
              </div>
            </div>
          </v-form>
        </v-stepper-content>
        <v-stepper-step
          :complete="stepper > 2"
          step="2"
          class="ma-0">
          <span class="font-weight-bold dark-grey-color text-subtitle-1">{{ $t('rule.form.label.stepTwo') }}</span>
        </v-stepper-step>
        <v-stepper-content step="2" class="pe-0 ma-0 py-0">
          <div class="ps-7">
            <v-chip
              class="ma-2"
              :color="durationCondition && 'primary' || ''"
              :outlined="!durationCondition"
              :dark="durationCondition"
              @click="updateDateCondition">
              {{ $t('rule.form.label.duration') }}
            </v-chip>
            <v-chip
              class="ma-2"
              :color="recurrenceCondition && 'primary' || ''"
              :outlined="!recurrenceCondition"
              :dark="recurrenceCondition"
              @click="updateRecurrenceCondition">
              {{ $t('rule.form.label.recurrence') }}
            </v-chip>
            <v-tooltip :disabled="$root.isMobile" bottom>
              <template #activator="{ on, attrs }">
                <v-chip
                  class="ma-2"
                  :color="prerequisiteRuleCondition && 'primary' || ''"
                  :outlined="!prerequisiteRuleCondition"
                  :dark="prerequisiteRuleCondition"
                  v-bind="attrs"
                  v-on="on"
                  @click="updatePrerequisiteRuleCondition">
                  {{ $t('rule.form.label.action') }}
                </v-chip>
              </template>
              <span>{{ $t('rule.form.label.actionTooltip') }}</span>
            </v-tooltip>
          </div>
          <div v-if="durationCondition">
            <engagement-center-rule-dates-input
              v-model="validDatesInput"
              :start-date.sync="rule.startDate"
              :end-date.sync="rule.endDate" />
          </div>
          <div v-if="recurrenceCondition">
            <engagement-center-rule-recurrence-input
              v-model="rule.recurrence" />
          </div>
          <div v-if="prerequisiteRuleCondition">
            <engagement-center-rule-lock-input
              v-model="rule.prerequisiteRules"
              :program-id="programId"
              :excluded-ids="excludedRuleIds" />
          </div>
        </v-stepper-content>
      </v-stepper>
    </template>
    <template slot="footer">
      <div class="d-flex">
        <v-spacer />
        <v-btn
          v-if="stepper === 2"
          class="btn me-2"
          @click="previousStep">
          {{ $t('rule.form.label.button.back') }}
        </v-btn>
        <v-btn
          v-else
          class="btn me-2"
          @click="close">
          {{ $t('rule.form.label.button.cancel') }}
        </v-btn>
        <v-btn
          v-if="stepper === 1"
          :disabled="disableNextButton"
          class="btn btn-primary"
          @click="nextStep">
          {{ $t('rule.form.label.button.next') }}
        </v-btn>
        <v-btn
          v-else
          :disabled="disableSaveButton"
          class="btn btn-primary"
          @click="saveRule">
          {{ saveButtonLabel }}
        </v-btn>
      </div>
    </template>
  </exo-drawer>
</template>

<script>
export default {
  props: {
    program: {
      type: Object,
      default: () => null,
    },
    events: {
      type: Array,
      default: function() {
        return [];
      },
    },
  },
  data: () => ({
    rule: {},
    ruleToUpdate: {},
    saving: false,
    eventMapping: [],
    value: '',
    eventExist: false,
    validDescription: false,
    validEvent: false,
    stepper: 0,
    programCoverSize: 40,
    isValidForm: true,
    drawer: false,
    scoreRules: [
      v => ( v && v <= 10000 ),
    ],
    durationCondition: false,
    recurrenceCondition: false,
    prerequisiteRuleCondition: false,
    validDatesInput: false,
    programEvents: [],
  }),
  computed: {
    eventNames() {
      this.events.filter(event => event != null).forEach(event => {
        const eventObject = {};
        eventObject.name = event;
        let fieldLabelI18NKey = `exoplatform.gamification.gamificationinformation.rule.title.${event}`;
        let fieldLabelI18NValue = this.$t(fieldLabelI18NKey);
        if (fieldLabelI18NValue === fieldLabelI18NKey) {
          fieldLabelI18NKey = `exoplatform.gamification.gamificationinformation.rule.title.def_${event}`;
          fieldLabelI18NValue = this.$t(fieldLabelI18NKey);
        }
        eventObject.label = fieldLabelI18NValue === fieldLabelI18NKey ? event : fieldLabelI18NValue;
        this.eventMapping.push(eventObject);
      });
      return this.eventMapping;
    },
    programCover() {
      return this.program?.coverUrl || '';
    },
    programTitle() {
      return this.program?.title;
    },
    programId() {
      return this.program?.id;
    },
    ruleTitle() {
      return this.rule?.title;
    },
    ruleId() {
      return this.rule?.id;
    },
    excludedRuleIds() {
      return this.ruleId && [this.ruleId] || [];
    },
    ruleTitleValid() {
      return this.ruleTitle?.length > 0;
    },
    ruleTypeValid() {
      return this.manualType || (this.automaticType && this.validEvent);
    },
    ruleStartDate() {
      return this.rule?.startDate;
    },
    ruleEndDate() {
      return this.rule?.endDate;
    },
    durationValid() {
      return !this.durationCondition || this.validDatesInput;
    },
    recurrenceValid() {
      return !this.recurrenceCondition || this.rule.recurrence;
    },
    prerequisiteRuleValid() {
      return !this.prerequisiteRuleCondition || this.rule.prerequisiteRules?.length;
    },
    disableNextButton() {
      return this.saving || this.eventExist || !this.ruleTitleValid || !this.validDescription || !this.ruleTypeValid || !this.isValidForm;
    },
    disableSaveButton() {
      return this.saving || this.eventExist || !this.ruleTitleValid || !this.validDescription || !this.ruleTypeValid || !this.durationValid || !this.recurrenceValid || !this.prerequisiteRuleValid || !this.isValidForm;
    },
    drawerTitle() {
      return this.ruleId ? this.$t('rule.form.label.edit') : this.$t('rule.form.label.add');
    },
    saveButtonLabel() {
      return this.ruleId ? this.$t('rule.form.label.button.update') : this.$t('rule.form.label.button.add');
    },
    ruleType() {
      return this.rule?.type;
    },
    automaticType() {
      return this.ruleType === 'AUTOMATIC';
    },
    manualType() {
      return this.ruleType === 'MANUAL';
    },
  },
  watch: {
    value: {
      immediate: true,
      handler() {
        this.emitSelectedValue(this.value);
        this.validEvent = this.value && this.value !== '';
      }
    },
  },
  created() {
    this.$root.$on('rule-form-drawer', this.open);
  },
  methods: {
    open(rule) {
      this.rule = rule && JSON.parse(JSON.stringify(rule)) || {
        score: 20,
        enabled: true,
        area: this.programTitle
      };
      if (!this.program) {
        this.program = this.rule?.program;
      }
      this.durationCondition = this.rule.startDate || this.rule.endDate;
      this.recurrenceCondition = !!this.rule.recurrence;
      this.prerequisiteRuleCondition = this.rule.prerequisiteRules?.length;
      this.eventExist = false;
      if (this.$refs.ruleFormDrawer) {
        this.$refs.ruleFormDrawer.open();
      }
      window.setTimeout(() => {
        if (this.$refs.ruleTitle) {
          this.$refs.ruleTitle.focus();
          if (this.$refs.ruleDescription) {
            this.$refs.ruleDescription.initCKEditor();
          }
        }
      }, 200);
      this.$nextTick().then(() => {
        this.$root.$emit('rule-form-drawer-opened', this.rule);
        this.value = this.eventMapping.find(event => event.name === rule?.event) || '';
      });
      this.retrieveProgramRules();
    },
    close() {
      this.$refs.ruleDescription?.destroyCKEditor();
      this.$refs.ruleFormDrawer.close();
    },
    clear() {
      this.$refs.ruleDescription?.destroyCKEditor();
      this.stepper = 0;
      this.rule.enabled = true;
      this.rule.event = null;
      this.durationCondition = false;
      this.recurrenceCondition = false;
      this.prerequisiteRuleCondition = false;
      this.$set(this.rule,'title','');
      this.rule.description = '';
      this.value = {};
    },
    retrieveProgramRules() {
      return this.$ruleService.getRules({
        domainId: this.programId,
        status: 'ENABLED',
        type: 'AUTOMATIC',
        offset: 0,
        limit: this.events?.length || 10,
      })
        .then(data => this.programEvents = data && data.rules.map(rule => ({
          ruleId: rule.id,
          event: rule.event,
        })) || []);
    },
    emitSelectedValue(value) {
      const eventObject = this.eventMapping.find(event => event.label === value);
      if (eventObject) {
        this.$set(this.rule,'event', eventObject.name);
        this.eventExist = this.programEvents.find(programEvent => eventObject.name === programEvent.event && programEvent.ruleId !== this.rule?.id);
      }
    },
    addDescription(value) {
      if (value) {
        this.$set(this.rule,'description', value);
      }
    },
    saveRule() {
      this.saving = true;
      this.$refs.ruleFormDrawer.startLoading();
      if (this.rule.id) {
        this.$ruleService.updateRule(this.rule)
          .then(rule => {
            this.displayAlert(this.$t('programs.details.ruleUpdateSuccess'));
            this.$root.$emit('rule-updated', rule);
            this.close();
          })
          .catch(e => this.eventExist = e.message === '409')
          .finally(() => {
            this.saving = false;
            this.$refs.ruleFormDrawer.endLoading();
          });
      } else {
        this.$ruleService.createRule(this.rule, this.program)
          .then(rule => {
            this.displayAlert(this.$t('programs.details.ruleCreationSuccess'));
            this.$root.$emit('rule-created', rule);
            this.close();
          })
          .catch(e => this.eventExist = e.message === '409')
          .finally(() => {
            this.saving = false;
            this.$refs.ruleFormDrawer.endLoading();
          });
      }
    },
    setAutomatic() {
      this.$set(this.rule,'type', 'AUTOMATIC');
    },
    setManual() {
      this.$set(this.rule,'type', 'MANUAL');
    },
    updateDateCondition() {
      this.durationCondition = !this.durationCondition;
      this.$set(this.rule, 'startDate', null);
      this.$set(this.rule, 'endDate', null);
    },
    updateRecurrenceCondition() {
      this.recurrenceCondition = !this.recurrenceCondition;
      this.$set(this.rule,'recurrence', null);
    },
    updatePrerequisiteRuleCondition() {
      this.prerequisiteRuleCondition = !this.prerequisiteRuleCondition;
      this.$set(this.rule,'prerequisiteRules', []);
    },
    displayAlert(message, type) {
      document.dispatchEvent(new CustomEvent('notification-alert', {detail: {
        message,
        type: type || 'success',
      }}));
    },
    nextStep(event) {
      if (event) {
        event.preventDefault();
        event.stopPropagation();
      }
      this.stepper++;
    },
    previousStep(event) {
      if (event) {
        event.preventDefault();
        event.stopPropagation();
      }
      this.stepper--;
    },
  }
};
</script>

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
    body-classes="hide-scroll decrease-z-index-more"
    class="EngagementCenterDrawer"
    right
    allow-expand
    @opened="stepper = 1"
    @closed="stepper = 0">
    <template slot="title">
      {{ drawerTitle }}
    </template>
    <template slot="content">
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
            @submit="updateRule">
            <v-card-text class="d-flex flex-grow-1 text-left text-subtitle-1 px-0 py-2">
              {{ $t('rule.form.label.program') }}
            </v-card-text>
            <v-card-text class="d-flex pa-0">
              <v-img
                :src="program.coverUrl"
                :height="programCoverSize"
                :width="programCoverSize"
                :max-height="programCoverSize"
                :max-width="programCoverSize" /><span class="my-auto ms-3">{{ program.title }}</span>
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
                {{ $t('rule.form.error.sameEventExist') }}
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
              v-if="manualType"
              class="ma-2"
              :color="durationCondition && 'primary' || ''"
              :outlined="!durationCondition"
              :dark="durationCondition"
              @click="updateDateCondition">
              {{ $t('rule.form.label.duration') }}
            </v-chip>
            <v-chip
              class="ma-2"
              outlined>
              {{ $t('rule.form.label.action') }}
            </v-chip>
            <v-chip
              class="ma-2"
              outlined>
              {{ $t('rule.form.label.recurrence') }}
            </v-chip>
          </div>
          <div v-if="durationCondition">
            <v-card-text class="d-flex flex-grow-1 text-left text-subtitle-1 px-0 py-2">
              {{ $t('rule.form.label.duration.title') }}
            </v-card-text>

            <v-card-text class="pa-0 flex d-flex challengeDates">
              <select
                v-model="durationFilter"
                class="d-flex flex-grow-1 width-auto ignore-vuetify-classes">
                <option value="BEFORE">
                  {{ $t('rule.form.label.before') }}
                </option>
                <option value="AFTER">
                  {{ $t('rule.form.label.after') }}
                </option>
              </select>
              <v-icon color="primary" class="mb-2 px-4">fas fa-calendar-check</v-icon>
              <date-picker
                v-if="durationFilter === 'AFTER'"
                v-model="startDate"
                :default-value="false"
                :placeholder="$t('challenges.label.startDate')"
                :max-value="maximumStartDate"
                :min-value="minimumStartDate"
                :disabled="disabledStartDate"
                class="flex-grow-1 my-auto"
                @input="updateRuleStartDate" />
              <date-picker
                v-else
                v-model="endDate"
                :default-value="false"
                :placeholder="$t('challenges.label.endDate')"
                :min-value="minimumEndDate"
                :disabled="disabledEndDate"
                class="flex-grow-1 my-auto"
                @input="updateRuleEndDate" />
            </v-card-text>
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
          :disabled="disableSaveButton"
          class="btn btn-primary"
          @click="nextStep">
          {{ $t('rule.form.label.button.next') }}
        </v-btn>
        <v-btn
          v-else
          :disabled="disableSaveButton"
          class="btn btn-primary"
          @click="updateRule">
          {{ saveButtonLabel }}
        </v-btn>
      </div>
    </template>
  </exo-drawer>
</template>

<script>
export default {
  props: {
    events: {
      type: Array,
      default: function() {
        return [];
      },
    },
  },
  data: () => ({
    rule: {},
    program: {},
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
    scoreRules: [
      v => ( v && v <= 10000 ),
    ],
    startDate: null,
    endDate: null,
    disabledStartDate: false,
    disabledEndDate: false,
    durationCondition: false,
    durationFilter: 'BEFORE'
  }),
  computed: {
    minimumStartDate() {
      return new Date();
    },
    maximumStartDate() {
      if (this.endDate){
        const date = new Date(this.endDate);
        date.setDate(date.getDate()- 1) ;
        return date;
      } else {
        return null;
      }
    },
    minimumEndDate() {
      let date = new Date();
      if (this.startDate){
        date = new Date(this.startDate);
      }
      date.setDate(date.getDate() + 1) ;
      return date;
    },
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
    ruleTitle() {
      return this.rule?.title;
    },
    ruleId() {
      return this.rule?.id;
    },
    ruleTitleValid() {
      return this.ruleTitle?.length > 0;
    },
    ruleTypeValid() {
      return this.manualType || (this.automaticType && this.validEvent);
    },
    durationValid() {
      return !this.durationCondition || (this.durationCondition && (this.startDate != null || this.endDate != null));
    },
    disableSaveButton() {
      return this.saving || !this.ruleTitleValid || !this.validDescription || !this.ruleTypeValid || !this.durationValid || !this.isValidForm;
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
    ruleStartDate() {
      return this.rule?.startDate;
    },
    ruleEndDate() {
      return this.rule?.endDate;
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
    durationFilter() {
      this.startDate = null;
      this.endDate = null;
    }
  },
  created() {
    this.$root.$on('rule-form-drawer', (rule) => {
      this.rule = rule && JSON.parse(JSON.stringify(rule)) || {
        score: 20,
        enabled: true,
        area: this.programTitle
      };
      this.program = this.rule?.domainDTO;
      this.durationCondition = this.ruleStartDate != null || this.ruleEndDate != null;
      this.startDate = this.ruleStartDate ? new Date(this.rule?.startDate) : null;
      this.endDate = this.ruleEndDate ? new Date(this.rule?.endDate) : null;
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
    });
  },
  methods: {
    close() {
      this.$refs.ruleDescription.destroyCKEditor();
      this.$refs.ruleFormDrawer.close();
      this.rule.enabled = true;
      this.rule.event = null;
      this.$set(this.rule,'title','');
      this.rule.description = '';
      this.value = {};
    },
    emitSelectedValue(value) {
      const eventObject = this.eventMapping.find(event => event.label === value);
      if (eventObject) {
        this.$set(this.rule,'event', eventObject.name);
        if (this.eventExist) {
          this.eventExist = false;
        }
      }
    },
    addDescription(value) {
      if (value) {
        this.$set(this.rule,'description', value);
      }
    },
    updateRuleStartDate() {
      if (this.startDate) {
        const date = new Date(this.startDate);
        this.$set(this.rule,'startDate', this.$engagementCenterUtils.getIsoDate(date));
      }
    },
    updateRuleEndDate() {
      if (this.endDate) {
        const date = new Date(this.endDate);
        this.$set(this.rule,'endDate', this.$engagementCenterUtils.getIsoDate(date));
      }
    },
    updateRule() {
      this.saving = true;
      this.$refs.ruleFormDrawer.startLoading();
      if (this.rule.id) {
        this.$ruleServices.updateRule(this.rule)
          .then(rule => {
            this.displayAlert(this.$t('programs.details.ruleUpdateSuccess'));
            this.$root.$emit('program-rules-refresh', rule);
            this.close();
          }).catch(e => {
            if (e.message === '409') {
              this.eventExist = true;
            }}).finally(() => {
            this.saving = false;
            this.$refs.ruleFormDrawer.endLoading();
          });
      } else {
        this.$ruleServices.createRule(this.rule, this.program)
          .then(rule => {
            this.displayAlert(this.$t('programs.details.ruleCreationSuccess'));
            this.$root.$emit('program-rules-refresh', rule);
            this.close();
          }).catch(e => {
            if (e.message === '409') {
              this.eventExist = true;
            }}).finally(() => {
            this.saving = false;
            this.$refs.ruleFormDrawer.endLoading();
          });
      }
    },
    setAutomatic() {
      if (this.ruleType === 'MANUAL' || !this.ruleType) {
        this.$set(this.rule,'type', 'AUTOMATIC');
        this.durationCondition = false;
        this.startDate = null;
        this.endDate = null;
        this.$set(this.rule,'startDate', null);
        this.$set(this.rule,'endDate', null);
      } else {
        this.$set(this.rule,'type', '');
      }
    },
    setManual() {
      if (this.ruleType === 'AUTOMATIC' || !this.ruleType) {
        this.$set(this.rule,'type', 'MANUAL');
      } else {
        this.$set(this.rule,'type', '');
      }
    },
    updateDateCondition() {
      this.durationCondition = !this.durationCondition;
      this.$set(this.rule,'startDate', null);
      this.$set(this.rule,'endDate', null);
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

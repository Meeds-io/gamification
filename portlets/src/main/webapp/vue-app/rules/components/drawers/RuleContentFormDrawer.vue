<!--

 This file is part of the Meeds project (https://meeds.io/).

 Copyright (C) 2020 - 2024 Meeds Association contact@meeds.io

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
    ref="ruleContentFormDrawer"
    v-model="drawer"
    :confirm-close="ruleChanged"
    :confirm-close-labels="confirmCloseLabels"
    class="EngagementCenterDrawer"
    right
    allow-expand
    @expand-updated="expanded = $event"
    @opened="stepper = 1">
    <template #title>
      {{ ruleTitle }}
    </template>
    <template v-if="drawer" #content>
      <v-form
        ref="RuleForm"
        v-model="isValidForm"
        class="form-horizontal pt-0 pb-4"
        flat
        @submit="saveRule">
        <v-stepper
          v-model="stepper"
          :class="expanded && 'flex-row' || 'flex-column'"
          class="ma-0 pa-4 d-flex"
          vertical
          flat>
          <div :class="expanded && 'col-6'" class="d-flex flex-column">
            <div :class="expanded && 'pt-6'">
              <v-stepper-step
                :step="1"
                :editable="!expanded"
                class="ma-0 pa-0">
                {{ $t('rule.form.label.stepOne') }}
              </v-stepper-step>
              <v-stepper-items>
                <v-slide-y-transition>
                  <div v-show="expanded || (stepper === 1)">
                    <v-card-text class="pa-0">
                      <translation-text-field
                        ref="ruleDescriptionTranslation"
                        v-model="ruleDescriptionTranslations"
                        :field-value.sync="ruleDescription"
                        :object-id="ruleId"
                        :maxlength="maxDescriptionLength"
                        :no-expand-icon="!expanded"
                        class="ma-1px mt-4"
                        object-type="rule"
                        field-name="description"
                        drawer-title="rule.form.translateDescription"
                        back-icon
                        rich-editor
                        rich-editor-oembed
                        @initialized="setFormInitialized">
                        <template #title>
                          {{ $t('rule.form.label.description') }}
                        </template>
                        <rich-editor
                          id="ruleDescription"
                          ref="ruleDescriptionEditor"
                          v-model="ruleDescription"
                          :placeholder="$t('rule.form.label.description.placeholder')"
                          :max-length="maxDescriptionLength"
                          :tag-enabled="false"
                          ck-editor-type="rule"
                          oembed
                          @validity-updated="validDescription = $event" />
                      </translation-text-field>
                    </v-card-text>
                    <engagement-center-rule-form-automatic-flow
                      v-if="triggerType"
                      :selected-trigger="selectedTrigger"
                      :trigger-type="triggerType"
                      :event-properties="eventProperties"
                      :program-id="programId"
                      :rule-id="ruleId"
                      class="pt-5"
                      @triggerUpdated="selectTrigger"
                      @unfilled="eventProperties = {}"
                      @event-extension-initialized="eventExtensionInitialized" />
                  </div>
                </v-slide-y-transition>
              </v-stepper-items>
            </div>
            <div :class="expanded && 'pt-6'">
              <div :class="!expanded && 'pt-6'">
                <v-stepper-step
                  :step="2"
                  class="ma-0 pa-0"
                  :editable="ruleTitleValid && firstStepValid && !expanded">
                  {{ $t('rule.form.label.stepTwo') }}
                </v-stepper-step>
                <v-stepper-items>
                  <v-slide-y-transition>
                    <div v-show="expanded || (stepper === 2)">
                      {{ $t('rule.form.ruleConditionsLabel') }}
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
                    </div>
                  </v-slide-y-transition>
                </v-stepper-items>
              </div>
            </div>
          </div>
          <div :class="expanded && 'col-6'" class="d-flex flex-column">
            <div :class="expanded && 'pt-6'">
              <div :class="!expanded && 'pt-6'">
                <v-stepper-step
                  :step="3"
                  class="ma-0 pa-0"
                  :editable="ruleTitleValid && firstStepValid && !expanded">
                  {{ $t('rule.form.label.stepThree') }}
                </v-stepper-step>
                <v-stepper-items>
                  <v-slide-y-transition>
                    <div v-show="expanded || (stepper === 3)">
                      <v-card-text class="d-flex flex-grow-1 text-no-wrap text-left text-header px-0 pb-2 pt-4">
                        {{ $t('rule.form.defaultRealizationStatus.defaultStatus') }}
                      </v-card-text>
                      <v-radio-group
                        v-model="rule.defaultRealizationStatus"
                        class="mt-0 ps-2 pt-0"
                        mandatory
                        dense>
                        <v-radio
                          value="PENDING"
                          class="my-0 align-baseline">
                          <template #label>
                            <v-list-item class="px-0" two-line>
                              <v-list-item-content class="py-0">
                                <v-list-item-title>{{ $t('rule.form.defaultRealizationStatus.pending') }}</v-list-item-title>
                                <v-list-item-subtitle class="text-subtitle-font-size">
                                  {{ $t('rule.form.defaultRealizationStatus.pending.placeholder') }}
                                </v-list-item-subtitle>
                              </v-list-item-content>
                            </v-list-item>
                          </template>
                        </v-radio>
                        <v-radio
                          value="ACCEPTED"
                          class="my-0 align-baseline">
                          <template #label>
                            <v-list-item class="px-0" two-line>
                              <v-list-item-content class="py-0">
                                <v-list-item-title>{{ $t('rule.form.defaultRealizationStatus.accepted') }}</v-list-item-title>
                                <v-list-item-subtitle class="text-subtitle-font-size">
                                  {{ $t('rule.form.defaultRealizationStatus.accepted.placeholder') }}
                                </v-list-item-subtitle>
                              </v-list-item-content>
                            </v-list-item>
                          </template>
                        </v-radio>
                      </v-radio-group>
                      <v-card-text class="d-flex flex-grow-1 text-no-wrap text-left text-header px-0 pt-5 pb-2">
                        {{ $t('rule.form.label.rewards') }}
                      </v-card-text>
                      <div v-if="canVariableRewarding" class="d-flex justify-center">
                        <v-chip
                          class="ma-2"
                          :color="!variablePoints && 'primary' || ''"
                          :outlined="variablePoints"
                          :dark="!variablePoints"
                          @click="selectRewardingMode">
                          {{ $t('rule.form.label.fixedPoints') }}
                        </v-chip>
                        <v-chip
                          class="ma-2"
                          :color="variablePoints && 'primary' || ''"
                          :outlined="!variablePoints"
                          :dark="variablePoints"
                          @click="selectRewardingMode">
                          {{ $t('rule.form.label.variablePoints') }}
                        </v-chip>
                      </div>
                      <div class="d-flex flex-row">
                        <v-card
                          flat
                          class="d-flex flex-grow-1 pa-0 col-4">
                          <v-text-field
                            v-model="rule.score"
                            :rules="scoreRules"
                            class="mt-0 pt-0 me-2"
                            type="number"
                            outlined
                            dense
                            required />
                        </v-card>
                        <v-card-text class="mt-1 px-0 col-2"> {{ canVariableRewarding && variablePoints ? $t('rule.form.label.pointsFor') : $t('rule.form.label.points') }} </v-card-text>
                        <v-card
                          v-if="canVariableRewarding && variablePoints"
                          flat
                          class="d-flex flex-grow-1 pt-2 pe-0 col-4">
                          <v-text-field
                            v-model="totalTargetItem"
                            class="mt-0 pt-0 me-2"
                            type="number"
                            outlined
                            dense
                            required />
                        </v-card>
                        <v-card-text v-if="canVariableRewarding && variablePoints" class="mt-1 px-0 col-2"> {{ targetItem }} </v-card-text>
                      </div>
                    </div>
                  </v-slide-y-transition>
                </v-stepper-items>
              </div>
            </div>
            <div :class="expanded && 'pt-6'">
              <div :class="!expanded && 'pt-6'">
                <v-stepper-step
                  :step="4"
                  class="ma-0 pa-0"
                  :editable="ruleTitleValid && firstStepValid && !expanded">
                  {{ $t('rule.form.label.stepFour') }}
                </v-stepper-step>
                <v-stepper-items>
                  <v-slide-y-transition>
                    <div v-show="expanded || (stepper === 4)">
                      <div v-if="ruleId" class="d-flex align-center">
                        <v-card-text class="d-flex flex-grow-1 text-no-wrap text-left text-header px-0 pb-2">
                          {{ $t('rule.form.label.enabled') }}
                        </v-card-text>
                        <div class="flex-shrink-0 ms-2">
                          <v-switch
                            id="engagementCenterActionStatusSwitch"
                            ref="engagementCenterActionStatusSwitchRef"
                            v-model="rule.enabled"
                            class="my-0 ms-0 me-n1" />
                        </div>
                      </div>
                      <engagement-center-rule-publish-editor
                        v-if="enablePublication"
                        ref="rulePublishInput"
                        :enabled="!rule.id"
                        :rule="rule"
                        :metadata-object-id="metadataObjectId"
                        :program="program"
                        :publish.sync="rule.publish"
                        :space-id.sync="rule.spaceId"
                        :message.sync="rule.message"
                        :template-params="rule.templateParams"
                        :valid-message.sync="validMessage"
                        @attachments-edited="attachmentsEdited = true" />
                    </div>
                  </v-slide-y-transition>
                </v-stepper-items>
              </div>
            </div>
          </div>
        </v-stepper>
      </v-form>
    </template>
    <template #footer>
      <div class="d-flex">
        <v-spacer />
        <v-btn
          v-if="stepper > 1 && !expanded"
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
          v-if="stepper < 4 && !expanded"
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
    triggerType: {
      type: String,
      default: null
    },
    content: {
      type: Object,
      default: null
    },
    program: {
      type: Object,
      default: null
    },
    ruleTitleTranslations: {
      type: Object,
      default: null
    },
    originalRuleTitleTranslations: {
      type: Object,
      default: null
    },
  },
  data: () => ({
    rule: {},
    ruleDescription: null,
    originalRule: null,
    ruleToUpdate: {},
    ruleDescriptionTranslations: {},
    saving: false,
    eventMapping: [],
    eventProperties: null,
    validEventProperties: false,
    value: '',
    eventExist: false,
    validDescription: false,
    validEvent: false,
    selectedTrigger: '',
    stepper: 0,
    programAvatarSize: 40,
    isValidForm: true,
    maxTitleLength: 50,
    maxDescriptionLength: 1300,
    drawer: false,
    expanded: false,
    durationCondition: false,
    variablePoints: false,
    canVariableRewarding: false,
    totalTargetItem: null,
    defaultTotalTargetItem: null,
    targetItem: '',
    recurrenceCondition: false,
    prerequisiteRuleCondition: false,
    validDatesInput: false,
    validMessage: false,
    metadataObjectId: null,
    attachmentsEdited: false,
    defaultTemplateParams: {
      'previewHeight': '-',
      'previewWidth': '-',
      'link': '-',
      'description': '-',
      'title': '-',
      'comment': '-',
      'default_title': '-',
      'html': '-',
    },
    isExtensibleEvent: false
  }),
  computed: {
    scoreRules() {
      return [
        v => v && v <= 10000 || this.$t('rules.actionScoreExceedsMax'),
        v => v && v > 0 || this.$t('rules.actionScoreMandatory'),
      ];
    },
    programAvatar() {
      return this.program?.avatarUrl || '';
    },
    programTitle() {
      return this.program?.title;
    },
    programId() {
      return this.program?.id;
    },
    ruleId() {
      return this.rule?.id;
    },
    excludedRuleIds() {
      return this.ruleId && [this.ruleId] || [];
    },
    automaticType() {
      return this.ruleType === 'AUTOMATIC';
    },
    ruleTitle() {
      return this.rule?.title;
    },
    ruleTitleValid() {
      return this.ruleTitle?.length > 0;
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
    firstStepValid() {
      return (this.triggerType && ((this.isExtensibleEvent && this.validEventProperties) || (!this.isExtensibleEvent && this.value))) || !this.triggerType;
    },
    disableNextButton() {
      return this.saving || !this.ruleTitleValid || !this.validDescription || !this.isValidForm || (this.triggerType && ((this.isExtensibleEvent && !this.validEventProperties) || (!this.isExtensibleEvent && !this.value))) || (this.automaticType && !this.value);
    },
    disableSaveButton() {
      return !this.ruleChanged || this.disableNextButton || !this.durationValid || !this.recurrenceValid || !this.prerequisiteRuleValid || (this.enablePublication && !this.validMessage);
    },
    saveButtonLabel() {
      return this.ruleId ? this.$t('rule.form.label.button.update') : this.$t('rule.form.label.button.add');
    },
    ruleType() {
      return this.rule?.type;
    },
    enablePublication() {
      return this.rule?.enabled
          && !this.rule?.deleted
          && this.program?.enabled
          && !this.program?.deleted
          && (!this.rule.id || !this.rule.published);
    },
    ruleToSave() {
      return this.computeRuleModel(this.rule, this.program, this.ruleDescription);
    },
    ruleChanged() {
      if (!this.originalRule || !this.originalRuleTitleTranslations || !this.originalRuleDescriptionTranslations) {
        return false;
      }
      return this.attachmentsEdited || JSON.stringify({
        title: JSON.parse(JSON.stringify(this.originalRuleTitleTranslations)),
        description: JSON.parse(JSON.stringify(this.originalRuleDescriptionTranslations)),
        type: this.originalRule.type,
        defaultRealizationStatus: this.originalRule.defaultRealizationStatus,
        score: this.originalRule.score,
        enabled: this.originalRule.enabled,
        event: this.originalRule.type === 'AUTOMATIC' && this.originalRule.event?.title || null,
        eventProperties: this.originalRule.type === 'AUTOMATIC' && JSON.stringify(this.originalRule.event?.properties) || null,
        startDate: this.originalRule.startDate,
        endDate: this.originalRule.endDate,
        recurrence: this.originalRule.recurrence,
        prerequisiteRuleIds: this.originalRule.prerequisiteRuleIds?.filter?.(id => id),
        publish: this.originalRule.publish,
        message: this.originalRule.message,
        templateParams: this.originalRule.templateParams
      }) !== JSON.stringify({
        title: JSON.parse(JSON.stringify(this.ruleTitleTranslations)),
        description: JSON.parse(JSON.stringify(this.ruleDescriptionTranslations)),
        type: this.ruleToSave.type,
        defaultRealizationStatus: this.ruleToSave.defaultRealizationStatus,
        score: this.ruleToSave.score,
        enabled: this.ruleToSave.enabled,
        event: this.ruleToSave.type === 'AUTOMATIC' && this.ruleToSave.event?.title || null,
        eventProperties: this.ruleToSave.type === 'AUTOMATIC' && JSON.stringify(this.ruleToSave.event?.properties) || null,
        startDate: this.ruleToSave.startDate,
        endDate: this.ruleToSave.endDate,
        recurrence: this.ruleToSave.recurrence,
        prerequisiteRuleIds: this.ruleToSave.prerequisiteRuleIds?.filter?.(id => id),
        publish: this.ruleToSave.publish,
        message: this.ruleToSave.message,
        templateParams: this.ruleToSave.templateParams
      });
    },
    confirmCloseLabels() {
      return {
        title: this.rule?.id && this.$t('rule.confirmCloseModificationTitle') || this.$t('rule.confirmCloseCreationTitle'),
        message: this.rule?.id && this.$t('rule.confirmCloseModificationMessage') || this.$t('rule.confirmCloseCreationMessage'),
        ok: this.$t('confirm.yes'),
        cancel: this.$t('confirm.no'),
      };
    },
    programStyle() {
      return this.program?.color && `border: 1px solid ${this.program.color} !important;` || '';
    },
  },
  watch: {
    value: {
      immediate: true,
      handler() {
        this.validEvent = this.value && this.value !== '';
      }
    },
    ruleDescription() {
      if (this.$refs.ruleDescriptionTranslation) {
        this.$refs.ruleDescriptionTranslation.setValue(this.ruleDescription);
      }
    },
    saving() {
      if (this.saving) {
        this.$refs.ruleContentFormDrawer.startLoading();
      } else {
        this.$refs.ruleContentFormDrawer.endLoading();
      }
    },
    expanded() {
      this.stepper = this.expanded ? 3 : 1;
    },
  },
  methods: {
    open() {
      this.rule = this.content;
      this.ruleDescription = this.rule?.description || '';
      this.validDescription = !!this.ruleDescription;
      this.ruleDescriptionTranslations = {};
      this.durationCondition = this.rule.startDate || this.rule.endDate;
      this.recurrenceCondition = !!this.rule.recurrence;
      this.prerequisiteRuleCondition = this.rule.prerequisiteRules?.length;
      this.eventExist = false;
      this.metadataObjectId = this.content?.id;
      this.attachmentsEdited = false;
      this.value = this.rule?.event?.title;
      this.selectedTrigger = this.rule?.event?.title;
      this.eventProperties = this.rule?.event?.properties;
      this.variablePoints = !!this.rule?.event?.properties?.totalTargetItem;
      this.totalTargetItem = Number(this.rule?.event?.properties?.totalTargetItem);
      this.validEventProperties = true;
      if (this.$refs.ruleContentFormDrawer) {
        this.$refs.ruleContentFormDrawer.open();
      }
    },
    selectTrigger(trigger, triggerType, eventProperties, validEventProperties) {
      this.eventProperties = eventProperties;
      this.validEventProperties = validEventProperties;
      this.value = trigger || null;
      const event = {
        cancellerEvents: null,
        id: this.rule?.event?.id,
        title: trigger,
        trigger: trigger,
        type: triggerType,
        properties: eventProperties
      };
      this.$set(this.rule, 'event', event);
      this.rule = Object.assign({}, this.rule);
    },
    eventExtensionInitialized(extension, canVariableRewarding) {
      if (extension) {
        this.isExtensibleEvent = extension?.isExtensible;
        this.targetItem = extension?.targetItemLabel;
        this.canVariableRewarding = canVariableRewarding;
        this.defaultTotalTargetItem = extension?.defaultTotalTargetItem;
        if (!this.totalTargetItem) {
          this.totalTargetItem = this.defaultTotalTargetItem;
        }
      }
    },
    close() {
      this.$refs.ruleContentFormDrawer.close();
    },
    saveRule() {
      this.saving = true;
      if (this.rule.id) {
        this.$translationService.saveTranslations('rule', this.rule.id, 'title', this.ruleTitleTranslations)
          .then(() => this.$translationService.saveTranslations('rule', this.rule.id, 'description', this.ruleDescriptionTranslations))
          .then(() => this.$refs?.rulePublishInput?.saveAttachments())
          .then(() => this.$ruleService.updateRule(this.ruleToSave))
          .then(rule => {
            this.$root.$emit('rule-updated-event', rule);
            if (this.ruleToSave.publish && rule.activityId) {
              document.dispatchEvent(new CustomEvent('alert-message-html', {
                detail: {
                  alertType: 'success',
                  alertMessage: this.$t('programs.details.ruleUpdateAndPublishSuccess'),
                  alertLink: `${eXo.env.portal.context}/${eXo.env.portal.defaultPortal}/activity?id=${rule.activityId}`,
                  alertLinkText: this.$t('rule.alert.see'),
                  alertLinkTarget: '_self',
                }
              }));
            } else {
              this.$root.$emit('alert-message', this.$t('programs.details.ruleUpdateSuccess'), 'success');
            }
            this.saving = false; // To Keep to be able to close drawer
            this.originalRule = null;
            this.attachmentsEdited = false;
            this.originalRuleTitleTranslations = null;
            this.originalRuleDescriptionTranslations = null;
            this.attachmentsEdited = false;
            return this.$nextTick();
          })
          .then(() => {
            this.close();
            this.$nextTick().then(() => this.$emit('saved'));
          })
          .catch(e => {
            console.error(e);
            this.eventExist = e.message === '409';
          })
          .finally(() => this.saving = false);
      } else {
        this.$ruleService.createRule(this.ruleToSave)
          .then(rule => {
            this.originalRule = rule;
            this.$root.$emit('rule-created-event', rule);
            return this.$translationService.saveTranslations('rule', this.originalRule.id, 'title', this.ruleTitleTranslations);
          })
          .then(() => this.$translationService.saveTranslations('rule', this.originalRule.id, 'description', this.ruleDescriptionTranslations))
          .then(() => {
            this.metadataObjectId = String(this.originalRule.id);
            return this.$nextTick();
          })
          .then(() => this.$refs?.rulePublishInput?.saveAttachments())
          .then(() => {
            if (this.ruleToSave.publish && this.originalRule.activityId) {
              document.dispatchEvent(new CustomEvent('alert-message-html', {
                detail: {
                  alertType: 'success',
                  alertMessage: this.$t('programs.details.ruleCreationAndPublishSuccess'),
                  alertLink: `${eXo.env.portal.context}/${eXo.env.portal.defaultPortal}/activity?id=${this.originalRule.activityId}`,
                  alertLinkText: this.$t('rule.alert.see'),
                  alertLinkTarget: '_self',
                }
              }));
            } else {
              this.$root.$emit('alert-message', this.$t('programs.details.ruleCreationSuccess'), 'success');
            }
            this.saving = false; // To Keep to be able to close drawer
            this.originalRule = null;
            this.attachmentsEdited = false;
            this.originalRuleTitleTranslations = null;
            this.originalRuleDescriptionTranslations = null;
            this.attachmentsEdited = false;
            return this.$nextTick();
          })
          .then(() => {
            this.close();
            this.$nextTick().then(() => this.$emit('saved'));
          })
          .catch(e => {
            console.error(e);
            this.eventExist = e.message === '409';
          })
          .finally(() => this.saving = false);
      }
    },
    computeRuleModel(rule, program, description) {
      if (rule?.event && this.canVariableRewarding) {
        if (this.variablePoints) {
          rule.event.properties.totalTargetItem = this.totalTargetItem;
        } else {
          delete rule.event.properties.totalTargetItem;
        }
      }
      const ruleModel = {
        id: rule?.id,
        title: this.ruleTitle,
        description: description || this.ruleDescription,
        score: rule?.score,
        program: {
          id: program?.id,
        },
        enabled: rule?.enabled,
        event: rule.type === 'AUTOMATIC' ? rule?.event : null,
        startDate: rule?.startDate,
        endDate: rule?.endDate,
        publish: rule?.publish,
        message: rule?.message,
        spaceId: rule?.spaceId,
        templateParams: rule?.templateParams || Object.assign({}, this.defaultTemplateParams),
      };
      if (rule.recurrence) {
        ruleModel.recurrence = rule.recurrence;
      }
      if (rule.type) {
        ruleModel.type = rule.type;
      }
      if (rule.defaultRealizationStatus) {
        ruleModel.defaultRealizationStatus = rule.defaultRealizationStatus;
      }
      if (rule.prerequisiteRules?.length) {
        ruleModel.prerequisiteRuleIds = rule.prerequisiteRules.map(r => r.id).filter(id => id);
      }
      return ruleModel;
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
    selectRewardingMode() {
      this.variablePoints = !this.variablePoints;
      this.totalTargetItem = !this.variablePoints ? 0 : this.defaultTotalTargetItem;
    },
    nextStep(event) {
      if (event) {
        event.preventDefault();
        event.stopPropagation();
      }
      this.stepper++;
    },
    setFormInitialized() {
      this.originalRule = this.computeRuleModel(this.rule, this.program);
      this.originalRuleDescriptionTranslations = this.ruleDescriptionTranslations && JSON.parse(JSON.stringify(this.ruleDescriptionTranslations));
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

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
    :confirm-close-labels="confirmCloseLabels"
    :confirm-close="ruleChanged"
    class="EngagementCenterDrawer"
    right
    @closed="clear">
    <template #title>
      {{ drawerTitle }}
    </template>
    <template v-if="drawer" #content>
      <v-card-text class="d-flex">
        <translation-text-field
          ref="ruleTitle"
          id="ruleTitle"
          v-model="ruleTitleTranslations"
          :field-value.sync="ruleTitle"
          :placeholder="$t('rule.form.label.rules.placeholder')"
          :maxlength="maxTitleLength"
          :object-id="ruleId"
          no-expand-icon
          object-type="rule"
          field-name="title"
          drawer-title="rule.form.translateTitle"
          class="width-auto flex-grow-1 pb-1 pt-4"
          back-icon
          autofocus
          required
          @initialized="setFormInitialized">
          <template #title>
            {{ $t('rule.form.label.rules') }}
          </template>
        </translation-text-field>
      </v-card-text>
      <v-card-text class="pb-0">{{ $t('rule.form.label.incentivizeUsers') }}</v-card-text>
      <v-list-item-group
        v-model="selectedConnectorIndex"
        :role="null"
        class="px-4"
        color="primary"
        active-class="primary white--text">
        <engagement-center-rule-form-connector-item
          v-for="connector in connectors"
          :key="connector.id"
          :connector="connector" />
        <v-list-item dense class="border-color-grey border-radius my-2 ps-3">
          <v-list-item-avatar
            class="mx-2"
            size="25"
            tile>
            <v-icon size="24">
              fas fa-bullhorn
            </v-icon>
          </v-list-item-avatar>
          <v-list-item-content>
            <v-list-item-title>{{ $t('rule.form.label.manuelLabel') }}</v-list-item-title>
          </v-list-item-content>
        </v-list-item>
      </v-list-item-group>
      <div class="d-flex justify-center py-5">
        <v-btn
          :disabled="disableStartButton"
          class="btn btn-primary"
          @click="start">
          {{ startButtonLabel }}
        </v-btn>
      </div>
      <engagement-center-rule-content-form-drawer
        ref="ruleContentFormDrawer"
        :content="ruleToSave"
        :program="program"
        :connector="selectedConnector"
        :rule-title-translations="ruleTitleTranslations"
        :original-rule-title-translations="originalRuleTitleTranslations"
        @contentChanged="contentChanged = $event"
        @saved="save"
        @closed="close" />
    </template>
  </exo-drawer>
</template>

<script>
export default {
  data: () => ({
    rule: {},
    program: null,
    ruleDescription: null,
    value: '',
    drawer: false,
    originalRuleTitleTranslations: {},
    connectors: [],
    selectedConnectorIndex: -1,
    originalSelectedConnector: null,
    selectedConnector: null,
    ruleTitle: null,
    ruleTitleTranslations: {},
    maxTitleLength: 50,
    selectedTrigger: '',
    extensionApp: 'engagementCenterConnectors',
    connectorExtensionType: 'connector-extensions',
    connectorsExtensions: [],
    actionValueExtensions: {},
    actionValueExtensionType: 'user-actions',
    contentChanged: false
  }),
  computed: {
    ruleId() {
      return this.rule?.id;
    },
    programTitle() {
      return this.program?.title;
    },
    drawerTitle() {
      return this.ruleId ? this.$t('rule.form.label.edit') : this.$t('rule.form.label.add');
    },
    ruleTitleValid() {
      return this.ruleTitle?.length > 0;
    },
    eventType() {
      return this.rule?.event?.type;
    },
    disableStartButton() {
      return typeof this.selectedConnectorIndex === 'undefined' || this.selectedConnectorIndex < 0 || !this.ruleTitleValid;
    },
    confirmCloseLabels() {
      return {
        title: this.rule?.id && this.$t('rule.confirmCloseModificationTitle') || this.$t('rule.confirmCloseCreationTitle'),
        message: this.rule?.id && this.$t('rule.confirmCloseModificationMessage') || this.$t('rule.confirmCloseCreationMessage'),
        ok: this.$t('confirm.yes'),
        cancel: this.$t('confirm.no'),
      };
    },
    ruleToSave() {
      return this.computeRuleModel(this.rule, this.program, this.ruleDescription);
    },
    triggerType() {
      return this.selectedConnector?.name || null;
    },
    startButtonLabel() {
      return this.ruleId && this.$t('rule.form.label.button.next') || this.$t('rule.form.label.button.start');
    },
    ruleChanged() {
      return this.contentChanged || (JSON.stringify({
        title: this.originalRuleTitleTranslations,
        triggerType: this.originalSelectedConnector ? this.originalSelectedConnector : null,
      }) !== JSON.stringify({
        title: this.ruleTitleTranslations,
        triggerType: this.triggerType
      }));
    }
  },
  watch: {
    selectedConnectorIndex() {
      if (this.selectedConnectorIndex > 0) {
        this.selectedConnector = this.connectors[this.selectedConnectorIndex - 1];
        this.$set(this.rule,'type', 'AUTOMATIC');
        const event = {type: this.selectedConnector?.name};
        this.$set(this.rule,'event', event);
      } else if (this.selectedConnectorIndex === 0){
        this.selectedConnector = null;
        this.$set(this.rule,'type', 'MANUAL');
      }
    }
  },
  created() {
    this.$root.$on('rule-form-drawer-event', this.open);
    this.refreshConnectorExtensions();
  },
  methods: {
    init() {
      this.refreshConnectorExtensions();
      // Check connectors status from store
      this.loading = true;
      return this.$gamificationConnectorService.getConnectors(eXo.env.portal.userName)
        .then(connectors => {
          const enabledConnectors = connectors?.filter(connector => connector.enabled) || [];
          const connectorsToDisplay = this.connectorsExtensions.filter(connectorExtension => enabledConnectors.some(item => item.name === connectorExtension.name) || connectorExtension.defaultConnector) || [];
          this.connectors.push(...connectorsToDisplay);
        }).then(() => {
          if (this.eventType) {
            this.selectedConnectorIndex = this.connectors?.findIndex?.(connector => connector.name === this.eventType) + 1;
            this.$nextTick().then(() => {
              this.trigger = this.selectedTrigger;
            });
          } else if (this.ruleId) {
            this.selectedConnectorIndex = 0;
          }
        })
        .finally(() => {
          this.loading = false;
        });
    },
    refreshConnectorExtensions() {
      this.connectorsExtensions = extensionRegistry.loadExtensions(this.extensionApp, this.connectorExtensionType) || [];
      this.connectorsExtensions.forEach(extension => {
        if (extension?.init) {
          const initPromise = extension.init();
          if (initPromise?.then) {
            return initPromise.then(() => this.$nextTick());
          }
        }
      });
    },
    open(rule, program) {
      this.rule = rule && JSON.parse(JSON.stringify(rule)) || {
        score: 20,
        enabled: true,
        publish: true,
        area: this.programTitle
      };
      if (!this.rule.templateParams) {
        this.rule.templateParams = Object.assign({}, this.defaultTemplateParams);
      }
      this.program = this.rule?.program || program;
      this.ruleTitle = this.rule?.title || '';
      this.ruleDescription = this.rule?.description || '';
      this.ruleTitleTranslations = {};
      this.originalSelectedConnector = this.rule?.event?.type;
      if (this.$refs.ruleFormDrawer) {
        this.$refs.ruleFormDrawer.open();
      }
      this.init();
      this.$nextTick().then(() => {
        this.$root.$emit('rule-form-drawer-opened', this.rule);
        this.$nextTick().then(() => {
          if (this.ruleId) {
            this.start();
          }
        });
      });
    },
    start() {
      if (this.$refs.ruleContentFormDrawer) {
        this.$refs.ruleContentFormDrawer.open();
      }
    },
    close() {
      this.$refs.ruleFormDrawer.close();
    },
    save() {
      this.originalRuleTitleTranslations = this.ruleTitleTranslations;
      this.originalSelectedConnector = this.triggerType;
      this.$nextTick().then(() => this.close());
    },
    clear() {
      this.connectors = [];
      this.selectedConnectorIndex = -1;
      this.selectedConnector = null;
      this.ruleTitle = null;
      this.ruleTitleTranslations = {};
      this.$set(this.rule,'title','');
      this.contentChanged = false;
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
    setFormInitialized() {
      this.originalRuleTitleTranslations = this.ruleTitleTranslations && JSON.parse(JSON.stringify(this.ruleTitleTranslations));
    },
  }
};
</script>

<!--

 This file is part of the Meeds project (https://meeds.io/).

 Copyright (C) 2020 - 2023 Meeds Association contact@meeds.io

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
  <div>
    <v-card-text class="px-0">
      {{ $t('rule.form.label.application.createAutomaticFlow') }}
    </v-card-text>
    <v-autocomplete
      id="triggerAutoComplete"
      ref="triggerAutoComplete"
      v-model="trigger"
      :items="sortedTriggers"
      :placeholder="$t('rule.form.label.event.placeholder')"
      :filter="filterTriggers"
      class="pa-0"
      background-color="white"
      dense
      flat
      solo
      outlined
      @change="triggerChanged">
      <template #selection="{item, selected}">
        <v-chip
          :input-value="selected"
          color="white">
          <rule-icon :rule-event="item" class="me-2" />
          <v-tooltip bottom>
            <template #activator="{ on }">
              <span v-on="on" class="text-truncate">{{ getTriggerLabel(item) }}
              </span>
            </template>
            <span>{{ getTriggerLabel(item) }}</span>
          </v-tooltip>
        </v-chip>
      </template>
      <template #item="{item}">
        <rule-icon :rule-event="item" class="me-2" />
        <v-list-item-content>
          <v-list-item-title>
            {{ getTriggerLabel(item) }}
          </v-list-item-title>
        </v-list-item-content>
      </template>
    </v-autocomplete>
    <v-card v-if="trigger && isExtensibleEvent" flat>
      <extension-registry-components
        v-if="initialized"
        :params="params"
        name="engagementCenterEvent"
        type="connector-event-extensions"
        parent-element="div"
        element="div"
        class="d-flex flex-column" />
    </v-card>
  </div>
</template>

<script>

export default {
  props: {
    triggerType: {
      type: String,
      default: null
    },
    selectedTrigger: {
      type: String,
      default: null
    },
    eventProperties: {
      type: Object,
      default: null
    },
    validEventProperties: {
      type: Boolean,
      default: false
    },
    programId: {
      type: Number,
      default: null,
    },
    ruleId: {
      type: Number,
      default: null,
    }
  },
  data: () => ({
    trigger: '',
    triggers: [],
    triggersItems: [],
    extensionApp: 'engagementCenterConnectors',
    connectorExtensionType: 'connector-extensions',
    connectorsExtensions: [],
    connectorComponentExtension: null,
    actionValueExtensions: {},
    extensionActionApp: 'engagementCenterActions',
    actionValueExtensionType: 'user-actions',
    programEventsWithSameTrigger: [],
    initialized: true
  }),
  computed: {
    extensionAction() {
      if (this.actionValueExtensions) {
        return Object.values(this.actionValueExtensions)
          .sort((ext1, ext2) => (ext1.rank || 0) - (ext2.rank || 0))
          .find(extension => extension.match && extension.match(this.trigger)) || null;
      }
      return null;
    },
    sortedTriggers() {
      const filteredTriggers = this.triggers?.length && this.triggers.slice() || [];
      return filteredTriggers.sort((a, b) => this.getTriggerLabel(a).localeCompare(this.getTriggerLabel(b)));
    },
    params() {
      return {
        trigger: this.trigger,
        type: this.triggerType,
        properties: this.eventProperties,
        isEditing: true,
      };
    },
    isExtensibleEvent() {
      return this.extensionAction?.isExtensible && !this.extensionAction?.notExtensible?.includes(this.trigger);
    },
    canVariableRewarding() {
      return this.triggersItems.find(trigger => trigger.title === this.trigger)?.canVariableRewarding;
    },
  },
  watch: {
    validEventProperties() {
      if (this.validEventProperties) {
        const index = this.programEventsWithSameTrigger.findIndex(event => JSON.stringify(event.properties) === JSON.stringify(this.eventProperties));
        if (index >= 0) {
          this.$root.$emit('alert-message', this.$t('rule.form.error.sameEventExistsInProgram'), 'warning');
        }
      }
    },
  },
  created() {
    document.addEventListener(`extension-${this.extensionApp}-${this.connectorExtensionType}-updated`, this.refreshConnectorExtensions);
    document.addEventListener('event-form-filled', (event) => {
      this.eventProperties = event?.detail ? event?.detail : null;
      this.validEventProperties = true;
      this.$emit('triggerUpdated', this.trigger, this.triggerType, this.eventProperties, true);

    });
    document.addEventListener('event-form-unfilled', () => {
      this.eventProperties = null;
      this.validEventProperties = false;
      this.$emit('triggerUpdated', this.trigger, this.triggerType, this.eventProperties, false);
    });

    if (this.selectedTrigger) {
      this.trigger = this.selectedTrigger;

      this.$ruleService.getRules({
        status: 'ENABLED',
        programId: this.programId,
        programStatus: 'ENABLED',
        dateFilter: 'ACTIVE',
        eventName: this.trigger,
      }).then(result => {
        this.programEventsWithSameTrigger = result?.rules.filter(rule => this.ruleId !== rule.id).map(rule => rule.event);
      });
    }
    this.$root.$on('rule-actions-updated', this.refreshExtensions);
    this.init();
  },
  methods: {
    init() {
      this.refreshExtensions();
      this.retrieveTriggers();
    },

    filterTriggers(item, queryText) {
      return this.getTriggerLabel(item).toLocaleLowerCase().indexOf(queryText.toLocaleLowerCase()) > -1;
    },
    getTriggerLabel(trigger) {
      return this.$t(`gamification.event.title.${trigger}`);
    },
    retrieveTriggers() {
      return this.$gamificationConnectorService.getTriggers(this.triggerType)
        .then(triggers => {
          this.triggersItems = triggers;
          this.triggers = triggers.map(trigger => trigger.title);
          this.$nextTick().then(() => {
            this.$emit('event-extension-initialized', this.extensionAction, this.canVariableRewarding);
          });
        });
    },
    triggerChanged() {
      if (this.trigger) {
        this.initialized = false;
        this.$ruleService.getRules({
          status: 'ENABLED',
          programId: this.programId,
          programStatus: 'ENABLED',
          dateFilter: 'ACTIVE',
          eventName: this.trigger,
        }).then(result => {
          this.programEventsWithSameTrigger = result?.rules.map(rule => rule.event);
          if (this.triggerType && !this.isExtensibleEvent) {
            const index = this.programEventsWithSameTrigger.findIndex(event => event.trigger === this.trigger);
            if (index >= 0) {
              this.$root.$emit('alert-message', this.$t('rule.form.error.sameEventExistsInProgram'), 'warning');
            }
          }
        }).finally(window.setTimeout(() => this.initialized = true, 10));
        if (!this.isExtensibleEvent) {
          this.$emit('triggerUpdated', this.trigger, this.triggerType, this.eventProperties, true);
        }
      }
    },
    refreshExtensions() {
      if (this.$root.actionValueExtensions && Object.keys(this.$root.actionValueExtensions).length) {
        this.actionValueExtensions = this.$root.actionValueExtensions;
      } else {
        this.$utils.includeExtensions('engagementCenterActions');
      }
    },
  }
};
</script>
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
  <v-app>
    <v-card-text class="px-0 dark-grey-color font-weight-bold">
      {{ $t('rule.form.label.application') }}
    </v-card-text>
    <v-autocomplete
      id="applicationAutoComplete"
      ref="applicationAutoComplete"
      v-model="selectedConnector"
      :items="connectors"
      :placeholder="$t('rule.form.label.application.Placeholder')"
      :filter="filterConnectors"
      class="pa-0"
      background-color="white"
      item-value="name"
      dense
      flat
      cache-items
      solo
      outlined>
      <template #selection="{item, selected}">
        <v-chip
          :input-value="selected"
          color="white">
          <v-icon
            v-if="item.icon"
            :class="item.iconColorClass"
            size="20"
            class="pe-2">
            {{ item.icon }}
          </v-icon>
          <img
            v-else
            :src="item.image"
            :alt="item.name"
            class="pe-2"
            width="28">
          {{ item.title }}
        </v-chip>
      </template>
      <template #item="{item}">
        <v-icon
          v-if="item.icon"
          :class="item.iconColorClass"
          size="20"
          class="pe-2">
          {{ item.icon }}
        </v-icon>
        <img
          v-else
          :src="item.image"
          :alt="item.name"
          class="pe-2"
          width="20">
        <v-list-item-content>
          <v-list-item-title>
            {{ item.title }}
          </v-list-item-title>
        </v-list-item-content>
      </template>
    </v-autocomplete>
    <template v-if="selectedConnector">
      <v-card-text class="px-0 dark-grey-color font-weight-bold">
        {{ $t('rule.form.label.event') }}
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
      <v-card v-if="trigger" flat>
        <extension-registry-components
          :params="params"
          name="engagementCenterEvent"
          type="connector-event-extensions"
          parent-element="div"
          element="div"
          class="d-flex flex-column" />
      </v-card>
    </template>
  </v-app>
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
    programId: {
      type: Number,
      default: null,
    }
  },
  data: () => ({
    selectedConnector: null,
    trigger: '',
    connectors: [],
    triggers: [],
    extensionApp: 'engagementCenterConnectors',
    extensionEventApp: 'engagementCenterEvent',
    connectorExtensionType: 'connector-extensions',
    connectorEventExtensionType: 'connector-event-extensions',
    connectorsExtensions: [],
    connectorsEventComponentsExtensions: [],
    connectorComponentExtension: null,
    programEventsWithSameTrigger: [],
  }),
  computed: {
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
      return this.connectorsEventComponentsExtensions.map(extension => extension.componentOptions.name).includes(this.selectedConnector);
    },
  },
  watch: {
    selectedConnector() {
      if (this.selectedConnector) {
        this.trigger = null;
        this.triggers = [];
        this.retrieveTriggers();
      }
    },
    eventProperties() {
      if (this.selectedConnector) {
        this.$emit('triggerUpdated', this.trigger, this.selectedConnector, this.eventProperties);
        const index = this.programEventsWithSameTrigger.findIndex(event => JSON.stringify(event.properties) === JSON.stringify(this.eventProperties));
        if (index >= 0) {
          this.$root.$emit('alert-message', this.$t('rule.form.error.sameEventExistsInProgram'), 'warning');
        }
      }
    }
  },
  created() {
    document.addEventListener(`extension-${this.extensionApp}-${this.connectorExtensionType}-updated`, this.refreshConnectorExtensions);
    document.addEventListener(`component-${this.extensionEventApp}-${this.connectorEventExtensionType}-updated`, this.refreshConnectorExtensions);
    document.addEventListener('event-form-filled', (event) => {
      if (event.detail) {
        this.eventProperties = event.detail;
      }
    });
    document.addEventListener('event-form-unfilled', () => {
      this.eventProperties = null;
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
        this.programEventsWithSameTrigger = result?.rules.map(rule => rule.event);
      });
    }
    this.init();
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
          if (this.triggerType) {
            this.selectedConnector = this.connectors.find(connector => connector.name === this.triggerType)?.name;
            this.$nextTick().then(() => {
              this.trigger = this.selectedTrigger;
            });
          }
        })
        .finally(() => {
          this.loading = false;
        });
    },
    refreshConnectorExtensions() {
      // Get list of connectors from extensionRegistry
      this.connectorsEventComponentsExtensions = extensionRegistry.loadComponents(this.extensionEventApp) || [];
      this.$emit('event-extension-initialized', this.connectorsEventComponentsExtensions.map(extension => extension.componentOptions.name));
      this.connectorsExtensions = extensionRegistry.loadExtensions(this.extensionApp, this.connectorExtensionType) || [];
    },
    filterConnectors(item, queryText) {
      return item.title.toLocaleLowerCase().indexOf(queryText.toLocaleLowerCase()) > -1;
    },
    filterTriggers(item, queryText) {
      return this.getTriggerLabel(item).toLocaleLowerCase().indexOf(queryText.toLocaleLowerCase()) > -1;
    },
    getTriggerLabel(trigger) {
      return this.$t(`gamification.event.title.${trigger}`);
    },
    retrieveTriggers() {
      return this.$gamificationConnectorService.getTriggers(this.selectedConnector)
        .then(triggers => {
          this.triggers = triggers.map(trigger => trigger.title);
        });
    },
    triggerChanged() {
      if (this.trigger) {
        this.$ruleService.getRules({
          status: 'ENABLED',
          programId: this.programId,
          programStatus: 'ENABLED',
          dateFilter: 'ACTIVE',
          eventName: this.trigger,
        }).then(result => {
          this.programEventsWithSameTrigger = result?.rules.map(rule => rule.event);
          if (this.selectedConnector && !this.isExtensibleEvent) {
            const index = this.programEventsWithSameTrigger.findIndex(event => event.trigger === this.trigger);
            if (index >= 0) {
              this.$root.$emit('alert-message', this.$t('rule.form.error.sameEventExistsInProgram'), 'warning');
            }
            this.$emit('triggerUpdated', this.trigger, this.selectedConnector);
          }
        });
      }
    },
  }
};
</script>
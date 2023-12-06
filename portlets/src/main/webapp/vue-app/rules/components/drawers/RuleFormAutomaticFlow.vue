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
        outlined>
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
  },
  data: () => ({
    selectedConnector: null,
    trigger: '',
    connectors: [],
    triggers: [],
    adminConnectorsExtensions: [],
  }),
  computed: {
    sortedTriggers() {
      const filteredTriggers = this.triggers?.length && this.triggers.slice() || [];
      return filteredTriggers.sort((a, b) => this.getTriggerLabel(a).localeCompare(this.getTriggerLabel(b)));
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
    trigger() {
      if (this.selectedConnector) {
        this.$emit('triggerUpdated', this.trigger, this.selectedConnector);
      }
    }
  },
  created() {
    if (this.selectedTrigger) {
      this.trigger = this.selectedTrigger;
    }
    this.init();
  },
  methods: {
    init() {
      this.refreshUserConnectorList();
      // Check connectors status from store
      this.loading = true;
      return this.$gamificationConnectorService.getConnectors(eXo.env.portal.userName)
        .then(connectors => {
          const enabledConnectors = connectors?.filter(connector => connector.enabled) || [];
          const connectorsToDisplay = this.adminConnectorsExtensions.filter(connectorExtension => enabledConnectors.some(item => item.name === connectorExtension.componentOptions.name) || connectorExtension.componentOptions.defaultConnector).map(item => ({
            name: item?.componentOptions?.name,
            title: item?.componentOptions?.title,
            icon: item?.componentOptions?.icon || null,
            iconColorClass: item?.componentOptions?.iconColorClass || null,
            image: item?.componentOptions?.image || null,
          })) || [];
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
    refreshUserConnectorList() {
      // Get list of connectors from extensionRegistry
      this.adminConnectorsExtensions = extensionRegistry.loadComponents('gamification-admin-connector') || [];
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
  }
};
</script>
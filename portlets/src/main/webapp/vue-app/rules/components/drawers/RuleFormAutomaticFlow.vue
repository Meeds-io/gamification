<template>
  <v-app>
    <v-card-text class="px-0 dark-grey-color font-weight-bold">
      {{ $t('rule.form.label.application') }}
    </v-card-text>
    <v-combobox
      v-model="selectedConnector"
      :items="connectors"
      :filter="filterConnectors"
      :menu-props="{ closeOnContentClick: true }"
      :placeholder="$t('rule.form.label.application.Placeholder')"
      class="py-0"
      background-color="white"
      dense
      flat
      outlined
      solo
      clearable
      hide-no-data
      open-on-clear>
      <template #item="{ item }">
        <v-list-item @click="selectedConnector = item">
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
        </v-list-item>
      </template>
      <template #selection="data">
        <v-chip
          :key="JSON.stringify(data.item)"
          v-bind="data.attrs"
          color="white"
          :input-value="data.selected"
          :disabled="data.disabled"
          @click:close="data.parent.selectItem(data.item)">
          <v-icon
            v-if="data.item.icon"
            :class="data.item.iconColorClass"
            size="20"
            class="pe-2">
            {{ data.item.icon }}
          </v-icon>
          <img
            v-else
            :src="data.item.image"
            :alt="data.item.name"
            class="pe-2"
            width="28">
          {{ data.item.title }}
        </v-chip>
      </template>
    </v-combobox>
    <template v-if="selectedConnector">
      <v-card-text class="px-0 dark-grey-color font-weight-bold">
        {{ $t('rule.form.label.event') }}
      </v-card-text>
      <v-combobox
        v-model="trigger"
        :items="triggers"
        :filter="filterTriggers"
        :menu-props="{ closeOnContentClick: true }"
        :placeholder="$t('rule.form.label.event.placeholder')"
        class="py-0"
        background-color="white"
        dense
        flat
        outlined
        solo
        clearable
        hide-no-data
        hide-selected
        open-on-clear>
        <template #item="{ item }">
          <v-list-item @click="trigger = item">
            <rule-icon :rule-event="item" class="me-2" />
            <v-list-item-content>
              <v-list-item-title>
                {{ getTriggerLabel(item) }}
              </v-list-item-title>
            </v-list-item-content>
          </v-list-item>
        </template>
        <template #selection="data">
          <v-chip
            :key="JSON.stringify(data.item)"
            v-bind="data.attrs"
            color="white"
            :input-value="data.selected"
            :disabled="data.disabled"
            @click:close="data.parent.selectItem(data.item)">
            <rule-icon :rule-event="data.item" class="me-2" />
            {{ getTriggerLabel(data.item) }}
          </v-chip>
        </template>
      </v-combobox>
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
  created() {
    if (this.selectedTrigger) {
      this.trigger = this.selectedTrigger;
    }
    this.init();
  },
  watch: {
    selectedConnector() {
      if (this.selectedConnector) {
        this.retrieveTriggers();
        this.trigger = null;
        this.triggers = [];
      }
    },
    trigger() {
      this.$emit('triggerUpdated', this.trigger, this.selectedConnector.name);
    }
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
            this.selectedConnector = this.connectors.find(connector => connector.name === this.triggerType);
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
      return this.$gamificationConnectorService.getTriggers(this.selectedConnector.name)
        .then(triggers => {
          this.triggers = triggers.map(trigger => trigger.title);
        });
    },
  }
};
</script>
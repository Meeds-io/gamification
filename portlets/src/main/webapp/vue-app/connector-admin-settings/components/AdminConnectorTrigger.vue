<template>
  <div>
    <v-list-item class="ps-0 align-start">
      <v-list-item-avatar class="align-self-start position-static" tile>
        <rule-icon
          :size="40"
          :rule-event="triggerTitle"
          class="position-absolute" />
      </v-list-item-avatar>
      <v-list-item-content>
        <v-list-item-title class="mb-2">{{ triggerTitleLabel }}</v-list-item-title>
        <v-list-item-subtitle>{{ triggerDescription }}</v-list-item-subtitle>
        <div v-if="cancellerTriggersSize" class="d-flex flex-row pt-2px">
          <v-list-item-subtitle>
            {{ $t('gamification.label.cancelledBy') }}: <span class="font-weight-bold">{{ cancellerTriggersToDisplay }}</span>
            <v-btn
              v-if="cancellerTriggersSize > 2"
              depressed
              dark
              width="31"
              min-width="31"
              height="16"
              class="ms-2 text-caption pa-0 light-black-background rounded-xl"
              @click="$refs.cancellerEventsDrawer.open()">
              +{{ cancellerTriggersSize - 2 }}
            </v-btn>
          </v-list-item-subtitle>
        </div>
      </v-list-item-content>
    </v-list-item>
    <gamification-canceller-events-drawer
      v-if="trigger"
      ref="cancellerEventsDrawer"
      :event="trigger" />
  </div>
</template>

<script>
export default {
  props: {
    trigger: {
      type: Object,
      default: null
    },
  },
  computed: {
    triggerTitle() {
      return this.trigger?.title;
    },
    triggerTitleLabel() {
      return this.$t(`gamification.event.title.${this.triggerTitle}`);
    },
    triggerDescription() {
      return this.$t(`gamification.event.description.${this.triggerTitle}`);
    },
    cancellerTriggers() {
      return this.trigger?.canceller || [];
    },
    cancellerTriggersToDisplay() {
      return this.cancellerTriggers.slice(0, 2).map(event => this.$t(`gamification.event.title.${event}`)).join(' - ');
    },
    cancellerTriggersSize() {
      return this.cancellerTriggers.length;
    }
  }
};
</script>
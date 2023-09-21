<template>
  <div>
    <v-list-item class="ps-0 align-start">
      <rule-icon
        :size="40"
        :rule-event="eventTitle"
        class="me-4 py-2" />
      <v-list-item-content>
        <v-list-item-title class="text-subtitle-2 text-color mb-2">{{ eventTitleLabel }}</v-list-item-title>
        <v-list-item-subtitle class="text-sub-title">{{ eventDescription }}</v-list-item-subtitle>
        <div v-if="cancellerEventsSize" class="d-flex flex-row pt-2px">
          <v-list-item-subtitle class="text-sub-title">
            <span class="dark-grey-color">{{ $t('gamification.label.cancelledBy') }}: </span>{{ cancellerEventsToDisplay }}
            <v-btn
              v-if="cancellerEventsSize > 2"
              depressed
              dark
              width="31"
              min-width="31"
              height="16"
              class="ms-2 text-caption pa-0 light-black-background rounded-xl"
              @click="$refs.cancellerEventsDrawer.open()">
              +{{ cancellerEventsSize - 2 }}
            </v-btn>
          </v-list-item-subtitle>
        </div>
      </v-list-item-content>
    </v-list-item>
    <gamification-canceller-events-drawer
      v-if="event"
      ref="cancellerEventsDrawer"
      :event="event" />
  </div>
</template>

<script>
export default {
  props: {
    event: {
      type: Object,
      default: null
    },
  },
  computed: {
    eventTitle() {
      return this.event?.title;
    },
    eventTitleLabel() {
      return this.$t(`gamification.event.title.${this.eventTitle}`);
    },
    eventDescription() {
      return this.$t(`gamification.event.description.${this.eventTitle}`);
    },
    cancellerEvents() {
      return this.event?.cancellerEvents || [];
    },
    cancellerEventsToDisplay() {
      return this.cancellerEvents.slice(0, 2).map(event => this.$t(`gamification.event.title.${event}`)).join(' - ');
    },
    cancellerEventsSize() {
      return this.cancellerEvents.length;
    }
  }
};
</script>
<template>
  <v-list-item class="ps-0">
    <rule-icon
      :size="40"
      :rule-event="eventTitle"
      class="me-4" />
    <v-list-item-content>
      <v-list-item-title class="text-subtitle-2 dark-grey-color mb-2">{{ eventTitleLabel }}</v-list-item-title>
      <v-list-item-subtitle class="text-sub-title">{{ eventDescription }}</v-list-item-subtitle>
    </v-list-item-content>
  </v-list-item>
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
    }
  }
};
</script>
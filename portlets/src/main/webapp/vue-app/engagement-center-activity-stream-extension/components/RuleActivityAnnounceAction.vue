<template>
  <div v-if="canAnnounce" class="d-inline-flex ms-lg-4">
    <!-- Added for mobile -->
    <v-tooltip :disabled="isMobile" bottom>
      <template #activator="{ on, attrs }">
        <v-btn
          v-if="canAnnounce"
          :id="`AnnounceButton${activityId}`"
          class="pa-0 mt-0"
          text
          link
          small
          v-bind="attrs"
          v-on="on"
          @click="openAnnouncementDrawer">
          <div class="d-flex flex-lg-row flex-column">
            <v-icon
              class="baseline-vertical-align mx-auto disabled--text"
              size="14">
              fas fa-bullhorn
            </v-icon>
            <span class="mx-auto mt-1 mt-lg-0 ms-lg-2">
              {{ $t('rule.detail.Announce') }}
            </span>
          </div>
        </v-btn>
      </template>
      <span>
        {{ $t('rule.detail.Announce') }}
      </span>
    </v-tooltip>
  </div>
</template>

<script>
export default {
  props: {
    activity: {
      type: Object,
      default: null,
    },
    activityTypeExtension: {
      type: Object,
      default: null,
    },
  },
  computed: {
    rule() {
      return this.activity?.rule;
    },
    ruleId() {
      return this.rule?.id;
    },
    canAnnounce() {
      return this.rule?.userInfo?.context?.valid && this.rule?.type === 'MANUAL';
    },
    isMobile() {
      return this.$vuetify.breakpoint.name === 'sm' || this.$vuetify.breakpoint.name === 'xs';
    },
  },
  methods: {
    openAnnouncementDrawer() {
      this.$root.$emit('rule-detail-drawer-by-id', this.ruleId, true);
    },
  },
};
</script>
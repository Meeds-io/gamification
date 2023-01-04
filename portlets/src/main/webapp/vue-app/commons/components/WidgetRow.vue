<template>
  <v-list class="py-0" :disabled="disabled">
    <div v-if="$slots.title" class="subtitle-2">
      <slot name="title"></slot>
    </div>
    <v-list-item
      v-on="isChallengeIdProvided ? { click: clickEvent } : {}"
      :href="redirectionUrl"
      :dense="!normalHeight"
      class="px-0 no-ripple">
      <v-list-item-action-text v-if="$slots.icon" class="me-4">
        <slot name="icon"></slot>
      </v-list-item-action-text>
      <v-list-item-content>
        <v-list-item-title class="text-wrap">
          <slot name="content"></slot>
        </v-list-item-title>
      </v-list-item-content>
    </v-list-item>
  </v-list>
</template>
<script>
export default {
  props: {
    isChallengeIdProvided: {
      type: Boolean,
      default: false,
    },
    disabled: {
      type: Boolean,
      default: false,
    },
    redirectionUrl: {
      type: String,
      default: () => '',
    },
    normalHeight: {
      type: Boolean,
      default: false,
    },
    clickEventParam: {
      type: String,
      default: () => '',
    },
  },
  methods: {
    clickEvent() {
      if (this.isChallengeIdProvided) {
        document.dispatchEvent(new CustomEvent('widget-row-click-event', {detail: this.clickEventParam}));
      }
    }
  },
};
</script>
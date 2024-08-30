<template>
  <v-list
    :disabled="disabled"
    class="py-0 transparent">
    <div v-if="$slots.title" class="text-header">
      <slot name="title"></slot>
    </div>
    <v-list-item
      :dense="!normalHeight"
      class="px-0 flex-grow-1 flex-shrink-1"
      v-on="clickable && !disabled && {
        click: open,
      }">
      <v-list-item-action-text v-if="$slots.icon" class="me-4">
        <slot name="icon"></slot>
      </v-list-item-action-text>
      <v-list-item-content :class="dense && 'pa-0'">
        <v-list-item-title class="text-wrap">
          <slot></slot>
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
    normalHeight: {
      type: Boolean,
      default: false,
    },
    clickable: {
      type: Boolean,
      default: false,
    },
    dense: {
      type: Boolean,
      default: false,
    },
  },
  methods: {
    open() {
      if (!this.redirectionUrl) {
        this.$emit('open');
      }
    }
  },
};
</script>
<template>
  <v-list class="py-0" dense>
    <div v-if="$slots.title" class="subtitle-2">
      <slot name="title"></slot>
    </div>
    <v-list-item class="px-0" v-on="redirectionUrl ? { click: selectedRowUrl } : {}">
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
    redirectionUrl: {
      type: String,
      default: () => '',
    }
  },
  methods: {
    selectedRowUrl() {
      window.location.href = this.redirectionUrl;
    }
  },
};
</script>
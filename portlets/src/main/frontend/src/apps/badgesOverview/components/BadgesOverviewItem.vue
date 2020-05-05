<template>
  <div class="px-2">
    <v-avatar
      class="d-flex flex-column BadgeItemAvatar content-box-sizing clickable mx-auto"
      :title="badge.badgeLabel"
      :tile="!skeleton"
      @click="openDrawer">
      <img
        :class="skeleton && 'skeleton-background no-border'"
        :src="badge.avatar" />
    </v-avatar>
    <div
      :class="skeleton && 'skeleton-text skeleton-background skeleton-text-height skeleton-border-radius px-6'"
      class="d-block text-center mt-2 clickable"
      @click="openDrawer">
      {{ skeleton && '&nbsp;' || badge.domainLabel }}
    </div>
  </div>
</template>

<script>
export default {
  props: {
    badge: {
      type: Object,
      default: () => ({}),
    },
    skeleton: {
      type: Boolean,
      default: false,
    },
  },
  methods: {
    openDrawer() {
      this.$root.$emit('open-badge-drawer', this.badge);
    },
  },
};
</script>

<template>
  <v-avatar
    class="d-flex flex-column py-5 content-box-sizing clickable"
    tile
    @click="openDrawer">
    <img :src="badge.avatar">
    <div class="d-block text-center">
      {{ badge.domainLabel }}
    </div>
  </v-avatar>
</template>

<script>
export default {
  props: {
    badge: {
      type: Object,
      default: () => ({}),
    },
  },
  methods: {
    openDrawer() {
      this.$root.$emit('open-badge-drawer', this.badge);
    },
  },
};
</script>

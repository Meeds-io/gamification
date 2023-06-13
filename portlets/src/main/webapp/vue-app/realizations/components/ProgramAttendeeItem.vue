<template>
  <v-chip
    close
    class="identitySuggesterItem me-4 mt-4"
    @click:close="$emit('remove-attendee', program)">
    <v-avatar
      :style="programStyle"
      class="border-color"
      rounded
      left>
      <v-img :src="avatarUrl" />
    </v-avatar>
    <span class="text-truncate">
      {{ displayName }}
    </span>
  </v-chip>
</template>

<script>
export default {
  props: {
    program: {
      type: Object,
      default: () => ({}),
    },
  },
  computed: {
    avatarUrl() {
      return this.program?.avatarUrl || '';
    },
    displayName() {
      return this.program?.title;
    },
    programStyle() {
      return this.program?.color && `border: 1px solid ${this.program.color} !important;` || '';
    },
  },
};
</script>
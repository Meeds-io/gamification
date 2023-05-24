<template>
  <v-btn-toggle
    v-model="value"
    color="primary"
    mandatory
    outlined
    dense>
    <v-btn value="YOURS" text>
      <v-icon :color="!isOwned && 'primary'" class="me-2" small>fa-user</v-icon>
      {{ $t('gamification.achievement.yours') }}
    </v-btn>
    <v-btn value="OWNED" text>
      <v-icon :color="isOwned && 'primary'" class="me-2" small>fa-users-cog</v-icon>
      {{ $t('gamification.achievement.owned') }}
    </v-btn>
  </v-btn-toggle>
</template>
<script>
export default {
  data: () => ({
    value: 'YOURS',
  }),
  computed: {
    isOwned() {
      return this.value === 'OWNED';
    },
  },
  watch: {
    value() {
      this.$emit('input', this.isOwned);
    },
  },
};
</script>
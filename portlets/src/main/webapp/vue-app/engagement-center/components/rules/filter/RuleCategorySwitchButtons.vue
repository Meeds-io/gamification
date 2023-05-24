<template>
  <v-btn-toggle
    v-model="filter"
    :mandatory="!disabled"
    color="primary"
    outlined
    dense
    @change="$emit('change')">
    <v-btn
      value="TRENDS"
      text>
      <v-icon
        :color="isTrends && 'primary'"
        class="me-2"
        small>
        fa-fire
      </v-icon>
      {{ $t('gamification.actions.groupBy.trends') }}
    </v-btn>
    <v-btn
      value="PROGRAMS"
      text>
      <v-icon
        :color="isPrograms && 'primary'"
        class="me-2"
        small>
        fa-layer-group
      </v-icon>
      {{ $t('gamification.actions.groupBy.programs') }}
    </v-btn>
  </v-btn-toggle>
</template>
<script>
export default {
  props: {
    value: {
      type: Boolean,
      default: false,
    },
    disabled: {
      type: Boolean,
      default: false,
    },
  },
  data: () => ({
    filter: 'TRENDS',
    originalFilter: 'TRENDS',
  }),
  computed: {
    isPrograms() {
      return this.filter === 'PROGRAMS';
    },
    isTrends() {
      return this.filter === 'TRENDS';
    },
  },
  watch: {
    filter() {
      if (!this.disabled) {
        this.$nextTick().then(() => this.$emit('input', this.isPrograms));
      }
    },
    disabled() {
      if (this.disabled) {
        this.originalFilter = this.filter || this.originalFilter;
        this.$nextTick().then(() => this.filter = '');
      } else {
        this.$nextTick().then(() => this.filter = this.originalFilter);
      }
    },
  },
};
</script>
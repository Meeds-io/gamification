<template>
  <v-expansion-panels
    :value="domainIndexes"
    multiple
    flat>
    <challenges-list-item
      v-for="domain in domains"
      :key="domain.id"
      :domain="domain"
      :challenges="challengesByDomainId[domain.id]"
      :loading="loading"
      @load-more="$emit('load-more', domain.id)" />
  </v-expansion-panels>
</template>
<script>
export default {
  props: {
    domains: {
      type: Array,
      default: function() {
        return [];
      },
    },
    challengesByDomainId: {
      type: Object,
      default: function() {
        return {};
      },
    },
    loading: {
      type: Boolean,
      default: false,
    },
  },
  computed: {
    domainIndexes() {
      return this.domains.map((value, index) => index);
    },
  },
};
</script>

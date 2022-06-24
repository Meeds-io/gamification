<template>
  <v-expansion-panels
    :value="domainIndexes"
    multiple
    flat>
    <domain-challenges-list
      v-for="domain in domains"
      :key="domain.id"
      :domain="domain"
      :challenges="challengesByDomainId[domain.id]"
      :loading="loading" />
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
      return this.domains.map((_value, index) => index);
    },
  },
  methods: {
    announcementAdded(announcement) {
      this.listWinners.unshift({'userName': announcement.assignee});
      this.challenge.announcementsCount = this.challenge.announcementsCount + 1;
    },
  }
};
</script>

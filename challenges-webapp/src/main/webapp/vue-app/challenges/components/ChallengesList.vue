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
      @create-announce="createAnnouncement($event)"
      @confirm-delete="confirmDelete"
      @open-announcements-details="openAnnouncementsDetails($event)" />
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
  methods: {
    announcementAdded(announcement) {
      this.listWinners.unshift({'userName': announcement.assignee});
      this.challenge.announcementsCount = this.challenge.announcementsCount + 1;
    },
    createAnnouncement(challenge) {
      if (challenge){
        this.selectedChallenge = challenge;
        this.$nextTick().then(() =>  this.$refs.announceRef.open());       
      }
    },
    openAnnouncementsDetails(challenge) {
      if (challenge) {
        this.$root.$emit('open-winners-details', challenge?.id);
      }
    },
  }
};
</script>

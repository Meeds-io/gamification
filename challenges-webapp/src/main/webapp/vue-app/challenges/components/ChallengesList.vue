<template>
  <div>
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
        @load-more="$emit('load-more', domain.id)"
        @create-announce="createAnnouncement($event)"
        @open-challenge-details="openChallengeDetails($event)"
        @confirm-delete="confirmDelete"
        @open-announcements-details="openAnnouncementsDetails($event)" />
    </v-expansion-panels>
    <exo-confirm-dialog
      ref="deleteChallengeConfirmDialog"
      :title="$t('challenges.delete')"
      :message="$t('challenges.deleteConfirmMessage')"
      :ok-label="$t('challenges.ok')"
      :cancel-label="$t('challenges.button.cancel')"
      @ok="deleteChallenge"
       />
    <challenge-details-drawer
      ref="challenge"
      :challenge="selectedChallenge"
      />
    <announce-drawer
      :challenge="selectedChallenge"
      @announcement-added="announcementAdded($event)"
      ref="announceRef" />
    <challenge-winners-details
      :challenge-id="selectedChallenge && selectedChallenge.id"
      ref="winnersDetails" 
      />
  </div>
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
  data: () => ({
    selectedChallenge: {},
  }),
  computed: {
    domainIndexes() {
      return this.domains.map((value, index) => index);
    },
  },
  methods: {
    deleteChallenge() {
      this.$challengesServices.deleteChallenge(this.challenge.id).then(() =>{
        this.$root.$emit('show-alert', {type: 'success',message: this.$t('challenges.deleteSuccess')});
        this.$root.$emit('challenge-deleted');
      })
        .catch(e => {
          let msg = '';
          if (e.message === '401' || e.message === '403') {
            msg = this.$t('challenges.deletePermissionDenied');
          } else if (e.message  === '404') {
            msg = this.$t('challenges.notFound');
          } else  {
            msg = this.$t('challenges.deleteErrorSave');
          }
          this.$root.$emit('show-alert', {type: 'error',message: msg});
        });
    },
     announcementAdded(announcement) {
      this.listWinners.unshift({'userName': announcement.assignee});
      this.challenge.announcementsCount = this.challenge.announcementsCount + 1;
    },
    createAnnouncement(challenge) {
      if(challenge){
        this.selectedChallenge = challenge;
              this.$nextTick().then(() =>  this.$refs.announceRef.open());       
      }
    },
     openAnnouncementsDetails(challenge) {
       if(challenge){
         this.selectedChallenge = challenge;
         this.$nextTick().then(() =>  this.$refs.winnersDetails.open());  
       }
    },
    openChallengeDetails(challenge) {
      if (this.$refs.challenge && challenge){
        this.selectedChallenge = challenge;
        window.history.replaceState('challenges', this.$t('challenges.challenges'), `${eXo.env.portal.context}/${eXo.env.portal.portalName}/challenges/${challenge.id}`);
        this.$nextTick().then(() =>  this.$refs.challenge.open()); 
      }
    },
    confirmDelete() {
      this.$refs.deleteChallengeConfirmDialog.open();
    },
  }
};
</script>

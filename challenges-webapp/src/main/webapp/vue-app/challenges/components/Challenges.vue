<template>
  <v-app
    class="challenges-application border-box-sizing"
    :class="classWelcomeMessage"
    role="main"
    flat>
    <v-toolbar
      color="transparent"
      flat
      class="pa-4">
      <div class="border-box-sizing clickable addChallengeButton" v-if="canAddChallenge">
        <v-btn class="btn btn-primary" @click="openChallengeDrawer">
          <v-icon>fas fa-plus</v-icon>
          <span class="mx-2 d-none d-lg-inline">
            {{ $t('challenges.button.addChallenge') }}
          </span>
        </v-btn>
      </div>
    </v-toolbar>
    <welcome-message
      v-if="displayWelcomeMessage"
      :can-add-challenge="canAddChallenge" />
    <challenges-list
      v-else
      :domains="domainsHavingChallenges"
      :challenges-by-domain-id="challengesByDomainId"
      :loading="loading"
      class="pl-2 pt-5" />
    <challenge-drawer ref="challengeDrawer" :can-add-challenge="canAddChallenge" />
    <challenge-details-drawer ref="challengeDetails" />
    <challenge-winners-details ref="winnersDetails" />
    <exo-confirm-dialog
      ref="deleteChallengeConfirmDialog"
      :title="$t('challenges.delete')"
      :message="$t('challenges.deleteConfirmMessage')"
      :ok-label="$t('challenges.ok')"
      :cancel-label="$t('challenges.button.cancel')"
      @ok="deleteChallenge" />
    <announce-drawer
      ref="announceDrawer"
      :challenge="selectedChallenge" />
      <challenge-alert />
  </v-app>
</template>
<script>
export default {
  data: () => ({
    selectedChallenge: null,
    canAddChallenge: false,
    loading: true,
    domainsWithChallenges: [],
    announcementsPerChallenge: 2,
  }),
  computed: {
    classWelcomeMessage() {
      return this.displayWelcomeMessage && 'emptyChallenges' || '';
    },
    domainsHavingChallenges() {
      return this.domainsWithChallenges.filter(domain => domain.challenges.length > 0);
    },
    displayWelcomeMessage() {
      return !this.loading && !this.domainsHavingChallenges.length;
    },
    challengesByDomainId() {
      const challengesByDomainId = {};
      this.domainsHavingChallenges.forEach(domain => {
        challengesByDomainId[domain.id] = domain.challenges;
      });
      return challengesByDomainId;
    },
    domainsById() {
      const domainsById = {};
      this.domainsHavingChallenges.forEach(domain => {
        domainsById[domain.id] = domain;
      });
      return domainsById;
    },
    challengePerPage() {
      if (this.$vuetify.breakpoint.xs) {
        return 2;
      } else if (this.$vuetify.breakpoint.smAndDown) {
        return 4;
      } else if (this.$vuetify.breakpoint.lgAndDown) {
        return 8;
      } else {
        return 12;
      }
    }
  },
  created() {
    this.$challengesServices.canAddChallenge()
      .then(canAddChallenge => this.canAddChallenge = canAddChallenge);
    this.getChallenges(false);
    this.$root.$on('challenge-added', this.pushChallenge);
    this.$root.$on('challenge-updated', this.refreshChallenges);
    this.$root.$on('challenge-deleted', this.refreshChallenges);
    this.$root.$on('challenge-load-more', this.loadMore);
    this.$root.$on('challenge-delete-confirm', this.confirmDelete);
    const urlPath = document.location.pathname;
    const challengeId = urlPath.match( /\d+/ ) && urlPath.match( /\d+/ ).join('');
    if (challengeId) {
      setTimeout(() => {
        this.$challengesServices.getChallengeById(challengeId).then(challenge => {
          if (challenge && challenge.id) {
            this.$root.$emit('open-challenge-details', challenge);
            window.history.replaceState('challenges', this.$t('challenges.challenges'), `${eXo.env.portal.context}/${eXo.env.portal.portalName}/challenges/${challengeId}`);
          } else {
            window.history.replaceState('challenges', this.$t('challenges.challenges'), `${eXo.env.portal.context}/${eXo.env.portal.portalName}/challenges`);
            this.showAlert('error', this.$t('challenges.viewChallengeError'));
          }
        });
      }, 10);
    }
  },
  methods: {
    pushChallenge(challenge) {
      if (challenge?.program?.id && this.challengesByDomainId[challenge.program.id]) {
        this.challengesByDomainId[challenge.program.id].push(challenge);
      }
    },
    refreshChallenges() {
      this.getChallenges(false);
    },
    loadMore(domainId) {
      this.getChallenges(true, domainId);
    },
    openChallengeDrawer(){
      this.$refs.challengeDrawer.open();
    },
    getChallenges(append, domainId) {
      this.loading = true;
      const offset = append && domainId && this.challengesByDomainId[domainId]?.length || 0;
      this.$challengesServices.getAllChallengesByUser(offset, this.challengePerPage, this.announcementsPerChallenge, domainId, !domainId)
        .then(result => {
          if (domainId) {
            if (append) {
              this.domainsById[domainId].challenges = this.challengesByDomainId[domainId].concat(result);
            } else {
              this.domainsById[domainId].challenges = result;
            }
          } else {
            this.domainsWithChallenges = result;
          }
        }).finally(() => {
          this.loading = false;
          this.$nextTick().then(() => document.dispatchEvent(new CustomEvent('hideTopBarLoading'))) ;
        });
    },
    deleteChallenge() {
      this.$challengesServices.deleteChallenge(this.selectedChallenge.id).then(() =>{
        this.showAlert('success', this.$t('challenges.deleteSuccess'));
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
          this.showAlert('error', msg);
        });
    },
    confirmDelete(challenge) {
      this.selectedChallenge = challenge;
      this.$refs.deleteChallengeConfirmDialog.open();
    },
    showAlert(alertType, alertMessage){
      this.$root.$emit('challenge-notification-alert', {
        type: alertType,
        message: alertMessage,
      });
    }
  }
};
</script>
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
          <i class="fas fa-plus pr-1"></i>
          <span class="ms-2 d-none d-lg-inline">
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
      class="pl-2 pt-5"
      @load-more="loadMore"
      @edit-challenge="editChallenge($event)" />
    <challenge-drawer 
      ref="challengeDrawer" 
      :can-add-challenge="canAddChallenge" />
    <challenge-details-drawer 
      ref="challengeDetails" />
    <challenge-alert />
    <announce-drawer
      ref="announceDrawer"
      :challenge="selectedChallenge" />
  </v-app>
</template>
<script>
export default {
  data: () => ({
    canAddChallenge: false,
    loading: true,
    domainsWithChallenges: [],
    challengePerPage: 12,
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
  },
  created() {
    this.$challengesServices.canAddChallenge()
      .then(canAddChallenge => this.canAddChallenge = canAddChallenge);
    this.getChallenges(false);
    this.$root.$on('challenge-added', this.pushChallenge);
    this.$root.$on('challenge-updated', this.refreshChallenges);
    this.$root.$on('challenge-deleted', this.refreshChallenges);
    const urlPath = document.location.pathname;
    const challengeId = urlPath.match( /\d+/ ) && urlPath.match( /\d+/ ).join('');
    if (challengeId) {
      setTimeout(() => {
        this.$challengesServices.getChallengeById(challengeId).then(challenge => {
          if (challenge && challenge.id) {
            this.$refs.challengeDetails.challenge = challenge;
            window.history.replaceState('challenges', this.$t('challenges.challenges'), `${eXo.env.portal.context}/${eXo.env.portal.portalName}/challenges/${challengeId}`);
            this.$refs.challengeDetails.open();
          } else {
            window.history.replaceState('challenges', this.$t('challenges.challenges'), `${eXo.env.portal.context}/${eXo.env.portal.portalName}/challenges`);
            this.$root.$emit('show-alert', {type: 'error', message: this.$t('challenges.viewChallengeError')});
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
    editChallenge(challenge) {
      this.$refs.challengeDrawer.challenge =JSON.parse(JSON.stringify(challenge));
      this.$nextTick().then(() => this.openChallengeDrawer());
    }
  }
};
</script>
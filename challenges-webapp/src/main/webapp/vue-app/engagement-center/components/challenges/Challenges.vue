<template>
  <div
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
      <v-spacer />
      <div class="challengeFilter">
        <v-text-field
          v-model="search"
          :placeholder="$t('challenges.filter.search')"
          prepend-inner-icon="fa-filter"
          single-line
          hide-details
          class="pa-0 mx-3" />
      </div>
    </v-toolbar>
    <challenge-welcome-message
      v-if="displayWelcomeMessage"
      :can-add-challenge="canAddChallenge" />
    <challenge-no-results v-else-if="displayNoSearchResult" />
    <challenges-list
      v-else-if="displayChallengesList"
      :domains="domainsHavingChallenges"
      :challenges-by-domain-id="challengesByDomainId"
      :loading="loading"
      class="pl-2 pt-5" />

    <challenge-drawer v-if="canAddChallenge" ref="challengeDrawer" />
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
  </div>
</template>
<script>
export default {
  data: () => ({
    selectedChallenge: null,
    canAddChallenge: false,
    loading: true,
    domainsWithChallenges: [],
    announcementsPerChallenge: 2,
    search: '',
    startSearchAfterInMilliseconds: 600,
    endTypingKeywordTimeout: 50,
    startTypingKeywordTimeout: 0,
    typing: false,
  }),
  computed: {
    classWelcomeMessage() {
      return this.displayWelcomeMessage && 'emptyChallenges' || '';
    },
    domainsHavingChallenges() {
      return this.domainsWithChallenges.filter(domain => domain.challenges.length > 0);
    },
    displayWelcomeMessage() {
      return !this.typing && !this.loading && !this.domainsHavingChallenges.length && !this.search?.length;
    },
    displayNoSearchResult() {
      return !this.typing && !this.loading && !this.domainsHavingChallenges.length && this.search?.length;
    },
    displayChallengesList() {
      return this.domainsHavingChallenges.length;
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
  watch: {
    search()  {
      this.startTypingKeywordTimeout = Date.now() + this.startSearchAfterInMilliseconds;
      if (!this.typing) {
        this.typing = true;
        this.waitForEndTyping();
      }
    },
    loading() {
      if (this.loading) {
        document.dispatchEvent(new CustomEvent('displayTopBarLoading'));
      } else {
        document.dispatchEvent(new CustomEvent('hideTopBarLoading'));
      }
    },
  },
  created() {
    const promises = [];
    promises.push(this.computeCanAddChallenge());    
    promises.push(this.getChallenges(false));
    this.$root.$on('challenge-added', this.pushChallenge);
    this.$root.$on('challenge-updated', this.refreshChallenges);
    this.$root.$on('challenge-deleted', this.refreshChallenges);
    this.$root.$on('challenge-load-more', this.loadMore);
    this.$root.$on('challenge-delete-confirm', this.confirmDelete);
    const urlPath = document.location.pathname;
    const challengeId = urlPath.match( /\d+/ ) && urlPath.match( /\d+/ ).join('');
    if (challengeId) {
      setTimeout(() => {
        const retrieveChallengePromise = this.$challengesServices.getChallengeById(challengeId)
          .then(challenge => {
            if (challenge && challenge.id) {
              this.$root.$emit('open-challenge-details', challenge);
              window.history.replaceState('challenges', this.$t('challenges.challenges'), `${eXo.env.portal.context}/${eXo.env.portal.portalName}/challenges/${challengeId}`);
            } else {
              window.history.replaceState('challenges', this.$t('challenges.challenges'), `${eXo.env.portal.context}/${eXo.env.portal.portalName}/challenges`);
              this.showAlert('error', this.$t('challenges.viewChallengeError'));
            }
          });
        promises.push(retrieveChallengePromise);
      }, 10);
    }
    Promise.all(promises)
      .finally(() => this.$root.$applicationLoaded());
  },
  methods: {
    pushChallenge(challenge) {
      if (challenge?.program?.id && this.challengesByDomainId[challenge.program.id]) {
        this.challengesByDomainId[challenge.program.id].push(challenge);
      }
    },
    computeCanAddChallenge() {
      return this.$challengesServices.canAddChallenge()
        .then(canAddChallenge => this.canAddChallenge = canAddChallenge);
    },
    refreshChallenges() {
      return this.getChallenges(false);
    },
    loadMore(domainId) {
      return this.getChallenges(true, domainId);
    },
    openChallengeDrawer(){
      this.$refs.challengeDrawer.open();
    },
    getChallenges(append, domainId) {
      this.loading = true;
      const offset = append && domainId && this.challengesByDomainId[domainId]?.length || 0;
      return this.$challengesServices.getAllChallengesByUser(this.search, offset, this.challengePerPage, this.announcementsPerChallenge, domainId, !domainId)
        .then(result => {
          if (!result) {
            return;
          }
          if (domainId) {
            const program = Object.assign({}, this.domainsById[domainId]);
            Object.assign(program, {challenges: null});
            result.forEach(challenge => challenge.program = program);
            if (append) {
              this.domainsById[domainId].challenges = this.challengesByDomainId[domainId].concat(result);
            } else {
              this.domainsById[domainId].challenges = result;
            }
          } else {
            result.forEach(domain => {
              if (domain.challenges) {
                const program = Object.assign({}, domain);
                Object.assign(program, {challenges: null});
                Object.values(domain.challenges).forEach(challenge => challenge.program = program);
              }
            });
            this.domainsWithChallenges = result;
          }
        }).finally(() => this.loading = false);
    },
    deleteChallenge() {
      this.loading = true;
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
        })
        .finally(() => this.loading = false);
    },
    confirmDelete(challenge) {
      this.selectedChallenge = challenge;
      this.$refs.deleteChallengeConfirmDialog.open();
    },
    showAlert(alertType, alertMessage){
      this.$challengeUtils.displayAlert({
        type: alertType,
        message: alertMessage,
      });
    },
    waitForEndTyping() {
      window.setTimeout(() => {
        if (Date.now() > this.startTypingKeywordTimeout) {
          this.typing = false;
          this.getChallenges(false, false);
        } else {
          this.waitForEndTyping();
        }
      }, this.endTypingKeywordTimeout);
    },
  }
};
</script>
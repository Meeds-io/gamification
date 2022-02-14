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
    <template v-if="displayChallenges">
      <div class="pl-2 pt-5">
        <challenges-list :challenges="challengesToDisplay" @edit-challenge="editChallenge($event)" />
      </div>
    </template>
    <template v-else>
      <welcome-message
        :can-add-challenge="canAddChallenge" />
    </template>
    <challenge-drawer ref="challengeDrawer" />
    <v-alert
      v-model="alert"
      :type="type"
      class="walletAlert"
      dismissible>
      {{ message }}
    </v-alert>
    <v-row class="ml-6 mr-6 mb-6 mt-n4 d-none d-lg-inline">
      <v-btn
        v-if="showLoadMoreButton"
        :loading="loading"
        :disabled="loading"
        class="loadMoreButton ma-auto mt-4 btn"
        block
        @click="loadMore">
        {{ $t('challenges.button.ShowMore') }}
      </v-btn>
    </v-row>
  </v-app>
</template>
<script>
export default {
  data: () => ({
    canAddChallenge: false,
    loading: true,
    challenges: [],
    showLoadMoreButton: false,
    challengePerPage: 20,
    announcementsPerChallenge: 2,
    displayChallenges: true,
    alert: false,
    type: '',
    message: '',
    search: '',
  }),
  computed: {
    classWelcomeMessage() {
      return !this.displayChallenges ? 'emptyChallenges': '';
    },
    challengesToDisplay() {
      let challenges = this.challenges.slice();
      if (this.search && this.search.trim().length) {
        const searchTerm = this.search.trim().toLowerCase();
        challenges = challenges.slice().filter(challenge => (challenge.title && challenge.title.toLowerCase().indexOf(searchTerm)) >= 0 || (challenge.description && challenge.description.toLowerCase().indexOf(searchTerm) >= 0));
      }
      return challenges;
    }
  },
  created() {
    this.$challengesServices.canAddChallenge().then(canAddChallenge => {
      this.canAddChallenge = canAddChallenge;
    });
    this.getChallenges(false);
    this.$root.$on('show-alert', message => {
      this.displayMessage(message);
    });
    this.$root.$on('challenge-added', this.pushChallenge);
    this.$root.$on('challenge-updated', this.refreshChallenges);
  },
  methods: {
    pushChallenge(event) {
      if (event) {
        if (this.challenges.length <= this.challengePerPage){
          this.challenges.push(event);
        } else {
          this.showLoadMoreButton = true;
        }

      }
    },
    refreshChallenges() {
      this.getChallenges(false);
    },
    loadMore() {
      this.getChallenges();
    },
    openChallengeDrawer(){
      this.$refs.challengeDrawer.open();
    },
    getChallenges(append = true) {
      this.loading = true;
      const offset = append ? this.challenges.length : 0;
      this.$challengesServices.getAllChallengesByUser(offset, this.challengePerPage, this.announcementsPerChallenge).then(challenges => {
        if (challenges.length >= this.challengePerPage) {
          this.showLoadMoreButton = true;
        } else {
          this.showLoadMoreButton = false;
        }
        this.challenges = append && this.challenges.concat(challenges) || challenges;
        this.displayChallenges = this.challenges && this.challenges.length;
      }).finally(() => {
        this.loading = false;
      });
    },
    displayMessage(message) {
      this.message=message.message;
      this.type=message.type;
      this.alert = true;
      window.setTimeout(() => this.alert = false, 5000);
    },
    editChallenge(challenge) {
      this.$refs.challengeDrawer.challenge = challenge;
      this.$nextTick().then(() => this.openChallengeDrawer());
    },
  }
};
</script>
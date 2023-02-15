<!--
This file is part of the Meeds project (https://meeds.io/).
Copyright (C) 2022 Meeds Association
contact@meeds.io
This program is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 3 of the License, or (at your option) any later version.
This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.
You should have received a copy of the GNU Lesser General Public License
along with this program; if not, write to the Free Software Foundation,
Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
-->
<template>
  <div
    id="ChallengesApplication"
    class="challenges-application border-box-sizing"
    :class="classWelcomeMessage"
    role="main">
    <v-toolbar
      flat>
      <div v-if="displayAddChallengeButton" class="border-box-sizing clickable">
        <v-btn
          id="engagementCenterAddChallengeBtn"
          class="btn btn-primary"
          @click="openChallengeDrawer">
          <v-icon small>fas fa-plus</v-icon>
          <span class="mx-2 d-none d-lg-inline text-capitalize-first-letter subtitle-1">
            {{ $t('challenges.button.addChallenge') }}
          </span>
        </v-btn>
      </div>
      <v-spacer />
      <div class="challengeFilter text-center d-flex align-center justify-space-around">
        <v-text-field
          id="EngagementCenterApplicationSearchFilter"
          v-model="search"
          :placeholder="$t('challenges.filter.search')"
          prepend-inner-icon="fa-filter"
          single-line
          hide-details
          class="pa-0 mx-3" />
        <v-tooltip
          id="EngagementCenterApplicationSearchFilterTooltip"
          :value="displayMinimumCharactersToolTip"
          attach>
          <span> {{ $t('challenges.filter.searchTooltip') }} </span>
        </v-tooltip>
      </div>
      <div class="pt-1">
        <select
          id="EngagementCenterApplicationChallengesQuickFilter"
          v-model="filter"
          class="my-auto ignore-vuetify-classes text-truncate challengeQuickFilter mb-3"
          @change="refreshChallenges">
          <option
            v-for="filter in challengesFilter"
            :key="filter.value"
            :value="filter.value">
            <span class="d-none d-lg-inline">
              {{ filter.text }}
            </span>
          </option>
        </select>
      </div>
    </v-toolbar>
    <v-card flat>
      <v-progress-linear
        v-if="loading"
        indeterminate
        height="2"
        color="primary" />
      <engagement-center-welcome-message
        v-else-if="displayWelcomeMessage">
        <template #content>
          <div class="mx-4 my-6 dark-grey-color">
            <p class="align-center font-weight-bold mb-5"> {{ $t('challenges.welcomeMessage') }} </p>
            <p v-if="canAddChallenge" class="align-center"> {{ $t('challenges.welcomeMessageForManager') }} </p>
            <p v-else class="align-center"> {{ $t('challenges.welcomeMessageForUser') }} </p>
          </div>
        </template>
      </engagement-center-welcome-message>
      <engagement-center-no-results
        v-else-if="displayNoSearchResult"
        :info="$t('challenges.search.noResults')"
        :info-message="notFoundInfoMessage" />
      <challenges-list
        v-else-if="displayChallengesList"
        :domains="domainsHavingChallenges"
        :challenges-by-domain-id="challengesByDomainId"
        :can-edit-challenge="canAddChallenge"
        class="pl-2 pt-8" />
    </v-card>
    <exo-confirm-dialog
      ref="deleteChallengeConfirmDialog"
      :title="$t('challenges.delete')"
      :message="$t('challenges.deleteConfirmMessage')"
      :ok-label="$t('challenges.ok')"
      :cancel-label="$t('engagementCenter.button.cancel')"
      @ok="deleteChallenge" />
  </div>
</template>
<script>
export default {
  props: {
    challengeId: {
      type: Number,
      default: null
    },
    canAddChallenge: {
      type: Boolean,
      default: false,
    }
  },
  data: () => ({
    selectedChallenge: null,
    loading: true,
    domains: [],
    domainsWithChallenges: [],
    announcementsPerChallenge: 2,
    search: '',
    startSearchAfterInMilliseconds: 600,
    endTypingKeywordTimeout: 50,
    startTypingKeywordTimeout: 0,
    typing: false,
    displayMinimumCharactersToolTip: false,
    filter: 'STARTED',
    challengeIdFromUrl: null,
  }),
  computed: {
    displayAddChallengeButton() {
      return this.canAddChallenge || this.domains?.filter(domain => domain.owners?.filter(owner => Number(owner) === Number(eXo.env.portal.userIdentityId)).length)?.length;
    },
    classWelcomeMessage() {
      return this.displayWelcomeMessage && 'emptyChallenges' || '';
    },
    domainsHavingChallenges() {
      return this.domainsWithChallenges.filter(domain => domain.challenges.length > 0);
    },
    displayWelcomeMessage() {
      return !this.typing && !this.loading && !this.domainsHavingChallenges.length && !this.search?.length && (this.filter === 'ALL' || this.filter === 'STARTED');
    },
    notFoundInfoMessage() {
      if (this.filter === 'NOT_STARTED' && !this.search?.length) {
        return this.$t('challenges.filter.upcomingNoResultsMessage');
      } else if (this.filter === 'ENDED' && !this.search?.length) {
        return this.$t('challenges.filter.endedNoResultsMessage');
      } else {
        return this.$t('challenges.search.noResultsMessage');
      }
    },
    displayNoSearchResult() {
      return !this.typing && !this.loading && !this.domainsHavingChallenges.length && (this.search?.length || (this.filter === 'NOT_STARTED' || this.filter === 'ENDED'));
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
      this.domainsWithChallenges.forEach(domain => {
        domainsById[domain.id] = domain;
      });
      return domainsById;
    },
    challengePerPage() {
      if (this.$vuetify.breakpoint.width <= 768) {
        return 5;
      } else {
        return 6;
      }
    },
    challengesFilter() {
      return [{
        text: this.$t('challenges.filter.allChallenges'),
        value: 'ALL',
      },{
        text: this.$t('challenges.filter.activeChallenges'),
        value: 'STARTED',
      },{
        text: this.$t('challenges.filter.UpcomingChallenges'),
        value: 'NOT_STARTED',
      },{
        text: this.$t('challenges.filter.endedChallenges'),
        value: 'ENDED',
      }];
    },
    providedId() {
      return this.challengeIdFromUrl ? this.challengeIdFromUrl : this.challengeId;
    },
  },
  watch: {
    search()  {
      this.displayMinimumCharactersToolTip = false;
      if (!this.search?.length) {
        this.getChallenges(false);
        return;
      } else if (this.search.length < 3) {
        this.displayMinimumCharactersToolTip = true;
        return;
      }
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
    promises.push(this.getChallenges(false));
    this.$root.$on('challenge-added', this.pushChallenge);
    this.$root.$on('challenge-updated', this.refreshChallenges);
    this.$root.$on('challenge-deleted', this.refreshChallenges);
    this.$root.$on('challenge-load-more', this.loadMore);
    this.$root.$on('challenge-delete-confirm', this.confirmDelete);
    const urlPath = document.location.pathname;
    if (urlPath.indexOf(`${eXo.env.portal.context}/${eXo.env.portal.portalName}/contributions/challenges`) > -1) {
      this.challengeIdFromUrl = urlPath.match( /\d+/ ) && urlPath.match( /\d+/ ).join('');
      if (this.providedId) {
        setTimeout(() => {
          const retrieveChallengePromise = this.$challengesServices.getChallengeById(this.providedId)
            .then(challenge => {
              if (challenge?.id) {
                this.$root.$emit('rule-detail-drawer', challenge);
                window.history.replaceState('challenges', this.$t('challenges.challenges'), `${eXo.env.portal.context}/${eXo.env.portal.portalName}/contributions/challenges/${this.providedId}`);
              } else {
                window.history.replaceState('challenges', this.$t('challenges.challenges'), `${eXo.env.portal.context}/${eXo.env.portal.portalName}/contributions/challenges`);
                this.showAlert('error', this.$t('challenges.viewChallengeError'));
              }
            });
          promises.push(retrieveChallengePromise);
        }, 10);
      }
    }

    Promise.all(promises)
      .finally(() => this.$root.$applicationLoaded());
  },
  methods: {
    pushChallenge(challenge) {
      if (challenge?.program?.id) {
        this.getChallenges(false, challenge.program.id);
      }
    },
    refreshChallenges() {
      return this.getChallenges(false);
    },
    loadMore(domainId) {
      return this.getChallenges(true, domainId);
    },
    openChallengeDrawer() {
      this.$root.$emit('edit-challenge-details');
    },
    getChallenges(append, domainId) {
      this.loading = true;
      const offset = append && domainId && this.challengesByDomainId[domainId]?.length || 0;
      return this.$challengesServices.getAllChallengesByUser(this.search, offset, this.challengePerPage, this.announcementsPerChallenge, domainId, !domainId, this.filter)
        .then(result => {
          if (!result) {
            return;
          }
          this.domains = result;
          if (domainId) {
            const program = this.domainsById[domainId] || {};
            if (!program.challenges) {
              program.challenges = [];
            }
            result.forEach(challenge => challenge.program = program);
            if (append) {
              program.challenges = this.challengesByDomainId[domainId].concat(result);
            } else {
              program.challenges = result || [];
              if (!program.challengesSize || program.challengesSize < 6) {
                program.challengesSize = result.length;
              }
            }
          } else {
            result.forEach(domain => {
              if (domain.challenges) {
                const program = Object.assign({}, domain);
                delete program.challenges;
                domain.challenges.forEach(challenge => challenge.program = program);
              } else {
                domain.challenges = [];
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
      this.$engagementCenterUtils.displayAlert(alertMessage, alertType);
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
    }
  }
};
</script>
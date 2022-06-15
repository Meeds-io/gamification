<template>
  <v-app id="ChallengeCard">
    <v-card
      class="mx-auto card cardOfChallenge"
      height="230"
      max-height="230"
      outlined>
      <div class="contentCard">
        <v-list-item class="pa-0" three-line>
          <v-list-item-content class="title pl-4 pr-4 pt-3">
            <div class="d-flex">
              <div class="status">
                <i class="uiIconStatus iconStatus" :class="classStatus"></i> <span class="date">{{ getStatus() }}</span>
              </div>
              <div class="edit">
                <v-menu
                  v-if="enableEdit"
                  v-model="showMenu"
                  offset-y
                  attach
                  :left="!$vuetify.rtl"
                  :right="$vuetify.rtl">
                  <template #activator="{ on }">
                    <v-btn
                      icon
                      class="ml-2"
                      v-on="on"
                      @blur="closeMenu()">
                      <v-icon>mdi-dots-vertical</v-icon>
                    </v-btn>
                  </template>
                  <v-list>
                    <v-list-item class="editList" @mousedown="$event.preventDefault()">
                      <v-list-item-title class="editLabel" @click="$emit('edit', challenge)">{{ $t('challenges.edit') }}</v-list-item-title>
                    </v-list-item>
                    <v-list-item
                      v-if="enableDelete"
                      class="editList"
                      @mousedown="$event.preventDefault()">
                      <v-list-item-title class="editLabel" @click="confirmDelete">{{ $t('challenges.delete') }}</v-list-item-title>
                    </v-list-item>
                  </v-list>
                </v-menu>
              </div>
            </div>
            <div class="contentChallenge" @click="showDetails(challenge.id)">
              <v-list-item-subtitle class="px-5 mb-2 mt-1 subtitleChallenge">
                {{ challenge && challenge.title }}
              </v-list-item-subtitle>
            </div>
            <div class="points title mb-1">
              <span>
                <i class="fas fa-trophy trophy"></i>
                {{ challenge && challenge.points }} {{ $t('challenges.label.points') }}
              </span>
            </div>
          </v-list-item-content>
        </v-list-item>
      </div>

      <div class="footer assigneeAvatars d-flex">
        <div class="winners pa-2" v-if="challenge && challenge.announcementsCount === 0 ">
          <p
            class="emptyWinners my-auto pl-2 align-self-end text-no-wrap pt-1">
            {{ challenge && challenge.announcementsCount }} {{ $t('challenges.winners.details') }}
          </p>
        </div>
        <div
          v-else
          :class="showAllAvatarList && 'AllUsersAvatar'"
          class="winners winnersAvatarsList d-flex flex-nowrap my-2">
          <exo-user-avatars-list
            ref="announcementsUsersAvatar"
            :users="avatarToDisplay"
            :max="2"
            :default-length="announcementCount"
            :icon-size="28"
            retrieve-extra-information
            @open-detail="openDetails()" />
          <p
            class="announcesNumber my-auto pl-2 align-self-end caption text-no-wrap pt-1"
            @click="openDetails">
            {{ challenge && challenge.announcementsCount }} {{ $t('challenges.label.announces') }}
          </p>
        </div>
        <div class="addAnnounce">
          <v-btn
            class="btn btnAdd mx-1"
            :disabled="!enableAnnounce"
            :title="showMessage"
            @click="$emit('create-announce', challenge )">
            {{ $t('challenges.button.announce') }}
          </v-btn>
        </div>
      </div>
    </v-card>
    <exo-confirm-dialog
      ref="deleteChallengeConfirmDialog"
      :title="$t('challenges.delete')"
      :message="$t('challenges.deleteConfirmMessage')"
      :ok-label="$t('challenges.ok')"
      :cancel-label="$t('challenges.button.cancel')"
      @ok="deleteChallenge" />
    <challenge-details-drawer
      ref="challenge"
      :challenge="challenge" />
    <challenge-winners-details
      :challenge-id="challenge && challenge.id"
      ref="winnersDetails" />
  </v-app>
</template>

<script>

export default {
  props: {
    challenge: {
      type: Object,
      default: null
    }
  },
  data: () => ({
    showMenu: false,
    label: '',
    status: '',
    listWinners: []
  }),
  computed: {
    avatarToDisplay () {
      return this.listWinners;
    },
    showMessage() {
      if (this.challenge && this.challenge.userInfo && !this.challenge.userInfo.canAnnounce) {
        return  this.$t('challenges.permissionDenied');
      } else if (this.status === 'Starts') {
        return  this.$t('challenges.challengeNotStarted');
      } else if (this.status === 'Ended') {
        return  this.$t('challenges.challengeEnded');
      } else if ( this.ChallengeProgramEnabled ) {
        return  this.$t('challenges.domainDeleteOrDisabled');
      } else {
        return '';
      }
    },
    classStatus() {
      if (this.status === 'Starts') {
        return 'startsColor';
      } else if (this.status === 'Ended') {
        return 'endedColor';
      } else {
        return 'endsColor';
      }
    },
    enableAnnounce(){
      return this.challenge && !this.ChallengeProgramEnabled  && this.challenge.userInfo.canAnnounce && this.status !== 'Ended' && this.status !== 'Starts';
    },
    enableDelete(){
      return this.challenge && this.challenge.announcementsCount === 0 && this.status === 'Ended';
    },
    enableEdit(){
      return this.challenge && this.challenge.userInfo.canEdit;
    },
    announcementCount() {
      return this.challenge && this.challenge.announcementsCount;
    },
    ChallengeProgramEnabled() {
      return this.challenge?.program && (!this.challenge.program.enabled || this.challenge.program.deleted);
    }
  },
  created() {
    this.getListWinners();
    document.addEventListener('announcement-added', this.announcementAdded);
  },
  methods: {
    getListWinners() {
      this.challenge.announcements.map(announce => {
        this.listWinners.push({'userName': announce.assignee});
      });
    },
    announcementAdded(event) {
      if (event && event.detail) {
        this.listWinners.unshift({'userName': event.detail.assignee});
        this.challenge.announcementsCount = this.challenge.announcementsCount + 1;
      }
    },
    openDetails() {
      this.$refs.winnersDetails.open();
    },
    closeMenu() {
      this.showMenu= false;
    },
    showDetails(challengeId) {
      if (this.$refs.challenge){
        window.history.replaceState('challenges', this.$t('challenges.challenges'), `${eXo.env.portal.context}/${eXo.env.portal.portalName}/challenges/${challengeId}`);
        this.$refs.challenge.open();
      }
    },
    getStatus() {
      const currentDate = new Date();
      const startDate = new Date(this.challenge && this.challenge.startDate);
      const endDate = new Date(this.challenge && this.challenge.endDate);
      if (startDate.getTime() > currentDate.getTime() && endDate.getTime() > currentDate.getTime()) {
        this.status = 'Starts';
        this.label=this.$t('challenges.status.starts');
        return `${this.label } ${ this.formattedDate(new Date(this.challenge.startDate)) }`;
      } else if ((startDate.getTime()<currentDate.getTime() && endDate.getTime() > currentDate.getTime()) || (this.formattedDate(endDate) ===  this.formattedDate(currentDate))) {
        this.status = 'Ends';
        this.label=this.$t('challenges.status.ends');
        return `${this.label } ${ this.formattedDate(new Date(this.challenge.endDate))}`;
      } else if (endDate.getTime() < currentDate.getTime() && startDate.getTime()< currentDate.getTime()) {
        this.status = 'Ended';
        this.label=this.$t('challenges.status.ended');
        return this.label;
      }
    },
    formattedDate(d) {
      let month = String(d.getMonth() + 1);
      let day = String(d.getDate());
      const year = String(d.getFullYear());

      if (month.length < 2) {month = `0${  month}`;}
      if (day.length < 2) {day = `0${  day}`;}

      return `${day}/${month}/${year}`;
    },
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
    confirmDelete() {
      this.$refs.deleteChallengeConfirmDialog.open();
    },
  }
};
</script>

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
                  <template v-slot:activator="{ on }">
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
        <div class="winners pa-2" v-if="!(listWinners && listWinners.length)">
          <p
            class="emptyWinners my-auto pl-2 align-self-end text-no-wrap pt-1">
            {{ challenge && challenge.announcementsCount }} {{ $t('challenges.winners.details') }}
          </p>
        </div>
        <div
          v-else
          :class="showAllAvatarList && 'AllUsersAvatar'"
          class="winners winnersAvatarsList d-flex flex-nowrap my-2">
          <exo-user-avatar
            v-for="winner in avatarToDisplay"
            :key="winner.user.id"
            :username="winner.user.remoteId"
            :title="winner.user.fullName"
            :avatar-url="winner.user.avatarUrl"
            :size="iconSize"
            :style="'background-image: url('+winner.user.avatarUrl+')'"
            class="me-1 projectManagersAvatar" />
          <div class="seeMoreAvatars">
            <div
              v-if="challenge && challenge.announcementsCount > maxAvatarToShow"
              class="seeMoreItem"
              @click="openDetails">
              <v-avatar
                :size="iconSize">
                <img
                  :src="listWinners[maxAvatarToShow-1].user.avatarUrl"
                  :title="listWinners[maxAvatarToShow-1].user.displayName"
                  :alt="$t('challenges.label.avatarUser', {0: listWinners[maxAvatarToShow-1].user.displayName})"
                  class="object-fit-cover"
                  loading="lazy"
                  role="presentation">
                <span class="seeMoreAvatarList">+{{ showMoreAvatarsNumber }}</span>
              </v-avatar>
            </div>
          </div>
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
            @click="createAnnounce">
            {{ $t('challenges.button.announce') }}
          </v-btn>
        </div>
      </div>
    </v-card>
    <challenge-details-drawer
      ref="challenge"
      :challenge="challenge" />
    <announce-drawer
      :challenge="challenge"
      @announcementAdded="announcementAdded($event)"
      ref="announceRef" />
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
    listWinners: [],
    iconSize: 28,
    maxAvatarToShow: 2
  }),
  computed: {
    avatarToDisplay () {
      if ( this.challenge && this.challenge.announcementsCount > this.maxAvatarToShow) {
        return this.listWinners.slice(0, this.maxAvatarToShow-1);
      } else {
        return this.listWinners;
      }
    },
    showMoreAvatarsNumber() {
      return this.challenge.announcementsCount - this.maxAvatarToShow;
    },
    showMessage() {
      if (this.challenge && this.challenge.userInfo && !this.challenge.userInfo.canAnnounce) {
        return  this.$t('challenges.permissionDenied');
      } else if (this.status === 'Starts') {
        return  this.$t('challenges.challengeNotStarted');
      } else if (this.status === 'Ended') {
        return  this.$t('challenges.challengeEnded');
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
      return this.challenge && this.challenge.userInfo.canAnnounce && this.status !== 'Ended' && this.status !== 'Starts';
    },
    enableEdit(){
      return this.challenge && this.challenge.userInfo.canEdit;
    },
  },
  mounted() {
    this.getListWinners();
  },
  methods: {
    getListWinners() {
      this.challenge.announcements.map(announce => {
        const announcement = {
          user: announce.assignee ||  announce.creator,
          activityId: announce.activityId,
          createDate: announce.createdDate
        };
        this.listWinners.push(announcement);
      });
    },
    announcementAdded(announcement) {
      const newAnnouncement = {
        user: announcement.assignee,
        activityId: announcement.activityId,
        createDate: announcement.createdDate
      };
      this.listWinners.unshift(newAnnouncement);
      this.challenge.announcementsCount = this.challenge.announcementsCount +1;
    },
    openDetails() {
      this.$refs.winnersDetails.open();
    },
    createAnnounce() {
      this.$refs.announceRef.open();
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
    }, formattedDate(d) {
      let month = String(d.getMonth() + 1);
      let day = String(d.getDate());
      const year = String(d.getFullYear());

      if (month.length < 2) {month = `0${  month}`;}
      if (day.length < 2) {day = `0${  day}`;}

      return `${day}/${month}/${year}`;
    }
  }
};
</script>

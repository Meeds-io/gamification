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
  <div class="engagement-center-card">
    <v-card
      id="engagementCenterChallengeCard"
      class="mx-auto card cardOfChallenge"
      height="230"
      max-height="230"
      outlined
      hover>
      <div class="contentCard">
        <v-list-item class="pa-0" three-line>
          <v-list-item-content class="title px-4 pt-3">
            <div class="d-flex">
              <div class="status">
                <i class="uiIconStatus iconStatus" :class="classStatus"></i> <span class="date">{{ getStatus() }}</span>
              </div>
              <div class="edit">
                <v-menu
                  v-if="enableEdit"
                  v-model="showMenu"
                  :left="!$vuetify.rtl"
                  :right="$vuetify.rtl"
                  bottom
                  offset-y
                  attach>
                  <template #activator="{ on, attrs }">
                    <v-btn
                      icon
                      small
                      class="me-2"
                      v-bind="attrs"
                      v-on="on">
                      <v-icon size="16" class="icon-default-color">fas fa-ellipsis-v</v-icon>
                    </v-btn>
                  </template>
                  <v-list dense class="pa-0">
                    <v-list-item
                      class="editList"
                      dense
                      @click="editChallenge">
                      <v-list-item-title class="editLabel">{{ $t('challenges.edit') }}</v-list-item-title>
                    </v-list-item>
                    <v-list-item
                      v-if="enableDelete"
                      class="editList"
                      @click="$root.$emit('challenge-delete-confirm', challenge)">
                      <v-list-item-title class="editLabel">{{ $t('challenges.delete') }}</v-list-item-title>
                    </v-list-item>
                  </v-list>
                </v-menu>
              </div>
            </div>
            <div class="contentChallenge" @click="$root.$emit('open-challenge-details', challenge)">
              <v-list-item-subtitle class="px-5 py-2 ma-5 subtitleChallenge">
                <v-tooltip  
                  :open-on-hover="displayTitleToolTip"
                  bottom>
                  <template #activator="{ on, attrs }">
                    <span
                      class="d-flex-inline position-relative text-truncate-2"
                      v-bind="attrs"
                      v-on="on">
                      {{ challengeTitle }}
                    </span>
                  </template>
                  {{ challengeTitle }}
                </v-tooltip>
              </v-list-item-subtitle>
            </div>
            <div class="points title">
              <span class="text-font-size">
                <v-icon size="16" class="primary--text">fas fa-trophy</v-icon>
                {{ challenge && challenge.points }} {{ $t('challenges.label.points') }}
              </span>
            </div>
          </v-list-item-content>
        </v-list-item>
      </div>
      <div class="footer assigneeAvatars d-flex">
        <div v-if="challenge && challenge.announcementsCount === 0" class="winners pa-2">
          <p
            class="emptyWinners my-auto pl-2 align-self-end text-no-wrap pt-1">
            {{ challenge && challenge.announcementsCount }} {{ $t('challenges.winners.details') }}
          </p>
        </div>
        <div
          v-else
          :class="showAllAvatarList && 'AllUsersAvatar'"
          class="winners winnersAvatarsList d-flex flex-nowrap my-2">
          <engagement-center-avatars-list
            :avatars="winnerAvatars"
            :max-avatars-to-show="3"
            :avatars-count="announcementCount"
            :size="27"
            @open-avatars-drawer="$root.$emit('open-winners-drawer', challenge.id)" />
        </div>
        <div class="addAnnounce mt-1">
          <v-btn
            class="btn btnAdd mx-1"
            :disabled="!enableAnnounce"
            :title="showMessage"
            @click="$root.$emit('open-announcement-drawer', challenge)">
            {{ $t('challenges.button.announce') }}
          </v-btn>
        </div>
      </div>
    </v-card>
  </div>
</template>
<script>
export default {
  props: {
    challenge: {
      type: Object,
      default: null
    },
    domain: {
      type: Array,
      default: function() {
        return [];
      },
    },
  },
  data: () => ({
    showMenu: false,
    label: '',
    status: '',
  }),
  computed: {
    winnerAvatars() {
      return (this.challenge?.announcements || []).map(announce => ({
        userName: announce.assignee
      }));
    },
    showMessage() {
      if (this.challenge && this.challenge.userInfo && !this.challenge.userInfo.canAnnounce) {
        return  this.$t('challenges.permissionDenied');
      } else if (this.status === 'Starts') {
        return  this.$t('challenges.challengeNotStarted');
      } else if (this.status === 'Ended') {
        return  this.$t('challenges.challengeEnded');
      } else if ( this.challengeProgramEnabled ) {
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
      return this.challenge && !this.challengeProgramEnabled  && this.challenge.userInfo.canAnnounce && this.status !== 'Ended' && this.status !== 'Starts';
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
    challengeProgramEnabled() {
      return this.domain && (!this.domain.enabled || this.domain.deleted);
    },
    challengeTitle() {
      return this.challenge?.title;
    },
    displayTitleToolTip() {
      return this.challengeTitle.length > 50;
    }
  },
  created() {
    this.$root.$on('announcement-added', this.announcementAdded);
    // Workaround to fix closing menu when clicking outside
    $(document).mousedown(() => {
      if (this.showMenu) {
        window.setTimeout(() => {
          this.showMenu = false;
        }, 200);
      }
    });
  },
  methods: {
    editChallenge() {
      this.$root.$emit('edit-challenge-details', this.challenge);
    },
    announcementAdded(event) {
      const announcement = event?.detail?.announcement;
      const challengeId = event?.detail?.challengeId;
      if (announcement && this.challenge.id === challengeId) {
        this.winnerAvatars.unshift({
          userName: announcement.assignee
        });
        this.challenge.announcementsCount++;
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
  }
};
</script>

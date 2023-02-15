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
      class="mx-auto"
      :width="challengeCardWidth"
      height="230"
      max-height="230"
      outlined
      hover>
      <div class="d-flex flex-column full-height pa-2" @click="$root.$emit('rule-detail-drawer', challenge)">
        <div
          class="d-flex flex-row pb-1">
          <div
            class="d-flex flex-row flex-grow-0 flex-shrink-0">
            <div class="d-flex flex-row my-auto">
              <v-icon size="18" class="mt-n2px primary--text ms-1">fas fa-trophy</v-icon>
              <div class="font-weight-bold text--secondary text-subtitle-2 mt-1 ms-2">
                {{ challenge && challenge.points }} {{ $t('challenges.label.points') }}
              </div>
            </div>
          </div>
          <v-spacer />
          <div
            class="d-flex flex-column flex-grow-0 flex-shrink-1">
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
                    v-bind="attrs"
                    v-on="on">
                    <v-icon size="16" class="icon-default-color">fas fa-ellipsis-v</v-icon>
                  </v-btn>
                </template>
                <v-list dense class="pa-0">
                  <v-list-item
                    dense
                    @mousedown="$event.preventDefault()"
                    @click="editChallenge">
                    <v-list-item-title class="editLabel">{{ $t('challenges.edit') }}</v-list-item-title>
                  </v-list-item>
                  <v-list-item
                    v-if="enableDelete"
                    class="editList"
                    @mousedown="$event.preventDefault()"
                    @click="deleteChallenge">
                    <v-list-item-title class="editLabel">{{ $t('challenges.delete') }}</v-list-item-title>
                  </v-list-item>
                </v-list>
              </v-menu>
            </div>
          </div>
        </div>
        <div class="ma-auto">
          <v-tooltip bottom>
            <template #activator="{ on, attrs }">
              <span
                class="d-flex-inline text-center position-relative text-truncate-2 font-weight-bold text-subtitle-1 text--secondary text-break overflow-hidden" 
                v-bind="attrs"
                v-on="on">
                {{ challengeTitle }}
              </span>
            </template>
            {{ challengeTitle }}
          </v-tooltip>
        </div>
        <div
          class="d-flex flex-row ms-1 ms-sm-0"
          @click="
            $event.preventDefault();
            $event.stopPropagation();
          ">
          <engagement-center-avatars-list
            :avatars="winnerAvatars"
            :max-avatars-to-show="maxAvatarsToShow"
            :avatars-count="announcementCount"
            :size="27"
            @open-avatars-drawer="openWinnersDrawer" />
          <v-spacer />
          <div
            v-if="!noParticipationYet"
            class="winners winnersAvatarsList d-flex flex-nowrap pe-3"
            @click="
              $event.preventDefault();
              $event.stopPropagation();
            ">
            <v-spacer />
          </div>
          <div class="d-flex flex-row my-auto my-sm-0">
            <v-icon size="18" class="primary--text">fas fa-calendar-day</v-icon>
            <span class="mt-1 ms-2 text-subtitle-2" v-sanitized-html="remainingPeriodLabel"></span>
          </div>
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
      default: function () {
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
      return this.challenge?.announcements?.filter(announce => announce.assignee)
        .map(announce => ({
          userName: announce.assignee
        })) || [];
    },
    enableDelete() {
      return this.enableEdit && !this.announcementCount && this.status === 'Ended';
    },
    enableEdit() {
      return this.challenge?.userInfo?.canEdit;
    },
    announcementCount() {
      return this.challenge?.announcementsCount || 0;
    },
    challengeTitle() {
      return this.challenge?.title;
    },
    startDate() {
      return new Date(this.challenge?.startDate);
    },
    endDate() {
      return new Date(this.challenge?.endDate);
    },
    remainingPeriodLabel() {
      if (this.endDate.getTime() < Date.now()) {
        return this.$t('challenges.label.over');
      } else if (this.startDate.getTime() > Date.now()) {
        const days = Math.round((this.startDate.getTime() - Date.now()) / (1000 * 60 * 60 * 24)) + 1;
        return this.$t('challenges.label.openIn', {0: days});
      } else {
        const days = Math.round((this.endDate.getTime() - Date.now()) / (1000 * 60 * 60 * 24)) + 1;
        return this.$t('challenges.label.daysLeft', {0: days});
      }
    },
    noParticipationYet() {
      return this.challenge?.announcementsCount === 0;
    },
    isMobile() {
      return this.$vuetify.breakpoint.xsOnly;
    },
    maxAvatarsToShow() {
      return this.isMobile ? 2 : 3;
    },
    challengeCardWidth() {
      return this.isMobile ? '100%' : '95%';
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
    editChallenge(event) {
      if (event) {
        event.preventDefault();
        event.stopPropagation();
      }
      this.$challengesServices.getChallengeById(this.challenge?.id)
        .then(challenge => this.$root.$emit('edit-challenge-details', challenge));
    },
    deleteChallenge(event) {
      if (event) {
        event.preventDefault();
        event.stopPropagation();
      }
      this.$root.$emit('challenge-delete-confirm', this.challenge);
    },
    openWinnersDrawer(event) {
      if (event) {
        event.preventDefault();
        event.stopPropagation();
      }
      this.$root.$emit('open-winners-drawer', this.challenge);
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
  }
};
</script>

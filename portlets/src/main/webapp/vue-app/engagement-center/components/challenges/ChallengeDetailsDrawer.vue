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
  <exo-drawer
    id="challengeDetails"
    ref="challengeDetails"
    right
    @closed="close">
    <template slot="title">
      {{ $t('challenges.details') }}
    </template>
    <template slot="content">
      <div class="pr-4 pl-4 pt-4 titleChallenge">
        {{ challenge && challenge.title }}
      </div>
      <hr class="separation ml-4 mr-4">
      <div class="pl-4 pr-4 descriptionLabel">
        {{ $t('challenges.label.description') }}
      </div>
      <div class="description pr-4 pl-4 pt-4" v-sanitized-html="challenge && challenge.description"></div>
      <hr class="separation mx-4">
      <v-flex class="px-4 nowrap winners">
        <span class="winnersLabel">
          {{ $t('challenges.winners.details') }}
        </span>
        <span
          v-if="winners && winners.length"
          class="viewAll"
          @click="openDetails">
          {{ $t('challenges.label.fullView') }}
        </span>
      </v-flex>
      <div class="assigneeAvatars flex-nowrap">
        <div class="winners pa-2" v-if="this.challenge && !this.challenge.announcementsCount">
          <p class="emptyWinners my-auto pl-2 align-self-end text-no-wrap pt-1">
            {{ challenge && challenge.announcementsCount }} {{ $t('challenges.winners.details') }}
          </p>
        </div>
        <div v-else class="winners winnersAvatarsList d-flex flex-nowrap my-2 px-4">
          <exo-user-avatars-list
            :users="avatarToDisplay"
            :max="5"
            :icon-size="28"
            :default-length="announcementCount"
            retrieve-extra-information
            @open-detail="openDetails()" />
        </div>
      </div>
      <div class="px-4 py-2">
        <span class="descriptionLabel">
          {{ $t('challenges.label.points') }}:
          <span class="descriptionLabel">
            {{ challenge && challenge.points }}
          </span>
        </span>
      </div>
      <div class="startDate d-flex px-4 py-2">
        <i class="uiIconStartDate "></i>
        <div class="mt-1 date">
          <date-format :value="challenge && challenge.startDate" />
        </div>
      </div>
      <div class="endDate d-flex pl-4 pr-4 pt-4">
        <i class="uiIconDueDate "></i>
        <div class="date">
          <date-format :value="challenge && challenge.endDate" />
        </div>
      </div>
      <div class="pl-4 pr-4 pt-4">
        {{ $t('challenges.label.audience') }}
      </div>
      <div v-if="space" class="pl-4 pr-4">
        <v-chip
          :title="space && space.displayName"
          color="primary"
          class="identitySuggesterItem mt-2">
          <v-avatar left>
            <v-img :src="space && space.avatarUrl" />
          </v-avatar>
          <span class="text-truncate">
            {{ space && space.displayName }}
          </span>
        </v-chip>
      </div>
      <div class="pl-4 pr-4 pt-4">
        {{ $t('challenges.label.program') }}
      </div>
      <div class="pl-4 pr-4">
        <v-chip
          :title="domainTitle"
          :color="domainShipColor"
          class="identitySuggesterItem mt-2">
          <span class="text-truncate">
            {{ domainTitle }}
          </span>
          <span v-if="isDomainDisabled" class="mx-1">
            ( {{ $t('challenges.label.disabled') }} )
          </span>
          <span v-else-if="isDomainDeleted" class="mx-1">
            ( {{ $t('challenges.label.deleted') }} )
          </span>
        </v-chip>
      </div>
      <div class="pl-4 pr-4 pt-4">
        {{ $t('challenges.label.managers') }}
      </div>
      <div v-if="users && users.length" class="listMangers pt-2">
        <v-chip
          v-for="user in users"
          :key="user"
          :title="user.fullName"
          color="primary"
          class="identitySuggesterItem mb-2 mx-1">
          <v-avatar left>
            <v-img :src="user.avatarUrl" />
          </v-avatar>
          <span class="text-truncate">
            {{ user.fullName }}
          </span>
        </v-chip>
      </div>
    </template>
  </exo-drawer>
</template>

<script>
export default {
  data: () => ({
    challenge: null,
    winners: [],
    maxAvatarToShow: 5,
  }),
  computed: {
    avatarToDisplay () {
      return this.winners ;
    },
    space() {
      return this.challenge?.space;
    },
    users() {
      return this.challenge?.managers || [];
    },
    announcementCount() {
      return this.challenge?.announcementsCount || this.winners.length;
    },
    isDomainDeleted() {
      return this.challenge?.program?.deleted ;
    },
    isDomainDisabled() {
      return !this.challenge?.program?.enabled;
    },
    isDomainDisabledOrDeleted() {
      return this.challenge?.program && (this.challenge.program.deleted || !this.challenge.program.enabled);
    },
    domainShipColor() {
      return !this.isDomainDisabledOrDeleted && 'primary' || '#e0e0e0';
    },
    domainTitle() {
      return this.challenge?.program?.title;
    }
  },
  created() {
    this.$root.$on('open-challenge-details', this.open);
  },
  methods: {
    open(challenge) {
      this.challenge = challenge;
      window.history.replaceState('challenges', this.$t('challenges.challenges'), `${eXo.env.portal.context}/${eXo.env.portal.portalName}/engagement/challenges/${this.challenge.id}`);

      this.$refs.challengeDetails.open();
      this.$refs.challengeDetails.startLoading();
      this.$challengesServices.getAllAnnouncementsByChallenge(this.challenge?.id, 0, this.maxAvatarToShow)
        .then(announcements => {
          this.winners = [];
          if (announcements.length > 0) {
            announcements.map(announce => {
              this.winners.push({'userName': announce.assignee });
            });
          }
          return this.$nextTick();
        }).finally(() => this.$refs.challengeDetails.endLoading());
    },
    close() {
      window.history.replaceState('challenges', this.$t('challenges.challenges'), `${eXo.env.portal.context}/${eXo.env.portal.portalName}/engagement/challenges`);
      this.$refs.challengeDetails.close();
    },
    getFromDate(date) {
      return this.$challengeUtils.getFromDate(date);
    },
    openDetails() {
      this.$root.$emit('open-winners-drawer', this.challenge?.id, true);
    },
  }
};
</script>

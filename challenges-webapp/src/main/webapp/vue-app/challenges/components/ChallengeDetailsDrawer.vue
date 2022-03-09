<template>
  <v-app id="challengeDetails">
    <exo-drawer
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
          <div class="winners pa-2" v-if="this.challenge && this.challenge.announcementsCount === 0">
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
            {{ challenge && getFromDate(new Date(challenge.startDate)) }}
          </div>
        </div>
        <div class="endDate d-flex pl-4 pr-4 pt-4">
          <i class="uiIconDueDate "></i>
          <div class="date">
            {{ challenge && getFromDate(new Date(challenge.endDate)) }}
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
            :title="challenge && challenge.program && challenge.program.title"
            color="primary"
            class="identitySuggesterItem mt-2">
            <span class="text-truncate">
              {{ challenge && challenge.program && challenge.program.title }}
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
    <challenge-winners-details
      :challenge-id="challenge && challenge.id"
      back
      ref="winnersDetails" />
  </v-app>
</template>

<script>
export default {
  props: {
    challenge: {
      type: Object,
      default: null
    },

  },
  data: () => ({
    winners: [],
  }),
  computed: {
    avatarToDisplay () {
      const winnerIdentity = [];
      if ( this.winners ) {
        this.winners.forEach(winner => {
          winnerIdentity.push({'userName': winner.user.remoteId});
        });
      }
      return winnerIdentity;
    },
    space() {
      return this.challenge && this.challenge.space;
    },
    users() {
      return this.challenge && this.challenge.managers || [];
    },
    announcementCount() {
      return this.challenge && this.challenge.announcementsCount || this.winners.length;
    }
  },
  methods: {
    open() {
      this.$challengesServices.getAllAnnouncementsByChallenge(this.challenge && this.challenge.id, 0).then(announcements => {
        if (announcements.length > 0) {
          this.winners = [];
          announcements.map(announce => {
            const announcement = {
              user: announce.assignee || announce.creator ,
              activityId: announce.activityId,
              createDate: announce.createdDate
            };
            this.winners.push(announcement);
          });
        }
      }).then(() => {
        return this.$nextTick();
      }).finally(() => this.$refs.challengeDetails.open());
    },
    close() {
      window.history.replaceState('challenges', this.$t('challenges.challenges'), `${eXo.env.portal.context}/${eXo.env.portal.portalName}/challenges`);
      this.$refs.challengeDetails.close();
    },
    getFromDate(date) {
      return this.$challengeUtils.getFromDate(date);
    },
    openDetails() {
      this.$refs.winnersDetails.open();
    },
  }
};
</script>

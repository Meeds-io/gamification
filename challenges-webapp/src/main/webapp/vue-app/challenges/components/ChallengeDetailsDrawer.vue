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
        <div class="px-4 nowrap">
          <span class="title winnersLabel">
            {{ $t('challenges.winners.details') }}
          </span>
        </div>
        <div class="px-4 pt-4 mt-n8 mx-3">
          <p class="viewAll" @click="openDetails">
            {{ $t('challenges.label.viewAll') }}
          </p>
        </div>
        <div class="assigneeAvatars flex-nowrap">
          <div class="winners winnersAvatarsList d-flex flex-nowrap my-2 px-4">
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
                v-if="winners.length > maxAvatarToShow"
                class="seeMoreItem"
                @click="openDetails">
                <v-avatar
                  :size="iconSize">
                  <img
                    :src="winners[maxAvatarToShow].user.avatarUrl"
                    :title="winners[maxAvatarToShow].user.displayName"
                    :alt="$t('challenges.label.avatarUser', {0: winners[maxAvatarToShow].user.displayName})"
                    class="object-fit-cover"
                    loading="lazy"
                    role="presentation">
                  <span class="seeMoreAvatarList">+{{ showMoreAvatarsNumber }}</span>
                </v-avatar>
              </div>
            </div>
          </div>
        </div>
        <div class="px-4 py-2">
          <span class="title">
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
    winners: {
      type: Object,
      default: null
    }
  },
  data: () => ({
    maxAvatarToShow: 7,
    iconSize: 28,
  }),
  computed: {
    avatarToDisplay () {
      if (this.winners.length > this.maxAvatarToShow) {
        return this.winners.slice(0, this.maxAvatarToShow-1);
      } else {
        return this.winners;
      }
    },
    showMoreAvatarsNumber() {
      return this.challenge.announcementsCount - this.maxAvatarToShow;
    },
    space() {
      return this.challenge && this.challenge.space;
    },
    users() {
      return this.challenge && this.challenge.managers || [];
    }
  },
  methods: {
    open() {
      if (this.$refs.challengeDetails) {
        this.$refs.challengeDetails.open();
      }
    },
    close() {
      if (this.$refs.challengeDetails) {
        this.$refs.challengeDetails.close();
      }
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

<template>
  <exo-drawer
    ref="announcementDrawer"
    id="announcementDrawer"
    class="challengeDrawer"
    :right="!$vuetify.rtl"
    @closed="close"
    eager>
    <template slot="title">
      <span class="pb-2"> {{ $t('challenges.label.announce') }} </span>
    </template>
    <template slot="content">
      <div class="pl-4 pr-4 mt-7 descriptionLabel">
        {{ $t('challenges.label.title') }}
      </div>
      <div class="pr-4 pl-4 pt-4 titleChallenge">
        {{ challenge && challenge.title }}
      </div>
      <hr class="ml-4 mr-4 separator">
      <div class="pl-4 pr-4 mt-7 descriptionLabel">
        {{ $t('challenges.label.assignedAchievement') }}
      </div>
      <div class="pl-4" v-if="enableSuggester">
        <challenge-assignment
          ref="challengeAssignment"
          class="my-2"
          :audience="space"
          v-model="announcement.assignee"
          @remove-user="removeAssignee"
          @add-item="addUser($event)" />
      </div>
      <div v-else-if="disableSuggester" class="pl-4 pr-4">
        <v-chip
          color="primary"
          class="identitySuggesterItem mt-2">
          <v-avatar left>
            <v-img :src="challenge && challenge.userInfo.avatarUrl" />
          </v-avatar>
          <span class="text-truncate">
            {{ challenge && challenge.userInfo.fullName }}
          </span>
        </v-chip>
      </div>
      <div class="pl-4 pr-4 pt-9 py-4 my-2">
        <challenge-description
          ref="challengeDescription"
          v-model="announcement.comment"
          :value="announcement.comment"
          @invalidDescription="invalidDescription($event)"
          @validDescription="validDescription($event)"
          @addDescription="addDescription($event)" />
      </div>
      <div
        class="
          pl-4
          pr-4
          pt-4">
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
    </template>
    <template slot="footer">
      <div class="d-flex mr-2">
        <v-spacer />
        <button
          class="ignore-vuetify-classes btn mx-1"
          @click="close">
          {{ $t('challenges.button.cancel') }}
        </button>
        <button
          :disabled="!disabledSave"
          class="ignore-vuetify-classes btn btn-primary"
          @click="createAnnouncement">
          {{ $t('challenges.button.create') }}
        </button>
      </div>
    </template>
  </exo-drawer>
</template>

<script>
export default {
  props: {
    challenge: {
      type: Object,
      default: null
    }
  },
  data() {
    return {
      announcement: {},
      isValidDescription: {
        description: true },
    };
  },
  computed: {
    space() {
      return this.challenge && this.challenge.space;
    },
    disabledSave() {
      return this.announcement.assignee && this.announcement.assignee.length > 0 && this.isValidDescription.description ;
    },
    enableSuggester() {
      return this.challenge && (this.challenge.userInfo && this.challenge.userInfo.manager || this.challenge.userInfo && this.challenge.userInfo.redactor);
    },
    disableSuggester(){
      return this.challenge && this.challenge.userInfo && !this.challenge.userInfo.manager && !this.challenge.userInfo.redactor && this.challenge.userInfo.member;
    },
  },
  methods: {
    initAnnounce() {
      this.invalidDescription();
      if (this.disableSuggester) {
        this.$set(this.announcement,'assignee', this.challenge.userInfo.id);
      } else {
        this.$refs.challengeAssignment.assigneeObj = [];
      }
    },
    open() {
      this.initAnnounce();
      this.$refs.challengeDescription.initCKEditor();
      this.$refs.announcementDrawer.open();
    },
    close() {
      this.reset();
      this.$refs.announcementDrawer.close();
    },
    removeAssignee() {
      this.$set(this.announcement,'assignee', 0);
    },
    addDescription(value) {
      if (value) {
        this.$set(this.announcement,'comment', value);
      }
    },
    invalidDescription() {
      this.$set(this.isValidDescription,'description', false);
    },
    validDescription() {
      this.$set(this.isValidDescription,'description', true);
    },
    createAnnouncement() {
      this.announcement.challengeId =  this.challenge.id;
      this.announcement.createdDate = new Date();
      this.$refs.announcementDrawer.startLoading();
      this.$challengesServices.saveAnnouncement(this.announcement).then((announcement) =>{
        this.$root.$emit('show-alert', {type: 'success',message: `<a href="${eXo.env.portal.context}/${eXo.env.portal.portalName}/activity?id=${announcement.activityId}" target="_blank" rel="noopener noreferrer">${this.$t('challenges.announcement')}</a>${this.$t('challenges.announcementCreateSuccess')}`});
        this.$emit('announcementAdded', announcement);
        this.close();
      })
        .catch(e => {
          let msg = '';
          if (e.message === '401' || e.message === '403') {
            msg = this.$t('challenges.permissionDenied');
          } else if (e.message  === '406') {
            msg = this.$t('challenges.challengeNotStartedOrEnded');
          } else  {
            msg = this.$t('challenges.announcementErrorSave');
          }
          this.$root.$emit('show-alert', {type: 'error',message: msg});
        })
        .finally(() => this.$refs.announcementDrawer.endLoading());
    },
    addUser(id){
      this.$set(this.announcement,'assignee', id);
    },
    reset() {
      this.isValidDescription= {
        description: true };
      this.$refs.challengeDescription.deleteDescription();
    }
  }
};
</script>

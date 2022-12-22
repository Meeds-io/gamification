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
    ref="announcementDrawer"
    id="announcementDrawer"
    v-model="drawer"
    :right="!$vuetify.rtl"
    class="EngagementCenterDrawer"
    @closed="close"
    eager>
    <template #title>
      <span class="pb-2"> {{ $t('challenges.label.announce') }} </span>
    </template>
    <template v-if="drawer" #content>
      <div class="pl-4 pr-4 mt-7 descriptionLabel">
        {{ $t('challenges.label.title') }}
      </div>
      <div class="pr-4 pl-4 pt-4 titleChallenge">
        {{ challenge && challenge.title }}
      </div>
      <hr class="ml-4 mr-4 separator">
      <div class="pl-4 pr-4 mt-7 descriptionLabel">
        {{ $t('challenges.label.assignedAchievement') }} *
      </div>
      <div class="pl-4" v-if="enableSuggester">
        <engagement-center-assignment
          ref="challengeAssignment"
          v-model="assignee"
          :audience="space"
          class="my-2" />
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
        <div class="py-1 px-2 subtitle-1">
          {{ $t('challenges.label.describeYourAchievement') }} *
        </div>
        <exo-activity-rich-editor
          v-model="comment"
          ref="announcementRichEditor"
          :max-length="MAX_LENGTH"
          :template-params="templateParams"
          ck-editor-type="announcementContent"
          class="flex"
          @validity-updated=" validInput = $event" />
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
          {{ $t('engagementCenter.button.cancel') }}
        </button>
        <button
          :disabled="disabledSave"
          class="ignore-vuetify-classes btn btn-primary"
          @click="createAnnouncement">
          {{ $t('engagementCenter.button.create') }}
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
  data: () => ({
    drawer: false,
    assignee: null,
    comment: null,
    templateParams: {},
    validInput: true,
    MAX_LENGTH: 1300
  }),
  computed: {
    space() {
      return this.challenge && this.challenge.space;
    },
    disabledSave() {
      return !this.assignee
        || !this.assignee.length
        || !this.assignee[0].id
        || !this.validInput
        || !this.comment
        || !this.comment.length;
    },
    enableSuggester() {
      return this.challenge && (this.challenge.userInfo && this.challenge.userInfo.manager || this.challenge.userInfo && this.challenge.userInfo.redactor);
    },
    disableSuggester(){
      return this.challenge && this.challenge.userInfo && !this.challenge.userInfo.manager && !this.challenge.userInfo.redactor && this.challenge.userInfo.member;
    }
  },
  created() {
    this.$root.$on('open-announcement-drawer', this.openDrawer);
  },
  methods: {
    initAnnounce() {
      this.templateParams = {};
      this.comment = null;
      if (this.disableSuggester) {
        this.assignee = [this.challenge.userInfo];
      } else {
        this.assignee = null;
      }
    },
    open() {
      this.initAnnounce();
      this.$refs.announcementDrawer.open();
      this.$nextTick().then(() => this.$refs.announcementRichEditor.initCKEditor());
    },
    close() {
      this.$refs.announcementRichEditor.destroyCKEditor();
      this.$refs.announcementDrawer.close();
    },
    createAnnouncement() {
      if (this.disabledSave) {
        return;
      }
      const announcement = {
        assignee: this.assignee[0].id,
        comment: this.comment,
        challengeId: this.challenge.id,
        challengeTitle: this.challenge.title,
        templateParams: this.templateParams,
      };

      this.$refs.announcementDrawer.startLoading();
      this.$challengesServices.saveAnnouncement(announcement)
        .then(createdAnnouncement => {
          this.$engagementCenterUtils.displayAlert(this.$t('challenges.announcementCreateSuccess'));
          this.$root.$emit('announcement-added', {detail: {
            announcement: createdAnnouncement,
            challengeId: this.challenge.id,
          }});
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
          this.$engagementCenterUtils.displayAlert(msg, 'error');
        })
        .finally(() => this.$refs.announcementDrawer.endLoading());
    },
    openDrawer(event) {
      if (event) {
        this.challenge = event;
        this.$nextTick().then(() => this.open());
      }
    },
  }
};
</script>

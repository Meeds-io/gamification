<!--

 This file is part of the Meeds project (https://meeds.io/).

 Copyright (C) 2020 - 2023 Meeds Association contact@meeds.io

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
  <div v-if="canAnnounce" class="d-flex flex-column">
    <div v-if="editor">
      <div class="d-flex flex-row pt-3">
        <v-list-item class="text-truncate px-0">
          <exo-space-avatar
            v-if="spaceId"
            :space-id="spaceId"
            :space="space"
            :size="30"
            extra-class="text-truncate"
            avatar />
          <exo-user-avatar
            :profile-id="username"
            :size="spaceId && 25 || 30"
            :extra-class="spaceId && 'ms-n4 mt-6' || ''"
            avatar />
          <v-list-item-content class="py-0 accountTitleLabel text-truncate">
            <v-list-item-title class="font-weight-bold d-flex body-2 mb-0">
              <exo-space-avatar
                :space-id="spaceId"
                :space="space"
                extra-class="text-truncate"
                fullname
                bold-title
                link-style
                username-class />
            </v-list-item-title>
            <v-list-item-subtitle class="d-flex flex-row flex-nowrap">
              <exo-user-avatar
                :profile-id="username"
                extra-class="text-truncate ms-2 me-1"
                fullname
                link-style
                small-font-size
                username-class />
            </v-list-item-subtitle>
          </v-list-item-content>
        </v-list-item>
      </div>
      <rich-editor
        ref="announcementEditor"
        id="announcementEditor"
        v-model="comment"
        :max-length="MAX_LENGTH"
        :template-params="templateParams"
        :placeholder="$t('rule.detail.announceEditor.placeholder')"
        :tag-enabled="false"
        ck-editor-type="announcementContent"
        class="flex my-3"
        autofocus
        @validity-updated="validLength = $event" />
    </div>
    <v-btn
      v-else
      color="primary"
      class="primary-border-color mx-auto my-4"
      outlined
      @click="editor = true">
      <v-icon
        size="16"
        color="primary">
        fas fa-bullhorn
      </v-icon>
      <span class="font-weight-bold my-auto ms-3">
        {{ $t('rule.detail.Announce') }}
      </span>
    </v-btn>
  </div>
</template>
<script>
export default {
  props: {
    rule: {
      type: Object,
      default: null,
    },
    value: {
      type: Boolean,
      default: false,
    },
  },
  data: () => ({
    MAX_LENGTH: 1300,
    editor: false,
    sending: false,
    comment: null,
    templateParams: {},
    userId: eXo.env.portal.userIdentityId,
    username: eXo.env.portal.userName,
    validLength: true,
  }),
  computed: {
    spaceId() {
      return this.rule?.program?.spaceId;
    },
    validComment() {
      return this.comment?.length > 0 && this.validLength;
    },
    space() {
      return this.rule?.program?.space;
    },
    canAnnounce() {
      return this.rule?.type === 'MANUAL' && this.rule?.userInfo?.allowedToRealize;
    },
  },
  watch: {
    sending() {
      this.$emit('sending', this.sending);
    },
    validComment() {
      this.$emit('input', this.validComment);
    },
    editor() {
      if (this.editor) {
        this.$emit('form-opened', true);
      } else {
        this.destroyEditor();
        this.$emit('form-opened', false);
      }
    },
  },
  beforDestroy() {
    this.destroyEditor();
  },
  methods: {
    clear() {
      this.editor = false;
      this.sending = false;
      this.comment = null;
      this.templateParams = {};
    },
    displayForm() {
      this.editor = this.canAnnounce;
    },
    destroyEditor() {
      if (this.$refs.announcementEditor) {
        this.$refs.announcementEditor.destroyCKEditor();
      }
    },
    createAnnouncement() {
      if (!this.validComment) {
        return;
      }
      const announcement = {
        assignee: this.userId,
        comment: this.comment,
        challengeId: this.rule.id,
        challengeTitle: this.rule.title,
        templateParams: this.templateParams,
      };
      this.sending = true;
      this.$announcementService.createAnnouncement(announcement)
        .then(createdAnnouncement => {
          document.dispatchEvent(new CustomEvent('alert-message-html-confeti', {detail: {
            alertType: 'success',
            alertMessage: `
              <div class="d-flex flex-nowrap pt-1 justify-center">
                ${this.$t('challenges.announcementCreateSuccess')}
              </div>
            `,
            alertLink: `${eXo.env.portal.context}/${eXo.env.portal.portalName}/activity?id=${createdAnnouncement.activityId}`,
            alertLinkText: this.$t('announcement.alert.see'),
            alertLinkTarget: '_self',
          }}));
          this.$root.$emit('announcement-added-event', {detail: {
            announcement: createdAnnouncement,
            challengeId: this.rule.id,
          }});
          this.comment = null;
          this.editor = false;
          this.$emit('sent');
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
          this.$root.$emit('alert-message', msg, 'error');
        })
        .finally(() => this.sending = false);
    },
  },
};
</script>
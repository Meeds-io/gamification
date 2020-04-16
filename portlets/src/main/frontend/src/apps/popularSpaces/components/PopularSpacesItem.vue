<template>
  <v-list-item class="pa-0 spaceItem">
    <v-list-item-avatar
      :class="spaceItemClass"
      :href="url"
      class="my-0"
      tile>
      <v-avatar :size="avatarSize" tile>
        <v-img
          :src="avatarUrl"
          :height="avatarSize"
          :width="avatarSize"
          :max-height="avatarSize"
          :max-width="avatarSize"
          :class="skeleton && 'skeleton-background'"
          class="mx-auto spaceAvatar" />
      </v-avatar>
    </v-list-item-avatar>
    <v-list-item-content
      :class="spaceItemClass"
      :href="url"
      class="pa-0">
      <v-list-item-title>
        <v-skeleton-loader
          v-if="skeleton"
          type="text"
          boilerplate
          class="mt-3 mr-3 skeleton-background"
          height="11px" />
        <a
          v-else
          :href="url"
          class="text-color text-truncate">
          {{ space.displayName }}
        </a>
      </v-list-item-title>
      <v-list-item-subtitle>
        <v-skeleton-loader
          v-if="skeleton"
          type="text"
          boilerplate
          class="mb-2 mt-1 skeleton-background"
          height="8px"
          width="70px" />
        <template v-else>
          {{ $t('popularSpaces.label.points', {0: space.score}) }}
        </template>
      </v-list-item-subtitle>
    </v-list-item-content>
    <v-list-item-action class="ma-0 flex-row align-self-center">
      <template v-if="space.isInvited || skeleton" class="invitationButtons">
        <v-btn
          :title="$t('popularSpaces.button.acceptToJoin')"
          :width="actionIconSize"
          :height="actionIconSize"
          :loading="sendingAction"
          :disabled="skeleton"
          :class="skeleton && 'skeleton-background skeleton-text'"
          class="mr-1 popularSpacesAction popularSpacesCheck"
          fab
          dark
          icon
          depressed
          @click="acceptToJoin">
          <v-icon small size="18">mdi-check</v-icon>
        </v-btn>
        <v-btn
          :title="$t('popularSpaces.button.refuseToJoin')"
          :width="actionIconSize"
          :height="actionIconSize"
          :loading="sendingAction"
          :disabled="skeleton"
          :class="skeleton && 'skeleton-background skeleton-text'"
          class="mr-1 popularSpacesAction popularSpacesClose"
          fab
          dark
          icon
          depressed
          @click="refuseToJoin">
          <v-icon small size="18">mdi-close</v-icon>
        </v-btn>
      </template>
      <v-btn
        v-else-if="space.isPending"
        :title="$t('popularSpaces.button.cancelRequest')"
        :width="actionIconSize"
        :height="actionIconSize"
        :loading="sendingAction"
        class="mr-1 popularSpacesAction popularSpacesClose"
        fab
        dark
        icon
        depressed
        @click="cancelRequest">
        <v-icon small size="18">mdi-close</v-icon>
      </v-btn>
      <template v-else-if="!space.isMember || skeleton">
        <v-btn
          v-if="space.subscription === 'open'"
          :disabled="skeleton"
          :title="$t('popularSpaces.button.join')"
          :width="actionIconSize"
          :height="actionIconSize"
          :loading="sendingAction"
          class="mr-1 popularSpacesAction popularSpacesCheck"
          fab
          dark
          icon
          depressed
          @click="join">
          <v-icon small size="18">mdi-plus</v-icon>
        </v-btn>
        <v-btn
          v-else-if="space.subscription === 'validation'"
          :disabled="skeleton"
          :title="$t('popularSpaces.button.requestJoin')"
          :width="actionIconSize"
          :height="actionIconSize"
          :loading="sendingAction"
          class="mr-1 popularSpacesAction popularSpacesCheck"
          fab
          dark
          icon
          depressed
          @click="requestJoin">
          <v-icon small size="18">mdi-plus</v-icon>
        </v-btn>
      </template>
    </v-list-item-action>
  </v-list-item>
</template>
<script>
const randomMax = 10000;
import * as popularSpacesService from '../js/PopularSpacesService.js';

export default {
  props: {
    space: {
      type: Object,
      default: () => null,
    },
    avatarSize: {
      type: Number,
      default: () => 37,
    },
    skeleton: {
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {
      actionIconSize: 20,
      sendingAction: false,
      spaceItemClass: `popularSpace${parseInt(Math.random() * randomMax)
        .toString()
        .toString()}`,
    };
  },
  computed: {
    avatarUrl() {
      return this.skeleton ?
          '':
          this.space.avatarUrl || `${eXo.env.portal.context}/${eXo.env.portal.rest}/v1/social/spaces/${this.space.prettyName}/avatar`;
    },
    url() {
      if (this.skeleton || !this.space || !this.space.groupId) {
        return '#';
      }
      const uri = this.space.groupId.replace(/\//g, ':');
      return `${eXo.env.portal.context}/g/${uri}/`;
    },
    labels() {
      return {
        CancelRequest: this.$t('popularSpaces.label.profile.CancelRequest'),
        Confirm: this.$t('popularSpaces.label.profile.Confirm'),
        Connect: this.$t('popularSpaces.label.profile.Connect'),
        Ignore: this.$t('popularSpaces.label.profile.Ignore'),
        RemoveConnection: this.$t('popularSpaces.label.profile.RemoveConnection'),
        StatusTitle: this.$t('popularSpaces.label.profile.StatusTitle'),
        join: this.$t('popularSpaces.label.profile.join'),
        leave: this.$t('popularSpaces.label.profile.leave'),
        members: this.$t('popularSpaces.label.profile.members'),
      };
    },
  },
  mounted() {
    if (!this.skeleton && this.space && this.space.groupId) {
      // TODO disable tiptip because of high CPU usage using its code
      this.initTiptip();
    }
  },
  methods: {
    initTiptip() {
      this.$nextTick(() => {
        $(`.${this.spaceItemClass}`).spacePopup({
          userName: eXo.env.portal.userName,
          spaceID: this.space.id,
          restURL: '/portal/rest/v1/social/spaces/{0}',
          membersRestURL: '/portal/rest/v1/social/spaces/{0}/users?returnSize=true',
          managerRestUrl: '/portal/rest/v1/social/spaces/{0}/users?role=manager&returnSize=true',
          membershipRestUrl: '/portal/rest/v1/social/spacesMemberships?space={0}&returnSize=true',
          defaultAvatarUrl: `/portal/rest/v1/social/spaces/${this.space.prettyName}/avatar`,
          deleteMembershipRestUrl: '/portal/rest/v1/social/spacesMemberships/{0}:{1}:{2}',
          labels: this.labels,
          content: false,
          keepAlive: true,
          defaultPosition: this.tiptipPosition || 'left_bottom',
          maxWidth: '420px',
        });
      });
    },
    acceptToJoin() {
      this.sendingAction = true;
      popularSpacesService.accept(this.space.id)
        .then(() => this.$emit('refresh'))
        .catch((e) => {
          // eslint-disable-next-line no-console
          console.error('Error processing action', e);
        })
        .finally(() => {
          this.sendingAction = false;
        });
    },
    refuseToJoin() {
      this.sendingSecondAction = true;
      popularSpacesService.deny(this.space.id)
        .then(() => this.$emit('refresh'))
        .catch((e) => {
          // eslint-disable-next-line no-console
          console.error('Error processing action', e);
        })
        .finally(() => {
          this.sendingSecondAction = false;
        });
    },
    join() {
      this.sendingAction = true;
      popularSpacesService.join(this.space.id)
        .then(() => this.$emit('refresh'))
        .catch((e) => {
          // eslint-disable-next-line no-console
          console.error('Error processing action', e);
        })
        .finally(() => {
          this.sendingAction = false;
        });
    },
    requestJoin() {
      this.sendingAction = true;
      popularSpacesService.requestJoin(this.space.id)
        .then(() => this.$emit('refresh'))
        .catch((e) => {
          // eslint-disable-next-line no-console
          console.error('Error processing action', e);
        })
        .finally(() => {
          this.sendingAction = false;
        });
    },
    cancelRequest() {
      this.sendingAction = true;
      popularSpacesService.cancel(this.space.id)
        .then(() => this.$emit('refresh'))
        .catch((e) => {
          // eslint-disable-next-line no-console
          console.error('Error processing action', e);
        })
        .finally(() => {
          this.sendingAction = false;
        });
    },
  }
};
</script>
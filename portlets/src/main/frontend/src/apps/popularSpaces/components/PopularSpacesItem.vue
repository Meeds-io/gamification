<!--
This file is part of the Meeds project (https://meeds.io/).
Copyright (C) 2020 Meeds Association
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
          transition="none"
          class="mx-auto spaceAvatar"
          eager />
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
    <v-list-item-action class="ma-0 pr-4 flex-row align-self-center" :class="displaySecondButton ? 'secondButtonDisplayed' : ''">
      <template v-if="space.isInvited || skeleton" class="invitationButtons">
        <div class="acceptToJoinSpaceButtonParent">
          <v-btn
            :loading="sendingAction"
            :disabled="sendingAction"
            class="btn mx-auto spaceMembershipButton acceptToJoinSpaceButton"
            depressed
            small
            @click="acceptToJoin">
            <v-icon>mdi-check</v-icon>
            <span class="d-flex">
              {{ $t('popularSpaces.button.acceptToJoin') }}
            </span>
          </v-btn>
          <v-btn
            class="btn spaceButtonMenu d-inline"
            depressed
            x-small
            @click="displaySecondButton = !displaySecondButton">
            <v-icon>mdi-menu-down</v-icon>
          </v-btn>
        </div>
        <v-btn
          v-show="displaySecondButton"
          :loading="sendingSecondAction"
          :disabled="sendingSecondAction"
          class="btn mx-auto px-0 spaceMembershipButton refuseToJoinSpaceButton"
          depressed
          block
          small
          @click="refuseToJoin">
          <v-icon>mdi-close</v-icon>
          <span class="d-flex">
            {{ $t('popularSpaces.button.refuseToJoin') }}
          </span>
        </v-btn>
      </template>
      <v-btn
        v-else-if="space.isPending"
        :loading="sendingAction"
        class="btn mx-auto spaceMembershipButton"
        depressed
        block
        small
        @click="cancelRequest">
        <v-icon>mdi-close</v-icon>
        <span class="spaceMembershipButtonText d-inline">
          {{ $t('popularSpaces.button.cancelRequest') }}
        </span>
      </v-btn>
      <template v-else-if="!space.isMember || skeleton">
        <v-btn
          v-if="space.subscription === 'open'"
          :disabled="skeleton"
          :loading="sendingAction"
          class="btn mx-auto spaceMembershipButton joinSpaceButton"
          depressed
          block
          small
          @click="join">
          <v-icon>mdi-plus</v-icon>
          <span class="spaceMembershipButtonText d-inline">
            {{ $t('popularSpaces.button.join') }}
          </span>
        </v-btn>
        <v-btn
          v-else-if="space.subscription === 'validation'"
          :disabled="skeleton"
          :title="$t('popularSpaces.button.requestJoin')"
          :loading="sendingAction"
          class="btn mx-auto spaceMembershipButton joinSpaceButton"
          depressed
          block
          small
          @click="requestJoin">
          <v-icon>mdi-plus</v-icon>
          <span class="spaceMembershipButtonText d-inline">
            {{ $t('popularSpaces.button.requestJoin') }}
          </span>
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
      displaySecondButton: false,
      sendingSecondAction: false,
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
        CancelRequest: this.$t('profile.label.CancelRequest'),
        Confirm: this.$t('profile.label.Confirm'),
        Connect: this.$t('profile.label.Connect'),
        Ignore: this.$t('profile.label.Ignore'),
        RemoveConnection: this.$t('profile.label.RemoveConnection'),
        StatusTitle: this.$t('profile.label.StatusTitle'),
        join: this.$t('profile.label.join'),
        leave: this.$t('profile.label.leave'),
        members: this.$t('profile.label.members'),
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
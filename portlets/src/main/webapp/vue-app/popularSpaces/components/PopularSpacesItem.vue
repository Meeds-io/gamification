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
    <exo-space-avatar
      :space="space"
      class="text-truncate"
      subtitle-new-line
      popover> 
      <template slot="subTitle">
        <span class="caption">
          {{ $t('popularSpaces.label.points', {0: space.score}) }}
        </span>
      </template>
    </exo-space-avatar>
    <v-list-item-action class="ma-0 pe-4 flex-row align-self-center ml-auto flex-shrink-0" :class="displaySecondButton ? 'secondButtonDisplayed' : ''">
      <div v-if="space.isInvited || skeleton" class="invitationButtons">
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
      </div>
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
    skeleton: {
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {
      sendingAction: false,
      displaySecondButton: false,
      sendingSecondAction: false,
      spaceItemClass: `popularSpace${parseInt(Math.random() * randomMax)
        .toString()
        .toString()}`,
    };
  },
  methods: {
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

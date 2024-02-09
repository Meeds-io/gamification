<!--
This file is part of the Meeds project (https://meeds.io/).

Copyright (C) 2020 - 2024 Meeds Association contact@meeds.io

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
  <v-app>
    <v-card-text class="px-0 pb-0 dark-grey-color font-weight-bold">
      {{ $t('gamification.event.detail.activity.label') }}
    </v-card-text>
    <v-radio-group v-model="activity" @change="changeSelection">
      <v-radio
        value="ANY"
        :label="anyLabel" />
      <v-radio value="ANY_IN_SPACE" :label="inSpaceLabel" />
      <exo-identity-suggester
        v-if="activity === 'ANY_IN_SPACE'"
        ref="spacesSuggester"
        v-model="space"
        :labels="spaceSuggesterLabels"
        :include-users="false"
        :width="220"
        name="spacesSuggester"
        class="user-suggester mt-n2"
        include-spaces />
      <v-radio
        v-if="canSpecifyActivity"
        value="ONE_ACTIVITY"
        :label="$t('gamification.event.detail.oneActivity.label')" />
    </v-radio-group>
    <input
      v-if="activity === 'ONE_ACTIVITY' && canSpecifyActivity"
      ref="activityLink"
      v-model="activityLink"
      :placeholder="$t('gamification.event.detail.activityLink.placeholder')"
      type="text"
      class="ignore-vuetify-classes full-width"
      required
      @input="handleActivity"
      @change="checkActivityLink(activityLink)">
    <v-card-text class="ps-0 py-0" />
    <v-list-item-action-text v-if="!isValidLink" class="d-flex py-0 me-0 me-sm-8">
      <span class="error--text">{{ $t('gamification.event.detail.activityLink.error') }}</span>
    </v-list-item-action-text>
  </v-app>
</template>

<script>
export default {
  props: {
    properties: {
      type: Object,
      default: null
    },
    trigger: {
      type: String,
      default: null
    },
  },
  data() {
    return {
      anySpace: true,
      space: null,
      activity: 'ANY',
      activityLink: null,
      isValidLink: true,
      startTypingKeywordTimeout: 0,
      startSearchAfterInMilliseconds: 300,
      endTypingKeywordTimeout: 50,
    };
  },
  computed: {
    canSpecifyActivity() {
      return ['postActivityComment', 'likeActivity'].includes(this.trigger);
    },
    inSpaceLabel() {
      return this.canSpecifyActivity ? this.$t('gamification.event.detail.activityInSpace.label') : this.$t('gamification.event.detail.onlyOneSpace.label');
    },
    anyLabel() {
      return this.canSpecifyActivity ? this.$t('gamification.event.detail.anyActivity.label') : this.$t('gamification.event.detail.anySpace.label');
    },
    spaceSuggesterLabels() {
      return {
        placeholder: this.$t('activity.composer.audience.placeholder'),
        noDataLabel: this.$t('activity.composer.audience.noDataLabel'),
      };
    },
  },
  watch: {
    space() {
      if (this.space) {
        if (!this.space.notToChange) {
          const eventProperties = {
            spaceId: this.space?.spaceId.toString(),
          };
          document.dispatchEvent(new CustomEvent('event-form-filled', {detail: eventProperties}));
        }
      } else if (this.activity === 'ANY_IN_SPACE'){
        document.dispatchEvent(new CustomEvent('event-form-unfilled'));
      }
    },
    trigger() {
      this.activity = 'ANY';
      this.isValidLink = true;
      document.dispatchEvent(new CustomEvent('event-form-filled'));
    },
  },
  created() {
    if (this.properties?.spaceId) {
      this.activity = 'ANY_IN_SPACE';
      this.$spaceService.getSpaceById(this.properties?.spaceId)
        .then(spaceData=> {
          this.space = {
            id: `space:${spaceData.prettyName}`,
            profile: {
              avatarUrl: spaceData.avatarUrl,
              fullName: spaceData.displayName,
            },
            providerId: 'space',
            remoteId: spaceData.prettyName,
            spaceId: spaceData.id,
            displayName: spaceData.displayName,
            notToChange: true,
          };
        });
    } else if (this.properties?.activityId) {
      this.activity = 'ONE_ACTIVITY';
      this.activityLink = `${window.location.origin}/${eXo.env.portal.context}/${eXo.env.portal.defaultPortal}/activity?id=${this.properties?.activityId}`;
    } else if (this.properties?.activity === 'any') {
      this.activity = 'ANY';
    } else {
      document.dispatchEvent(new CustomEvent('event-form-filled'));
    }
  },
  methods: {
    changeSelection() {
      this.space = null;
      this.activityLink = null;
      if (this.activity === 'ANY') {
        document.dispatchEvent(new CustomEvent('event-form-filled'));
      } else {
        document.dispatchEvent(new CustomEvent('event-form-unfilled'));
      }
    },
    handleActivity() {
      if (this.activityLink) {
        this.startTypingKeywordTimeout = Date.now() + this.startSearchAfterInMilliseconds;
        if (!this.typing) {
          this.typing = true;
          this.waitForEndTyping();
        }
      }
    },
    waitForEndTyping() {
      window.setTimeout(() => {
        if (Date.now() > this.startTypingKeywordTimeout) {
          this.typing = false;
          if (this.checkActivityLink(this.activityLink) && this.activityLink !== this.properties?.activity) {
            const eventProperties = {
              activityId: this.getActivityIdFromUrl(this.activityLink)
            };
            document.dispatchEvent(new CustomEvent('event-form-filled', {detail: eventProperties}));
          } else {
            document.dispatchEvent(new CustomEvent('event-form-unfilled'));
          }
        } else {
          this.waitForEndTyping();
        }
      }, this.endTypingKeywordTimeout);
    },
    checkActivityLink(activityLink) {
      const activityUrlRegex = /^http(s)?:\/\/[^/]+\/portal\/meeds\/activity\?id=\d+$/;
      this.isValidLink = activityUrlRegex.test(activityLink);
      return this.isValidLink;
    },
    getActivityIdFromUrl(activityLink) {
      const match = activityLink.match(/\/activity\?id=(\d+)/);
      return match ? match[1] : null;
    }
  }
};
</script>
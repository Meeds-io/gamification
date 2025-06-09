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
  <div v-if="!isEditing">
    <div class="text-header mb-2">
      {{ $t('gamification.event.display.goThere') }}
    </div>
    <div class="d-flex justify-center">
      <v-btn
        :href="buttonLink"
        max-width="250"
        class="ignore-vuetify-classes text-capitalize btn btn-primary">
        {{ buttonLabel }}
      </v-btn>
    </div>
  </div>
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
    isEditing: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      eventTypes: {
        NOTIFICATION_SETTING: ['addUserProfileNotificationSetting'],
        RELATIONSHIP: ['receiveRelationshipRequest', 'sendRelationshipRequest'],
      },
    };
  },
  computed: {
    eventType() {
      if (this.eventTypes.NOTIFICATION_SETTING.includes(this.trigger)) {
        return 'NOTIFICATION_SETTING';
      }
      if (this.eventTypes.RELATIONSHIP.includes(this.trigger)) {
        return 'RELATIONSHIP';
      }
      return 'PROFILE';
    },
    profileLink() {
      return `${eXo.env.portal.context}/${eXo.env.portal.portalName}/profile/${eXo.env.portal.userName}`;
    },
    peopleLink() {
      return `${eXo.env.portal.context}/${eXo.env.portal.portalName}/people`;
    },
    userNotificationSettingLink() {
      return `${eXo.env.portal.context}/${eXo.env.portal.portalName}/settings#notifications`;
    },
    eventConfig() {
      return {
        NOTIFICATION_SETTING: {
          button: this.$t('gamification.event.display.profileNotificationSettingAdded'),
          link: this.userNotificationSettingLink
        },
        RELATIONSHIP: {
          button: this.$t('gamification.event.display.connectWithOthers'),
          link: this.peopleLink
        },
        PROFILE: {
          button: this.$t('gamification.event.display.yourProfile'),
          link: this.profileLink
        }
      };
    },
    buttonLabel() {
      return this.eventConfig[this.eventType].button;
    },
    buttonLink() {
      return this.eventConfig[this.eventType].link;
    }
  }
};
</script>
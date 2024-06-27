<!--

  This file is part of the Meeds project (https://meeds.io/).

  Copyright (C) 2023 Meeds Association contact@meeds.io

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
  <v-hover v-slot="{ hover }">
    <tr>
      <td class="no-border-bottom ps-0">
        <div @click="openRule(false)" class="clickable d-flex flex-row ma-auto">
          <div class="d-flex flex-column align-center justify-center col-2 col-sm-1 pa-0">
            <rule-icon
              :rule-event="eventTitle"
              size="22"
              class="my-auto" />
          </div>
          <div class="d-flex flex-column col-10 col-sm-11 pa-0 text-truncate">
            <engagement-center-program-rule-title :rule="rule" />
            <span class="d-sm-none text-subtitle">{{ ruleScore }} {{ $t('challenges.label.points') }}</span>
          </div>
        </div>
      </td>
      <td
        cols="2"
        class="align-center no-border-bottom">
        <div v-if="hover || isMobile" class="d-flex">
          <div v-if="canAnnounce" class="align-center d-none d-sm-block">
            <v-btn
              icon
              class="me-2"
              @click="openRule(true)">
              <v-icon :size="iconSize">fas fa-bullhorn</v-icon>
            </v-btn>
          </div>
          <engagement-center-rule-menu
            v-if="canManageRule"
            :rule="rule"
            top />
        </div>
      </td>
      <td class="no-border-bottom d-none d-sm-inline">
        <div class="align-center">
          {{ ruleScore }}
        </div>
      </td>
      <td class="no-border-bottom  d-none d-sm-table-cell">
        <div
          :class="showAllAvatarList && 'AllUsersAvatar'"
          class="d-flex flex-nowrap my-2 justify-center">
          <engagement-center-avatars-list
            v-if="hasParticipants"
            :avatars="users"
            :max-avatars-to-show="maxAvatarsToShow"
            :avatars-count="realizationsCount"
            :size="27"
            @open-avatars-drawer="openAchievementsDrawer" />
          <div v-else>
            <span>
              -
            </span>
          </div>
        </div>
      </td>
    </tr>
  </v-hover>
</template>

<script>
export default {
  props: {
    rule: {
      type: Object,
      default: null,
    },
    canManageRule: {
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {
      maxAvatarsToShow: 3,
    };
  },
  computed: {
    ruleScore() {
      return this.rule?.score;
    },
    eventTitle() {
      return this.rule?.event?.title;
    },
    isMobile() {
      return this.$vuetify.breakpoint.xsOnly;
    },
    iconSize() {
      return this.isMobile ? 13 : 16;
    },
    users() {
      return this.rule?.realizations?.filter(realization => realization?.earner?.remoteId)
        .map(realization => ({
          userName: realization?.earner?.remoteId,
        })) || [];
    },
    realizationsCount() {
      return this.rule?.realizationsCount || 0;
    },
    hasParticipants() {
      return this.realizationsCount !== 0;
    },
    canAnnounce() {
      return this.rule?.type === 'MANUAL' && this.rule?.userInfo?.allowedToRealize;
    },
  },
  methods: {
    openRule(announceRule) {
      this.$root.$emit('rule-detail-drawer', this.rule, announceRule);
    },
    openAchievementsDrawer() {
      document.dispatchEvent(new CustomEvent('open-achievements-drawer', {detail: {
        rule: this.rule,
        backIcon: false,
      }}));
    },
  }
};
</script>
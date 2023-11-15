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
  <v-app
    v-if="displayWidget"
    :class="owner && 'profileBadge' || 'profileBadgeOther'"
    class="white"
    id="badgesOverview">
    <div class="card-border-radius overflow-hidden">
      <div
        v-if="isOverviewDisplay"
        v-show="!loading && hasBadges"
        class="subtitle-1">
        {{ $t('gamification.overview.badges') }}
      </div>
      <v-toolbar
        v-else
        id="badgesOverviewHeader"
        color="white"
        flat
        class="border-box-sizing"
        :class="isOverviewDisplay ? 'mb-5' : '64'">
        <div class="widget-text-header text-truncate">
          {{ $t('exoplatform.gamification.badgesByDomain') }}
        </div>
      </v-toolbar>
      <v-card
        v-if="!loading"
        :class="!loading && 'd-flex'"
        class="align-center justify-center"
        min-height="100"
        flat>
        <card-carousel
          v-if="hasBadges"
          class="d-flex flex-shrink-0 flex-grow-1 align-center justify-center full-height"
          dense>
          <badges-overview-item
            v-for="badge in badges"
            :key="badge.id"
            :badge="badge" />
        </card-carousel>
        <div v-else class="d-flex flex-column align-center justify-center full-height full-width py-4">
          <v-icon color="secondary" size="54">fa-graduation-cap</v-icon>
          <span
            v-html="emptyBadgesSummaryText"
            class="subtitle-1 font-weight-bold mt-7"></span>
        </div>
      </v-card>
    </div>
    <badges-overview-drawer />
    <gamification-rules-overview-list-drawer
      ref="listDrawer" />
    <engagement-center-rule-extensions />
  </v-app>
</template>

<script>
export default {
  props: {
    isOverviewDisplay: {
      type: Boolean,
      default: () => false,
    },
  },
  data: () => ({
    emptyBadgesActionName: 'gamification-bagdes-check-actions',
    badges: [],
    loading: true,
    isCurrentUserProfile: eXo.env.portal.userName === eXo.env.portal.profileOwner,
  }),
  computed: {
    hasBadges() {
      return this.badges?.length;
    },
    displayWidget() {
      return this.isOverviewDisplay || this.isCurrentUserProfile || (!this.loading && this.badges?.length);
    },
    emptyBadgesSummaryText() {
      return this.$t('gamification.overview.emptyBadgesMessage', {
        0: !this.isExternal && `<a class="primary--text font-weight-bold" href="javascript:void(0)" onclick="document.dispatchEvent(new CustomEvent('${this.emptyBadgesActionName}'))">` || '',
        1: !this.isExternal && '</a>' || '',
      });
    },
  },
  created() {
    this.refresh();
    document.addEventListener(this.emptyBadgesActionName, this.clickOnBadgesEmptyActionLink);
  },
  beforeDestroy() {
    document.removeEventListener(this.emptyBadgesActionName, this.clickOnBadgesEmptyActionLink);
  },
  methods: {
    refresh() {
      this.loading = true;
      return fetch(`${eXo.env.portal.context}/${eXo.env.portal.rest}/gamification/reputation/badges/${eXo.env.portal.profileOwnerIdentityId}`, {
        method: 'GET',
        credentials: 'include',
      })
        .then(resp => {
          if (!resp || !resp.ok) {
            throw new Error('Response code indicates a server error', resp);
          } else {
            return resp.json();
          }
        })
        .then(data => {
          this.badges = data || [];
          document.dispatchEvent(new CustomEvent('badgesCount', {detail: this.badges.length}));
          this.badges.forEach(badge => {
            badge.avatar = badge.url;
            badge.programLabel = badge?.program?.title;
            badge.badgeLabel = badge.title;
          });
          return this.$nextTick();
        })
        .finally(() => {
          this.loading = false;
          this.$root.$applicationLoaded();
        });
    },
    clickOnBadgesEmptyActionLink() {
      this.$refs?.listDrawer?.open();
    },
  },
};
</script>

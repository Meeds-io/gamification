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
    id="badgesOverview">
    <v-hover v-slot="{ hover }">
      <div class="application-body overflow-hidden d-flex flex-column">
        <component
          id="badgesOverviewHeader"
          :is="hasBadges && 'v-toolbar' || 'div'"
          color="transparent"
          flat
          class="border-box-sizing flex-grow-0">
          <div class="d-flex full-width align-center position-relative">
            <div v-if="hasBadges" class="widget-text-header text-truncate">
              {{ $t('exoplatform.gamification.badgesByDomain') }}
            </div>
            <v-btn
              v-if="$root.canEdit && hover"
              class="position-absolute t-0 r-0"
              icon
              small
              @click="$root.$emit('badges-overview-settings')">
              <v-icon>fa-cog</v-icon>
            </v-btn>
          </div>
        </component>
        <v-card
          v-if="!loading"
          :class="!loading && 'd-flex'"
          class="align-center justify-center transparent flex-grow-0 flex-shrink-0 px-5 pb-2"
          min-height="100"
          flat>
          <card-carousel
            v-if="hasBadges"
            class="d-flex width-max-content flex-shrink-0 flex-grow-0 align-center justify-center"
            dense>
            <badges-overview-item
              v-for="(badge, index) in sortedBadges"
              :key="`${badge.id}_${index}`"
              :badge="badge" />
          </card-carousel>
          <div v-else class="d-flex flex-column align-self-center align-center justify-center full-height full-width py-4">
            <v-icon color="tertiary" size="54">fa-graduation-cap</v-icon>
            <span
              v-html="emptyBadgesSummaryText"
              class="mt-7"></span>
          </div>
        </v-card>
      </div>
    </v-hover>
    <badges-overview-drawer />
    <badges-overview-settings-drawer v-if="$root.canEdit" />
    <gamification-rules-overview-list-drawer
      ref="listDrawer" />
    <engagement-center-rule-extensions />
  </v-app>
</template>

<script>
export default {
  data: () => ({
    emptyBadgesActionName: 'gamification-bagdes-check-actions',
    badges: [],
    loading: true,
    isCurrentUserProfile: eXo.env.portal.userName === eXo.env.portal.profileOwner,
    collator: new Intl.Collator(eXo.env.portal.language, {numeric: true, sensitivity: 'base'}),
  }),
  computed: {
    sortBy() {
      return this.$root.sortBy;
    },
    sortedBadges() {
      const badges = this.badges.slice();
      if (this.sortBy === 'alphanumeric') {
        badges.sort((b1, b2) => this.collator.compare(b1?.badgeLabel?.toLowerCase?.(), b2?.badgeLabel?.toLowerCase?.()));
      } else {
        badges.sort((b1, b2) => b2.startScore - b1.startScore);
      }
      return badges;
    },
    hasBadges() {
      return this.badges?.length;
    },
    displayWidget() {
      return this.isCurrentUserProfile || (!this.loading && this.badges?.length);
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
          const badges = data || [];
          badges.forEach(badge => {
            badge.avatar = badge.url;
            badge.programLabel = badge?.program?.title;
            badge.badgeLabel = badge.title;
          });
          this.badges = badges;
          document.dispatchEvent(new CustomEvent('badgesCount', {detail: this.badges.length}));
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

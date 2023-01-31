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
    :class="owner && 'profileBadge' || 'profileBadgeOther'"
    class="white"
    id="badgesOverview">
    <v-toolbar
      v-if="!isOverviewDisplay"
      id="badgesOverviewHeader"
      color="white"
      flat
      class="border-box-sizing"
      :height="isOverviewDisplay ? '50' : '64'">
      <div class="text-header-title text-sub-title">
        {{ $t('exoplatform.gamification.badgesByDomain') }}
      </div>
    </v-toolbar>
    <v-card flat>
      <v-card-text
        :class="isOverviewDisplay && 'my-auto pa-0' || 'pt-0'"
        class="d-flex flex-wrap justify-space-between">
        <template v-if="badges && badges.length">
          <badges-overview-item
            v-for="badge in badgesToDisplay"
            :key="badge.id"
            :badge="badge" />
        </template>
        <div
          v-else
          class="d-flex justify-center"
          :class="isOverviewDisplay && 'my-auto' || 'py-4 mx-auto'">
          <span class="emptyBadgesIcon display-3 my-1">
            Ø
          </span>
        </div>
      </v-card-text>
    </v-card>
    <badges-overview-drawer />
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
  computed: {
    badgesToDisplay() {
      return this.badges?.slice(0, 3);
    }
  },
  data: () => ({
    badges: [],
  }),
  created() {
    this.refresh();
  },
  methods: {
    refresh() {
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
            badge.domainLabel = this.getLabel('exoplatform.gamification.gamificationinformation.domain', badge.zone);
            badge.badgeLabel = this.getLabel('exoplatform.gamification.gamificationinformation.domain', badge.title);
          });
          return this.$nextTick();
        })
        .finally(() => this.$root.$applicationLoaded());
    },
    getLabel(base, key) {
      const label = `${base}.${key}`;
      const translation = this.$t(label);
      return translation === label && key || translation;
    },
  },
};
</script>

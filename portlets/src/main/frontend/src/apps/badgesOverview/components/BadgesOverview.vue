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
    class="white">
    <v-toolbar
      color="white"
      flat
      class="border-box-sizing">
      <div
        :class="skeleton && 'skeleton-text skeleton-text-width skeleton-background skeleton-text-height-thick skeleton-border-radius'"
        class="text-header-title text-sub-title">
        {{ $t('exoplatform.gamification.badgesByDomain') }}
      </div>
    </v-toolbar>
    <v-card flat>
      <v-card-text class="mx-auto d-flex flex-wrap justify-center pt-0">
        <template v-if="skeleton">
          <badges-overview-item
            v-for="i in 5"
            :key="i"
            :badge="{}"
            skeleton />
        </template>
        <template v-else-if="badges && badges.length">
          <badges-overview-item
            v-for="badge in badges"
            :key="badge.id"
            :badge="badge" />
        </template>
        <div v-else class="d-flex justify-center py-10">
          <span class="emptyBadgesIcon mb-2">
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
  data: () => ({
    badges: [],
    skeleton: true,
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
          this.badges.forEach(badge => {
            badge.avatar = badge.url;
            badge.domainLabel = this.getLabel('exoplatform.gamification.gamificationinformation.domain', badge.zone);
            badge.badgeLabel = this.getLabel('exoplatform.gamification.gamificationinformation.domain', badge.title);
          });
        })
        .finally(() => {
          this.skeleton = false;
          document.dispatchEvent(new CustomEvent('hideTopBarLoading'));
        });
    },
    getLabel(base, key) {
      const label = `${base}.${key}`;
      const translation = this.$t(label);
      return translation === label && key || translation;
    },
  },
};
</script>

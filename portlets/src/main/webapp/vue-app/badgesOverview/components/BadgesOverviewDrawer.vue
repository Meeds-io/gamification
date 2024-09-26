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
  <exo-drawer
    ref="badgesDrawer"
    :loading="loading"
    class="badgesDrawer"
    body-classes="hide-scroll decrease-z-index-more"
    right>
    <template slot="title">
      <div class="d-flex width-fit-content">
        {{ $t('gamification.badges.badgesOf') }}:
        <v-tooltip bottom>
          <template #activator="{ on }">
            <div class="ms-1 text-truncate" v-on="on">
              {{ programLabel }}
            </div>
          </template>
          <span>{{ programLabel }}</span>
        </v-tooltip>
      </div>
    </template>
    <template slot="content">
      <div class="pa-5 position-relative">
        <v-divider
          v-if="badges.length > 1"
          class="full-height position-absolute t-0 b-0 ms-7 my-8"
          size="1px"
          vertical />
        <div
          v-for="b in badges"
          :key="b.id">
          <badges-overview-drawer-item
            :badge="b"
            :current-score="currentScore"
            class="mb-2"
            @open="$refs.achievementsDrawer.open(user, period, programId)" />
        </div>
      </div>
      <users-leaderboard-profile-achievements-drawer
        ref="achievementsDrawer"
        go-back-button
        program-only />
    </template>
  </exo-drawer>
</template>

<script>
export default {
  data() {
    return {
      loading: false,
      user: null,
      badge: null,
      period: 'all',
      badges: [],
    };
  },
  computed: {
    programId() {
      return this.badge?.programId;
    },
    programLabel() {
      return this.badge?.programLabel;
    },
    currentScore() {
      return this.badge?.score;
    },
  },
  created() {
    this.$root.$on('open-badge-drawer', this.open);
  },
  methods: {
    async open(badge) {
      this.loading = true;

      this.badge = badge;
      const identity = await this.$identityService.getIdentityById(eXo.env.portal.profileOwnerIdentityId);
      this.user = {
        identityId: eXo.env.portal.profileOwnerIdentityId,
        score: this.badge.score,
        rank: 0,
        ...identity,
        ...identity?.profile,
      };
      const users = await this.$leaderboardService.getLeaderboard({
        identityId: eXo.env.portal.profileOwnerIdentityId,
        programId: this.programId,
        period: this.period,
        limit: 0,
      });
      const currentUser = users?.find?.(u => u.identityId === Number(eXo.env.portal.profileOwnerIdentityId));
      if (currentUser?.rank) {
        this.user.rank = currentUser?.rank;
      }
      return fetch(`${eXo.env.portal.context}/${eXo.env.portal.rest}/gamification/badges/all`, {
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
          let badges = data || [];
          const currentBadgeProgram = badges.find(tmp => tmp.id && tmp.id === this.badge?.id)?.program;
          badges = badges.filter(tmp => tmp?.program?.id && tmp?.program?.id === currentBadgeProgram?.id);
          badges.push({
            isCurrent: true,
            title: this.user.fullname,
            neededScore: this.badge.score,
            avatar: this.user.avatar,
          });
          badges.sort((a,b) => {
            const diff = b.neededScore - a.neededScore;
            if (diff) {
              return diff;
            } else if (b.isCurrent) {
              return 1;
            } else {
              return a.isCurrent ? -1 : 0;
            }
          });
          this.badges = badges;
          this.$refs.badgesDrawer.open();
        })
        .finally(() => this.loading = false);
    },
  },
};
</script>

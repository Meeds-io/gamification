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
    class="badgesDrawer"
    body-classes="hide-scroll decrease-z-index-more"
    right>
    <template slot="title">
      <div class="d-flex width-fit-content">
        {{ $t('exoplatform.gamification.badgesByDomain') }}:
        <v-tooltip bottom>
          <template #activator="{ on }">
            <div class="ms-1 text-truncate" v-on="on">
              {{ domainLabel }}
            </div>
          </template>
          <span>{{ domainLabel }}</span>
        </v-tooltip>
      </div>
    </template>
    <template slot="content">
      <badges-overview-drawer-item
        v-for="tmp in badges"
        :key="tmp.id"
        :badge="tmp"
        :current-score="badge && badge.score" />
    </template>
  </exo-drawer>
</template>

<script>
export default {
  data() {
    return {
      badge: null,
      badges: [],
    };
  },
  computed: {
    domainLabel() {
      return this.badge?.domainLabel;
    },
  },
  created() {
    this.$root.$on('open-badge-drawer', (badge) => {
      this.open(badge);
    });
  },
  methods: {
    open(badge) {
      this.badge = badge;

      this.$refs.badgesDrawer.startLoading();
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
          badges = badges.filter(tmp => tmp.domainDTO?.title === this.badge.zone);
          badges.push({
            isCurrent: true,
            neededScore: this.badge.score,
            avatar: `${eXo.env.portal.context}/${eXo.env.portal.rest}/v1/social/users/${eXo.env.portal.profileOwner}/avatar`,
          });
          badges.sort((a,b) => b.neededScore - a.neededScore);
          this.badges = badges;
          this.$refs.badgesDrawer.open();
        })
        .finally(() => {
          this.$refs.badgesDrawer.endLoading();
        });
    },
  },
};
</script>

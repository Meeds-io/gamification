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
  <v-app>
    <v-card flat class="pa-3">
      <div class="UserGamificationHeader text-color d-flex">
        <div class="align-start d-flex">
          <div
            class="d-inline-block">
            {{ $t('exoplatform.gamification.userLeaderboard.title') }}
          </div>
          <a
            :href="infoUrl"
            :title="$t('exoplatform.gamification.leaderboard.Howearnpoints')"
            class="d-inline-block mx-3">
            <i class="uiIconInformation uiIconLightBlue"></i>
          </a>
        </div>
        <div class="flex-grow-1"></div>
        <div class="align-end selectDomainFilterParent">
          <select
            v-model="selectedDomain"
            class="selectDomainFilter ignore-vuetify-classes mb-0">
            <users-leaderboard-domain-option
              v-for="domain in domains"
              :key="domain.title"
              :domain="domain" />
          </select>
        </div>
      </div>
      <v-tabs v-model="selectedPeriod">
        <v-tabs-slider color="tertiary" />
        <v-tab
          v-for="period in periods"
          :key="period.value"
          :href="`#${period.value}`">
          {{ period.text }}
        </v-tab>
      </v-tabs>
      <v-tabs-items v-model="selectedPeriod">
        <v-tab-item
          v-for="period in periods"
          :id="period.value"
          :key="period.value"
          :value="period.value">
          <v-list>
            <users-leaderboard-profile
              v-for="user in users"
              :key="user.username"
              :user="user"
              :domains="domains" />
            <template v-if="currentRank">
              <v-divider class="ma-0" />
              <v-list-item class="disabled-background">
                <div class="me-4">
                  {{ $t('exoplatform.gamification.leaderboard.rank') }}
                </div>
                <div>
                  <v-avatar color="tertiary" size="32">
                    {{ currentRank }}
                  </v-avatar>
                </div>
              </v-list-item>
            </template>
          </v-list>
        </v-tab-item>
      </v-tabs-items>
      <v-card-actions v-if="canLoadMore">
        <v-spacer />
        <v-btn
          class="primary--text"
          outlined
          link
          @click="loadNextPage">
          {{ $t('exoplatform.gamification.leaderboard.showMore') }}
        </v-btn>
        <v-spacer />
      </v-card-actions>
    </v-card>
  </v-app>
</template>
<script>
export default {
  data: () => ({
    domains: [],
    users: [],
    periods: [{
      value: 'WEEK',
      text: 'WEEK',
    },{
      value: 'MONTH',
      text: 'MONTH',
    },{
      value: 'ALL',
      text: 'ALL',
    }],
    pageSize: 10,
    limit: 10,
    currentRank: null,
    selectedDomain: null,
    loading: null,
    selectedPeriod: 'WEEK',
  }),
  computed: {
    infoUrl() {
      return `${eXo.env.portal.context}/${eXo.env.portal.portalName}/contributions/programs`;
    },
    canLoadMore() {
      return this.users && this.limit <= this.users.length;
    },
  },
  watch: {
    selectedPeriod(newVal, oldVal) {
      if (oldVal) {
        this.refreshBoard();
      }
    },
    selectedDomain(newVal, oldVal) {
      if (oldVal) {
        this.refreshBoard();
      }
    },
    limit(newVal, oldVal) {
      if (oldVal) {
        this.refreshBoard();
      }
    },
  },
  created() {
    this.retrieveDomains()
      .then(this.refreshBoard)
      .then(() => {
        this.periods = [{
          value: 'WEEK',
          text: this.$t('exoplatform.gamification.leaderboard.selectedPeriod.WEEK'),
        },{
          value: 'MONTH',
          text: this.$t('exoplatform.gamification.leaderboard.selectedPeriod.MONTH'),
        },{
          value: 'ALL',
          text: this.$t('exoplatform.gamification.leaderboard.selectedPeriod.ALL'),
        }];
        return this.$nextTick();
      })
      .finally(() => this.$root.$applicationLoaded());
  },
  methods: {
    refreshBoard() {
      const params = `domain=${this.selectedDomain && this.selectedDomain.title || this.selectedDomain || ''}&period=${this.selectedPeriod || 'WEEK'}&capacity=${this.limit}`;
      return fetch(`${eXo.env.portal.context}/${eXo.env.portal.rest}/gamification/leaderboard/filter?${params}`, {
        credentials: 'include',
      }).then(resp => resp && resp.ok && resp.json())
        .then(data => {
          const currentUser = data && data.find(user => !user.socialId);
          if (currentUser) {
            this.currentRank = currentUser.rank;
          }

          this.users = data && data.filter(user => user.socialId);
          this.users.sort((a, b) => b.score - a.score);
          this.users.forEach((user, index) => {
            user.rank = index + 1;
          });
        });
    },
    retrieveDomains() {
      return fetch(`${eXo.env.portal.context}/${eXo.env.portal.rest}/gamification/domains?type=ALL&sortByBudget=true`, {
        credentials: 'include',
      }).then(resp => resp && resp.ok && resp.json())
        .then(data => {
          const domains = data?.domains || [];
          domains.forEach(domain => {
            if (!domain || domain.label || !domain.title) {
              return;
            }
            const domainKey = `exoplatform.gamification.gamificationinformation.domain.${domain.title}`;
            const translation = this.$t(domainKey);
            domain.label = translation === domainKey && this.$t(domain.title) || translation;
          });
          const defaultDomain = {
            title: 'All',
            label: this.$t('exoplatform.gamification.leaderboard.domain.all'),
          };
          this.domains = [defaultDomain, ...domains];
          this.selectedDomain = defaultDomain.title;
        });
    },
    loadNextPage() {
      this.limit += this.pageSize;
    },
  },
};
</script>
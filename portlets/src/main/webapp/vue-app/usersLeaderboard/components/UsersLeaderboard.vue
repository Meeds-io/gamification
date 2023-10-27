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
    <v-card flat class="pa-5 card-border-radius">
      <div class="UserGamificationHeader text-color d-flex">
        <div class="align-start d-flex">
          <div
            class="d-inline-block widget-text-header text-truncate">
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
        <div class="align-end selectProgramFilterParent">
          <select
            v-model="selectedProgramId"
            class="selectProgramFilter ignore-vuetify-classes mb-0">
            <users-leaderboard-domain-option
              v-for="program in programs"
              :key="program.id"
              :program="program" />
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
          <v-progress-linear v-if="loading" indeterminate />
          <v-list v-if="!selectionChanged">
            <users-leaderboard-profile
              v-for="(user, index) in sortedUsers"
              :key="user.remoteId"
              :user="user"
              :rank="index + 1"
              :programs="programs" />
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
          :loading="loading"
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
    programs: [],
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
    selectedProgramId: '0',
    loading: false,
    selectionChanged: false,
    selectedPeriod: 'WEEK',
  }),
  computed: {
    infoUrl() {
      return `${eXo.env.portal.context}/${eXo.env.portal.portalName}/contributions/programs`;
    },
    canLoadMore() {
      return !this.selectionChanged && this.users && this.limit <= this.users.length;
    },
    sortedUsers() {
      return this.users.slice().sort((a, b) => b.score - a.score);
    },
  },
  watch: {
    selectedPeriod(newVal, oldVal) {
      if (oldVal) {
        this.selectionChanged = true;
        this.refreshBoard();
      }
    },
    selectedProgramId(newVal, oldVal) {
      if (oldVal) {
        this.selectionChanged = true;
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
    this.loading = true;
    this.retrievePrograms()
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
      .finally(() => {
        this.loading = false;
        this.$root.$applicationLoaded();
      });
  },
  methods: {
    refreshBoard() {
      const formData = new FormData();
      if (this.selectedProgramId && this.selectedProgramId !== '0') {
        formData.append('programId', this.selectedProgramId);
      }
      formData.append('period', this.selectedPeriod || 'WEEK');
      formData.append('capacity', this.limit);
      const params = decodeURIComponent(new URLSearchParams(formData).toString());

      this.loading = true;
      return fetch(`${eXo.env.portal.context}/${eXo.env.portal.rest}/gamification/leaderboard/filter?${params}`, {
        credentials: 'include',
      }).then(resp => resp?.ok && resp.json())
        .then(data => {
          const currentUser = data && data.find(user => !user.socialId);
          this.currentRank = currentUser?.rank;
          this.users = data && data.filter(user => user.socialId);
        })
        .finally(() => {
          this.loading = false;
          this.selectionChanged = false;
        });
    },
    retrievePrograms() {
      return fetch(`${eXo.env.portal.context}/${eXo.env.portal.rest}/gamification/programs?type=ALL`, {
        credentials: 'include',
      }).then(resp => resp?.ok && resp.json())
        .then(data => {
          const programs = data?.programs || [];
          programs.forEach(program => {
            if (!program || program.label || !program.title) {
              return;
            }
            const programKey = `exoplatform.gamification.gamificationinformation.domain.${program.title}`;
            const translation = this.$t(programKey);
            program.label = translation === programKey && this.$t(program.title) || translation;
          });
          const defaultProgram = {
            id: '0',
            title: 'All',
            label: this.$t('exoplatform.gamification.leaderboard.domain.all'),
          };
          this.programs = [defaultProgram, ...programs];
          this.selectedProgramId = '0';
        });
    },
    loadNextPage() {
      this.limit += this.pageSize;
    },
  },
};
</script>
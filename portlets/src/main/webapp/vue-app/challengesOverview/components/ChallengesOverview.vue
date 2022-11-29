<!--
  This file is part of the Meeds project (https://meeds.io/).
  Copyright (C) 2022 Meeds Association contact@meeds.io
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
    <gamification-overview-widget
      v-if="!displayChallenges"
      :loading="loading">
      <template #title>
        {{ $t('gamification.overview.emptyChallengesOverviewTitle') }}
      </template>
      <template #content>
        <gamification-overview-widget-row class="my-auto">
          <template #icon>
            <v-icon color="secondary" size="55px">fas fa-rocket</v-icon>
          </template>
          <template #content>
            <span v-html="emptySummaryText"></span>
          </template>
        </gamification-overview-widget-row>
      </template>
    </gamification-overview-widget>
    <gamification-overview-widget
      v-else
      :see-all-url="challengesURL"
      :extra-class="'px-0'">
      <template #title>
        {{ $t('gamification.overview.challengesOverviewTitle') }}
      </template>
      <template #content>
        <gamification-overview-widget-row
          class="py-auto"                   
          v-for="(item, index) in listChallenges" 
          :key="index"
          :redirection-url="`${challengesURL}/${item.challengeId}`">
          <template #icon>
            <v-icon
              color="yellow darken-2"
              size="30px"
              class="ps-4">
              fas fa-trophy
            </v-icon>
          </template>
          <template #content>
            <span>
              <v-list
                subheader
                two-line>
                <v-list-item
                  two-line>
                  <v-list-item-content>
                    <v-list-item-title class="">
                      {{ item.challengeTitle }}
                    </v-list-item-title>
                    <v-list-item-subtitle v-if="item.challengesAnnouncementsCount === 0"> 
                      {{ $t('gamification.overview.label.firstAnnounecement') }}
                    </v-list-item-subtitle>
                    <v-list-item-subtitle v-else-if="item.challengesAnnouncementsCount === 1"> 
                      {{ item.challengesAnnouncementsCount }}  {{ $t('gamification.overview.label.participant') }}
                    </v-list-item-subtitle>
                    <v-list-item-subtitle v-else> 
                      {{ item.challengesAnnouncementsCount }}  {{ $t('gamification.overview.label.participants') }}
                    </v-list-item-subtitle>
                  </v-list-item-content>
                  <v-list-item-action>
                    <v-list-item-action-text v-text="item.challengePoints + ' ' + $t('challenges.label.points') " class="mt-5" />
                  </v-list-item-action>
                </v-list-item>
              </v-list>
            </span>
          </template>
        </gamification-overview-widget-row>
      </template>
    </gamification-overview-widget>
  </v-app>
</template>
<script>
export default {
  data: () => ({
    emptyActionName: 'gamification-challengesOverview-check-action',
    search: '',
    challengePerPage: 3,
    announcementsPerChallenge: 2,
    filter: 'STARTED',
    loading: true,
    displayChallenges: false,
    listChallenges: [],
  }),
  computed: {
    emptySummaryText() {
      return this.$t('gamification.overview.challengesOverviewSummary', {
        0: `<a class="primary--text font-weight-bold" href="javascript:void(0)" onclick="document.dispatchEvent(new CustomEvent('${this.emptyActionName}'))">`,
        1: '</a>',
      });
    },
    challengesURL() {
      return `${eXo.env.portal.context}/${eXo.env.portal.portalName}/contributions/challenges`;
    },
  },
  created() {
    document.addEventListener(this.emptyActionName, this.clickOnEmptyActionLink);
    this.getChallenges();
  },
  beforeDestroy() {
    document.removeEventListener(this.emptyActionName, this.clickOnEmptyActionLink);
  },
  methods: {
    clickOnEmptyActionLink() {
      window.location.href = `${eXo.env.portal.context}/${eXo.env.portal.portalName}/contributions/programs`;
    },
    getChallenges() {
      this.loading = true;
      return this.$challengesServices.getAllChallengesByUser(this.search, 0, this.challengePerPage, this.announcementsPerChallenge, null, null, this.filter, true)
        .then(result => {
          if (!result) {
            return;
          }
          result.forEach(data => {
            const challenge = {};
            challenge.challengeId = data.id;
            challenge.challengeTitle = data.title;
            challenge.challengePoints =  data.points;
            challenge.challengesAnnouncementsCount =  data.announcements.length;
            this.listChallenges.push(challenge);
          });
          this.listChallenges = this.listChallenges.sort((challenge1, challenge2) => challenge2.challengePoints - challenge1.challengePoints);
          this.displayChallenges = this.listChallenges.length > 0;
        }).finally(() => this.loading = false);
    },
  },
};
</script>
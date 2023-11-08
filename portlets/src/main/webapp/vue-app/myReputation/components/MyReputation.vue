<!--
This file is part of the Meeds project (https://meeds.io/).
Copyright (C) 2022 Meeds Association
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
  <gamification-overview-widget 
    :title="!loading && !empty && $t('gamification.myReputation.title')"
    :loading="loading">
    <v-card
      :class="!kudosDisplayed && 'align-center justify-center'"
      class="d-flex flex-grow-1 fill-height"
      flat>
      <gamification-overview-widget-row
        v-show="kudosDisplayed && !loading"
        :class="kudosDisplayed && !loading && 'd-flex flex-column'">
        <template #title>
          <div class="subtitle-1 d-flex">
            <span>{{ $t('gamification.myReputation.KudosTitle') }}</span>
            <v-spacer />
            <v-btn
              height="auto"
              min-width="auto"
              class="pa-0"
              text
              @click="clickOnKudosEmptyActionLink">
              <span class="primary--text text-none">{{ $t('gamification.overview.send') }}</span>
            </v-btn>
          </div>
        </template>
        <template #content>
          <extension-registry-components
            :params="params"
            name="my-reputation-overview-kudos"
            type="my-reputation-item"
            class="d-flex flex-column mx-n4" />
        </template>
      </gamification-overview-widget-row>
      <div v-if="!kudosDisplayed && !loading" class="d-flex flex-column align-center justify-center">
        <v-icon color="secondary" size="54">fa-award</v-icon>
        <span
          v-html="emptyKudosSummaryText"
          class="subtitle-1 font-weight-bold mt-7"></span>
      </div>
    </v-card>
    <v-card
      class="d-flex flex-grow-1 fill-height full-width"
      flat>
      <v-card
        v-show="badgesDisplayed && !loading"
        class="full-height full-width"
        flat>
        <div class="d-flex flex-column full-height">
          <div class="subtitle-1 d-flex">
            {{ $t('gamification.overview.badges') }}
          </div>
          <card-carousel
            v-if="badgesDisplayed"
            class="d-flex flex-shrink-0 flex-grow-1 align-center justify-center"
            dense>
            <extension-registry-components
              :params="params"
              name="my-reputation-overview-badges"
              type="my-reputation-item"
              class="d-flex flex-column"
              element-class="px-2" />
          </card-carousel>
        </div>
      </v-card>
      <div v-if="!badgesDisplayed && !loading" class="d-flex flex-column align-center justify-center full-width">
        <v-icon color="secondary" size="54">fa-graduation-cap</v-icon>
        <span
          v-html="emptyBadgesSummaryText"
          class="subtitle-1 font-weight-bold mt-7"></span>
      </div>
    </v-card>
  </gamification-overview-widget>
</template>
<script>
export default {
  data: () => ({
    emptyKudosActionName: 'gamification-myReputation-kudos-check-actions',
    emptyBadgesActionName: 'gamification-myReputation-bagdes-check-actions',
    receivedKudosCount: 0,
    kudosDisplayed: true,
    badgesDisplayed: true,
    loadingKudos: true,
    loadingBadges: true,
  }),
  computed: {
    params() {
      return {
        isOverviewDisplay: true,
      };
    },
    empty() {
      return !this.kudosDisplayed && !this.badgesDisplayed;
    },
    loading() {
      return this.loadingBadges || this.loadingKudos;
    },
    isExternal() {
      return eXo.env.portal.isExternal === 'true';
    },
    emptyKudosSummaryText() {
      return this.$t('gamification.overview.emptyKudosMessage', {
        0: !this.isExternal && `<a class="primary--text font-weight-bold" href="javascript:void(0)" onclick="document.dispatchEvent(new CustomEvent('${this.emptyKudosActionName}'))">` || '',
        1: !this.isExternal && '</a>' || '',
      });
    },
    emptyBadgesSummaryText() {
      return this.$t('gamification.overview.emptyBadgesMessage', {
        0: !this.isExternal && `<a class="primary--text font-weight-bold" href="javascript:void(0)" onclick="document.dispatchEvent(new CustomEvent('${this.emptyBadgesActionName}'))">` || '',
        1: !this.isExternal && '</a>' || '',
      });
    },
    kudosData() {
      return (this.sentKudosSize + this.receivedKudosSize) > 0;
    }
  },
  created() {
    document.addEventListener(this.emptyKudosActionName, this.clickOnKudosEmptyActionLink);
    document.addEventListener(this.emptyBadgesActionName, this.clickOnBadgesEmptyActionLink);
    document.addEventListener('kudosCount', (event) => {
      if (event) {
        this.kudosDisplayed = event.detail > 0;
        this.$nextTick().then(() => this.loadingKudos = false);
      }
    });
    document.addEventListener('badgesCount', (event) => {
      if (event) {
        this.badgesDisplayed = event.detail > 0;
        this.$nextTick().then(() => this.loadingBadges = false);
      }
    });
    this.$root.$applicationLoaded();
  },
  beforeDestroy() {
    document.removeEventListener(this.emptyKudosActionName, this.clickOnKudosEmptyActionLink);
    document.removeEventListener(this.emptyBadgesActionName, this.clickOnBadgesEmptyActionLink);
  },
  methods: {
    clickOnKudosEmptyActionLink() {
      document.dispatchEvent(new CustomEvent('exo-kudos-open-send-modal', {detail: {
        id: eXo.env.portal.userIdentityId,
        type: 'USER_PROFILE',
        parentId: '',
        owner: eXo.env.portal.userName,
      }}));
    },
    clickOnBadgesEmptyActionLink() {
      document.dispatchEvent(new CustomEvent('rules-overview-list-drawer'));
    },
  },
};
</script>
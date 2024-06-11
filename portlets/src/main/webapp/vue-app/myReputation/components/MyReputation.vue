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
      class="d-flex flex-grow-1 fill-height transparent"
      flat>
      <gamification-overview-widget-row
        v-show="!loading"
        :class="!loading && 'd-flex flex-column'">
        <template #title>
          <div v-if="kudosDisplayed" class="subtitle-1 d-flex">
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
        <extension-registry-components
          :params="params"
          name="my-reputation-overview-kudos"
          type="my-reputation-item"
          class="d-flex flex-column mx-n4" />
      </gamification-overview-widget-row>
    </v-card>
    <v-card
      class="d-flex flex-grow-1 fill-height transparent full-width"
      flat>
      <v-card
        class="full-height full-width transparent"
        flat>
        <div class="d-flex flex-column full-height">
          <extension-registry-components
            :params="params"
            name="my-reputation-overview-badges"
            type="my-reputation-item"
            class="d-flex flex-column" />
        </div>
      </v-card>
    </v-card>
  </gamification-overview-widget>
</template>
<script>
export default {
  data: () => ({
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
    kudosData() {
      return (this.sentKudosSize + this.receivedKudosSize) > 0;
    }
  },
  watch: {
    loading() {
      if (!this.loading) {
        this.$root.$applicationLoaded();
      }
    },
  },
  created() {
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
  },
};
</script>
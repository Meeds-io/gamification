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
  <v-app id="winnersDetails">
    <exo-drawer
      ref="winnersDetails"
      right
      @closed="close">
      <template slot="title">
        <div>
          <i
            v-if="back"
            class="uiIcon uiArrowBAckIcon"
            @click="close"></i>  <span class="pb-2"> {{ $t('challenges.winners.details') }} </span>
        </div>
      </template>
      <template slot="content">
        <v-row 
          v-for="winner in listWinners" 
          :key="winner.user"
          class="mx-auto">
          <v-col>
            <exo-user-avatar
              :profile-id="winner.user"
              :size="44"
              extra-class="px-4 py-3"
              bold-title
              link-style
              popover>
              <template slot="subTitle">
                <a :href="getLinkActivity(winner.activityId)">
                  <relative-date-format
                    class="text-capitalize-first-letter text-light-color text-truncate"
                    :value="winner.createDate" />
                </a>
              </template>
            </exo-user-avatar>
          </v-col>
          <v-col class="d-flex align-center justify-center">
            <v-tooltip v-if="!winner.noActivityId" bottom>
              <template #activator="{ on }">
                <a v-on="on">
                  <span>
                    <v-icon
                      size="16"
                      color="dark-grey"
                      @click="redirectToLinkActivity(winner.activityId)">fas fa-eye</v-icon>
                  </span>
                </a>
              </template>
              <span>{{ $t('program.winner.label.checkActivity') }}</span>
            </v-tooltip>
            <v-tooltip v-else bottom>
              <template #activator="{ on }">
                <a v-on="on">
                  <span>
                    <v-icon
                      size="16"
                      color="grey"
                      class="not-clickable ">fas fa-eye</v-icon>
                  </span>
                </a>
              </template>
              <span>{{ $t('program.winner.label.noActivity') }}</span>
              {{ $t('challenges.winners.details') }}
            </v-tooltip>
          </v-col>
        </v-row>
      </template>
      <template v-if="showPagination" slot="footer">
        <div class="text-center">
          <v-pagination
            v-model="page"
            :length="paginationPageCount"
            circle
            light
            flat
            @input="loadMore" />
        </div>
      </template>
    </exo-drawer>
  </v-app>
</template>

<script>

export default {
  data() {
    return {
      challengeId: false,
      showLoadMoreButton: false,
      announcementTotalCount: 0,
      page: 1,
      loading: true,
      announcement: [],
      listWinners: []
    };
  },
  watch: {
    loading() {
      if (this.loading) {
        this.$refs.winnersDetails.startLoading();
      } else {
        this.$refs.winnersDetails.endLoading();
      }
    },
    announcementPerPage() {
      this.retrieveAnnouncements();
    },
  },
  created() {
    this.$root.$on('open-winners-drawer', this.open);
  },
  computed: {
    paginationPageCount() {
      return Math.ceil(this.announcementTotalCount / this.announcementPerPage);
    },
    showPagination() {
      return this.announcementTotalCount >= this.announcementPerPage;
    },
    announcementPerPage() {
      return Math.round((this.$vuetify.breakpoint.height - 122) / 100);
    },
  },
  methods: {
    getLinkActivity(id) {
      return `${eXo.env.portal.context}/${eXo.env.portal.portalName}/activity?id=${id}`;
    },
    redirectToLinkActivity(id) {
      window.location.href = `${eXo.env.portal.context}/${eXo.env.portal.portalName}/activity?id=${id}`;
    },
    close() {
      this.$refs.winnersDetails.close();
    },
    open(challengeId, earnerType, announcementsCount) {
      this.announcementTotalCount = announcementsCount;
      this.listWinners = [];
      this.challengeId = challengeId;
      this.$refs.winnersDetails.open();
      this.retrieveAnnouncements(earnerType);
    },
    loadMore() {
      this.retrieveAnnouncements();
    },
    retrieveAnnouncements(earnerType) {
      this.loading = true;
      const offset = (this.page - 1) * this.announcementPerPage;
      this.listWinners = [];
      this.$challengesServices.getAllAnnouncementsByChallenge(this.challengeId, earnerType, offset, this.announcementPerPage).then(announcements => {
        if (announcements.length > 0) {
          announcements.filter(announce => announce.assignee).map(announce => {
            const announcement = {
              user: announce.assignee,
              activityId: announce.activityId,
              createDate: announce.createdDate,
              noActivityId: announce.activityId === null,
            };
            this.listWinners.push(announcement);
          });
        }
      }).finally(() => this.loading = false);
    },
  }
};
</script>
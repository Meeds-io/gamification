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
  <exo-drawer
    id="EngagementCenterWinnersDetails"
    ref="winnersDetails"
    v-model="drawer"
    allow-expand
    right
    @closed="close">
    <template slot="title">
      <div>
        <v-btn
          v-if="backIcon"
          icon
          class="ms-n2 mt-n2 position-absolute"
          @click="close">
          <v-icon
            size="16"
            class="mx-1 ps-1">
            {{ $vuetify.rtl && 'fa fa-arrow-right' || 'fa fa-arrow-left' }}
          </v-icon>
        </v-btn>
        <span :class="backIcon && 'ms-8' || 'pb-2'">
          {{ $t('challenges.winners.details') }}
        </span>
      </div>
    </template>
    <template slot="content">
      <engagement-center-rule-header
        :rule="rule"
        :action-value-extensions="actionValueExtensions"
        class="pa-4" />
      <v-list-item>
        <v-list-item-title>
          {{ $t('rules.latestAnnouncements') }}
        </v-list-item-title>
      </v-list-item>
      <v-list-item
        v-for="winner in listWinners"
        :key="winner.user">
        <v-list-item-content class="d-inline">
          <exo-user-avatar
            :profile-id="winner.user"
            :size="44"
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
        </v-list-item-content>
        <v-list-item-action>
          <v-tooltip :disabled="$root.isMobile" bottom>
            <template #activator="{ on }">
              <div v-on="on">
                <v-btn
                  :href="`${basePath}/activity?id=${winner.activityId}`"
                  :disabled="winner.noActivityId"
                  icon>
                  <v-icon
                    size="16"
                    color="dark-grey">
                    fas fa-eye
                  </v-icon>
                </v-btn>
              </div>
            </template>
            <span>{{ winner.noActivityId && noActivityLabel || $t('program.winner.label.checkActivity') }}</span>
          </v-tooltip>
        </v-list-item-action>
      </v-list-item>
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
</template>
<script>
export default {
  props: {
    actionValueExtensions: {
      type: Object,
      default: function() {
        return null;
      },
    },
  },
  data() {
    return {
      drawer: false,
      backIcon: false,
      rule: null,
      showLoadMoreButton: false,
      announcementTotalCount: 0,
      page: 1,
      loading: true,
      announcement: [],
      listWinners: [],
      earnerType: 'USER'
    };
  },
  computed: {
    basePath() {
      return `${eXo.env.portal.context}/${eXo.env.portal.portalName}`;
    },
    paginationPageCount() {
      return Math.ceil(this.announcementTotalCount / this.announcementPerPage);
    },
    showPagination() {
      return this.announcementTotalCount >= this.announcementPerPage;
    },
    announcementPerPage() {
      return Math.round((this.$vuetify.breakpoint.height - 122) / 70);
    },
    noActivityLabel() {
      return this.$t(`program.winner.label.${this.automaticRule ? 'noActivity' : 'activityDeleted'}`);
    },
    automaticRule() {
      return this.rule?.type === 'AUTOMATIC';
    },
    ruleId() {
      return this.rule?.id;
    },
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
      if (this.drawer) {
        this.retrieveAnnouncements();
      }
    },
  },
  created() {
    this.$root.$on('open-winners-drawer', this.open);
  },
  methods: {
    getLinkActivity(id) {
      return `${eXo.env.portal.context}/${eXo.env.portal.portalName}/activity?id=${id}`;
    },
    close() {
      this.$refs.winnersDetails.close();
      this.page = 1;
    },
    open(rule, backIcon) {
      this.announcementTotalCount = rule?.announcementsCount;
      this.listWinners = [];
      this.rule = rule;
      this.backIcon = backIcon;
      this.$refs.winnersDetails.open();
      this.retrieveAnnouncements(this.earnerType);
    },
    loadMore() {
      this.retrieveAnnouncements();
    },
    retrieveAnnouncements(earnerType) {
      const offset = (this.page - 1) * this.announcementPerPage;
      this.listWinners = [];

      this.loading = true;
      this.$announcementService.getAnnouncements(this.ruleId, earnerType, offset, this.announcementPerPage)
        .then(announcements => {
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
        })
        .finally(() => this.loading = false);
    },
  }
};
</script>
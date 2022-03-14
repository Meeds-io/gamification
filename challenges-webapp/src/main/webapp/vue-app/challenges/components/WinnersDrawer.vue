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
        <exo-user-avatar
          v-for="winner in listWinners" 
          :key="winner.user"
          :profile-id="winner.user"
          :size="44"
          extra-class="px-4 py-3 border-bottom-color"
          bold-title
          popover>
          <template slot="subTitle">
            <a :href="getLinkActivity(winner.activityId)">
              <relative-date-format
                class="text-capitalize-first-letter text-light-color text-truncate"
                :value="winner.createDate" />
            </a>
          </template>
        </exo-user-avatar> 
      </template>
      <template v-if="showLoadMoreButton" slot="footer">
        <v-row class="ml-6 mr-6 mb-6 mt-n4 d-none d-lg-inline">
          <v-btn
            v-if="showLoadMoreButton"
            :loading="loading"
            :disabled="loading"
            class="loadMoreButton ma-auto btn"
            block
            @click="loadMore">
            {{ $t('challenges.button.ShowMore') }}
          </v-btn>
        </v-row>
      </template>
    </exo-drawer>
  </v-app>
</template>

<script>

export default {
  props: {
    challengeId: {
      type: String,
      default: ''
    },
    back: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      showLoadMoreButton: false,
      announcementPerPage: 20,
      loading: true,
      announcement: [],
      listWinners: []
    };
  },
  methods: {
    getLinkActivity(id) {
      return `${eXo.env.portal.context}/${eXo.env.portal.portalName}/activity?id=${id}`;
    },
    close() {
      this.$refs.winnersDetails.close();
    },
    open() {
      this.listWinners = [];
      this.getAnnouncement(false);
      this.$nextTick().then(()=> this.$refs.winnersDetails.open());
    },
    loadMore() {
      this.getAnnouncement();
    },
    getAnnouncement(append = true) {
      this.loading = true;
      const offset = append ? this.announcement.length : 0;
      this.$challengesServices.getAllAnnouncementsByChallenge(this.challengeId, offset, this.announcementPerPage,).then(announcements => {
        if (announcements.length >= this.announcementPerPage) {
          this.showLoadMoreButton = true;
        } else {
          this.showLoadMoreButton = false;
        }
        this.announcement = append && this.announcement.concat(announcements) || announcements;
        if (announcements.length > 0) {
          announcements.map(announce => {
            const announcement = {
              user: announce.assignee,
              activityId: announce.activityId,
              createDate: announce.createdDate
            };
            this.listWinners.push(announcement);

          });
        }
      }).finally(() => {
        this.loading = false;
      });
    },
  }
};
</script>
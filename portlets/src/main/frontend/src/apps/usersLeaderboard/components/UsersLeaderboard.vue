<template>
  <v-app>
    <v-card flat class="pa-3">
      <div class="UserGamificationHeader text-color d-flex">
        <div class="align-start d-flex">
          <div
            :class="skeleton && 'skeleton-background skeleton-text skeleton-border-radius skeleton-text-width skeleton-text-height'"
            class="d-inline-block">
            {{ skeleton && '&nbsp;&nbsp;&nbsp;' || $t('exoplatform.gamification.userLeaderboard.title') }}
          </div>
          <a
            :href="infoUrl"
            :class="skeleton && 'skeleton-background skeleton-text'"
            :title="$t('exoplatform.gamification.leaderboard.Howearnpoints')"
            class="d-inline-block mx-3">
            <i class="uiIconInformation uiIconLightBlue"></i>
          </a>
        </div>
        <div class="flex-grow-1"></div>
        <div class="align-end selectDomainFilterParent">
          <select
            v-model="selectedDomain"
            :class="skeleton && 'skeleton-background skeleton-text skeleton-text-width'"
            class="selectDomainFilter ignore-vuetify-classes mb-0">
            <users-leaderboard-domain-option
              v-for="domain in domains"
              :key="domain.title"
              :skeleton="skeleton"
              :domain="domain" />
          </select>
        </div>
      </div>
      <v-tabs v-model="selectedPeriod">
        <v-tabs-slider :color="skeleton && 'skeleton-text' || 'tertiary'" />
        <v-tab
          v-for="period in periods"
          :key="period.value"
          :href="`#${period.value}`"
          :class="skeleton && 'skeleton-background skeleton-text skeleton-border-radius skeleton-text-height' || ''">
          {{ skeleton && '&nbsp;' || period.text }}
        </v-tab>
      </v-tabs>
      <v-tabs-items v-model="selectedPeriod">
        <v-tab-item
          v-for="period in periods"
          :id="period.value"
          :key="period.value"
          :value="period.value">
          <v-list>
            <template v-if="skeleton">
              <users-leaderboard-profile
                v-for="i in 10"
                :key="i"
                :user="{}"
                :domains="[]"
                skeleton />
            </template>
            <template v-else>
              <users-leaderboard-profile
                v-for="user in users"
                :key="user.username"
                :user="user"
                :domains="domains" />
            </template>
            <template v-if="currentRank || skeleton">
              <v-divider class="ma-0" />
              <v-list-item class="disabled-background">
                <div
                  :class="skeleton && 'skeleton-background skeleton-text skeleton-text-width skeleton-text-height'"
                  class="mr-4">
                  {{ $t('exoplatform.gamification.leaderboard.rank') }}
                </div>
                <div>
                  <v-avatar
                    :color="skeleton && 'skeleton-background' || 'tertiary'"
                    size="32">
                    {{ skeleton && '&nbsp;&nbsp;' || currentRank }}
                  </v-avatar>
                </div>
              </v-list-item>
            </template>
          </v-list>
        </v-tab-item>
      </v-tabs-items>
      <v-card-actions v-if="canLoadMore || skeleton">
        <v-spacer />
        <v-btn
          :disabled="skeleton"
          :class="skeleton && 'skeleton-background skeleton-text skeleton-text-width skeleton-text-height'"
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
    skeleton: true,
    currentRank: null,
    selectedDomain: null,
    selectedPeriod: 'WEEK',
  }),
  computed: {
    infoUrl() {
      return `${eXo.env.portal.context}/${eXo.env.portal.portalName}/gamification-earn-points`;
    },
    canLoadMore() {
      return this.users && this.limit <= this.users.length;
    },
  },
  watch: {
    selectedPeriod() {
      this.refreshBoard();
    },
    selectedDomain() {
      this.refreshBoard();
    },
    limit() {
      this.refreshBoard();
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
      })
      .finally(() => {
        document.dispatchEvent(new CustomEvent('hideTopBarLoading'));
        this.skeleton = false;
      });
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
      return fetch(`${eXo.env.portal.context}/${eXo.env.portal.rest}/gamification/api/v1/domains`, {
        credentials: 'include',
      }).then(resp => resp && resp.ok && resp.json())
        .then(data => {
          const domains = data || [];
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
            label: this.$t(`exoplatform.gamification.leaderboard.domain.all`),
          };
          this.domains = [defaultDomain, ...domains];
          this.selectedDomain = defaultDomain.title;
        });
    },
    loadNextPage() {
      this.limit += this.pageSize;
    },
  },
}
</script>
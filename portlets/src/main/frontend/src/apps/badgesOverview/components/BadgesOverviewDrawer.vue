<template>
  <exo-drawer
    ref="badgesDrawer"
    class="badgesDrawer"
    body-classes="hide-scroll decrease-z-index-more"
    right>
    <template slot="title">
      {{ $t('exoplatform.gamification.badgesByDomain') }}: {{ badge && badge.domainLabel }}
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
          badges = badges.filter(tmp => tmp.domain === this.badge.zone);
          badges.push({
            isCurrent : true,
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

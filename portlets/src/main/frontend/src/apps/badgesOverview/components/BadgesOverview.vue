<template>
  <v-app
    :class="owner && 'profileAboutMe' || 'profileAboutMeOther'"
    class="white">
    <v-toolbar
      color="white"
      flat
      class="border-box-sizing">
      <div class="text-header-title text-sub-title">
        {{ $t('exoplatform.gamification.badgesByDomain') }}
      </div>
    </v-toolbar>
    <v-card flat>
      <v-card-text class="mx-auto d-flex justify-center py-0">
        <badges-overview-item
          v-for="badge in badges"
          :key="badge.id"
          :badge="badge" />
      </v-card-text>
    </v-card>
    <badges-overview-drawer />
  </v-app>
</template>

<script>
export default {
  data: () => ({
    badges: [],
  }),
  created() {
    this.refresh();
  },
  methods: {
    refresh() {
      return fetch(`${eXo.env.portal.context}/${eXo.env.portal.rest}/gamification/reputation/badges/${eXo.env.portal.profileOwnerIdentityId}`, {
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
          this.badges = data || [];
          this.badges.forEach(badge => {
            badge.avatar = badge.url;
            badge.domainLabel = this.getLabel('exoplatform.gamification.gamificationinformation.domain', badge.zone);
            badge.badgeLabel = this.getLabel('exoplatform.gamification.gamificationinformation.domain', badge.title);
          });
        })
        .finally(() => {
          document.dispatchEvent(new CustomEvent('hideTopBarLoading'));
        });
    },
    getLabel(base, key) {
      const label = `${base}.${key}`;
      const translation = this.$t(label);
      return translation === label && key || translation;
    },
  },
};
</script>

<template>
  <v-list-item class="pa-0 BadgeItem">
    <v-list-item-action-text 
      class="BadgeItemPoints"
      :class="isCurrent && 'primary--text' || !isAquired && 'text-sub-title' || 'text-color'">
      {{ score }}
      {{ $t('exoplatform.gamification.gamificationinformation.Points') }}
    </v-list-item-action-text>
    <v-list-item-avatar tile class="BadgeItemAvatarParent">
      <v-list-item-avatar
        :tile="!isCurrent"
        :class="isCurrent && 'BadgeItemAvatarProfile' || !isAquired && 'badgeNotAquired'"
        class="BadgeItemAvatar mx-2">
        <v-img :src="avatar" />
      </v-list-item-avatar>
    </v-list-item-avatar>
    <v-list-item-content>
      <v-list-item-title
        v-if="title"
        :class="isCurrent && 'primary--text' || !isAquired && 'text-sub-title' || 'text-color'">
        {{ title }}
      </v-list-item-title>
      <v-list-item-title
        v-if="description"
        :title="description"
        class="caption text-sub-title">
        {{ description }}
      </v-list-item-title>
    </v-list-item-content>
  </v-list-item>
</template>

<script>
export default {
  props: {
    badge: {
      type: Object,
      default: () => ({}),
    },
    currentScore: {
      type: Number,
      default: 0,
    },
  },
  computed: {
    isCurrent() {
      return this.badge.isCurrent;
    },
    isAquired() {
      return this.isCurrent || this.currentScore >= this.badge.neededScore;
    },
    avatar() {
      return this.badge.avatar || `${eXo.env.portal.context}/${eXo.env.portal.rest}/gamification/reputation/badge/${this.badge.iconFileId || this.badge.id}/avatar`
    },
    title() {
      if (this.isCurrent) {
        return null;
      } else {
        return this.getLabel('badge.title', this.badge.title);
      }
    },
    description() {
      if (this.isCurrent) {
        return null;
      } else {
        return this.badge.description;
      }
    },
    score() {
      if (this.badge.neededScore) {
        return this.badge.neededScore;
      }
      return this.currentScore;
    },
  },
  methods: {
    getLabel(base, key) {
      const label = `${base}.${key}`;
      const translation = this.$t(label);
      return translation === label && key || translation;
    },
  },
};
</script>
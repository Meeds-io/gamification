<template>
  <v-layout
    row
    wrap
    mx-0>
    <v-flex
      xs12>
      <v-list>
        <template>
          <v-list-item class="suggestions-list-item pa-0">
            <v-list-item-avatar
              :size="avatarSize">
              <v-img
                :src="!skeleton && avatarUrl || ''"
                :class="skeleton && 'skeleton-background'" />
            </v-list-item-avatar>
            <v-list-item-content class="py-0">
              <v-list-item-title
                class="body-2 font-weight-bold suggestions-list-item-title">
                <a
                  :href="url"
                  :class="skeleton && 'skeleton-background skeleton-text skeleton-list-item-title skeleton-border-radius'"
                  class="font-weight-bold subtitle-2 request-user-name darken-2">
                  {{ spaceSuggestion.displayName }}
                </a>
              </v-list-item-title>
              <v-list-item-subtitle
                class="caption text-sub-title suggestions-list-item-subtitle">
                <span :class="skeleton && 'skeleton-background skeleton-text skeleton-list-item-subtitle skeleton-border-radius'">
                  {{ spaceSuggestion.members }} {{ $t('spacemember.Label') }}
                </span>
              </v-list-item-subtitle>
            </v-list-item-content>
            <v-list-item-action class="suggestions-list-item-actions">
              <v-btn-toggle class="transparent">
                <a
                  :class="skeleton && 'skeleton-background skeleton-text skeleton-border-radius'"
                  text
                  icon
                  small
                  min-width="auto"
                  class="px-0 suggestions-btn-action connexion-accept-btn"
                  @click="joinSpace(spaceSuggestion)">
                  <i class="uiIconPlusLight"></i>
                </a>
                <a
                  :class="skeleton && 'skeleton-background skeleton-text skeleton-border-radius'"
                  text
                  small
                  min-width="auto"
                  class="px-0 suggestions-btn-action connexion-refuse-btn"
                  @click="ignoredSuggestionSpace(spaceSuggestion)">
                  <i class="uiIconCloseCircled"></i>
                </a>
              </v-btn-toggle>
            </v-list-item-action>
          </v-list-item>
        </template>
      </v-list>
    </v-flex>
  </v-layout>
</template>


<script>
export default {
  props: {
    spaceSuggestion: {
      type: Object,
      default: () => null,
    },
    avatarSize: {
      type: Number,
      default: () => 30,
    },
    skeleton: {
      type: Boolean,
      default: false,
    },
    spacesSuggestionsList: {
      type: Array,
      default: () => []
    }
  },
  computed: {
    avatarUrl() {
      return this.spaceSuggestion && this.spaceSuggestion.spaceAvatarUrl || `${eXo.env.portal.context}/${eXo.env.portal.rest}/v1/social/spaces/${this.spaceSuggestion.spaceUrl}/avatar`;
    },
    url() {
      if (!this.spaceSuggestion || !this.spaceSuggestion.spaceId) {
        return '#';
      }
      return `${eXo.env.portal.context}/g/:spaces:${this.spaceSuggestion.spaceUrl}/`;
    },
  },
  methods: {
    joinSpace(item) {
      this.$spaceService.requestJoin(item.spaceId).then(
        () => {
          this.spacesSuggestionsList.splice(this.spacesSuggestionsList.indexOf(item),1);
        }
      );
    },
    ignoredSuggestionSpace(item) {
      this.$spaceService.ignoreSuggestion(item).then(
        () => {
          this.spacesSuggestionsList.splice(this.spacesSuggestionsList.indexOf(item),1);
        }
      );
    },
  },
};
</script>

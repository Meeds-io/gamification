<template>
  <v-expansion-panel>
    <v-expansion-panel-header class="pa-3" hide-actions>
      <template #default="{open}">
        <v-list-item flat dense>
          <v-list-item-icon class="me-2">
            <v-icon size="16" v-text="open && 'fa-chevron-up' || 'fa-chevron-down'" />
          </v-list-item-icon>
          <v-list-item-content class="flex flex-grow-0 flex-shrink-0 me-2">
            {{ title }}
          </v-list-item-content>
          <v-list-item-content class="flex flex-grow-0 flex-shrink-0 me-2">
            ( {{ size }} )
          </v-list-item-content>
          <v-list-item-content>
            <v-divider />
          </v-list-item-content>
        </v-list-item>
      </template>
    </v-expansion-panel-header>
    <v-expansion-panel-content>
      <v-container>
        <v-row no-gutters>
          <v-col
            v-for="challenge in challenges"
            :key="challenge.id"
            class="mb-4"
            cols="12"
            md="6"
            lg="4"
            xl="3">
            <challenge-card
              :domain="domain"
              :challenge="challenge" />
          </v-col>
        </v-row>
        <v-row v-if="hasMore" class="ml-6 mr-6 mb-6 mt-n4 d-none d-lg-inline">
          <v-btn
            :loading="loading"
            :disabled="loading"
            class="loadMoreButton ma-auto mt-4 btn"
            block
            @click="$root.$emit('challenge-load-more', domain.id)">
            {{ $t('challenges.button.ShowMore') }}
          </v-btn>
        </v-row>
      </v-container>
    </v-expansion-panel-content>
  </v-expansion-panel>
</template>
<script>
export default {
  props: {
    domain: {
      type: Array,
      default: function() {
        return [];
      },
    },
    challenges: {
      type: Object,
      default: function() {
        return {};
      },
    },
  },
  computed: {
    title() {
      const key = `exoplatform.gamification.gamificationinformation.domain.${this.domain.title}`;
      return this.$te(key) && this.$t(key) || this.domain.title;
    },
    size() {
      return this.domain.challengesSize;
    },
    hasMore() {
      return this.domain.challengesSize > this.domain.challenges.length;
    },
  },
};
</script>

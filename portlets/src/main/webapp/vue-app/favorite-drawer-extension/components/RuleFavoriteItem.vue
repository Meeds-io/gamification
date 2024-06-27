<template>
  <v-list-item :href="ruleUrl">
    <v-list-item-icon
      size="28"
      class="ms-n1 me-3 my-auto">
      <v-avatar
        size="28"
        class="rule-icon border-color grey lighten-2">
        <rule-icon :rule-event="ruleEvent" />
      </v-avatar>
    </v-list-item-icon>
    <v-list-item-content>
      <v-list-item-title class="text-truncate">{{ ruleTitle }}</v-list-item-title>
    </v-list-item-content>
    <v-list-item-action>
      <favorite-button
        :id="ruleId"
        :favorite="isFavorite"
        :space-id="spaceId"
        :top="top"
        :right="right"
        type="rule"
        type-label="rules"
        @removed="removed"
        @remove-error="removeError" />
    </v-list-item-action>
    <engagement-center-rule-extensions />
  </v-list-item>
</template>
<script>
export default {
  props: {
    id: {
      type: String,
      default: () => null,
    },
  },
  data: () => ({ 
    isFavorite: true,
    rule: {},
  }),
  computed: {
    ruleId() {
      return this.rule?.id || this.id;
    },
    spaceId() {
      return this.rule?.spaceId;
    },
    ruleTitle() {
      return this.rule?.title || '';
    },
    ruleEvent() {
      return this.rule?.event?.title;
    },
    ruleUrl() {
      return `${eXo.env.portal.context}/${eXo.env.portal.engagementSiteName}/contributions/actions/${this.ruleId}`;
    },
  },
  created() {
    this.$ruleService.getRuleById(this.ruleId, {
      lang: eXo.env.portal.language,
    }).then(rule => this.rule = rule);
  },
  methods: {
    removed() {
      this.isFavorite = !this.isFavorite;
      this.displayAlert(this.$t('Favorite.tooltip.SuccessfullyDeletedFavorite'));
      this.$emit('removed');
      this.$root.$emit('refresh-favorite-list');
    },
    removeError() {
      this.displayAlert(this.$t('Favorite.tooltip.ErrorDeletingFavorite', 'rule'), 'error');
    },
    displayAlert(message, type) {
      document.dispatchEvent(new CustomEvent('notification-alert', {detail: {
        message,
        type: type || 'success',
      }}));
    },
  },
};
</script>

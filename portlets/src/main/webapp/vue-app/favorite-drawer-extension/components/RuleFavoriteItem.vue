<template>
  <v-list-item
    v-if="rule"
    :href="ruleUrl"
    @keydown.enter="setAsViewed"
    @auxclick="setAsViewed"
    @click="setAsViewed">
    <v-list-item-icon
      :size="avatarSize"
      class="me-3 my-auto">
      <v-card
        :min-width="iconWidth"
        class="d-flex justify-center no-border-radius"
        color="transparent"
        flat>
        <v-avatar
          :size="avatarSize"
          class="rule-icon border-color border-radius-circle grey lighten-2">
          <rule-icon :rule-event="ruleEvent" :size="iconSize" />
        </v-avatar>
      </v-card>
    </v-list-item-icon>
    <v-list-item-content>
      <v-list-item-title class="text-truncate">{{ ruleTitle }}</v-list-item-title>
      <v-list-item-subtitle v-if="expanded" class="d-flex align-center full-width overflow-hidden pt-2px">
        <div class="flex-grow-0 flex-shrink-1 overflow-hidden">
          <rule-favorite-program
            :rule="rule" />
        </div>
        <v-icon class="flex-grow-0 flex-shrink-0 mx-2" size="2">fa-circle</v-icon>
        <v-chip
          color="tertiary"
          class="flex-grow-0 flex-shrink-0 content-box-sizing white--text"
          small>
          <span>+ {{ ruleScore }}</span>
        </v-chip>
        <template v-if="hasRecurrence">
          <v-icon class="flex-grow-0 flex-shrink-0 mx-2" size="2">fa-circle</v-icon>
          <div class="flex-grow-0 flex-shrink-1 overflow-hidden">
            <rule-favorite-recurrence
              :rule="rule"
              class="text-truncate" />
          </div>
        </template>
      </v-list-item-subtitle>
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
    clickCallback: {
      type: Function,
      default: null,
    },
    expanded: {
      type: Boolean,
      default: false,
    },
  },
  data: () => ({ 
    isFavorite: true,
    rule: null,
  }),
  computed: {
    iconWidth() {
      return this.expanded ? 40 : 30;
    },
    ruleId() {
      return this.rule?.id || this.id;
    },
    spaceId() {
      return this.rule?.spaceId;
    },
    ruleTitle() {
      return this.rule?.title && this.$utils.htmlToText(this.rule?.title) || '';
    },
    ruleEvent() {
      return this.rule?.event?.title;
    },
    ruleUrl() {
      return `${eXo.env.portal.context}/${eXo.env.portal.engagementSiteName}/contributions/actions/${this.ruleId}`;
    },
    ruleScore() {
      return this.rule?.score;
    },
    hasRecurrence() {
      return this.rule?.recurrence && this.rule?.recurrence !== 'NONE' || false;
    },
    avatarSize() {
      return this.expanded ? 38 : 28;
    },
    iconSize() {
      return this.expanded ? 22 : 16;
    },
  },
  async created() {
    try {
      this.rule = await this.$ruleService.getRuleById(this.ruleId, {
        lang: eXo.env.portal.language,
      });
    } catch {
      this.$root.$emit('favorite-removed', 'rule', this.id);
    }
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
    setAsViewed(event) {
      if (event.which === 1 || event.which === 2) {
        this.clickCallback('rule', this.id);
      }
    },
  },
};
</script>

<template>
  <favorite-button
    :id="ruleId"
    :favorite="favorite"
    :space-id="spaceId"
    type="rule"
    type-label="rules"
    @removed="removed"
    @remove-error="removeError"
    @added="added"
    @add-error="addError" />
</template>

<script>
export default {
  props: {
    ruleId: {
      type: Object,
      default: null,
    },
    spaceId: {
      type: String,
      default: null,
    },
    favorite: {
      type: Boolean,
      default: false,
    },
  },
  created() {
    document.addEventListener('favorite-removed', this.handleFavoriteRemoved);
    document.addEventListener('favorite-added', this.handleFavoriteAdded);
  },
  beforeDestroy() {
    document.removeEventListener('favorite-removed', this.handleFavoriteRemoved);
    document.removeEventListener('favorite-added', this.handleFavoriteAdded);
  },
  methods: {
    handleFavoriteRemoved(event) {
      if (event?.detail?.type !== 'rule' || event?.detail?.id !== this.ruleId) {
        return;
      }
      this.$emit('update:favorite', false);
    },
    handleFavoriteAdded(event) {
      if (event?.detail?.type !== 'rule' || event?.detail?.id !== this.ruleId) {
        return;
      }
      this.$emit('update:favorite', true);
    },
    removed() {
      this.displayAlert(this.$t('Favorite.tooltip.SuccessfullyDeletedFavorite', {0: this.$t('activity.label')}));
      this.$emit('removed');
    },
    added() {
      this.displayAlert(this.$t('Favorite.tooltip.SuccessfullyAddedAsFavorite', {0: this.$t('activity.label')}));
      this.$emit('added');
    },
    removeError() {
      this.displayAlert(this.$t('Favorite.tooltip.ErrorDeletingFavorite', {0: this.$t('activity.label')}), 'error');
    },
    addError() {
      this.displayAlert(this.$t('Favorite.tooltip.ErrorAddingAsFavorite', {0: this.$t('activity.label')}), 'error');
    },
    displayAlert(message, type) {
      this.$root.$emit('alert-message',  message, type || 'success');
    },
  },
};
</script>
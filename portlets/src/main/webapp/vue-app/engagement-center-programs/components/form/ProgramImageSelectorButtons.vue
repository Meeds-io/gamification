<template>
  <div class="d-flex flex-row justify-end">
    <div class="d-flex position-absolute z-index-one pa-1">
      <div
        v-show="!isDefault"
        class="changeBannerButtonIcon me-1">
        <v-btn
          :title="removeButtonTooltip"
          id="programCoverDeleteButton"
          class="border-color mask-color"
          outlined
          small
          icon
          dark
          @click="$emit('remove')">
          <v-icon size="16">mdi-delete</v-icon>
        </v-btn>
      </div>
      <div class="changeBannerButtonIcon">
        <v-btn
          :title="changeButtonTooltip"
          id="programCoverEditButton"
          class="border-color mask-color"
          outlined
          icon
          small
          dark
          @click="$emit('edit')">
          <v-icon size="16">fas fa-file-image</v-icon>
        </v-btn>
      </div>
    </div>
  </div>
</template>
<script>
export default {
  props: {
    isDefault: {
      type: Boolean,
      default: false,
    },
    imageType: {
      type: String,
      default: null,
    },
  },
  computed: {
    isAvatar() {
      return this.imageType === 'avatar';
    },
    changeButtonTooltip() {
      return this.isAvatar && this.$t('programs.details.tooltip.changeProgramAvatar') || this.$t('programs.details.tooltip.changeProgramCover');
    },
    removeButtonTooltip() {
      return this.isAvatar && this.$t('programs.details.tooltip.removeProgramAvatar') || this.$t('programs.details.tooltip.removeProgramCover');
    },
  },
};
</script>
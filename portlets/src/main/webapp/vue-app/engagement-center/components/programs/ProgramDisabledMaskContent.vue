<template>
  <div class="d-flex flex-column full-width full-height align-center justify-space-between white--text">
    <span></span>
    <v-icon color="white" size="60">fa-slash</v-icon>
    <span class="headline">
      {{ programSubtitle }}
    </span>
    <v-btn
      v-if="canEnableProgram"
      color="primary"
      depressed
      large
      @click="enableProgram">
      {{ $t('programs.details.activateProgram') }}
    </v-btn>
    <span></span>
  </div>
</template>
<script>
export default {
  props: {
    program: {
      type: Object,
      default: null
    },
    isAdministrator: {
      type: Boolean,
      default: false,
    },
  },
  data: () => ({
    loading: false,
  }),
  watch: {
    loading() {
      if (this.loading) {
        document.dispatchEvent(new CustomEvent('displayTopBarLoading'));
      } else {
        document.dispatchEvent(new CustomEvent('hideTopBarLoading'));
      }
    },
  },
  computed: {
    canManageProgram() {
      return this.isAdministrator || this.program?.userInfo?.canEdit;
    },
    isAudienceValid() {
      return this.program.open || this.program.space;
    },
    hasActiveRules() {
      return this.program.activeRulesCount;
    },
    canEnableProgram() {
      return this.canManageProgram && this.hasActiveRules && this.isAudienceValid;
    },
    programSubtitle() {
      if (!this.hasActiveRules) {
        return this.$t('programs.label.programDeactivatedDueToLackOfRules');
      } else if (!this.isAudienceValid) {
        return this.$t('programs.label.programDeactivatedDueToMissingAudience');
      } else {
        return this.$t('programs.label.programDisabledIntroduction');
      }
    },
  },
  methods: {
    enableProgram() {
      this.loading = true;
      return this.$programService.getProgramById(this.program.id)
        .then(program => {
          if (!program.enabled) {
            program.enabled = true;
            return this.$programService.updateProgram(program);
          }
        })
        .then(program => {
          document.dispatchEvent(new CustomEvent('hideTopBarLoading'));
          this.$root.$emit('program-updated', program);
          this.$root.$emit('alert-message', this.$t('programs.label.programEnabledSuccess'), 'success');
        })
        .catch(e => {
          const error = String(e);
          if (this.$te(error)) {
            this.$root.$emit('alert-message', this.$t(error), 'error');
          } else {
            this.$root.$emit('alert-message', this.$t('program.form.errorSavingProgram'), 'error');
          }
        })
        .finally(() => this.loading = false);
    },
  },
};
</script>
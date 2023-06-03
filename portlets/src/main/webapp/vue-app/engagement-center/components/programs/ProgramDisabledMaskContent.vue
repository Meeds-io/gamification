<template>
  <div class="d-flex flex-column full-width full-height align-center justify-space-between white--text">
    <span></span>
    <v-icon color="white" size="60">fa-slash</v-icon>
    <span class="headline">
      {{ $t('programs.label.programDisabledIntroduction') }}
    </span>
    <v-btn
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
        .finally(() => this.loading = false);
    },
  },
};
</script>
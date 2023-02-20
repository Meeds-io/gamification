<template>
  <div>
    <div class="pt-8 d-flex align-center justify-center">
      <v-icon
        class="px-8 primary--text"
        size="30">
        fas fa-award
      </v-icon>
      <v-icon
        class="px-4 px-sm-12 secondary--text"
        size="30">
        fas fa-stream
      </v-icon>
      <v-icon
        class="px-8 primary--text"
        size="30">
        fas fa-trophy
      </v-icon>
    </div>
    <div class="d-flex flex-column justify-center align-center py-2">
      <span class="mx-2 py-3 text-capitalize-first-letter subtitle-1" v-html="noResultsText">
      </span>
    </div>
  </div>
</template>
<script>
export default {
  computed: {
    noResultsText() {
      return this.$t('programs.details.noRuleFound', {
        0: '<a class="primary--text font-weight-bold" onclick="window.dispatchEvent(new CustomEvent(\'reset-keyword\'))">',
        1: '</a>',
      });
    },
  },
  created() {
    window.addEventListener('reset-keyword', () => {
      this.$emit('keyword-changed', null);
    });
  },
};
</script>
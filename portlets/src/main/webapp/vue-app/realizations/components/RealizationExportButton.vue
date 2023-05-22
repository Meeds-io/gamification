<template>
  <v-btn
    :href="link"
    download="achievements.xlsx"
    class="btn btn-primary export">
    <span class="ms-2 d-none d-lg-inline text-none">
      {{ $t("realization.label.export") }}
    </span>
  </v-btn>
</template>
<script>
export default {
  props: {
    link: {
      type: String,
      default: null,
    },
  },
};
</script>
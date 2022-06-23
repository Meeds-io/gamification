<template>
  <div class="border-box-sizing clickable">
    <v-btn class="btn btn-primary export" @click="getReport()">
      <span class="ms-2 d-none d-lg-inline">
        {{ $t("realization.label.export") }}
      </span>
    </v-btn>
  </div>
</template>
<script>
export default {
  props: {
    fromDate: {
      type: Date,
      default: '',
    },
    toDate: {
      type: Date,
      default: '',
    },
  },
  getReport() {
    return this.$realizationsServices.getReport(this.fromDate, this.toDate);
  },
};
</script>
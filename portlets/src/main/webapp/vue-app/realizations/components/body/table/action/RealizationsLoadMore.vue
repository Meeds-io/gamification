<template>
  <v-btn
    v-if="showLoadMoreButton"
    class="btn"
    :loading="loading"
    :disabled="loading"
    @click="getRealizations(true)"
    block>
    <span class="ms-2 d-none d-lg-inline">
      {{ $t("realization.label.loadMore") }}
    </span>
  </v-btn>
</template>
<script>
export default {
  methods: {
    getRealizations(loadMore) {
      this.loading = true;
      const offset = loadMore ? this.realizations.length : 0;
      this.$realizationsServices.getAllRealizations(this.fromDate,this.toDate,offset,this.realizationsPerPage).then(realizations => {
        if (realizations.length >= this.realizationsPerPage) {
          this.showLoadMoreButton = true;
        } else {
          this.showLoadMoreButton = false;
        }
        this.realizations = loadMore && this.realizations.concat(realizations) || realizations;
      }).finally(() => {
        this.loading = false;
      });
    },
  }
};
</script>
 
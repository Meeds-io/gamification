<template>
  <div>
    <div class="d-flex flex-row">
      <realizations-header-left />
    </div>
    <div class="selected-period-menu mt-6 mx-3">
      <realizations-header-righter-left v-model="selectedPeriod" class="mx-2" />
    </div>
  </div>
</template>

<script>
export default {
  data: () => ({
    showMobileFilter: false,
    selectedPeriod: null,

  }),
  computed: {
    canShowMobileFilter() {
      return this.isMobile && this.showMobileFilter;
    },
    isMobile() {
      return this.$vuetify.breakpoint.name === 'xs' || this.$vuetify.breakpoint.name === 'sm';
    },
  },
  watch: {
    selectedPeriod(newValue) {
      if (newValue) {
        this.fromDate = new Date(newValue.min).toISOString() ;
        this.toDate = new Date(newValue.max).toISOString();
        this.getRealizations();
      }
    },
  },
  created() {
    this.$root.$on('show-mobile-filter', data => {
      this.showMobileFilter= data;
    });
  },
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
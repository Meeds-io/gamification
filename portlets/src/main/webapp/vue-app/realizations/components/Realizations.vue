<template>
  <v-app
    class="Realizations border-box-sizing">
    <v-toolbar
      color="transparent"
      flat
      class="pa-4 mb-4">
      <realizations-header />
    </v-toolbar>
    <realisations-body />
    <v-toolbar
      color="transparent"
      flat>
      <realizations-load-more />
    </v-toolbar>
    <edit-realization-drawer
      ref="editRealizationDrawer"
      @realization-updated="realizationUpdated($event)" />
  </v-app>
</template>

<script>
export default {
  data: () => ({
    realizations: [],
    selectedRealizations: {},
    realizationsPerPage: 10,
    loading: true,
    loadMore: false,
    limitReached: false,
    showLoadMoreButton: false,
    showMenu: false,
    dateMenu: false,
    toDate: new Date().toISOString() ,
    fromDate: null ,
    alert: false,
    type: '',
    message: '',
    selectedPeriod: null,
  }),
  methods: {
    closeActionsMenu() {
      this.selectedRealizations = {};
    }
  }
};
</script>
<!--

  This file is part of the Meeds project (https://meeds.io/).
  
  Copyright (C) 2023 Meeds Association contact@meeds.io
  
  This program is free software; you can redistribute it and/or
  modify it under the terms of the GNU Lesser General Public
  License as published by the Free Software Foundation; either
  version 3 of the License, or (at your option) any later version.
  
  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
  Lesser General Public License for more details.
  
  You should have received a copy of the GNU Lesser General Public License
  along with this program; if not, write to the Free Software Foundation,
  Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.

-->
<template>
  <v-list class="pa-0">
    <users-leaderboard-profile-realization-item
      v-for="r in realizations"
      :key="r.id"
      :realization="r" />
  </v-list>
</template>
<script>
export default {
  props: {
    identityId: {
      type: String,
      default: null,
    },
    programId: {
      type: String,
      default: null,
    },
    pageSize: {
      type: Number,
      default: () => 10,
    },
  },
  data: () => ({
    loading: false,
    totalSize: 0,
    sortBy: 'date',
    sortDescending: true,
    realizations: [],
  }),
  computed: {
    hasMore() {
      return this.totalSize > this.realizations.length;
    },
    realizationsFilter() {
      return {
        status: 'ACCEPTED',
        earnerIds: [this.identityId],
        programIds: this.programId && [this.programId],
        allPrograms: true,
        sortBy: this.sortBy,
        sortDescending: this.sortDescending,
      };
    },
  },
  watch: {
    loading() {
      this.$emit('loading', this.loading);
    },
    hasMore() {
      this.$emit('has-more', this.hasMore);
    },
    programId() {
      this.realizations = [];
      this.retrieveRealizations();
    },
  },
  created() {
    this.limit = this.pageSize;
    this.retrieveRealizations();
  },
  methods: {
    retrieveRealizations() {
      this.loading = true;
      return this.$realizationService.getRealizations({
        offset: this.realizations.length,
        limit: this.pageSize,
        returnSize: true,
        ...this.realizationsFilter})
        .then(data => {
          if (data.realizations.length) {
            this.realizations.push(...data.realizations);
          }
          this.totalSize = data?.size || 0;
        })
        .finally(() => this.loading = false);
    },
    loadMore() {
      this.retrieveRealizations();
    },
  },
};
</script>
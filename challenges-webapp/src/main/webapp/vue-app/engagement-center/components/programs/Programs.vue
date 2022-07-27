<!--
This file is part of the Meeds project (https://meeds.io/).
Copyright (C) 2022 Meeds Association
contact@meeds.io
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
  <div
    id="EngagementCenterPrograms"
    class="border-box-sizing"
    role="main"
    flat>
    <engagement-center-programs-list
      :loading="loading"
      :programs="programs"
      class="py-10 mx-4" />
  </div>
</template>

<script>
export default {
  data() {
    return {
      programs: null,
      loading: false,
    };
  },
  watch: {
    loading() {
      if (this.loading) {
        document.dispatchEvent(new CustomEvent('displayTopBarLoading'));
      } else {
        document.dispatchEvent(new CustomEvent('hideTopBarLoading'));
      }
    },
  },
  created() {
    this.retrievePrograms();
  },
  methods: {
    retrievePrograms() {
      this.loading = true;
      this.$challengesServices.getAllDomains()
        .then(programs => this.programs =  programs.slice().filter(program => program.enabled))
        .finally(() => this.loading = false);
    },
  },
};
</script>
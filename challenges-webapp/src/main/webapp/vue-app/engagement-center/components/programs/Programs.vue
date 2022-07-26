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
    <v-toolbar
      color="transparent"
      flat
      class="pa-4">
      <div class="border-box-sizing clickable addChallengeButton">
        <v-btn class="btn btn-primary">
          <v-icon>fas fa-plus</v-icon>
          <span class="mx-2 d-none d-lg-inline">
            {{ $t('programs.button.addProgram') }}
          </span>
        </v-btn>
      </div>
      <v-spacer />
    </v-toolbar>
    <programs-list class="my-10 mx-4" :programs="programs" />
  </div>
</template>

<script>
export default {
  data() {
    return {
      programs: null
    };
  },
  created() {
    this.getAllPrograms();
  },
  methods: {
    getAllPrograms() {
      this.$challengesServices.getAllDomains()
        .then(programs => this.programs =  programs.slice().filter(program => program.enabled));
    },
  },
};
</script>
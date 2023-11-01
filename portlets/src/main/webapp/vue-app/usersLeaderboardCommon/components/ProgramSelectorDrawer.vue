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
  <exo-drawer
    ref="drawer"
    v-model="drawer"
    :loading="loading"
    :go-back-button="goBackButton"
    id="programSelectorDrawer"
    class="program-selector-drawer"
    allow-expand
    right
    @click.stop.prevent="0">
    <template #title>
      {{ $t('gamification.overview.programsFilter.drawer.title') }}
    </template>
    <template v-if="drawer" #content>
      <v-radio-group
        v-model="programId"
        class="px-5 pb-5">
        <v-radio
          v-for="program in programs"
          :key="program.id"
          :label="program.label || program.title"
          :value="program.id" />
      </v-radio-group>
    </template>
    <template #footer>
      <div class="d-flex">
        <v-btn
          class="dark-grey-color px-0 hidden-xs-only"
          text
          outlined
          @click="reset">
          <v-icon size="18" class="icon-default-color me-2">fa-redo</v-icon>
          {{ $t('challenge.button.resetFilter') }}
        </v-btn>
        <v-spacer />
        <v-btn
          class="btn me-2"
          @click="close">
          {{ $t('programs.details.filter.cancel') }}
        </v-btn>
        <v-btn
          :disabled="!modified"
          class="btn btn-primary"
          @click="apply">
          {{ $t('gamification.apply') }}
        </v-btn>
      </div>
    </template>
  </exo-drawer>
</template>
<script>
export default {
  props: {
    value: {
      type: String,
      default: null,
    },
    goBackButton: {
      type: Boolean,
      default: false,
    },
  },
  data: () => ({
    programs: [],
    programId: '0',
    drawer: false,
    loading: false,
  }),
  computed: {
    modified() {
      return this.programId !== this.value;
    },
  },
  created() {
    this.init();
  },
  mounted() {
    document.querySelector('#vuetify-apps').appendChild(this.$el);
  },
  methods: {
    open() {
      this.reset();
      this.$refs.drawer.open();
    },
    close() {
      this.$refs.drawer.close();
    },
    apply() {
      this.$emit('input', this.programId);
      this.close();
    },
    reset() {
      this.programId = this.value;
    },
    init() {
      this.loading = true;
      return this.retrievePrograms()
        .then(() => this.$nextTick())
        .finally(() => {
          this.loading = false;
          this.$root.$applicationLoaded();
        });
    },
    retrievePrograms() {
      return this.$leaderboardService.getPrograms()
        .then(data => {
          const programs = data?.programs || [];
          const defaultProgram = {
            id: '0',
            title: 'All',
            label: this.$t('exoplatform.gamification.leaderboard.domain.all'),
          };
          this.programs = [defaultProgram, ...programs];
        });
    },
  },
};
</script>
<!--
  This file is part of the Meeds project (https://meeds.io/).
  Copyright (C) 2022 Meeds Association contact@meeds.io
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
  <v-app>
    <v-hover v-model="hover">
      <gamification-overview-widget :loading="loading">
        <template #title>
          <div class="d-flex flex-grow-1 flex-shrink-1 full-width align-center position-relative">
            <div
              v-if="programsDisplayed"
              class="widget-text-header text-none text-truncate d-flex align-center">
              {{ $t('gamification.overview.programsOverviewTitle') }}
            </div>
            <div
              :class="{
                'mt-2 me-2': !programsDisplayed,
                'l-0': $vuetify.rtl,
                'r-0': !$vuetify.rtl,
              }"
              class="position-absolute absolute-vertical-center z-index-one">
              <v-tooltip v-if="$root.displayNotPublicallyVisible" top>
                <template #activator="{attrs, on}">
                  <v-icon
                    size="18"
                    color="warning"
                    class="me-2"
                    v-on="on"
                    v-bind="attrs">
                    fa-exclamation-triangle
                  </v-icon>
                </template>
                <span>
                  {{ $t('gamification.publicWidgetHiddenTooltipPart1') }}
                  <br>
                  {{ $t('gamification.publicWidgetHiddenTooltipPart2') }}
                </span>
              </v-tooltip>
              <v-btn
                v-show="programLink"
                :icon="hoverEdit"
                :small="hoverEdit"
                height="auto"
                min-width="auto"
                class="pa-0"
                text
                @click="$refs.listDrawer.open()">
                <v-icon
                  v-if="hoverEdit"
                  size="18"
                  color="primary">
                  fa-external-link-alt
                </v-icon>
                <span v-else class="primary--text text-none">{{ $t('rules.seeAll') }}</span>
              </v-btn>
              <v-fab-transition hide-on-leave>
                <v-btn
                  v-show="hoverEdit"
                  :title="$t('gamification.programs.overviewSettings.editTooltip')"
                  :class="!programsDisplayed && 'mt-n4 me-n2 z-index-one'"
                  small
                  icon
                  @click="$root.$emit('programs-overview-settings')">
                  <v-icon size="18">fa-cog</v-icon>
                </v-btn>
              </v-fab-transition>
            </div>
          </div>
        </template>
        <template #default>
          <div
            v-if="programsDisplayed"
            class="flex-grow-1 flex-shrink-1 overflow-hidden">
            <gamification-overview-program-item
              v-for="program in programsToDisplay" 
              :key="program.id"
              :program="program"
              class="flex-grow-1" />
          </div>
          <div v-else-if="!loading" class="d-flex flex-column align-center justify-center full-width full-height">
            <v-icon color="tertiary" size="60">fa-puzzle-piece</v-icon>
            <span class="mt-5">{{ $t('gamification.overview.programs') }}</span>
          </div>
        </template>
      </gamification-overview-widget>
    </v-hover>
    <div v-if="programsDisplayed">
      <gamification-program-list-drawer
        ref="listDrawer" />
      <gamification-program-detail-drawer
        :administrators="administrators" />
      <engagement-center-rule-extensions />
    </div>
    <gamification-programs-overview-settings-drawer
      v-if="$root.canEdit" />
  </v-app>
</template>
<script>
export default {
  data: () => ({
    programs: [],
    administrators: null,
    hover: false,
    loading: true,
  }),
  computed: {
    programsDisplayed() {
      return !!this.programs?.length;
    },
    programsToDisplay() {
      return this.programs.slice(0, this.$root.limit);
    },
    programURL() {
      return `${eXo.env.portal.context}/${eXo.env.portal.engagementSiteName}/contributions/programs`;
    },
    programLink() {
      return this.programsDisplayed && this.programURL || null;
    },
    hoverEdit() {
      return this.hover && this.$root.canEdit;
    },
    limit() {
      return this.$root.limit || 4;
    },
    sortBy() {
      return this.$root.programsSortBy || 'modifiedDate';
    },
  },
  watch: {
    limit() {
      if (!this.loading) {
        this.retrievePrograms();
      }
    },
    sortBy() {
      if (!this.loading) {
        this.retrievePrograms();
      }
    },
  },
  created() {
    this.retrievePrograms();
  },
  methods: {
    retrievePrograms() {
      this.loading = true;
      return this.$programService.getPrograms({
        sortBy: this.sortBy || 'modifiedDate',
        sortDescending: this.sortBy !== 'title',
        limit: this.limit || 4,
        type: 'ALL',
        status: 'ENABLED',
        lang: eXo.env.portal.language,
        expand: 'countActiveRules,administrators'
      })
        .then((data) => {
          this.administrators = data?.administrators || [];
          this.programs = data?.programs || [];
        })
        .finally(() => this.loading = false);
    },
  },
};
</script>
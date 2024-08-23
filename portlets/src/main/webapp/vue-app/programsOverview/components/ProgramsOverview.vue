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
    <gamification-overview-widget :loading="loading">
      <template #title>
        <div v-if="programsDisplayed" class="d-flex flex-grow-1 flex-shrink-1 overflow-hidden full-width">
          <div class="widget-text-header text-none text-truncate">
            {{ $t('gamification.overview.programsOverviewTitle') }}
          </div>
          <v-spacer />
          <v-btn
            height="auto"
            min-width="auto"
            class="pa-0"
            text
            @click="$refs.listDrawer.open()">
            <span class="primary--text text-none">{{ $t('rules.seeAll') }}</span>
          </v-btn>
        </div>
      </template>
      <div
        v-if="programsDisplayed"
        ref="content"
        class="flex-grow-1 flex-shrink-1 overflow-hidden">
        <gamification-overview-program-item
          v-for="program in programsToDisplay" 
          :key="program.id"
          :program="program"
          class="flex-grow-1" />
      </div>
      <div v-else-if="!loading" class="d-flex flex-column align-center justify-center full-width full-height">
        <v-icon color="tertiary" size="54">fa-puzzle-piece</v-icon>
        <span class="mt-7">{{ $t('gamification.overview.programs') }}</span>
      </div>
    </gamification-overview-widget>
    <div v-if="programsDisplayed">
      <gamification-program-list-drawer
        ref="listDrawer" />
      <gamification-program-detail-drawer
        :administrators="administrators" />
      <engagement-center-rule-extensions />
    </div>
  </v-app>
</template>
<script>
export default {
  data: () => ({
    programs: [],
    administrators: null,
    loading: true,
    // Dynamic Height Resizer attributes
    applicationResizeObserver: null,
    resizeObserver: null,
    maxItems: 10,
    itemHeight: 64,
    itemsParentHeight: 0,
    mountedComponent: false,
    fixedHeight: false,
    dynamicSection: false,
    hideContent: false,
  }),
  computed: {
    initialized() {
      return this.mountedComponent && !this.loading;
    },
    programsDisplayed() {
      return !!this.programs?.length;
    },
    limitToDisplay() {
      if (this.dynamicSection && !this.fixedHeight) {
        return this.maxItems;
      } else {
        return this.programsDisplayed && !this.hideContent && parseInt(this.itemsParentHeight / this.itemHeight) || 0;
      }
    },
    programsToDisplay() {
      return this.programs.slice(0, this.limitToDisplay);
    },
    programURL() {
      return `${eXo.env.portal.context}/${eXo.env.portal.engagementSiteName}/contributions/programs`;
    },
    programLink() {
      return this.programsDisplayed && this.programURL || null;
    },
  },
  watch: {
    programsDisplayed() {
      if (this.programsDisplayed) {
        this.installObserver();
      } else {
        this.uninstallObserver();
      }
    },
    initialized() {
      this.installObserver();
    },
    limitToDisplay() {
      if (this.limitToDisplay > this.maxItems
          && (this.loading || this.programs?.length >= this.maxItems)) {
        this.maxItems = this.limitToDisplay;
        this.retrievePrograms();
      }
    },
  },
  created() {
    this.retrievePrograms();
  },
  mounted() {
    this.mountedComponent = true;
    this.init();
  },
  beforeDestroy () {
    this.uninstallAllObservers();
  },
  methods: {
    init() {
      if (this.$el) {
        this.dynamicSection = !!this.$el.closest('.flex-cell');
        this.refreshContentHeight();
      }
    },
    retrievePrograms() {
      this.loading = true;
      return this.$programService.getPrograms({
        limit: this.maxItems,
        type: 'ALL',
        status: 'ENABLED',
        sortBy: 'modifiedDate',
        sortDescending: true,
        lang: eXo.env.portal.language,
        expand: 'countActiveRules,administrators'
      })
        .then((data) => {
          this.administrators = data?.administrators || [];
          this.programs = data?.programs || [];
        })
        .finally(() => this.loading = false);
    },
    installObserver() {
      this.refreshContentHeight();
      if (this.initialized) {
        if (!this.resizeObserver && this.$refs.content) {
          this.resizeObserver = new ResizeObserver(this.refreshContentHeight);
          this.resizeObserver.observe(this.$refs.content);
        }
        if (!this.applicationResizeObserver && this.$el.closest('.layout-application')) {
          this.applicationResizeObserver = new ResizeObserver(this.refreshApplicationContentHeight);
          this.applicationResizeObserver.observe(this.$el.closest('.layout-application'));
        }
      }
    },
    uninstallObserver() {
      if (this.resizeObserver) {
        this.resizeObserver?.disconnect?.();
        this.resizeObserver = null;
      }
    },
    uninstallAllObservers() {
      if (this.resizeObserver) {
        this.resizeObserver?.disconnect?.();
        this.resizeObserver = null;
      }
      if (this.applicationResizeObserver) {
        this.applicationResizeObserver?.disconnect?.();
        this.applicationResizeObserver = null;
      }
    },
    refreshApplicationContentHeight() {
      this.hideContent = true;
      this.$nextTick()
        .then(() => {
          window.setTimeout(() => {
            this.refreshContentHeight();
            this.hideContent = false;
          }, 50);
        });
    },
    refreshContentHeight() {
      if (this.$el?.closest?.('.layout-application')) {
        this.fixedHeight = !!this.$el.closest('.layout-application')?.style?.getPropertyValue?.('--appHeight');
      }
      this.$nextTick().then(() => this.itemsParentHeight = this.$refs.content?.offsetHeight || 0);
    },
  },
};
</script>
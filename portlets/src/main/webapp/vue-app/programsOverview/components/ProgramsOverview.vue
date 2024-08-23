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
      <template v-if="programsDisplayed || loading" #title>
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
    <gamification-program-list-drawer
      v-if="programsDisplayed"
      ref="listDrawer" />
    <gamification-program-detail-drawer
      v-if="programsDisplayed"
      :administrators="administrators" />
    <engagement-center-rule-extensions
      v-if="programsDisplayed" />
  </v-app>
</template>
<script>
export default {
  data: () => ({
    programs: [],
    administrators: null,
    loading: true,
    programsDisplayed: false,
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
    programURL() {
      return `${eXo.env.portal.context}/${eXo.env.portal.engagementSiteName}/contributions/programs`;
    },
    programLink() {
      return this.programsDisplayed && this.programURL || null;
    },
    programsToDisplay() {
      return this.programs.slice(0, this.limitToDisplay);
    },
    limitToDisplay() {
      if (this.dynamicSection && !this.fixedHeight) {
        return this.maxItems;
      } else {
        return this.programsDisplayed && !this.hideContent && parseInt(this.itemsParentHeight / this.itemHeight) || 0;
      }
    },
    initialized() {
      return this.mountedComponent && !this.loading;
    },
  },
  watch: {
    programsDisplayed() {
      if (!this.programsDisplayed) {
        this.uninstallObserver();
      }
    },
    initialized() {
      if (this.programsDisplayed) {
        this.installObserver();
      }
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
  },
  beforeDestroy () {
    this.uninstallObserver();
  },
  methods: {
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
          this.programsDisplayed = data.size > 0;
        })
        .finally(() => this.loading = false);
    },
    installObserver() {
      if (this.$refs.content
          && this.initialized
          && (!this.resizeObserver || !this.itemsParentHeight)) {
        this.refreshContentHeight();
        this.resizeObserver = new ResizeObserver(this.refreshContentHeight).observe(this.$refs.content);
        this.applicationResizeObserver = new ResizeObserver(this.refreshApplicationContentHeight).observe(this.$el.closest('.layout-application'));
      }
    },
    uninstallObserver() {
      if (this.resizeObserver) {
        this.resizeObserver?.disconnect?.();
        this.applicationResizeObserver?.disconnect?.();
      }
    },
    refreshApplicationContentHeight() {
      this.hideContent = true;
      this.$nextTick()
        .then(() => {
          this.refreshContentHeight();
          this.hideContent = false;
        });
    },
    refreshContentHeight() {
      this.dynamicSection = !!this.$el.closest('.flex-cell');
      this.fixedHeight = !!this.$el.closest('.layout-application').style.getPropertyValue('--appHeight');
      this.itemsParentHeight = this.$refs.content?.offsetHeight || 0;
    },
  },
};
</script>
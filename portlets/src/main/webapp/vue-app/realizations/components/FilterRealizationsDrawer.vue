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
  <exo-drawer
    ref="RealizationsFilterDrawer"
    class="RealizationsFilterDrawer"
    :right="!$vuetify.rtl"
    @closed="close"
    eager>
    <v-progress-circular
      v-if="loading"
      indeterminate
      size="32" />
    <template slot="title">
      <span class="pb-2"> {{ $t('realization.label.search.filtersAchievements') }} </span>
    </template>
    <template slot="content">
      <div class="VuetifyApp">
        <template>
          <v-tabs v-model="tab">
            <v-tab class="text-capitalize">{{ $t('realization.label.programLabel') }}</v-tab>
          </v-tabs>
          <v-tabs-items v-model="tab">
            <v-tab-item>
              <realizations-filter-program-list v-on="$listeners" @empty-list="verifySelection" />
            </v-tab-item>
            <v-tab-item />
          </v-tabs-items>
        </template>
      </div>
    </template>
    <template slot="footer">
      <div class="VuetifyApp flex d-flex">
        <v-btn
          class="btn"
          @click="reset">
          <v-icon x-small class="pr-1">fas fa-redo</v-icon>
          <template>
            {{ $t('exoplatform.gamification.gamificationinformation.domain.reset') }}
          </template>
        </v-btn>
        <v-spacer />
        <div class="d-btn">
          <v-btn
            class="btn me-2"
            @click="cancel">
            <template>
              {{ $t('exoplatform.gamification.gamificationinformation.domain.cancel') }}
            </template>
          </v-btn>
          <v-btn
            :disabled="disabled"
            class="btn btn-primary"
            @click="confirm">
            <template>
              {{ $t('exoplatform.gamification.gamificationinformation.domain.confirm') }}
            </template>
          </v-btn>
        </div>
      </div>
    </template>
  </exo-drawer>
</template>

<script>
export default {
  data() {
    return {
      tab: null,
      disabled: false,
    };
  },
  created() {    
    this.$root.$on('realization-open-filter-drawer', this.open);
    this.$root.$on('program-load-more', this.loadMore);
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
  methods: {
    open() {
      this.$refs.RealizationsFilterDrawer.open();
    },
    cancel() {
      this.$refs.RealizationsFilterDrawer.close();
    },
    confirm() {
      this.$emit('selectionConfirmed');
      this.cancel();
    },
    reset() {
      this.$root.$emit('reset-selection');
    },
    loadMore() {
      return this.retrievePrograms(true);
    },
    verifySelection(value) {
      this.disabled = !value;
    },
  }
};
</script>
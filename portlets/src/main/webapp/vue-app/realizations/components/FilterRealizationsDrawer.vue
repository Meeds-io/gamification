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
      <form
        ref="realizationFilter"
        id="realizationFilter">
        <v-card-text v-if="isAdministrator">
          <span class="subtitle-1">{{ $t('realization.label.filter.grantee') }}</span>
          <v-flex class="user-suggester text-truncate">
            <exo-identity-suggester
              ref="granteeAttendeeAutoComplete"
              name="granteeAttendee"
              v-model="granteeAttendee"
              :search-options="searchOptions"
              :labels="granteeSuggesterLabels"
              include-users />
            <div v-if="grantees" class="identitySuggester no-border mt-0">
              <grantee-attendee-item
                v-for="grantee in grantees"
                :key="grantee.identity.id"
                :attendee="grantee"
                @remove-attendee="removeGranteeAttendee" />
            </div>
          </v-flex>
        </v-card-text>
        <v-card-text>
          <span class="subtitle-1">{{ $t('realization.label.filter.program') }}</span>
          <program-suggester
            ref="programAttendeeAutoComplete"
            v-model="programAttendee"
            :labels="programSuggesterLabels"
            :include-deleted="IncludeDisabledPrograms"
            :include-disabled="IncludeDisabledPrograms" />
          <div v-if="programs" class="identitySuggester no-border mt-0">
            <program-attendee-item
              v-for="program in programs"
              :key="program.id"
              :program="program"
              @remove-attendee="removeProgramAttendee" />
          </div>
          <v-checkbox
            v-model="IncludeDisabledPrograms"
            hide-details
            @click="changeSelection">
            <template #label>
              <span class="text--color subtitle-2 font-weight-normal">
                {{ $t('realization.label.filter.program.includeDisabledOrRemoved') }}
              </span>
            </template>
          </v-checkbox>
        </v-card-text>
      </form>
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
  props: {
    isAdministrator: {
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {
      tab: null,
      disabled: false,
      granteeAttendee: null,
      grantees: [],
      granteesIds: [],
      programAttendee: null,
      programs: [],
      searchOptions: {
        currentUser: '',
      },
      IncludeDisabledPrograms: false
    };
  },
  computed: {
    granteeSuggesterLabels() {
      return {
        searchPlaceholder: this.$t('realization.label.filter.grantee.searchPlaceholder'),
        placeholder: this.$t('realization.label.filter.grantee.placeholder'),
        noDataLabel: this.$t('realization.label.filter.grantee.noDataLabel'),
      };
    },
    programSuggesterLabels() {
      return {
        searchPlaceholder: this.$t('realization.label.filter.program.searchPlaceholder'),
        placeholder: this.$t('realization.label.filter.program.placeholder'),
        noDataLabel: this.$t('realization.label.filter.program.noDataLabel'),
      };
    },
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
    granteeAttendee() {
      if (!this.granteeAttendee) {
        this.$nextTick(this.$refs.granteeAttendeeAutoComplete.$refs.selectAutoComplete.deleteCurrentItem);
        return;
      }
      if (!this.grantees) {
        this.grantees = [];
      }

      const found = this.grantees?.find(grantee => {
        return grantee.identity.remoteId === this.granteeAttendee.remoteId
            && grantee.identity.providerId === this.granteeAttendee.providerId;
      });
      if (!found) {
        this.grantees.push({
          identity: this.granteeAttendee,
        });
      }
      this.granteeAttendee = null;
    },
    programAttendee() {
      if (!this.programAttendee) {
        this.$nextTick(this.$refs.programAttendeeAutoComplete.$refs.selectAutoComplete.deleteCurrentItem);
        return;
      }
      if (!this.programs) {
        this.programs = [];
      }

      const found = this.programs?.find(program => {
        return program.id === this.programAttendee.id;
      });
      if (!found) {
        this.programs.push(this.programAttendee);
      }
      this.programAttendee = null;
    },
  },
  methods: {
    open() {
      this.$refs.RealizationsFilterDrawer.open();
    },
    cancel() {
      this.$refs.RealizationsFilterDrawer.close();
      this.reset();
    },
    confirm() {
      this.$emit('selectionConfirmed', this.programs, this.grantees);
      this.$refs.RealizationsFilterDrawer.close();
    },
    reset() {
      this.programs = [];
      this.grantees = [];
      this.IncludeDisabledPrograms = false;
    },
    removeGranteeAttendee(attendee) {
      const index = this.grantees.findIndex(addedAttendee => {
        return attendee.identity.remoteId === addedAttendee.identity.remoteId
            && attendee.identity.providerId === addedAttendee.identity.providerId;
      });
      if (index >= 0) {
        this.grantees.splice(index, 1);
        this.granteesIds.splice(index, 1);
      }
    },
    removeProgramAttendee(attendee) {
      const index = this.programs.findIndex(addedAttendee => {
        return attendee.id === addedAttendee.id;
      });
      if (index >= 0) {
        this.programs.splice(index, 1);
      }
    },
    changeSelection() {
      this.programs = [];
    }
  }
};
</script>
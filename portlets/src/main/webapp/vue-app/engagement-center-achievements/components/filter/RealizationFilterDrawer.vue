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
    ref="drawer"
    v-model="drawer"
    :right="!$vuetify.rtl"
    class="RealizationsFilterDrawer"
    eager
    @closed="close">
    <template slot="title">
      <span class="pb-2"> {{ $t('realization.label.search.filtersAchievements') }} </span>
    </template>
    <template v-if="drawer" slot="content">
      <form
        ref="realizationFilter"
        id="realizationFilter">
        <v-card-text>
          <span class="subtitle-1">{{ $t('realization.label.filter.program') }}</span>
          <engagement-center-realizations-program-suggester
            ref="programAutoComplete"
            v-model="program"
            :labels="programSuggesterLabels"
            :include-deleted="includeDisabledPrograms"
            :include-disabled="includeDisabledPrograms"
            :only-owned="administrationMode"
            :excluded-ids="programIds" />
          <div v-if="programs && programs.length" class="identitySuggester no-border mt-0">
            <engagement-center-realizations-program-item
              v-for="program in programs"
              :key="program.id"
              :program="program"
              @remove-attendee="removeProgram" />
          </div>
          <v-checkbox
            v-model="includeDisabledPrograms"
            hide-details
            @click="changeSelection">
            <template #label>
              <span class="text--color subtitle-2 font-weight-normal">
                {{ $t('realization.label.filter.program.includeDisabledOrRemoved') }}
              </span>
            </template>
          </v-checkbox>
        </v-card-text>
        <v-card-text>
          <span class="subtitle-1">{{ $t('realization.label.filter.action') }}</span>
          <rule-suggester
            ref="ruleAutoComplete"
            v-model="rule"
            :labels="ruleSuggesterLabels"
            :excluded-ids="ruleIds"
            :include-deleted="includeDisabledRules" />
          <div v-if="rules && rules.length" class="identitySuggester no-border mt-0">
            <engagement-center-realizations-rule-item
              v-for="(rule, index) in rules"
              :key="rule.id"
              :rule="rule"
              @remove="rules.splice(index, 1)" />
          </div>
          <v-checkbox
            v-model="includeDisabledRules"
            :disabled="includeDisabledPrograms"
            hide-details
            @click="changeSelection">
            <template #label>
              <span class="text--color subtitle-2 font-weight-normal">
                {{ $t('realization.label.filter.program.includeDisabledOrRemovedActions') }}
              </span>
            </template>
          </v-checkbox>
        </v-card-text>
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
            <div v-if="grantees && grantees.length" class="identitySuggester no-border mt-0">
              <engagement-center-realizations-grantee-attendee-item
                v-for="grantee in grantees"
                :key="grantee.identity.id"
                :attendee="grantee"
                @remove-attendee="removeGranteeAttendee" />
            </div>
          </v-flex>
        </v-card-text>
      </form>
    </template>
    <template slot="footer">
      <div class="VuetifyApp flex d-flex">
        <v-btn
          class="dark-grey-color px-0 hiddent-xs-only"
          text
          outlined
          @click="resetAndApply">
          <v-icon size="18" class="icon-default-color me-2">fa-redo</v-icon>
          {{ $t('challenge.button.resetFilter') }}
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
            @click="confirm()">
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
    administrationMode: {
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {
      tab: null,
      drawer: false,
      disabled: false,
      granteeAttendee: null,
      grantees: [],
      granteesIds: [],
      program: null,
      programs: [],
      rule: null,
      rules: [],
      searchOptions: {
        currentUser: '',
      },
      includeDisabledPrograms: false,
      includeDisabledRules: false,
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
    ruleSuggesterLabels() {
      return {
        searchPlaceholder: this.$t('realization.label.filter.action.searchPlaceholder'),
        placeholder: this.$t('realization.label.filter.action.placeholder'),
        noDataLabel: this.$t('realization.label.filter.action.noDataLabel'),
      };
    },
    programSuggesterLabels() {
      return {
        searchPlaceholder: this.$t('realization.label.filter.program.searchPlaceholder'),
        placeholder: this.$t('realization.label.filter.program.placeholder'),
        noDataLabel: this.$t('realization.label.filter.program.noDataLabel'),
      };
    },
    ruleIds() {
      return this.rules.map(r => r.id);
    },
    programIds() {
      return this.programs.map(r => r.id);
    },
  },
  watch: {
    granteeAttendee() {
      this.addSelectedGrantee(this.granteeAttendee);
    },
    rule() {
      this.addSelectedRule(this.rule);
    },
    program() {
      this.addSelectedProgram(this.program);
    },
    includeDisabledPrograms() {
      if (this.includeDisabledPrograms) {
        this.includeDisabledRules = true;
      } else {
        this.includeDisabledRules = false;
      }
    },
  },
  created() {
    this.$root.$on('realization-open-filter-drawer', this.open);
    this.$root.$on('program-load-more', this.loadMore);
    this.$root.$on('reset-filter-values', this.resetAndApply);
    this.$root.$on('realization-filter-program-add', this.programAdd);
    this.$root.$on('realization-filter-program-remove', this.programRemove);
    this.$root.$on('realization-filter-action-add', this.ruleAdd);
    this.$root.$on('realization-filter-action-remove', this.ruleRemove);
    this.$root.$on('realization-filter-earner-add', this.earnerAdd);
    this.$root.$on('realization-filter-earner-remove', this.earnerRemove);
  },
  methods: {
    open() {
      this.$refs.drawer.open();
    },
    clear() {
      this.$refs?.programAutoComplete?.clear?.();
      this.programs = [];
    },
    cancel() {
      this.$refs.drawer.close();
      this.reset();
    },
    confirm(differ) {
      window.setTimeout(() => {
        this.$emit('selectionConfirmed', this.programs, this.rules, this.grantees);
        this.$refs.drawer.close();
      }, differ && 200 || 10);
    },
    reset() {
      this.programs = [];
      this.rules = [];
      this.grantees = [];
      this.program = null;
      this.rule = null;
      this.granteeAttendee = null;
      this.includeDisabledPrograms = false;
    },
    resetAndApply() {
      this.reset();
      this.$nextTick().then(() => this.confirm());
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
    removeProgram(program) {
      const index = this.programs.findIndex(p => program.id === p.id);
      if (index >= 0) {
        this.programs.splice(index, 1);
      }
    },
    addSelectedProgram(program) {
      if (!program) {
        if (this.$refs?.programAutoComplete) {
          this.$nextTick(this.$refs.programAutoComplete.$refs.selectAutoComplete.deleteCurrentItem);
        }
        return;
      } else if (!this.programs?.length) {
        this.programs = [program];
      } else if (!this.programs?.find(p => p.id === program.id)) {
        this.programs.push(program);
      }
      this.program = null;
    },
    addSelectedRule(rule) {
      if (!rule) {
        if (this.$refs?.ruleAutoComplete) {
          this.$nextTick(this.$refs.ruleAutoComplete.$refs.selectAutoComplete.deleteCurrentItem);
        }
        return;
      } else if (!this.rules?.length) {
        this.rules = [rule];
      } else if (!this.rules?.find(r => r.id === rule.id)) {
        this.rules.push(rule);
      }
      this.rule = null;
    },
    addSelectedGrantee(grantee) {
      if (!grantee) {
        if (this.$refs?.granteeAttendeeAutoComplete) {
          this.$nextTick(this.$refs.granteeAttendeeAutoComplete.$refs.selectAutoComplete.deleteCurrentItem);
        }
        return;
      }
      if (!this.grantees) {
        this.grantees = [];
      }

      const found = this.grantees?.find(g => {
        return g.identity.remoteId === grantee.remoteId
            && g.identity.providerId === grantee.providerId;
      });
      if (!found) {
        this.grantees.push({
          identity: grantee,
        });
      }
      this.granteeAttendee = null;
    },
    programAdd(program) {
      this.addSelectedProgram(program);
      this.$nextTick(this.confirm);
    },
    programRemove(programId) {
      const index = this.programs.findIndex(p => programId === p.id);
      if (index >= 0) {
        this.programs.splice(index, 1);
      }
      this.$nextTick(this.confirm);
    },
    ruleAdd(rule) {
      this.addSelectedRule(rule);
      this.$nextTick(this.confirm);
    },
    ruleRemove(ruleId) {
      const index = this.rules.findIndex(r => ruleId === r.id);
      if (index >= 0) {
        this.rules.splice(index, 1);
      }
      this.$nextTick(this.confirm);
    },
    earnerAdd(earner) {
      this.addSelectedGrantee({
        id: `organization:${earner.remoteId}`,
        providerId: 'organization',
        remoteId: earner.remoteId,
        identityId: earner.id,
        profile: {
          avatarUrl: earner.avatarUrl,
          fullName: earner.fullName,
        },
      });
      this.$nextTick(this.confirm);
    },
    earnerRemove(earnerId) {
      const index = this.grantees.findIndex(e => earnerId === e?.identity?.identityId);
      if (index >= 0) {
        this.grantees.splice(index, 1);
      }
      this.$nextTick(this.confirm);
    },
    changeSelection() {
      this.programs = [];
    }
  }
};
</script>
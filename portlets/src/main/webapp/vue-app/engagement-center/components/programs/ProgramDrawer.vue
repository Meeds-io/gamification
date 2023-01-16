<!--
This file is part of the Meeds project (https://meeds.io/).
Copyright (C) 2022 Meeds Association
contact@meeds.io
This program is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 3 of the License, or (at your option) any later version.
This program is distributed in the hope that it will be useful,5
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.
You should have received a copy of the GNU Lesser General Public License
along with this program; if not, write to the Free Software Foundation,
Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
-->
<template>
  <exo-drawer
    ref="programDrawer"
    class="EngagementCenterDrawer"
    v-model="drawer"
    :right="!$vuetify.rtl"
    eager>
    <template slot="title">
      <span class="pb-2"> {{ drawerTitle }} </span>
    </template>
    <template v-if="drawer" #content>
      <v-card-text v-if="program">
        <div
          v-if="warning"
          class="alert alert-error v-content mb-4">
          <i class="uiIconError"></i>
          {{ warning }}
        </div>
        <v-form
          id="EngagementCenterProgramDrawerForm"
          ref="form"
          v-model="isValidForm"
          @submit="
            $event.preventDefault();
            $event.stopPropagation();
          ">
          <v-textarea
            id="EngagementCenterProgramDrawerTitleTextArea"
            v-model="program.title"
            :rules="[rules.length]"
            :placeholder="$t('programs.label.enterProgramTitle') "
            name="programTitle"
            class="pl-0 pt-0 EngagementCenter-title"
            auto-grow
            rows="1"
            row-height="13"
            required
            autofocus />
          <v-divider class="my-2" />
          <div class="py-2">
            <span class="subtitle-1"> {{ $t('programs.label.programCover') }}</span>
            <engagement-center-image-selector
              id="engagementCenterProgramDrawerImageSelector"
              ref="programCover"
              v-model="program.coverUrl"
              class="mb-2"
              @updated="addCover" />
            <span class="caption mt-2 text-light-color">
              <v-icon class="mb-1" small>fas fa-info-circle</v-icon>
              {{ $t('programs.label.coverWarning') }}
            </span>
          </div>
          <div class="py-2">
            <engagement-center-description-editor
              id="engagementCenterProgramDrawerDescriptionEditor"
              ref="programDescription"
              v-model="program.description"
              :label="$t('programs.label.describeProgram')"
              @validity-updated="validDescription = $event" />
          </div>
          <div
            v-if="showBudget"
            class="mt-4">
            <span class="subtitle-1">{{ $t('programs.label.budget') }}</span>
            <template>
              <v-list-item
                flat
                dense
                class="px-0">
                <v-list-item-content class="f me-2 points">
                  <v-text-field
                    id="EngagementCenterProgramDrawerBudget"
                    v-model="program.budget"
                    :rules="[rules.value]"
                    class="pt-2 points mt-n3"
                    type="number"
                    outlined
                    required />
                </v-list-item-content>
                <v-list-item-content class="flex flex-grow-0 flex-shrink-0 me-2">
                  <span class="caption mx-2 text-light-color"> {{ $t('programs.label.budgetPerWeek') }} </span>
                </v-list-item-content>
              </v-list-item>
            </template>
          </div>
          <span class="subtitle-1">{{ $t('programs.label.audienceSpace') }}</span>
          <exo-identity-suggester
            id="EngagementCenterProgramDrawerSpaceSuggester"
            ref="programSpaceSuggester"
            name="programSpaceAutocomplete"
            v-model="audience"
            :items="audience && [audience] || []"
            :search-options="audienceSearchOptions"
            :labels="spaceSuggesterLabels"
            :width="220"
            include-spaces />
          <div class="mt-4">
            <span class="subtitle-1"> {{ $t('programs.label.programOwners') }}</span>
            <div class="d-flex">
              <v-icon class="me-1" small>fas fa-info-circle</v-icon>
              <span class="caption text-light-color"> {{ $t('programs.label.accessPermission') }}</span>
            </div>
          </div>
          <v-row v-if="audience" class="pa-0 mx-0 mt-4 mb-2">
            <v-col class="pa-0 d-flex align-center">
              <span> {{ $t('programs.label.spaceHostsOf') }}</span>
            </v-col>
            <v-col class="pa-0 d-flex align-center">
              <exo-space-avatar
                v-if="spaceId"
                :space-id="spaceId"
                extra-class=" ms-auto d-flex align-center primary--text" />
            </v-col>
          </v-row>
          <engagement-center-program-owner-assignment
            v-if="audience"
            id="engagementCenterProgramOwnerAssignee"
            ref="programAssignment"
            v-model="programOwners"
            :audience="audience"
            class="mt-4"
            multiple />
          <div v-if="isExternalOwner" class="error--text mt-1">
            {{ $t('programs.label.externalOwner.warning') }}
          </div>
          <div class="mt-4">
            <div class="d-flex">
              <span class="subtitle-1 me-auto">{{ $t('programs.label.status') }}</span>
              <v-switch
                id="engagementCenterProgramDrawerSwitch"
                v-model="program.enabled"
                class="my-0 ms-0 me-n1" />
            </div>
            <div class="caption text-light-color">
              <v-icon class="me-1" small>fas fa-info-circle</v-icon>
              {{ $t('programs.label.programStatusSubtitle') }}
            </div>
          </div>
        </v-form>
      </v-card-text>
    </template>
    <template slot="footer">
      <div class="d-flex mr-2">
        <v-spacer />
        <v-btn
          :disabled="loading"
          class="btn mx-1"
          @click="close">
          {{ $t('engagementCenter.button.cancel') }}
        </v-btn>
        <v-btn
          :disabled="disabledSave"
          :loading="loading"
          class="btn btn-primary"
          @click="save">
          {{ buttonName }}
        </v-btn>
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
    }
  },
  data: () => ({
    program: null,
    programOwners: [],
    audience: null,
    warning: null,
    isValidForm: true,
    drawer: false,
    showMenu: false,
    loading: false,
    showBudget: false,
    validDescription: false,
    programSpace: null,
  }),
  computed: {
    audienceSearchOptions() {
      return this.isAdministrator && {
        filterType: 'all',
      } || {
        filterType: 'manager',
      };
    },
    drawerTitle() {
      return this.program?.id ? this.$t('programs.button.editProgram') : this.$t('programs.button.addProgram');
    },
    disabledSave() {
      return !this.isValidForm
          || !this.audienceId
          || !this.validDescription
          || this.isExternalOwner;
    },
    buttonName() {
      return this.program && this.program.id && this.$t('engagementCenter.button.save') || this.$t('engagementCenter.button.create');
    },
    spaceId() {
      return this.audience?.spaceId;
    },
    audienceId() {
      return Number(this.audience?.spaceId) || 0;
    },
    spaceSuggesterLabels() {
      return {
        searchPlaceholder: this.$t('programs.label.spaces.noDataLabel'),
        placeholder: this.$t('programs.label.spaces.placeholder'),
      };
    },
    rules() {
      return {
        length: (v) => (v && v.length < 50) || this.$t('programs.label.TitleLengthExceed'),
        value: (v) => (v >= 0 && v <= 9999) || this.$t('challenges.label.pointsValidation')
      };
    },
    isExternalOwner() {
      return this.programOwners.some(owner => owner.external);
    },
  },
  watch: {
    loading() {
      if (this.loading) {
        this.$refs.programDrawer.startLoading();
      } else {
        this.$refs.programDrawer.endLoading();
      }
    },
    audience() {
      if (this.drawer && !this.audience?.spaceId) {
        this.program.space = null;
        this.programOwners = [];
        document.dispatchEvent(new CustomEvent('audienceChanged'));
      }
    },
  },
  created() {
    this.$root.$on('edit-program-details', this.open);
    this.$root.$on('open-program-drawer', this.open);
    $(document).mousedown(() => {
      if (this.showMenu) {
        window.setTimeout(() => {
          this.showMenu = false;
        }, 200);
      }
    });
  },
  methods: {
    open(program) {
      this.program = program && JSON.parse(JSON.stringify(program)) || {
        budget: 0,
        enabled: true,
        owners: []
      };
      this.programOwners = this.program?.owners?.filter(owner => owner.domainOwner).slice() || [];

      if (this.program?.id && this.program?.space) {
        const space = this.program?.space;
        this.audience = {
          id: `space:${space.prettyName}`,
          profile: {
            avatarUrl: space.avatarUrl,
            fullName: space.displayName,
          },
          providerId: 'space',
          remoteId: space.prettyName,
          spaceId: this.program.space.id,
          displayName: this.program.space.displayName,
          notToChange: true,
        };
      } else {
        this.audience = null;
      }
      this.$refs.programDrawer.open();
      this.$nextTick().then(() => this.$refs.programDescription.initCKEditor());
    },
    close() {
      this.$refs.programDrawer.close();
      this.program = {};
    },
    addDescription(value) {
      if (value) {
        this.$set(this.program, 'description', value);
      }
    },
    addCover(value) {
      if (value) {
        this.$set(this.program, 'coverUploadId', value);
      }
    },
    save() {
      if (this.disabledSave) {
        return;
      }
      this.program.type = 'MANUAL';
      this.loading = true;
      if (this.program?.id) {
        const programToSave = JSON.parse(JSON.stringify(this.program));
        programToSave.owners = this.programOwners?.map(owner => owner.id).filter(id => !!id);
        programToSave.audienceId = this.audienceId;
        this.$programsServices.updateProgram(programToSave)
          .then((program) => {
            this.close();
            this.$root.$emit('program-updated', program);
            this.$engagementCenterUtils.displayAlert(this.$t('programs.programUpdateSuccess'));
          })
          .catch(() => {
            this.$engagementCenterUtils.displayAlert(this.$t('programs.programUpdateError'), 'error');
          })
          .finally(() => this.loading = false);
      } else {
        const programToSave = JSON.parse(JSON.stringify(this.program));
        programToSave.owners = this.programOwners?.map(owner => owner.id).filter(id => !!id);
        programToSave.audienceId = this.audienceId;
        this.$programsServices.saveProgram(programToSave)
          .then((program) => {
            this.$root.$emit('program-added', program);
            this.close();
            this.$engagementCenterUtils.displayAlert(this.$t('programs.programCreateSuccess'));
          })
          .catch(() => {
            this.$engagementCenterUtils.displayAlert(this.$t('programs.programCreateError'), 'error');
          })
          .finally(() => this.loading = false);
      }
    },
  }
};
</script>
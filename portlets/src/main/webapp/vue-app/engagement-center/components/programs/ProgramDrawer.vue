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
            <span class="text-caption mt-2 text-light-color">
              <v-icon class="mb-1" small>fas fa-info-circle</v-icon>
              {{ $t('programs.label.coverWarning') }}</span>
          </div>
          <div class="py-2">
            <engagement-center-description-editor
              id="engagementCenterProgramDrawerDescriptionEditor"
              ref="programDescription"
              v-model="program.description"
              :label="$t('programs.label.describeProgram')"
              :max-length="maxDescriptionLength"
              @addDescription="addDescription($event)"
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
                  <span class="text-caption mx-2 text-light-color"> {{ $t('programs.label.budgetPerWeek') }} </span>
                </v-list-item-content>
              </v-list-item>
            </template>
          </div>
          <span class="subtitle-1">{{ $t('challenges.label.audienceSpace') }} *</span>
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
          <div class="my-2">
            <span class="subtitle-1"> {{ $t('programs.label.programOwners') }} *</span>
            <engagement-center-assignment
              id="engagementCenterProgramDrawerAssignee"
              ref="programAssignment"
              v-model="programOwners"
              :audience="audience"
              class="my-2"
              only-manager
              multiple />
          </div>
          <div class="my-2 mt-4">
            <span class="subtitle-1"> {{ $t('programs.label.status') }}</span>
            <v-list-item class="px-0 mt-n6 mx-auto">
              <v-list-item-content class="pt-1 mt-6">
                <span class="subtitle-1 text-light-color"> {{ $t('programs.label.enabled') }}</span>
              </v-list-item-content>
              <v-list-item-content class="flex flex-grow-0 flex-shrink-0 overflow-visible me-7">
                <v-switch
                  id="engagementCenterProgramDrawerSwitch"
                  v-model="program.enabled" />
              </v-list-item-content>
            </v-list-item>
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
          @click="$programDrawer.close()">
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
    rules: {
      length: (v) => (v && v.length < 50) || this.$t('programs.label.TitleLengthExceed'),
      value: (v) => (v >= 0 && v <= 9999) || this.$t('challenges.label.pointsValidation')
    },
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
        || !this.programOwners.length
        || !this.validDescription;
    },
    buttonName() {
      return this.program && this.program.id && this.$t('engagementCenter.button.save') || this.$t('engagementCenter.button.create');
    },
    audienceId() {
      return Number(this.audience?.spaceId) || 0;
    },
    spaceSuggesterLabels() {
      return {
        searchPlaceholder: this.$t('challenges.spaces.noDataLabel'),
        placeholder: this.$t('challenges.spaces.placeholder'),
      };
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
      if (this.drawer) {
        if (this.audience?.spaceId) {
          this.$spaceService.getSpaceMembers(null, 0, 0, null,'manager', this.audience.spaceId)
            .then(managers => {
              const listManagers = managers.users.map(manager => ({
                id: manager.id,
                remoteId: manager.username,
                fullName: manager.fullname,
                avatarUrl: manager.avatar,
              }));
              const data = {
                managers: listManagers,
                space: this.audience,
              };
              this.programOwners = listManagers;
              document.dispatchEvent(new CustomEvent('audienceChanged', {detail: data}));
            });
        } else {
          this.program.space = null;
          this.programOwners = [];
          document.dispatchEvent(new CustomEvent('audienceChanged'));
        }
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
      this.programOwners = this.program?.owners?.slice() || [];

      if (this.program?.id && this.program?.space) {
        const space = this.program?.space ;
        this.audience = {
          id: `space:${space.prettyName}` ,
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
            this.$root.$emit('program-added', program);
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
            this.$engagementCenterUtils.displayAlert(this.$t('programs.programCreateError'),'error');
          })
          .finally(() => this.loading = false);
      }
    },
  }
};
</script>
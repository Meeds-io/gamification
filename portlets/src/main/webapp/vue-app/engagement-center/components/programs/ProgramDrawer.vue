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
    eager
    @closed="close">
    <template slot="title">
      <span class="pb-2"> {{ drawerTitle }} </span>
    </template>
    <template slot="content">
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
          v-model="isValidTitle"
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
              @addDescription="addDescription($event)" />
          </div>
          <div 
            v-if="showBudget"
            class="mt-4">
            <span class="subtitle-1"> {{ $t('programs.label.budget') }}</span>
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
          <span class="subtitle-1"> {{ $t('challenges.label.audienceSpace') }} *</span>
          <exo-identity-suggester
              id="EngagementCenterProgramDrawerSpaceSuggester"
              ref="programSpaceSuggester"
              name="programSpaceAutocomplete"
              v-model="audience"
              :labels="spaceSuggesterLabels"
              :width="220"
              include-spaces />
          <div class="my-2">
            <span class="subtitle-1"> {{ $t('programs.label.programOwners') }} *</span>
            <engagement-center-assignment
              id="engagementCenterProgramDrawerAssignee"
              ref="programAssignment"
              class="my-2"
              :audience="audience"
              v-model="program.owners"
              multiple
              @remove-user="removeOwner"
              @add-item="addOwner" />
          </div>
          <div class="my-2 mt-4">
            <template>
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
            </template>
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
          :disabled="!disabledSave"
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
  computed: {
    drawerTitle() {
      return this.program && this.program.id ? this.$t('programs.button.editProgram') : this.$t('programs.button.addProgram');
    },
    disabledSave() {
      return this.program && this.program.title && this.program.owners.length > 0 && this.program.description && this.isValidTitle && this.validDescription && !this.disabledUpdate;
    },
    buttonName() {
      return this.program && this.program.id && this.$t('engagementCenter.button.save') || this.$t('engagementCenter.button.create');
    },
    spaceSuggesterLabels() {
      return {
        searchPlaceholder: this.$t('challenges.spaces.noDataLabel'),
        placeholder: this.$t('challenges.spaces.placeholder'),
      };
    },
  },
  data() {
    return {
      rules: {
        length: (v) => (v && v.length < 50) || this.$t('programs.label.TitleLengthExceed'),
        value: (v) => (v >= 0 && v <= 9999) || this.$t('challenges.label.pointsValidation')
      },
      program: null,
      audience: '',
      warning: null,
      validDescription: true,
      isValidTitle: true,
      drawer: false,
      showMenu: false,
      loading: false,
      showBudget: false,
    };
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
      if (this.audience && this.audience.id && !this.audience.notToChange) {
        this.$spaceService.getSpaceMembers(null, 0, 0, null,'manager', this.audience.spaceId).then(managers => {
          const listManagers = [];
          managers.users.forEach(manager => {
            const newManager= {
              id: manager.id,
              remoteId: manager.username,
              fullName: manager.fullname,
              avatarUrl: manager.avatar,
            };
            this.$set(this.program.owners,this.program.owners.length, newManager.id);
            listManagers.push(newManager);
          });
          this.$set(this.program,'audienceId', this.audience.spaceId);
          const data = {
            managers: listManagers,
            space: this.audience,
          };
          document.dispatchEvent(new CustomEvent('audienceChanged', {detail: data}));
        });
      } else if (this.audience && this.audience.id && this.audience.notToChange){
        this.audience.notToChange = false ;
        return;
      } else {
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
      this.$refs.programDrawer.open();
      this.$nextTick()
        .then(() => {
          if (this.program?.id) {
            this.$refs.programAssignment.setUp(this.program?.owners);
            if (this.program?.space) {
              const space = this.program?.space ;
              const NewAudience = {
                id: `space:${ space.displayName }` ,
                profile: {
                  avatarUrl: space.avatarUrl,
                  fullName: space.displayName,
                },
                providerId: 'space',
                remoteId: space.displayName,
                spaceId: this.program.space.id,
                notToChange: true,
              };
              this.$refs.programSpaceSuggester.emitSelectedValue(NewAudience);
            }
          }
          this.$refs.programDescription.initCKEditor();
        });
    },
    close() {
      this.program = {};
      this.$refs.programDescription.destroyCKEditor();
      this.$refs.programCover.reset();
      this.$refs.programAssignment.reset();
      this.$refs.programDrawer.close();
      this.$refs.programSpaceSuggester.emitSelectedValue( {});
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
    removeOwner(id) {
      const index = this.program.owners && this.program.owners.findIndex((owner) => {
        if (owner?.id) {
          return owner.id === id;
        } else {
          return owner === id;
        }
      });
      if (index >= 0) {
        this.$delete(this.program.owners, index, 1);
      }
    },
    addOwner(id) {
      const index = this.program.owners && this.program.owners.findIndex((owner) => {
        if (owner?.id) {
          return owner.id === id;
        } else {
          return owner === id;
        }
      });
      if (index < 0) {
        this.$set(this.program.owners, this.program.owners.length, id);
      }
    },
    save() {
      this.program.type = 'MANUAL';
      this.loading = true;
      if ( this.program?.id) {
        if ( this.program.owners && this.program.owners[0].id){
          this.program.owners = this.program.owners.map(owner => owner.id);
        }
        if ( this.program.audience){
          this.program.owners = this.program.owners.map(owner => owner.id);
        }
        this.$programsServices.updateProgram(this.program)
          .then((program) =>{
            this.$root.$emit('program-added', program);
            this.$engagementCenterUtils.displayAlert(this.$t('programs.programUpdateSuccess'));
            this.close();
            this.program = {};
          })
          .catch(() => {
            this.$engagementCenterUtils.displayAlert(this.$t('programs.programUpdateError'), 'error');
          })
          .finally(() => this.loading = false);
      } else {
        this.$programsServices.saveProgram(this.program)
          .then((program) =>{
            this.$root.$emit('program-added', program);
            this.$engagementCenterUtils.displayAlert(this.$t('programs.programCreateSuccess'));
            this.close();
            this.program = {};
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
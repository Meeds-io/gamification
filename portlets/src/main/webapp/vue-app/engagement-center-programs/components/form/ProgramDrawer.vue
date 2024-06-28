<!--

  This file is part of the Meeds project (https://meeds.io/).

  Copyright (C) 2023 Meeds Association contact@meeds.io

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
    v-model="drawer"
    :right="!$vuetify.rtl"
    :confirm-close="programChanged"
    :confirm-close-labels="confirmCloseLabels"
    :allow-expand="!$root.isMobile"
    class="EngagementCenterDrawer overflow-initial"
    eager
    @opened="stepper = 1"
    @closed="closed"
    @expand-updated="expanded = $event">
    <template #title>
      {{ drawerTitle }}
    </template>
    <template v-if="showDrawerContent" #content>
      <v-form
        id="EngagementCenterProgramDrawerForm"
        ref="form"
        v-model="isValidForm"
        autocomplete="off"
        class="pa-0"
        @submit="
          $event.preventDefault();
          $event.stopPropagation();
        ">
        <v-stepper
          v-model="stepper"
          :class="expanded && 'flex-row' || 'flex-column'"
          class="ma-0 py-0 d-flex"
          vertical
          flat>
          <div :class="expanded && 'col-6'" class="flex-grow-1 flex-shrink-0">
            <v-stepper-step
              step="1"
              class="ma-0 px-6">
              <span class="text-header">
                {{ $t('programs.label.introduceYourProgram') }}
              </span>
            </v-stepper-step>
            <v-slide-y-transition>
              <div v-show="expanded || stepper === 1" class="px-6">
                <div class="pt-2">
                  {{ $t('programs.label.programCover') }}
                  <engagement-center-program-image-selector
                    id="engagementCenterProgramDrawerImageSelector"
                    ref="programCover"
                    v-model="program.coverUrl"
                    :no-expand="!expanded"
                    image-type="cover"
                    @updated="addCover"
                    @deleted="removeCover" />
                </div>
                <div class="pt-4">
                  {{ $t('programs.label.programAvatar') }}
                  <engagement-center-program-image-selector
                    id="engagementCenterProgramDrawerImageSelector"
                    ref="programAvatar"
                    v-model="program.avatarUrl"
                    image-type="avatar"
                    @updated="addAvatar"
                    @deleted="removeAvatar" />
                </div>
                <div class="mt-4">
                  <span class="d-flex align-center">
                    {{ $t('program.form.programColorTitle') }}
                  </span>
                  <engagement-center-program-color-picker
                    id="engagementCenterProgramColorPicker"
                    v-model="program.color"
                    :program="program"
                    :allow-expand="expanded" />
                </div>
                <translation-text-field
                  ref="programTitle"
                  id="programTitle"
                  v-model="programTitleTranslations"
                  :rules="[rules.length]"
                  :field-value.sync="programTitle"
                  :placeholder="$t('programs.label.enterProgramTitle')"
                  :maxlength="maxTitleLength"
                  :object-id="programId"
                  :no-expand-icon="!expanded"
                  object-type="program"
                  field-name="title"
                  drawer-title="program.form.translateTitle"
                  name="programTitle"
                  class="width-auto flex-grow-1 pt-4"
                  back-icon
                  autofocus
                  required
                  @initialized="setFormInitialized">
                  <template #title>
                    {{ $t('programs.label.nameYourProgram') }}
                  </template>
                </translation-text-field>
                <translation-text-field
                  ref="programDescriptionTranslation"
                  v-model="programDescriptionTranslations"
                  :field-value.sync="programDescription"
                  :object-id="programId"
                  :maxlength="maxDescriptionLength"
                  :no-expand-icon="!expanded"
                  object-type="program"
                  field-name="description"
                  drawer-title="program.form.translateDescription"
                  class="width-auto flex-grow-1 pt-4"
                  back-icon
                  rich-editor
                  @initialized="setFormInitialized">
                  <template #title>
                    {{ $t('programs.label.describeProgram') }}
                  </template>
                  <rich-editor
                    id="programDescription"
                    ref="programDescriptionEditor"
                    v-model="programDescription"
                    :placeholder="$t('programs.placeholder.describeProgram')"
                    :max-length="maxDescriptionLength"
                    :tag-enabled="false"
                    ck-editor-type="program"
                    @validity-updated="validDescription = $event" />
                </translation-text-field>
              </div>
            </v-slide-y-transition>
          </div>
          <div :class="expanded && 'col-6'" class="flex-grow-1 flex-shrink-0">
            <v-stepper-step
              step="2"
              class="ma-0 px-6">
              <span class="text-header">
                {{ $t('programs.label.introduceProgramDetais') }}
              </span>
            </v-stepper-step>
            <v-slide-y-transition>
              <div v-show="expanded || stepper > 1" class="px-6">
                <div>
                  <span class="d-flex align-center">
                    {{ $t('programs.label.audienceSpace') }}
                  </span>
                  <div class="mt-4">
                    <div class="d-flex align-center">
                      <span class="me-auto">{{ $t('programs.label.programAudience.all') }}</span>
                      <v-switch
                        id="engagementCenterProgramPublicSwitch"
                        v-model="program.open"
                        class="ms-0 me-n1 mt-0 mb-n2 pt-0" />
                    </div>
                    <div class="text-subtitle">
                      {{ $t('programs.subtitle.programAudience.all') }}
                    </div>
                  </div>
                </div>
                <div v-if="!openProgram" class="d-flex flex-column justify-center mt-4">
                  <div>
                    {{ $t('programs.label.spaceMembersOf') }}
                  </div>
                  <exo-identity-suggester
                    id="EngagementCenterProgramDrawerSpaceSuggester"
                    ref="programSpaceSuggester"
                    name="programSpaceAutocomplete"
                    v-model="audience"
                    :items="audience && [audience] || []"
                    :search-options="audienceSearchOptions"
                    :labels="suggesterLabels"
                    :width="220"
                    sugester-class="ma-0 no-box-shadow border-color"
                    include-spaces />
                  <div v-if="openSpace !== null" class="text-subtitle mt-2">
                    {{ openSpace && $t('programs.label.openSpaceSubtitle') || $t('programs.label.restrictedSpaceSubtitle') }}
                  </div>
                </div>
                <div class="mt-4">
                  <span class="d-flex align-center">
                    {{ $t('programs.label.programOwners') }}
                  </span>
                  <div class="d-flex align-center">
                    <span class="text-subtitle"> {{ $t('programs.label.accessPermission') }}</span>
                  </div>
                </div>
                <v-row v-if="audience" class="mt-4 mx-0">
                  <v-col class="pa-0 d-flex align-center">
                    <span> {{ $t('programs.label.spaceHostsOf') }}</span>
                  </v-col>
                  <v-col class="pa-0 d-flex align-center">
                    <exo-space-avatar
                      v-if="spaceId"
                      :space-id="spaceId"
                      extra-class="ms-auto d-flex align-center primary--text" />
                  </v-col>
                </v-row>
                <v-chip v-else-if="openProgram" class="mt-4">
                  {{ $t('programs.label.anyRewardAdmin') }}
                </v-chip>
                <engagement-center-program-owner-assignment
                  v-if="audience || openProgram"
                  id="engagementCenterProgramOwnerAssignee"
                  ref="programAssignment"
                  v-model="programOwners"
                  :audience="audience"
                  multiple />
                <div v-if="isExternalOwner" class="error--text mt-1">
                  {{ $t('programs.label.externalOwner.warning') }}
                </div>
                <div v-if="programId && activeRulesCount" class="mt-4">
                  <div class="d-flex align-center">
                    <span class="me-auto">{{ $t('programs.label.status') }}</span>
                    <v-switch
                      id="engagementCenterProgramDrawerSwitch"
                      v-model="program.enabled"
                      class="my-0 ms-0 me-n1" />
                  </div>
                  <div class="text-subtitle">
                    {{ $t('programs.label.programStatusSubtitle') }}
                  </div>
                </div>
              </div>
            </v-slide-y-transition>
          </div>
        </v-stepper>
      </v-form>
    </template>
    <template #footer>
      <div class="d-flex mr-2">
        <v-spacer />
        <v-btn
          v-if="stepper === 2 && !expanded"
          :disabled="loading"
          class="btn me-2"
          @click.prevent.stop="stepper--">
          {{ $t('rule.form.label.button.back') }}
        </v-btn>
        <v-btn
          v-else
          :disabled="loading"
          class="btn me-2"
          @click="close">
          {{ $t('rule.form.label.button.cancel') }}
        </v-btn>
        <v-btn
          v-if="stepper === 1"
          :disabled="disableNextButton"
          class="btn btn-primary"
          @click.prevent.stop="stepper++">
          {{ $t('rule.form.label.button.next') }}
        </v-btn>
        <v-btn
          v-else
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
    },
    maxUploadSize: {
      type: Number,
      default: () => 2,
    },
  },
  data: () => ({
    stepper: 0,
    program: null,
    originalProgram: null,
    programOwners: [],
    maxTitleLength: 50,
    maxDescriptionLength: 1300,
    programTitle: null,
    programDescription: null,
    programTitleTranslations: {},
    programDescriptionTranslations: {},
    originalProgramTitleTranslations: {},
    originalProgramDescriptionTranslations: {},
    audience: null,
    isValidForm: true,
    drawer: false,
    initiliazing: false,
    showMenu: false,
    loading: false,
    validDescription: false,
    programSpace: null,
    expanded: false,
    imageType: 'banner',
    deleteCover: false,
    defaultCover: false,
    deleteAvatar: false,
    defaultAvatar: false,
    openSpace: null,
  }),
  computed: {
    showDrawerContent() {
      return this.drawer && !this.initiliazing && !!this.program;
    },
    audienceSearchOptions() {
      return this.isAdministrator && {
        filterType: 'all',
      } || {
        filterType: 'manager',
      };
    },
    programId() {
      return this.program?.id;
    },
    activeRulesCount() {
      return this.program?.activeRulesCount;
    },
    openProgram() {
      return this.program?.open;
    },
    drawerTitle() {
      return this.program?.id ? this.$t('programs.button.editProgram') : this.$t('programs.button.addProgram');
    },
    disabledSave() {
      return this.disableNextButton
          || (!this.spaceId && !this.openProgram)
          || !this.programChanged
          || this.isExternalOwner;
    },
    disableNextButton() {
      return !this.isValidForm || !this.validDescription;
    },
    buttonName() {
      return this.program?.id && this.$t('engagementCenter.button.save') || this.$t('engagementCenter.button.create');
    },
    spaceId() {
      return Number(this.audience?.spaceId) || 0;
    },
    suggesterLabels() {
      return {
        searchPlaceholder: this.$t('programs.label.spaces.noDataLabel'),
        placeholder: this.$t('programs.label.spaces.placeholder'),
      };
    },
    rules() {
      return {
        length: (v) => (v && v.length < this.maxTitleLength) || this.$t('programs.label.TitleLengthExceed'),
        value: (v) => (v >= 0 && v <= 9999) || this.$t('challenges.label.pointsValidation')
      };
    },
    isExternalOwner() {
      return this.programOwners.some(owner => owner.external);
    },
    programToSave() {
      return this.computeProgramModel(
        this.program,
        this.spaceId,
        this.programOwners?.map(owner => owner.id).filter(id => !!id)
      );
    },
    programChanged() {
      if (this.deleteCover || this.deleteAvatar) {
        return true;
      } else if (!this.originalProgram || !this.program) {
        return false;
      } else {
        return JSON.stringify({
          title: this.programTitleTranslations,
          description: this.programDescriptionTranslations,
          color: this.programToSave.color,
          coverUploadId: this.programToSave.coverUploadId,
          avatarUploadId: this.programToSave.avatarUploadId,
          spaceId: this.programToSave.spaceId || 0,
          ownerIds: this.programToSave.ownerIds || null,
          open: this.programToSave.open || false,
          enabled: this.program.enabled || false,
        }) !== JSON.stringify({
          title: this.originalProgramTitleTranslations,
          description: this.originalProgramDescriptionTranslations,
          color: this.originalProgram.color,
          coverUploadId: this.originalProgram.coverUploadId,
          avatarUploadId: this.originalProgram.avatarUploadId,
          spaceId: this.originalProgram.spaceId || 0,
          ownerIds: this.originalProgram.ownerIds || null,
          open: this.originalProgram.open || false,
          enabled: this.originalProgram.enabled || false,
        });
      }
    },
    confirmCloseLabels() {
      return {
        title: this.program?.id && this.$t('program.confirmCloseModificationTitle') || this.$t('program.confirmCloseCreationTitle'),
        message: this.program?.id && this.$t('program.confirmCloseModificationMessage') || this.$t('program.confirmCloseCreationMessage'),
        ok: this.$t('confirm.yes'),
        cancel: this.$t('confirm.no'),
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
    openProgram() {
      if (this.openProgram) {
        this.audience = null;
      }
    },
    initiliazing() {
      if (this.initiliazing) {
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
    programDescription() {
      if (this.$refs.programDescriptionTranslation) {
        this.$refs.programDescriptionTranslation.setValue(this.programDescription);
      }
    },
    expanded() {
      if (this.expanded) {
        this.stepper = 2;
      } else {
        this.stepper = 1;
      }
    },
    saving() {
      if (this.saving) {
        this.$refs.programDrawer.startLoading();
      } else {
        this.$refs.programDrawer.endLoading();
      }
    },
    drawer() {
      if (!this.drawer && window.location.hash === '#create') {
        window.location.hash = '';
      }
    },
    spaceId() {
      this.openSpace = null;
      if (this.spaceId) {
        this.$spaceService.getSpaceById(this.spaceId)
          .then(space => this.openSpace = (space?.subscription === 'open' && space?.visibility !== 'hidden'));
      }
    },
  },
  created() {
    this.$root.$on('program-form-open', this.open);
    $(document).mousedown(() => {
      if (this.showMenu) {
        window.setTimeout(() => {
          this.showMenu = false;
        }, 200);
      }
    });
  },
  mounted() {
    if (window.location.hash === '#create') {
      this.open();
    }
  },
  methods: {
    open(program, freshInstance) {
      if (program && !freshInstance) {
        this.initiliazing = true;
        this.$refs.programDrawer.open();
        return this.$programService.getProgramById(program.id, {
          expand: 'countActiveRules',
        })
          .then(freshProgram => this.open(freshProgram, true));
      }
      this.program = program && JSON.parse(JSON.stringify(program)) || {
        title: null,
        description: null,
        budget: 0,
        open: false,
        enabled: false,
        owners: []
      };
      this.programOwners = this.program?.owners?.slice() || [];
      this.programTitle = this.program?.title || '';
      this.programTitleTranslations = {};
      this.programDescription = this.program?.description || '';
      this.programDescriptionTranslations = {};
      this.deleteCover = false;
      this.deleteAvatar = false;
      this.defaultCover = !this.program?.id || !this.program?.coverUrl || this.program.coverUrl.includes('default');
      this.defaultAvatar = !this.program?.id || !this.program?.avatarUrl || this.program.avatarUrl.includes('default');

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

      this.originalProgram = this.computeProgramModel(
        this.program,
        this.spaceId,
        this.programOwners?.map(owner => owner.id).filter(id => !!id)
      );
      this.initiliazing = false;
      this.$refs.programDrawer.open();
    },
    close() {
      this.$refs.programDescriptionEditor?.destroyCKEditor();
      this.$refs.programDrawer.close();
    },
    addDescription(value) {
      if (value) {
        this.$set(this.program, 'description', value);
      }
    },
    addCover(value) {
      this.deleteCover = false;
      this.$set(this.program, 'coverUploadId', value);
    },
    addAvatar(value) {
      this.deleteAvatar = false;
      this.$set(this.program, 'avatarUploadId', value);
    },
    removeCover() {
      this.deleteCover = !this.defaultCover;
      this.$set(this.program, 'coverUploadId', null);
    },
    removeAvatar() {
      this.deleteAvatar = !this.defaultAvatar;
      this.$set(this.program, 'avatarUploadId', null);
    },
    save() {
      if (this.disabledSave) {
        return;
      }
      this.loading = true;
      if (this.program?.id) {
        this.$programService.updateProgram(this.programToSave)
          .then(() => this.$translationService.saveTranslations('program', this.program.id, 'title', this.programTitleTranslations))
          .then(() => this.$translationService.saveTranslations('program', this.program.id, 'description', this.programDescriptionTranslations))
          .then(() => {
            if (this.deleteCover) {
              return this.$programService.deleteProgramCover(this.program.id)
                .finally(() => this.deleteCover = false);
            }
          })
          .then(() => {
            if (this.deleteAvatar) {
              return this.$programService.deleteProgramAvatar(this.program.id)
                .finally(() => this.deleteAvatar = false);
            }
          })
          .then(() => this.$programService.getProgramById(this.program.id, {
            expand: 'countActiveRules',
          }))
          .then((program) => {
            this.originalProgram = null;
            this.loading = false;
            this.$root.$emit('program-updated', program);
            this.$root.$emit('alert-message', this.$t('programs.programUpdateSuccess'), 'success');
            return this.$nextTick();
          })
          .then(() => this.close())
          .catch(e => {
            const error = String(e);
            if (this.$te(error)) {
              this.$root.$emit('alert-message', this.$t(error), 'error');
            } else {
              this.$root.$emit('alert-message', this.$t('programs.programUpdateError'), 'error');
            }
          })
          .finally(() => this.loading = false);
      } else {
        this.$programService.createProgram(this.programToSave)
          .then((program) => {
            this.$root.$emit('program-added', program);
            this.originalProgram = program;
            return this.$translationService.saveTranslations('program', this.originalProgram.id, 'title', this.programTitleTranslations);
          })
          .then(() => this.$translationService.saveTranslations('program', this.originalProgram.id, 'description', this.programDescriptionTranslations))
          .then(() => {
            this.$root.$emit('alert-message-html', `
                <div class="text-start">
                  <div>${ this.$t('programs.programCreateSuccess') }</div>
                  <div>${ this.$t('programs.programCreateSuccessPart2') }</div>
                </div>
              `, 'success');
            this.loading = false; // To Keep to be able to close drawer
            this.originalProgram = null;
            return this.$nextTick();
          })
          .then(() => this.close())
          .catch(e => {
            const error = String(e);
            if (this.$te(error)) {
              this.$root.$emit('alert-message', this.$t(error), 'error');
            } else {
              this.$root.$emit('alert-message', this.$t('programs.programCreateError'), 'error');
            }
          })
          .finally(() => this.loading = false);
      }
    },
    computeProgramModel(program, spaceId, ownerIds) {
      return {
        id: program?.id,
        title: this.programTitle,
        description: this.programDescription,
        color: program?.color,
        coverUploadId: program?.coverUploadId,
        avatarUploadId: program?.avatarUploadId,
        spaceId: spaceId || program?.spaceId || 0,
        ownerIds: ownerIds || program?.ownerIds || null,
        open: program?.open || false,
        enabled: program?.enabled || false,
      };
    },
    setFormInitialized() {
      if (this.originalProgram) {
        this.originalProgram.title = this.programTitle;
        this.originalProgram.description = this.programDescription;
        this.originalProgramTitleTranslations = this.programTitleTranslations && JSON.parse(JSON.stringify(this.programTitleTranslations));
        this.originalProgramDescriptionTranslations = this.programDescriptionTranslations && JSON.parse(JSON.stringify(this.programDescriptionTranslations));
      }
    },
  }
};
</script>
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
  <exo-drawer
    id="EngagementCenterChallengeDrawer"
    ref="challengeDrawer"
    v-model="drawer"
    class="EngagementCenterDrawer"
    :right="!$vuetify.rtl"
    eager
    @closed="close">
    <template #title>
      <span class="pb-2"> {{ drawerTitle }} </span>
    </template>
    <template v-if="drawer" #content>
      <v-card-text v-if="challenge">
        <v-form
          id="EngagementCenterChallengeDrawerForm"
          ref="form"
          v-model="isValidForm"
          @submit="
            $event.preventDefault();
            $event.stopPropagation();
          ">
          <v-textarea
            id="EngagementCenterChallengeDrawerTitleTextArea"
            v-model="challenge.title"
            :placeholder="$t('challenges.label.enterChallengeTitle') "
            :rules="[rules.length]"
            name="challengeTitle"
            class="pl-0 pt-0 EngagementCenter-title"
            auto-grow
            rows="1"
            row-height="13"
            required
            autofocus />
          <v-divider class="my-2" />

          <span class="subtitle-1"> {{ $t('challenges.label.program') }} *</span>
          <program-suggester
            id="EngagementCenterChallengeDrawerProgramSuggester"
            ref="challengeProgram"
            v-model="challenge.program"
            :labels="programSuggesterLabels"
            :only-owned="!isAdministrator" />

          <div class="mt-4">
            <span class="subtitle-1"> {{ $t('challenges.label.ChallengeDates') }} *</span>
            <challenge-date-picker
              id="EngagementCenterChallengeDrawerDatePicker"
              ref="challengeDatePicker"
              :challenge="challenge"
              class="challengeDates my-2"
              @startDateChanged="updateChallengeStartDate($event)"
              @endDateChanged="updateChallengeEndDate($event)" />
          </div>
          <div class="mt-4">
            <span class="subtitle-1"> {{ $t('challenges.label.reward') }}</span>
            <v-text-field
              id="EngagementCenterChallengeDrawerPoints"
              v-model="challenge.points"
              :label="$t('challenges.label.points')"
              :rules="[rules.value]"
              :placeholder="$t('challenges.label.points')"
              class="pt-2 points"
              type="number"
              outlined
              required />
          </div>
          <div class="challengeDescription py-4 my-2">
            <engagement-center-description-editor
              id="EngagementCenterChallengeDrawerDescriptionEditor"
              v-if="drawer"
              ref="challengeDescription"
              v-model="challenge.description"
              :label="$t('challenges.label.describeYourChallenge')"
              @validity-updated="isValidDescription = $event"
              @addDescription="addDescription($event)" />
          </div>
          <div class="mt-4">
            <span class="subtitle-1"> {{ $t('challenges.label.status') }}</span>
            <div class="d-flex flex-row px-4">
              <label class="subtitle-1 text-light-color">{{ $t('challenges.label.enabled') }}</label>
              <v-switch
                v-if="drawer"
                id="engagementCenterChallengeDrawerSwitch"
                v-model="challenge.enabled"
                class="mt-0 ms-4" />
            </div>
          </div>
        </v-form>
      </v-card-text>
    </template>
    <template slot="footer">
      <div class="d-flex mr-2">
        <v-spacer />
        <v-btn
          id="EngagementCenterChallengeDrawerCancelButton"
          class="btn mx-1"
          @click="close">
          {{ $t('engagementCenter.button.cancel') }}
        </v-btn>
        <v-btn
          id="EngagementCenterChallengeDrawerSaveButton"
          :disabled="disabledSave"
          class="btn btn-primary"
          @click="saveChallenge">
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
  data() {
    return {
      rules: {
        length: (v) => (v && v.length < 50) || this.$t('challenges.label.challengeTitleLengthExceed') ,
        value: (v) => (v >= 0 && v<= 9999) || this.$t('challenges.label.pointsValidation')
      },
      drawer: false,
      challenge: null,
      isValidForm: true,
      isValidDescription: true,
    };
  },
  computed: {
    drawerTitle(){
      return this.challenge && this.challenge.id ? this.$t('challenges.button.editChallenge') : this.$t('challenges.button.addChallenge') ;
    },
    spaceSuggesterLabels() {
      return {
        searchPlaceholder: this.$t('challenges.spaces.noDataLabel'),
        placeholder: this.$t('challenges.spaces.placeholder'),
      };
    },
    disabledSave() {
      return !this.challenge
        || !this.isValidForm
        || !this.isValidDescription
        || !this.challenge.startDate
        || !this.challenge.endDate
        || !this.challenge.program?.id;
    },
    buttonName() {
      return this.challenge && this.challenge.id && this.$t('engagementCenter.button.save') || this.$t('engagementCenter.button.create') ;
    },
    programSuggesterLabels() {
      return {
        searchPlaceholder: this.$t('challenges.programSuggester.searchPlaceholder'),
        placeholder: this.$t('challenges.programSuggester.placeholder'),
        noDataLabel: this.$t('challenges.programSuggester.noDataLabel'),
      };
    }
  },
  created() {
    this.$root.$on('edit-challenge-details', this.open);
    this.$root.$on('edit-manuel-rule', this.editManuelRule);
  },
  methods: {
    reset() {
      this.challenge = {
        points: 20,
      };
      this.$refs.challengeDatePicker.startDate = null;
      this.$refs.challengeDatePicker.endDate = null;
    },
    open(challenge) {
      this.challenge = challenge && JSON.parse(JSON.stringify(challenge)) || {
        points: 20,
        enabled: true,
        description: '',
      };

      const status = this.getChallengeStatus();

      this.$refs.challengeDrawer.open();
      this.$nextTick()
        .then(() => {
          this.$refs.challengeDescription.initCKEditor();
          if (status === 'STARTED' || status === 'ENDED'){
            this.$refs.challengeDatePicker.startDate = new Date(this.challenge.startDate);
            this.$refs.challengeDatePicker.endDate = this.challenge.endDate;
            this.$refs.challengeDatePicker.disabledStartDate = true;
            this.disabledSuggester = true ;
          } else {
            this.$refs.challengeDatePicker.startDate = this.challenge.startDate;
            this.$refs.challengeDatePicker.endDate = this.challenge.endDate;
          }
        });
    },
    editManuelRule(rule) {
      this.$challengesServices.getChallengeById(rule.id)
        .then(challenge => this.open(challenge));
    },
    close() {
      this.reset();
      this.$refs.challengeDrawer.close();
    },
    updateChallengeStartDate(value) {
      if (value) {
        this.$set(this.challenge,'startDate', value);
      }
    },
    updateChallengeEndDate(value) {
      if (value) {
        this.$set(this.challenge,'endDate', value);
      }
    },
    addDescription(value) {
      if (value) {
        this.$set(this.challenge,'description', value);
      }
    },
    getChallengeStatus() {
      const status = {
        NOTSTARTED: 'NOTSTARTED',
        STARTED: 'STARTED',
        ENDED: 'ENDED'
      };
      const currentDate = new Date();
      const startDate = new Date(this.challenge && this.challenge.startDate);
      const endDate = new Date(this.challenge && this.challenge.endDate);
      if (startDate.getTime() > currentDate.getTime() && endDate.getTime() > currentDate.getTime()) {
        return status.NOTSTARTED;
      } else if ((startDate.getTime()<currentDate.getTime() && endDate.getTime() > currentDate.getTime()) || (this.getFromDate(endDate) ===  this.getFromDate(currentDate))) {
        return status.STARTED;
      } else if (endDate.getTime() < currentDate.getTime() && startDate.getTime()< currentDate.getTime()) {
        return status.ENDED;
      }
    },
    getFromDate(date) {
      return this.$engagementCenterUtils.getFromDate(date);
    },
    saveChallenge() {
      if (this.challenge.startDate > this.challenge.endDate){
        this.$engagementCenterUtils.displayAlert(this.$t('challenges.challengeDateError'), 'error');
        return;
      }
      if (this.challenge.id) {
        const chanllengeToSave = JSON.parse(JSON.stringify(this.challenge));
        chanllengeToSave.programId = this.challenge.program.id;
        chanllengeToSave.program = this.challenge.program.title;
        this.$refs.challengeDrawer.startLoading();
        this.$challengesServices.updateChallenge(chanllengeToSave)
          .then(challenge =>{
            this.$engagementCenterUtils.displayAlert(this.$t('challenges.challengeUpdateSuccess'));
            this.$root.$emit('challenge-updated', challenge);
            this.$root.$emit('program-rules-refresh');
            this.close();
            this.challenge = {};
          })
          .catch(e => {
            console.error(e);
            this.$engagementCenterUtils.displayAlert(this.$t('challenges.challengeCreateError'), 'error');
          })
          .finally(() => this.$refs.challengeDrawer.endLoading());
      } else {
        const chanllengeToSave = JSON.parse(JSON.stringify(this.challenge));
        chanllengeToSave.programId = this.challenge.program.id;
        chanllengeToSave.program = this.challenge.program.title;
        this.$refs.challengeDrawer.startLoading();
        this.$challengesServices.saveChallenge(chanllengeToSave)
          .then((challenge) =>{
            this.$root.$emit('challenge-added', challenge);
            this.$engagementCenterUtils.displayAlert(this.$t('challenges.challengeCreateSuccess'));
            this.close();
            this.challenge = {};
          })
          .catch(e => {
            console.error(e);
            this.$engagementCenterUtils.displayAlert(this.$t('challenges.challengeCreateError'), 'error');
          })
          .finally(() => this.$refs.challengeDrawer.endLoading());
      }
    },
  }
};
</script>
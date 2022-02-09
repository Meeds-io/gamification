<template>
  <exo-drawer
    ref="challengeDrawer"
    class="challengeDrawer"
    :right="!$vuetify.rtl"
    @closed="close"
    eager>
    <template slot="title">
      <span class="pb-2"> {{ drawerTitle }} </span>
    </template>
    <template slot="content">
      <v-card-text>
        <div
          v-if="warning"
          class="alert alert-error v-content mb-4">
          <i class="uiIconError"></i>
          {{ warning }}
        </div>
        <v-form
          ref="form"
          v-model="isValid.title"
          @submit="
            $event.preventDefault();
            $event.stopPropagation();
          ">
          <v-textarea
            v-model="challenge.title"
            :disabled="disabledTitleEdit"
            :placeholder="$t('challenges.label.enterChallengeTitle') "
            :rules="[rules.length]"
            name="challengeTitle"
            class="pl-0 pt-0 challenge-title"
            auto-grow
            rows="1"
            row-height="13"
            required
            autofocus />
          <v-divider class="my-2" />
          <span class="subtitle-1"> {{ $t('challenges.label.audienceSpace') }} *</span>
          <exo-identity-suggester
            ref="challengeSpaceSuggester"
            v-model="audience"
            :labels="spaceSuggesterLabels"
            :include-users="false"
            :width="220"
            :disabled="disabledSuggester"
            name="challengeSpaceAutocomplete"
            include-spaces
            only-manager />

          <span class="subtitle-1"> {{ $t('challenges.label.program') }} *</span>
          <challenge-program
            ref="challengeProgram"
            @addProgram="addProgram($event)"
            @removeProgram="removeProgram($event)" />

          <span class="subtitle-1"> {{ $t('challenges.label.challengeOwners') }} *</span>
          <challenge-assignment
            ref="challengeAssignment"
            class="my-2"
            :audience="audience"
            v-model="challenge.managers"
            multiple
            @remove-user="removeManager"
            @add-item="addManager" />

          <div class="mt-4">
            <span class="subtitle-1"> {{ $t('challenges.label.ChallengeDates') }} *</span>
            <challenge-date-picker
              ref="challengeDatePicker"
              :challenge="challenge"
              class="challengeDates my-2"
              @startDateChanged="updateChallengeStartDate($event)"
              @endDateChanged="updateChallengeEndDate($event)" />
          </div>
          <div class="mt-4">
            <span class="subtitle-1"> {{ $t('challenges.label.reward') }}</span>
            <v-text-field
              v-model="challenge.points"
              :label="$t('challenges.label.points')"
              :rules="[rules.value]"
              :placeholder="$t('challenges.label.points')"
              class="pt-2 pointsChallenges"
              type="number"
              outlined
              required />
          </div>
          <div class="challengeDescription py-4 my-2">
            <challenge-description
              ref="challengeDescription"
              :challenge="challenge"
              :is-challenge="true"
              v-model="challenge.description"
              :value="challenge.description"
              @invalidDescription="invalidDescription($event)"
              @validDescription="validDescription($event)"
              @addDescription="addDescription($event)" />
          </div>
        </v-form>
      </v-card-text>
    </template>
    <template slot="footer">
      <div class="d-flex mr-2">
        <v-spacer />
        <button
          class="ignore-vuetify-classes btn mx-1"
          @click="close">
          {{ $t('challenges.button.cancel') }}
        </button>
        <button
          :disabled="!disabledSave"
          class="ignore-vuetify-classes btn btn-primary"
          @click="SaveChallenge">
          {{ buttonName }}
        </button>
      </div>
    </template>
  </exo-drawer>
</template>

<script>

export default {
  name: 'ChallengeDrawer',
  props: {
    challenge: {
      type: Object,
      default: function() {
        return {};
      },
    },
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
      return this.challenge && this.challenge.title && this.challenge.audience && this.challenge.managers.length > 0 && this.challenge.startDate && this.challenge.endDate && this.challenge.program  && this.challenge.description && this.challenge.description.length > 0 && this.isValid.title && this.isValid.description && !this.disabledUpdate;
    },
    buttonName() {
      return this.challenge && this.challenge.id && this.$t('challenges.button.save') || this.$t('challenges.button.create') ;
    },
  },
  data() {
    return {
      rules: {
        length: (v) => (v && v.length < 250) || this.$t('challenges.label.challengeTitleLengthExceed') ,
        value: (v) => (v >= 0 && v<= 9999) || this.$t('challenges.label.pointsValidation')
      },
      audience: '' ,
      isValid: {
        title: true,
        description: true },
      disabledSuggester: false,
      disabledTitleEdit: false,
      warning: null,
      disabledUpdate: false,
    };
  },
  watch: {
    audience() {
      if (this.audience && this.audience.id && !this.audience.notToChange) {
        this.$spaceService.getSpaceMembers(null, 0, 0, null,'manager', this.audience.spaceId).then(managers => {
          this.challenge.managers = [];
          const listManagers = [];
          managers.users.forEach(manager => {
            const newManager= {
              id: manager.id,
              remoteId: manager.username,
              fullName: manager.fullname,
              avatarUrl: manager.avatar,
            };
            this.$set(this.challenge.managers,this.challenge.managers.length, newManager.id);
            listManagers.push(newManager);
          });
          this.$set(this.challenge,'audience', this.audience.spaceId);
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
        this.challenge.managers= [];
        document.dispatchEvent(new CustomEvent('audienceChanged'));
      }
    },
  },
  mounted() {
    if (!(this.challenge && this.challenge.points)) {
      this.$set(this.challenge,'points', 20);
    }
  },
  methods: {
    setUp(){
      const space = this.challenge.space ;
      const NewAudience = {
        id: `space:${ space.displayName }` ,
        profile: {
          avatarUrl: space.avatarUrl,
          fullName: space.displayName,
        },
        providerId: 'space',
        remoteId: space.displayName,
        spaceId: this.challenge.space.id,
        notToChange: true,
      };
      const status= this.getChallengeStatus();
      if (status === 'STARTED' || status === 'ENDED'){
        this.$refs.challengeDatePicker.startDate = new Date(this.challenge.startDate);
        this.$refs.challengeDatePicker.endDate = this.challenge.endDate;
        this.$refs.challengeDatePicker.disabledStartDate = true;
        this.$refs.challengeSpaceSuggester.emitSelectedValue(NewAudience);
        this.disabledSuggester = true ;
      } else {
        this.$refs.challengeDatePicker.startDate = this.challenge.startDate;
        this.$refs.challengeDatePicker.endDate = this.challenge.endDate;
        this.$refs.challengeSpaceSuggester.emitSelectedValue(NewAudience);
      }
      this.$refs.challengeProgram.broadcast = false;
      this.$refs.challengeProgram.program =  this.challenge.program;
      const data = {
        managers: this.challenge.managers,
        space: space,
      };
      document.dispatchEvent(new CustomEvent('audienceChanged', {detail: data}));
      this.$refs.challengeAssignment.assigneeObj = this.challenge.managers;
      this.$set(this.challenge,'audience', space.id);
    },
    reset(){
      this.challenge = {};
      this.$refs.challengeDatePicker.startDate = null;
      this.$set(this.challenge,'points', 20);
      this.$refs.challengeDatePicker.endDate = null;
      this.$refs.challengeDescription.inputVal = null;
      this.$refs.challengeAssignment.assigneeObj = null;
      this.$refs.challengeSpaceSuggester.emitSelectedValue( {});
      this.$refs.challengeDatePicker.disabledStartDate = false;
      this.$refs.challengeDatePicker.disabledEndDate = false;
      this.$refs.challengeSpaceSuggester.disabledUnAssign = false;
      this.disabledSuggester = false ;
      this.disabledTitleEdit = false ;
      this.disabledUpdate = false ;
      this.$refs.challengeAssignment.disabledUnAssign = false;
      this.$refs.challengeDescription.disabled = false;
      this.warning= null;
      this.$refs.challengeProgram.program = null;
      this.$refs.challengeProgram.broadcast = true;
    },
    open(){
      this.$refs.challengeDescription.initCKEditor();
      if (this.challenge && this.challenge.id){
        this.setUp();
      }
      this.$refs.challengeDrawer.open();
    },
    close(){
      this.reset();
      this.$refs.challengeDescription.deleteDescription();
      this.$refs.challengeDrawer.close();
    },
    removeManager(id) {
      const index = this.challenge.managers && this.challenge.managers.findIndex((manager) => {
        if (manager && manager.id) {
          return manager.id === id;
        } else {
          return manager === id;
        }
      });
      if (index >= 0) {
        this.$delete(this.challenge.managers,index, 1);
      }
    },
    addManager(id) {
      const index = this.challenge.managers && this.challenge.managers.findIndex((manager) => {
        if (manager && manager.id) {
          return manager.id === id;
        } else {
          return manager === id;
        }
      });
      if (index < 0) {
        this.$set(this.challenge.managers,this.challenge.managers.length, id);
      }
    },
    addProgram(program) {
      this.$set(this.challenge,'program', program);
    },
    removeProgram() {
      this.$set(this.challenge,'program', '');
      this.$refs.challengeProgram.broadcast = true;
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
    invalidDescription() {
      this.$set(this.isValid,'description', false);
    },
    validDescription() {
      this.$set(this.isValid,'description', true);
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
      return this.$challengeUtils.getFromDate(date);
    },
    SaveChallenge() {
      if (this.challenge.startDate > this.challenge.endDate){
        this.$root.$emit('show-alert', {type: 'error',message: this.$t('challenges.challengeDateError')});
        return;
      }
      if (this.challenge && this.challenge.id){
        if ( this.challenge.managers && this.challenge.managers[0].id){
          this.challenge.managers = this.challenge.managers.map(manager => manager.id);
        }
        this.$challengesServices.updateChallenge(this.challenge).then(() =>{
          this.$root.$emit('show-alert', {type: 'success',message: this.$t('challenges.challengeUpdateSuccess')});
          this.$root.$emit('challenge-updated');
          this.close();
          this.challenge = {};
        })
          .catch(e => {
            this.$root.$emit('show-alert', {type: 'error',message: String(e)});
          });
      } else {
        this.$challengesServices.saveChallenge(this.challenge).then((challenge) =>{
          this.$root.$emit('show-alert', {type: 'success',message: this.$t('challenges.challengeCreateSuccess')});
          this.$root.$emit('challenge-added', challenge);
          this.close();
          this.challenge = {};
        })
          .catch(e => {
            this.$root.$emit('show-alert', {type: 'error',message: String(e)});
          });
      }
    },
  }
};
</script>
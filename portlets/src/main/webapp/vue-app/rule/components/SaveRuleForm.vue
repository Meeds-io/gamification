<!-- eslint-disable -->
<!--
This file is part of the Meeds project (https://meeds.io/).
Copyright (C) 2020 Meeds Association
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
  <div>
    <div class="col-sm-12 fluid">
      <div id="headingOne" class="addRule pull-right">
        <button
          aria-controls="collapseOne"
          aria-expanded="true"
          class="btn btn-primary ignore-vuetify-classes"
          data-target="#collapseOne"
          data-toggle="collapse"
          type="button"
          @click.prevent="collapseButton()">
          {{ $t(`exoplatform.gamification.addrule`) }}
        </button>
      </div>
      <div
        id="collapseOne"
        aria-labelledby="headingOne"
        class="collapse show"
        :class="isShown ? '' : 'out'"
        data-parent="#accordionExample"
        style="height: 0px; transition: inherit;">
        <div class="card-body">
          <div
            id="myForm"
            class="UIPopupWindow uiPopup UIDragObject NormalStyle myForm">
            <div class="popupHeader ClearFix">
              <a class="uiIconClose pull-right" @click.prevent="collapseButton()"></a>

              <span class="PopupTitle popupTitle">{{ this.$t(`exoplatform.gamification.addrule`) }}</span>
            </div>
            <div class="PopupContent popupContent">
              <form id="titleInputGroup">
                <label class="col-form-label pt-0">{{ $t(`exoplatform.gamification.gamificationinformation.Event`) }}:</label>
                <select
                  v-model="rule.event"
                  class="mb-4 select-event"
                  required>
                  <option
                    disabled
                    selected
                    value="null">
                    {{ $t(`exoplatform.gamification.selectevent`) }}
                  </option>
                  <option v-for="option in events" :value="option">
                    {{ $t(`exoplatform.gamification.gamificationinformation.rule.title.${option}`,option)  }}
                  </option>
                </select>


                <!--  <b-alert v-if="formErrors.title" :show="dismissCountDown" dismissible variant="danger" class="require-msg" @dismissed="dismissCountdown=0" @dismiss-count-down="countDownChanged">
                                      Rule title is required please enter a title {{dismissCountDown}}
                                  </b-alert>-->
              </form>

              <form id="descriptionInputGroup">
                <label class="col-form-label pt-0">{{ $t(`exoplatform.gamification.gamificationinformation.domain.Description`) }}:</label>
                <textarea
                  id="ruleDescription"
                  v-model="rule.description"
                  :placeholder="$t('exoplatform.gamification.gamificationinformation.domain.edit.label.description')"
                  :rows="3"
                  :max-rows="6"
                  class="ignore-vuetify-classes">
                  </textarea>
              </form>
              <b-form id="scoreInputGroup">
                <label
                  id="scoreInputGroup"
                  class="col-form-label pt-0"
                  for="scoreInput">{{ $t(`exoplatform.gamification.score`) }}:</label>
                <input
                  id="scoreDescription"
                  v-model="rule.score"
                  type="number"
                  required
                  :placeholder="$t(`exoplatform.gamification.rules.score`)">

                <div
                  v-if="formErrors.score"
                  class="alert alert-danger require-msg"
                  :show="dismissCountDown"
                  dismissible
                  variant="danger"
                  @dismissed="dismissCountdown=0"
                  @dismiss-count-down="countDownChanged">
                  {{ $t(`exoplatform.gamification.rule.score.required`) }}
                </div>
              </b-form>
              <form class="switch">
                <label class="col-form-label pt-0">{{ $t(`exoplatform.gamification.enabled`) }}:</label>
                <label class="uiSwitchBtn">

                  <input v-model="rule.enabled" type="checkbox">
                  <div class="slider round"><span class="absolute-yes">{{ $t(`exoplatform.gamification.YES`,"YES") }}</span></div>
                  <span class="absolute-no">{{ $t(`exoplatform.gamification.NO`) }}</span>
                </label>
              </form>
              <form id="domainSelectboxGroup">
                <label class="col-form-label pt-0">{{ $t(`exoplatform.gamification.gamificationinformation.Domain`) }}:</label>
                <select v-model="rule.domainDTO" required>
                  <option
                    disabled
                    selected
                    value="null">
                    {{ $t(`exoplatform.gamification.selectdM`) }}
                  </option>
                  <option v-for="option in domains" :value="option">
                    {{
                      $t(`exoplatform.gamification.gamificationinformation.domain.${option.title}`,option.title)
                    }}
                  </option>
                </select>
              </form>

              <v-toolbar
                  color="transparent"
                  flat
                  class="pa-4 mb-4">
                <v-spacer />
                <div class="d-flex">
                  <btn
                      class="ignore-vuetify-classes btn btn-primary mx-1"
                      type="submit"
                      :disabled="isDisabled"
                      @click.prevent="onSubmit(), collapseButton()">
                    {{ this.$t('exoplatform.gamification.gamificationinformation.domain.confirm') }}
                  </btn>
                  <btn
                      class="ignore-vuetify-classes btn mx-4"
                      outlined
                      @click.prevent="collapseButton(), onCancel()">
                    {{ this.$t('exoplatform.gamification.gamificationinformation.domain.cancel') }}
                  </btn>
                </div>
              </v-toolbar>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script>

export default {
  props: {
    rule: {
      type: Object,
      default: function() {
        return {};
      },
    },
    domains: {
      type: Object,
      default: function() {
        return [];
      },
    },
    events: {
      type: Object,
      default: function() {
        return [];
      },
    },
    errorType: {
      type: String,
      default: function() {
        return '';
      },
    }
  },
  data: function (){
    return {
      SaveRuleForm: '',
      formErrors: {},
      selectedFile: null,
      selectedFileName: '',
      dismissSecs: 5,
      dismissCountDown: 0,
      date: new Date(),
      isShown: false,
      config: {
        format: 'YYYY-MM-DD',
        useCurrent: false,
      },
      dynamicAreas: []
    };
  },
  computed: {
    isDisabled: function(){
      return !(this.isNotEmpty(this.rule.event)&&this.isNotEmpty(this.rule.score)&&this.rule.domainDTO !== null);
    }
  },

  watch: {
    'rule.id'() {
      this.formErrors = {};
      this.selectedFile = null;
      this.selectedFileName = this.rule.imageName;
    },
    'rule.domainDTO'() {
      if (typeof(this.rule.domainDTO) !== 'undefined'){this.rule.area = this.rule.domainDTO.title;}
    },
    'rule.ruleDTO'() {
      this.rule.title = this.rule.ruleDTO.title;
    }
  },
  methods: {
    isNotEmpty(str){
      return (str!=null&&str !== '');
    },
    onImageChanged(event) {
      this.selectedFile = event.target.files[0];
      this.selectedFileName = event.target.files[0].name;
    },
    onCancel() {
      this.$emit('cancel');

    },
    onSubmit() {
      this.createRule(this.rule);
      if (this.isShown) {
        this.closeAlert('.alert');
      }
    },
    closeAlert(item) {
      setTimeout(function () {
        $(item).fadeOut('fast');
      }, 4000);

    },
    collapseButton() {
      this.isShown = !this.isShown;
    },

    countDownChanged(dismissCountDown) {
      this.dismissCountDown = dismissCountDown;
    },
    createRule(ruleDTO) {
      this.$RuleServices.createRule(ruleDTO)
        .then(() => {
          this.$emit('sucessAdd', this.rule);
        })
        .catch(e => {
          this.addError = true;
          this.uploadId='';
          if (e.response.status===304){
            this.errorType='ruleExists';
          } else {
            this.errorType='addRuleError';
          }
          this.$emit('failAdd', this.rule, this.errorType);
        });
    }
  }
};
</script>

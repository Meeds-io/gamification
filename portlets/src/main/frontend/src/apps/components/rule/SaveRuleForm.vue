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
      <div id="headingOne" class="pull-right">
        <button
          aria-controls="collapseOne"
          aria-expanded="true"
          class="btn btn-primary"
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
            class="UIPopupWindow uiPopup UIDragObject NormalStyle"
            style="width: 760px; z-index:1000000; position: relative; left: auto; margin: 0 20px; z-index: 1; max-width: 100%;margin: 0 auto;height: 100%;">
            <div class="popupHeader ClearFix">
              <a class="uiIconClose pull-right" @click.prevent="collapseButton()"></a>

              <span class="PopupTitle popupTitle">{{ this.$t(`exoplatform.gamification.addrule`) }}</span>
            </div>
            <div class="PopupContent popupContent">
              <form id="titleInputGroup">
                <label class="col-form-label pt-0">{{ $t(`exoplatform.gamification.gamificationinformation.Event`)
                }}:</label>


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
                    {{
                      $t(`exoplatform.gamification.gamificationinformation.rule.title.${option}`,option)
                    }}
                  </option>
                </select>


                <!--  <b-alert v-if="formErrors.title" :show="dismissCountDown" dismissible variant="danger" class="require-msg" @dismissed="dismissCountdown=0" @dismiss-count-down="countDownChanged">
                                      Rule title is required please enter a title {{dismissCountDown}}
                                  </b-alert>-->
              </form>

              <form id="descriptionInputGroup">
                <label class="col-form-label pt-0">{{ $t(`exoplatform.gamification.gamificationinformation.domain.Description`)
                }}:</label>
                <textarea
                  id="ruleDescription"
                  v-model="rule.description"
                  :placeholder="$t(`exoplatform.gamification.rules.description`)"
                  :rows="3"
                  :max-rows="6">
                            </textarea>
              </form>
              <b-form id="scoreInputGroup">
                <label
                  id="scoreInputGroup"
                  class="col-form-label pt-0"
                  for="scoreInput">{{ $t(`exoplatform.gamification.score`)
                  }}:</label>
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

                <label class="col-form-label pt-0" style="margin-left: 20%">{{ $t(`exoplatform.gamification.gamificationinformation.Domain`)
                }}:</label>
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

              <div class="row">
                <b-col>
                  <button
                    class="btn secondary pull-right"
                    type="submit"
                    @click.prevent="collapseButton(), onCancel()">
                    {{
                      this.$t('exoplatform.gamification.gamificationinformation.domain.cancel') }}
                  </button>
                  <button
                    class="btn-primary pull-right"
                    type="submit"
                    :disabled="isDisabled" 
                    @click.prevent="onSubmit(), collapseButton()">
                    {{ this.$t('exoplatform.gamification.gamificationinformation.domain.confirm') }}
                  </button>
                </b-col>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
/* eslint-disable */
import Vue from 'vue';
import BootstrapVue from 'bootstrap-vue';
import 'bootstrap/dist/css/bootstrap.css';
import 'bootstrap-vue/dist/bootstrap-vue.css';
import axios from 'axios';

Vue.use(BootstrapVue);
export default {
  props: ['rule', 'domains', 'events', 'errorType'],
  data: function (){
    return {
      SaveRuleForm: '',
      formErrors: {},
      selectedFile: undefined,
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
      return !(this.isNotEmpty(this.rule.event)&&this.isNotEmpty(this.rule.score)&&this.rule.domainDTO!=null);
    }
  },

  watch: {
    'rule.id'() {
      this.formErrors = {};
      this.selectedFile = undefined;
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
      return (str!=null&&str!='');
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
    confirm() {
      this.$modals.confirm({
        message: 'Confirm?',
        onApprove: () => {
        },
        onCancel: () => {
        },
      });
    },
    createRule(ruleDTO) {
      axios.post('/portal/rest/gamification/rules/add', ruleDTO)
        .then(response => {
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
      //this.resetRuleInForm()
    }
  }
};
</script>

<style scoped>
    .card-body label {
        display: block;
    }
    form {
        margin-bottom: 24px;
    }
    form-row {
        display: flex;
        flex-wrap: wrap;
        margin-right: -5px;
        margin-left: -5px;
    }
    div#headingOne button.btn.btn-primary {
        margin: 15px 12px 5px;
    }
    .btn-primary:focus, .btn-primary.focus {
        box-shadow: inset 0 0 0 0.2rem rgba(38, 143, 255, 0.5);
    }
    label {
        display: inline-block;
        max-width: 100%;
        margin-bottom: 5px;
        font-weight: 500;
        color: #333;
    }
    input[type="number"] {
        font-size: 15px;
        height: 40px;
        padding: 0 10px;
        border: 2px solid #e1e8ee;
        border-radius: 5px;
        box-shadow: none;
        max-height: 40px;
        text-overflow: ellipsis;
    }
    form#areaSelectboxGroup {
        margin-bottom: 0px;
    }

    .require-msg{
        max-width: 100% !important;
        font-size: 14px;
    }
    msg.alert-dismissible.alert-danger {
        display: -webkit-inline-box;
        width: auto;
    }
    input {
        width: 100%;
    }
    textarea#ruleDescription {
        width: 100%;
        font-size: 15px;
    }

    button, [type="button"], [type="reset"], [type="submit"] {
        -webkit-appearance: button;
        align-content: stretch;
        padding: 8px 25px;
        margin-left: 500px;
    }

    input[type="checkbox"] {
        width: auto;
        margin-bottom: 10px;
    }
    div#headingOne:hover {
        background: transparent;
        transition: all .5s;
    }
    div#headingOne {
        box-shadow: none;
        transition: all .5s;
    }
    button.btn.secondary {
        padding: 8px 25px;
        margin-left: 25px;
        border: 1px solid #e1e8ee !important;
        color: #4d5466;
        background-color: transparent !important;
    }
    button.btn-primary.pull-right {
        border-radius: 0.25rem;
    }

    .collapse {
        top: 15px;
    }
    div#collapseOne {
        position: fixed;
        z-index: 10000;
        left: 0;
        top: 0;
        bottom: 0;
        width: 100%;
        height: 100%;
        overflow: auto;
        background-color: rgb(0,0,0);
        background-color: rgba(0,0,0,0.4);
    }

     /* switch */
    .uiSwitchBtn {
        position: relative;
        display: inline-block !important;
        width: 60px;
        height: 29px;
        margin-left: 20px;
    }
    .uiSwitchBtn input {display:none;}
    .slider {
        position: absolute;
        cursor: pointer;
        overflow: hidden;
        top: 5px;
        left: 0;
        right: 0;
        bottom: 0;
        width: 60px;
        height: 20px;
        background-color: #f2f2f2;
        -webkit-transition: .4s;
        transition: .4s;
    }
    .slider:before {
        position: absolute;
        z-index: 2;
        content: "";
        height: 14px;
        width: 14px;
        left: 5px;
        bottom: 3px;
        background-color: darkgrey;
        -webkit-box-shadow: 0 2px 5px rgba(0, 0, 0, 0.22);
        box-shadow: 0 2px 5px rgba(0, 0, 0, 0.22);
        -webkit-transition: .4s;
        transition: all 0.4s ease-in-out;
    }
    .slider:after {
        position: absolute;
        left: -20px;
        z-index: 1;
        content: "";
        font-size: 13px;
        text-align: left!important;
        line-height: 19px;
        padding-left: 0;
        width: 95px;
        height: 26px!important;
        color: #f9f9f9;
        background-color: #477ab3;
        background-image: -moz-linear-gradient(top, #578dc9, #2f5e92);
        background-image: -webkit-gradient(linear, 0 0, 0 100%, from(#578dc9), to(#2f5e92));
        background-image: -webkit-linear-gradient(top, #578dc9, #2f5e92);
        background-image: -o-linear-gradient(top, #578dc9, #2f5e92);
        background-image: linear-gradient(to bottom, #578dc9, #2f5e92);
        background-repeat: repeat-x;
        filter: progid:DXImageTransform.Microsoft.gradient(startColorstr='#ff578dc9', endColorstr='#ff2f5e92', GradientType=0);
        -webkit-box-shadow: inset 0px 3px 5px #224469;
        -moz-box-shadow: inset 0px 3px 5px #224469;
        box-shadow: inset 0px 3px 5px #224469;
        -webkit-border-top-left-radius: 9px;
        -moz-border-radius-topleft: 9px;
        border-top-left-radius: 9px;
        -webkit-border-bottom-left-radius: 9px;
        -moz-border-radius-bottomleft: 9px;
        border-bottom-left-radius: 9px;
        height: 57px;
        border-radius: 100px;
        background-color: #578dc9;
        -webkit-transform: translateX(-190px);
        -ms-transform: translateX(-190px);
        transform: translateX(-190px);
        transition: all 0.4s ease-in-out;
    }
    input:not(:checked)+.slider > .absolute-yes {
        z-index: -1;
        position: absolute;
        -webkit-transform: translateX(-190px);
        -ms-transform: translateX(-190px);
        transform: translateX(-190px);
        left: 2px;
        color: white;
        text-align: right;
        font-size: 16px;
        width: calc(100% - 25px);
        line-height: 22px;
        cursor: pointer;
        transition: all 0.4s ease-in-out;
    }
    input:checked+.slider> .absolute-yes {
        -webkit-transform: translateX(0px);
        -ms-transform: translateX(0px);
        transform: translateX(0px);
        z-index: 15;
        position: absolute;
        left: 2px;
        color: white;
        text-align: right;
        font-size: 16px;
        width: calc(100% - 25px);
        line-height: 22px;
        cursor: pointer;
        transition: all 0.4s ease-in-out;
    }
    input:checked + .slider:after {
        -webkit-transform: translateX(0px);
        -ms-transform: translateX(0px);
        transform: translateX(0px);
        padding-left: 25px;
    }
    input:checked + .slider:before {
        background-color: #fff;
        -webkit-transform: translateX(38px);
        -ms-transform: translateX(38px);
        transform: translateX(38px);
    }
    input:checked + .slider:before {
        -webkit-transform: translateX(38px);
        -ms-transform: translateX(38px);
        transform: translateX(38px);
    }
    /* Rounded sliders */
    .slider.round {
        border-radius: 100px;
    }
    .slider.round:before {
        border-radius: 50%;
    }
    .absolute-no {
        position: absolute;
        left: 27px;
        color: DarkGrey;
        text-align: right !important;
        font-size: 16px;
        /* width: calc(100% - 25px); */
        line-height: 30px;
        cursor: pointer;
    }
    input.rule-needed-score-col{
        max-width: 60px;
        text-align: center;
    }
    i.uiIconClose.uiIconBlue {
        zoom: 133%;
        height: 100%;
        vertical-align: super;
        color: #578dc9;
        line-height: inherit;
    }
    .collapse.show.out {
        display: none;
    }

    .uiPopup .popupContent select, .modal.uiPopup .popupContent select {
        outline: none;
        border: 2px solid #e1e8ee;
        border-radius: 5px;
        box-shadow: none;
        margin-left: 20px;
        height: 40px;
        width: 343px;
        margin-top: 17px;
        margin-bottom: inherit;

    }
    select:focus{
        border-color: #a6bad6;
        -webkit-box-shadow: inset 0 1px 1px rgba(0,0,0,.075), 0 0 5px #c9d5e6;
        -moz-box-shadow: inset 0 1px 1px rgba(0,0,0,.075),0 0 5px #c9d5e6;
        box-shadow: inset 0 1px 1px rgba(0,0,0,.075), 0 0 5px #c9d5e6;
        color: #333;
    }

    label.col-form-label.pt-0 {
        display: inline-block;
    }

    select.mb-4.select-event {
        margin: 0 !important;
        width: 100%;
        height: 40px;
    }

    .btn-primary.disabled, .btn-primary:disabled {
     background-color: #afc9e5; 
     background-image: none;
    }
    .PopupContent.popupContent select {
        height: 40px;
    }

</style>
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
          {{
            this.$t('exoplatform.gamification.gamificationinformation.domain.popupadd') }}
        </button>
      </div>
      <div
        id="collapseOne"
        :class="isShown ? '' : 'out'"
        aria-labelledby="headingOne"
        class="collapse show"
        data-parent="#accordionExample"
        style="height: 0px; transition: inherit;">
        <div class="card-body">
          <div
            id="myForm"
            class="UIPopupWindow uiPopup UIDragObject NormalStyle"
            style="width: 760px; z-index:1000000; position: relative; left: auto; margin: 0 20px; z-index: 1; max-width: 100%;margin: 0 auto;height: 100%;">
            <div class="popupHeader ClearFix">
              <a class="uiIconClose pull-right" @click.prevent="collapseButton()"></a>

              <span class="PopupTitle popupTitle"> {{ this.$t('exoplatform.gamification.gamificationinformation.domain.popupadd') }}</span>
            </div>

            <div class="PopupContent popupContent">
              <form id="titleInputGroup">
                <label class="col-form-label pt-0">{{
                  this.$t('exoplatform.gamification.gamificationinformation.domain.Title') }} </label>
                <input
                  id="titleInput"
                  v-model="domain.title"
                  :placeholder="$t('exoplatform.gamification.gamificationinformation.add.domain.title')"
                  required
                  type="text">
                </input>
              </form>
              <form id="descriptionInputGroup">
                <label class="col-form-label pt-0">{{
                  this.$t('exoplatform.gamification.gamificationinformation.domain.Description')
                }}:</label>
                <textarea
                  id="domainDescription"
                  v-model="domain.description"
                  :max-rows="6"
                  :rows="3"
                  :placeholder="$t('exoplatform.gamification.gamificationinformation.add.domain.description')">
                            </textarea>
              </form>

              <form>
                <label class="col-form-label pt-0">{{ $t(`exoplatform.gamification.enabled`,"Enabled") }}:</label>
                <label class="uiSwitchBtn">
                  <input v-model="domain.enabled" type="checkbox">
                  <div class="slider round"><span class="absolute-yes">{{ $t(`exoplatform.gamification.YES`,"YES") }}</span></div>
                  <span class="absolute-no">{{ $t(`exoplatform.gamification.NO`,"NO") }}</span>
                </label>
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
                    @click.prevent="onSubmit()">
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

  props: ['domain'],


  data: function () {
    return {
      formErrors: {},
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
      return (this.domain.title==null||this.domain.title=='');
    }
  },

  watch: {
    'domain.id'() {
      this.formErrors = {};
    },

  },
  methods: {


    onCancel() {
      this.$emit('cancel');
    },

    onSubmit() {
      this.isShown = !this.isShown;
      this.createDomain(this.domain);
      if (this.isShown) {

        this.closeAlert('.alert');
        this.collapseButton();
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

    confirm() {
      this.$modals.confirm({
        message: 'Confirm?',
        onApprove: () => {
        },
        onCancel: () => {
        },
      });
    },

    createDomain(domainDTO) {
      axios.post('/portal/rest/gamification/domains', domainDTO)
        .then(response => {
          this.$emit('sucessAdd', this.domain);
        })
        .catch(e => {
          this.$emit('failAdd', this.domain);
        });
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
        font-weight: 600;
        color: #333;
    }

    input[type="number"] {
        font-size: 15px;
        height: 40px;
        padding: 0 10px;
        border: 1px solid #e1e8ee;
        border-radius: 5px;
        box-shadow: none;
        max-height: 40px;
        text-overflow: ellipsis;
    }

    form#areaSelectboxGroup {
        margin-bottom: 0px;
    }

    .require-msg {
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
        margin-right: 1%;
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
    div#headingOne button.btn.btn-primary:hover {
        background: #476a9c ;
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
        background-color: rgb(0, 0, 0);
        background-color: rgba(0, 0, 0, 0.4);
    }

    /* switch */
    label.uiSwitchBtn {
        height: 30px;
    }
    .uiSwitchBtn span.absolute-no {
        text-align: left;
    }
    .switch {
        position: relative;
        display: inline-block;
        width: 53px;
        height: 32px;
        top: 0.4rem;
    }

    .switch input {
        display: none;
    }

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

    .slider:after {
        position: absolute;
        left: -20px;
        z-index: 1;
        content: "";
        font-size: 13px;
        text-align: left !important;
        line-height: 19px;
        padding-left: 0;
        width: 95px;
        height: 26px !important;
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

    input:checked+.slider:after {
        -webkit-transform: translateX(0px);
        -ms-transform: translateX(0px);
        transform: translateX(0px);
        padding-left: 25px;
    }

    input:checked+.slider:before {
        background-color: #fff;
        -webkit-transform: translateX(38px);
        -ms-transform: translateX(38px);
        transform: translateX(38px);
    }

    input:checked+.slider:before {
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
        text-align: right;
        font-size: 16px;
        width: calc(100% - 25px);
        line-height: 30px;
        cursor: pointer;
    }

    input.rule-needed-score-col {
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
    }

    select:focus {
        border-color: #a6bad6;
        -webkit-box-shadow: inset 0 1px 1px rgba(0, 0, 0, .075), 0 0 5px #c9d5e6;
        -moz-box-shadow: inset 0 1px 1px rgba(0, 0, 0, .075), 0 0 5px #c9d5e6;
        box-shadow: inset 0 1px 1px rgba(0, 0, 0, .075), 0 0 5px #c9d5e6;
        color: #333;
    }

    button, [type="button"], [type="reset"], [type="submit"] {
        -webkit-appearance: button;
        align-content: stretch;
        padding: 8px 25px;
        margin-left: 500px;
    }

    textarea {
        overflow: auto;
        resize: vertical;
        width: 100%;
        font-size: 15px;
    }

    .btn-primary.disabled, .btn-primary:disabled {
     background-color: #afc9e5; 
     background-image: none;
}

</style>
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
          {{ $t('exoplatform.gamification.addbadge') }}
        </button>
      </div>
      <div
        id="collapseOne"
        :class="isShown ? '' : 'out'"
        aria-labelledby="headingOne"
        class="collapse show"
        data-parent="#accordionExample">
        <div class="card-body">
          <div
            id="myForm"
            class="UIPopupWindow uiPopup UIDragObject NormalStyle"
            style="width: 760px; z-index:1000000; position: relative; left: auto; margin: 0 20px; z-index: 1; max-width: 100%;margin: 0 auto;height: 100%;">
            <div class="popupHeader ClearFix">
              <a class="uiIconClose pull-right" @click.prevent="collapseButton(), onCancel()"></a>
              <span class="PopupTitle popupTitle"> {{ $t('exoplatform.gamification.addbadge') }}</span>
            </div>
            <div class="PopupContent popupContent">
              <form id="titleInputGroup">
                <label class="pt-0">{{ $t('exoplatform.gamification.title') }}:</label>
                <input
                  id="titleInput"
                  v-model="badge.title"
                  :placeholder="$t('badge.title.placeholder','Enter badge title')"
                  maxlength="50"
                  class="form-control"
                  required
                  type="text">
                </input>
              </form>

              <div id="descriptionInputGroup">
                <label id="descriptionInput" class="pt-0">{{
                  this.$t('exoplatform.gamification.gamificationinformation.domain.Description')
                }}:</label>
                <textarea
                  id="badgeDescription"
                  v-model="badge.description"
                  :max-rows="6"
                  :rows="3"
                  class="form-control"
                  maxlength="255"
                  :placeholder="$t('badge.description.placeholder','Enter description')">
                    </textarea>
              </div>

              <form id="neededScoreInputGroup">
                <label
                  id="Needed"
                  label-for="neededScoreInput"
                  class="pt-0">{{ $t('exoplatform.gamification.badge.score','Score') }}:</label>
                <input
                  id="neededScoreInput"
                  v-model="badge.neededScore"
                  type="number"
                  class="form-control"
                  required
                  :placeholder="$t('badge.score.placeholder','Enter badge needed score')">
              </form>
              <form id="iconInputGroup">
                <label for="iconInput" class="pt-0"> {{ $t('exoplatform.gamification.badge.icon','Icon') }}: </label>
                <button type="button" onclick="document.getElementById('iconInput').click()"> {{ $t('exoplatform.gamification.badge.icon.label','Choose file') }} </button>  {{ imageName }}
                <input
                  type="file"
                  id="iconInput"
                  style="display:none"
                  name="badge.icon"
                  accept="image/jpeg, image/png, image/gif"
                  placeholder="+"
                  @change="onFilePicked">
              </form>

              <form id="programSelectboxGroup">
                <label class="pt-0">{{ $t('exoplatform.gamification.gamificationinformation.Domain') }}:</label>

                <select v-model="badge.program" class="mb-4">
                  <option disabled>
                    {{ $t('exoplatform.gamification.selectdomain') }}
                  </option>
                  <option
                    v-for="program in programs"
                    :key="program.id"
                    :value="program">
                    {{
                      $t(`exoplatform.gamification.gamificationinformation.domain.${program.title}`,program.title)
                    }}
                  </option>
                </select>
              </form>
              <form id="enable">
                <label class="pt-0">{{ $t('exoplatform.gamification.enabled') }} :</label>
                <label class="uiSwitchBtn" @click="badge.enabled = !badge.enabled">
                  <input v-model="badge.enabled" type="checkbox">
                  <div class="slider round" @click="badge.enabled = !badge.enabled">
                    <span class="absolute-yes">{{ $t(`exoplatform.gamification.YES`,"YES") }}</span>
                  </div>
                  <span class="absolute-no" @click="badge.enabled = !badge.enabled">{{ $t('exoplatform.gamification.NO') }}</span>
                </label>
              </form>

              <div class="card-body-actions">
                <button
                  class="btn-primary"
                  type="submit"
                  :disabled="isDisabled"
                  @click="onSubmit(), collapseButton()">
                  {{ $t('exoplatform.gamification.gamificationinformation.domain.confirm') }}
                </button>
                <button
                  class="btn cancel"
                  @click.prevent="collapseButton(), onCancel()">
                  {{ $t('exoplatform.gamification.gamificationinformation.domain.cancel') }}
                </button>
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
import axios from 'axios';
import BootstrapVue from 'bootstrap-vue';
import 'bootstrap-vue/dist/bootstrap-vue.css';
Vue.use(BootstrapVue);
export default {
  props: ['badge', 'programs', 'errorType'],
  data: function () {
    return {
      formErrors: {},
      selectedFile: undefined,
      selectedFileName: '',
      dismissSecs: 5,
      isShown: false,
      imageName: '',
      imageFile: '',
      imageUrl: '',
      uploadId: '',
      date: new Date(),
      config: {
        format: 'YYYY-MM-DD',
        useCurrent: false,
      },
      dynamicRules: []
    };
  },
  computed: {
    isDisabled() {
      return !(this.isNotEmpty(this.badge.neededScore)&&this.isNotEmpty(this.badge.title)&&this.isNotEmpty(this.uploadId)&&this.badge.program!=null);
    }
  },
  watch: {
    'badge.id'() {
      this.formErrors = {};
      this.selectedFile = undefined;
      this.selectedFileName = this.badge.imageName;
    },
  },
  methods: {
    isNotEmpty(str){
      return (str!=null&&str!='');
    },

    collapseButton() {
      this.isShown = !this.isShown;
    },
    createBadge(badgeDTO) {

      if (this.uploadId != '') {badgeDTO.uploadId = this.uploadId;}
      axios.post('/portal/rest/gamification/badges/add', badgeDTO)

        .then(response => {
          this.addSuccess = true;
          this.updateMessage = 'added';
          this.$emit('submit', response.data);
          this.uploadId='';
        })
        .catch(e => {
          this.addError = true;
          this.uploadId='';
          if (e.response.status===304){
            this.errorType='badgeExists';
          } else {
            this.errorType='createBadgeError';
          }
          this.$emit('failAdd', this.badgeDTO, this.errorType);
        });
    },

    onCancel() {
      this.$emit('cancel');
      this.uploadId='';
    },
    onSubmit() {
      this.createBadge(this.badge);

      if (this.isShown) {
        this.closeAlert('.alert');
      }
    },

    getFormData(files) {
      const data = new FormData();
      [...files].forEach((file) => {
        data.append('data', file, file.name);
      });
      return data;
    },

    onFilePicked(e) {
      const files = e.target.files;
      if (files[0] !== undefined) {
        this.imageName = files[0].name;
        if (this.imageName.lastIndexOf('.') <= 0) {
          return;
        }
        const fr = new FileReader();
        fr.readAsDataURL(files[0]);
        fr.addEventListener('load', () => {
          this.imageUrl = fr.result;
          this.imageFile = files[0];
        });

        const MAX_RANDOM_NUMBER = 100000;
        const uploadId = Math.round(Math.random() * MAX_RANDOM_NUMBER);
        console.log(uploadId);
        const form = this.getFormData(files);
        axios.post(`/portal/upload?uploadId=${uploadId}&action=upload`, form, {
          headers: {
            'Content-Type': 'multipart/form-data'
          }
        }).then(response => {
          this.uploadId = uploadId;
        });
      } else {
        this.imageName = '';
        this.imageFile = '';
        this.imageUrl = '';
      }
    },

    confirm() {
      this.$modals.confirm({
        message: 'Confirm?',
        onApprove: () => {},
        onCancel: () => {},
      });
    },
    closeAlert(item) {
      setTimeout(function () {
        $(item).fadeOut('fast');
      }, 4000);

    },
  }
};
</script>

<style scoped>
form {
    margin-bottom: 24px;
}

.card.col label {
    display: block;
}

div#headingOne button.btn.btn-primary {
    margin: 15px 12px 5px;
    border-radius: 3px;

}

.collapse.show.out {
    display: none;
}

input.custom-file.b-form-file {
    display: inline-block;
    height: calc(2.25rem + 2px);
    position: relative;
}

h5.mt-0 {
    color: #4d5466;
    font-family: Helvetica, arial, sans-serif;
    line-height: 20px;
    font-size: 1.5em;
    text-transform: uppercase;
    font-weight: bold;
    text-align: center;
    padding: 20px 0px;
}

label {
    display: block;
    max-width: 100%;
    margin: 5px 0;
    font-weight: 700;
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

input[type="number"]:focus:invalid:focus,
input[type="date"]:focus:invalid:focus {
    border-color: #e9322d;
    -webkit-box-shadow: 0 0 6px #f8b9b7;
    -moz-box-shadow: 0 0 6px #f8b9b7;
    box-shadow: 0 0 6px #f8b9b7;
}

.card {
    position: relative;
    border-radius: 3px;
    background: #ffffff;
    margin-bottom: 20px;
    width: 100%;
    box-shadow: 0 1px 1px rgba(0, 0, 0, 0.1);
    margin: 10px auto;
    padding: 15px;
    flex-basis: 0;
    flex-grow: 1;
    max-width: 100%;
}

.require-msg {
    max-width: 100% !important;
    font-size: 14px;
}

msg.alert-dismissible.alert-danger {
    display: -webkit-inline-box;
    width: auto;
}

.close {
    float: right;
    font-size: 21px;
    font-weight: 700;
    line-height: 1;
    color: #bbb;
    text-shadow: 0 1px 0 #fff;
    filter: alpha(opacity=20);
    cursor: pointer;
}

.close:hover {
    color: #000;
}

.card label {
    display: block;
}

form {
    display: flex;
    flex-wrap: wrap;
}

button.btn.secondary {
    padding: 8px 25px;
    margin-left: 25px;
    border: 1px solid #e1e8ee !important;
    color: #4d5466;
    background-color: transparent !important;
}

div#headingOne:hover {
    background: transparent;
}

.custom-file-label:after {
    position: absolute;
    right: 5px;
    text-overflow: ellipsis;
}

.custom-file-label {
    position: relative !important;
    left: 0;
    z-index: 1;
    height: 36px;
    padding: 0 10px;
}

div#headingOne:hover {
    background: transparent;
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
.uiSwitchBtn {
    position: relative;
    display: inline-block;
    width: 60px;
    margin: auto;
    height: 55px;
}

.uiSwitchBtn input {
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

.slider:after {
    position: absolute;
    left: -20px;
    z-index: 1;
    content: "";
    font-size: 16px;
    text-align: left !important;
    line-height: 19px;
    padding-left: 0;
    width: 95px;
    height: 26px !important;
    color: #f9f9f9;
    background-color: #477ab3;

    border-radius: 100px;
    background-color: #476A9C;
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
    text-align: right !important;
    font-size: 16px;
    /* width: calc(100% - 25px); */
    line-height: 30px;
    cursor: pointer;
}

.collapse.show.out {
    display: none;
}

.uiPopup .popupContent select,
.modal.uiPopup .popupContent select {
    outline: none;
    border: 2px solid #e1e8ee;
    border-radius: 5px;
    box-shadow: none;
    height: 40px;
}

select:focus {
    border-color: #a6bad6;
    -webkit-box-shadow: inset 0 1px 1px rgba(0, 0, 0, .075), 0 0 5px #c9d5e6;
    -moz-box-shadow: inset 0 1px 1px rgba(0, 0, 0, .075), 0 0 5px #c9d5e6;
    box-shadow: inset 0 1px 1px rgba(0, 0, 0, .075), 0 0 5px #c9d5e6;
    color: #333;
}
.card-body .UIPopupWindow {
    background: #fff;
    overflow: auto;
}
.card-body .PopupContent {
    padding: 8px 10px 8px 15px;
}
.card-body .card-body-actions {
    display: flex;
    justify-content: center;
}
.card-body .card-body-actions button {
    margin: 8px 10px 8px 15px;
}
label.pt-0 {
    display: inline-block;
    width: 100%;
}

form#programSelectboxGroup {
    display: inline-block;
}
textarea#badgeDescription {
    border:2px solid #e1e8ee
}
form#enable {
    display: inline-block;
    margin-left: 10%;
}
</style>

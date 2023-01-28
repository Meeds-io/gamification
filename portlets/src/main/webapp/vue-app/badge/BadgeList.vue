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
  <b-container fluid>
    <b-row>
      <b-col sm="12">
        <div
          v-if="isdeleted"
          class="alert alert-success"
          v-on:="closeAlertt()">
          <i class="uiIconSuccess"></i>
        </div>

        <div class="uiSearchForm uiSearchInput searchWithIcon">
          <a
            class="advancedSearch"
            data-placement="bottom"
            rel="tooltip"
            title="">
            <i class="uiIconSearch uiIconLightGray"></i>
          </a>
          <input
            v-model="search"
            :placeholder="''"
            name="keyword"
            type="text"
            value="">
        </div>

        <div class="filter-bar">
          <select v-model="enabledFilter" class="mb-4">
            <option :value="null">{{ $t(`exoplatform.gamification.all`,"All") }}</option>
            <option :value="true">{{ $t(`exoplatform.gamification.enabled`,"Enabled") }}</option>
            <option :value="false">{{ $t(`exoplatform.gamification.disabled`,"Disabled") }}</option>
          </select>
        </div>

        <div
          id="collapseThree"
          :class="isEditShown ? '' : 'out'"
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
                <a class="uiIconClose pull-right" @click.prevent="collapseEditButton(), onCancel()"></a>
                <span class="PopupTitle popupTitle">  {{ $t('exoplatform.gamification.editbadge',"Edit badge") }}</span>
              </div>
              <div class="PopupContent popupContent">
                <form id="titleInputGroup">
                  <label class="pt-0">{{ $t('exoplatform.gamification.title',"Title") }}:</label>
                  <input
                    id="titleInput"
                    v-model="editedbadge.title"
                    class="form-control"
                    required
                    type="text">
                  </input>
                </form>
                <div id="descriptionInputGroup">
                  <label id="descriptionInput" class="pt-0">{{
                    this.$t('exoplatform.gamification.gamificationinformation.domain.Description',"Description")
                  }}:</label>
                  <textarea
                    id="badgeDescription"
                    v-model="editedbadge.description"
                    :max-rows="6"
                    :rows="3"
                    class="form-control"
                    :placeholder="this.$t('exoplatform.gamification.badge.edit.description.label')">
                    </textarea>
                </div>

                <form id="neededScoreInputGroup">
                  <label
                    id="Needed"
                    label-for="neededScoreInput"
                    class="pt-0">{{ this.$t('exoplatform.gamification.badge.score','Score') }}:</label>
                  <input
                    id="neededScoreInput"
                    v-model="editedbadge.neededScore"
                    type="number"
                    class="form-control"
                    required
                    :placeholder="this.$t('exoplatform.gamification.badge.edit.score.label')">
                </form>
                <form id="iconInputGroup">
                  <label for="iconInputBadge" class="pt-0"> {{ this.$t('exoplatform.gamification.badge.icon','Icon') }}: </label>
                  <button type="button" onclick="document.getElementById('iconInputBadge').click()"> {{ this.$t('exoplatform.gamification.badge.icon.label','Choose file') }} </button>  {{ this.imageName }}

                  <input
                    id="iconInputBadge"
                    type="file"
                    name="badge.icon"
                    style="display:none"
                    accept="image/jpeg, image/png, image/gif"
                    placeholder="+"
                    @change="onFilePicked">
                </form>

                <form id="domainSelectboxGroup">
                  <label class="pt-0">{{ $t('exoplatform.gamification.gamificationinformation.Domain') }}:</label>

                  <select v-model="editedbadge.domainDTO" class="mb-4">
                    <option disabled>
                      {{ $t('exoplatform.gamification.selectdomain') }}
                    </option>
                    <option
                      v-for="domain in domains"
                      :key="domain.id"
                      :value="domain">
                      {{
                        $t(`exoplatform.gamification.gamificationinformation.domain.${domain.title}`,domain.title)
                      }}
                    </option>
                  </select>
                </form>



                <form id="enabledswittch">
                  <label class="col-form-label pt-0">{{ $t(`exoplatform.gamification.enabled`,"Enabled") }}:</label>
                  <label class="uiSwitchBtn">
                    <input
                      v-model="editedbadge.enabled"
                      :disabled="editedbadge.domainDTO==null||!editedbadge.domainDTO.enabled"
                      type="checkbox">
                    <div class="slider round"><span class="absolute-yes">{{ $t(`exoplatform.gamification.YES`,"YES") }}</span></div>
                    <span class="absolute-no">{{ $t(`exoplatform.gamification.NO`,"NO") }}</span>
                  </label>
                  <div v-if="editedbadge.domainDTO==null||!editedbadge.domainDTO.enabled" class="error"> *{{ $t(`exoplatform.gamification.disabledDomainForBadges`,"This domain cannot be enabled as long as the related domain is disabled") }}.</div>
                </form>
 

                <b-row style="display: inherit;">
                  <b-col>
                    <button
                      class="btn secondary pull-right"
                      type="cancel"
                      @click.prevent="collapseEditButton(), onCancel()">
                      {{ $t('exoplatform.gamification.gamificationinformation.domain.cancel',"cancel") }}
                    </button>
                    <button
                      class="btn-primary pull-right"
                      type="submit"
                      :disabled="isDisabled"
                      @click.prevent="onSave">
                      {{ $t('exoplatform.gamification.gamificationinformation.domain.confirm',"confirm") }}
                    </button>
                  </b-col>
                </b-row>
              </div>
            </div> 
          </div>
        </div>

        <div
          id="collapseTwo"
          :class="isShown ? '' : 'out'"
          aria-labelledby="headingOne"
          class="collapse show"
          data-parent="#accordionExample"
          style=" transition: inherit;">
          <div class="card-body">
            <div
              id="myForm"
              class="UIPopupWindow uiPopup UIDragObject NormalStyle"
              style="width: 760px; z-index:1000000; position: relative; left: auto; margin: 0 20px; z-index: 1; max-width: 100%;margin: 0 auto;height: 100%;">
              <div class="popupHeader ClearFix">
                <div id="confirmLabel" class="PopupTitle popupTitle">
                  {{ $t('exoplatform.gamification.Confirmation') }}
                </div>

                <a class="uiIconClose pull-right" @click.prevent="collapseConfirm(badge)"></a>
              </div>
              <div class="PopupContent popupContent">
                <div class="media">
                  <div class="pull-left">
                    <i class="uiIconColorQuestion"></i>
                  </div>
                  <div class="media-body">
                    <p class="msg">
                      {{ $t('exoplatform.gamification.areyousure.deletebadge') }}
                    </p>
                  </div>
                </div>
                <div class="uiAction uiActionBorder">
                  <b-col>
                    <button
                      class="btn cancel pull-right"
                      type="submit"
                      @click.prevent="collapseConfirm(badge), onCancel()">
                      {{ $t('exoplatform.gamification.gamificationinformation.domain.cancel') }}
                    </button>


                    <button
                      class="btn-primary pull-right"
                      type="submit"

                      @click.prevent="onRemove(badge.id,badge.title),collapseConfirm(badge)">
                      {{ $t('exoplatform.gamification.gamificationinformation.domain.confirm') }}
                    </button>
                  </b-col>
                </div>
              </div>
            </div>
          </div>
        </div>
        <table class=" uiGrid table table-hover badge-table">
          <thead>
            <tr>
              <th class="badge-icon-col">{{ $t('exoplatform.gamification.icon') }}</th>
              <th class="badge-title-col">{{ $t('exoplatform.gamification.title') }}</th>
              <th class="badge-desc-col">
                {{
                  $t('exoplatform.gamification.gamificationinformation.domain.Description') }}
              </th>
              <th class="badge-nedded-score-col">{{ $t('exoplatform.gamification.neededscore') }}</th>
              <th class="badge-domain-col">
                {{ $t('exoplatform.gamification.gamificationinformation.Domain') }}
              </th>
                        
              <th class="badge-status-col">{{ this.$t('exoplatform.gamification.status') }}</th>
              <!--    <th class="badge-created-by-col">Created by</th> -->
              <th class="badge-action-col">{{ this.$t('exoplatform.gamification.action') }}</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="badge in filteredBadges">
              <td id="iconInputGroup" style="max-width: 100px;">
                <div style="z-index: 0;">
                  <img
                    thumbnail
                    fluid
                    :src="`/portal/rest/gamification/reputation/badge/${badge.id}/avatar`"
                    alt="Thumbnail"
                    class="m-1"
                    width="40"
                    height="40">
                </div>
              </td>
              <td class="badge-title-col">
                <div>{{ $t(`badge.title.${badge.title.replace(' ','')}`,badge.title) }}</div>
              </td>
              <td class="badge-desc-col">
                <div>{{ $t(`badge.description.${badge.title.replace(' ','')}_${badge.domain}`,badge.description) }}</div>
              </td>
              <td class="badge-needed-score-col">
                <div>
                  <div v-if="badge.neededScore >=1000">{{ badge.neededScore/1000 }} K</div>
                  <div v-if="badge.neededScore <1000"> {{ badge.neededScore }}</div>
                </div>
              </td>
              <td style="max-width: 105px;">
                <div v-if="badge.domainDTO != null">{{ $t(`exoplatform.gamification.gamificationinformation.domain.${badge.domainDTO.title}`,badge.domainDTO.title) }}</div>
              </td>


              <td class="badge-status-col">
                <div>
                  <label class="switch">
                    <input
                      v-model="badge.enabled"
                      type="checkbox"
                      disabled>
                    <div class="slider round"><span class="absolute-yes">{{ $t(`exoplatform.gamification.YES`,"YES") }}</span></div>
                    <span class="absolute-no">{{ $t('exoplatform.gamification.NO') }}</span>
                  </label>
                </div>
              </td>

              <td class="center actionContainer" style="z-index: 10;">
                <a
                  aria-controls="collapseThree"
                  aria-expanded="true"
                  data-target="#collapseThree"
                  data-toggle="collapse"
                  href=""
                  @click.prevent="collapseEditButton(badge)"> <i
                    class="uiIconEdit uiIconLightGray"></i></a>
                <a
                  v-if="badge.id"
                  v-b-tooltip.hover
                  class="actionIcon"
                  data-original-title="Supprimer"
                  data-placement="bottom"
                  href="#"
                  rel="tooltip"
                  title="Supprimer"
                  @click.prevent="collapseConfirm(badge)">
                  <i class="uiIconDelete uiIconLightGray"></i>
                </a>
              </td>
            </tr>
            <tr v-if="!badges.length || !filteredBadges.length " v-model="search">
              <td class="empty center" colspan="7">
                {{ $t(`exoplatform.gamification.ErrorBadgesMsg`) }}
              </td>
            </tr>
          </tbody>
        </table>
      </b-col>
    </b-row>
  </b-container>
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
  props: ['badges','domains'],
  data() {
    return {
      search: '',
      formErrors: {},
      selectedFile: undefined,
      selectedFileName: '',
      editedbadge: {},
      isEnabled: false,
      isdeleted: false,
      isShown: false,
      isEditShown: false,
      enabledFilter: null,
      filerlabel: 'all',
      editedEnabled: null,
      enabledMessage: '',
      imageName: '',
    };
  },
  computed: {
    filteredBadges() {
      return this.badges.filter(item => {
        return (this.$t(`badge.description.${item.title}`,item.description).toLowerCase().indexOf(this.search.toLowerCase()) > -1
                        || this.$t(`badge.title.${item.title}`,item.title).toLowerCase().indexOf(this.search.toLowerCase()) > -1
                        || item.neededScore.toString().toLowerCase().indexOf(this.search.toLowerCase()) > -1
                        || this.$t(`exoplatform.gamification.gamificationinformation.domain.${item.domainDTO.title}`,item.domainDTO.title).toLowerCase().indexOf(this.search.toLowerCase()) > -1)
                        && (this.enabledFilter === null || item.enabled === this.enabledFilter);

      });
    },
    isDisabled: function(){
      return !(this.isNotEmpty(this.editedbadge.neededScore)&&this.isNotEmpty(this.editedbadge.title)&&this.editedbadge.domainDTO!=null);

    }
  },
  watch: {
    'badge.id'() {
      this.formErrors = {};
      this.selectedFile = undefined;
      this.selectedFileName = this.badge.imageName;
    }
  },
       
  methods: {
    isNotEmpty(str){
      return (str!=null&&str!='');
    },
    onSave() {
      this.$emit('save', this.editedbadge);
      this.editedbadge= {};
      this.isEditShown = !this.isEditShown;               
    },

    onImageChanged(event) {
      this.selectedFile = event.target.files[0];
      this.selectedFileName = event.target.files[0].name;
    },
    onCancel(badge) {
      this.editedbadge= {};
    },
    onRemove(id, title) {
      this.$emit('remove', id, title);
    },
    change() {
      console.log('filechange');
    },
    collapseConfirm(badge) {
      this.badge = badge;
      this.isShown = !this.isShown;
      if (this.isShown) {
        this.closeAlertt('.alert');
      }
    },
    collapseEditButton(badge) {
      if ( badge ) {
        if (badge?.domainDTO && this.domains?.length) {
          badge.domainDTO = this.domains.find(domain => domain.title === badge.domainDTO.title);
        }
        this.badge = badge;
        this.editedbadge=badge;
        this.editedbadge.description =  this.$t(`badge.description.${this.editedbadge.title.replace(' ','')}_${this.editedbadge.domain}`,this.editedbadge.description) ;
        this.editedbadge.title =  this.$t(`badge.title.${this.editedbadge.title.replace(' ','')}`,this.editedbadge.title) ;
      }
      this.isEditShown = !this.isEditShown;
    },
    closeAlertt(item) {
      setTimeout(function () {
        $(item).fadeOut('fast');
      }, 8000);
    } ,         
    getFormData(files) {
      const data = new FormData();
      [...files].forEach((file) => {
        data.append('data', file, file.name); // currently only one file at a time
      });
      return data;
    },


    onFilePicked (e) {
      const files = e.target.files;
      if (files[0] !== undefined) {
        this.imageName = files[0].name;
        if (this.imageName.lastIndexOf('.') <= 0) {
          return;
        }
        const fr = new FileReader ();
        fr.readAsDataURL(files[0]);
        fr.addEventListener('load', () => {
          this.imageUrl = fr.result;
          this.imageFile = files[0];
        });

        const MAX_RANDOM_NUMBER = 100000;
        const uploadId = Math.round(Math.random() * MAX_RANDOM_NUMBER);
        console.log(uploadId);
        const form = this.getFormData(files);
        axios.post(`/portal/upload?uploadId=${uploadId}&action=upload`, form,
          {
            headers: {
              'Content-Type': 'multipart/form-data'
            }
          }).then(response => {
          this.editedbadge.uploadId=uploadId;
        });

      } else {
        this.imageName = '';
        this.imageFile = '';
        this.imageUrl = '';
      }
    }
  },
};
</script>
<style scoped>
    .container-fluid {
        display: table;
    }
    .table {
        position: relative;
        border-radius: 3px;
        background: #fff;
        margin-bottom: 20px;
        width: 96%;
        box-shadow: 0 1px 1px rgba(0,0,0,.1);
        margin: 30px auto 0;
        margin-bottom: 30px;
    }
    .table td,
    .table th {
        padding: 8px;
        line-height: 1.42857143;
        vertical-align: top;
        text-align: center;
    }
    .uiGrid.table tr td {
        padding: 5px;
    }
    .table-hover tbody tr:hover {
        cursor: pointer;
    }
    .table-striped>tbody>tr:nth-of-type(odd) {
        background-color: #f9f9f9;
    }
    .uiGrid.table td, .uiGrid.table th {
        border-left: none;
    }
    .uiGrid.table thead {
        border: 1px solid #e1e8ee;
    }
    .uiGrid.table {
        border: none;
    }
    /* switch */
    .switch, .uiSwitchBtn {
        position: relative;
        display: inline-block;
        width: 53px;
        height: 32px;
        /* zoom: 30%; */
        top: 0.4rem;
    }
    label.uiSwitchBtn {
        width: 60px !important;
    }


    .switch input, .uiSwitchBtn input {
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
        font-size: 13px;
        text-align: left !important;
        line-height: 19px;
        padding-left: 0;
        width: 95px;
        height: 26px !important;
        color: #f9f9f9;
        background-color: #477ab3;
        background-image: -moz-linear-gradient(top, #476A9C, #2f5e92);
        background-image: -webkit-gradient(linear, 0 0, 0 100%, from(#476A9C), to(#2f5e92));
        background-image: -webkit-linear-gradient(top, #476A9C, #2f5e92);
        background-image: -o-linear-gradient(top, #476A9C, #2f5e92);
        background-image: linear-gradient(to bottom, #476A9C, #2f5e92);
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
        left: 18px;
        color: DarkGrey;
        text-align: right !important;
        font-size: 16px;
        width: calc(100% - 25px);
        line-height: 30px;
        cursor: pointer;
    }
    select.mb-4 {
        max-width: 115px;
        margin: 10px auto;
        height: 35px;
    }
    i.uiIconClose.uiIconBlue {
        zoom: 163%;
        vertical-align: super;
        opacity: 1;
        line-height: inherit;
    }
    /* input icon */
    .custom-file {
        position: relative;
        display: inline-block;
        width: 40%;
        height: 38px;
        margin-bottom: 0;
    }
    td#iconInputGroup {
        max-width: 145px;
    }
    td#iconInputGroup input {
        width: auto;
        max-width: 65px;
    }
    input.badge-needed-score-col {
        width: 60px;
        text-align: center;
    }
    .table td {
        vertical-align: middle;
    }
    /*edit Mode
    td input {
        max-width: min-content;
    } */
    input[type="text"] {
        height: 40px;
        margin: auto;
    }
    .custom-file-input:lang(en) ~ .custom-file-label::after {
        content: "+";
        display: none !important;
    }
    td#iconInputGroup input {
        max-width: 70px;
        width: 70px;
    }
    .custom-file {
        margin-left: 58px !important;
        z-index: 0;
    }
    .custom-file-input {
        width: 50px;
        max-width: 50px;
    }
    td.badge-title-col {
        max-width: 210px;
    }
    img.m-1 {
        border-radius: 50%;
        width: 40px;
        height: 40px;
    }
    label.custom-file-label::after {
        content: '+'!important;
        height: 35px;
        width: 35px;
        border-radius: 50%;
    }
    .uiSearchInput.searchWithIcon {
        display: flex;
        flex-direction: row-reverse;
        float: right;
        margin-top: 18px;
    }
    i.uiIconSearch.uiIconLightGray {
        position: relative;
        float: left;
    }
    @media (max-width: 416px) {
        .uiSearchInput.searchWithIcon {
            max-width: 45%;
            margin-left: 5px;
        }
    }

    @media (max-width: 340px) {
        .uiSearchInput.searchWithIcon {
            max-width: 35%;
            margin-left: 5px;
        }
    }

    .collapse.show.out {
        display: none;
    }

    .collapse {
        top: 15px;
    }

    div#collapseTwo {
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

        div#collapseThree {
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


    .alert-success {
        margin-top: 124px !important;
        margin-left: 38%;
        transform: translateX(-50%);
    }
    .filterWithIcon {
        display: flex;
        flex-direction: row-reverse;
        float: right;
        margin: 10px;
        font-size: 15px;
        height: 35px;
        border: Solid 2px #e1e8ee;
        border-radius: 5px;
        box-shadow: none;
        width: 90px;
        text-overflow: ellipsis;
        margin-top: 18px;
    }
    .action-bar.dropdown.filterWithIcon> a.actionIcon.dropdown-toggle {
        box-shadow: none;
        border: none;
        text-decoration: none;
        margin: auto;
        width: 100%;
        border-radius: 3px;
        background-color: transparent;
    }
    button.btn.cancel.pull-right {
        border: 1px solid #e1e8ee !important;
        color: #4d5466;
        background: transparent !important;
    }
    button.btn-primary.pull-right {
        border-radius: 0.25rem;
    }

    button.btn.secondary.pull-right {
        padding: 8px 25px;
        margin-left: 25px;
        border: 1px solid #e1e8ee !important;
        color: #4d5466;
        background-color: transparent !important;
    }
    div#collapseThree label {
        font-weight: 600;
        margin-right: 20px;
    }
    form#domainSelectboxGroup, form#enabledswittch {
        width: 40%;
        display: inline-block;
        margin: 15px 0;
    }

    .filter-bar select.mb-4 {
        margin: 18px 12px;
        padding: 0 10px;
        border: Solid 2px #e1e8ee;
        height: 40px;
        border-radius: 5px;
        width: auto;
        display: flex;
        flex-direction: row-reverse;
        outline: none;
        float: right;
    }
    .btn-primary.disabled, .btn-primary:disabled {
        background-color: #afc9e5; 
        background-image: none;
    }
    form#domainSelectboxGroup select.mb-4 {
        vertical-align: sub;
    }
    input#neededScoreInput {
        padding: 0 10px;
        border: Solid 2px #e1e8ee;
        border-radius: 5px;
        box-shadow: none;
        outline: none;
        height: 40px;
    }
    textarea.form-control {
        height: auto;
        padding: 0 10px;
        border: Solid 2px #e1e8ee;
        border-radius: 5px;
        box-shadow: none;
        outline: none;
    }
    form {
        margin: 5px 0;
    }
</style>

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
          v-on:="closeAlert()">
          <button
            aria-label="Close"
            class="close"
            data-dismiss="alert"
            type="button">
          </button>
          <i class="uiIconSuccess"></i>
          {{ this.$t('exoplatform.gamification.successdelete') }}
        </div>
        <v-div
            class="pa-4 mb-4">
          <v-spacer />
          <div class="uiSearchForm uiSearchInput searchWithIcon">
            <input
                v-model="search"
                class="ignore-vuetify-classes"
                :placeholder="this.$t('exoplatform.gamification.gamificationinformation.domain.search')"
                name="keyword"
                type="text"
                value="">
            <a
                class="advancedSearch"
                data-placement="bottom"
                rel="tooltip"
                title="">
              <i class="uiIconSearch uiIconLightGray"></i>
            </a>
          </div>

          <div class="filter-bar">
            <select v-model="enabledFilter" class="mb-4">
              <option :value="null">{{ $t(`exoplatform.gamification.all`,"All") }}</option>
              <option :value="true">{{ $t(`exoplatform.gamification.enabled`,"Enabled") }}</option>
              <option :value="false">{{ $t(`exoplatform.gamification.disabled`,"Disabled") }}</option>
            </select>
          </div>
        </v-div>

        <div
          id="collapseTwo"
          :class="isShown ? '' : 'out'"
          aria-labelledby="headingOne"
          class="collapse show"
          data-parent="#accordionExample">
          <div class="card-body">
            <div
              id="myForm"
              class="UIPopupWindow uiPopup UIDragObject NormalStyle myForm" >
              <div class="popupHeader ClearFix">
                <a class="uiIconClose pull-right" @click.prevent="collapseButtonn(rule)"></a>

                <span class="PopupTitle popupTitle">{{ this.$t('exoplatform.gamification.gamificationinformation.rule.popupedit') }}</span>
              </div>
              <div class="PopupContent popupContent">
                <form id="titleInputGroup">
                  <label class="col-form-label pt-0">{{ $t(`exoplatform.gamification.gamificationinformation.Event`) }}:</label>


                  <select
                    v-model="editedrule.event"
                    class="mb-4 select-event"
                    required>
                    <option
                      disabled
                      selected
                      value="null">
                      {{ $t(`exoplatform.gamification.selectevent`) }}
                    </option>
                    <option v-for="option in events" :value="option">
                      {{ $t(`exoplatform.gamification.gamificationinformation.rule.title.${option}`,option) }}
                    </option>
                  </select>
                </form>

                <form id="descriptionInputGroup">
                  <label class="col-form-label pt-0">{{ $t(`exoplatform.gamification.gamificationinformation.domain.Description`) }}:</label>
                  <textarea
                    id="ruleDescription"
                    class="ignore-vuetify-classes"
                    v-model="editedrule.description"
                    :max-rows="6"
                    :rows="3"
                    :placeholder=" $t(`exoplatform.gamification.gamificationinformation.domain.edit.label.description`) ">
                  </textarea>
                </form>
                <b-form id="scoreInputGroup">
                  <label
                    id="scoreInputGroup"
                    class="col-form-label pt-0"
                    for="scoreInput">{{ $t(`exoplatform.gamification.score`) }}:</label>
                  <input
                    id="scoreDescription"
                    v-model="editedrule.score"
                    placeholder="Enter rule's score"
                    required
                    type="number">

                  <div
                    v-if="formErrors.score"
                    class="alert alert-danger require-msg"
                    :show="dismissCountDown"
                    dismissible
                    variant="danger"
                    @dismiss-count-down="countDownChanged"
                    @dismissed="dismissCountdown=0">
                    Rule score is required please enter a score
                  </div>
                </b-form>
                <form class="switchEnabled">
                  <label class="col-form-label pt-0" max-rows="6">{{ $t(`exoplatform.gamification.enabled`) }}:</label>
                  <label class="switch">
                    <input
                      v-model="editedrule.enabled"
                      :disabled="editedrule.domainDTO==null||!editedrule.domainDTO.enabled"
                      type="checkbox">
                    <div class="slider round"><span class="absolute-yes">{{ $t(`exoplatform.gamification.YES`,"YES") }}</span></div>
                    <span class="absolute-no">{{ $t(`exoplatform.gamification.NO`) }}</span>
                  </label>
                  <div v-if="editedrule.domainDTO==null||!editedrule.domainDTO.enabled" class="error"> *{{ $t(`exoplatform.gamification.disabledDomainForRules`,"This rule cannot be enabled as long as the related domain is disabled") }}.</div>
                </form>
                <form id="domainSelectboxGroup">
                  <label class="col-form-label pt-0">{{ $t(`exoplatform.gamification.gamificationinformation.Domain`) }}:</label>
                  <select v-model="editedrule.domainDTO" required>
                    <option disabled value="null">
                      {{ $t(`exoplatform.gamification.selectdM`) }}
                    </option>
                    <option v-for="option in domains" :value="option">
                      {{ domainTitle(option.title) }}
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
                        class="ignore-vuetify-classes btn mx-4"
                        outlined
                        @click.prevent="collapseButtonn(editedrule), onCancel()">
                        {{ this.$t('exoplatform.gamification.gamificationinformation.domain.cancel') }}
                      </btn>
                      <btn
                        class="ignore-vuetify-classes btn btn-primary mx-1"
                        type="submit"
                        :disabled="isBottonDisabled"
                        @click.prevent="onSave(editedrule),collapseButtonn(editedrule)">
                        {{ this.$t('exoplatform.gamification.gamificationinformation.domain.confirm') }}
                      </btn>
                    </div>
                    </v-toolbar>
                </div>
              </div>
            </div>
          </div>
        </div>


        <div
          id="collapseOne"
          :class="isShowndeleted ? '' : 'out'"
          aria-labelledby="headingOne"
          class="collapse show"
          data-parent="#accordionExample"
          style=" transition: inherit;">
          <div class="card-body">
            <div
              id="myForm2"
              class="UIPopupWindow uiPopup UIDragObject NormalStyle myForm">
              <div class="popupHeader ClearFix">
                <a class="uiIconClose pull-right" @click.prevent="collapseConfirm(rule)"></a>

                <span class="PopupTitle popupTitle">{{ this.$t('exoplatform.gamification.rule.popupdelete') }}</span>
              </div>
              <div class="PopupContent popupContent">
                <div class="media">
                  <div class="pull-left">
                    <i class="uiIconColorQuestion"></i>
                  </div>
                  <div class="media-body">
                    <p class="msg">
                      {{ this.$t('exoplatform.gamification.areyousure.deleterule') }}
                    </p>
                  </div>
                </div>
                <v-toolbar
                    color="transparent"
                    flat
                    class="pa-4 mb-4">
                  <v-spacer />
                  <div class="d-flex">
                    <btn
                        class="ignore-vuetify-classes btn mx-4"
                        outlined
                        @click.prevent="collapseConfirm(rule), onCancel()">
                      {{ this.$t('exoplatform.gamification.gamificationinformation.domain.cancel') }}
                    </btn>
                    <btn
                        class="btn btn-primary"
                        @click.prevent="onRemove(rule.id,rule.title),collapseConfirm(rule)">
                      {{ this.$t('exoplatform.gamification.gamificationinformation.domain.confirm') }}
                    </btn>
                  </div>
                </v-toolbar>
              </div>
            </div>
          </div>
        </div>


        <table
          striped
          hover
          class="uiGrid table table-hover rule-table">
          <thead>
            <tr>
              <th class="rule-name-col ruleEvent">{{ $t(`exoplatform.gamification.gamificationinformation.Event`) }}</th>
              <th class="rule-enable-col">{{ $t(`gamification.type`) }}</th>
              <th class="rule-desc-col ruleEvent">
                {{ $t(`exoplatform.gamification.gamificationinformation.domain.Description`) }}
              </th>
              <th class="rule-price-col">{{ $t(`exoplatform.gamification.score`) }}</th>
              <th class="rule-area-col">
                {{ $t(`exoplatform.gamification.gamificationinformation.Domain`) }}
              </th>
              <th class="rule-enable-col">{{ $t(`exoplatform.gamification.enabled`) }}</th>
              <th class="rule-action-col">{{ $t(`exoplatform.gamification.action`) }}</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="rule in filteredRules">
              <td class="ruleText">
                <span v-if="rule && rule.type === 'AUTOMATIC'">
                  {{ eventTitle(rule.event,rule.title) }}
                </span>
                <span v-if="rule && rule.type === 'MANUAL'"> {{ rule.title }} </span>
              </td>
              <td>
                <span v-if="rule && rule.type === 'AUTOMATIC'"> {{ $t('gamification.label.auto') }} </span>
                <span v-if="rule && rule.type === 'MANUAL'"> {{ $t('gamification.label.manual') }} </span>
              </td>
              <td >
                <div class="ruleText mx-2" >
                  <span v-if="rule && rule.type === 'AUTOMATIC'">
                    {{ description(rule.description,rule.event,rule.title) }}
                  </span>
                  <span v-if="rule && rule.type === 'MANUAL'" v-sanitized-html="rule && rule.description" />
                </div>
              </td>
              <td>
                <div>{{ rule.score }}</div>
              </td>
              <td style="max-width: 115px;">
                <div v-if="rule.domainDTO != null">
                  {{ domainTitle(rule.domainDTO.title)}}
                </div>
              </td>
              <td>
                  <label class="switch">
                    <input
                      :checked= "rule.enabled || (!rule.enabled && getRuleStatus(rule.startDate ,rule.endDate) === 'STARTED')"
                      disabled
                      type="checkbox">
                    <div class="slider round"><span class="absolute-yes">{{ $t(`exoplatform.gamification.YES`,"YES") }}</span></div>
                    <span class="absolute-no" data-value="rule.enabled">{{ $t(`exoplatform.gamification.NO`) }}</span>
                  </label>
              </td>

              <td class="center actionContainer defaultCursor">
                <v-btn
                    icon
                    small
                    :disabled="rule && rule.type === 'MANUAL'"
                    @click.prevent="collapseButtonn(rule)">
                  <i
                    class="uiIconEdit uiIconLightGray"
                    :class="rule && rule.type === 'MANUAL' ? 'disabledButton' : ''"
                  ></i>
                </v-btn>
                <v-btn
                  icon
                  small
                  class="actionIcon"
                  rel="tooltip"
                  :disabled="rule && rule.type === 'MANUAL'"
                  @click.prevent="collapseConfirm(rule)">
                  <i
                    class="uiIconDelete uiIconLightGray"
                    :class="rule && rule.type === 'MANUAL' ? 'disabledButton' : ''"
                  ></i>
                </v-btn>
              </td>
            </tr>
            <tr v-if="!rules.length || !filteredRules.length " v-model="search">
              <td class="empty center" colspan="6">
                {{ $t(`exoplatform.gamification.ErrorRulesMsg`) }}
              </td>
            </tr>
          </tbody>
        </table>
      </b-col>
    </b-row>
  </b-container>

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
    domain: {
      type: Object,
      default: function() {
        return {};
      },
    },
    rules: {
      type: Array,
      default: function() {
        return [];
      },
    },
    domains: {
      type: Array,
      default: function() {
        return [];
      },
    },
    events: {
      type: Array,
      default: function() {
        return [];
      },
    },
  },

  data() {
    return {
      search: '',
      formErrors: {},
      editedrule: {},
      isdeleted: false,
      isShown: false,
      isShowndeleted: false,
      enabledFilter: null,
      isEnabled: false,
      editedEnabled: null,
      enabledMessage: '',
      filerlabel: 'all',
      showMenu: false,
    };
  },

  computed: {
    filteredRules() {
      return this.rules.filter(item => {
        return (( item.title && item.description && item.event &&
          this.description(item.description,item.event ,item.title).toLowerCase().indexOf(this.search.toLowerCase()) > -1 || item.event && item.title &&
          this.eventTitle(item.event ,item.title).toLowerCase().indexOf(this.search.toLowerCase()) > -1 || item.domainDTO && item.domainDTO.title &&
                    this.domainTitle(item.domainDTO.title).toLowerCase().indexOf(this.search.toLowerCase()) > -1||
                    item.score.toString().toLowerCase().indexOf(this.search.toLowerCase()) > -1)
                    && (this.enabledFilter === null || ( item.enabled === this.enabledFilter && item.type === 'AUTOMATIC' ) ||
                    ( this.enabledFilter && item.type === 'MANUAL' && this.getRuleStatus(item.startDate ,item.endDate) === 'STARTED') ||
                    ( !this.enabledFilter && item.type === 'MANUAL' && this.getRuleStatus(item.startDate ,item.endDate) !== 'STARTED'))
        );
      });
    },
    isBottonDisabled(){
      return !(this.isNotEmpty(this.editedrule.event)&&this.isNotEmpty(this.editedrule.score)&&this.editedrule.domainDTO!=null);
    },
  },


  methods: {
    onEdit(rule) {
      this.rule = rule;
      this.editedrule = rule;
    },
    isNotEmpty(str){
      return (str!=null&&str !== '');
    },
    onRemove(id, title) {
      this.$emit('remove', id, title);
      this.isdeleted = true;
    },
    onSave(rule) {
      this.$emit('save', rule);
      this.editedrule = {};
      if (this.isShown) {
        this.closeAlert('.alert');
      }
    },
    onCancel() {
      this.editedrule = {};
    },
    collapseConfirm(rule) {
      this.rule = rule;
      this.isShowndeleted = !this.isShowndeleted;
      if (this.isShowndeleted) {
        this.closeAlert('.alert');
      }
    },

    onSubmit(rule) {
      this.updateDomain(this.editedrule);
      this.collapseButtonn(rule);
    },
    collapseButtonn(rule) {
      this.editedrule = rule;
      this.isShown = !this.isShown;
      this.editedrule.description = this.description(this.editedrule.description,this.editedrule.event,this.editedrule.title);
    },
    closeAlert(item) {
      setTimeout(function () {
        $(item).fadeOut('fast');
      }, 4000);
    },
    domainTitle(title){
      if (!this.$t(`exoplatform.gamification.gamificationinformation.domain.${title}`).includes('exoplatform.gamification.gamificationinformation.domain')){
        return this.$t(`exoplatform.gamification.gamificationinformation.domain.${title}`) ;
      } else {
        return title;
      }
    },
    eventTitle(event,title){
      if (!this.$t(`exoplatform.gamification.gamificationinformation.rule.title.${event}`).includes('exoplatform.gamification.gamificationinformation.rule.title')){
        return this.$t(`exoplatform.gamification.gamificationinformation.rule.title.${event}`) ;
      } else if (!this.$t(`exoplatform.gamification.gamificationinformation.rule.title.${title}`).includes('exoplatform.gamification.gamificationinformation.rule.title')){
        return this.$t(`exoplatform.gamification.gamificationinformation.rule.title.${title}`) ;
      } else {
        return event;
      }
    },
    description(description,event,title){
      if (!this.$t(`exoplatform.gamification.gamificationinformation.rule.description.${event}`).includes('exoplatform.gamification.gamificationinformation.rule.description')){
        return this.$t(`exoplatform.gamification.gamificationinformation.rule.description.${event}`) ;
      } else if (!this.$t(`exoplatform.gamification.gamificationinformation.rule.description.${title}`).includes('exoplatform.gamification.gamificationinformation.rule.description')){
        return this.$t(`exoplatform.gamification.gamificationinformation.rule.description.${title}`) ;
      } else {
        return description;
      }
    },
    getRuleStatus(startDate, endDate){
      const status = {
        NOTSTARTED: 'NOTSTARTED',
        STARTED: 'STARTED',
        ENDED: 'ENDED'
      };
      const currentDate = new Date();
      startDate = new Date(startDate);
      endDate = new Date(endDate);
      if (startDate.getTime() > currentDate.getTime() && endDate.getTime() > currentDate.getTime()) {
        return status.NOTSTARTED;
      } else if ((startDate.getTime() < currentDate.getTime() && endDate.getTime() > currentDate.getTime()) || (this.getFromDate(endDate) ===  this.getFromDate(currentDate))) {
        return status.STARTED;
      } else if (endDate.getTime() < currentDate.getTime() && startDate.getTime() < currentDate.getTime()) {
        return status.ENDED;
      }
    },
    getFromDate(date) {
      const lang = eXo.env.portal.language;
      const options = { month: 'long' };
      const day = String(date.getDate());
      const year = String(date.getFullYear());
      return `${date.toLocaleDateString(lang || 'en', options)} ${day}, ${year}` ;
    }
  }
};
</script>
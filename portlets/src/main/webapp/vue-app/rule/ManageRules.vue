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
  <section>
    <div
      v-show="isadded || addSuccess"
      class="alert alert-success"
      v-on:="closeAlert()">
      <i class="uiIconSuccess"></i>
      {{ this.$t('exoplatform.gamification.rule') }} {{ updateMessage }}
      {{ this.$t('exoplatform.gamification.successfully') }}
    </div>

    <div
      v-show="addError"
      class="alert alert-error"
      v-on:="closeAlert()">
      <i class="uiIconError"></i>
      {{ this.$t(`exoplatform.gamification.${errorType}`) }}
    </div>

    <save-rule-form
      :rule="ruleInForm"
      :domains="domains"
      :events="events"
      :error-type="errorType"
      @sucessAdd="onRuleCreated"
      @failAdd="onRuleFail"
      @cancel="resetRuleInForm" />
    <rule-list
      :domain="domain"
      :domains="domains"
      :events="events"
      :rule="ruleInForm"
      :rules="rules"
      @remove="onRemoveClicked"
      @save="onSaveClicked" />
  </section>
</template>
<script>
/* eslint-disable */
import Vue from 'vue';
import RuleList from './RuleList';
import SaveRuleForm from './SaveRuleForm';
import BootstrapVue from 'bootstrap-vue';
import axios from 'axios';
import 'bootstrap/dist/css/bootstrap.css';
import 'bootstrap-vue/dist/bootstrap-vue.css';

Vue.use(BootstrapVue);
const initialData = () => {
  return {
    ruleInForm: {
      id: null,
      title: '',
      description: '',
      score: null,
      enabled: true,
      area: '',
      lastModifiedBy: '',
      lastModifiedDate: null
    },
    addSuccess: false,
    addError: false,
    updateMessage: '',
    rules: [],
    domains: [],
    domain: '',
    events: [],
    isadded: false,
    isShown: false,
    errorType: 'errorrule'
  };
};
export default {
  components: {
    RuleList,
    SaveRuleForm
  },
  data: initialData,
  created() {
    this.getRules(); 
    this.getDomains(); 
    this.getEvents();
  },
  methods: {
    getRules() {
      axios.get('/portal/rest/gamification/rules/all')
        .then(response => {
          this.rules = response.data;
        })
        .catch(e => {
          this.addError = true;
          this.errorType='getRulesError';
        });
    },
    getDomains() {
      axios.get('/portal/rest/gamification/domains')
        .then(response => {
          this.domains = response.data;
        })
        .catch(e => {
          console.log(e);
        });
    },
    getEvents() {
      axios.get('/portal/rest/gamification/api/v1/events')
        .then(response => {
          this.events = response.data;
        })
        .catch(e => {
          console.log(e);
        });
    },
    countDownChanged(dismissCountDown) {
      this.dismissCountDown = dismissCountDown;
    },
    resetRuleInForm() {
      this.ruleInForm = initialData().ruleInForm;
    },
    onSaveClicked (rule) {
      this.updateRule(rule);
      this.isShown = !this.isShown;
    },

    onRuleCreated(rule) {
      this.isadded = true;
      this.addSuccess = true;
      this.updateMessage = 'added';
      this.rules.push(rule);
      this.resetRuleInForm();
    },

    onRuleFail(rule,errorType) {                
      this.addError = true;
      this.errorType=errorType;
      this.resetRuleInForm();
      this.dismissCountDown = 15;
    },

    onRemoveClicked(ruleId, ruleTitle) {
      const index = this.rules.findIndex((p) => p.id === ruleId);
      axios.delete(`/portal/rest/gamification/rules/delete/${ruleId}`)
        .then(response => {
          this.rules.splice(index, 1);
        })
        .catch(e => {
          this.getRules();
          this.errorType='deleteRuleError';
        });
      if (ruleId === this.ruleInForm.id) {
        this.resetRuleInForm();
      }
    },
    updateRule(ruleDTO) {
      axios.put('/portal/rest/gamification/rules/update', ruleDTO)
        .then(response => {
          this.addSuccess = true;
          this.updateMessage = 'updated';
          this.rules.push(rule);
          this.dismissCountDown = 15;
        })
        .catch(e => {
          if (e.response.status===304){
            this.errorType='ruleExists';
          } else {
            this.errorType='updateRuleError';
          }
          this.addError = true;
          this.getRules();
        });
    }
  }
};
</script>
<style scoped>
    .alert {
        left: 50%;
        transform: translateX(-50%);
        max-width: 40%;
        top: 35%;
        display: inline-block;
    }
    .alert-success {
        background: #6ccbae;
        border-color: #2eb58c;
        color: #333333;
    }

    section {
        background: white;
        margin: 14px;
        border: 1px solid #cccccc;
        border-radius: 0 0 3px 3px;
        box-shadow: 0px 0px 1px rgba(0, 0, 0, 0.17);
        -webkit-border-radius: 0 0 3px 3px;
        -moz-border-radius: 0 0 3px 3px;
        -webkit-box-shadow: 0px 0px 1px rgba(0, 0, 0, 0.17);
        -moz-box-shadow: 0px 0px 1px rgba(0, 0, 0, 0.17);
    }
</style>
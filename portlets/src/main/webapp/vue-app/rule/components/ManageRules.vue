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

    <gamification-save-rule
      :rule="ruleInForm"
      :domains="domains"
      :events="events"
      :error-type="errorType"
      @sucessAdd="onRuleCreated"
      @failAdd="onRuleFail"
      @cancel="resetRuleInForm" />
    <gamification-rule-list
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


export default {
  data: () => ({
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
  }),
  created() {
    this.getRules(); 
    this.getDomains(); 
    this.getEvents();
  },
  methods: {
    getRules() {
      this.$RuleServices.getRules()
        .then(rules => {
          this.rules = rules;
        })
        .catch(() => {
          this.addError = true;
          this.errorType='getRulesError';
        });
    },
    getDomains() {
      this.$RuleServices.getDomains()
        .then(domains => {
          this.domains = domains;
        });
    },
    getEvents() {
      this.$RuleServices.getEvents()
        .then(events => {
          this.events = events;
        });
    },
    countDownChanged(dismissCountDown) {
      this.dismissCountDown = dismissCountDown;
    },
    resetRuleInForm() {
      this.ruleInForm =  {
        id: null,
        title: '',
        description: '',
        score: null,
        enabled: true,
        area: '',
        lastModifiedBy: '',
        lastModifiedDate: null
      };
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

    onRemoveClicked(ruleId) {
      const index = this.rules.findIndex((p) => p.id === ruleId);
      this.$RuleServices.deleteRule(ruleId)
        .then(() => {
          this.rules.splice(index, 1);
        })
        .catch(() => {
          this.getRules();
          this.errorType='deleteRuleError';
        });
      if (ruleId === this.ruleInForm.id) {
        this.resetRuleInForm();
      }
    },
    updateRule(ruleDTO) {
      this.$RuleServices.updateRule(ruleDTO)
        .then(() => {
          this.addSuccess = true;
          this.updateMessage = 'updated';
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
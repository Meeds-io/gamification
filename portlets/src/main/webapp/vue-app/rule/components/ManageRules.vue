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
      <v-flex v-if="hasMore" class="pb-5 align-center">
        <v-btn
            class="btn mx-auto"
            @click="loadMore">
          {{ $t('homepage.loadMore') }}
        </v-btn>
      </v-flex>
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
    errorType: 'errorrule',
    filter: 'ALL',
    limit: 20,
    pageSize: 20,
    totalSize: 0,
    query: null,
  }),
  computed: {
    hasMore() {
      return this.limit < this.totalSize;
    },
  },
  created() {
    this.$root.$on('rules-filter-applied', (filter) => this.filter = filter);
    this.$root.$on('rules-search-applied', (query) => this.query = query);
    this.retrieveRules();
    this.getDomains(); 
    this.getEvents();
  },
  watch: {
    filter() {
      this.retrieveRules();
    },
    limit() {
      this.retrieveRules();
    },
    query() {
      this.retrieveRules();
    },
    loading() {
      if (this.loading) {
        this.$emit('start-loading');
      } else {
        this.$emit('end-loading');
      }
    },
  },
  methods: {
    retrieveRules() {
      this.loading = true;
      this.$ruleServices.getRules(this.query, null, this.filter, null, 0, this.limit).then(data => {
        this.rules = data.rules;
        this.totalSize = data.size || this.totalSize;
        return this.$nextTick();
      }).finally(() => this.loading = false);
    },
    getRules() {
      this.$ruleServices.getRules(null, null, this.filter, 'AUTOMATIC', 0, 50)
        .then(data => {
          this.rules = data.rules || [];
        })
        .catch(() => {
          this.addError = true;
          this.errorType='getRulesError';
        });
    },
    getDomains() {
      this.$ruleServices.getDomains()
        .then(data => {
          this.domains = data.domains || [];
        });
    },
    getEvents() {
      this.$ruleServices.getEvents()
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
      this.$ruleServices.deleteRule(ruleId)
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
      this.$ruleServices.updateRule(ruleDTO)
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
    },
    loadMore() {
      if (this.hasMore) {
        this.limit += this.pageSize;
      }
    },
  }
};
</script>
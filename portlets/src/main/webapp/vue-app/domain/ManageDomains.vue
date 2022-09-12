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
    <alert
      v-if="isadded || addSuccess"
      :show="dismissCountDown"
      class="success alert alert-success"
      dismissible
      fade
      v-on:="closeAlert()"
      variant="success"
      @dismiss-count-down="countDownChanged">
      <i class="uiIconSuccess"></i>{{ updateMessage }}
    </alert>
    <alert
      v-if="addError"
      class="alert alert-error"
      data-auto-dismiss="5000">
      <i class="uiIconError"></i> {{ errorMessage }}
    </alert>


    <save-domain-form
      :domain="domainInForm"
      :domains="domains"
      @cancel="resetDomainInForm"
      @failAdd="onDomainFail"
      @sucessAdd="onDomainCreated" />

    <domain-list
      :domain="domainInForm"
      :domains="domains"
      @failAdd="onDomainFail"
      @remove="onRemoveClicked"
      @sucessAdd="onDomainUpdated"
      @edit="onSaveClicked" />
  </section>
</template>
<script>
/* eslint-disable */
import DomainList from './DomainList.vue';
import SaveDomainForm from './SaveDomainForm.vue';
import BootstrapVue from 'bootstrap-vue';
import axios from 'axios';
import 'bootstrap/dist/css/bootstrap.css';
import 'bootstrap-vue/dist/bootstrap-vue.css';

Vue.use(BootstrapVue);

const initialData = () => {
  return {
    domainInForm: {
      id: null,
      enabled: true,
      title: '',
      description: '',

    },
    editeddomain: {},
    addSuccess: false,
    addError: false,
    updateMessage: '',
    errorMessage: '',
    domains: [],
    dismissSecs: 5,
    dismissCountDown: 0,
    isShown: false,
    isadded: false,
  };
};
export default {

  components: {
    SaveDomainForm,
    DomainList
  },
  data: initialData,
  created() {
    this.getAllDomains();
  },
  methods: {
    getAllDomains(){
    axios.get('/portal/rest/gamification/domains?status=ALL')
      .then(response => {
        this.domains = response.data && response.data.domains;
      })
      .catch(e => {
        this.addError = true;
        this.errors.push(e);
      })
      .catch(e => {
        this.errors.push(e);
      });
    },
    validateForm() {
      const errors = {};
      if (this.addSuccess = true) {
        errors.title = 'success';
        this.dismissCountDown = 5;
      }
      if (this.addError = true) {
        errors.title = 'error';
        this.dismissCountDown = 5;
      }
      if (this.addSuccess = true) {
        errors.description = 'success';
        this.dismissCountDown = 5;
      }
      if (this.addError = true) {
        errors.description = 'error';
        this.dismissCountDown = 5;
      }

      this.formErrors = errors;
      return Object.keys(errors).length === 0;
    },
    countDownChanged(dismissCountDown) {
      this.dismissCountDown = dismissCountDown;
    },

    showAlert() {
      this.dismissCountDown = this.dismissSecs;
    },
    resetDomainInForm() {
      this.domainInForm = initialData().domainInForm;
    },
    onSaveClicked(domain) {
      this.updateDomain(domain);
      this.isShown = !this.isShown;


    },
    onDomainCreated(domain) {
      this.isadded = true;
      this.addSuccess = true;
      this.updateMessage = this.$t('exoplatform.gamification.message.domain.added','Domain added successfully');
      this.getAllDomains();
      this.resetDomainInForm();

    },
    onDomainUpdated(domain) {
      this.addSuccess = true;
      this.updateMessage = this.$t('exoplatform.gamification.message.domain.updated','Domain updated successfully');
      this.domains.push(domain);
      this.resetDomainInForm();
      this.dismissCountDown = 5;

    },
    onDomainFail(domain) {
      this.addError = true;
      this.errorMessage= this.$t('exoplatform.gamification.message.domain.fail','An error happen when adding a domain'); 
      // this.errors.push(e);
      this.resetDomainInForm();
    },
    onRemoveClicked(domainId) {
      const index = this.domains.findIndex((p) => p.id === domainId);

      axios.delete(`/portal/rest/gamification/domains/${  domainId}`)
        .then(response => {
          this.domains.splice(index, 1);
        })
        .catch(e => {
          this.errors.push(e);
        });
      if (domainId === this.domainInForm.id) {
        this.resetDomainInForm();
      }
    },

    updateDomain(domainDTO) {
      axios.put(`/portal/rest/gamification/domains/${  domainDTO.id}`, domainDTO)
        .then(response => {
          this.addSuccess = true;
          this.updateMessage = 'updated';
          this.domains.push(domain)
            .catch(e => {
              this.addError = true;
              this.errors.push(e);
            });
        })
        .catch(e => {
          console.log('Error');
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
        top: 26%;
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
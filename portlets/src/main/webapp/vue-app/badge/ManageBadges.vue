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
    <div
      v-show="addSuccess"
      class="alert alert-success"
      v-on:="closeAlert()">
      <i class="uiIconSuccess"></i>
      {{ $t('exoplatform.gamification.badge') }}
      {{ updateMessage }} {{ $t('exoplatform.gamification.successfully') }}
    </div>
    <div
      v-show="addError"
      class="alert alert-error"
      v-on:="closeAlert()">
      <i class="uiIconError"></i>
      {{ $t(`exoplatform.gamification.${errorType}`) }}
    </div>

    <section class="application-body overflow-hidden">
      <save-badge-form
        :badge="badgeInForm"
        :programs="programs"
        :error-type="errorType"
        @cancel="resetBadgeInForm"
        @failAdd="onBadgeFail"
        @submit="onBadgeCreated" />
      <badge-list
        :badges="badges"
        :programs="programs"
        @remove="onRemoveClicked"
        @save="onSaveClicked" />
    </section>
  </div>
</template>

<script>
/* eslint-disable */
import Vue from 'vue';
import BadgeList from './BadgeList.vue';
import SaveBadgeForm from './SaveBadgeForm.vue';
import BootstrapVue from 'bootstrap-vue';
import axios from 'axios';


Vue.use(BootstrapVue);
const initialData = () => {
  return {
    badgeInForm: {
      id: null,
      title: '',
      description: '',
      neededScore: null,
      icon: null,
      dismissSecs: 5,
      countDown: 0,
      showDismissibleAlert: false,
      program: '',
      enabled: true,
      createdDate: null,
      lastModifiedBy: '',
      lastModifiedDate: null,
      isShown: false,
    },
    addSuccess: false,
    addError: false,
    updateMessage: '',
    badges: [],
    programs: [],
    errorType: ''
  };
};
export default {
  components: {
    BadgeList,
    SaveBadgeForm
  },
  data: initialData,
  created() {
    this.getPrograms()
      .then(() => this.getBadges());
  },
  methods: {
    resetBadgeInForm() {
      this.badgeInForm = initialData().badgeInForm;
    },
    onSaveClicked(badge) {
      this.updateBadge(badge);
      this.isShown = !this.isShown;
    },
    onBadgeCreated(badge) {
      this.addSuccess = true;
      this.updateMessage = 'added';
      this.badges.push(badge);
      this.resetBadgeInForm();
    },
    collapseButton() {
      this.isShown = !this.isShown;
    },
    onBadgeFail(badge,errorType) {
      this.addError = true;
      this.errorType=errorType;
      this.resetBadgeInForm();
      this.dismissCountDown();
    },
    dismissCountDown() {
      setTimeout(() => {
        this.addError = false;
        this.addSuccess = false;
      }, 3000);
    }, 
    onBadgeAction(badge) {
      const index = this.badges.findIndex((p) => p.id === badge.id);
      if (index !== -1) {
        this.updateBadge(badge);
        this.badges.splice(index, 1, badge);
      } else {
        this.createBadge(badge);
        this.badges.push(badge);
      }
      this.resetBadgeInForm();
    },
    onRemoveClicked(badgeId, badgeTitle) {
      const index = this.badges.findIndex((p) => p.id === badgeId);
      axios.delete(`/portal/rest/gamification/badges/delete/${  badgeId}`)
        .then(response => {
          this.addSuccess = true;
          this.updateMessage = 'deleted';
          this.badges.splice(index, 1);
          this.dismissCountDown();
        })
        .catch(e => {
          this.errorType='deleteBadgeError';
          this.addError=true;
          this.dismissCountDown();
        });
      if (badgeId === this.badgeInForm.id) {
        this.resetBadgeInForm();
      }
    },
    updateBadge(badgeDTO) {
      axios.put('/portal/rest/gamification/badges/update', badgeDTO)
        .then(response => {
          this.addSuccess = true;
          this.updateMessage = 'updated';
          this.dismissCountDown();
          this.getBadges();
        })
        .catch(e => {
          if (e.response.status===304){
            this.errorType='badgeExists';
          } else {
            this.errorType='updateBadgeError';
          }
          this.addError=true;
          this.dismissCountDown();
          this.getBadges();
        });

    },
    getBadges() {
      return fetch('/portal/rest/gamification/badges/all', {
        credentials: 'include',
      }).then(resp => resp?.ok && resp.json())
        .then(badges => {
          badges.forEach(badge => {
            if (badge?.program && this.programs?.length) {
              if(!this.programs.find(program => program.id === badge.program.id)) {
                badge.enabled = false;
              }
            }
          });
          this.badges = badges;
        })
        .catch(e => {
          this.errorType='getBadgesError';
          this.addError=true;
          this.dismissCountDown();
          console.log(e);
        });
    },
    getPrograms(){
      return fetch(`${eXo.env.portal.context}/${eXo.env.portal.rest}/gamification/programs?type=ALL`, {
        credentials: 'include',
      }).then(resp => resp?.ok && resp.json())
        .then(data => this.programs = data?.programs || []);
    }
  }
};
</script>

<style>
section {
    background: white;
    border-radius: 0 0 3px 3px;
}

.collapse.show.out {
    display: none;
}
.alert-success {
    background: #6ccbae;
    border-color: #2eb58c;
    color: #333333;
}

.alert {
    left: 50%;
    transform: translateX(-50%);
    max-width: 40%;
    margin: 10px !important;
    display: inline-block;
}

</style>

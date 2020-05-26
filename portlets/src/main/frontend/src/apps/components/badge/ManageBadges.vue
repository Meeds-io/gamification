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
      {{ this.$t('exoplatform.gamification.badge') }}
      {{ updateMessage }} {{ this.$t('exoplatform.gamification.successfully') }}
    </div>
    <div
      v-show="addError"
      class="alert alert-error"
      v-on:="closeAlert()">
      <i class="uiIconError"></i>
      {{ this.$t(`exoplatform.gamification.${errorType}`) }}
    </div>

    <section>
      <save-badge-form
        :badge="badgeInForm"
        :domains="domains"
        :error-type="errorType"
        @cancel="resetBadgeInForm"
        @failAdd="onBadgeFail"
        @submit="onBadgeCreated" />
      <badge-list
        :badges="badges"
        :domains="domains"
        @remove="onRemoveClicked"
        @save="onSaveClicked" />
    </section>
  </div>
</template>

<script>
import Vue from 'vue'
import BadgeList from './BadgeList'
import SaveBadgeForm from './SaveBadgeForm'
import BootstrapVue from 'bootstrap-vue'
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
            domain: '',
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
        domains: [],
        errorType: ""
    }
};
export default {
    components: {
        BadgeList,
        SaveBadgeForm
    },
    data: initialData,
    created() {
        this.getBadges()
        this.getDomains() 
    },
    methods: {
        resetBadgeInForm() {
            this.badgeInForm = initialData().badgeInForm
        },
        onSaveClicked(badge) {
            this.updateBadge(badge)
            this.isShown = !this.isShown;
        },
        onBadgeCreated(badge) {
            this.addSuccess = true;
            this.updateMessage = 'added';
            this.badges.push(badge);
            this.resetBadgeInForm()
        },
        collapseButton() {
            this.isShown = !this.isShown;
        },
        onBadgeFail(badge,errorType) {
            this.addError = true
            this.errorType=errorType
            this.resetBadgeInForm()
            this.dismissCountDown();
        },
         dismissCountDown() {
                setTimeout(() => {
                this.addError = false
                this.addSuccess = false
            }, 3000);
            }, 
        onBadgeAction(badge) {
            const index = this.badges.findIndex((p) => p.id === badge.id);
            if (index !== -1) {
                this.updateBadge(badge);
                this.badges.splice(index, 1, badge)
            } else {
                this.createBadge(badge);
                this.badges.push(badge)
            }
            this.resetBadgeInForm()
        },
        onRemoveClicked(badgeId, badgeTitle) {
            const index = this.badges.findIndex((p) => p.id === badgeId);
            axios.delete(`/portal/rest/gamification/badges/delete/${  badgeId}`)
                .then(response => {
                    this.addSuccess = true
                    this.updateMessage = 'deleted'
                    this.badges.splice(index, 1)
                    this.dismissCountDown();
                })
                .catch(e => {
                    this.errorType="deleteBadgeError"
                    this.addError=true
                    this.dismissCountDown();
                });
            if (badgeId === this.badgeInForm.id) {
                this.resetBadgeInForm()
            }
        },
        updateBadge(badgeDTO) {
            axios.put(`/portal/rest/gamification/badges/update`, badgeDTO)
                .then(response => {
                    this.addSuccess = true;
                    this.updateMessage = 'updated';
                    this.dismissCountDown();
                    this.getBadges()
                })
                .catch(e => {
                        if(e.response.status===304){
                            this.errorType="badgeExists"
                        }else{
                            this.errorType="updateBadgeError"
                        }
                        this.addError=true
                        this.dismissCountDown();
                        this.getBadges()
                })

        },
        getBadges() {
            axios.get(`/portal/rest/gamification/badges/all`)
            .then(response => {
                this.badges = response.data;
            })
            .catch(e => {
                this.errorType="getBadgesError"
                this.addError=true
                this.dismissCountDown();
                console.log(e)
            });
        },
        getDomains(){
            axios.get(`/portal/rest/gamification/domains`)
            .then(response => {
                this.domains = response.data;
            })
            .catch(e => {
                console.log(e)
            })
        }
    }
}
</script>

<style>
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

<template>
    <section>
        <div class="alert alert-success" v-if="isadded || addSuccess" v-on:="closeAlert()">
            <i class="uiIconSuccess"></i>
            {{this.$t('exoplatform.gamification.badge')}}
            {{updateMessage}}{{this.$t('exoplatform.gamification.successfully')}}
        </div>

        <b-alert @dismiss-count-down="countDownChanged"
                 dismissible
                 fade
                 show="dismissCountDown"
                 v-if="addError"
                 variant="danger">
            {{this.$t('exoplatform.gamification.errorbadge')}}
        </b-alert>

        <save-badge-form :badge="badgeInForm" :domains="domains" v-on:cancel="resetBadgeInForm"
                         v-on:failAdd="onBadgeFail"
                         v-on:submit="onBadgeCreated"></save-badge-form>
        <badge-list :badges="badges" :domains="domains" v-on:remove="onRemoveClicked"
                    v-on:save="onSaveClicked"></badge-list>
    </section>
</template>
<script>
    import Vue from 'vue'
    import BadgeList from './BadgeList'
    import SaveBadgeForm from './SaveBadgeForm'
    import BootstrapVue from 'bootstrap-vue'
    import axios from 'axios';
    import 'bootstrap/dist/css/bootstrap.css'
    import 'bootstrap-vue/dist/bootstrap-vue.css'

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
                dismissCountDown: 0,
                showDismissibleAlert: false,
                domain: '',
                enabled: null,
                createdDate: null,
                lastModifiedBy: '',
                lastModifiedDate: null,
                isadded: false,
                isShown: false,
            },
            addSuccess: false,
            addError: false,
            updateMessage: '',
            badges: [],
            domains: []
            ,
        }
    };
    export default {
        components: {
            BadgeList,
            SaveBadgeForm
        },
        data: initialData,
        methods: {
            resetBadgeInForm() {
                this.badgeInForm = initialData().badgeInForm
            },
            onSaveClicked(badge) {
                this.updateBadge(badge)
            },
            onBadgeCreated(badge) {
                this.isadded = true;
                this.addSuccess = true;
                this.updateMessage = 'added';
                this.badges.push(badge);
                this.resetBadgeInForm()
            },
            onBadgeFail(rule) {
                this.addError = true;
                this.errors.push(e);
                this.resetBadgeInForm();
                this.dismissCountDown = 5
            },
            countDownChanged(dismissCountDown) {
                this.dismissCountDown = dismissCountDown
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
                axios.delete(`/rest/gamification/badges/delete`, {params: {'badgeTitle': badgeTitle}})
                    .then(response => {
                        this.badges.splice(index, 1)
                    })
                    .catch(e => {
                        this.errors.push(e)
                    });
                if (badgeId === this.badgeInForm.id) {
                    this.resetBadgeInForm()
                }
            },
            updateBadge(badgeDTO) {
                const formData = new FormData();
                formData.append('file', badgeDTO.icon);
                const MAX_RANDOM_NUMBER = 100000;
                const uploadId = Math.round(Math.random() * MAX_RANDOM_NUMBER);
                axios.post(`/portal/upload?uploadId=${uploadId}&action=upload`, formData,
                    {
                        headers: {
                            'Content-Type': 'multipart/form-data'
                        }
                    }).then(response => {
                    badgeDTO.uploadId = uploadId;
                    axios.put(`/rest/gamification/badges/update`, badgeDTO)
                        .then(response => {
                            this.addSuccess = true;
                            this.updateMessage = 'updated';
                            this.badges.push(badge);
                            this.dismissCountDown = 5
                                .catch(e => {
                                    this.addError = true;
                                    this.errors.push(e)
                                })
                        })
                        .catch(e => {
                            console.log("Error")
                        })
                })
            }
        },
        created() {
            axios.get(`/rest/gamification/badges/all`)
                .then(response => {
                    this.badges = response.data;
                })
                .catch(e => {
                    this.errors.push(e)
                });
            axios.get(`/rest/gamification/api/v1/domains`)
                .then(response => {
                    this.domains = response.data;
                })
                .catch(e => {
                    this.errors.push(e)
                })
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
</style>
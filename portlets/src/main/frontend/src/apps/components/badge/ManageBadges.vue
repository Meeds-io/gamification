<template>
    <section>
        <b-alert v-if="addSuccess" variant="success" show dismissible>Badge {{updateMessage}} successully </b-alert>

        <b-alert v-if="addError" variant="danger" show dismissible>An error happen when adding a badge</b-alert>

        <save-badge-form :badge="badgeInForm" v-on:submit="onBadgeCreated" v-on:cancel="resetBadgeInForm"></save-badge-form>
        <badge-list :badges="badges" :domains="domains" v-on:save="onSaveClicked" v-on:remove="onRemoveClicked"></badge-list>
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
                domain: '',
                enabled: null,
                createdDate: null,
                lastModifiedBy: '',
                lastModifiedDate: null
            },
            addSuccess: false,
            addError: false,
            updateMessage: '',
            badges: [],
            domains: []
,
        }
    }
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
                this.addSuccess=true
                this.updateMessage='added'
                this.badges.push(badge)
                this.resetBadgeInForm()
            },
            onBadgeAction(badge) {
                const index = this.badges.findIndex((p) => p.id === badge.id)
                if (index !== -1) {
                    this.updateBadge(badge)
                    this.badges.splice(index, 1, badge)
                } else {
                    this.createBadge(badge)
                    this.badges.push(badge)
                }
                this.resetBadgeInForm()
            },
            onRemoveClicked(badgeId, badgeTitle) {
                const index = this.badges.findIndex((p) => p.id === badgeId)
                axios.delete(`/rest/gamification/badges/delete`, { params: { 'badgeTitle': badgeTitle } })
                    .then(response => {
                        this.badges.splice(index, 1)
                    })
                    .catch(e => {
                        this.errors.push(e)
                    })
                if (badgeId === this.badgeInForm.id) {
                    this.resetBadgeInForm()
                }
            },

            updateBadge(badgeDTO) {
                const formData = new FormData();
                formData.append('file', badgeDTO.icon)
                const MAX_RANDOM_NUMBER = 100000;
                const uploadId = Math.round(Math.random() * MAX_RANDOM_NUMBER);
                axios.post(`/portal/upload?uploadId=${uploadId}&action=upload`, formData,
                    {
                        headers: {
                            'Content-Type': 'multipart/form-data'
                        }
                    }).then(response => {
                    badgeDTO.uploadId=uploadId
                    axios.put(`/rest/gamification/badges/update`, badgeDTO)
                        .then(response => {
                            this.addSuccess = true;
                            this.updateMessage = 'updated'
                            this.badges.push(badge)
                        })
                        .catch(e => {
                            this.addError = true
                            this.errors.push(e)
                        })
                })
                    .catch(e => {
                        console.log("Error")
                    })
            },
        },
        created() {
            axios.get(`/rest/gamification/badges/all`)
                .then(response => {
                    this.badges = response.data;
                })
                .catch(e => {
                    this.errors.push(e)
                })
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
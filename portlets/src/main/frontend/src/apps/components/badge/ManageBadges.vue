
<template>
    <section>

        <b-alert v-if="addSuccess" variant="success" show dismissible>Rule {{updateMessage}} successully</b-alert>

        <b-alert v-if="addError" variant="danger" show dismissible>An error happen when adding a badge</b-alert>

        <save-badge-form :badge="badgeInForm" v-on:submit="onBadgeAction" v-on:cancel="resetBadgeInForm"></save-badge-form>
        <badge-list :badges="badges" v-on:edit="onEditClicked" v-on:remove="onRemoveClicked"></badge-list>
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
                startValidityDate: null,
                endValidityDate: null,
                domain: '',
                enabled: null,
                createdBy: '',
                createdDate: null,
                lastModifiedBy: '',
                lastModifiedDate: null
            },
            addSuccess: false,
            addError: false,
            updateMessage: '',
            badges: []

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
            onEditClicked(badge) {

                this.badgeInForm = { ...badge }
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
            createBadge(badgeDTO) {

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
                    axios.post(`/rest/gamification/badges/add`, badgeDTO)
                        .then(response => {

                            this.addSuccess = true
                            this.updateMessage = 'added'
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
                        })
                        .catch(e => {

                            this.addError = true
                            this.errors.push(e)

                        })


                })
                    .catch(e => {

                        console.log("Error")

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
                })
        }
    }
</script>

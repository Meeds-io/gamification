<!-- src/components/ManageBadges.vue -->
<template>
    <section>
        <!-- Manage Success Alerts -->
        <b-alert v-if="addSuccess" variant="success" show dismissible>Rule {{updateMessage}} successully</b-alert>
        <!-- End -->
        <!-- Manage Success Alerts -->
        <b-alert v-if="addError" variant="danger" show dismissible>An error happen when adding a badge</b-alert>
        <!-- End -->
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
                zone: '',
                isEnabled: false,
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
                // since objects are passed by reference we need to clone the product
                // either by using Object.assign({}, product) or by using object
                // spread like we do here.
                this.badgeInForm = { ...badge }
            },
            onBadgeAction(badge) {
                const index = this.badges.findIndex((p) => p.id === badge.id)
                if (index !== -1) {
                    // Update the selected badge
                    // See http://vuejs.org/guide/list.html#Caveats
                    this.updateBadge(badge)
                    this.badges.splice(index, 1, badge)
                } else {
                    // Create a new badge
                    this.createBadge(badge)
                    this.badges.push(badge)
                }

                this.resetBadgeInForm()
            },
            onRemoveClicked(badgeId, badgeTitle) {
                const index = this.badges.findIndex((p) => p.id === badgeId)


                // Add dynamic invocation to server side
                axios.delete(`/rest/gamification/badges/delete`, { params: { 'badgeTitle': badgeTitle } })
                    .then(response => {
                        // JSON responses are automatically parsed.

                        console.log(JSON.stringify(response.data))

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

                console.log(badgeDTO)

                const formData = new FormData();
                /**
                Object.keys(badgeDTO).forEach(field => {
                    formData.append(field, badgeDTO[field]);
                });
                */    
                formData.append('file', badgeDTO.icon)

               const MAX_RANDOM_NUMBER = 100000;
               const uploadId = Math.round(Math.random() * MAX_RANDOM_NUMBER);     
               

                axios.post(`/portal/upload?uploadId=${uploadId}&action=upload`, formData,
                    {
                        headers: {
                            'Content-Type': 'multipart/form-data'
                        }
                    }).then(response => {
                        console.log("Doneeeeeee")
                        badgeDTO.uploadId=uploadId
                        axios.post(`/rest/gamification/badges/add`, badgeDTO)
                        .then(response => {
                            //this.rules = response.data;
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
                axios.put(`/rest/gamification/badges/update`, badgeDTO)
                    .then(response => {
                        this.addSuccess = true;
                        this.updateMessage = 'updated'

                    })
                    .catch(e => {
                        this.addError = true
                        this.errors.push(e)
                    })
            }
        },
        // Fetches badges when the component is created.
        created() {
            axios.get(`/rest/gamification/badges/all`)
                .then(response => {
                    // JSON responses are automatically parsed.
                    //this.posts = response.data
                    console.log(JSON.stringify(response.data))
                    this.badges = response.data;
                })
                .catch(e => {
                    this.errors.push(e)
                })
        }
    }
</script>

<style scoped>
</style>
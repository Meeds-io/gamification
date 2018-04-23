<!-- src/components/ManageBadges.vue -->
<template>
    <section>
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
                rules: null,
                isEnabled: false,
                createdBy: '',
                createdDate: null,
                lastModifiedBy: '',
                lastModifiedDate: null
            },
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
            onFormSave(badge) {

                // Generate an id using the third-party lib 'uuid' ==> doesn't work as expected
                //badge.id = uuid.v4()
                badge.id = Math.random();
                // eslint-disable-next-line no-console
                console.log('badgeData', JSON.stringify(badge))
                // add it to the product list
                this.badges.push(badge)
                // reset the form
                this.resetBadgeInForm()
            },
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

                console.log(index);
                // update product if it exists or create it if it doesn't
                if (index !== -1) {
                    // We need to replace the array entirely so that vue can recognize
                    // the change and re-render entirely.
                    // See http://vuejs.org/guide/list.html#Caveats
                    this.badges.splice(index, 1, badge)
                } else {
                    //badge.id = uuid.v4()
                    badge.id = Math.random();
                    this.badges.push(badge)
                }

                this.resetBadgeInForm()
            },
            onRemoveClicked(badgeId, badgeTitle) {
                const index = this.badges.findIndex((p) => p.id === badgeId)

                console.log('#############################' + badgeTitle)
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
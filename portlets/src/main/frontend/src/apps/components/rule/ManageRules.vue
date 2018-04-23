<!-- src/components/ManageRules.vue -->
<template>
    <section>
        <save-rule-form :rule="ruleInForm" v-on:submit="onRuleAction" v-on:cancel="resetRuleInForm"></save-rule-form>
        <rule-list :rules="rules" v-on:edit="onEditClicked" v-on:remove="onRemoveClicked"></rule-list>
    </section>
</template>
<script>

    import Vue from 'vue'
    import RuleList from './RuleList'
    import SaveRuleForm from './SaveRuleForm'
    import BootstrapVue from 'bootstrap-vue'
    import axios from 'axios';

    Vue.use(BootstrapVue);

    const initialData = () => {
        return {
            ruleInForm: {
                id: null,
                title: '',
                description: '',
                score: null,
                startValidity: null,
                endValidity: null,
                enabled: false
            },
            rules: []

        }
    }

    export default {
        components: {
            RuleList,
            SaveRuleForm
        },

        data: initialData,
        methods: {
            onFormSave(rule) {

                // Generate an id using the third-party lib 'uuid' ==> doesn't work as expected
                //rule.id = uuid.v4()
                rule.id = Math.random();
                // eslint-disable-next-line no-console
                console.log('ruleData', JSON.stringify(rule))
                // add it to the product list
                this.rules.push(rule)
                // reset the form
                this.resetRuleInForm()
            },
            resetRuleInForm() {
                this.ruleInForm = initialData().ruleInForm
            },
            onEditClicked(rule) {
                // since objects are passed by reference we need to clone the product
                // either by using Object.assign({}, product) or by using object
                // spread like we do here.
                this.ruleInForm = { ...rule }
            },
            onRuleAction(rule) {
                const index = this.rules.findIndex((p) => p.id === rule.id)

                console.log(index);
                // update product if it exists or create it if it doesn't
                if (index !== -1) {
                    // We need to replace the array entirely so that vue can recognize
                    // the change and re-render entirely.
                    // See http://vuejs.org/guide/list.html#Caveats
                    this.rules.splice(index, 1, rule)
                } else {
                    //rule.id = uuid.v4()
                    rule.id = Math.random();
                    this.rules.push(rule)
                }

                this.resetRuleInForm()
            },
            onRemoveClicked(ruleId, ruleTitle) {
                const index = this.rules.findIndex((p) => p.id === ruleId)

                console.log('#############################' + ruleTitle)
                // Add dynamic invocation to server side
                axios.delete(`/rest/gamification/rules/delete`, { params: { 'ruleTitle': ruleTitle } })
                    .then(response => {
                        // JSON responses are automatically parsed.

                        console.log(JSON.stringify(response.data))

                        this.rules.splice(index, 1)
                    })
                    .catch(e => {
                        this.errors.push(e)
                    })



                if (ruleId === this.ruleInForm.id) {
                    this.resetRuleInForm()
                }
            }
        },
        // Fetches posts when the component is created.
        created() {
            axios.get(`/rest/gamification/rules/all`)
                .then(response => {
                    // JSON responses are automatically parsed.
                    //this.posts = response.data
                    console.log(JSON.stringify(response.data))
                    this.rules = response.data;
                })
                .catch(e => {
                    this.errors.push(e)
                })
        }
    }
</script>
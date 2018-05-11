<!-- src/components/ManageRules.vue -->
<template>
    <section>
        <!-- Manage Success Alerts -->
        <b-alert v-if="addSuccess" variant="success" show dismissible>Rule {{updateMessage}} successully</b-alert>
        <!-- End -->
        <!-- Manage Success Alerts -->
        <b-alert v-if="addError" variant="danger" show dismissible>An error happen when adding a rule</b-alert>
        <!-- End -->
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
    import 'bootstrap/dist/css/bootstrap.css'
    import 'bootstrap-vue/dist/bootstrap-vue.css'
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
                enabled: null,
                area: ''
            },
            addSuccess: false,
            addError: false,
            updateMessage: '',
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
            resetRuleInForm() {
                this.ruleInForm = initialData().ruleInForm
            },
            onEditClicked(rule) {
                // since objects are passed by reference we need to clone the rule
                // either by using Object.assign({}, rule) or by using object
                // spread like I do here.
                this.ruleInForm = { ...rule }
            },
            onRuleAction(rule) {
                const index = this.rules.findIndex((p) => p.id === rule.id)
                // update rule if it exists or create it if it doesn't
                if (index !== -1) {
                    // Update the selected rule
                    // See http://vuejs.org/guide/list.html#Caveats
                    this.updateRule(rule)
                    this.rules.splice(index, 1, rule)
                } else {
                    // Create a new rule
                    //rule.id = Math.random();
                    this.createRule(rule)
                    this.rules.push(rule)
                }

                this.resetRuleInForm()
            },
            onRemoveClicked(ruleId, ruleTitle) {
                const index = this.rules.findIndex((p) => p.id === ruleId)

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
            },
            createRule(ruleDTO) {
                axios.post(`/rest/gamification/rules/add`, ruleDTO)
                    .then(response => {
                        //this.rules = response.data;
                        this.addSuccess=true
                         this.updateMessage='added'
                    })
                    .catch(e => {

                        this.addError=true
                        this.errors.push(e)

                    })
                this.resetRuleInForm()

            },
            updateRule(ruleDTO) {
                axios.put(`/rest/gamification/rules/update`, ruleDTO)
                    .then(response => {
                         this.addSuccess=true; 
                         this.updateMessage='updated'

                    })
                    .catch(e => {
                        this.addError=true                        
                        this.errors.push(e)
                    })
            }

        },
        // Fetches rules when the component is created.
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

<style scoped>

</style>
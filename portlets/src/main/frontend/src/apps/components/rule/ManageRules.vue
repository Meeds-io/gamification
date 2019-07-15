<template>
    <section>

        <b-alert v-if="addSuccess" variant="success" show dismissible>Rule {{updateMessage}} successfully</b-alert>

        <b-alert v-if="addError" variant="danger" show dismissible>An error happen when adding a rule</b-alert>

        <save-rule-form :rule="ruleInForm" :domains="domains" v-on:sucessAdd="onRuleCreated" v-on:failAdd="onRuleFail" v-on:cancel="resetRuleInForm"></save-rule-form>
        <rule-list  :rules="rules"  :domains="domains" v-on:save="onSaveClicked" v-on:remove="onRemoveClicked"></rule-list>

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
                enabled: null,
                area: '',
                lastModifiedBy: '',
                lastModifiedDate: null
            },
            addSuccess: false,
            addError: false,
            updateMessage: '',
            rules: [],
            domains: []
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
            onSaveClicked (rule) {

                this.updateRule(rule)

            },
            onRuleCreated(rule) {
                this.addSuccess=true
                this.updateMessage='added'
                this.rules.push(rule)
                this.resetRuleInForm()

            },
            onRuleFail(rule) {
                this.addError=true
                this.errors.push(e)
                this.resetRuleInForm()
            },
            onRemoveClicked(ruleId, ruleTitle) {
                const index = this.rules.findIndex((p) => p.id === ruleId)

                axios.delete(`/rest/gamification/rules/delete`, { params: { 'ruleTitle': ruleTitle } })
                    .then(response => {
                        this.rules.splice(index, 1)
                    })
                    .catch(e => {
                        this.errors.push(e)
                    })
                if (ruleId === this.ruleInForm.id) {
                    this.resetRuleInForm()
                }
            },
            updateRule(ruleDTO) {
                axios.put(`/rest/gamification/rules/update`, ruleDTO)
                    .then(response => {
                        this.addSuccess=true;
                        this.updateMessage='updated'
                            .catch(e => {
                                this.addError=true
                                this.errors.push(e)
                            })
                    })
                    .catch(e => {
                        console.log("Error")
                    })
            }
        },

        created() {
            axios.get(`/rest/gamification/rules/all`)
                .then(response => {
                    this.rules = response.data;
                })
                .catch(e => {
                    this.addError=true
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

<style scoped>
    .alert-success {
        color: #315b73;
        background-color: #d4edda;
        border-color: #c3e6cb;
        margin: 0 auto;
        top: 10px;
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

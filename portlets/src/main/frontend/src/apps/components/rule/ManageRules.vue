
<template>
    <section>
       
        <b-alert v-if="addSuccess" variant="success" show dismissible>Rule {{updateMessage}} successully</b-alert>
      
        <b-alert v-if="addError" variant="danger" show dismissible>An error happen when adding a rule</b-alert>
       
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
                
                this.ruleInForm = { ...rule }
            },
            onRuleAction(rule) {
                const index = this.rules.findIndex((p) => p.id === rule.id)
                
                if (index !== -1) {
                   
                    this.updateRule(rule)
                    this.rules.splice(index, 1, rule)
                } else {
                   
                    this.createRule(rule)
                    this.rules.push(rule)
                }

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
            createRule(ruleDTO) {
                axios.post(`/rest/gamification/rules/add`, ruleDTO)
                    .then(response => {
                       
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
       
        created() {
            axios.get(`/rest/gamification/rules/all`)
                .then(response => {
                    this.rules = response.data;
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
</style>
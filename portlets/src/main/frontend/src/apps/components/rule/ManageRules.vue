<template>
    <section>
        <div class="alert alert-success" v-if="isadded || addSuccess" v-on:="closeAlert()">
            <i class="uiIconSuccess"></i>
            {{this.$t('exoplatform.gamification.rule')}} {{updateMessage}}
            {{this.$t('exoplatform.gamification.successfully')}}
        </div>


        <alert @dismiss-count-down="countDownChanged"
               class="alert alert-danger"
               :show="dismissCountDown"
               dismissible
               fade
               variant="danger"
               v-if="addError"><i class="uiIconError"></i> {{this.$t('exoplatform.gamification.errorrule')}}
        </alert>


        <save-rule-form :rule="ruleInForm" :domains="domains" :events="events"  v-on:sucessAdd="onRuleCreated" v-on:failAdd="onRuleFail" v-on:cancel="resetRuleInForm"></save-rule-form>
        <rule-list :domain="domain" :domains="domains" :events="events" :rule="ruleInForm" :rules="rules"
                   v-on:remove="onRemoveClicked" v-on:save="onSaveClicked"></rule-list>

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
                enabled: true,
                area: '',
                lastModifiedBy: '',
                lastModifiedDate: null
            },
            addSuccess: false,
            addError: false,
            updateMessage: '',
            rules: [],
            domains: [],
            domain: '',
            events: [],
            isadded: false,
            isShown: false,
        }
    };
    export default {
        components: {
            RuleList,
            SaveRuleForm
        },
        data: initialData,
        methods: {
            validateForm() {
                const errors = {};
                if (this.addSuccess=true) {
                    errors.title = 'success';
                    this.dismissCountDown = 5
                }
                if (this.addError=true) {
                    errors.title = 'error';
                    this.dismissCountDown = 5
                }

                this.formErrors = errors;
                return Object.keys(errors).length === 0
            },
            countDownChanged(dismissCountDown) {
                this.dismissCountDown = dismissCountDown
            },
            resetRuleInForm() {
                this.ruleInForm = initialData().ruleInForm
            },
            onSaveClicked (rule) {
                this.updateRule(rule);
                this.isShown = !this.isShown;
            },

            onRuleCreated(rule) {
                this.isadded = true;
                this.addSuccess = true;
                this.updateMessage = 'added';
                this.rules.push(rule);
                this.resetRuleInForm()

            },

            onRuleFail(rule) {
                this.addError = true;
                this.errors.push(e);
                this.resetRuleInForm();
                this.dismissCountDown = 5
            },

            onRemoveClicked(ruleId, ruleTitle) {
                const index = this.rules.findIndex((p) => p.id === ruleId);
                axios.delete(`/rest/gamification/rules/delete/`+ruleId)
                    .then(response => {
                        this.rules.splice(index, 1)
                    })
                    .catch(e => {
                        this.errors.push(e)
                    });
                if (ruleId === this.ruleInForm.id) {
                    this.resetRuleInForm()
                }
            },
            updateRule(ruleDTO) {
                axios.put(`/rest/gamification/rules/update`, ruleDTO)
                    .then(response => {
                        this.addSuccess = true;
                        this.updateMessage = 'updated';
                        this.rules.push(rule);
                        this.dismissCountDown = 5
                            .catch(e => {
                                this.addError = true;
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
                    this.addError = true;
                    this.errors.push(e)
                });
            axios.get(`/rest/gamification/domains`)
                .then(response => {
                    this.domains = response.data;
                })
                .catch(e => {
                    this.errors.push(e)
                });
            axios.get(`/rest/gamification/api/v1/events`)
                .then(response => {
                    this.events = response.data;
                })
                .catch(e => {
                    this.errors.push(e)
                })
        }
    }
</script>
<style scoped>
    .alert {
        position: fixed;
        left: 50%;
        transform: translateX(-50%);
        max-width: 40%;
        top: 35%;
        display: inline-block;
    }
    .alert-success {
        background: #6ccbae;
        border-color: #2eb58c;
        color: #333333;
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
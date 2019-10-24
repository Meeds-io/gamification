<template>
    <section>
        <alert :show="dismissCountDown"
               @dismiss-count-down="countDownChanged"
               class="success alert alert-success"
               dismissible
               fade
               v-if="isadded || addSuccess" v-on:="closeAlert()" variant="success"><i class="uiIconSuccess"></i>{{updateMessage}}
        </alert>
        <alert class="alert alert-error" data-auto-dismiss="5000" v-if="addError"><i class="uiIconError"></i> {{errorMessage}}
        </alert>


        <save-domain-form :domain="domainInForm" :domains="domains" v-on:cancel="resetDomainInForm"
                          v-on:failAdd="onDomainFail" v-on:sucessAdd="onDomainCreated"></save-domain-form>

        <domain-list :domain="domainInForm" :domains="domains" v-on:failAdd="onDomainFail" v-on:remove="onRemoveClicked"
                     v-on:sucessAdd="onDomainUpdated" v-on:edit="onSaveClicked">


        </domain-list>


    </section>
</template>
<script>
    import DomainList from './DomainList'
    import SaveDomainForm from './SaveDomainForm'
    import BootstrapVue from 'bootstrap-vue'
    import axios from 'axios';
    import 'bootstrap/dist/css/bootstrap.css'
    import 'bootstrap-vue/dist/bootstrap-vue.css'

    Vue.use(BootstrapVue);

    const initialData = () => {
        return {
            domainInForm: {
                id: null,
                enabled:true,
                title: '',
                description: '',

            },
            editeddomain: {},
            addSuccess: false,
            addError: false,
            updateMessage: '',
            errorMessage:'',
            domains: [],
            dismissSecs: 5,
            dismissCountDown: 0,
            isShown: false,
            isadded: false,
        }
    };
    export default {

        components: {
            SaveDomainForm,
            DomainList
        },
        data: initialData,
        methods: {
            validateForm() {
                const errors = {};
                if (this.addSuccess = true) {
                    errors.title = 'success';
                    this.dismissCountDown = 5
                }
                if (this.addError = true) {
                    errors.title = 'error';
                    this.dismissCountDown = 5
                }
                if (this.addSuccess = true) {
                    errors.description = 'success';
                    this.dismissCountDown = 5
                }
                if (this.addError = true) {
                    errors.description = 'error';
                    this.dismissCountDown = 5
                }

                this.formErrors = errors;
                return Object.keys(errors).length === 0
            },
            countDownChanged(dismissCountDown) {
                this.dismissCountDown = dismissCountDown
            },

            showAlert() {
                this.dismissCountDown = this.dismissSecs
            },
            resetDomainInForm() {
                this.domainInForm = initialData().domainInForm
            },
            onSaveClicked(domain) {
                this.updateDomain(domain)
                this.isShown = !this.isShown;


            },
            onDomainCreated(domain) {
                this.isadded = true;
                this.addSuccess = true;
                this.updateMessage = this.$t('exoplatform.gamification.message.domain.added',"Domain added successfully");
                this.domains.push(domain);
                this.resetDomainInForm();

            },
            onDomainUpdated(domain) {
                this.addSuccess = true;
                this.updateMessage = this.$t('exoplatform.gamification.message.domain.updated',"Domain updated successfully");
                this.domains.push(domain);
                this.resetDomainInForm();
                this.dismissCountDown = 5

            },
            onDomainFail(domain) {
                this.addError = true;
                this.errorMessage= this.$t('exoplatform.gamification.message.domain.fail',"An error happen when adding a domain"); 
                // this.errors.push(e);
                this.resetDomainInForm()
            },
            onRemoveClicked(domainId) {
                const index = this.domains.findIndex((p) => p.id === domainId);

                axios.delete(`/rest/gamification/domains/` + domainId)
                    .then(response => {
                        this.domains.splice(index, 1)
                    })
                    .catch(e => {
                        this.errors.push(e)
                    });
                if (domainId === this.domainInForm.id) {
                    this.resetDomainInForm()
                }
            },

            updateDomain(domainDTO) {
                axios.put(`/rest/gamification/domains/` + domainDTO.id, domainDTO)
                    .then(response => {
                        this.addSuccess = true;
                        this.updateMessage = 'updated';
                        this.domains.push(domain)
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
            axios.get(`/rest/gamification/domains`)
                .then(response => {
                    this.domains = response.data;
                })
                .catch(e => {
                    this.addError = true;
                    this.errors.push(e)
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
        top: 26%;
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
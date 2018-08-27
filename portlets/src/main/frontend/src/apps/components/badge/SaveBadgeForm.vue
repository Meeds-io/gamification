<template>

    <div>
        <b-container fluid>
            <b-col sm="12">
                <h5 class="mt-0 ">Manage gamification's badges</h5>
                <b-form-row>
                    <b-col class="card">
                        <b-form-group id="titleInputGroup" label="Title:" label-for="titleInput">
                            <b-form-input id="titleInput" type="text" v-model="badge.title" required placeholder="Enter badge's title">
                            </b-form-input>

                            <b-alert v-if="formErrors.title" :show="dismissCountDown" dismissible variant="danger" class="require-msg" @dismissed="dismissCountdown=0"
                                @dismiss-count-down="countDownChanged">
                                Badge title is required please enter a title {{dismissCountDown}} ...
                            </b-alert>
                        </b-form-group>

                        <b-form-group id="descriptionInputGroup" label="Description:" label-for="descriptionInput">
                            <b-form-textarea id="badgeDescription" v-model="badge.description" placeholder="Enter description" :rows="3" :max-rows="6">
                            </b-form-textarea>
                        </b-form-group>

                        <b-form-group id="neededScoreInputGroup" label="Needed score:" label-for="neededScoreInput">
                            <b-form-input id="neededScoreInput" type="number" v-model="badge.neededScore" required placeholder="Enter badge's needed score">
                            </b-form-input>
                            <b-alert v-if="formErrors.neededScore" :show="dismissCountDown" dismissible variant="danger" class="require-msg" @dismissed="dismissCountdown=0"
                                @dismiss-count-down="countDownChanged">
                                Badge needed score is required please enter a value {{dismissCountDown}} ...
                            </b-alert>
                        </b-form-group>

                        <b-form-group id="iconInputGroup" label="Icon:" label-for="iconInput">

                            <b-form-file v-model="badge.icon" :state="Boolean(badge.icon)" placeholder="Choose a file..." accept="image/jpeg, image/png, image/gif"></b-form-file>
                            <div class="mt-3">Selected icon: {{badge.icon && badge.icon.name}}</div>
                        </b-form-group>


                    </b-col>
                    <b-col class="card">

                        <b-form-group id="startValidityDateInputGroup " label="Start Validity Date:" label-for="startValidityDateInput">

                            <date-picker name="startValidityDateInput" id="startValidityDateInput" v-model="badge.startValidityDate" :config="config"
                                placeholder="Enter badge's start validity date"></date-picker>
                        </b-form-group>

                        <b-form-group id="endValidityDateInputGroup" label="End Validity Date:" label-for="endValidityDateInput">
                            <date-picker name="endValidityDateInput" id="endValidityDateInput" v-model="badge.endValidityDate" :config="config" placeholder="Enter badge's start validity date"></date-picker>
                        </b-form-group>

                        <b-form-group id="domainSelectboxGroup">
                            <b-form-select v-model="badge.domain" class="mb-3" required>
                                <template slot="first">

                                    <option :value="null" disabled>-- Please select a domain --</option>
                                </template>

                                <option value="Social">Social</option>
                                <option value="Knowledge">Knowledge</option>
                                <!--
                                <option value="Content">Content</option>
                                -->
                            </b-form-select>
                            <b-alert v-if="formErrors.neededScore" :show="dismissCountDown" dismissible variant="danger" class="require-msg" @dismissed="dismissCountdown=0"
                                @dismiss-count-down="countDownChanged">
                                Domain is required please choice a domain {{dismissCountDown}} ...
                            </b-alert>
                        </b-form-group>

                        <b-form-group id="enableCheckboxGroup">
                            <b-form-checkbox v-model="badge.enabled">Enable rule</b-form-checkbox>
                        </b-form-group>


                        <b-row>
                            <b-col>

                                <b-button type="submit" v-on:click.prevent="onSubmit" class="btn btn-primary">
                                    {{badge.id ? 'Update' : 'Add'}} badge
                                </b-button>
                            </b-col>
                            <b-col>
                                <b-button type="submit" v-if="badge.id" v-on:click.prevent="onCancel" class="btn btn-secondary">Cancel</b-button>

                            </b-col>
                        </b-row>
                    </b-col>
                </b-form-row>
            </b-col>

        </b-container>
    </div>

</template>

<script>
    import Vue from 'vue'
    import axios from 'axios'
    import BootstrapVue from 'bootstrap-vue'
    import 'bootstrap-vue/dist/bootstrap-vue.css'
    import datePicker from 'vue-bootstrap-datetimepicker'
    import 'eonasdan-bootstrap-datetimepicker/build/css/bootstrap-datetimepicker.css';
    Vue.use(BootstrapVue);
    Vue.use(datePicker);

    export default {
        props: ['badge'],
        data: function () {
            return {
                formErrors: {},
                selectedFile: undefined,
                selectedFileName: '',
                dismissSecs: 5,
                dismissCountDown: 0,
                date: new Date(),
                config: {
                    format: 'YYYY-MM-DD',
                    useCurrent: false,
                },
                dynamicRules: []
            }
        },

        watch: {
            'badge.id'() {
                this.formErrors = {}
                this.selectedFile = undefined
                this.selectedFileName = this.badge.imageName
            }
        },
        methods: {
            validateForm() {
                const errors = {}

                if (!this.badge.title) {
                    errors.title = 'Title is required'
                    this.dismissCountDown = 5
                }

                if (!this.badge.neededScore) {
                    errors.neededScore = 'Needed score is required'
                    this.dismissCountDown = 5
                }
                if (!this.badge.domain) {
                    errors.domain = 'Domain required'
                    this.dismissCountDown = 5
                }
                this.formErrors = errors

                return Object.keys(errors).length === 0
            },

            onImageChanged(event) {
                this.selectedFile = event.target.files[0]
                this.selectedFileName = event.target.files[0].name

            },
            onCancel() {
                this.$emit('cancel')
            },
            onSubmit() {
                if (this.validateForm()) {
                    this.$emit('submit', this.badge)
                }

            },
            countDownChanged(dismissCountDown) {
                this.dismissCountDown = dismissCountDown
            },
            confirm() {
                this.$modals.confirm({
                    message: 'Confirm?',
                    onApprove: () => { },
                    onCancel: () => { },
                });
            },


        },

        created() {

        }


    }
</script>

<style scoped>
    form {
        margin-bottom: 24px;
    }

    h5.mt-0 {
        color: #4d5466;
        font-family: Helvetica, arial, sans-serif;
        line-height: 20px;
        font-size: 1.5em;
        text-transform: uppercase;
        font-weight: bold;
        text-align: center;
        padding: 20px 0px;
    }

    label {
        display: inline-block;
        max-width: 100%;
        margin-bottom: 5px;
        font-weight: 700;
        color: #333;
    }

    input[type="number"] {
        font-size: 15px;
        height: 40px;
        padding: 0 10px;
        border: 2px solid #e1e8ee;
        border-radius: 5px;
        box-shadow: none;
        max-height: 40px;
        text-overflow: ellipsis;
    }

    input[type="number"]:focus:invalid:focus,
    input[type="date"]:focus:invalid:focus {
        border-color: #e9322d;
        -webkit-box-shadow: 0 0 6px #f8b9b7;
        -moz-box-shadow: 0 0 6px #f8b9b7;
        box-shadow: 0 0 6px #f8b9b7;
    }

    .card {
        position: relative;
        border-radius: 3px;
        background: #ffffff;
        border-top: 3px solid #d2d6de;
        margin-bottom: 20px;
        width: 100%;
        box-shadow: 0 1px 1px rgba(0, 0, 0, 0.1);
        border-top-color: #3c8dbc;
        margin: 0px 11px;
        padding: 15px;

    }

    .require-msg {
        max-width: 100% !important;
        font-size: 14px;
        padding: 10px;
    }

    .close {
        float: right;
        font-size: 21px;
        font-weight: 700;
        line-height: 1;
        color: #bbb;
        text-shadow: 0 1px 0 #fff;
        filter: alpha(opacity=20);
        cursor: pointer;
    }

    .close:hover {
        color: #000;
    }
</style>
<template>
    <div>

            <div class="col-sm-12 card">

                <div>
                  <!--  <div class="btn" id="headingOne">
                        <h5 class="mb-0"><button aria-controls="collapseOne" aria-expanded="true" class="btn btn-link primary" data-target="#collapseOne" data-toggle="collapse" type="button">add rule</button></h5>
                    </div> -->

                    <div aria-labelledby="headingOne" class="in collapse show" data-parent="#accordionExample" id="collapseOne" style="height: auto;">
                        <div class="card-body">
                            <form-row>
                                <div class="card body">

                                    <form-group id="titleInputGroup">
                                        <label class="col-form-label pt-0">Title:</label>
                                        <input id="titleInput" type="text" v-model="rule.title" required placeholder="Enter rule's title">
                                        </input>


                                        <b-alert v-if="formErrors.title" :show="dismissCountDown" dismissible variant="danger" class="require-msg" @dismissed="dismissCountdown=0" @dismiss-count-down="countDownChanged">
                                            Rule title is required please enter a title {{dismissCountDown}} ...
                                        </b-alert>
                                    </form-group>



                                    <form id="descriptionInputGroup">
                                        <label class="col-form-label pt-0">Description:</label>

                                        <textarea id="ruleDescription" v-model="rule.description" placeholder="Enter description" :rows="3" :max-rows="6">
                            </textarea>
                                    </form>
                                    <form id="scoreInputGroup">

                                        <label id="scoreInputGroup" for="scoreInput" class="col-form-label pt-0">Score:</label>
                                        <input id="scoreDescription" type="number" v-model="rule.score" required placeholder="Enter rule's score">
                                        </input>
                                        <b-alert v-if="formErrors.score" :show="dismissCountDown" dismissible variant="danger" class="require-msg" @dismissed="dismissCountdown=0" @dismiss-count-down="countDownChanged">
                                            Rule score is required please enter a score {{dismissCountDown}} ...
                                        </b-alert>
                                    </form>
                                </div>

                                <div class="card col">

                                    <form id="startValidityInputGroup">
                                        <label id="startValidityInputGroup" for="startValidityInput" class="col-form-label pt-0">Start validity:</label>
                                        <date-picker name="endValidityDateInput" id="startValidityInput" v-model="rule.startValidity" :config="config" required placeholder="Enter rule's start validity"></date-picker>
                                        <b-alert v-if="formErrors.startValidity" :show="dismissCountDown" dismissible variant="danger" class="require-msg" @dismissed="dismissCountdown=0" @dismiss-count-down="countDownChanged">
                                            Rule start validity date is required please enter a date {{dismissCountDown}} ...
                                        </b-alert>
                                    </form>

                                    <form id="endValidityInputGroup">
                                        <label id="endValidityInputGroup" for="endValidityInput" class="col-form-label pt-0">End validity:</label>
                                        <date-picker name="endValidityDateInput" id="endValidityInput" v-model="rule.endValidity" :config="config" required placeholder="Enter rule's end validity"></date-picker>
                                        <b-alert v-if="formErrors.endValidity" :show="dismissCountDown" dismissible variant="danger" class="require-msg" @dismissed="dismissCountdown=0" @dismiss-count-down="countDownChanged">
                                            Rule end validity date is required please enter a date {{dismissCountDown}} ...
                                        </b-alert>
                                    </form>

                                    <div class="custom-control custom-checkbox custom-control-inline">

                                        <label class="checkbox custom-checkbox" id="enableCheckboxGroup">
                                            <input type="checkbox" v-model="rule.enabled"> Enable rule
                                        </label>

                                    </div>

                                    <form id="areaSelectboxGroup">
                                        <select v-model="rule.area" class="mb-4">
                                            <template slot="first">

                                                <option :value="null" disabled>-- Please select an area --</option>
                                            </template>

                                            <option value="Social">Social</option>
                                            <option value="Knowledge">Knowledge</option>
                                            <option value="Teamwork">Teamwork</option>
                                            <option value="Feedback">Feedback</option>
                                        </select>
                                    </form>

                                    <div class="row">
                                        <b-col>
                                            <b-button type="submit" v-on:click.prevent="onSubmit" class="btn btn-primary" :size="lg">
                                                {{rule.id ? 'Update' : 'Add'}} rule
                                            </b-button>
                                        </b-col>
                                        <b-col>
                                            <button type="submit" v-if="rule.id" v-on:click.prevent="onCancel" class="btn btn-secondary">Cancel</button>
                                        </b-col>
                                    </div>

                                </div>
                            </form-row>
                        </div>
                    </div>
                </div>

            </div>

        </div>

</template>


<script>
    import Vue from 'vue'
    import BootstrapVue from 'bootstrap-vue'
    import 'bootstrap/dist/css/bootstrap.css'
    import 'bootstrap-vue/dist/bootstrap-vue.css'
    import datePicker from 'vue-bootstrap-datetimepicker';
    import 'eonasdan-bootstrap-datetimepicker/build/css/bootstrap-datetimepicker.css';
    Vue.use(BootstrapVue);
    Vue.use(datePicker);
    export default {
        props: ['rule'],
        data() {
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
                dynamicAreas: []
            }
        },
        watch: {
            'rule.id'() {
                this.formErrors = {}
                this.selectedFile = undefined
                this.selectedFileName = this.rule.imageName
            }
        },
        methods: {
            validateForm() {
                const errors = {}
                if (!this.rule.title) {
                    errors.title = 'Title is required'
                    this.dismissCountDown = 5
                }
                if (!this.rule.score) {
                    errors.score = 'Score is required'
                    this.dismissCountDown = 5
                }
                if (!this.rule.startValidity) {
                    errors.startValidity = 'Start validity date is required'
                    this.dismissCountDown = 5
                }
                if (!this.rule.endValidity) {
                    errors.endValidity = 'End validity date is required'
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
                    this.$emit('submit', this.rule)
                }
            },
            countDownChanged(dismissCountDown) {
                this.dismissCountDown = dismissCountDown
            },
        }
    }
</script>

<style scoped>
    .card.col label {
        display: block;
    }
    form {
        margin-bottom: 24px;
    }
    form-row {
        display: flex;
        flex-wrap: wrap;
        margin-right: -5px;
        margin-left: -5px;
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
        width: 100%;
    }
    .card {
        position: relative;
        border-radius: 3px;
        background: #ffffff;
        margin-bottom: 20px;
        width: 100%;
        box-shadow: 0 1px 1px rgba(0, 0, 0, 0.1);
        margin: 10px auto;
        padding: 15px;
        flex-basis: 0;
        flex-grow: 1;
    }
    .require-msg{
        max-width: 100% !important;
        font-size: 14px;
        padding: 10px;
    }
    input {
        width: 100%;
    }
    textarea#ruleDescription {
        width: 100%;
    }
    input[type="checkbox"] {
        width: auto;
        margin-bottom: 10px;
    }
   /* button.btn.btn-link, h5.mb-0 {
        color: #3c8dbc;
        background: transparent;
        border: 1px solid lightblue;
        border-radius: 50%;
        font-weight: bolder;
        text-decoration: none;
        position: absolute;
        top: 10px;
    }
    h5.mb-0:hover, h5.mb-0:focus{
        color: #3c8dbc;
        background: transparent;
        border: 1px solid blue;
    }*/
    h5.mb-0 {
        height: auto;
    }
    div#headingOne:hover {
        background: transparent;
    }
    button.btn.btn-link.primary.collapsed, button.btn.btn-link.primary {
        background: #3c8dbc;
        color: white;
        padding: 5px 10px;
        position: absolute;
        right: 10px;
        top: 10px;
        text-decoration: none;
    }
    button.btn.btn-link.primary{
        background: #3c8dbc;
    }
    .col-sm-12.card {
        position: relative;
        border-radius: 3px;
        background: #ffffff;
        margin-bottom: 20px;
        width: 100%;
        box-shadow: none;
        margin: 0 auto;
        padding: 0;
        border: none;
    }
    .collapse {
        top: 15px;
    }
</style>

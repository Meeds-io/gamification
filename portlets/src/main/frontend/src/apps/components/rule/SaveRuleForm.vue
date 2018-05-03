<!-- src/components/SaveProductForm.vue  -->
<template>

    <div>
        <b-container fluid>
            <b-col sm="12">
                <h5 class="mt-0">Manage gamification's rules</h5>
                <b-form-row>
<b-col class="card">
                    <b-form-group id="titleInputGroup" label="Title:" label-for="titleInput"  >
                        <b-form-input id="titleInput" type="text" v-model="rule.title" required placeholder="Enter rule's title">
                        </b-form-input>

                        <b-alert v-if="formErrors.title" :show="dismissCountDown" dismissible variant="warning" @dismissed="dismissCountdown=0" @dismiss-count-down="countDownChanged">
                            This alert will dismiss after {{dismissCountDown}} seconds...
                        </b-alert>
                    </b-form-group>

                    <b-form-group id="descriptionInputGroup" label="Description:" label-for="descriptionInput" >
                        <b-form-textarea id="ruleDescription" v-model="rule.description" placeholder="Enter description" :rows="3" :max-rows="6">
                        </b-form-textarea>
                    </b-form-group>

                    <!-- Rule Score Component -->



                    <b-form-group id="scoreInputGroup" label="Score:" label-for="scoreInput" >
                        <b-form-input id="scoreDescription" type="number" v-model="rule.score" required placeholder="Enter rule's score">
                        </b-form-input>
                    </b-form-group>
                </b-col>
                    <!-- END -->
                    <b-col class="card">


                    <!-- Start validity date -->
                    <b-form-group id="startValidityInputGroup" label="Start validity:" label-for="startValidityInput" >
                        <date-picker name="endValidityDateInput" id="startValidityInput" v-model="rule.startValidity" :config="config" required placeholder="Enter rule's start validity"></date-picker>
                        <!--<b-form-input id="startValidityInput" type="date" v-model="rule.startValidity" required placeholder="Enter rule's start validity">
                        </b-form-input> -->
                    </b-form-group>
                    <!-- END -->

                    <!-- End validity date -->
                    <b-form-group id="endValidityInputGroup" label="End validity:" label-for="endValidityInput">
                        <date-picker name="endValidityDateInput" id="endValidityInput" v-model="rule.endValidity" :config="config" required placeholder="Enter rule's end validity"></date-picker>
                        <!--<b-form-input id="endValidityInput" type="date" v-model="rule.endValidity" required placeholder="Enter rule's end validity">
                        </b-form-input> -->
                    </b-form-group>
                    <!-- END -->
                        <!-- Rule Enable Component -->
                        <b-form-group id="enableCheckboxGroup">
                            <b-form-checkbox-group v-model="rule.enabled" id="enabledChecks">
                                <b-form-checkbox>Enable rule</b-form-checkbox>
                            </b-form-checkbox-group>
                        </b-form-group>
                        <!-- END -->
                        <b-row>
                       <b-col> <b-button type="submit" v-on:click.prevent="onSubmit" class="btn btn-primary" :size="lg" >
                            {{rule.id ? 'Update' : 'Add'}} rule
                        </b-button>
                        </b-col><b-col>
                        <b-button type="submit" v-if="rule.id" v-on:click.prevent="onCancel" class="btn btn-secondary" >Cancel</b-button>
                </b-col> </b-row>
                        <!-- END -->
                        </b-col>
                </b-form-row>
                <!-- Form's actions -->
              </b-col>

        </b-container>
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
                }
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
                    this.dismissCountDown=5
                }

                if (!this.rule.score) {
                    errors.score = 'Score is required'
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
    form {
        margin-bottom: 24px;
    }
    h5.mt-0{
        color: #578dc9;
        font-family: Helvetica,arial,sans-serif;
        line-height: 20px;
        font-size:1.5em;
        text-transform:uppercase;
        font-weight:bold;
        text-align:center;
        padding:20px 0px;
    }
    label{
        display: inline-block;
        max-width: 100%;
        margin-bottom: 5px;
        font-weight: 700;
        color: #333;
    }

    input[type="number"]{
        font-size: 15px;
        height: 40px;
        padding: 0 10px;
        border: 2px solid #e1e8ee;
        border-radius: 5px;
        box-shadow: none;
        max-height: 40px;
        text-overflow: ellipsis;
    }
    input[type="number"]:focus:invalid:focus, input[type="date"]:focus:invalid:focus{
        border-color: #e9322d;
        -webkit-box-shadow: 0 0 6px #f8b9b7;
        -moz-box-shadow: 0 0 6px #f8b9b7;
        box-shadow: 0 0 6px #f8b9b7;
    }
    .card{
        position: relative;
        border-radius: 3px;
        background: #ffffff;
        border-top: 3px solid #d2d6de;
        margin-bottom: 20px;
        width: 100%;
        box-shadow: 0 1px 1px rgba(0,0,0,0.1);
        border-top-color: #3c8dbc;
        margin: 0px 11px;
        padding: 15px;

    }

</style>
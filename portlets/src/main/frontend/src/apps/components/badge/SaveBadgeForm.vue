<!-- src/components/SaveProductForm.vue  -->
<template>

    <div>
        <b-container fluid>
            <b-col sm="12">
                <h5 class="mt-0">Manage gamification's badges</h5>
                <b-form>
                    <b-form-group id="titleInputGroup" label="Title:" label-for="titleInput">
                        <b-form-input id="titleInput" type="text" v-model="badge.title" required placeholder="Enter badge's title">
                        </b-form-input>

                        <b-alert v-if="formErrors.title" :show="dismissCountDown" dismissible variant="warning" @dismissed="dismissCountdown=0" @dismiss-count-down="countDownChanged">
                            Title should not be blanc {{dismissCountDown}} seconds...
                        </b-alert>
                    </b-form-group>

                    <!-- Badge Description Component -->

                    <b-form-group id="descriptionInputGroup" label="Description:" label-for="descriptionInput">
                        <b-form-textarea id="badgeDescription" v-model="badge.description" placeholder="Enter description" :rows="3" :max-rows="6">
                        </b-form-textarea>
                    </b-form-group>

                    <!-- Badge icon Component -->
                    <!--
                    <b-form-group id="neededScoreInputGroup" label="Score:" label-for="neededScoreInput">
                        <b-form-input id="neededScoreDescription" type="number" v-model="badge.neededScore" required placeholder="Enter badge's needed score">
                        </b-form-input>
                    </b-form-group>
                    -->
                    <!-- END -->

                    <!-- Badge neededScore Component -->
                    <b-form-group id="neededScoreInputGroup" label="Needed score:" label-for="neededScoreInput">
                        <b-form-input id="neededScoreInput" type="number" v-model="badge.neededScore" required placeholder="Enter badge's needed score">
                        </b-form-input>
                    </b-form-group>
                    <!-- END -->

                    <!-- Badge Start validity Date Component -->
                    <b-form-group id="startValidityDateInputGroup " label="Start Validity Date:" label-for="startValidityDateInput">

                        <date-picker name="startValidityDateInput" id="startValidityDateInput" v-model="badge.startValidityDate" :config="config" required placeholder="Enter badge's start validity date"></date-picker>
                    </b-form-group>
                    <!-- END -->

                    <!-- Badge End validity date component -->
                    <b-form-group id="endValidityDateInputGroup" label="End Validity Date:" label-for="endValidityDateInput">
                        <date-picker name="endValidityDateInput" id="endValidityDateInput" v-model="badge.endValidityDate" :config="config" required placeholder="Enter badge's start validity date"></date-picker>
                    </b-form-group>
                    <!-- END -->

                    <!-- Badge rule Component -->
                    <b-form-group id="ruleInputGroup" label="Rule:" label-for="ruleInput">
                        <b-form-select id="ruleInput" :options="rules" required v-model="badge.rules">
                        </b-form-select>
                    </b-form-group>
                    <!-- END -->

                    <!-- Badge Enable Component -->
                    <b-form-group id="isEnabledCheckboxGroup">
                        <b-form-checkbox-group v-model="badge.isEnabled" id="enabledChecks">
                            <b-form-checkbox>Enable badge</b-form-checkbox>
                        </b-form-checkbox-group>
                    </b-form-group>
                    <!-- END -->

                    <!-- Form's actions -->
                    <b-button type="submit" v-on:click.prevent="onSubmit" class="btn btn-primary">
                        {{badge.id ? 'Update' : 'Add'}} badge
                    </b-button>
                    <b-button type="submit" v-if="badge.id" v-on:click.prevent="onCancel" class="btn btn-secondary">Cancel</b-button>
                    <!-- END -->
                </b-form>
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
        props: ['badge'],
        data() {
            return {
                formErrors: {},
                selectedFile: undefined,
                selectedFileName: '',
                dismissSecs: 5,
                dismissCountDown: 0,
                date: new Date(),
                config: {
                    format: 'jj/mm/aaaa',
                    useCurrent: false,
                }
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
    }
    input[type="number"], input[type="date"]{
        font-size: 15px;
        height: 40px;
        padding: 0 10px;
        border: Solid 2px #e1e8ee;
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
</style>
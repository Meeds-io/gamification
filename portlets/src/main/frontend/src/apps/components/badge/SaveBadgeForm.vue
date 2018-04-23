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
                    <b-form-group id="startValidityDateInputGroup" label="Start Validity Date:" label-for="startValidityDateInput">
                        <b-form-input id="startValidityDateInput" type="date" v-model="badge.startValidityDate" required placeholder="Enter badge's start validity date">
                        </b-form-input>
                    </b-form-group>
                    <!-- END -->

                    <!-- Badge End validity date component -->
                    <b-form-group id="endValidityDateInputGroup" label="End Validity Date:" label-for="endValidityDateInput">
                        <b-form-input id="endValidityDateInput" type="date" v-model="badge.startValidityDate" required placeholder="Enter badge's start validity date">
                        </b-form-input>
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
    import axios from 'axios'
    import BootstrapVue from 'bootstrap-vue'

    Vue.use(BootstrapVue);

    export default {
        props: ['badge'],
        data() {
            return {
                formErrors: {},
                selectedFile: undefined,
                selectedFileName: '',
                dismissSecs: 5,
                dismissCountDown: 0,
                rules: []
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

                if (!this.badge.score) {
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
                    this.$emit('submit', this.badge)
                }
            },
            countDownChanged(dismissCountDown) {
                this.dismissCountDown = dismissCountDown
            },
        },
        
        created() {
            // Fetches rules when the component is created.
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
    form {
        margin-bottom: 24px;
    }
</style>
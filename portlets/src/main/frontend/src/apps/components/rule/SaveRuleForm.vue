<!-- src/components/SaveProductForm.vue  -->
<template>

    <div>
        <b-container fluid>
            <b-col sm="12">
                <h5 class="mt-0">Manage gamification's rules</h5>
                <b-form>
                    <b-form-group id="titleInputGroup" label="Title:" label-for="titleInput">
                        <b-form-input id="titleInput" type="text" v-model="rule.title" required placeholder="Enter rule's title">
                        </b-form-input>

                        <b-alert v-if="formErrors.title" :show="dismissCountDown" dismissible variant="warning" @dismissed="dismissCountdown=0" @dismiss-count-down="countDownChanged">
                            This alert will dismiss after {{dismissCountDown}} seconds...
                        </b-alert>
                    </b-form-group>

                    <!-- Rule Description Component -->

                    <b-form-group id="descriptionInputGroup" label="Description:" label-for="descriptionInput">
                        <b-form-textarea id="ruleDescription" v-model="rule.description" placeholder="Enter description" :rows="3" :max-rows="6">
                        </b-form-textarea>
                    </b-form-group>

                    <!-- Rule Score Component -->

                    <b-form-group id="scoreInputGroup" label="Score:" label-for="scoreInput">
                        <b-form-input id="scoreDescription" type="number" v-model="rule.score" required placeholder="Enter rule's score">
                        </b-form-input>
                    </b-form-group>
                    <!-- END -->

                    <!-- Start validity date -->
                    <b-form-group id="startValidityInputGroup" label="Start validity:" label-for="startValidityInput">
                        <b-form-input id="startValidityInput" type="date" v-model="rule.startValidity" required placeholder="Enter rule's start validity">
                        </b-form-input>
                    </b-form-group>
                    <!-- END -->

                    <!-- End validity date -->
                    <b-form-group id="endValidityInputGroup" label="End validity:" label-for="endValidityInput">
                        <b-form-input id="endValidityInput" type="date" v-model="rule.endValidity" required placeholder="Enter rule's end validity">
                        </b-form-input>
                    </b-form-group>
                    <!-- END -->

                    <!-- Rule Enable Component -->
                    <b-form-group id="enableCheckboxGroup">
                        <b-form-checkbox-group v-model="rule.enabled" id="enabledChecks">
                            <b-form-checkbox>Enable rule</b-form-checkbox>
                        </b-form-checkbox-group>
                    </b-form-group>
                    <!-- END -->

                    <!-- Form's actions -->
                    <b-button type="submit" v-on:click.prevent="onSubmit" class="btn btn-primary">
                        {{rule.id ? 'Update' : 'Add'}} rule
                    </b-button>
                    <b-button type="submit" v-if="rule.id" v-on:click.prevent="onCancel" class="btn btn-secondary">Cancel</b-button>
                    <!-- END -->
                </b-form>
            </b-col>
        </b-container>
    </div>

</template>

<script>
    export default {
        props: ['rule'],
        data() {
            return {
                formErrors: {},
                selectedFile: undefined,
                selectedFileName: '',
                dismissSecs: 5,
                dismissCountDown: 0
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
</style>
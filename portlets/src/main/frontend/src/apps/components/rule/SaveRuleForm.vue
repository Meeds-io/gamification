<template>
    <div>
        <div class="col-sm-12 fluid">
            <div class="btn" id="headingOne">
                <h5 class="mb-0">
                    <button aria-controls="collapseOne" aria-expanded="false" class="btn btn-link primary" data-target="#collapseOne" data-toggle="collapse" type="button">Add rule</button>
                </h5>
            </div>
            <div aria-labelledby="headingOne" class="collapse show" data-parent="#accordionExample" id="collapseOne" style="height: 0px;top: 100px; border: none;">

                <div class="UIPopupWindow uiPopup UIDragObject NormalStyle" id="myForm" style="width: 560px; z-index:1000000; position: relative; top: 20%; left: auto; margin: 0 auto 20px; z-index: 1; max-width: 100%;">
                    <!--  <div tabindex="-1" style="position: fixed; z-index: 10000; top: 0px; left: 0px; width: 1284px; height: 377px;" class="uiPopupWrapper"> </div> -->
                    <div class="popupHeader ClearFix">
                        <a class="uiIconClose pull-right"  aria-controls="collapseOne" aria-expanded="false" data-target="#collapseOne" data-toggle="collapse" aria-hidden="true" data-dismiss="modal" ></a>
                        <span class="PopupTitle popupTitle">Add Rule</span>
                    </div>
                    <div class="PopupContent popupContent">
                        <form id="titleInputGroup">
                            <label class="col-form-label pt-0">Title:</label>
                            <input id="titleInput" type="text" v-model="rule.title" required placeholder="Enter rule's title">
                            </input>
                            <b-alert v-if="formErrors.title" :show="dismissCountDown" dismissible variant="danger" class="require-msg" @dismissed="dismissCountdown=0" @dismiss-count-down="countDownChanged">
                                Rule title is required please enter a title {{dismissCountDown}} ...
                            </b-alert>
                        </form>
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
                        <label class="switch">
                            <input type="checkbox" v-model="rule.enabled" >
                            <span class="slider round"></span>
                            <span class="absolute-no">NO</span>
                        </label>
                        <form id="areaSelectboxGroup">
<!--                            <select v-model="rule.area" class="mb-4">
                                <template slot="first">
                                    <option :value="null" disabled>&#45;&#45; Please select an area &#45;&#45;</option>
                                </template>
                                <option value="Social">Social</option>
                                <option value="Knowledge">Knowledge</option>
                                <option value="Teamwork">Teamwork</option>
                                <option value="Feedback">Feedback</option>
                                <option value="Reward">reward</option>
                            </select>-->
                            <select v-model="rule.domainDTO" class="mb-4">
                                <option :value="null" disabled>-- Please select an area --</option>
                                <option v-for="option in domains" v-bind:value="option">
                                    {{ option.title }}
                                </option>
                            </select>

                        </form>
                        <div class="row">
                            <b-col>
                                <b-button class="btn-primary" type="submit" v-on:click.prevent="onSubmit" >
                                    {{rule.id ? 'Update' : 'Add'}}
                                </b-button>
                            </b-col>
                        </div>
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
    //import datePicker from 'vue-bootstrap-datetimepicker';
    //   import 'eonasdan-bootstrap-datetimepicker/build/css/bootstrap-datetimepicker.css';
    Vue.use(BootstrapVue);
    import axios from 'axios';
    // Vue.use(datePicker);
    export default {
        props: ['rule'],
        data: function (){
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
                dynamicAreas: [],
                domains: []
            }
        },
        watch: {
            'rule.id'() {
                this.formErrors = {}
                this.selectedFile = undefined
                this.selectedFileName = this.rule.imageName
                },
            'rule.domainDTO'() {
                this.rule.area = this.rule.domainDTO.title
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
                    this.createRule(this.rule)
                    this.SaveRuleForm.update()
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
            createRule(ruleDTO) {
                axios.post(`/rest/gamification/rules/add`, ruleDTO)
                    .then(response => {
                        this.$emit('sucessAdd', this.rule)
                    })
                    .catch(e => {
                        this.$emit('failAdd', this.rule)
                    })
                //this.resetRuleInForm()
            },
            created() {
                axios.get(`/rest/gamification/api/v1/domains`)
                    .then(response => {
                        this.domains = response.data;
                    })
                    .catch(e => {
                        this.errors.push(e)
                    })
            }
        }
    }
</script>

<style scoped>
    .card.col label {
        display: block;
    }
    .col-sm-11.fluid {
        top: 10px;
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
    btn {
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
        box-shadow: 0 1px 1px rgba(0, 0, 0, 0.1);
        margin: 10px auto;
        padding: 15px;
        flex-basis: 0;
        flex-grow: 1;
    }
    .require-msg{
        max-width: 100% !important;
        font-size: 14px;
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
    div#headingOne:hover {
        background: transparent;
    }
    div#headingOne {
        box-shadow: none;
    }
    .btn {
        display: inline-block;
        padding: 1.25rem 0.75rem;
    }
    button.btn.btn-link.primary.collapsed, button.btn.btn-link.primary, btn{
        background: #3c8dbc;
        color: white;
        padding: 5px 25px;
        position: absolute;
        right: 42px;;
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
    .btn.btn-primary, .btn-primary {
        padding: 7px 20px;
        margin: 0 5px;
        background: #578dc9;
    }
    div#collapseOne {
        position: absolute;
        width: 100%;
        min-width: 100%;
        z-index: 100;
        padding: 11px 20px;
    }
    /* switch test */
    .switch {
        position: relative;
        display: inline-block;
        width: 150px;
        height: 50px;
        bottom: 45px;
        zoom: 40%;
    }
    .switch input {display:none;}
    .slider {
        position: absolute;
        cursor: pointer;
        overflow: hidden;
        top: 0;
        left: 0;
        right: 0;
        bottom: 0;
        background-color: #f2f2f2;
        -webkit-transition: .4s;
        transition: .4s;
    }
    .slider:before {
        position: absolute;
        z-index: 2;
        content: "";
        height: 55px;
        width: 54px;
        left: 2px;
        bottom: 0px;
        background-color: darkgrey;
        -webkit-box-shadow: 0 2px 5px rgba(0, 0, 0, 0.22);
        box-shadow: 0 2px 5px rgba(0, 0, 0, 0.22);
        -webkit-transition: .4s;
        transition: all 0.4s ease-in-out;
    }
    .slider:after {
        position: absolute;
        left: 0;
        z-index: 1;
        content: "YES";
        font-size: 45px;
        text-align: left !important;
        line-height: 51px;
        padding-left: 0;
        width: 130px;
        color: #fff;
        height: 50px;
        border-radius: 100px;
        background-color: #578dc9;
        -webkit-transform: translateX(-160px);
        -ms-transform: translateX(-160px);
        transform: translateX(-160px);
        transition: all 0.4s ease-in-out;
    }
    input:checked + .slider:after {
        -webkit-transform: translateX(0px);
        -ms-transform: translateX(0px);
        transform: translateX(0px);
        /*width: 235px;*/
        padding-left: 25px;
    }
    input:checked + .slider:before {
        background-color: #fff;
    }
    input:checked + .slider:before {
        -webkit-transform: translateX(160px);
        -ms-transform: translateX(160px);
        transform: translateX(160px);
    }
    /* Rounded sliders */
    .slider.round {
        border-radius: 100px;
    }
    .slider.round:before {
        border-radius: 50%;
    }
    .absolute-no {
        position: absolute;
        left: 0;
        color: darkgrey;
        text-align: right !important;
        font-size: 40px;
        width: calc(100% - 25px);
        height: 84px;
        line-height: 51px;
        cursor: pointer;
    }
    /* div#collapseOne .card {
         position: relative;
         top: 0;
         left: 0;
         right: 0;
         bottom: 0;
         margin: 0;
         background-color: rgba(0, 0, 0, 0.3);
     } */
</style>
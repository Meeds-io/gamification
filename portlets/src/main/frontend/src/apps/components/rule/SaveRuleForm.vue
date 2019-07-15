<template>
    <div>
        <div class="col-sm-12 fluid">

            <div class="btn" id="headingOne">

                <button aria-controls="collapseOne" aria-expanded="false" class="btn btn-link primary" data-target="#collapseOne" data-toggle="collapse" type="button">add Rule</button>
            </div>

            <div aria-labelledby="headingOne" class="collapse show" data-parent="#accordionExample" id="collapseOne" style="height: 0px; transition: inherit;/* top:-90px; */ ">

                <div class="card-body">
                    <div class="UIPopupWindow uiPopup UIDragObject NormalStyle" id="myForm" style="width: 760px; z-index:1000000; position: relative; left: auto; margin: 0 20px; z-index: 1; max-width: 100%;margin: 0 auto;height: 100%;">
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
                                    Rule title is required please enter a title {{dismissCountDown}}
                                </b-alert>

                            </form>
                            <form id="descriptionInputGroup">
                                <label class="col-form-label pt-0">Description:</label>
                                <textarea id="ruleDescription" v-model="rule.description" placeholder="Enter description" :rows="3" :max-rows="6">
                            </textarea>
                            </form>
                            <b-form id="scoreInputGroup">

                                <label id="scoreInputGroup" for="scoreInput" class="col-form-label pt-0">Score:</label>
                                <input id="scoreDescription" type="number" v-model="rule.score" required placeholder="Enter rule's score">

                                <b-alert v-if="formErrors.score" :show="dismissCountDown" dismissible variant="danger" class="require-msg" @dismissed="dismissCountdown=0" @dismiss-count-down="countDownChanged">
                                    Rule score is required please enter a score {{dismissCountDown}}
                                </b-alert>
                            </b-form>
                            <form>

                                <label class="col-form-label pt-0">Enable:</label>
                               <label class="uiSwitchBtn">

                                <input type="checkbox" v-model="rule.enabled" >
                                <span class="slider round"></span>
                                <span class="absolute-no">NO</span>
                               </label>
                            </form>


                            <form id="areaSelectboxGroup">
                                <label class="col-form-label pt-0">Domain :</label>
                                <select v-model="rule.domainDTO" class="mb-4">
                                    <option :value="null" disabled>-- Please select an area --</option>
                                    <option v-for="option in domains" v-bind:value="option">
                                        {{ option.title }}
                                    </option>
                                </select>
                            </form>
                            <div class="row">
                                <b-col>
                                    <button type="submit" v-on:click.prevent="onCancel" class="btn secondary pull-right" data-target="#collapseOne" data-toggle="collapse" aria-hidden="true" data-dismiss="modal">Cancel</button>
                                    <b-button class="btn-primary pull-right" type="submit" v-on:click.prevent="onSubmit" >
                                        {{rule.id ? 'Update' : 'Confirm'}}
                                    </b-button>

                                </b-col>
                            </div>
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
        props: ['rule','domains'],
        data: function (){
            return {
                SaveRuleForm:'',
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
                this.SaveRuleForm.cancel()
            },
            onSubmit() {
                if (this.validateForm()) {
                    this.createRule(this.rule)
                    this.SaveRuleForm.update()
                    this.SaveRuleForm.cancel()
                }
            },
            countDownChanged(dismissCountDown) {
                this.dismissCountDown = dismissCountDown
            },
            confirm() {
                this.$modals.confirm({
                    message: 'Confirm?',
                    onApprove: () => {
                    },
                    onCancel: () => {
                    },
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
            }
        }
        }
</script>

<style scoped>
    .card-body label {
        display: block;
    }
    .col-sm-11.fluid {
        top: 10px;
    }
    form {
        margin-bottom: 24px;
    }
    form-row {
        display: flex;t
        flex-wrap: wrap;
        margin-right: -5px;
        margin-left: -5px;
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
    form#areaSelectboxGroup {
        margin-bottom: 0px;
    }
    input[type="number"]:focus:invalid:focus,
    input[type="date"]:focus:invalid:focus {
        border-color: #e9322d;
        -webkit-box-shadow: 0 0 6px #f8b9b7;
        -moz-box-shadow: 0 0 6px #f8b9b7;
        box-shadow: 0 0 6px #f8b9b7;
        width: 100%;
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
        font-size: 15px;
    }
    input[type="checkbox"] {
        width: auto;
        margin-bottom: 10px;
    }
    div#headingOne:hover {
        background: transparent;
    }
    div#headingOne {
        box-shadow: none;
    }
    .btn {
        display: inline-block;
        padding: 1.5rem;
    }
    button.btn.btn-link.primary.collapsed, button.btn.btn-link.primary, btn{
        background: #3c8dbc;
        color: white;
        padding: 7px 25px;
        position: absolute;
        right: 42px;;
        top: 12px;
        text-decoration: none;
    }
    button.btn.btn-link.primary{
        background: #3c8dbc;
    }
    button.btn.secondary {
        padding: 6px 25px;
        border: Solid 2px #e1e8ee;
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

        /* position: fixed;
        /* width: 100%; */
        /* min-width: 100%;
        z-index: 1000000;
        /* margin: 0 auto;
        margin-left: 50%;
        transform: translateX(-50%); */

        position: fixed; /* Stay in place */
        z-index: 10000; /* Sit on top */
      /*  padding-top: 100px; /* Location of the box */
        left: 0;
        top: 0;
        bottom: 0;
        width: 100%; /* Full width */
        height: 100%; /* Full height */
        overflow: auto; /* Enable scroll if needed */
        background-color: rgb(0,0,0); /* Fallback color */
        background-color: rgba(0,0,0,0.4); /* Black w/ opacity */
    }
    /* switch test */
    .uiSwitchBtn {
        position: relative;
        display: inline-block;
        width: 150px;
        height: 40px;
        bottom: 5px;
        zoom: 40%;
    }
    .uiSwitchBtn input {display:none;}
    .slider {
        position: absolute;
        cursor: pointer;
        overflow: hidden;
        top: 0;
        left: 0;
        right: 0;
        bottom: 0;
        background-color: #e6e6e6;
        -webkit-transition: .4s;
        transition: .4s;
    }
    .slider:before {
        position: absolute;
        z-index: 2;
        content: "";
        height: 35px;
        width: 35px;
        left: 2px;
        bottom: 5px;
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
        font-size: 33px;
        text-align: left !important;
        line-height: 45px;
        padding-left: 0;
        width: 125px;
        color: #fff;
        height: 39px;
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
        font-size: 28px;
        width: calc(100% - 40px);
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
<template>
    <div>
        <div class="col-sm-12 fluid">
            <div class="pull-right" id="headingOne">
                <button aria-controls="collapseOne" aria-expanded="true" class="btn btn-primary" data-target="#collapseOne" data-toggle="collapse" type="button"  v-on:click.prevent="collapseButton()">Add badge</button>
            </div>
            <div aria-labelledby="headingOne" class="collapse show" :class="isShown ? '' : 'out'" data-parent="#accordionExample" id="collapseOne"  style="height: 0px; transition: inherit;">
                <div class="card-body">
                    <div class="UIPopupWindow uiPopup UIDragObject NormalStyle" id="myForm" style="width: 760px; z-index:1000000; position: relative; left: auto; margin: 0 20px; z-index: 1; max-width: 100%;margin: 0 auto;height: 100%;">
                        <div class="popupHeader ClearFix">
                            <a class="uiIconClose pull-right" v-on:click.prevent="collapseButton(), onCancel()" ></a>
                            <span class="PopupTitle popupTitle">Add Badge</span>
                        </div>
                        <div class="PopupContent popupContent">
                            <form id="titleInputGroup">
                                <label class="pt-0">Title:</label>
                                <input id="titleInput" type="text" v-model="badge.title" class="form-control" required placeholder="Enter badge's title">
                                </input>

                                <b-alert v-if="formErrors.title" :show="dismissCountDown" dismissible variant="danger" class="require-msg" @dismissed="dismissCountdown=0"
                                         @dismiss-count-down="countDownChanged">
                                    Badge title is required please enter a title {{dismissCountDown}}
                                </b-alert>
                            </form>

                            <div id="descriptionInputGroup">
                                <label class="pt-0" id="descriptionInput">Description:</label>
                                <textarea id="badgeDescription" v-model="badge.description" class="form-control" placeholder="Enter description" :rows="3" :max-rows="6">
                    </textarea>
                            </div>
                            <form id="neededScoreInputGroup" >
                                <label id="Needed" label-for="neededScoreInput" class="pt-0">score:</label>
                                <input id="neededScoreInput" type="number" v-model="badge.neededScore" class="form-control" required placeholder="Enter badge's needed score">
                                </input>
                                <b-alert v-if="formErrors.neededScore" :show="dismissCountDown" dismissible variant="danger" class="require-msg" @dismissed="dismissCountdown=0"
                                         @dismiss-count-down="countDownChanged">
                                    Badge needed score is required please enter a value {{dismissCountDown}}
                                </b-alert>
                            </form>
                            <form id="iconInputGroup">
                                <label id="iconInput" label-for="iconInput" class="pt-0"> Icon: </label>
                                <b-form-file id="iconInput" v-model="badge.icon"  class="form-control" required  placeholder="Choose a file..." accept="image/jpeg, image/png, image/gif"></b-form-file>
                                <b-alert v-if="formErrors.icon" :show="dismissCountDown" dismissible variant="danger" class="require-msg" @dismissed="dismissCountdown=0"
                                         @dismiss-count-down="countDownChanged">
                                    Badge icon is required please enter a badge {{dismissCountDown}}
                                </b-alert>
                            </form>
                            <label class="pt-0">Domain:</label>
                            <form id="domainSelectboxGroup">

                                <select v-model="badge.domainDTO" class="mb-4">
                                    <option :value="null" disabled>Select your Domain</option>
                                    <option v-for="option in domains" v-bind:value="option">
                                        {{ option.title }}
                                    </option>
                                </select>

                            </form>

                            <label class="pt-0">Enabled:</label>
                            <label class="uiSwitchBtn">

                                <input type="checkbox" v-model="badge.enabled" >
                                <span class="slider round"></span>
                                <span class="absolute-no">NO</span>
                            </label>

                            <b-row style="display: inherit;">
                                <b-col>

                                    <button type="cancel" v-on:click.prevent="collapseButton(), onCancel()" class="btn secondary pull-right" >Cancel</button>
                                    <button class="btn-primary pull-right" type="submit" v-on:click.prevent="onSubmit(), showAlert()">
                                        {{badge.id ? 'Update' : 'Confirm'}}
                                    </button>
                                </b-col>

                            </b-row>
                        </div>
                    </div>
                </div>

            </div>
        </div>
    </div>
</template>
<script>
    import Vue from 'vue'
    import axios from 'axios'
    import BootstrapVue from 'bootstrap-vue'
    import 'bootstrap-vue/dist/bootstrap-vue.css'
    // import datePicker from 'vue-bootstrap-datetimepicker'
    // import 'eonasdan-bootstrap-datetimepicker/build/css/bootstrap-datetimepicker.css';
    Vue.use(BootstrapVue);
    //  Vue.use(datePicker);
    export default {
        props: ['badge','domains'],
        data: function () {
            return {
                formErrors: {},
                selectedFile: undefined,
                selectedFileName: '',
                dismissSecs: 5,
                dismissCountDown: 0,
                isShown: false,
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
            ,
            'badge.domainDTO'() {
                this.badge.domain = this.badge.domainDTO.title
            }
        },
        methods: {
            validateForm() {
                const errors = {}
                if (!this.badge.title) {
                    errors.title = 'Title is required'
                    this.dismissCountDown = 5
                }
                if (!this.badge.icon) {
                    errors.icon = 'Needed icon is required'
                    this.dismissCountDown = 5
                }
                if (!this.badge.neededScore) {
                    errors.neededScore = 'Needed score is required'
                    this.dismissCountDown = 5
                }
                this.formErrors = errors
                return Object.keys(errors).length === 0
            },
            collapseButton() {
                this.isShown = !this.isShown;
            },
            createBadge(badgeDTO) {
                const formData = new FormData();
                formData.append('file', badgeDTO.icon)
                const MAX_RANDOM_NUMBER = 100000;
                const uploadId = Math.round(Math.random() * MAX_RANDOM_NUMBER);
                axios.post(`/portal/upload?uploadId=${uploadId}&action=upload`, formData,
                    {
                        headers: {
                            'Content-Type': 'multipart/form-data'
                        }
                    }).then(response => {
                    badgeDTO.uploadId=uploadId
                    axios.post(`/rest/gamification/badges/add`, badgeDTO)
                        .then(response => {
                            this.addSuccess = true
                            this.updateMessage = 'added'
                            this.$emit('submit', this.badge)
                        })
                        .catch(e => {
                            this.addError = true
                            this.errors.push(e)
                        })
                })
                    .catch(e => {
                        console.log("Error")
                    })
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
                    this.createBadge(this.badge)
                    this.collapseButton()
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
    }
</script>


<style scoped>
    form {
        margin-bottom: 24px;
    }
    .card.col label {
        display: block;
    }
    div#headingOne button.btn.btn-primary {
        margin: 15px 12px 5px;
        border-radius: 3px;
    }
    .btn-primary:focus, .btn-primary.focus {
        box-shadow: inset 0 0 0 0.2rem rgba(38, 143, 255, 0.5);
    }
    .collapse.show.out {
        display: none;
    }
    input.custom-file.b-form-file {
        display: inline-block;
        height: calc(2.25rem + 2px);
        position: relative;
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
        display: block;
        max-width: 100%;
        margin: 5px 0;
        font-weight: 700;
        color: #333;
    }
    input[type="number"] {
        font-size: 15px;
        height: 40px;
        padding: 0 10px;
        border: 1px solid #e1e8ee;
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
        margin-bottom: 20px;
        width: 100%;
        box-shadow: 0 1px 1px rgba(0, 0, 0, 0.1);
        margin: 10px auto;
        padding: 15px;
        flex-basis: 0;
        flex-grow: 1;
        max-width: 100%;
    }
    .require-msg {
        max-width: 100% !important;
        font-size: 14px;
    }
    msg.alert-dismissible.alert-danger {
        display: -webkit-inline-box;
        width: auto;
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
    .card label {
        display: block;
    }
    form {
        display: flex;
        flex-wrap: wrap;
    }
    button.btn.secondary {
        padding: 8px 25px;
        margin-left: 25px;
        border: 1px solid #e1e8ee !important;
        color: #4d5466;
        background-color: transparent !important;
    }
    div#headingOne:hover {
        background: transparent;
    }
    .custom-file-label:after {
        position: absolute;
        right: 5px;
        text-overflow: ellipsis;
    }
    .custom-file-label {
        position: relative!important;
        left: 0;
        z-index: 1;
        height: 36px;
        padding: 0 10px;
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
    button.btn-primary.pull-right {
        border-radius: 3px;
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
    div#collapseOne {
        position: fixed;
        z-index: 10000;
        left: 0;
        top: 0;
        bottom: 0;
        width: 100%;
        height: 100%;
        overflow: auto;
        background-color: rgb(0,0,0);
        background-color: rgba(0,0,0,0.4);
    }
    /* switch */
       .uiSwitchBtn {
           position: relative;
           display: inline-block;
           width: 60px;
           height: 29px;
          /* zoom: 30%; */
           top: 0.4rem;
       }
       .uiSwitchBtn input {display:none;}
       .slider {
           position: absolute;
           cursor: pointer;
           overflow: hidden;
           top: 5px;
           left: 0;
           right: 0;
           bottom: 0;
           width: 60px;
           height: 20px;
           background-color: #f2f2f2;
           -webkit-transition: .4s;
           transition: .4s;
       }
       .slider:before {
           position: absolute;
           z-index: 2;
           content: "";
           height: 14px;
           width: 14px;
           left: 5px;
           bottom: 3px;
           background-color: darkgrey;
           -webkit-box-shadow: 0 2px 5px rgba(0, 0, 0, 0.22);
           box-shadow: 0 2px 5px rgba(0, 0, 0, 0.22);
           -webkit-transition: .4s;
           transition: all 0.4s ease-in-out;
       }
       .slider:after {
           position: absolute;
           left: -20px;
           z-index: 1;
           content: "YES";
           font-size: 13px;
           text-align: left!important;
           line-height: 19px;
           padding-left: 0;
           width: 95px;
           height: 26px!important;
           color: #f9f9f9;
           background-color: #477ab3;
           background-image: -moz-linear-gradient(top, #578dc9, #2f5e92);
           background-image: -webkit-gradient(linear, 0 0, 0 100%, from(#578dc9), to(#2f5e92));
           background-image: -webkit-linear-gradient(top, #578dc9, #2f5e92);
           background-image: -o-linear-gradient(top, #578dc9, #2f5e92);
           background-image: linear-gradient(to bottom, #578dc9, #2f5e92);
           background-repeat: repeat-x;
           filter: progid:DXImageTransform.Microsoft.gradient(startColorstr='#ff578dc9', endColorstr='#ff2f5e92', GradientType=0);
           -webkit-box-shadow: inset 0px 3px 5px #224469;
           -moz-box-shadow: inset 0px 3px 5px #224469;
           box-shadow: inset 0px 3px 5px #224469;
           -webkit-border-top-left-radius: 9px;
           -moz-border-radius-topleft: 9px;
           border-top-left-radius: 9px;
           -webkit-border-bottom-left-radius: 9px;
           -moz-border-radius-bottomleft: 9px;
           border-bottom-left-radius: 9px;
           height: 57px;
           border-radius: 100px;
           background-color: #578dc9;
           -webkit-transform: translateX(-190px);
           -ms-transform: translateX(-190px);
           transform: translateX(-190px);
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
           -webkit-transform: translateX(38px);
           -ms-transform: translateX(38px);
           transform: translateX(38px);
       }
       input:checked + .slider:before {
           -webkit-transform: translateX(38px);
           -ms-transform: translateX(38px);
           transform: translateX(38px);
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
           left: 27px;
           color: DarkGrey;
           text-align: right !important;
           font-size: 16px;
          /* width: calc(100% - 25px); */
           line-height: 30px;
           cursor: pointer;
       }
    .collapse.show.out {
        display: none;
    }

    .uiPopup .popupContent select, .modal.uiPopup .popupContent select {
        outline: none;
        border: 2px solid #e1e8ee;
        border-radius: 5px;
        box-shadow: none;
    }
    select:focus{
        border-color: #a6bad6;
        -webkit-box-shadow: inset 0 1px 1px rgba(0,0,0,.075), 0 0 5px #c9d5e6;
        -moz-box-shadow: inset 0 1px 1px rgba(0,0,0,.075),0 0 5px #c9d5e6;
        box-shadow: inset 0 1px 1px rgba(0,0,0,.075), 0 0 5px #c9d5e6;
        color: #333;
    }
</style>
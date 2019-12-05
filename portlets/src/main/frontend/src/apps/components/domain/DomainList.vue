<template>
    <b-container fluid>
        <b-row>
            <b-col sm="12">
                <div>
                    <div class="uiSearchForm uiSearchInput searchWithIcon">
                        <input :placeholder="this.$t('exoplatform.gamification.gamificationinformation.domain.search','Search')"
                               name="keyword" type="text" v-model="search" value="">
                        <a class="advancedSearch" data-placement="bottom" rel="tooltip" title="">
                            <i class="uiIconSearch uiIconLightGray"></i>
                        </a>
                    </div>
                    <div class="filter-bar" >
                         <select  v-model="enabledFilter">
                                        <option :value=null>{{$t(`exoplatform.gamification.all`,"All")}}</option>
                                        <option :value=true>{{$t(`exoplatform.gamification.enabled`,"Enabled")}}</option>
                                        <option :value=false>{{$t(`exoplatform.gamification.disabled`,"Disabled")}}</option>
                          </select>
                    </div>

                    <div class="alert alert-success" v-if="isdeleted" v-on:="onRemovealertclose()">
                        <button aria-label="Close" class="close" data-dismiss="alert" style="line-height: 27px; margin-right: 5px;" type="button">
                        </button>
                        <i class="uiIconSuccess"></i>
                        {{this.$t('exoplatform.gamification.domain.successdelete')}}
                    </div>
                    <div :class="isShown ? '' : 'out'" aria-labelledby="headingOne" class="collapse show"
                         data-parent="#accordionExample" id="collapseTwo" style="height: 0px; transition: inherit;">

                                         <div v-if="confirm" class="card-body">
                    <div class="UIPopupWindow uiPopup UIDragObject NormalStyle" id="validationForm" style="width: 760px; z-index:1000000; position: relative; left: auto; margin: 0 20px; z-index: 1; max-width: 100%;margin: 0 auto;height: 100%;">
                        <div class="popupHeader ClearFix">
                            <a class="uiIconClose pull-right" v-on:click.prevent="collapseButtonn(domain,true)"></a>

                            <span class="PopupTitle popupTitle">{{ this.$t('exoplatform.gamification.rule.popupdelete') }}</span>
                        </div>
                        <div class="PopupContent popupContent">
                            <div class="media">
                                <div class="pull-left">
                                    <i class="uiIconColorQuestion"></i>
                                </div>
                                <div class="media-body">
                                    <p class="msg"> {{ this.$t('exoplatform.gamification.areyousure.delete.domain','Are you sure you want to delete this domain ?')
                                            }}</p>
                                </div>
                            </div>
                            <div class="uiAction uiActionBorder pull-right" style="float: right">
                                <b-col>
                                    <button class="btn cancel pull-right" type="submit" v-on:click.prevent="collapseButtonn(domain,true), onCancel()">{{
                                            this.$t('exoplatform.gamification.gamificationinformation.domain.cancel')
                                            }}
                                    </button>

                                    <button class="btn-primary pull-right" type="submit" v-on:click.prevent="onRemove(editedDomain.id),collapseButtonn(domain,true)">
                                        {{
                                            this.$t('exoplatform.gamification.gamificationinformation.domain.confirm')
                                            }}
                                    </button>
                                </b-col>
                            </div>

                        </div>
                    </div>
                </div>

                        <div v-if="!confirm" class="card-body">
                            <div class="UIPopupWindow uiPopup UIDragObject NormalStyle" id="myForm"
                                 style="width: 760px; z-index:1000000; position: relative; left: auto; margin: 0 20px; z-index: 1; max-width: 100%;margin: 0 auto;height: 100%;">
                                <div class="popupHeader ClearFix">

                                    <a class="uiIconClose pull-right" v-on:click.prevent="collapseButtonn(domain,false)"></a>

                                    <span class="PopupTitle popupTitle">{{ this.$t('exoplatform.gamification.gamificationinformation.domain.popupedit',"Edit") }}</span>
                                </div>
                                <div class="PopupContent popupContent">

                                    <form id="titleInputGroup">
                                        <label class="col-form-label pt-0">{{
                                            this.$t('exoplatform.gamification.gamificationinformation.domain.Title',"Title") }}
                                            :</label>
                                        <input id="titleInput" placeholder="Enter domain's title" required type="text"
                                               v-model="editedDomain.title">
                                        </input>
                                    </form>


                                    <form id="descriptionInputGroup">
                                        <label class="col-form-label pt-0">{{
                                            this.$t('exoplatform.gamification.gamificationinformation.domain.Description',"Description")
                                            }} :</label>
                                        <textarea :max-rows="6" :rows="3" id="domainDescription"
                                                  placeholder="Enter description" v-model="editedDomain.description">
                                        </textarea>

                                    </form>
                            <form>
                                <label class="col-form-label pt-0">{{$t(`exoplatform.gamification.enabled`,"Enabled") }}:</label>
                                <label class="uiSwitchBtn">
                                    <input type="checkbox" v-model="editedDomain.enabled" >
                                    <span class="slider round"></span>
                                    <span class="absolute-no">{{$t(`exoplatform.gamification.NO`,"NO")}}</span>
                                </label>
                                <div class="error">{{enabledMessage}}</div>
                            </form>
                                    
                            <div class="row">
                                <b-col>
                                    <button class="btn secondary pull-right" type="submit"
                                            v-on:click.prevent="collapseButtonn(editedDomain,false), onCancel()">{{
                                        this.$t('exoplatform.gamification.gamificationinformation.domain.cancel') }}
                                    </button>
                                    <button class="btn-primary pull-right" type="submit" :disabled='isDisabled'
                                              v-on:click.prevent="onSave(editedDomain),collapseButtonn(editedDomain,false)" style="margin-left: 500px;">
                                        {{ this.$t('exoplatform.gamification.gamificationinformation.domain.confirm') }}

                                    </button>
                                </b-col>
                            </div>

                                </div>
                            </div>
                        </div>
                    </div>


                    <table class="uiGrid table table-hover domain-table" hover striped>


                        <thead>
                        <tr>
                            <th class="domain-name-col">{{
                                this.$t('exoplatform.gamification.gamificationinformation.domain.Title',"Title") }}
                            </th>
                            <th class="domain-desc-col">{{
                                this.$t('exoplatform.gamification.gamificationinformation.domain.Description',"Description") }}
                            </th>
                            <th class="domain-desc-col">{{
                                this.$t('exoplatform.gamification.enabled',"Enabled") }}
                            </th>
                            <th class="domain-action-col">{{
                                this.$t('exoplatform.gamification.gamificationinformation.domain.Actions',"Actions") }}
                            </th>
                        </tr>
                        </thead>
                        <tbody>


                        <tr v-for="domain in filteredDomains">

                            <td>
                                <div>
                                    {{$t(`exoplatform.gamification.gamificationinformation.domain.${domain.title}`,domain.title) }}
                                </div>
                            </td>

                            <td class="domain-desc-col">
                                <div>{{$t(`exoplatform.gamification.gamificationinformation.domain.${domain.description}`,domain.description)}}</div>
                            </td>
                            <td>
                            <div >
                                <label class="switch">
                                    <input type="checkbox" disabled  v-model="domain.enabled">
                                    <span class="slider round"></span>
                                    <span class="absolute-no">{{$t(`exoplatform.gamification.NO`,"No")}}</span>
                                </label>
                            </div>
                        </td>
                            <td class="center actionContainer">


                                <a aria-controls="collapseTwo" aria-expanded="true" data-target="#collapseTwo"
                                   data-toggle="collapse" href="" v-on:click.prevent="collapseButtonn(domain,false)"> <i
                                        class="uiIconEdit uiIconLightGray"></i></a>

                                <a aria-controls="collapseTwo" aria-expanded="true" data-target="#collapseTwo"
                                   data-toggle="collapse" href="" v-on:click.prevent="collapseButtonn(domain,true)"> <i
                                        class="uiIconDelete uiIconLightGray"></i></a>        

                            </td>

                        </tr>
                        <tr v-if="!domains.length || !filteredDomains.length " v-model="search">
                            <td class="empty center" colspan="6">
                                {{$t(`exoplatform.gamification.gamificationinformation.domain.warning.add.domains`,"Add domain") }}
                            </td>
                        </tr>
                        </tbody>
                    </table>

                </div>
            </b-col>
        </b-row>
    </b-container>
</template>
<script>
    import Vue from 'vue'
    import BootstrapVue from 'bootstrap-vue'
    import 'bootstrap/dist/css/bootstrap.css'
    import 'bootstrap-vue/dist/bootstrap-vue.css'
    import moment from 'moment'

    Vue.use(BootstrapVue);
    Vue.prototype.moment = moment;

    let domain = {};
    export default {

        props: ['domains', 'domain'],
        data() {
            return {
                search: '',
                id: null,
                isEnabled: false,
                SaveDomainForm: '',
                editedDomain: {},
                formErrors: {},
                dismissSecs: 5,
                dismissCountDown: 0,
                date: new Date(),
                isShown: false,
                isValidateShown:false,
                editedEnabled: null,
                enabledMessage:"",
                enabledFilter: null,
                filerlabel:"all",
                confirm: false,
                isdeleted: false,
                config: {
                    format: 'YYYY-MM-DD',
                    useCurrent: false,
                },
                dynamicAreas: [],
            }
        },

        computed: {
            filteredDomains() {
                return this.domains.filter(item => {
                    return ((item.description.toLowerCase().indexOf(this.search.toLowerCase()) > -1
                        || this.$t(`exoplatform.gamification.gamificationinformation.domain.${item.title}`,item.title).toLowerCase().indexOf(this.search.toLowerCase()) > -1)
                        && (this.enabledFilter === null || item.enabled === this.enabledFilter)
                    )
                })
            },
            isDisabled: function(){
                return (this.editedDomain.title==null||this.editedDomain.title=="")
            }
        },

         watch: {
            'editedDomain.enabled'() {
                console.log(this.editedEnabled);
                if(this.editedEnabled!=this.editedDomain.enabled){
                    if(this.editedDomain.enabled==true){
                        this.enabledMessage=this.$t(`exoplatform.gamification.domain.warning.enable`,"*All related rules and badges will be enabled")
                    }else{
                        this.enabledMessage=this.$t(`exoplatform.gamification.domain.warning.disable`,"*All related rules and badges will be disabled")
                    }
                }else{
                   this.enabledMessage="" 
                }
            }
        },
        methods: {

            onRemove(id) {

                this.isShown = !this.isShown;
                this.$emit('remove', id);
                this.isdeleted = true;
            },


            onRemovealertclose() {
               this.isShown = !this.isShown; 
               this.isdeleted = true;
                if (this.isShown) {
                    this.isdeleted = true;
                }
            },
            onSave(domain) {
                this.$emit('edit', domain);
                this.editedDomain = domain;
            },
            onCancel(editedDomain) {
                this.editedDomain = {};
            },
            onEdit(domain) {
            
                this.domain = domain;
                this.editedDomain = domain;
            },
            onSubmit() {
                this.isShown = !this.isShown;
                this.createDomain(this.domain);
                this.collapseButton()
            },

            closeAlert(item) {
                setTimeout(function () {
                    $(item).fadeOut('fast')
                }, 4000);

            },

            showAlert() {
                this.dismissCountDown = this.dismissSecs
            },
            onSubmit(domain) {
                this.updateDomain(this.editedDomain);
                this.collapseButtonn(domain)
            },
            collapseButtonn(domain,confirm) {
                this.confirm = confirm;
                this.editedDomain = domain;
                this.editedEnabled = domain.enabled;
                this.isShown = !this.isShown;
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
        }
    }

</script>

<style scoped>

    .table {
        position: relative;
        border-radius: 3px;
        background: #fff;
        margin-bottom: 20px;
        width: 96%;
        box-shadow: 0 1px 1px rgba(0, 0, 0, .1);
        margin: 14px auto 0;
        margin-bottom: 30px;
    }

    .uiGrid.table tr td {
        padding: 5px;
        vertical-align: inherit;
    }


    .table td,
    .table th {
        padding: 8px;
        line-height: 1.42857143;
        vertical-align: top;
        text-align: center;
        border: none;
    }
    .table-hover tbody tr:hover {
        cursor: pointer;
    }

    .uiSearchInput .advancedSearch, .uiSearchInput .AdvancedSearch {
        display: block;
        position: absolute;
        right: 5px;
        cursor: pointer;
    }

    i.uiIconSearch.uiIconLightGray {
        position: relative;
        float: left;
    }
    .uiSearchInput.searchWithIcon {
        display: flex;
        flex-direction: row-reverse;
        float: right;
        margin: 17px 10px;
    }
    .uiSearchInput input[type="text"] {
        height: 39px;
    }


    .action-bar.dropdown.filterWithIcon> a.actionIcon.dropdown-toggle {
        box-shadow: none;
        border: none;
        text-decoration: none;
        margin: auto;
        width: 100%;
        border-radius: 3px;
        background-color: transparent;
    }
    @media (max-width: 416px) {
        .uiSearchInput.searchWithIcon {
            max-width: 18%;
            margin-left: 5px;
        }
    }

    @media (max-width: 340px) {
        .uiSearchInput.searchWithIcon {
            max-width: 12%;
            margin-left: 5px;
        }
    }

    .card-body label {
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

    div#headingOne button.btn.btn-primary {
        margin: 15px 12px 5px;
    }

    .btn-primary:focus, .btn-primary.focus {
        box-shadow: inset 0 0 0 0.2rem rgba(38, 143, 255, 0.5);
    }
    button.btn-primary.pull-right {
        border-radius: 0.25rem;
        height: 44px;
    }
    button.btn-primary.pull-right:hover {
        background: #476a9c;
        color: #f9f9f9 !important;
    }
    label {
        display: inline-block;
        max-width: 100%;
        margin-bottom: 5px;
        font-weight: 600;
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

    form#areaSelectboxGroup {
        margin-bottom: 0px;
    }

    .require-msg {
        max-width: 100% !important;
        font-size: 14px;
    }

    msg.alert-dismissible.alert-danger {
        display: -webkit-inline-box;
        width: auto;
    }

    input {
        width: 100%;
    }

    textarea#domainDescription {
        width: 100%;
        font-size: 15px;
    }


    input[type="checkbox"] {
        width: auto;
        margin-bottom: 10px;
    }

    div#headingOne:hover {
        background: transparent;
        transition: all .5s;
    }

    div#headingOne {
        box-shadow: none;
        transition: all .5s;
    }

    button.btn.secondary {
        padding: 8px 25px;
        margin: 0 25px;;
        border: 1px solid #e1e8ee !important;
        color: #4d5466;
        background-color: transparent !important;
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


    input#titleInput {
        max-width: initial;
    }

    .collapse {
        top: 15px;
    }

    div#collapseTwo {
        position: fixed;
        z-index: 10000;
        left: 0;
        top: 0;
        bottom: 0;
        width: 100%;
        height: 100%;
        overflow: auto;
        background-color: rgb(0, 0, 0);
        background-color: rgba(0, 0, 0, 0.4);
    }
  
    /* switch */
    label.uiSwitchBtn {
        height: 30px;
    }
    .uiSwitchBtn span.absolute-no {
        text-align: left;
    }
    .switch {
        position: relative;
        display: inline-block;
        width: 53px;
        height: 32px;
        top: 0.4rem;
    }

    .switch input {
        display: none;
    }

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
        text-align: left !important;
        line-height: 19px;
        padding-left: 0;
        width: 95px;
        height: 26px !important;
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

    input:checked+.slider:after {
        -webkit-transform: translateX(0px);
        -ms-transform: translateX(0px);
        transform: translateX(0px);
        padding-left: 25px;
    }

    input:checked+.slider:before {
        background-color: #fff;
        -webkit-transform: translateX(38px);
        -ms-transform: translateX(38px);
        transform: translateX(38px);
    }

    input:checked+.slider:before {
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
        font-size: 16px;
        width: calc(100% - 25px);
        line-height: 30px;
    }
    
    input.domain-needed-score-col {
        max-width: 60px;
        text-align: center;
    }

    i.uiIconClose.uiIconBlue {
        zoom: 163%;
        vertical-align: super;
        opacity: 1;
        line-height: inherit;
    }

    i.uiIconSave.uiIconLightGray {
        left: -4px;
    }

    button, [type="button"], [type="reset"], [type="submit"] {
        -webkit-appearance: button;
        align-content: stretch;
        padding: 8px 25px;
        /* margin-left: 500px; */

    }
    .UIToolbarContainer .uiDropdownWithIcon > a {
        color: #ffffff !important;
    }
    button.btn.cancel.pull-right {
        border: 1px solid #e1e8ee !important;
        color: #4d5466;
        background: transparent !important;
    }
    button.btn.cancel.pull-right:hover, button.btn-primary.pull-right:hover{
        color: #4d5466;
    }
    div#collapseTwo button[data-v-52f850bc], [type="button"], [type="reset"], [type="submit"]{
        height: 44px;

    }

    .alert-success {
        margin-top: 124px !important;
        margin-left: 38%;
        transform: translateX(-50%);
    }

    .filter-bar{
    display: flex;
    flex-direction: row-reverse;
    border: 1px Solid #e1e8ee;
    border-radius: 5px;
    outline: none;
    float: right;
    margin-top: 17px;
    margin-right: 12px;
    }
    .filter-bar select {
        width: auto;
        outline: none;
        padding: 8px 20px;
        height: 35px;
    }

    .btn-primary.disabled, .btn-primary:disabled {
     background-color: #afc9e5; 
     background-image: none;
    }

        .collapse.show.out {
    display: none;
}
</style>
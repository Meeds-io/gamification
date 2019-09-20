<template>

    <b-container fluid>
        <b-row>
            <b-col sm="12">
                <div>
                    <div class="uiSearchForm uiSearchInput searchWithIcon">
                        <a class="advancedSearch" data-placement="bottom" rel="tooltip" title="">
                            <i class="uiIconSearch uiIconLightGray"></i>
                        </a>
                        <input :placeholder="this.$t('exoplatform.gamification.gamificationinformation.domain.search')"
                               name="keyword" type="text" v-model="search" value="">
                    </div>
                    <div :class="isShown ? '' : 'out'" aria-labelledby="headingOne" class="collapse show"
                         data-parent="#accordionExample" id="collapseTwo" style="height: 0px; transition: inherit;">
                        <div class="card-body">
                            <div class="UIPopupWindow uiPopup UIDragObject NormalStyle" id="myForm"
                                 style="width: 760px; z-index:1000000; position: relative; left: auto; margin: 0 20px; z-index: 1; max-width: 100%;margin: 0 auto;height: 100%;">
                                <div class="popupHeader ClearFix">

                                    <a class="uiIconClose pull-right" v-on:click.prevent="collapseButtonn(domain)"></a>

                                    <span class="PopupTitle popupTitle">{{ this.$t('exoplatform.gamification.gamificationinformation.domain.popupedit') }}</span>
                                </div>
                                <div class="PopupContent popupContent">

                                    <form id="titleInputGroup">
                                        <label class="col-form-label pt-0">{{
                                            this.$t('exoplatform.gamification.gamificationinformation.domain.Title') }}
                                            :</label>
                                        <input id="titleInput" placeholder="Enter domain's title" required type="text"
                                               v-model="editedDomain.title">
                                        </input>
                                        <div :show="dismissCountDown" @dismiss-count-down="countDownChanged"
                                             @dismissed="dismissCountdown=0" class="alert alert-error" dismissible
                                             v-if="formErrors.title" variant="danger">
                                            <i class="uiIconError"></i> Domain title is required please enter a title
                                            {{dismissCountDown}}
                                        </div>

                                    </form>


                                    <form id="descriptionInputGroup">
                                        <label class="col-form-label pt-0">{{
                                            this.$t('exoplatform.gamification.gamificationinformation.domain.Description')
                                            }} :</label>
                                        <textarea :max-rows="6" :rows="3" id="domainDescription"
                                                  placeholder="Enter description" v-model="editedDomain.description">
                            </textarea>

                                        <div :show="dismissCountDown" @dismiss-count-down="countDownChanged"
                                             @dismissed="dismissCountdown=0" class="alert alert-error" dismissible
                                             v-if="formErrors.description" variant="danger">
                                            <i class="uiIconError"></i> Domain description is required please enter a
                                            description {{dismissCountDown}}
                                        </div>
                                    </form>


                                    <div class="row">
                                        <b-col>
                                            <button class="btn secondary pull-right" type="submit"
                                                    v-on:click.prevent="collapseButtonn(domain), onCancel()">{{
                                                this.$t('exoplatform.gamification.gamificationinformation.domain.cancel')
                                                }}
                                            </button>
                                            <b-button class="btn-primary pull-right" type="submit"
                                                      v-on:click.prevent="onSave(domain),collapseButtonn(domain)">
                                                {{
                                                this.$t('exoplatform.gamification.gamificationinformation.domain.confirm')
                                                }}
                                            </b-button>
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
                                this.$t('exoplatform.gamification.gamificationinformation.domain.Title') }}
                            </th>
                            <th class="domain-desc-col">{{
                                this.$t('exoplatform.gamification.gamificationinformation.domain.Description') }}
                            </th>
                            <th class="domain-action-col">{{
                                this.$t('exoplatform.gamification.gamificationinformation.domain.Actions') }}
                            </th>
                        </tr>
                        </thead>
                        <tbody>


                        <tr v-for="domain in filteredDomains">

                            <td>
                                <div v-if="domain.title !=editedDomain.title ">
                                    {{$t(`exoplatform.gamification.gamificationinformation.domain.${domain.title}`) }}
                                </div>
                                <div v-else>{{domain.title}}</div>
                            </td>

                            <td class="domain-desc-col">
                                <div>{{domain.description}}</div>
                            </td>
                            <td class="center actionContainer">


                                <a aria-controls="collapseTwo" aria-expanded="true" data-target="#collapseTwo"
                                   data-toggle="collapse" href="" v-on:click.prevent="collapseButtonn(domain)"> <i
                                        class="uiIconEdit uiIconLightGray"></i></a>


                                <a class="actionIcon" data-original-title="Supprimer" data-placement="bottom" href="#"
                                   rel="tooltip" title="Supprimer"
                                   v-b-tooltip.hover v-if="editedDomain.id !== domain.id"
                                   v-on:click.prevent.stop="onRemove(domain.id)">
                                    <i class="uiIconDelete uiIconLightGray"></i></a>
                                <!--<a href="#" v-if="editedDomain.id === domain.id" v-on:click.prevent.stop="onSave(domain)" data-placement="bottom" rel="tooltip" class="actionIcon"
                                   data-original-title="Edit" v-b-tooltip.hover title="Save">
                                    <i class="uiIconSave uiIconLightGray"></i></a>-->
                                <a class="actionIcon" data-original-title="Cancel" data-placement="bottom" href="#"
                                   rel="tooltip" title="Cancel"
                                   v-b-tooltip.hover v-if="editedDomain.id === domain.id"
                                   v-on:click.prevent.stop="onCancel(domain)">
                                    <i class="uiIcon uiIconClose uiIconBlue"></i></a>
                            </td>

                        </tr>
                        <tr v-if="!domains.length || !filteredDomains.length " v-model="search">
                            <td class="empty center" colspan="6">
                                {{$t(`exoplatform.gamification.gamificationinformation.domain.warning.add.domains`) }}
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
                    return (item.description.toLowerCase().indexOf(this.search.toLowerCase()) > -1
                        || item.title.toLowerCase().indexOf(this.search.toLowerCase()) > -1
                    )
                })
            }
        },
        methods: {


            onRemove(id) {

                this.$emit('remove', id);


            },
            onSave(domain) {

                if (this.validateForm()) {

                    this.$emit('edit', domain);
                    this.editedDomain = domain;
                }
            },
            onCancel(editedDomain) {
                this.editedDomain = {};
            },
            onEdit(domain) {
                this.domain = domain;
                this.editedDomain = domain;
            },
            validateForm() {
                const errors = {};
                if (!this.editedDomain.title) {
                    errors.title = $t(`exoplatform.gamification.gamificationinformation.domain.errors.title`);
                    this.dismissCountDown = 5
                }
                if (!this.editedDomain.description) {
                    errors.title = $t(`exoplatform.gamification.gamificationinformation.domain.errors.description`);
                    this.dismissCountDown = 5
                }
                this.formErrors = errors;
                return Object.keys(errors).length === 0
            },

            showAlert() {
                this.dismissCountDown = this.dismissSecs
            },
            onSubmit(domain) {
                if (this.validateForm()) {
                    this.updateDomain(this.editedDomain);

                    this.collapseButtonn(domain)
                }

            },
            collapseButtonn(domain) {
                this.editedDomain = domain;
                this.isShown = !this.isShown;

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


        }
    }

</script>

<style scoped>
    .uiSearchInput .advancedSearch, .uiSearchInput .AdvancedSearch {
        position: relative;
        right: 25px;
        top: 10px;
    }

    i.uiIconSearch.uiIconLightGray {
        position: relative;
        float: left;
        margin-right: 1%;
    }

    .uiSearchInput.searchWithIcon {
        display: flex;
        margin-left: 70%;
        margin-top: 19px;
        position: static;
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

    label {
        display: inline-block;
        max-width: 100%;
        margin-bottom: 5px;
        font-weight: 500;
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
        margin-left: 25px;
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

    /* switch test */
    .uiSwitchBtn {
        position: relative;
        display: inline-block;
        width: 185px;
        height: 66px;
        zoom: 30%;
    }

    .uiSwitchBtn input {
        display: none;
    }

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
        height: 45px;
        width: 45px;
        left: 10px;
        bottom: 11px;
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
        font-size: 37px;
        text-align: left !important;
        line-height: 65px;
        padding-left: 0;
        width: 185px;
        height: 66px !important;
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
    }

    input:checked + .slider:before {
        -webkit-transform: translateX(115px);
        -ms-transform: translateX(115px);
        transform: translateX(115px);
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
        font-size: 45px;
        width: calc(100% - 25px);
        line-height: 70px;
        cursor: pointer;
    }

    input.domain-needed-score-col {
        max-width: 60px;
        text-align: center;
    }

    i.uiIconClose.uiIconBlue {
        zoom: 133%;
        height: 100%;
        vertical-align: super;
        color: #578dc9;
        line-height: inherit;
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

    select:focus {
        border-color: #a6bad6;
        -webkit-box-shadow: inset 0 1px 1px rgba(0, 0, 0, .075), 0 0 5px #c9d5e6;
        -moz-box-shadow: inset 0 1px 1px rgba(0, 0, 0, .075), 0 0 5px #c9d5e6;
        box-shadow: inset 0 1px 1px rgba(0, 0, 0, .075), 0 0 5px #c9d5e6;
        color: #333;
    }

    .container-fluid {
        display: table;
    }

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

    .table thead th {
        font-size: 0.9em;
    }

    .table td, .table th {
        padding: 8px;
        line-height: 1.42857143;
        vertical-align: top;
        text-align: center;
        border: none;
    }

    .table-hover tbody tr:hover {
        cursor: pointer;
    }

    /*edit Mode */


    input[type="text"] {
        height: 35px;
        margin: auto;
    }

    td.domain-needed-score-col input {
        width: 60px;
        text-align: center;
    }

    td select {
        word-wrap: normal;
        border: Solid 2px #e1e8ee;
        border-radius: 5px;
        margin: auto;
        outline: none;
    }

    input.domain-desc-col {
        min-width: 98%;
    }

    /* switch */
    .switch {
        position: relative;
        display: inline-block;
        width: 185px;
        height: 66px;
        zoom: 30%;
        top: 0.4rem;
    }

    .switch input {
        display: none;
    }

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
        height: 45px;
        width: 45px;
        left: 10px;
        bottom: 11px;
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
        font-size: 37px;
        text-align: left !important;
        line-height: 65px;
        padding-left: 0;
        width: 185px;
        height: 66px !important;
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
    }

    input:checked + .slider:before {
        -webkit-transform: translateX(115px);
        -ms-transform: translateX(115px);
        transform: translateX(115px);
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
        font-size: 45px;
        width: calc(100% - 25px);
        line-height: 70px;
        cursor: pointer;
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
        margin-left: 500px;
    }
</style>
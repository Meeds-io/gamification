<template>
<b-container fluid>
    <b-row>
        <b-col sm="12">
            <div class="alert alert-success" v-if="isdeleted" v-on:="closeAlert()">
                <button aria-label="Close" class="close" data-dismiss="alert" style="line-height: 27px; margin-right: 5px;" type="button">
                    <span aria-hidden="true">&times;</span>
                </button>
                <i class="uiIconSuccess"></i>
                {{this.$t('exoplatform.gamification.successdelete')}}
            </div>



            <div class="uiSearchForm uiSearchInput searchWithIcon">

                <input :placeholder="this.$t('exoplatform.gamification.gamificationinformation.domain.search')" name="keyword" type="text" v-model="search" value="">
                <a class="advancedSearch" data-placement="bottom" rel="tooltip" title="">
                    <i class="uiIconSearch uiIconLightGray"></i>
                </a>

            </div>

              <div class="action-bar dropdown filterWithIcon" data-currentorderby="dueDate">
                <a href="" class="actionIcon dropdown-toggle" data-toggle="dropdown" data-placement="bottom">
                    <i class="uiIconFilter uiIconLightGray"></i>
                </a>
                <ul class="dropdown-menu">

                    <li><a href="javascript:void(0)" v-on:click.prevent="enabledFilter=true">Enabled</a>
                    </li>
                    <li><a href="javascript:void(0)" v-on:click.prevent="enabledFilter=false">Disabled</a>
                    </li>

                    <li><a href="javascript:void(0)" v-on:click.prevent="enabledFilter=null"> All</a>
                    </li>

                </ul>
            </div>

            <div :class="isShown ? '' : 'out'" aria-labelledby="headingOne" class="collapse show" data-parent="#accordionExample" id="collapseTwo" style=" transition: inherit;">

                <div class="card-body">
                    <div class="UIPopupWindow uiPopup UIDragObject NormalStyle" id="myForm" style="width: 760px; z-index:1000000; position: relative; left: auto; margin: 0 20px; z-index: 1; max-width: 100%;margin: 0 auto;height: 100%;">
                        <div class="popupHeader ClearFix">
                            <div class="PopupTitle popupTitle" id="confirmLabel">{{
                                    this.$t('exoplatform.gamification.Confirmation') }}
                            </div>

                            <a class="uiIconClose pull-right" v-on:click.prevent="collapseConfirm(rule)"></a>

                            <span class="PopupTitle popupTitle">{{ this.$t('exoplatform.gamification.rule.popupdelete') }}</span>
                        </div>
                        <div class="PopupContent popupContent">
                            <div class="media">
                                <div class="pull-left">
                                    <i class="uiIconColorQuestion"></i>
                                </div>
                                <div class="media-body">
                                    <p class="msg"> {{ this.$t('exoplatform.gamification.areyousure.deleterule')
                                            }}</p>
                                </div>
                            </div>
                            <div class="uiAction uiActionBorder">
                                <b-col>
                                    <button class="btn cancel pull-right" type="submit" v-on:click.prevent="collapseConfirm(rule), onCancel()">{{
                                            this.$t('exoplatform.gamification.gamificationinformation.domain.cancel')
                                            }}
                                    </button>

                                    <b-button class="btn-primary pull-right" type="submit" v-on:click.prevent="onRemove(rule.id,rule.title),collapseConfirm(rule)">
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

            <table striped hover class="uiGrid table table-hover rule-table">
                <thead>
                    <tr>
                        <th class="rule-name-col">{{$t(`exoplatform.gamification.gamificationinformation.Event`) }}</th>

                        <th class="rule-desc-col">
                            {{$t(`exoplatform.gamification.gamificationinformation.domain.Description`) }}
                        </th>
                        <th class="rule-price-col">{{$t(`exoplatform.gamification.score`) }}</th>
                        <th class="rule-area-col">{{$t(`exoplatform.gamification.gamificationinformation.Domain`) }}
                        </th>
                        <th class="rule-enable-col">{{$t(`exoplatform.gamification.enabled`) }}</th>
                        <th class="rule-action-col">{{$t(`exoplatform.gamification.action`)}}</th>
                    </tr>
                </thead>
                <tbody>
                    <tr v-for="rule in filteredRules">

                        <td>
                            <div v-if="editedrule.id !== rule.id"> {{
                                $t(`exoplatform.gamification.gamificationinformation.rule.title.${rule.event}`,rule.event)
                                }}
                            </div>
                            <input class="rule-title-col" style="width: 130px; min-width: 98%;" type="text" v-if="editedrule.id === rule.id" v-model="rule.event">
                        </td>
                        <td class="rule-desc-col">
                            <div v-if="editedrule.id !== rule.id">{{
                                $t(`exoplatform.gamification.gamificationinformation.rule.description.${rule.title}`,rule.description)
                                }}
                            </div>
                            <input style="width: 130px;min-width: 98%;" type="text" v-if="editedrule.id === rule.id" v-model="rule.description">
                        </td>
                        <td>
                            <div v-if="editedrule.id !== rule.id">{{rule.score}}</div>
                            <input class="rule-needed-score-col" type="text" v-if="editedrule.id === rule.id" v-model="rule.score">
                        </td>
                        <td style="max-width: 115px;">
                            <div v-if="editedrule.id !== rule.id && rule.domainDTO != null">{{
                                $t(`exoplatform.gamification.gamificationinformation.domain.${rule.domainDTO.title}`,rule.domainDTO.title)
                                }}
                            </div>
                            <select v-if="editedrule.id === rule.id" v-model="rule.domainDTO" style="max-width: 115px;margin: 0px auto;height: 35px;" required>
                                <option :value="null" disabled style="max-width: 115px;">
                                    {{$t(`exoplatform.gamification.selectdM`)}}
                                </option>
                                <option v-for="option in domains" v-bind:value="option" style="max-width: 115px">
                                    {{ option.title }}
                                </option>
                            </select>
                        <td>
                            <div v-if="editedrule.id === rule.id">
                                <label class="switch">
                                    <input type="checkbox" v-model="rule.enabled">
                                    <span class="slider round"></span>
                                    <span class="absolute-no">{{$t(`exoplatform.gamification.YES`)}}</span>
                                </label>
                            </div>
                            <div v-else>
                                <label class="switch" v-on:click="rule.enabled = !rule.enabled">
                                    <input type="checkbox" v-model="rule.enabled">
                                    <span class="slider round"></span>
                                    <span class="absolute-no">{{$t(`exoplatform.gamification.YES`)}}</span>
                                </label>
                            </div>
                        </td>
                        <td class="center actionContainer">
                            <a href="#" v-if="editedrule.id !== rule.id" v-on:click.prevent.stop="onEdit(rule)" data-placement="bottom" rel="tooltip" class="actionIcon" data-original-title="Edit" v-b-tooltip.hover title="Edit">
                                <i class="uiIconEdit uiIconLightGray"></i></a>

                            <a class="actionIcon" data-placement="bottom" href="#" rel="tooltip" v-if="rule.id" v-on:click.prevent="collapseConfirm(rule)" data-original-title="Supprimer" v-b-tooltip.hover title="Supprimer">
                                <i class="uiIconDelete uiIconLightGray"></i>
                            </a>
                            <a href="#" v-if="editedrule.id === rule.id" v-on:click.prevent.stop="onSave(rule)" data-placement="bottom" rel="tooltip" class="actionIcon" data-original-title="Edit" v-b-tooltip.hover title="Save">
                                <i class="uiIconSave uiIconLightGray"></i></a>
                            <a href="#" v-if="editedrule.id === rule.id" v-on:click.prevent.stop="onCancel(rule)" data-placement="bottom" rel="tooltip" class="actionIcon" data-original-title="Cancel" v-b-tooltip.hover title="Cancel">
                                <i class="uiIcon uiIconClose uiIconBlue"></i></a>
                        </td>
                    </tr>
                    <tr v-if="!rules.length || !filteredRules.length  " v-model="search">

                        <td class="empty center" colspan="6"> {{$t(`exoplatform.gamification.ErrorRulesMsg`)}}

                        </td>
                    </tr>
                </tbody>
            </table>
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
export default {
    props: ['rules', 'domains'],
    data() {
        return {
            search: '',
            formErrors: {},
            editedrule: {},
            isdeleted: false,
            isShown: false,
            enabledFilter: null,
            isEnabled: false
        }
    },
    computed: {
        filteredRules() {
            return this.rules.filter(item => {
                return ((
                    this.$t(`exoplatform.gamification.gamificationinformation.rule.description.${item.title}`,item.description).toLowerCase().indexOf(this.search.toLowerCase()) > -1 ||
                    this.$t(`exoplatform.gamification.gamificationinformation.rule.title.${item.event}`,item.event).toLowerCase().indexOf(this.search.toLowerCase()) > -1 ||
                    this.$t(`exoplatform.gamification.gamificationinformation.domain.${item.domainDTO.title}`,item.domainDTO.title).toLowerCase().indexOf(this.search.toLowerCase()) > -1||
                    item.score.toString().toLowerCase().indexOf(this.search.toLowerCase()) > -1)
                    && (this.enabledFilter === null || item.enabled === this.enabledFilter)
                )
            })
        }
    },
    methods: {
        onEdit(rule) {
            this.rule = rule;
            this.editedrule = rule;
        },
        onRemove(id, title) {
            this.$emit('remove', id, title);
            this.isdeleted = true
        },
        onSave(rule) {
            this.$emit('save', rule);
            this.editedrule = {};
        },
        onCancel(rule) {
            this.editedrule = {};
        },
        collapseConfirm(rule) {
            this.rule = rule;
            this.isShown = !this.isShown;
            if (this.isShown) {
                this.closeAlert(".alert")
            }
        },
        closeAlert(item) {
            setTimeout(function () {
                $(item).fadeOut('fast')
            }, 4000);

        }
    }
}
</script>

<style scoped>
.uiSearchInput.searchWithIcon {
    display: flex;
    flex-direction: row-reverse;
    float: right;
    margin-top: 18px;
}

.filterWithIcon {
    display: flex;
    flex-direction: row-reverse;
    float: right;
    margin-top: 18px;
}

i.uiIconSearch.uiIconLightGray {
    position: relative;
    float: left;
}

@media (max-width: 416px) {
    .uiSearchInput.searchWithIcon {
        max-width: 45%;
        margin-left: 5px;
    }
}

@media (max-width: 340px) {
    .uiSearchInput.searchWithIcon {
        max-width: 35%;
        margin-left: 5px;

    }
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

/*edit Mode
    td input {
        max-width: min-content;
    }*/
input[type="text"] {
    height: 35px;
    margin: auto;
}

td.rule-needed-score-col input {
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

input.rule-desc-col {
    min-width: 98%;
}

/* switch */
.switch {
    position: relative;
    display: inline-block;
    width: 53px;
    height: 32px;
    /* zoom: 30%; */
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

.collapse.show.out {
    display: none;
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
    text-align: right !important;
    font-size: 16px;
    width: calc(100% - 25px);
    line-height: 30px;
    cursor: pointer;
}

input.rule-needed-score-col {
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

button.btn.cancel.pull-right {
    border: 1px solid #e1e8ee !important;
    color: #4d5466;
    background: transparent !important;
}

.collapse.show.out {
    display: none;
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

.alert-success {
    position: absolute;
    top: 11% !important;
    margin: auto 0.5% !important;
}
</style>

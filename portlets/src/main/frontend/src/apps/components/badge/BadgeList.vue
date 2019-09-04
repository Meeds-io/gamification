<template>
    <b-container fluid>
        <b-row>
            <b-col sm="12">

                <table class=" uiGrid table table-hover badge-table">
                    <thead>
                    <tr>
                        <th class="badge-title-col">Title</th>
                        <th class="badge-desc-col">Description</th>
                        <th class="badge-nedded-score-col">Needed Score</th>
                        <th class="badge-domain-col">Domain</th>
                        <th class="badge-icon-col">Icon</th>
                        <th class="badge-status-col">Status</th>
                        <!--    <th class="badge-created-by-col">Created by</th> -->
                        <th class="badge-action-col">Action</th>
                    </tr>
                    </thead>
                    <tbody>
                    <tr v-for="badge in badges" track-by="id">
                        <td class="badge-title-col">
                            <div v-if="editedbadge.id !== badge.id">{{badge.title}}</div>
                            <input type="text" v-if="editedbadge.id === badge.id" v-model="badge.title"style="width: 130px;min-width: 98%;">
                        </td>
                        <td class="badge-desc-col">
                            <div v-if="editedbadge.id !== badge.id">{{badge.description}}</div>
                            <input class="badge-desc-col" type="text" v-if="editedbadge.id === badge.id" v-model="badge.description" style="min-width: 98%;">
                        </td>
                        <td class="badge-needed-score-col">
                            <div v-if="editedbadge.id !== badge.id">
                                <div v-if="badge.neededScore >=1000">{{badge.neededScore/1000}} K</div>
                                <div v-if="badge.neededScore <1000"> {{badge.neededScore}}</div>
                            </div>
                            <input  class="badge-needed-score-col" type="text" v-if="editedbadge.id === badge.id" v-model="badge.neededScore">
                        </td>
                        <td style="max-width: 105px;">
                            <div v-if="editedbadge.id !== badge.id && badge.domainDTO != null">{{badge.domainDTO.title}}</div>

                            <select  v-if="editedbadge.id === badge.id" v-model="badge.domainDTO"  style="max-width: 115px;margin: 0px auto;height: 35px;" required>
                                <option :value="null" disabled>Select your Domain</option>
                                <option v-for="option in domains" v-bind:value="option">
                                    {{ option.title }}
                                </option>
                            </select>
                        </td>
                        <td id="iconInputGroup" style="max-width: 100px;">
                            <div v-if="editedbadge.id !== badge.id"  style="z-index: 0;"> <img thumbnail fluid :src="`/rest/gamification/reputation/badge/${badge.title}/avatar`" alt="Thumbnail" class="m-1"  width="40" height="40"/>
                            </div>
                             <b-form-file v-if="editedbadge.id === badge.id" v-model="badge.icon"  placeholder="+" accept="image/jpeg, image/png, image/gif" class="m-1"  width="40" height="40" ></b-form-file>
                         </td>
                         <td class="badge-status-col" style="padding: 0px; z-index: 10;">
                             <div v-if="editedbadge.id === badge.id" style="z-index: 10;">
                                 <label class="switch" >
                                     <input type="checkbox" v-model="badge.enabled">
                                     <span class="slider round"></span>
                                     <span class="absolute-no">NO</span>
                                 </label>
                             </div>
                             <div v-if="editedbadge.id !== badge.id">
                                 <label class="switch" v-on:click ="badge.enabled = !badge.enabled">
                                     <input type="checkbox" v-model="badge.enabled">
                                     <span class="slider round"></span>
                                     <span class="absolute-no">NO</span>
                                 </label>
                             </div>
                         </td>
                        <td class="center actionContainer"  style="z-index: 10;">
                            <a href="#" v-if="editedbadge.id !== badge.id" v-on:click.prevent.stop="onRemove(badge.id,badge.title)" data-placement="bottom" rel="tooltip" class="actionIcon"
                               data-original-title="Supprimer" v-b-tooltip.hover title="Supprimer">
                                <i class="uiIconDelete uiIconLightGray"></i>
                            </a>
                            <a href="#" v-if="editedbadge.id !== badge.id" v-on:click.prevent.stop="onEdit(badge)" data-placement="bottom" rel="tooltip" class="actionIcon"
                               data-original-title="Edit" v-b-tooltip.hover title="Edit">
                                <i class="uiIconEdit uiIconLightGray"></i></a>
                            <a href="#" v-if="editedbadge.id === badge.id" v-on:click.stop.prevent="onSave(badge)" data-placement="bottom" rel="tooltip" class="actionIcon"
                               data-original-title="Edit" v-b-tooltip.hover title="Save">
                                <i class="uiIconSave uiIconLightGray"></i></a>
                            <a href="#" v-if="editedbadge.id === badge.id" v-on:click.prevent="onCancel(badge)" data-placement="bottom" rel="tooltip" class="actionIcon"
                               data-original-title="Cancel" v-b-tooltip.hover title="Cancel">
                                <i class="uiIcon uiIconClose uiIconBlue"></i></a>
                        </td>
                    </tr>
                    <tr v-if="!badges.length">
                        <td colspan="9" class="p-y-3 text-xs-center" style="cursor: auto; background-color:white;">
                            <strong>You should add some badges!</strong>
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
    import moment from 'moment'
    Vue.prototype.moment = moment
    import BootstrapVue from 'bootstrap-vue'
    Vue.use(BootstrapVue);
    import 'bootstrap/dist/css/bootstrap.css'
    import 'bootstrap-vue/dist/bootstrap-vue.css'
    export default {
        props: ['badges','domains'],
        data() {
            return {
                formErrors: {},
                selectedFile: undefined,
                selectedFileName: '',
                editedbadge : {},
                isEnabled: false
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

            onSave(badge) {
                this.$emit('save', badge);
                this.editedbadge= {};
            },
            onEdit(badge) {
                //this.$emit('edit', badge)
                this.badge=badge;
                this.editedbadge=badge;
            },
            onImageChanged(event) {
                this.selectedFile = event.target.files[0]
                this.selectedFileName = event.target.files[0].name
            },
            onCancel(badge) {
                this.editedbadge= {}
            },
            onRemove(id, title) {
                this.$emit('remove', id, title);
            },
            change() {
                console.log('filechange');
            }
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
</script>
<style scoped>
    .container-fluid {
        display: table;
    }
    .table {
        position: relative;
        border-radius: 3px;
        background: #fff;
        margin-bottom: 20px;
        width: 96%;
        box-shadow: 0 1px 1px rgba(0,0,0,.1);
        margin: 30px auto 0;
        margin-bottom: 30px;
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
    }
    .table-hover tbody tr:hover {
        cursor: pointer;
    }
    .table-striped>tbody>tr:nth-of-type(odd) {
        background-color: #f9f9f9;
    }
    .uiGrid.table td, .uiGrid.table th {
        border-left: none;
    }
    .uiGrid.table thead {
        border: 1px solid #e1e8ee;
    }
    .uiGrid.table {
        border: none;
    }
    /* switch test */
    .switch {
        position: relative;
        display: inline-block;
        width: 53px;
        height: 32px;
        top: 0.4rem;
    }
    .switch input {
        display:none;
        width: 6px;
        height: 4px;
    }
    .slider {
        position: absolute;
        cursor: pointer;
        overflow: hidden;
        top: 0;
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
        bottom: 4px;
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
    input:checked + .slider:after {
        -webkit-transform: translateX(0px);
        -ms-transform: translateX(0px);
        transform: translateX(0px);
        padding-left: 25px;
    }
    input:checked + .slider:before {
        background-color: #fff;
        transform: translateX(38px);
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
        color: #a9a9a9;
        text-align: right !important;
        font-size: 16px;
        width: calc(100% - 25px);
        line-height: 21px;
        cursor: pointer;
    }

    select.mb-4 {
        max-width: 115px;
        margin: 0px auto;
        height: 35px;
    }
    i.uiIconClose.uiIconBlue {
        zoom: 163%;
        vertical-align: super;
        opacity: 1;
        line-height: inherit;
    }

    /* input icon */
    .custom-file {
        position: relative;
        display: inline-block;
        width: 40%;
        height: 38px;
        margin-bottom: 0;
    }
    td#iconInputGroup {
        max-width: 145px;
    }
    td#iconInputGroup input {
        width: auto;
        max-width: 65px;
    }
    input.badge-needed-score-col {
        width: 60px;
        text-align: center;
    }

    input[type="file"],  .custom-file-input {
        max-width: 65px !important;
        width: 65px !important;
        overflow: hidden;
    }
    input#__BVID__8{
        max-width: 65px !important;
        width: 65px !important;
    }
    .table td {
        vertical-align: middle;
    }
    /*edit Mode */
    td input {
        max-width: min-content;
    }
    input[type="text"] {
        height: 35px;
        margin: auto;
    }
    .custom-file-input:lang(en) ~ .custom-file-label::after {
        content: "+";
        display: none !important;
    }
    td#iconInputGroup input {
        max-width: 70px;
        width: 70px;
    }
    .custom-file {
        margin-left: 58px !important;
        z-index: 0;
    }
    .custom-file-input {
        width: 50px;
        max-width: 50px;
    }
    td.badge-title-col {
        max-width: 300px;
    }
    img.m-1 {
        border-radius: 50%;
        width: 40px;
        height: 40px;
    }
    label.custom-file-label::after {
        content: '+'!important;
        height: 35px;
        width: 35px;
        border-radius: 50%;
    }
</style>

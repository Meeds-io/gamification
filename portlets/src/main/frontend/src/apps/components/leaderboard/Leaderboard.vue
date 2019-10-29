<template>
<div class="uiBox container-fluid">
    <div class="row">
        <div class="col">
            <h5 class="mt-0 title">{{ this.$t('exoplatform.gamification.leaderboard.title',"Title") }}</h5>
            <a href="gamification-earn-points" class="ico-info actionIco" target="_blank" rel="tooltip" :title="this.$t('exoplatform.gamification.leaderboard.Howearnpoints') ">

                <i class="uiIconInformation"></i>
            </a>

        </div>
    </div>
    <div class="row">
        <div class="col">
            <select v-model="domain" class="mb-4" required>
                <option value="null"  selected>{{ this.$t('exoplatform.gamification.leaderboard.domain.null',"Global") }}</option>
                <option v-for="option in domains" v-bind:value="option.title">
                     {{$t(`exoplatform.gamification.gamificationinformation.domain.${option.title}`,option.title) }}
                </option>
            </select>
        </div>
    </div>
    <div class="row">
        <div class="col">
            <div role="toolbar" class="btn-toolbar" aria-label="Toolbar with button groups and dropdown menu">
                <div id="app" role="group" class="btn-group">
                    <button type="button" class="btn btn-secondary" @click="activeBtn = 'btn1';selectedPeriod = 'WEEK';loadCapacity=10" :class="{active: activeBtn === 'btn1' }" v-on:click.prevent="filter('WEEK')"> {{ this.$t('exoplatform.gamification.leaderboard.selectedPeriod.WEEK',"Week") }}</button>
                    <button type="button" class="btn btn-secondary" @click="activeBtn = 'btn2';selectedPeriod = 'MONTH';loadCapacity=10 " :class="{active: activeBtn === 'btn2' }" v-on:click.prevent="filter('MONTH')"> {{ this.$t('exoplatform.gamification.leaderboard.selectedPeriod.MONTH',"Month") }}</button>
                    <button type="button" class="btn btn-secondary" @click="activeBtn = 'btn3';selectedPeriod = 'ALL';loadCapacity=10 " :class="{active: activeBtn === 'btn3' }" v-on:click.prevent="filter('ALL')">{{ this.$t('exoplatform.gamification.leaderboard.selectedPeriod.ALL',"All") }}</button>
                </div>
            </div>
        </div>
    </div>
    <div class="row">

        <div class="list-lead col">
            <div class="list-group parentPosition" @mouseleave.native="popover = hidden">
                <div v-if="user.fullname != 'Your current rank'" v-for="(user, index) in users" @mouseover="onShown(user.remoteId)" :key="user.socialId" class="popover__wrapper list-group-item d-flex justify-content-between list-li align-items-center pop">

                    <div class="rank-user">{{index+1}}
                    </div>
                    <avatar :username="user.fullname" :size="35" :src="user.avatarUrl"></avatar>
                    <div class="desc-user">
                        <a :href="user.profileUrl">{{user.fullname}}</a>
                    </div>
                    <div class="number-user">{{user.score}}
                        <span>Pts</span>
                    </div>

                    <div class="push popover__content" :target="'leaderboard'+index" v-on:load="onShown(user.remoteId)">
                        <div class="popover fade show bs-popover-left" @mouseover="onShown(user.remoteId)" v-on:load="onShown(user.remoteId)" role="tooltip" tabindex="-1" :id="'leaderboard'+index" x-placement="left">
                            <div class="arrow" style="top: 108px;"></div>
                            <template>
                                <div class='chart' id="chart">


                                    <chart-pie :data='chartData' :config='chartConfig' v-on:load="onLoad"></chart-pie>
                                </div>
                            </template>
                        </div>
                    </div>

                </div>
                <div v-else class="current-rank">
                    <div v-if="users.length" class="popover__wrapper list-group-item d-flex justify-content-between list-li align-items-center pop">
                        <div class="desc-user">
                            {{ getRankLabel() }}
                        </div>
                        <div class="number-user">{{user.score}}</div>
                    </div>

                </div>
                <div class="load-more" v-if="users.length>1">
                    <b-link href="#" @click.prevent="showMore()"> {{ this.$t('exoplatform.gamification.leaderboard.showMore',"Show more") }}</b-link>
                </div>

            </div>
        </div>

    </div>
</div>
</template>

<script>
import Vue from 'vue'
import BootstrapVue from 'bootstrap-vue'
import {
    ChartPie
} from 'vue-d2b'
import {
    Popover
} from 'bootstrap-vue/es/components';
import {
    Image
} from 'bootstrap-vue/es/components';
import axios from 'axios';
import Avatar from 'vue-avatar';
Vue.use(BootstrapVue);
Vue.use(Popover);
Vue.use(Image);
const initialData = () => {
    return {
        chartData: [],
        chartConfig(chart) {
            chart.donutRatio(0.5)
        },
        users: [],
        domains: [],
        type: '',
        category: '',
        connection: 'everyone',
        selected: null,
        activeBtn: 'btn1',
        domain: 'null',
        show: false,
        selectedPeriod: 'WEEK',
        locale: 'lang',
        popoverShow: false,
        loadCapacity: 10,
    }
}
export default {
    data: initialData,
    components: {
        Avatar,
        ChartPie,
    },
    directives: {
        mouseover: {
            mounted: function () {
                jQuery('[data-toggle="popover"]').popover({
                        html: true,
                        content: $('#popover')
                    }).on('mouseenter', function () {
                        popoverShow: true;
                    })
                    .on('mouseleave', function () {
                        popoverShow: false;
                    });
            },
        }
    },
    watch: {
        domain() {
            this.loadCapacity = 10
            this.filter()
        }
    },
    mounted: function () {
        jQuery(".pop").popover({
                trigger: "hover",
                html: true,
                animation: false
            })
            .on("mouseenter", function () {
                var _this = this;
                jQuery(this).popover("show");
                jQuery(".popover").on("mouseleave", function () {
                    jQuery(_this).popover('hide');
                });
            }).on("mouseleave", function () {
                var _this = this;
                setTimeout(function () {
                    if (!jQuery(".popover:hover").length) {
                        jQuery(_this).popover("hide");
                    }
                }, 300);
            });
    },
    methods: {
        filter() {
            let self = this
            axios.get(`/rest/gamification/leaderboard/filter`, {
                    params: {
                        'domain': self.domain,
                        'period': self.selectedPeriod
                    }
                })
                .then(response => {
                    this.users = response.data;
                })
                .catch(e => {
                    console.warn(e)
                })
        },
        showMore() {
            let self = this
            self.loadCapacity += 10;
            axios.get(`/rest/gamification/leaderboard/filter`, {
                    params: {
                        'domain': self.domain,
                        'period': self.selectedPeriod,
                        'capacity': self.loadCapacity
                    }
                })
                .then(response => {
                    this.users = response.data;
                })
                .catch(e => {
                    console.warn(e)
                })
        },
        onShown(username) {
            window.dispatchEvent(new Event('resize'));
            axios.get(`/rest/gamification/leaderboard/stats`, {
                    params: {
                        'username': username
                    }
                })
                .then(response => {
                    this.chartData = response.data;
                    for (let i = 0; i < this.chartData.length; i++) {
                        this.chartData[i].label = this.$t(`exoplatform.gamification.gamificationinformation.domain.${this.chartData[i].label}`,this.chartData[i].label)
                    }
                })
                .catch(e => {
                    console.warn(e)
                })
        },
        popOpen() {
            jQuery(".popover").popover({
                    trigger: "hover",
                    html: true,
                    animation: false
                })
                .on("mouseenter", function () {
                    popoverShow: true;
                }).on("mouseleave", function () {
                    popoverShow: false;
                });
        },
        disableByRef() {
            if (this.disabled) {
                this.$refs.popover.$emit('enable')
            } else {
                this.$refs.popover.$emit('disable')
            }
        },
        mouseOver() {
            jQuery(this).popover("show");
        },
        isActive(value) {
            return this.active === value
        },
        toggleClass() {
            this.isActive = !this.isActive;
        },
        onLoad() {
            console.log("Pie chart loading")
        },
        onOpen() {
            console.log("Pie chart onOpen")
        },
        getRankLabel: function () {
            return this.$t('exoplatform.gamification.leaderboard.rank');
        }
    },
    created() {
        axios.get(`/rest/gamification/leaderboard/rank/all`)
            .then(response => {
                this.users = response.data;
            })
            .catch(e => {
                this.errors.push(e)
            })

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
.user-leaderboard-portlet .uiIconViewByChart {
    color: #4d5466 !important;
    font-size: 18px;
    top: 9px;
    padding-right: 10px;
    opacity: 0.4;
}

.list-group-item:hover .uiIconViewByChart {
    opacity: 1;
}

.popover__title {
    font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif;
    font-size: 24px;
    line-height: 36px;
    text-decoration: none;
    color: rgb(228, 68, 68);
    text-align: center;
    padding: 15px 0;
}

.popover__wrapper {
    position: relative;
    display: inline-block;
}

.desc-user a {
    color: #4d5466 !important;
}

.desc-user a:hover,
.desc-user a:focus {
    color: #578dc9 !important;
}

.current-rank .desc-user {
    padding-left: 10px;
    width: 50%;
}

.current-rank {
    background: #fbfbfb !important;
    padding: 5px 5px 10px 5px;
}

.current-rank .number-user {
    padding-left: 10px;
    width: 50%;
    margin: 0 auto;
    font-weight: bold;
}

.rank-user {
    font-size: 14px;
    width: 6%;
    white-space: nowrap;
    padding-top: 10px;
    text-align: center;
    color: #4d5466;
}

.popover__content {
    opacity: 0;
    visibility: hidden;
    position: absolute;
    left: -251px;
    transform: translateY(10px);
    background-color: #fff;
    padding: 1.5rem;
    box-shadow: 0 2px 5px 0 rgba(0, 0, 0, .26);
    width: 193px;
    height: 179px;
    top: -68px;
    border-radius: 7px;
}

.popover__content:before {
    position: absolute;
    z-index: -1;
    content: "";
    top: calc(50% - 10px);
    right: -15px;
    border-style: solid;
    border-width: 0 10px 10px;
    border-color: transparent transparent #fff;
    transition-duration: .3s;
    transition-property: transform;
    -webkit-transform: rotate(90deg);
    -moz-transform: rotate(90deg);
    -o-transform: rotate(90deg);
    -ms-transform: rotate(90deg);
    transform: rotate(90deg);
}

.popover__wrapper:hover .popover__content {
    z-index: 10;
    opacity: 1;
    visibility: visible;
    transform: translate(0, -20px);
    transition: all 0.5s cubic-bezier(0.75, -0.02, 0.2, 0.97);
}

.popover__message {
    text-align: center;
}

.d-flex {
    display: -ms-flexbox !important;
    display: flex !important;
}

.user-leaderboard-portlet .row {
    margin-left: 0px !important;
}

.user-leaderboard-portlet .img-thumbnail {
    width: 40px;
    padding: 0;
    height: 40px;
    background-color: #fff;
    border: 1px solid #dee2e6;
    border-radius: .25rem;
}

.container-fluid {
    padding-right: 0px;
    padding-left: 0px;
}

.btn {
    color: #4d5466;
    background-color: #fff;
    border-color: #e1e8ee;
    width: 50%;
}

.btn-group .btn:hover {
    background-color: #f8f8f8;
    border-color: #e1e8ee;
    color: #333;
}

.empty-leaderboard {
    text-align: center;
    color: #578dc9;
}

.d-flex {
    padding: 5px;
    font-size: 14px;
    color: #000;
}

.btn-group>.btn+.btn {
    margin-left: 0px;
}

.btn-group {
    width: 100%;
}

.list-lead {
    padding: 5px 0px !important;
}

.col {
    padding: 5px;
}

.btn-group>.btn {
    padding: 5px 0px;
    width: 33%;
    text-align: center;
}

uiBox {
    padding: 10px 15px;
}

h5 {
    text-align: center;
}

.btn-toolbar {
    margin-bottom: 0px;
}

.chart {
    width: 239px !important;
    height: 226px !important;
}

.d2b-chart-frame {
    width: 239px !important;
}

.vue-d2b-container {
    width: 239px !important;
    height: 226px !important;
}

.d2b-tooltip {
    z-index: 99999999555555 !important;
}

.d2b-chart {
    width: 219px !important;
    height: 168px !important;
}

.number-user {
    font-size: 14px;
    width: 25%;
    white-space: nowrap;
    padding-top: 10px;
    text-align: center;
}

select {
    height: calc(2.25rem + 2px);
    font-size: 14px;
    transition: background-color .15s ease-in-out, border-color .15s ease-in-out, box-shadow .15s ease-in-out;
}

.custom-select {
    display: inline-block;
    width: 100%;
    height: calc(2.25rem + 2px);
    padding: .375rem 1.75rem .375rem .75rem;
    line-height: 1.5;
    color: #495057;
    vertical-align: middle;
    background-size: 8px 10px;
    border: 1px solid #ced4da;
    border-radius: .25rem;
}

.desc-user {
    width: 55%;
    text-align: left;
    white-space: nowrap;
    overflow: hidden;
    display: block;
    text-overflow: ellipsis;
    padding-top: 10px;
}

.desc-user a:hover {
    color: #578dc9;
}

.vue-avatar--wrapper {
    margin: 3px 6px 3px 6px;
}

.list-group-item {
    padding: 5px;
    font-size: 14px;
    color: #4d5466 !important;
    font-family: helvetica;
    border: none;
    background: #ffffff;
    border-radius: inherit !important;
}

.list-group-item:last-child {
    border-bottom: 0px !important;
}

.list-lead .list-li::after {
    content: "";
    position: absolute;
    bottom: 0;
    width: 100%;
    height: 1px;
    background: #f4f5f5;
    left: 0;
    display: block;
}

.list-lead .list-li:last-child::after {
    display: none;
}

.list-group-item:hover {
    background: #fbfbfb;
}

.btn-secondary:not(:disabled):not(.disabled).active:focus,
.btn-secondary:not(:disabled):not(.disabled):active:focus,
.show>.btn-secondary.dropdown-toggle:focus {
    box-shadow: none;
}

.btn-secondary:not(:disabled):not(.disabled).active,
.btn-secondary:not(:disabled):not(.disabled):active,
.show>.btn-secondary.dropdown-toggle {
    background-color: #578dc9;
    border: solid 1px #578dc9;
    color: #fff;
}

.custom-select {
    font-size: 14px;
}

.avatarCircle {
    width: 50px !important;
    height: 50px !important;
}

.ico-info {
    position: relative;
    margin-top: -37px;
    padding: 2px 3px 0;
    display: block;
    top: 5px;
    float: right;
}

.actionIco {
    border: 1px solid transparent;
}

.actionIco:hover {
    background: none;
    filter: progid:DXImageTransform.Microsoft.gradient(startColorstr='#ffffffff', endColorstr='#fff0f0f0', GradientType=0);
    border: 1px solid #e1e8ee;
    -webkit-border-radius: 3px;
    -moz-border-radius: 3px;
    border-radius: 3px;
    -webkit-box-shadow: 0 1px 2px 0 rgba(255, 255, 255, 0);
    -moz-box-shadow: 0 1px 2px 0 rgba(255, 255, 255, 0);
    box-shadow: 0 1px 2px 0 rgba(255, 255, 255, 0);
    color: #333;
}

.load-more {
    float: right;
    padding: 15px;
    color: #578dc9;
    font-weight: bold;
}

.load-more:hover {
    color: #578dc9ba;
    cursor: pointer;
}
</style>

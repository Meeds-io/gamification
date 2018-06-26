<template>
    <b-container fluid class="uiBox">
        <b-row>
            <b-col>
                <h5 class="mt-0 title">Leaderboard</h5>
            </b-col>
        </b-row>
        <b-row>
            <b-col>
                <b-form-select v-model="domain, selected" class="">
                    <template slot="first">
                     
                        <option :value="null" >Overall Rank</option>
                    </template>
                    
                    <option value="social">Social</option>
                    <option value="knowledge">Knowledge</option>
                    <option value="content">Content</option>
                </b-form-select>
            </b-col>
        </b-row>
        <b-row>
            <b-col>
                <b-button-toolbar aria-label="Toolbar with button groups and dropdown menu">
                    <b-button-group id="app">
                        <b-btn @click="activeBtn = 'btn1'" :class="{active: activeBtn === 'btn1' }" v-on:click.prevent="filter('everyone')">Everyone</b-btn>
                        <b-btn @click="activeBtn = 'btn2'" :class="{active: activeBtn === 'btn2' }" v-on:click.prevent="filter('my-connection')">My connections</b-btn>
                    </b-button-group>
                </b-button-toolbar>
            </b-col>
        </b-row>
        <b-row>
            <b-col>
                <b-list-group>
                    <b-list-group-item v-for="(user, index) in users" class="d-flex justify-content-between align-items-center">

                        {{index+1}}
                        <avatar :username="user.username" :size="35" :src="user.userAvatarUrl"></avatar>
                        <div class="desc-user"> {{user.username}} </div>
                        <div class="number-user">{{user.score}}</div>
                        <b-img thumbnail fluid :id="'leaderboard'+index" src="https://www.uspto.gov/sites/default/files/styles/wysiwyg_small/public/Statistics%20-%20Pie%20Chart.png?itok=2rpaaFEX"
                            alt="Thumbnail" @click="onOpen" width="40" height="40" />
                        <b-popover :target="'leaderboard'+index" :placement="'left'" triggers="hover focus" @shown="onShown(user.username)">
                            <template>
                                <div class='chart' id="chart">
                                   
                                    <link href="https://maxcdn.bootstrapcdn.com/font-awesome/4.7.0/css/font-awesome.min.css" rel="stylesheet" integrity="sha384-wvfXpqpZZVQGK6TAh5PVlGOfQNHSoD2xbE+QkPxCAFlNEevoEH3Sl0sibVcOQVnN"
                                        crossorigin="anonymous">

                                    <chart-pie :data='chartData' :config='chartConfig' v-on:load="onLoad"></chart-pie>
                                </div>
                            </template>
                        </b-popover>
                    </b-list-group-item>
                </b-list-group>
            </b-col>
        </b-row>
    </b-container>
</template>
<script>

    import Vue from 'vue'
    import BootstrapVue from 'bootstrap-vue'
    import { ChartPie } from 'vue-d2b'
    import { Popover } from 'bootstrap-vue/es/components';
    import { Image } from 'bootstrap-vue/es/components';
    import axios from 'axios';
    import Avatar from 'vue-avatar'
    import 'bootstrap/dist/css/bootstrap.css'
    import 'bootstrap-vue/dist/bootstrap-vue.css'
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
            type: '',
            category: '',
            connection: 'everyone',
            selected: null,
            activeBtn: 'btn1',
            domain: ''
        }
    }

    export default {
        data: initialData,

        components: {
            Avatar,
            ChartPie
        },
        watch: {
            domain() {
                this.filter("everyone")
            }

        },
        methods: {
            filter(network) {
                let self = this
                axios.get(`/rest/gamification/leaderboard/filter`, { params: { 'category': self.domain, 'network': network } })
                    .then(response => {
                        this.users = response.data;

                    })
                    .catch(e => {
                        console.warn(e)

                    })

            },
            onShown(username) {

                window.dispatchEvent(new Event('resize'));
                axios.get(`/rest/gamification/leaderboard/stats`, { params: { 'username': username } })
                    .then(response => {
                        this.chartData = response.data;

                    })
                    .catch(e => {
                        console.warn(e)

                    })
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
        }
    }
</script>

<style scoped>
   .btn {
        color: #4d5466;
        background-color: #fff;
        border-color: #e1e8ee;
         width:50%;
    }

    .btn-group .btn:hover {
        background-color: #578dc9;
        border: solid 1px #578dc9;
        color: #fff;
    }

    .d-flex {
        padding: 5px;
        font-size: 14px;
        color: #000;
    }
    .btn-group{
        width: 100%; 
    }

    .col {
        padding: 5px;
    }

    .btn-group>.btn {
        padding: 5px 0px;
        width:50%;
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
        height: 168px!important;
    }

    .number-user {
        width: 20%;
        text-align: center;
    }

    .desc-user {
        width: 50%;
        text-align: left;
    }

    .vue-avatar--wrapper {
        margin: 3px 6px 3px 6px;
        width: 40px !important;
        
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
    .custom-select{
        font-size:14px;
    }
</style>
<template>
    <b-container fluid class="uiBox">
        <b-row>
            <b-col>
                <h5 class="mt-0 title">Leaderboard</h5>
            </b-col>
        </b-row>
        <b-row>

            <b-col>
                <b-form-select v-model="category" class="mb-3" @change="filter(this)">
                    <!-- these options will appear after the ones from 'options' prop -->
                    <option value="all">Overall Rank</option>
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
                        <b-btn @click="activeBtn = 'btn1'" :class="{active: activeBtn === 'btn1' }" v-on:click.prevent="filter('everyone')" >Everyone</b-btn>
                        <b-btn @click="activeBtn = 'btn2'" :class="{active: activeBtn === 'btn2' }" v-on:click.prevent="filter('my-connection')" >My connections</b-btn>
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
                        <b-popover :target="'leaderboard'+index" :placement="'left'" triggers="hover focus" @shown="onShown">
                            <template>
                                <div class='chart' id="chart">
                                    <!-- import font awesome for legend icons -->
                                    <link href="https://maxcdn.bootstrapcdn.com/font-awesome/4.7.0/css/font-awesome.min.css" rel="stylesheet" integrity="sha384-wvfXpqpZZVQGK6TAh5PVlGOfQNHSoD2xbE+QkPxCAFlNEevoEH3Sl0sibVcOQVnN"
                                        crossorigin="anonymous">

                                    <!--
                                    Both the :data and :config properties are deeply reactive so any changes
                                    to these will cause the chart to update.
                                -->
                                    <chart-pie :data='chartData' :config='chartConfig' v-on:load="onload"></chart-pie>
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
            chartData: [
                { label: 'Social', value: 23 },
                { label: 'Knowledge', value: 31 },
                { label: 'Marketing', value: 80 },
                { label: 'Communications', value: 8 }
            ],

            chartConfig(chart) {
                chart.donutRatio(0.5)
            },

            users: [],
            type: '',
            category: '',
            connection: 'everyone',
            selected: null,
            activeBtn:'btn1'
        }
    }

    export default {
        data: initialData,

        components: {
            Avatar,
            ChartPie
        },
        methods: {
            filter(domain) {
                axios.get(`/rest/gamification/leaderboard/filter`, { params: { 'category': domain} })
                    .then(response => {
                        console.log(JSON.stringify(response.data))
                        this.users = response.data;

                    })
                    .catch(e => {
                        console.warn(e)

                    })

            },
            onShown() {

                window.dispatchEvent(new Event('resize'));
            },
            isActive(value) {
                return this.active === value
            },
            toggleClass() {
                this.isActive = !this.isActive;
            }
        },

        created() {
            axios.get(`/rest/gamification/leaderboard/rank/all`)
                .then(response => {
                    // JSON responses are automatically parsed.
                    //this.posts = response.data
                    console.log(JSON.stringify(response.data))
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
        color: #fff;
        background-color: #476a9c;
        border-color: #8eb0ea;
    }

    .btn-group .btn:hover {
        color: #fff;
        background: #8eb0ea;
        border-color: #8eb0ea;
    }

    .d-flex {
        padding: 5px;
        font-size: 14px;
        color: #000;
    }

    .col {
        padding: 5px;
    }

    .btn-group>.btn {
        padding: 9px 25px;
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
        text-align: center;
    }

    .vue-avatar--wrapper {
        margin: 3px;
    }

    .btn-secondary:not(:disabled):not(.disabled).active:focus,
    .btn-secondary:not(:disabled):not(.disabled):active:focus,
    .show>.btn-secondary.dropdown-toggle:focus {
        box-shadow: none;
    }

    .btn-secondary:not(:disabled):not(.disabled).active,
    .btn-secondary:not(:disabled):not(.disabled):active,
    .show>.btn-secondary.dropdown-toggle {
        color: #fff;
        background: #8eb0ea;
        border-color: #8eb0ea;
    }
</style>
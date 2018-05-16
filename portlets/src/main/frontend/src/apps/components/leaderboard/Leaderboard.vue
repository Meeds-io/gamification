<template >
    <b-container fluid class="uiBox" >
        <b-row >
            <b-col>
                <h5 class="mt-0 title">Leaderboard</h5>
            </b-col>
        </b-row>
        <b-row>
            <b-col>
                <b-form-select v-model="type" class="mb-3">
                    <template slot="first">
                        <!-- this slot appears above the options from 'options' prop -->
                        <option :value="null" disabled >-- Please select an option --</option>
                    </template>
                    <!-- these options will appear after the ones from 'options' prop -->
                    <option value="rank">Rank</option>
                    <option value="badge">Badge</option>
                </b-form-select>

            </b-col>

            <b-col>
                <b-form-select v-model="category" class="mb-3">
                    <template slot="first">
                        <!-- this slot appears above the options from 'options' prop -->
                        <option :value="null" disabled>-- Please select a category --</option>
                    </template>
                    <!-- these options will appear after the ones from 'options' prop -->
                    <option value="social">Social</option>
                    <option value="knowledge">Knowledge</option>
                    <option value="content">Content</option>
                </b-form-select>
            </b-col>
        </b-row>
        <b-row>
            <b-col>
                <b-button-toolbar aria-label="Toolbar with button groups and dropdown menu">
                    <b-button-group class="mx-1">
                        <b-btn v-on:click.prevent="filter('everyone')">Everyone</b-btn>
                        <b-btn v-on:click.prevent="filter('my-connection')">My connection</b-btn>
                    </b-button-group>
                </b-button-toolbar>
            </b-col>
        </b-row>
        <b-row>
            <b-col>
                <b-list-group>
                    <b-list-group-item v-for="(user, index) in users" class="d-flex justify-content-between align-items-center">
                        {{index}} - <avatar username="Jane Doe"></avatar> - {{user.username}} - {{user.score}} - <b-img thumbnail fluid src="https://www.uspto.gov/sites/default/files/styles/wysiwyg_small/public/Statistics%20-%20Pie%20Chart.png?itok=2rpaaFEX" alt="Thumbnail"  width="50" height="50" />
                    </b-list-group-item>
                </b-list-group>
            </b-col>
        </b-row>


    </b-container>
</template>
<script>

    import Vue from 'vue'
    import BootstrapVue from 'bootstrap-vue'
    import axios from 'axios';
    import Avatar from 'vue-avatar'
    import 'bootstrap/dist/css/bootstrap.css'
    import 'bootstrap-vue/dist/bootstrap-vue.css'
    Vue.use(BootstrapVue);

    const initialData = () => {
        return {
            users: [],
            type: '',
            category: '',
            connection: 'everyone',
            selected: null

        }
    }

    export default {
        data: initialData,
        components: {
            Avatar
        },
        watch: {
            category() {
                this.search()
            },
            type() {
                this.search()
            },

        },
        methods: {
            filter(filter) {

                axios.get(`/rest/gamification/leaderboard/filter`, { params: { 'filter': filter } })
                    .then(response => {
                        console.log(JSON.stringify(response.data))
                        this.users = response.data;

                    })
                    .catch(e => {


                    })
            },
            search() {

                axios.get(`/rest/gamification/leaderboard/search`, { params: { 'category': this.category, 'type': this.type } })
                    .then(response => {
                        console.log(JSON.stringify(response.data))
                        this.users = response.data;

                    })
                    .catch(e => {
                        console.warn(e)

                    })

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

    .btn{
        color: #fff;
        background-color: #476a9c;
        border-color: #8eb0ea;
    }

    .btn-group .btn:hover {
        color: #fff;
        background: #8eb0ea;
        border-color: #8eb0ea;
    }
    .d-flex{
        padding: 5px;
        font-size: 14px;
        color: #000;
    }
    .col{
        padding: 5px;
    }
    .btn-group>.btn{
        padding: 9px 25px;
    }
    uiBox{
    padding: 10px 15px;}
    h5{text-align: center;}
    .btn-toolbar{margin-bottom:0px;}
</style>